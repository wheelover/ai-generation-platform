<template>
  <div id="homePage">
    <!-- 网站标题 -->
    <div class="hero-section">
      <h1 class="site-title">一句话 <span class="logo">🐱</span> 呈所想</h1>
      <p class="site-desc">与 AI 对话轻松创建应用和网站</p>
      
      <!-- 用户提示词输入框 -->
      <div class="prompt-input-section">
        <a-form layout="vertical" :model="promptForm" @finish="createApp">
          <a-form-item>
            <a-input
              v-model:value="promptForm.initPrompt"
              placeholder="使用 NoCode 创建一个高效的小工具，帮我计算......"
              :auto-size="{ minRows: 3, maxRows: 6 }"
              class="prompt-input"
            />
          </a-form-item>
          <div class="prompt-actions">
            <a-button icon="upload">上传</a-button>
            <a-button>优化</a-button>
            <a-button type="primary" html-type="submit" icon="arrow-up">创建应用</a-button>
          </div>
        </a-form>
        
        <!-- 快速模板 -->
        <div class="quick-templates">
          <a-button size="small">波普风电商页面</a-button>
          <a-button size="small">企业网站</a-button>
          <a-button size="small">电商运营后台</a-button>
          <a-button size="small">暗黑话题社区</a-button>
        </div>
      </div>
    </div>
    
    <!-- 我的应用分页列表 -->
    <div class="app-section">
      <h2 class="section-title">我的作品</h2>
      <div class="app-list">
        <a-empty v-if="myApps.length === 0" description="暂无应用" />
        <div class="app-grid" v-else>
          <AppWork 
            v-for="app in myApps" 
            :key="app.id" 
            :work="app" 
            @viewChat="goToViewChat" 
            @edit="goToUpdate" 
            @delete="deleteAppById" 
          />
        </div>
      </div>
      <div class="pagination">
        <div class="pagination-info">
          共 {{ myAppsTotal }} 个应用
        </div>
        <div class="pagination-controls">
          <a-pagination 
            v-model:current="myAppsParams.pageNum" 
            v-model:page-size="myAppsParams.pageSize"
            :total="myAppsTotal"
            :page-size-options="['6', '12', '18']"
            :show-size-changer="false"
            :simple="true"
            @change="fetchMyApps"
          />
        </div>
      </div>
    </div>
    
    <!-- 精选应用分页列表 -->
    <div class="app-section">
      <h2 class="section-title">精选案例</h2>
      <div class="app-list">
        <a-empty v-if="goodApps.length === 0" description="暂无应用" />
        <div class="app-grid" v-else>
          <AppWork 
            v-for="app in goodApps" 
            :key="app.id" 
            :work="app" 
            @viewChat="goToViewChat" 
          />
        </div>
      </div>
      <div class="pagination">
        <div class="pagination-info">
          共 {{ goodAppsTotal }} 个应用
        </div>
        <div class="pagination-controls">
          <a-pagination 
            v-model:current="goodAppsParams.pageNum" 
            v-model:page-size="goodAppsParams.pageSize"
            :total="goodAppsTotal"
            :page-size-options="['6', '12', '18']"
            :show-size-changer="false"
            :simple="true"
            @change="fetchGoodApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage, deleteApp } from '@/api/appController'
import AppWork from '@/components/appWork.vue'

const router = useRouter()

// 提示词表单
const promptForm = reactive({
  initPrompt: ''
})

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsTotal = ref(0)
const myAppsParams = reactive({
  pageNum: 1,
  pageSize: 6,
  appName: ''
})

// 精选应用数据
const goodApps = ref<API.AppVO[]>([])
const goodAppsTotal = ref(0)
const goodAppsParams = reactive({
  pageNum: 1,
  pageSize: 6,
  appName: ''
})

// 创建应用
const createApp = async () => {
  if (!promptForm.initPrompt.trim()) {
    message.error('请输入提示词')
    return
  }
  
  try {
    const res = await addApp({
      initPrompt: promptForm.initPrompt
    })
    
    if (res.data && res.data.code === 0 && res.data.data) {
      const appId = res.data.data
      message.success('应用创建成功')
      router.push(`/app/chat/${appId}`)
    } else {
      message.error('应用创建失败')
    }
  } catch {
    message.error('应用创建失败')
  }
}

