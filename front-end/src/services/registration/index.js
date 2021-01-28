import axios from 'axios'

export default {
  register (detail) {
    console.dir('*** registrationService.register executed...' + detail)
    return new Promise((resolve, reject) => {
      axios.post('/registrations', detail).then(({ data }) => {
        console.dir('*** registrationService.register : resolve : data.result => ' + data.result)
        resolve(data)
      }).catch((error) => {
        console.dir('*** registrationService.register : reject => ' + error)
        reject(error)
      })
    })
  }
}
