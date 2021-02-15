<template>
  <!-- Page Container -->
  <div class="page" v-show="board.id">
    <!-- Page Header -->
    <PageHeader />
    <!-- Page Body -->
    <div class="page-body">
      <!-- Board Wrapper -->
      <div class="board-wrapper">
        <!-- Board Container -->
        <div class="board">
          <!-- Board Header -->
          <div class="board-header clearfix">
            <!-- Board Name -->
            <div class="board-name board-header-item">{{ board.name }}</div>
            <div class="board-header-divider"></div>
            <!-- Team Name -->
            <div class="team-name board-header-item">
              <span v-if="!board.personal">{{ team.name }}</span>
              <span v-if="board.personal">Personal</span>
            </div>
            <div class="board-header-divider"></div>
            <!-- Board Members -->
            <div class="board-members board-header-item">
              <div class="member" v-for="member in members" v-bind:key="member.id">
                <span>{{ member.shortName }}</span>
              </div>
              <div class="member add-member-toggle" @click="openAddMember()">
                <span><font-awesome-icon icon="user-plus" /></span>
              </div>
            </div>
          </div>
          <!-- Board Body -->
          <div class="board-body">
            <!-- List Container (draggable) -->
            <draggable v-model="cardLists" class="list-container" @end="onCardListDragEnded"
              :options="{handle: '.list-header', animation: 0, scrollSensitivity: 100, touchStartThreshold: 20}">
              <!-- List Wrapper -->
              <div class="list-wrapper" v-for="cardList in cardLists" v-bind:key="cardList.id">
                <!-- List -->
                <div class="list">
                  <!-- List Header -->
                  <div class="list-header">{{ cardList.name }}</div>
                  <!-- Cards (draggable) -->
                  <draggable class="cards" v-model="cardList.cards" @end="onCardDragEnded"
                    :options="{draggable: '.card-item', group: 'cards', ghostClass: 'ghost-card',
                      animation: 0, scrollSensitivity: 100, touchStartThreshold: 20}"
                      v-bind:data-list-id="cardList.id">
                    <!-- Card Item (List) -->
                    <div class="card-item" v-for="card in cardList.cards" v-bind:key="card.id" @click="openCard(card)">
                      <div class="card-title">{{ card.title }}</div>
                    </div>
                    <!-- Add Card Form -->
                    <div class="add-card-form-wrapper" v-if="cardList.cardForm.open">
                      <form @submit.prevent="addCard(cardList)" class="add-card-form">
                        <div class="form-group">
                          <textarea class="form-control" v-model="cardList.cardForm.title" v-bind:id="'cardTitle' + cardList.id"
                            @keydown.enter.prevent="addCard(cardList)" placeholder="Type card title here"></textarea>
                        </div>
                        <button type="submit" class="btn btn-sm btn-primary">Add</button>
                        <button type="button" class="btn btn-sm btn-link btn-cancel" @click="closeAddCardForm(cardList)">Cancel</button>
                      </form>
                    </div>
                  </draggable>
                  <!-- Add Card Button -->
                  <div class="add-card-button" v-show="!cardList.cardForm.open" @click="openAddCardForm(cardList)">+ Add a card</div>
                </div>
              </div>
              <!-- Add List Form -->
              <div class="list-wrapper add-list">
                <div class="add-list-button" v-show="!addListForm.open" @click="openAddListForm()">+ Add a list</div>
                <form @submit.prevent="addCardList()" v-show="addListForm.open" class="add-list-form">
                  <div class="form-group">
                    <input type="text" class="form-control" v-model="addListForm.name" id="cardListName" placeholder="Type list name here" />
                  </div>
                  <button type="submit" class="btn btn-sm btn-primary">Add List</button>
                  <button type="button" class="btn btn-sm btn-link btn-cancel" @click="closeAddListForm()">Cancel</button>
                </form>
              </div>
            </draggable>
          </div>
        </div>
      </div>
    </div>
    <AddMemberModal
      :boardId="board.id"
      @added="onMemberAdded"/>
    <CardModal
      :card="openedCard"
      :cardList="focusedCardList"
      :board="board"
      :members="members" />
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import $ from 'jquery'
import PageHeader from '@/components/PageHeader.vue'
import AddMemberModal from '@/modals/AddMemberModal.vue'
import CardModal from '@/modals/CardModal.vue'
import notify from '@/utils/notify'
import boardService from '@/services/boards'
import cardListService from '@/services/card-lists'
import cardService from '@/services/cards'

