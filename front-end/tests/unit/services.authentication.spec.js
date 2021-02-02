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
        response: {
          status: 400,
          data: { message: 'Bad request' }
        }
      })
    })

    return authenticationService.authenticate().catch(error => {
      expect(error.message).toEqual('Bad request')
    })
  })
})
