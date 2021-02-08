export default {
/**
 *  autheticate mock object
 *
 */
  authenticate (detail) {
    // console.log('*** mock :: authenticate(detail) executed....')
    return new Promise((resolve, reject) => {
      (detail.username === 'sunny' || detail.username === 'sunny@taskagile.com') &&
        detail.password === 'JestRocks!'
        ? resolve({ result: 'success' })
        : reject(new Error('Invalid credentials'))
    })
  }

}
