import moxios from 'moxios'
import registrationService from '@/services/registration'

describe('services/registration', () => {
  beforeEach(() => {
    moxios.install()
  })

  afterEach(() => {
    moxios.uninstall()
  })

  // --- moxios를 이용한 register 서비스 검증하기 내부 처리 설명 ---
  // moxis는 axios의 요청을 가로채는 mock 객체 역할을 수행
  // unit test는 서버로 요청을 정상적으로 하는지만 확인하고 예상된 응담에 대해서
  // 대응처리만 테스트하면 되므로 moxis는 unit test에서만 사용된다.
  // 실제 back-end로 요청하고 결과를 받는 것은 e2e테스트에서 처리한다.
  // moxios.wait(callback)가 실행되면 axis의 호출을 대기하고 있다가
  // axis 호출이 발생하면 request를 가로채서 callback에서 가짜 응답을 생성해서 리턴함
  // register서비스는 axios를 이용해서 request를 보내지만 moxios가 가료채서
  // 리턴한 결과를 받아서 다시 호출한 측에 돌려주고 아래의 return 문에 존재하는 then()
  // 에서 결과를 확인하는 테스트를 수행
  // 아래 console log의 2, 4번은 resister서비스에서 실행됨
  // 에러 확인 테스트도 동일한 처리흐름으로 실행된다.
  it('should pass the response to caller when request succeeded', () => {
    expect.assertions(2)
    console.log('===1. it SUCCESS procsss')
    moxios.wait(() => {
      let request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      console.log('===3. moxis SUCCESS procsss')
      request.respondWith({
        status: 200,
        response: {result: 'success'}
      })
    })

    return registrationService.register().then(data => {
      console.dir('=== 5.moxios rtn : data.result ' + data.result)
      expect(data.result).toEqual('success')
    })
  })

  it('should propagate the error to caller when request failed', () => {
    expect.assertions(2)
    console.log('=== it ERROR procsss')
    moxios.wait(() => {
      let request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      console.log('=== moxis ERROR procsss')
      request.reject({
        status: 400,
        response: {message: 'Bad request'}
      })
    })

    return registrationService.register().catch(error => {
      console.dir('=== moxios rtn : error : ' + error.response.message)
      expect(error.response.message).toEqual('Bad request')
    })
  })

})
