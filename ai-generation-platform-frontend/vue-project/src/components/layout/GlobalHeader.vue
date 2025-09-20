<script setup lang="ts">
import { ref } from 'vue'
import {
  LayoutHeader as ALayoutHeader,
  Menu as AMenu,
  MenuItem as AMenuItem,
  Button as AButton,
} from 'ant-design-vue'

// 菜单项配置
const menuItems = ref([
  {
    key: 'home',
    title: '首页',
    path: '/',
  },
  {
    key: 'about',
    title: '关于',
    path: '/about',
  },
])

const selectedKeys = ref(['home'])

// 处理菜单点击事件
const handleMenuClick = (e: { key: string }) => {
  selectedKeys.value = [e.key]
}
</script>

<template>
  <a-layout-header class="global-header">
    <div class="logo-container">
      <router-link to="/">
        <img src="@/assets/pineapple-logo.svg" alt="菠萝 AI Logo" class="logo" />
        <h1 class="site-title">菠萝 AI 零酷空间</h1>
      </router-link>
    </div>

    <div class="menu-container">
      <a-menu mode="horizontal" :selectedKeys="selectedKeys" @click="handleMenuClick">
        <a-menu-item v-for="item in menuItems" :key="item.key">
          <router-link :to="item.path">{{ item.title }}</router-link>
        </a-menu-item>
      </a-menu>
    </div>

    <div class="user-container">
      <a-button type="primary">登录</a-button>
    </div>
  </a-layout-header>
</template>

<style scoped>
.global-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  height: 64px;
  line-height: 64px;
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo-container a {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.logo {
  height: 32px;
  margin-right: 12px;
}

.site-title {
  color: rgba(0, 0, 0, 0.85);
  font-size: 18px;
  margin: 0;
}

.menu-container {
  flex: 1;
  display: flex;
  justify-content: center;
}

.user-container {
  display: flex;
  align-items: center;
}

@media (max-width: 768px) {
  .global-header {
    flex-direction: column;
    height: auto;
    line-height: normal;
    padding: 12px;
  }

  .logo-container,
  .menu-container,
  .user-container {
    margin: 8px 0;
  }

  .menu-container {
    width: 100%;
  }
}
</style>
