<template>
  <a-modal
    v-model:open="localOpen"
    title="应用详情"
    :width="600"
    :footer="null"
    @cancel="handleCancel"
  >
    <div v-if="app" class="app-detail-content">
      <!-- 应用基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="应用名称">{{ app.appName }}</a-descriptions-item>
          <a-descriptions-item label="生成类型">
            <a-tag color="blue">{{ getCodeGenTypeLabel(app.codeGenType || CodeGenTypeEnum.HTML) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="初始提示词">
            <div class="prompt-text">{{ app.initPrompt || '暂无' }}</div>
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <!-- 封面图片 -->
      <div v-if="app.cover" class="detail-section">
        <h3 class="section-title">应用封面</h3>
        <div class="cover-image">
          <img :src="app.cover" :alt="app.appName" />
        </div>
      </div>

      <!-- 创建者信息 -->
      <div v-if="app.user" class="detail-section">
        <h3 class="section-title">创建者</h3>
        <div class="user-info">
          <a-avatar :src="app.user.userAvatar" :size="40" />
          <span class="user-name">{{ app.user.userName || app.user.userAccount }}</span>
        </div>
      </div>

      <!-- 时间信息 -->
      <div class="detail-section">
        <h3 class="section-title">时间信息</h3>
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="创建时间">{{ formatTime(app.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ formatTime(app.editTime) }}</a-descriptions-item>
        </a-descriptions>
      </div>

      <!-- 操作按钮 -->
      <div v-if="showActions" class="detail-actions">
        <a-space>
          <a-button type="primary" @click="handleEdit">编辑应用</a-button>
          <a-button danger @click="handleDelete">删除应用</a-button>
        </a-space>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CodeGenTypeEnum, getCodeGenTypeLabel } from '@/constants/codeGenType'

const props = defineProps<{
  open: boolean
  app?: API.AppVO
  showActions?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
  (e: 'edit'): void
  (e: 'delete'): void
}>()

const localOpen = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value)
})

const handleCancel = () => {
  emit('update:open', false)
}

const handleEdit = () => {
  emit('edit')
}

const handleDelete = () => {
  emit('delete')
}

const formatTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.app-detail-content {
  padding: 8px 0;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.prompt-text {
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 150px;
  overflow-y: auto;
}

.cover-image {
  text-align: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.cover-image img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 4px;
  object-fit: contain;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 8px;
}

.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.detail-actions {
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
  text-align: right;
}

.detail-actions .ant-space {
  display: inline-flex;
}
</style>
