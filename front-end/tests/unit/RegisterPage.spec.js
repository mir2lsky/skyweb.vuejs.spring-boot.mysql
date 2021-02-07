import { mount, createLocalVue } from '@vue/test-utils'
import RegisterPage from '@/views/RegisterPage'
import VueRouter from 'vue-router'
import Vuelidate from 'vuelidate'
import registrationService from '@/services/registration'
import { i18n } from '@/i18n'

// vm.$router 에 접근 할 수 있도록 테스트에 Vue Router 추가
const localVue = createLocalVue()
localVue.use(VueRouter)
localVue.use(Vuelidate)
const router = new VueRouter()

// registrationService의 mock인  /__mocks__/registration.js가 실행되도록 설정
jest.mock('@/services/registration')

describe('RegisterPage.vue', () => {
  let wrapper
  let fieldUsername
  let fieldEmailAddress
  let fieldPassword
  let buttonSubmit
  let registerSpy

  // initialize before each unit test case
  beforeEach(() => {
    wrapper = mount(RegisterPage, {
      localVue,
      router,
      i18n
      // 아래의 $t를 위한 목 오브젝트는 불필요하므로 혼란을 방지하기 위해 제거
      // mocks: {
      //   $t: (msg) => i18n.t(msg)
      // }
    })
    fieldUsername = wrapper.find('#username')
    fieldEmailAddress = wrapper.find('#emailAddress')
    fieldPassword = wrapper.find('#password')
    buttonSubmit = wrapper.find('form button[type="submit"]')
    // Create spy for registration service
    registerSpy = jest.spyOn(registrationService, 'register')
  })

  afterEach(() => {
    registerSpy.mockReset()
    registerSpy.mockRestore()
  })

  afterAll(() => {
    jest.restoreAllMocks()
  })

  // --------- registration form test ----------------------------------
  it('should render registration form', () => {
    expect(wrapper.find('.logo').attributes().src)
      .toEqual('/images/logo.png')
    expect(wrapper.find('.tagline').text())
      .toEqual('Open source task management tool')
    expect(fieldUsername.element.value).toEqual('')
    expect(fieldEmailAddress.element.value).toEqual('')
    expect(fieldPassword.element.value).toEqual('')
    expect(buttonSubmit.text()).toEqual('Create account')
  })

  it('should contain data model with initial values', () => {
    expect(wrapper.vm.form.username).toEqual('')
    expect(wrapper.vm.form.emailAddress).toEqual('')
    expect(wrapper.vm.form.password).toEqual('')
  })

  it('should have from inputs bound with data model', async () => {
    const username = 'sunny'
    const emailAddress = 'sunny@taskagile.com'
    const password = 'VueJsRocks!'

    // ★ wrapper.vm.form.username에 대한 값 설정이 안 되는 문제
    // 그래서 fieldUsername.element.value의 값이 old값을 가지고 있으므로
    // 아래의 toEqual문장에서 오류 발생
    // 원래는 아래 코드와 같이 vm의 모델을 수정하면 양방향 연결이 되어 있기 때문에
    // DOM의 fieleUsername을 통해 수정된 값을 읽어야 되는데 동작이 안됨.
    // 이유는 Vue-test util의 old 버전에서만 동작하는 코드로 추정됨.
    // 해결 방법은 아래와 같이 wrapper.setData()함수를 이용하여 설정해야 한다.
    // 주의할 점은 async..await을 사용해야만 설정이 정상 동작한다.
    // --- 아래 코드는 동작하지 않는다.
    // wrapper.vm.form.username = username
    // wrapper.vm.form.emailAddress = emailAddress
    // wrapper.vm.form.password = password
    await wrapper.setData({
      form: {
        username: username,
        emailAddress: emailAddress,
        password: password
      }
    })

    expect(fieldUsername.element.value).toEqual(username)
    expect(fieldEmailAddress.element.value).toEqual(emailAddress)
    expect(fieldPassword.element.value).toEqual(password)
  })

  it('should have form submit event handler `submitForm`', () => {
    const stub = jest.fn()
    wrapper.setMethods({ submitForm: stub })
    buttonSubmit.trigger('submit')
    expect(stub).toBeCalled()
  })

  // --------- new user test ----------------------------------
  // it('should register when it is a new user', async () => {
  it('should register when it is a new user', async () => {
    expect.assertions(2)
    const stub = jest.fn()
    wrapper.vm.$router.push = stub
    wrapper.vm.form.username = 'sunny'
    wrapper.vm.form.emailAddress = 'sunny@taskagile.com'
    wrapper.vm.form.password = 'JestRocks!'
    wrapper.vm.submitForm()
    expect(registerSpy).toBeCalled()
    // stub에는 {name: 'LoginPage'}가 없기 때문에 지금 시점에서 테스트는
    // 통과하지만 console에는 error가 나고 expected 오류메시지가 표시된다.
    // wrapper.vm.$nextTick(() => {
    //   expect(stub).toHaveBeenCalledWith({ name: 'LoginPage' })
    // })
    // 상기 오류는 비동기 호출의 허점 때문에 발생
    // await를 사용하면 submitForm의 register호출이 발생하지 않은 경우
    // nextTick의 콜백이 jest 테스트가 이미 끝난 후에 실행되어 vue에서
    // 오류를 발견하는 문제를 방지하고 프로미스가 호출되고 결과를 반환할 때까지
    // 기다린다.
    await wrapper.vm.$nextTick()
    expect(stub).toHaveBeenCalledWith({ name: 'login' })
  })

  it('should fail it is not a new user', async () => {
    expect.assertions(3)
    // 목에서는 오직 sunny@taskAgile.com만 새로운 사용자다
    wrapper.vm.form.username = 'ted'
    wrapper.vm.form.emailAddress = 'ted@taskagile.com'
    wrapper.vm.form.password = 'JestRocks!'
    expect(wrapper.find('.failed').isVisible()).toBe(false)
    wrapper.vm.submitForm()
    expect(registerSpy).toBeCalled()
    // wrapper.vm.$nextTick(null, () => {
    //   expect(wrapper.find('.failed').isVisible()).toBe(true)
    // })
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.failed').isVisible()).toBe(true)
  })

  it('should fail when the email address is invalid', () => {
    wrapper.vm.form.username = 'test'
    wrapper.vm.form.emailAddress = 'bad-email-address'
    wrapper.vm.form.password = 'JestRocks!'
    wrapper.vm.submitForm()
    expect(registerSpy).not.toHaveBeenCalled()
  })

  it('should fail when the username is invalid', () => {
    wrapper.vm.form.username = 'a'
    wrapper.vm.form.emailAddress = 'test@taskagile.com'
    wrapper.vm.form.password = 'JestRocks!'
    wrapper.vm.submitForm()
    expect(registerSpy).not.toHaveBeenCalled()
  })

  it('should fail when the password is invalid', () => {
    wrapper.vm.form.username = 'test'
    wrapper.vm.form.emailAddress = 'test@taskagile.com'
    wrapper.vm.form.password = 'bad!'
    wrapper.vm.submitForm()
    expect(registerSpy).not.toHaveBeenCalled()
  })

  // === 간단한 테스트 without vue/test-utils
  // it('should rnder correct contents', () => {
  //   const Constructor = Vue.extend(RegisterPage)
  //   const vm = new Constructor().$mount()

  //   expect(vm.$el.querySelector('.logo').getAttribute('src'))
  //     .toEqual('/static/images/logo.png')
  //   expect(vm.$el.querySelector('.tagline').textContent)
  //     .toEqual('Open source task management tool')
  //   expect(vm.$el.querySelector('#username').value).toEqual('')
  //   expect(vm.$el.querySelector('#emailAddress').value).toEqual('')
  //   expect(vm.$el.querySelector('#password').value).toEqual('')
  //   expect(vm.$el.querySelector('form button[type="submit"]').textContent)
  //     .toEqual('Create account')
  // })
})
