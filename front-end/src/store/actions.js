import meService from '@/services/me'

// -- Action ---
// action only can comit mutation
// acctiondp 전달되는 첫번째 변수는 Vux에서 제공하며
// 이 변수의 commit 메소드를 구조 분해로 취득

export const logout = ({ commit }) => {
  commit('logout')
}

export const getMyData = ({ commit }) => {
  // 비동기 호출
  // console.log('=== action.getMyData() exec...')
  meService.getMyData().then(data => {
    commit('updateMyData', data)
  })
}

export const addTeam = ({ commit }, team) => {
  commit('addTeam', team)
}

export const addBoard = ({ commit }, board) => {
  commit('addBoard', board)
}
