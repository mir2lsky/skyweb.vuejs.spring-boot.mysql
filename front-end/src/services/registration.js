import axios from 'axios'
import errorParser from '@/utils/error-parser'

export default {
  register (detail) {
    console.dir('*** 2. registrationService.register executed...' + detail)
    return new Promise((resolve, reject) => {
      axios.post('/registrations', detail).then(({ data }) => {
        console.dir('*** 4. registrationService.register : resolve : data.result => ' + data.result)
        resolve(data)
      }).catch((error) => {
        console.dir('*** 4. registrationService.register : reject => ' + error)
        // reject(error)
        reject(errorParser.parse(error))
      })
    })
  }
}
