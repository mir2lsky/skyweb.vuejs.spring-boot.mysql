import { mount, createLocalVue } from '@vue/test-utils'
import Vuelidate from 'vuelidate'
import VueRouter from 'vue-router'
import LoginPage from '@/views/LoginPage.vue'
import authenticationService from '@/services/authentication'
import { i18n } from '@/i18n'

// Setup local Vue with Vuelidate
const localVue = createLocalVue()
localVue.use(Vuelidate)
localVue.use(VueRouter)
const router = new VueRouter({
  mode: 'history'
})

// Mock dependency registrationService
jest.mock('@/services/authentication')

describe('LoginPage.vue', () => {
  let wrapper
  let fieldUsername
  let fieldPassword
  let buttonSubmit
  let authenticateSpy

  beforeEach(() => {
    wrapper = mount(LoginPage, {
      localVue,
      router,
      i18n
      // 아래의 $t를 위한 목 오브젝트는 불필요하므로 혼란을 방지하기 위해 제거
      // mocks: {
      //   $t: (msg) => i18n.t(msg)
      // }
    })
    fieldUsername = wrapper.find('#username')
    fieldPassword = wrapper.find('#password')
    buttonSubmit = wrapper.find('form button[type="submit"]')
    // Create spy for registration service
    authenticateSpy = jest.spyOn(authenticationService, 'authenticate')
  })

  afterEach(() => {
    authenticateSpy.mockReset()
    authenticateSpy.mockRestore()
  })

  afterAll(() => {
    jest.restoreAllMocks()
  })

  // it('should render correct contents', () => {
  //   const Constructor = Vue.extend(LoginPage)
  //   const vm = new Constructor().$mount()
  //   expect(vm.$el.querySelector('h1').textContent)
  //     .toEqual('TaskAgile')
  // })

  it('should render login form', () => {
    expect(wrapper.find('.logo').attributes().src)
      .toEqual('/static/images/logo.png')
    expect(wrapper.find('.tagline').text())
      .toEqual('Open source task management tool')
    expect(fieldUsername.element.value).toEqual('')
    expect(fieldPassword.element.value).toEqual('')
    expect(buttonSubmit.text()).toEqual('Sign in')
    expect(wrapper.find('.link-sign-up').attributes().href)
      .toEqual('/register')
    expect(wrapper.find('.link-forgot-password'))
      .toBeTruthy()
  })

  it('should contain data model with initial values', () => {
    expect(wrapper.vm.form.username).toEqual('')
    expect(wrapper.vm.form.password).toEqual('')
  })

  it('should have form inputs bound with data model', async () => {
    const username = 'sunny'
    const password = 'VueJsRocks!'

    // 아래 코드는 동작하지 않는다.
    // wrapper.vm.form.username = username
    // wrapper.vm.form.password = password
    // 아래와 같이 setData를 이용해서 설정하면 의도한 대로 동작한다.
    await wrapper.setData({
      form: {
        username: username,
        password: password
      }
    })

    expect(fieldUsername.element.value).toEqual(username)
    expect(fieldPassword.element.value).toEqual(password)
  })

  it('should have form submit event handler `submitForm`', () => {
    const stub = jest.fn()
    wrapper.setMethods({ submitForm: stub })
    buttonSubmit.trigger('submit')
    expect(stub).toBeCalled()
  })

  it('should succeed when credentials are valid', async () => {
    expect.assertions(2)
    const stub = jest.fn()
    wrapper.vm.$router.push = stub
    wrapper.vm.form.username = 'sunny'
    wrapper.vm.form.password = 'JestRocks!'
    wrapper.vm.submitForm()
    expect(authenticateSpy).toBeCalled()
    await wrapper.vm.$nextTick()
    expect(stub).toHaveBeenCalledWith({ name: 'home' })
  })

  it('should fail when credentials are invalid', async () => {
    expect.assertions(3)
    // In the mock, only password `JestRocks!` combined with
    // username `sunny` or `sunny@taskagile.com` is valid
    wrapper.vm.form.username = 'sunny'
    wrapper.vm.form.password = 'MyPassword!'
    expect(wrapper.find('.failed').isVisible()).toBe(false)
    wrapper.vm.submitForm()
    expect(authenticateSpy).toBeCalled()
    await wrapper.vm.$nextTick()
    expect(wrapper.find('.failed').isVisible()).toBe(true)
  })

  it('should have validation to prevent invalid data submit', () => {
    // Empty form
    wrapper.vm.submitForm()
    expect(authenticateSpy).not.toHaveBeenCalled()

    // Only username is valid
    wrapper.vm.form.username = 'synny'
    wrapper.vm.submitForm()
    expect(authenticateSpy).not.toHaveBeenCalled()

    // Only email is valid
    wrapper.vm.form.username = 'sunny@taskagile.com'
    wrapper.vm.submitForm()
    expect(authenticateSpy).not.toHaveBeenCalled()

    // Only password is valid
    wrapper.vm.form.password = 'MyPassword!'
    wrapper.vm.form.username = ''
    wrapper.vm.submitForm()
    expect(authenticateSpy).not.toHaveBeenCalled()
  })
})
