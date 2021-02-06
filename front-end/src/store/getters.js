// --- Getter ---
// store에 저장된 내용을 읽는 객체

// for test only
// export const user = state => { return { name: 'brsky Lee' } }
export const user = state => state.user

export const hasBoards = state => {
  // return true // for test only
  return state.boards.length > 0
}

export const personalBoards = state => {
  return state.boards.filter(board => board.teamId === 0)
  // for test only
  // return [{
  //   id: 1,
  //   name: 'vuejs.spring-boot.mysql',
  //   description: 'An implementation of TaskAgile application with Vue.js, Spring Boot, and MySQL'
  // }]
}

export const teamBoards = state => {
  const teams = []

  state.teams.forEach(team => {
    teams.push({
      id: team.id,
      name: team.name,
      boards: state.boards.filter(board => board.teamId === team.id)
    })
  })

  return teams

  // for test only
  // return [{
  //   id: 1,
  //   name: 'Sales & Marking',
  //   boards: [{
  //     id: 2,
  //     name: '2018 Planning',
  //     description: '2018 sales & marking planning'
  //   }, {
  //     id: 3,
  //     name: 'Ongoing Campaigns',
  //     description: '2018 ongoing marking campaigns'
  //   }]
  // }]
}
