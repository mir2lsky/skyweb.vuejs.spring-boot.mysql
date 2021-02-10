// -- Mutaion ---
// only mutations can change state of vuex store
export default {
  // 첫번째 매개변수 state는 Vuex가 제공,
  // 두번째 매개변수 data는 action에서 전달
  updateMyData (state, data) {
    state.user.name = data.user.name
    state.user.authenticated = true
    state.teams = data.teams
    state.boards = data.boards
  },
  logout (state) {
    state.user.name = ''
    state.user.authenticated = false
    state.teams = []
    state.boards = []
  },
  addTeam (state, team) {
    state.teams.push(team)
  },
  addBoard (state, board) {
    state.boards.push(board)
  }
}
