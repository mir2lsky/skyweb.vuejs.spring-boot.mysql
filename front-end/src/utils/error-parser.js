import _ from 'lodash'
import { i18n } from '@/i18n'

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
          return new Error(i18n.t('error.request.bad'))
        }
      } else if (status === 401) {
        return new Error(i18n.t('error.request.notAuthorized'))
      } else if (status === 403) {
        return new Error(i18n.t('error.request.forbidden'))
      } else if (status === 404) {
        return new Error(i18n.t('error.request.notFound'))
      } else if (status === 500) {
        if (data && data.message) {
          return new Error(data.message)
        } else {
          return new Error(i18n.t('error.request.unknownServerError'))
        }
      } else {
        return new Error(i18n.t('error.request.failed'))
      }
    } else if (error.request) {
      // Request was made and no response
      return new Error(i18n.t('error.request.noResponse'))
    } else {
      return _.isError(error) ? error : new Error(error)
    }
  }

}
