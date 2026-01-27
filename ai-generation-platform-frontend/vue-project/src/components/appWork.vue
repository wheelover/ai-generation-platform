<template>
  <div class="app-work-card">
    <div class="app-image-container">
      <a-image :src="work.cover || 'https://picsum.photos/320/180'" :width="'100%'" />
      <div class="preview-overlay">
        <span class="preview-icon">👁️</span>
        <span class="preview-text">Preview</span>
      </div>
    </div>
    <div class="app-info">
      <h3 class="app-title">{{ work.appName || '未命名应用' }}</h3>
      <p class="app-time">创建于 {{ work.createTime ? formatDate(work.createTime) : '未知时间' }}</p>
      <div class="app-actions">
        <a-button type="link" size="small" @click="work.id && $emit('viewChat', work.id)">查看对话</a-button>
        <a-button v-if="work.deployKey" type="link" size="small" @click="viewWork">查看作品</a-button>
        <a-button type="link" size="small" @click="work.id && $emit('edit', work.id)">编辑</a-button>
        <a-button type="link" size="small" @click="work.id && $emit('delete', work.id)">删除</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface AppWork {
  id?: number | string
  appName?: string
  cover?: string
  createTime?: string | Date
  codeGenType?: string
  deployKey?: string
}

const props = defineProps<{
  work: AppWork
}>()

const emit = defineEmits<{
  (e: 'edit', id: number | string): void
  (e: 'delete', id: number | string): void
  (e: 'viewChat', id: number | string): void
}>()

const viewWork = () => {
  if (props.work.deployKey) {
    window.open(`http://localhost/${props.work.deployKey}`, '_blank')
  }
}

const formatDate = (date: string | Date): string => {
  return new Date(date).toLocaleDateString()
}
</script>

<style scoped>
.app-work-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 350px;
}

.app-work-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.app-image-container {
  position: relative;
  overflow: hidden;
  height: 200px;
  border-radius: 12px 12px 0 0;
}

.app-image-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.app-work-card:hover .app-image-container img {
  transform: scale(1.05);
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: #ffffff;
}

.app-work-card:hover .preview-overlay {
  opacity: 1;
}

.preview-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.preview-text {
  font-size: 14px;
  font-weight: 500;
}

.app-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 8px 0;
  color: #333333;
  line-height: 1.4;
}

.app-time {
  font-size: 14px;
  color: #999999;
  margin: 0 0 16px 0;
}

.app-actions {
  margin-top: auto;
  display: flex;
  gap: 16px;
  justify-content: flex-start;
}

.app-actions .ant-btn {
  font-size: 14px;
  padding: 0;
  transition: all 0.3s ease;
}

.app-actions .ant-btn:hover {
  color: #667eea;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-image-container {
    height: 180px;
  }
  
  .app-info {
    padding: 16px;
  }
  
  .app-title {
    font-size: 15px;
  }
  
  .app-time {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .app-image-container {
    height: 150px;
  }
  
  .app-info {
    padding: 14px;
  }
  
  .app-title {
    font-size: 14px;
  }
  
  .app-time {
    font-size: 12px;
  }
  
  .app-actions {
    gap: 12px;
  }
  
  .app-actions .ant-btn {
    font-size: 12px;
  }
}
</style>