// 获取我的应用列表
const fetchMyApps = async () => {
  try {
    const res = await listMyAppVoByPage(myAppsParams)
    if (res.data && res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsTotal.value = res.data.data.totalRow || 0
    }
  } catch {
    message.error('获取应用列表失败')
  }
}

// 获取精选应用列表
const fetchGoodApps = async () => {
  try {
    const res = await listGoodAppVoByPage(goodAppsParams)
    if (res.data && res.data.code === 0 && res.data.data) {
      goodApps.value = res.data.data.records || []
      goodAppsTotal.value = res.data.data.totalRow || 0
    }
  } catch {
    message.error('获取精选应用列表失败')
  }
}

// 删除应用
const deleteAppById = async (appId: number | string) => {
  try {
    const res = await deleteApp({ id: Number(appId) })
    if (res.data && res.data.code === 0) {
      message.success('应用删除成功')
      fetchMyApps()
    } else {
      message.error('应用删除失败')
    }
  } catch {
    message.error('应用删除失败')
  }
}

// 跳转到应用对话页面
const goToChat = (appId: number | string) => {
  router.push(`/app/chat/${appId}`)
}

// 跳转到应用对话页面（查看模式，不自动发送消息）
const goToViewChat = (appId: number | string) => {
  router.push(`/app/chat/${appId}?view=1`)
}

// 跳转到应用编辑页面
const goToUpdate = (appId: number | string) => {
  router.push(`/app/update/${appId}`)
}

// 页面加载时获取数据
onMounted(() => {
  fetchMyApps()
  fetchGoodApps()
})
</script>

<style scoped>
#homePage {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.hero-section {
  text-align: center;
  padding: 80px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  margin-bottom: 60px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%23ffffff" fill-opacity="0.1"%3E%3Cpath d="M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');
  opacity: 0.5;
  z-index: 0;
}

.hero-section > * {
  position: relative;
  z-index: 1;
}

.site-title {
  font-size: 42px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ffffff;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  letter-spacing: -0.5px;
}

.logo {
  font-size: 36px;
  margin: 0 8px;
}

.site-desc {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.prompt-input-section {
  max-width: 800px;
  margin: 0 auto;
}

.prompt-input {
  font-size: 16px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.prompt-input:focus {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  transform: translateY(-1px);
}

.prompt-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-bottom: 32px;
}

.prompt-actions .ant-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.prompt-actions .ant-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.quick-templates {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.quick-templates .ant-btn {
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: #ffffff;
  transition: all 0.3s ease;
}

.quick-templates .ant-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.app-section {
  margin-bottom: 60px;
  padding: 32px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
  min-height: 920px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 32px;
  color: #333;
  position: relative;
  padding-bottom: 12px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.app-list {
  margin-bottom: 32px;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 32px;
}

.pagination-info {
  font-size: 14px;
  color: #666666;
}

.pagination-controls {
  display: flex;
  align-items: center;
}

/* 移除自定义样式，使用 ant-vue 默认样式 */

.app-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.app-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.app-card .ant-card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px;
}

.app-card .ant-card-cover {
  overflow: hidden;
  border-radius: 12px 12px 0 0;
}

.app-card .ant-card-cover img {
  transition: transform 0.5s ease;
  border-radius: 0;
}

.app-card:hover .ant-card-cover img {
  transform: scale(1.05);
}

.app-actions {
  margin-top: 16px;
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}

.app-actions .ant-btn {
  transition: all 0.3s ease;
  border-radius: 6px;
}

.app-actions .ant-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 分页组件样式已在上方定义，此处不再重复定义 */

@media (max-width: 768px) {
  #homePage {
    padding: 16px;
  }
  
  .hero-section {
    padding: 40px 24px;
    margin-bottom: 40px;
  }
  
  .site-title {
    font-size: 32px;
  }
  
  .site-desc {
    font-size: 16px;
    margin-bottom: 32px;
  }
  
  .prompt-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .prompt-actions .ant-btn {
    width: 100%;
    max-width: 200px;
  }
  
  .app-section {
    padding: 24px;
    margin-bottom: 40px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .app-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 16px;
  }
  
  .pagination-info {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 32px 16px;
  }
  
  .site-title {
    font-size: 28px;
  }
  
  .app-card {
    margin-bottom: 16px;
  }
  
  .app-section {
    padding: 16px;
  }
  
  .app-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .app-image {
    height: 150px;
  }
  
  .app-title {
    font-size: 14px;
  }
  
  .app-time {
    font-size: 12px;
  }
}
</style>
