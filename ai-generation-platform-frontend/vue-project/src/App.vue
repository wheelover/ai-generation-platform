<script setup lang="ts">
import { RouterView } from 'vue-router'
import BasicLayout from './layouts/BasicLayout.vue'
</script>

<template>
  <BasicLayout>
    <RouterView />
    <a-button type="primary">Primary Button</a-button>
  </BasicLayout>
</template>

<style>
/* 全局样式可以在这里添加 */
</style>