export default {
  name: 'BoardPage',
  data () {
    return {
      board: { id: 0, name: '', personal: false },
      cardLists: [/* {id, name, cards, cardForm} */],
      team: { name: '' },
      members: [/* {id, shortName} */],
      addListForm: {
        open: false,
        name: ''
      },
      // CardModal.vue 컴포넌트에 전달할 카드 정보
      openedCard: {}
    }
  },
  computed: {
    focusedCardList () {
      return this.cardLists.filter(cardList => cardList.id === this.openedCard.cardListId)[0] || {}
    }
  },
  components: {
    PageHeader,
    AddMemberModal,
    CardModal,
    draggable
  },
  // --- beforeRouteEnter와 beforeRouteUpdate 네비게이션 가드 제거 ---
  // 카드 모달창을 처리하기 위해 보드 URL과 카드 URL 두 경로를 BoardPage 컴포넌트로 매핑하기 때문에
  // vue-roter라이브러리가 beforeRouteUpdate 가드를 호출하지 않으므로 아래의
  // beforeRouteEnter와 beforeRouteUpdate가 경로의 변경사항을 감지할 수 없다.
  // 예를 들면, /board/1에서 /board/2로 이동할 경우는 감지하지만, /board/1에서 /card/1/card-titie로
  // 이동할 경우에는 감지되어 호출되지 않는다.
  // 그러므로 BoardPage에 머물때 경로의 변경사항을 감지하기 위해서는 네비게이션 가드에 의존할 수 없고
  // 아래와 같이 this.$route 객체를 watcher를 활용하여 지켜봐야 한다.
  // 단, beforeRouteLeave가드는 BoardPage에서 나가는 것을 감지하기 때문에 여전히 유효하다.
  // beforeRouteEnter (to, from, next) {
  //   next(vm => {
  //     vm.loadBoard()
  //   })
  // },
  // beforeRouteUpdate (to, from, next) {
  //   next()
  //   this.unsubscribeFromRealTimeUpdate()
  //   this.loadBoard()
  // },
  watch: {
    // to.name과 form.name을 이용해 보드간의 전환, 카드열기, 카드 닫기의 3개 시나리오를 감지
    '$route' (to, from) {
      // Switch from one board to another
      if (to.name === from.name && to.name === 'board') {
        this.unsubscribeFromRealTimeUpdate(from.params.boardId)
        this.loadBoard(to.params.boardId)
      }
      // Open a card
      if (to.name === 'card' && from.name === 'board') {
        this.loadCard(to.params.cardId).then(() => {
          this.openCardWindow()
        })
      }
      // Close a card
      if (to.name === 'board' && from.name === 'card') {
        this.closeCardWindow()
        this.openedCard = {}
      }
    }
  },
  beforeRouteLeave (to, from, next) {
    console.log('[BoardPage] Before route leave')
    next()
    this.unsubscribeFromRealTimeUpdate(this.board.id)
  },
  mounted () {
    // 사용자가 보드 URL이나 카드 URL을 바로 열 경우 또는 페이지를 새로 고칠 경우
    // 보드 페이지의 데이터 로딩을 시작
    console.log('[BoardPage] Mouted')
    this.loadInitial() // 서버로 부터 데이터를 로드

    this.$el.addEventListener('click', this.dismissActiveForms)

    // 카드 모달 창을 닫으면 보드 URL로 돌아간다.
    $('#cardModal').on('hide.bs.modal', () => {
      this.$router.push({ name: 'board', params: { boardId: this.board.id } })
    })
  },
  beforeDestroy () {
    this.$el.removeEventListener('click', this.dismissActiveForms)
  },
  methods: {
    // --- beforeRouteEnter와 beforeRouteUpdate 네비게이션 가드 제거에 따른 제거 처리 ---
    // loadBoard () {
    //   console.log('[BoardPage] Loading board')
    //   boardService.getBoard(this.$route.params.boardId).then(data => {
    //     this.team.name = data.team ? data.team.name : ''
    //     this.board.id = data.board.id
    //     this.board.personal = data.board.personal
    //     this.board.name = data.board.name

    //     this.members.splice(0) // array clear

    //     data.members.forEach(member => {
    //       this.members.push({
    //         id: member.userId,
    //         shortName: member.shortName
    //       })
    //     })

    //     this.cardLists.splice(0)

    //     data.cardLists.sort((list1, list2) => {
    //       return list1.position - list2.position
    //     })

    //     data.cardLists.forEach(cardList => {
    //       cardList.cards.sort((card1, card2) => {
    //         return card1.position - card2.position
    //       })

    //       this.cardLists.push({
    //         id: cardList.id,
    //         name: cardList.name,
    //         cards: cardList.cards,
    //         cardForm: {
    //           open: false,
    //           title: ''
    //         }
    //       })
    //     })
    //     // RealTime Client 구독 신청
    //     this.subscribeToRealTimUpdate()
    //   }).catch(error => {
    //     notify.error(error.message)
    //   })
    // },
    loadInitial () {
      // The board page can be opened through a card URL.
      if (this.$route.params.cardId) {
        // 카드 URL이면
        console.log('[BoardPage] Opened with card URL')
        // loadCard를 호출하여 카드를 서버로부터 가져오면
        this.loadCard(this.$route.params.cardId).then(card => {
          // 가져온 카드의 보드 id를 이용하여 보드를 로드하고
          return this.loadBoard(card.boardId)
        }).then(() => {
          // 보드가 로드되면 카드 모달창을 오픈
          this.openCardWindow()
        })
      } else {
        // 보드 URL이면
        console.log('[BoardPage] Opened with board URL')
        this.loadBoard(this.$route.params.boardId)
      }
    },
    loadCard (cardId) {
      // 서버로 부터 카드 정보를 취득
      return new Promise(resolve => {
        console.log('[BoardPage] Loading card ' + cardId)
        cardService.getCard(cardId).then(card => {
          // 성공하면 서버에서 취득한 카드정보를 openedCard 프로퍼티에 할당
          // openedCard 프로퍼티는 카드정보를 앞으로 만들 CardModal.vue 컴포넌트에 전달하기 위해 사용된다.
          this.openedCard = card
          // 메서드 호출을 연결하기 위해 프로미스를 반환(취득한 카드 정보를 다음 메서드에 넘김)
          resolve(card)
        }).catch(error => {
          notify.error(error.message)
        })
      })
    },
    loadBoard (boardId) {
      // 서버로 부터 카드 정보를 취득
      return new Promise(resolve => {
        console.log('[BoardPage] Loading board ' + boardId)
        boardService.getBoard(boardId).then(data => {
          this.team.name = data.team ? data.team.name : ''
          this.board.id = data.board.id
          this.board.personal = data.board.personal
          this.board.name = data.board.name

          this.members.splice(0)

          data.members.forEach(member => {
            this.members.push({
              id: member.userId,
              name: member.name,
              shortName: member.shortName
            })
          })

          this.cardLists.splice(0)

          data.cardLists.sort((list1, list2) => {
            return list1.position - list2.position
          })

          data.cardLists.forEach(cardList => {
            cardList.cards.sort((card1, card2) => {
              return card1.position - card2.position
            })

            this.cardLists.push({
              id: cardList.id,
              name: cardList.name,
              cards: cardList.cards,
              cardForm: {
                open: false,
                title: ''
              }
            })
          })
          this.subscribeToRealTimUpdate(data.board.id)
          // 메서드 호출을 연결하기 위해 프로미스를 반환
          resolve()
        }).catch(error => {
          notify.error(error.message)
        })
      })
    },
    dismissActiveForms (event) {
      console.log('[BoardPage] Dismissing forms')
      let dismissAddCardForm = true
      let dismissAddListForm = true
      if (event.target.closest('.add-card-form') || event.target.closest('.add-card-button')) {
        dismissAddCardForm = false
      }
      if (event.target.closest('.add-list-form') || event.target.closest('.add-list-button')) {
        dismissAddListForm = false
      }
      if (dismissAddCardForm) {
        this.cardLists.forEach((cardList) => { cardList.cardForm.open = false })
      }
      if (dismissAddListForm) {
        this.addListForm.open = false
      }
    },
    openAddMember () {
      $('#addMemberModal').modal('show')
    },
    onMemberAdded (member) {
      this.members.push(member)
    },
    addCardList () {
      if (!this.addListForm.name) {
        return
      }
      const cardList = {
        boardId: this.board.id,
        name: this.addListForm.name,
        position: this.cardLists.length + 1
      }
      cardListService.add(cardList).then(savedCardList => {
        this.cardLists.push({
          id: savedCardList.id,
          name: savedCardList.name,
          cards: [],
          cardForm: {
            open: false,
            title: ''
          }
        })
        this.closeAddListForm()
      }).catch(error => {
        notify.error(error.message)
      })
    },
    addCard (cardList) {
      if (!cardList.cardForm.title.trim()) {
        return
      }

      const card = {
        boardId: this.board.id,
        cardListId: cardList.id,
        title: cardList.cardForm.title,
        position: cardList.cards.length + 1
      }

      cardService.add(card).then(savedCard => {
        this.appendCardToList(cardList, savedCard)
        cardList.cardForm.title = ''
        this.focusCardForm(cardList)
      }).catch(error => {
        notify.error(error.message)
      })
    },
    openAddListForm () {
      this.addListForm.open = true
      this.$nextTick(() => {
        $('#cardListName').trigger('focus')
      })
    },
    closeAddListForm () {
      this.addListForm.open = false
      this.addListForm.name = ''
    },
    openAddCardForm (cardList) {
      // Close other add card form
      this.cardLists.forEach((cardList) => {
        cardList.cardForm.open = false
      })
      cardList.cardForm.open = true
      this.focusCardForm(cardList)
    },
    focusCardForm (cardList) {
      this.$nextTick(() => {
        $('#cardTitle' + cardList.id).trigger('focus')
      })
    },
    closeAddCardForm (cardList) {
      cardList.cardForm.open = false
    },
    onCardListDragEnded (event) {
      console.log('[BoardPage] Card list drag ended', event)

      // Get the latest card list order and send it to the back-end
      const positionChanges = {
        boardId: this.board.id,
        cardListPositions: []
      }

      this.cardLists.forEach((cardList, index) => {
        positionChanges.cardListPositions.push({
          cardListId: cardList.id,
          position: index + 1
        })
      })

      cardListService.changePositions(positionChanges).catch(error => {
        notify.error(error.message)
      })
    },
    onCardDragEnded (event) {
      console.log('[BoardPage] Card drag ended', event)
      // Get the card list that have card orders changed
      const fromListId = event.from.dataset.listId
      const toListId = event.to.dataset.listId
      const changedListIds = [fromListId]
      if (fromListId !== toListId) {
        changedListIds.push(toListId)
      }

      const positionChanges = {
        boardId: this.board.id,
        cardPositions: []
      }

      changedListIds.forEach(cardListId => {
        const cardList = this.cardLists.filter(cardList => {
          return cardList.id === parseInt(cardListId)
        })[0]

        cardList.cards.forEach((card, index) => {
          positionChanges.cardPositions.push({
            cardListId: cardListId,
            cardId: card.id,
            position: index + 1
          })
        })
      })

      cardService.changePositions(positionChanges).catch(error => {
        notify.error(error.message)
      })
    },
    // === RealTime Client 관련 method
    subscribeToRealTimUpdate (boardId) {
      // RealTime Client 구독 등록(이벤트 버스)
      this.$rt.subscribe('/board/' + boardId, this.onRealTimeUpdated)
    },
    unsubscribeFromRealTimeUpdate (boardId) {
      // RealTime Client 구독 해지 등록(이벤트 버스)
      this.$rt.unsubscribe('/board/' + boardId, this.onRealTimeUpdated)
    },
    onRealTimeUpdated (update) {
      console.log('[BoardPage] Real time update received', update)
      if (update.type === 'cardAdded') {
        this.onCardAdded(update.card)
      }
    },
    onCardAdded (card) {
      const cardList = this.cardLists.filter(cardList => { return cardList.id === card.cardListId })[0]
      if (!cardList) {
        console.warn('No card list found by id ' + card.cardListId)
        return
      }
      this.appendCardToList(cardList, card)
    },
    appendCardToList (cardList, card) {
      const existingIndex = cardList.cards.findIndex(existingCard => {
        return existingCard.id === card.id
      })

      if (existingIndex === -1) {
        cardList.cards.push({
          id: card.id,
          title: card.title
        })
      }
    },
    openCard (card) {
      const titlePart = card.title.toLowerCase().trim().replace(/\s/g, '-')
      this.$router.push({ name: 'card', params: { cardId: card.id, cardTitle: titlePart } })
    },
    openCardWindow () {
      console.log('[BoardPage] Open card window ' + this.openedCard.id)
      $('#cardModal').modal('show')
    },
    closeCardWindow () {
      console.log('[BoardPage] Close card window ' + this.openedCard.id)
      $('#cardModal').modal('hide')
    }
  }
}
</script>

