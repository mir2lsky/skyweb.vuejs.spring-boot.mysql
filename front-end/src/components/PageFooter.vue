<template>
  <footer class="footer">
    <span class="copyright">&copy; 2021 TaskAgile.com</span>
    <ul class="footer-links list-inline float-right">
      <ul class="list-inline-item"><a href="#">About</a></ul>
      <ul class="list-inline-item"><a href="#">Terms of Service</a></ul>
      <ul class="list-inline-item"><a href="#">Privacy Policy</a></ul>
      <ul class="list-inline-item"><a href="https://github.com/mir2lsky/skyweb.vuejs.spring-boot.mysql" target="_blank">GitHub</a></ul>
    </ul>
  </footer>
</template>

<script>
export default {
  name: 'PageFooter'
}
</script>

<style lang="scss" scoped>
.footer {
  width: 100%;
  font-size: 13px;
  color: #666;
  line-height: 40px;
  border-top: 1px solid #ddd;
  margin-top: 50px;

  .list-inline-item {
    margin-right: 10px;
  }

  a {
    color: #666;
  }
}
</style>
