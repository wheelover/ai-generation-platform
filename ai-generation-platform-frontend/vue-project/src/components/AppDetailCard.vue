<template>
  <a-card class="app-detail-card" style="width: 300px;">
    <div class="app-detail-section">
      <h4>应用基础信息</h4>
      <div class="app-creator">
        <a-avatar :size="40" :src="appInfo.user?.userAvatar || ''">{{ appInfo.user?.userName?.charAt(0) || '?' }}</a-avatar>
        <span class="creator-name">{{ appInfo.user?.userName || '未知用户' }}</span>
      </div>
      <div class="app-info-item">
        <span class="info-label">创建时间：</span>
        <span class="info-value">{{ appInfo.createTime ? formatDate(appInfo.createTime) : '未知时间' }}</span>
      </div>
      <div class="app-info-item">
        <span class="info-label">生成类型：</span>
        <span class="info-value">{{ appInfo.codeGenType || '未知类型' }}</span>
      </div>
    </div>
    <div v-if="isOwner" class="app-detail-section">
      <h4>操作</h4>
      <div class="app-actions">
        <a-button type="link" @click="$emit('edit')">修改</a-button>
        <a-button type="link" @click="$emit('delete')">删除</a-button>
      </div>
    </div>
  </a-card>
</template>

<script setup lang="ts">
interface AppInfo {
  user?: {
    userName?: string
    userAvatar?: string
  }
  createTime?: string | Date
  codeGenType?: string
}

const props = defineProps<{
  appInfo: AppInfo
  isOwner: boolean
}>()

const emit = defineEmits<{
  (e: 'edit'): void
  (e: 'delete'): void
}>()

const formatDate = (date: string | Date): string => {
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
.app-detail-card {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.app-detail-section {
  margin-bottom: 20px;
}

.app-detail-section h4 {
  margin-bottom: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.app-creator {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.creator-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.app-info-item {
  margin-bottom: 8px;
  font-size: 13px;
  color: #666;
}

.info-label {
  font-weight: 500;
}

.info-value {
  color: #999;
}

.app-actions {
  display: flex;
  gap: 16px;
}
</style>