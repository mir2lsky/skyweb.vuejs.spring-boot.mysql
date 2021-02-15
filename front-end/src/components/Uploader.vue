<template>
  <div class="fileinput-button">
    <font-awesome-icon :icon="icon"  class="icon" v-if="icon"/> {{ label }}
    <input :id="id" type="file" name="file" multiple>
  </div>
</template>

<script>
import $ from 'jquery'
import 'jquery-ui/ui/widget'
import 'blueimp-file-upload/js/jquery.fileupload'
import 'blueimp-file-upload/js/jquery.iframe-transport'

// fileUpload 라이브러리를 App의 다른 부분과 분리
export default {
  name: 'Uploader',
  // id는 file input의 id 속성, url은 업로드된 파일을 받는 서버의 엔드 포인트
  // icon은 버튼에서 아이콘은 보여주기 위해, label은 Upload 버튼의 라벨을 표시
  props: ['id', 'url', 'icon', 'label'],

  // url 프로퍼티에 와처를 추가 url프로퍼티의 형식은
  // /api/cards/{cardId}/attachments와 같은 형식
  watch: {
    url () {
      if (!this.url) {
        return
      }

      $('#' + this.id).fileupload({
        url: this.url,
        dataType: 'json',
        add: (e, data) => {
          // 파일이 업로드 큐에 추가 시 호출되는 이벤트 리스너로서
          // 이벤트 리스너가 호출되면 Uploader 컴포넌트의 클라이언트인
          // CardModal에게 관련된 이벤트를 발행한다.
          this.$emit('uploading', data.files[0])
          data.submit()
        },
        fail: (e, data) => {
          // 업로드기 실패 시
          this.$emit('failed', data._response.jqXHR.responseJSON.message)
        },
        done: (e, data) => {
          // 업로드가 완료 시
          this.$emit('uploaded', data.result)
        },
        progress: (e, data) => {
          // 업로드가 진행 중일 때
          const progress = parseInt(data.loaded / data.total * 100, 10)
          this.$emit('progress', progress)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.icon {
  margin-right: .5rem;
}
</style>
