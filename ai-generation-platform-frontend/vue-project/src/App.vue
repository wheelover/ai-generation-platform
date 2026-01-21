<template>
  <BasicLayout>
    <RouterView />
  </BasicLayout>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import BasicLayout from './layouts/BasicLayout.vue'
</script>

<style>
/* 全局样式可以在这里添加 */
</style>
