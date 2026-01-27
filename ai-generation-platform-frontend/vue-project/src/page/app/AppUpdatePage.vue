<template>
  <div id="appUpdatePage">
    <div class="page-header">
      <h1>应用信息修改</h1>
    </div>
    
    <!-- 应用基础信息卡片 -->
    <div class="info-card">
      <h2 class="card-title">应用基础信息</h2>
      <div class="info-grid">
        <div class="info-item">
          <span class="info-label">创建者：</span>
          <div class="creator-info">
            <a-avatar :size="32" :src="appInfo.user?.userAvatar || ''">{{ appInfo.user?.userName?.charAt(0) || '?' }}</a-avatar>
            <span class="creator-name">{{ appInfo.user?.userName || '未知用户' }}</span>
          </div>
        </div>
        <div class="info-item">
          <span class="info-label">创建时间：</span>
          <span class="info-value">{{ appInfo.createTime ? formatDate(appInfo.createTime) : '未知时间' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">更新时间：</span>
          <span class="info-value">{{ appInfo.editTime ? formatDate(appInfo.editTime) : '未知时间' }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">部署状态：</span>
          <span class="info-value" :class="appInfo.deployedTime ? 'deployed' : 'not-deployed'">{{ appInfo.deployedTime ? '已部署' : '未部署' }}</span>
        </div>
        <div class="info-item" v-if="appInfo.deployKey">
          <span class="info-label">部署密钥：</span>
          <span class="info-value deploy-key">{{ appInfo.deployKey }}</span>
        </div>
        <div class="info-item" v-if="appInfo.deployedTime">
          <span class="info-label">部署时间：</span>
          <span class="info-value">{{ formatDate(appInfo.deployedTime) }}</span>
        </div>
      </div>
    </div>
    
    <!-- 应用信息表单 -->
    <div class="update-form">
      <h2 class="card-title">修改应用信息</h2>
      <a-form
        layout="vertical"
        :model="appForm"
        :rules="rules"
        @finish="updateAppInfo"
      >
        <a-form-item label="应用ID" disabled>
          <a-input v-model:value="appForm.id" />
        </a-form-item>
        
        <a-form-item label="应用名称" name="appName">
          <a-input v-model:value="appForm.appName" placeholder="请输入应用名称" />
        </a-form-item>
        
        <a-form-item label="生成类型" disabled>
          <a-input v-model:value="appForm.codeGenType" />
        </a-form-item>
        
        <a-form-item label="初始提示词" disabled>
          <a-textarea v-model:value="appForm.initPrompt" :auto-size="{ minRows: 3 }" />
        </a-form-item>
        
        <!-- 管理员专属字段 -->
        <template v-if="isAdmin">
          <a-form-item label="应用封面" name="cover">
            <a-input v-model:value="appForm.cover" placeholder="请输入应用封面URL" />
            <div v-if="appForm.cover" class="cover-preview">
              <img :src="appForm.cover" alt="封面预览" />
            </div>
          </a-form-item>
          
          <a-form-item label="优先级" name="priority">
            <a-input-number v-model:value="appForm.priority" :min="0" :max="99" />
            <span class="priority-hint">（99表示精选应用）</span>
          </a-form-item>
        </template>
        
        <div class="form-actions">
          <a-button @click="goBack">返回</a-button>
          <a-button type="primary" html-type="submit" :loading="submitting">保存修改</a-button>
          <a-button type="link" @click="goToChat" v-if="appForm.id">查看对话</a-button>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVoById, updateApp } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'

const loginUserStore = useLoginUserStore()

const router = useRouter()
const route = useRoute()
const appId = Number(route.params.id)
const loginUser = loginUserStore.loginUser
const isAdmin = loginUser.userRole === 'admin'

// 应用信息
const appInfo = reactive({
  id: '',
  appName: '',
  codeGenType: '',
  initPrompt: '',
  cover: '',
  priority: 0,
  userId: '',
  user: {
    id: '',
    userName: '',
    userAvatar: ''
  },
  createTime: '',
  editTime: '',
  deployedTime: '',
  deployKey: ''
})

// 应用表单
const appForm = reactive({
  id: '',
  appName: '',
  codeGenType: '',
  initPrompt: '',
  cover: '',
  priority: 0
})

// 表单验证规则
const rules = {
  appName: [
    { required: true, message: '请输入应用名称', trigger: 'blur' },
    { max: 50, message: '应用名称不能超过50个字符', trigger: 'blur' }
  ],
  cover: [
    { max: 255, message: '封面URL不能超过255个字符', trigger: 'blur' }
  ],
  priority: [
    { type: 'number', message: '优先级必须是数字', trigger: 'blur' }
  ]
}

// 加载状态
const submitting = ref(false)

// 格式化日期
const formatDate = (date: string | Date): string => {
  return new Date(date).toLocaleString()
}

// 获取应用信息
const fetchAppInfo = async () => {
  try {
    const res = await getAppVoById({ id: appId })
    if (res.data && res.data.code === 0 && res.data.data) {
      const appData = res.data.data
      // 填充应用信息
      Object.assign(appInfo, appData)
      // 填充表单数据
      appForm.id = appData.id?.toString() || ''
      appForm.appName = appData.appName || ''
      appForm.codeGenType = appData.codeGenType || ''
      appForm.initPrompt = appData.initPrompt || ''
      appForm.cover = appData.cover || ''
      appForm.priority = appData.priority || 0
      
      // 权限检查：普通用户只能编辑自己的应用
      if (!isAdmin && appData.userId !== loginUser.id) {
        message.error('无权限编辑此应用')
        router.push('/')
      }
    }
  } catch {
    message.error('获取应用信息失败')
  }
}

// 更新应用信息
const updateAppInfo = async () => {
  submitting.value = true
  try {
    const res = await updateApp({
      id: appId,
      appName: appForm.appName,
      cover: appForm.cover,
      priority: appForm.priority
    })
    if (res.data && res.data.code === 0) {
      message.success('应用信息更新成功')
      // 跳转到应用对话页
      router.push(`/app/chat/${appId}`)
    } else {
      message.error('应用信息更新失败')
    }
  } catch {
    message.error('应用信息更新失败')
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 跳转到应用对话页面
const goToChat = () => {
  router.push(`/app/chat/${appId}`)
}

// 页面加载时获取应用信息
onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
#appUpdatePage {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  margin: 0;
  color: #333;
  position: relative;
  padding-bottom: 12px;
}

.page-header h1::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 80px;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.info-card {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  max-width: 800px;
}

.update-form {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  max-width: 800px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 14px;
  font-weight: 500;
  color: #666;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 400;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.creator-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.deployed {
  color: #52c41a;
  font-weight: 500;
}

.not-deployed {
  color: #faad14;
  font-weight: 500;
}

.deploy-key {
  font-family: 'Courier New', Courier, monospace;
  background: #f8f9fa;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.cover-preview {
  margin-top: 12px;
  max-width: 200px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cover-preview img {
  width: 100%;
  height: auto;
  display: block;
}

.priority-hint {
  margin-left: 8px;
  font-size: 12px;
  color: #999;
}

.form-actions {
  margin-top: 32px;
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  align-items: center;
}

.form-actions .ant-btn {
  border-radius: 8px;
  padding: 8px 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.form-actions .ant-btn:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.form-actions .ant-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

.form-actions .ant-btn-primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a428f 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  #appUpdatePage {
    padding: 16px;
  }
  
  .info-card,
  .update-form {
    padding: 16px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .card-title {
    font-size: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-actions .ant-btn {
    width: 100%;
  }
}
</style>