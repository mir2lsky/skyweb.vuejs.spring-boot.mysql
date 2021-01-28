export default {
  register (detail) {
    console.log('*** mock :: regisger executed...')
    console.log('*** mock :: detail.emailAddress : ' + detail.emailAddress)
    return new Promise((resolve, reject) => {
      detail.emailAddress === 'sunny@taskagile.com'
        ? resolve({result: 'success'})
        : reject(new Error('User already exist'))
    })
  }
}
