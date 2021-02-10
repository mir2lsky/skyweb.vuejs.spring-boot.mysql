import Vue from 'vue'
import SockJS from 'sockjs-client'
import globalBus from '@/event-bus'

class RealTimeClient {
  constructor () {
    // === 생성자 에서는 RealTimeClient의 프로퍼티를 초기화
    // web socker server URL, 기본적으로 null 값이 설정
    this.serverUrl = null
    // 실시간 토큰, client가 Server쪽에서 인증을 수행하는데 사용
    this.token = null // JWT 토큰 문자열
    // SockJS의 인스턴스, connect 메소드에서 생성됨
    this.socket = null

    // 클라이언트가 서버에서 인증됐는지 여부를 표시
    this.authenticated = false
    // 사용자가 로그아웃했고 그로 인해 실시간 클라이언트가 종료됐는지 여부
    this.loggedOut = false
    // Vue 인스턴스의 하나로 실시간 클라이언트의 내부 이벤트 버스로 사용
    this.$bus = new Vue()

    // 서버와 연결하기 전에 아래의 큐들은 각각 구독하기와 구독하기 액션을 가진다
    // 서버와 연결되면 실시간 클라이언트는 큐에 있는 모든 구독하기와 구독해제하기
    // 액션을 수행하고 큐를 비운다.
    // 구독하기 액션을 보관하는 큐
    this.subscribeQueue = {
      /* channel: [handler1, handler2] */
    }
    // 구독해제하기 액션을 보관하는 큐
    this.unsubscribeQueue = {
      /* channel: [handler1, handler2] */
    }
  }

  // === init 관련 주의 사항 ===
  // 1.실시간 클라이언트의 init() 메소드가 등록 되는 곳
  // 이 App는 오직 하나의 실시간 클라이언트만 존재하기 때문에 Init 메소드를 전역
  // 이벤트 버스에 등록할 완벽한 장소는 App.vue의 created() 라이프 사이클 훅이다.
  // 2.실제 Init() 메소드가 실행되는 시점
  // init()메소드는 serverUrl, token 두 개의 매개변수가 필요하므로 이 두 값을
  // 서버로부터 받아서 실시간 클라이언트로 전달해야 하는데 이 두 값을 받아서 처리
  // 하기 좋은 장소 중 하나는 getMyData API(/api/me)의 응답을 처리하는 곳이다.
  // 이 API의 응답을 수신하면 전역 이벤트 버스에 등록된 Init 메소드를 실행한다
  init (serverUrl, token) {
    // 실시간 클라이언트가 한번 인증돼서 연결되면 연결이 또 초기화되지 않도록
    // 메소드의 시작점에서 보호한다.
    if (this.authenticated) {
      console.warn('[RealTimeClient] WS connection already authenticated.')
      return
    }

    console.log('[RealTimeClient] Initializing')
    this.serverUrl = serverUrl
    this.token = token
    this.connect() // 연결을 초기화
  }

  logout () {
    // 실시간 클라이언트의 프로퍼티를 초기값으로 재설정하고 소켓을 닫아서
    // 클라이언트의 상태를 제거
    console.log('[RealTimeClient] Logging out')
    this.subscribeQueue = {}
    this.unsubscribeQueue = {}
    this.authenticated = false
    this.loggedOut = true
    this.socket && this.socket.close()
  }

  connect () {
    console.log('[RealTimeClient] Connecting to ' + this.serverUrl)
    // SockJS의 인스턴스를 생성해서 서버와 연결을 수립
    this.socket = new SockJS(this.serverUrl + '?token=' + this.token)
    this.socket.onopen = () => {
      // 서버와 연결이 됐을 때 호출되며 이 단계에서 SockJS 객체의 readyState
      // 프로퍼티는 SockJS.COLLECTING에서 SockJS.OPEN으로 변경됨
      // Once the connection established, always set the client as authenticated
      this.authenticated = true
      this._onConnected()
    }
    this.socket.onmessage = (event) => {
      // 클라인언트가 서버로부터 메시지를 받을 때 호출된다.
      this._onMessageReceived(event)
    }
    this.socket.onerror = (error) => {
      // 에러 발생시
      this._onSocketError(error)
    }
    this.socket.onclose = (event) => {
      // 웹소켓 연결이 종료 시
      this._onClosed(event)
    }
  }

  // 구독 액션을 이벤트 버스에 등록
  subscribe (channel, handler) {
    // 서버에 연결됐는지 확인
    if (!this._isConnected()) {
      // 연결되지 않았다면 서버와 연결이 성립되거나 복구된 후에 처리되도록
      // 큐에 'subscribe' 액션을 추가
      this._addToSubscribeQueue(channel, handler)
      return
    }
    // 연결되었으면 구독 메시지를 생성
    const message = {
      action: 'subscribe',
      channel
    }
    // 서버로 구독 메시지를 전송,
    // 그러면 서버는 클라이언트를 관련 채널의 구독자로 추가한다.
    this._send(message)

    // channel 메시지 핸들러를 내부 이벤트 버스에 바인드한다
    // 서버로부터 메시지를 받으면 클라이언트는 내부 이벤트 버스의 $emit메서르를
    // 호출해서 채널의 모든 핸들어에게 알려 줄수 있다.
    this.$bus.$on(this._channelEvent(channel), handler)
    console.log('[RealTimeClient] Subscribed to channel ' + channel)
  }

