// -- Mutaion ---
// only mutations can change state of vuex store
export default {
  updateMyData (state, data) {
    state.user.name = data.user.name
    state.teams = data.teams
    state.boards = data.boards
  },
  addTeam (state, team) {
    state.teams.push(team)
  },
  addBoard (state, board) {
    state.board.push(board)
  }
}
