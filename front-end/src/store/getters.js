// export const user = state => state.user
export const user = state => { return { name: 'James J. Ye' } }

export const hasBoards = state => {
  // return state.boards.length > 0
  return true // for test only
}

export const personalBoards = state => {
  // return state.boards.filter(board => board.teamId === 0)
  // for test only
  return [{
    id: 1,
    name: 'vuejs.spring-boot.mysql',
    description: 'An implementation of TaskAgile application with Vue.js, Spring Boot, and MySQL'
  }]
}

export const teamBoards = state => {
  // const teams = []

  // state.teams.forEach(team => {
  //   teams.push({
  //     id: team.id,
  //     name: team.name,
  //     boards: state.boards.filter(board => board.teamId === team.id)
  //   })
  // })

  // return team;

  // for test only
  return [{
    id: 1,
    name: 'Sales & Marking',
    boards: [{
      id: 2,
      name: '2018 Planning',
      description: '2018 sales & marking planning'
    }, {
      id: 3,
      name: 'Ongoing Campaigns',
      description: '2018 ongoing marking campaigns'
    }]
  }]
}
