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
  // moxis는 실제 서비스의 axios 요청을 가로채는 mock 객체 역할을 수행
  // cf) RegistPage.spec.js에서 사용하는 __mock__폴더의 Registration.js에서
  //     수행하는 mock 객체와 다르다.
  //     RegistPage.spec.js는 form 내용의 검증 및 서비스를 정상적인 파라미터로
  //     호출하는지 여부만 테스트한다.
  // serveces.registration.spec.js는 서비스의 request가 성공이나 실패한 경우
  // 예상되는 response에 대하여 moxios를 이용하여 테스트한다.
  // (실제 back-end로 요청하고 결과를 받는 것은 e2e테스트에서 처리한다.)
  // moxios.wait(callback)가 실행되면 axios의 요청을 대기하고 있다가
  // registrationService.register() 호출을 통해 axios 요청이 발생하면
  // moxios가 그 요청을 가로채서 callback에 정의된 가짜 응답을  리턴하고
  // register서비스의 axios는 가짜 응답을 받아서 호출한 측에 돌려준다.
  // 그러면 호출한 측의 then() 에서 결과를 받아서 확인하는 테스트를 수행한다.
  // 아래 console log의 2, 4번은 resister서비스에서 실행됨
  // 에러 확인 테스트도 동일한 처리흐름으로 실행된다.
  it('should pass the response to caller when request succeeded', () => {
    expect.assertions(2)
    // console.log('=== 1. it SUCCESS test')
    moxios.wait(() => {
      const request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      // console.log('=== 3. moxis SUCCESS procsss')
      request.respondWith({
        status: 200,
        response: { result: 'success(from moxios)' }
      })
    })

    return registrationService.register().then(data => {
      // console.dir('=== 5.moxios rtn : data.result ' + data.result)
      expect(data.result).toEqual('success(from moxios)')
    })
  })

  it('should propagate the error to caller when request failed', () => {
    expect.assertions(2)
    // console.log('=== 1. it ERROR test')
    moxios.wait(() => {
      const request = moxios.requests.mostRecent()
      expect(request).toBeTruthy()
      // console.log('=== 3. moxis ERROR procsss')
      request.reject({
        // status: 400,
        // response: { message: 'Bad request' }
        response: {
          status: 400,
          data: { message: 'Bad request' }
        }
      })
    })

    return registrationService.register().catch(error => {
      // console.dir('=== 5. moxios rtn : error : ' + error.message)
      // error를 response라는 이름의 객체에 담으면 내부의 data를 생략하고 바로
      // data의 속성을 읽을 수 있다.
      // expect(error.response.message).toEqual('Bad request')
      expect(error.message).toEqual('Bad request')
    })
  })

  // fix : add missing test of services.registration.spec.js
  it('should call `/registrations` API', () => {
    expect.assertions(1)
    moxios.wait(() => {
      let request = moxios.requests.mostRecent()
      expect(request.url).toEqual('/registrations')
      request.respondWith({
        status: 200,
        response: { result: 'success' }
      })
    })
    return registrationService.register()
  })
})
