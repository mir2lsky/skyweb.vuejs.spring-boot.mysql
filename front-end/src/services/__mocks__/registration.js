export default {
/**
 *  register mock object
 *
 */
  register (detail) {
    // console.log('*** mock :: regisger executed...')
    return new Promise((resolve, reject) => {
      detail.emailAddress === 'sunny@taskagile.com'
        ? resolve({ result: 'success' })
        : reject(new Error('User already exist'))
    })
  }

}
