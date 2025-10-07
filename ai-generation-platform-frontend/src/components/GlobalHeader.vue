<template>
  <a-layout-header class="header">
    <div class="logo">
      <img src="@/assets/logo.png" alt="logo" />
      <h1>AI生成平台</h1>
    </div>
    <a-menu
      v-model:selectedKeys="selectedKeys"
      theme="dark"
      mode="horizontal"
      :items="items"
      style="flex: 1"
    />
    <a-button type="primary">登录</a-button>
  </a-layout-header>
</template>

<script setup>
import { ref } from 'vue'
const selectedKeys = ref(['1'])
const items = ref([
  { key: '1', label: '首页' },
  { key: '2', label: '生成中心' },
  { key: '3', label: '我的作品' }
])
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  padding: 0 20px;
}
.logo {
  display: flex;
  align-items: center;
  margin-right: 24px;
}
.logo img {
  height: 32px;
  margin-right: 8px;
}
.logo h1 {
  color: white;
  font-size: 18px;
  margin: 0;
}
</style>