  // 구독 해지 액션을 이벤트 버스에 등록
  unsubscribe (channel, handler) {
    // 클라이언트가 로그아웃 했는지 여부를 확인, 이미 로그아웃했다면 더는 구독해지를
    // 수행할 필요가 없다. 왜냐하면, 실시간 클라이언트가 로그아웃했다면 서버쪽에서
    // 관련된 모든 구독액션이 지워질 것이기 때문이다.
    if (this.loggedOut) {
      return
    }

    // 로그아웃하지 않았다면 서버와 연결됐는지 여부를 확인
    if (!this._isConnected()) {
      // 연결되지 않은 실시간 클라이언트의 경우 나중에 처리하기 위해 구독해지 액션이
      // 큐에 추가된다.
      this._addToUnsubscribeQueue(channel, handler)
      return
    }
    // 구독해지 액션을 메시지로 생성
    const message = {
      action: 'unsubscribe',
      channel
    }
    // 서버로 전송
    this._send(message)
    // 내부 이벤트 버스에서 핸들러를 제거
    this.$bus.$off(this._channelEvent(channel), handler)
    console.log('[RealTimeClient] Unsubscribed from channel ' + channel)
  }

  // === 내부 함수 ===
  _isConnected () {
    return this.socket && this.socket.readyState === SockJS.OPEN
  }

  _onConnected () {
    globalBus.$emit('RealTimeClient.connected')
    console.log('[RealTimeClient] Connected')

    // Handle subscribe and unsubscribe queue
    this._processQueues()
  }

  _onMessageReceived (event) {
    const message = JSON.parse(event.data)
    console.log('[RealTimeClient] Received message', message)

    if (message.channel) {
      this.$bus.$emit(this._channelEvent(message.channel), JSON.parse(message.payload))
    }
  }

  _send (message) {
    this.socket.send(JSON.stringify(message))
  }

  _onSocketError (error) {
    console.error('[RealTimeClient] Socket error', error)
  }

  _onClosed (event) {
    console.log('[RealTimeClient] Received close event', event)
    if (this.loggedOut) {
      // Manually logged out, no need to reconnect
      console.log('[RealTimeClient] Logged out')
      globalBus.$emit('RealTimeClient.loggedOut')
    } else {
      // Temporarily disconnected, attempt reconnect
      console.log('[RealTimeClient] Disconnected')
      globalBus.$emit('RealTimeClient.disconnected')

      setTimeout(() => {
        console.log('[RealTimeClient] Reconnecting')
        globalBus.$emit('RealTimeClient.reconnecting')
        this.connect()
      }, 1000)
    }
  }

  _channelEvent (channel) {
    return 'channel:' + channel
  }

  _processQueues () {
    console.log('[RealTimeClient] Processing subscribe/unsubscribe queues')

    // Process subscribe queue
    const subscribeChannels = Object.keys(this.subscribeQueue)
    subscribeChannels.forEach(channel => {
      const handlers = this.subscribeQueue[channel]
      handlers.forEach(handler => {
        this.subscribe(channel, handler)
        this._removeFromQueue(this.subscribeQueue, channel, handler)
      })
    })

    // Process unsubscribe queue
    const unsubscribeChannels = Object.keys(this.unsubscribeQueue)
    unsubscribeChannels.forEach(channel => {
      const handlers = this.unsubscribeQueue[channel]
      handlers.forEach(handler => {
        this.unsubscribe(channel, handler)
        this._removeFromQueue(this.unsubscribeQueue, channel, handler)
      })
    })
  }

  _addToSubscribeQueue (channel, handler) {
    console.log('[RealTimeClient] Adding channel subscribe to queue. Channel: ' + channel)
    // To make sure the unsubscribe won't be sent out to the server
    this._removeFromQueue(this.unsubscribeQueue, channel, handler)
    const handlers = this.subscribeQueue[channel]
    if (!handlers) {
      this.subscribeQueue[channel] = [handler]
    } else {
      handlers.push(handler)
    }
  }

  _addToUnsubscribeQueue (channel, handler) {
    console.log('[RealTimeClient] Adding channel unsubscribe to queue. Channel: ' + channel)
    // To make sure the subscribe won't be sent out to the server
    this._removeFromQueue(this.subscribeQueue, channel, handler)
    const handlers = this.unsubscribeQueue[channel]
    if (!handlers) {
      this.unsubscribeQueue[channel] = [handler]
    } else {
      handlers.push(handlers)
    }
  }

  _removeFromQueue (queue, channel, handler) {
    const handlers = queue[channel]
    if (handlers) {
      const index = handlers.indexOf(handler)
      if (index > -1) {
        handlers.splice(index, 1)
      }
    }
  }
}

// 애플리케애션에 오직 하나의 RealTimeClient 인스턴스만이 존재하도록
// 인스턴스를 생성해서 default export
export default new RealTimeClient()
