import _ from 'lodash'

export default {
  // error 내용을 response라는 객체의 statu와 data 속성에 담아서
  // 생성한다는 전제하에 response의 status와 data를 읽어
  // status에 따른 error 객체를 파싱 처리
  // 호출자 측에서는 단순하게 error.message 형태로 접근 가능
  parse (error) {
    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      if (status === 400) {
        if (data && data.message) {
          return new Error(data.message)
        } else {
          return new Error('Bad request')
        }
      } else if (status === 401) {
        return new Error('Request not authorized.')
      } else if (status === 403) {
        return new Error('Request forbidden.')
      } else if (status === 404) {
        return new Error('Request failed. Request endpoint not found on the server.')
      } else if (status === 500) {
        if (data && data.message) {
          return new Error(data.message + ' Please try again later.')
        } else {
          return new Error('There is an error on the server side. Please try again later.')
        }
      } else {
        return new Error('Request failed. Please try again later.')
      }
    } else if (error.request) {
      // Request was made and no response
      return new Error('Request failed. No response from the server.')
    } else {
      return _.isError(error) ? error : new Error(error)
    }
  }

}
