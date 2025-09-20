<script setup lang="ts">
import { Layout } from 'ant-design-vue'
import GlobalHeader from '@/components/layout/GlobalHeader.vue'
import GlobalFooter from '@/components/layout/GlobalFooter.vue'
</script>

<template>
  <a-layout class="layout">
    <GlobalHeader />

    <a-layout-content class="layout-content">
      <div class="content-container">
        <slot></slot>
      </div>
    </a-layout-content>

    <GlobalFooter />
  </a-layout>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.layout-content {
  flex: 1;
  padding: 24px;
  background: #f0f2f5;
}

.content-container {
  background: #fff;
  padding: 24px;
  min-height: 280px;
  border-radius: 2px;
}

@media (max-width: 768px) {
  .layout-content {
    padding: 12px;
  }

  .content-container {
    padding: 12px;
  }
}
</style>
