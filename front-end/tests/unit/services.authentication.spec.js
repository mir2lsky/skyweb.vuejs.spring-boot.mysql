import moxios from 'moxios'
import authenticationService from '@/services/authentication'

describe('services/authentication', () => {
  beforeEach(() => {
    moxios.install()
  })

  afterEach(() => {
    moxios.uninstall()
  })

  // authentications 서비스를 올바로 호출하는지 테스트
  it('should call `/authentications` API', () => {
    expect.assertions(1)
    moxios.wait(() => {
      const request = moxios.requests.mostRecent()
      expect(request.url).toEqual('/authentications')
      request.respondWith({
        status: 200,
        response: { result: 'success' }
      })
    })

    return authenticationService.authenticate()
  })

  // 요청이 성공하면 호출한 곳에 응답하는지 테스트
  it('should pass the response to caller when request succeeded', () => {
    expect.assertions(2)
    moxios.wait(() => {
      const request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      request.respondWith({
        status: 200,
        response: { result: 'success' }
      })
    })

    return authenticationService.authenticate().then(data => {
      expect(data.result).toEqual('success')
    })
  })

  // 요청이 실패하면 호출한 곳에 에러를 전파하는지 테스트
  it('should propagate the error to caller when request failed', () => {
    expect.assertions(2)
    moxios.wait(() => {
      const request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      request.reject({
        // old : error 내용을 일반 객체로 생성
        // status: 400
        // response: { message: 'Bad request' }
        // new : error 내용을 가지는 response 객체 생성
        response: {
          status: 400,
          data: { message: 'Bad request' }
        }
      })
    })

    return authenticationService.authenticate().catch(error => {
      // error객체에 response라는 이름의 객체로 담기면 내부의 data를 생략하고 바로
      // data의 속성을 읽을 수 있다.
      // cf) moxios는 error 내용에 response가 있으면 자동으로 파싱해 주지만
      //     실제 axios를 사용할 때는 error-parser를 이용해서 파싱처리가 필요
      // old code
      // expect(error.response.message).toEqual('Bad request')
      // new code
      expect(error.message).toEqual('Bad request')
    })
  })
})