<style lang="scss" scoped>
.page-body {
  flex-grow: 1;  // 해당 요소가 스프린의 사용 가능한 모든 수직영역을 차지
  position: relative;
  overflow-y: auto;

  .board-wrapper {
    position: absolute;  // 자기 하위에서 플랙스 컨테이너로 동작하는 .board <div> 태그가
                         // 해당 요소 안의 모든 영역을 채우게 함
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;

    .board {
      height: 100%;
      display: flex;  // flex container로 설정
      flex-direction: column;

      .board-header {
        flex: none;  // 포함하는 콘텐츠의 너비 또는 높이에 따라 크기가 변경
        height: auto;
        overflow: hidden;
        position: relative;
        padding: 8px 4px 8px 8px;

        .board-header-divider {
          float: left;
          border-left: 1px solid #ddd;
          height: 16px;
          margin: 8px 10px;
        }

        .board-header-item {
          float: left;
          height: 32px;
          line-height: 32px;
          margin: 0 4px 0 0;
        }

        .board-name {
          font-size: 18px;
          line-height: 32px;
          padding-left: 4px;
          text-decoration: none;
        }

        .board-members {
          .member {
            display: block;
            float: left;
            height: 30px;
            width: 30px;
            margin: 0 0 0 -2px;
            border-radius: 50%;
            background-color: #377EF6;
            position: relative;

            span {
              height: 30px;
              line-height: 30px;
              width: 30px;
              text-align: center;
              display: block;
              color: #fff;
            }
          }

          .add-member-toggle {
            margin-left: 5px;
            background-color: #eee;
            cursor: pointer;

            svg {
              font-size: 10px;
              position: absolute;
              top: 9px;
              left: 9px;
              color: #000;
            }
          }

          .add-member-toggle:hover {
            background-color: #666;

            svg {
              color: #fff;
            }
          }
        }
      }

      .board-body {
        position: relative;
        flex-grow: 1;  // 해당 요소가 스크린의 사용가능한 모든 수직 영역을 차지

        .list-container {
          position: absolute;  // 모든 영역을 채움
          top: 0;
          left: 8px;
          right: 0;
          bottom: 0;
          overflow-x: auto;  // 해당 요소의 수평 스크롤이 가능
          overflow-y: hidden;
          white-space: nowrap;
          margin-bottom: 6px;
          padding-bottom: 6px;

          .list-wrapper {
            width: 272px;
            margin: 0 4px;
            height: 100%;
            box-sizing: border-box;
            display: inline-block;  // 소속된 요소를 수평으로 나란히 놓음
            vertical-align: top;
            white-space: nowrap;

            .list {
              background: #eee;
              border-radius: 3px;
              box-sizing: border-box;
              display: flex;  // flex container로 설정
              flex-direction: column;  // 수직 정렬
              max-height: 100%;  // 최대 높이 100%로 내부 요소가 해당 영역을 넘어가지 못하게 함
              white-space: normal;
              position: relative;

              .list-header {
                padding: .55rem .75rem;
                font-weight: 600;
                cursor: pointer;
              }

              .add-card-button {
                padding: 8px 10px;
                color: #888;
                cursor: pointer;
                border-bottom-left-radius: 3px;
                border-bottom-right-radius: 3px;
              }

              .add-card-button:hover {
                background: #dfdfdf;
                color: #333;
              }

              .add-card-form-wrapper {
                padding: 0 8px 8px;

                .form-group {
                  margin-bottom: 5px;

                  textarea {
                    resize: none;
                    padding: 0.30rem 0.50rem;
                    box-shadow: none;
                  }
                }
              }

              .cards {
                overflow-y: auto;  // 수직 스크롤 가능
                min-height: 1px;

                .card-item {
                  overflow: hidden;
                  background: #fff;
                  padding: 5px 8px;
                  border-radius: 4px;
                  margin: 0 8px 8px;
                  box-shadow: 0 1px 0 #ccc;
                  cursor: pointer;

                  .card-title {
                    margin: 0;

                    a {
                      color: #333;
                      text-decoration: none;
                    }
                  }
                }

                .card-item:hover {
                  background: #ddd;
                }

                .ghost-card {
                  background-color: #ccc !important;
                  color: #ccc !important;
                }
              }
            }

            .ghost-list .list {
              background: #aaa;
            }
          }

          .list-wrapper.add-list {
            background: #f4f4f4;
            border-radius: 3px;
            box-sizing: border-box;
            height: auto;
            color: #888;
            margin-right: 8px;

            .add-list-button {
              padding: 8px 10px;
            }

            .add-list-button:hover {
              background: #ddd;
              cursor: pointer;
              border-radius: 3px;
              color: #333;
            }

            form  {
              padding: 5px;

              .form-group {
                margin-bottom: 5px;

                .form-control {
                  height: calc(1.80rem + 2px);
                  padding: .375rem .3rem;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
