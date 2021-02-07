// For authoring Nightwatch tests, see
// https://nightwatchjs.org/guide
const data = require('../data/user')

module.exports = {
  // 'login test': function (browser) {
  //   browser
  //     // .url(process.env.VUE_DEV_SERVER_URL + 'login')
  //     .url(browser.launchUrl + 'login')
  //     .waitForElementVisible('#app', 5000)
  //     .assert.containsText('div', 'Open source task management tool')
  //     .end()
  // },

  'login page renders elements': function (browser) {
    const loginPage = browser.page.LoginPage();

    loginPage
      .navigate()
      .waitForElementVisible('@app', 500)
      .assert.visible('@usernameInput')
      .assert.visible('@passwordInput')
      .assert.visible('@submitButton')
      .assert.hidden('@formError')

    browser.end()
  },

  'login with invalid credential': function (browser) {
    const loginPage = browser.page.LoginPage();

    loginPage
      .navigate()
      .login('not-exist', 'incorrect')

    browser.pause(500)  // wait for 0.5sec

    loginPage
      .assert.visible('@formError')
      .assert.containsText('@formError', 'Invalid credentials')

    browser
      .assert.urlEquals(browser.launchUrl + 'login')
      .end()
  },

  'login with username': function (browser) {
    const loginPage = browser.page.LoginPage()
    const homePage = browser.page.HomePage()

    loginPage
      .navigate()
      .login(data.username, data.password)

    browser.pause(2000)

    homePage
      .navigate()
      .assert.visible('@logoImage')

      browser.end()
  },

  'login with email address': function (browser) {
    const loginPage = browser.page.LoginPage()
    const homePage = browser.page.HomePage()

    loginPage
      .navigate()
      .login(data.emailAddress, data.password)

    browser.pause(2000)

    homePage
      .navigate()
      // .expect.element('@pageTitle').text.to.contain('Home Page')
      .assert.visible('@logoImage')

    browser.end()
  }

}
