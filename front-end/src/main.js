import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'
import Vuelidate from 'vuelidate'
import { library as faLibrary } from '@fortawesome/fontawesome-svg-core'
import {
  faHome, faSearch, faPlus, faEllipsisH, faUserPlus, faListUl, faWindowMaximize,
  faUser, faPaperclip, faArchive, faPencilAlt, faComment, faUndo, faTrashAlt, faSpinner
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { i18n } from './i18n'
import eventBus from './event-bus'
import realTimeClient from '@/real-time-client'

// Bootstarp axios
axios.defaults.baseURL = '/api'
axios.defaults.headers.common.Accept = 'application/json'
axios.interceptors.response.use(
  response => response,
  (error) => {
    return Promise.reject(error)
  }
)

// Enable Vuelidate
Vue.use(Vuelidate)

// Set up FontAwesome
faLibrary.add(faHome, faSearch, faPlus, faEllipsisH, faUserPlus, faListUl, faWindowMaximize,
  faUser, faPaperclip, faArchive, faPencilAlt, faComment, faUndo, faTrashAlt, faSpinner)
Vue.component('font-awesome-icon', FontAwesomeIcon)

Vue.config.productionTip = false

// 모든 Vue 컴포넌트가 인스턴스의 $rt 프로퍼티로 실시간 클라이언트에 접근할 수 있도록
// RealTimeClient의 인스턴스를 Vue.prototype.$rt에 바인드
Vue.prototype.$bus = eventBus
Vue.prototype.$rt = realTimeClient

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')
