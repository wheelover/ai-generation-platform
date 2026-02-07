<template>
  <a-modal
    v-model:open="localVisible"
    title="部署成功"
    :footer="null"
    :width="400"
    :closable="true"
    @cancel="handleClose"
  >
    <div class="deploy-success-content">
      <div class="success-icon">✓</div>
      <h3>网站部署成功！</h3>
      <p>你的网站已经成功部署，可以通过以下链接访问：</p>
      <div class="deploy-url-container">
        <a-input :value="deployUrl" readonly />
        <a-button type="text" @click="copyDeployUrl" icon="copy">复制</a-button>
      </div>
      <div class="deploy-actions">
        <a-button type="primary" @click="visitWebsite">访问网站</a-button>
        <a-button @click="handleClose">关闭</a-button>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { message } from 'ant-design-vue'

const props = defineProps<{
  visible?: boolean
  open?: boolean
  deployUrl: string
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'update:open', value: boolean): void
  (e: 'close'): void
  (e: 'visit'): void
}>()

// 兼容 v-model:open 和 :visible 两种用法
const localVisible = computed({
  get: () => props.open ?? props.visible ?? false,
  set: (value) => {
    if (props.open !== undefined) {
      emit('update:open', value)
    } else {
      emit('update:visible', value)
    }
  }
})

const copyDeployUrl = () => {
  if (props.deployUrl) {
    navigator.clipboard.writeText(props.deployUrl)
      .then(() => {
        message.success('复制成功')
      })
      .catch(() => {
        message.error('复制失败')
      })
  }
}

const visitWebsite = () => {
  if (props.deployUrl) {
    window.open(props.deployUrl, '_blank')
    emit('visit')
  }
}

const handleClose = () => {
  localVisible.value = false
  emit('close')
}
</script>

<style scoped>
.deploy-success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  font-size: 48px;
  color: #52c41a;
  background: rgba(82, 196, 26, 0.1);
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.deploy-success-content h3 {
  margin: 0 0 12px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.deploy-success-content p {
  margin: 0 0 24px;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.deploy-url-container {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.deploy-url-container .ant-input {
  flex: 1;
  border-radius: 8px;
  background: #f8f9fa;
}

.deploy-url-container .ant-btn {
  white-space: nowrap;
}

.deploy-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.deploy-actions .ant-btn {
  padding: 8px 20px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  line-height: 1;
}

.deploy-actions .ant-btn:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.deploy-actions .ant-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

.deploy-actions .ant-btn-primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a428f 100%);
}

@media (max-width: 480px) {
  .deploy-url-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .deploy-url-container .ant-btn {
    margin-top: 8px;
    align-self: flex-end;
  }
  
  .deploy-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>