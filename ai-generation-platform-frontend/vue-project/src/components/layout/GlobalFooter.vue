<script setup lang="ts">
// GlobalFooter.vue
</script>

<template>
  <a-layout-footer class="global-footer">
    <div class="footer-content">
      编程导航原创项目 by <a href="https://www.codefather.cn" target="_blank">程序员鱼皮</a>
    </div>
  </a-layout-footer>
</template>

<style scoped>
.global-footer {
  text-align: center;
  padding: 16px 50px;
  background: #f0f2f5;
  position: sticky;
  bottom: 0;
  width: 100%;
}

.footer-content {
  color: rgba(0, 0, 0, 0.65);
}

.footer-content a {
  color: #1890ff;
  text-decoration: none;
}

.footer-content a:hover {
  text-decoration: underline;
}
</style>
