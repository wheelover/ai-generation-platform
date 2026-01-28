<template>
  <div id="homePage">
    <!-- 网站标题 -->
    <div class="hero-section">
      <h1 class="site-title">AI 应用生成平台</h1>
      <p class="site-desc">一句话轻松创建网站应用</p>
      
      <!-- 用户提示词输入框 -->
      <div class="prompt-input-section">
        <a-form layout="vertical" :model="promptForm" @finish="createApp">
          <a-form-item>
            <a-input
              v-model:value="promptForm.initPrompt"
              placeholder="帮我创建个人博客网站"
              :auto-size="{ minRows: 3, maxRows: 6 }"
              class="prompt-input"
            />
          </a-form-item>
          <div class="prompt-actions">
            <a-button type="primary" html-type="submit" icon="rocket">创建应用</a-button>
          </div>
        </a-form>
        
        <!-- 快速模板 -->
        <div class="quick-templates">
          <a-button size="small" @click="setPrompt('帮我创建一个个人博客网站，包含首页、关于我、文章列表、文章详情、留言板等页面，风格简洁现代，支持深色模式，采用React技术栈实现')">个人博客网站</a-button>
          <a-button size="small" @click="setPrompt('帮我创建一个企业官网，包含首页、关于我们、产品中心、新闻动态、联系我们等页面，风格专业大气，采用React和TailwindCSS实现，支持响应式布局')">企业官方网站</a-button>
          <a-button size="small" @click="setPrompt('帮我创建一个在线商城系统，包含商品展示、购物车、结算支付、订单管理、个人中心等功能，界面美观时尚，采用React和Redux实现，支持移动端响应式设计')">在线商城平台</a-button>
          <a-button size="small" @click="setPrompt('帮我创建一个任务管理工具看板，包含项目创建、任务分配、进度追踪、团队协作、消息通知等功能，界面清晰高效，采用React和Dnd-kit实现，支持拖拽操作')">团队任务看板</a-button>
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

// 设置提示词
const setPrompt = (prompt: string) => {
  promptForm.initPrompt = prompt
}

// 页面加载时获取数据
onMounted(() => {
  fetchMyApps()
  fetchGoodApps()
})
</script>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
}

.hero-section {
  text-align: center;
  padding: 100px 40px;
  background: linear-gradient(135deg, #0a0a0f 0%, #1a1a2e 25%, #16213e 50%, #0f3460 75%, #16213e 100%);
  border-radius: 0;
  margin-bottom: 0;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
  width: 100%;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 50%, rgba(66, 153, 225, 0.2) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(138, 43, 226, 0.2) 0%, transparent 50%),
    linear-gradient(90deg, transparent 50%, rgba(66, 153, 225, 0.03) 50%),
    linear-gradient(rgba(66, 153, 225, 0.03) 50%, transparent 50%);
  background-size: 100% 100%, 100% 100%, 60px 60px, 60px 60px;
  z-index: 0;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% {
    background-position: 0 0, 0 0, 0 0, 0 0;
  }
  100% {
    background-position: 0 0, 0 0, 60px 60px, 60px 60px;
  }
}

.hero-section > * {
  position: relative;
  z-index: 1;
}

.site-title {
  font-size: 64px;
  font-weight: 900;
  margin-bottom: 20px;
  color: #ffffff;
  text-shadow: 0 0 30px rgba(66, 153, 225, 0.6), 0 0 60px rgba(138, 43, 226, 0.4);
  letter-spacing: -1px;
  background: linear-gradient(135deg, #ffffff 0%, #64b5f6 50%, #9c27b0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  animation: titleGlow 3s ease-in-out infinite alternate;
}

@keyframes titleGlow {
  0% {
    text-shadow: 0 0 20px rgba(66, 153, 225, 0.6), 0 0 40px rgba(138, 43, 226, 0.4);
  }
  100% {
    text-shadow: 0 0 30px rgba(66, 153, 225, 0.8), 0 0 60px rgba(138, 43, 226, 0.6);
  }
}

.logo {
  font-size: 40px;
  margin: 0 12px;
}

.site-desc {
  font-size: 24px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 48px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  font-weight: 300;
  letter-spacing: 1px;
  text-shadow: 0 0 10px rgba(66, 153, 225, 0.5);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(100, 181, 246, 0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.prompt-input-section {
  max-width: 700px;
  margin: 0 auto;
}

.prompt-input {
  font-size: 16px;
  border-radius: 16px;
  border: 1px solid rgba(66, 153, 225, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 20px rgba(66, 153, 225, 0.2);
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.95);
}

.prompt-input:focus {
  box-shadow: 0 8px 40px rgba(66, 153, 225, 0.4), 0 0 30px rgba(138, 43, 226, 0.3);
  transform: translateY(-2px);
  border-color: rgba(66, 153, 225, 0.6);
}

.prompt-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 24px;
}

.prompt-actions .ant-btn {
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  padding: 0 32px;
  height: 48px;
  font-size: 16px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  background: linear-gradient(135deg, #4299e1 0%, #8a2be2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(66, 153, 225, 0.4);
  color: white;
}

.prompt-actions .ant-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(66, 153, 225, 0.6), 0 0 20px rgba(138, 43, 226, 0.4);
  background: linear-gradient(135deg, #3182ce 0%, #7b1fa2 100%);
}

.quick-templates {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
  margin-top: 32px;
}

.quick-templates .ant-btn {
  border-radius: 20px;
  border: 1px solid rgba(66, 153, 225, 0.3);
  background: rgba(66, 153, 225, 0.1);
  color: rgba(255, 255, 255, 0.9);
  transition: all 0.3s ease;
  font-style: normal;
  box-shadow: 0 2px 10px rgba(66, 153, 225, 0.2);
}

.quick-templates .ant-btn:hover {
  background: rgba(66, 153, 225, 0.2);
  border-color: rgba(66, 153, 225, 0.6);
  transform: translateY(-2px);
  color: #ffffff;
  font-style: normal;
  box-shadow: 0 4px 15px rgba(66, 153, 225, 0.4);
}

.app-section {
  margin-bottom: 0;
  padding: 48px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e3f2fd 100%);
  border-radius: 0;
  box-shadow: none;
  min-height: auto;
  position: relative;
  overflow: hidden;
}

.app-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(90deg, transparent 50%, rgba(66, 153, 225, 0.02) 50%),
    linear-gradient(rgba(66, 153, 225, 0.02) 50%, transparent 50%);
  background-size: 40px 40px;
  z-index: 0;
}

.section-title {
  font-size: 32px;
  font-weight: 800;
  margin-bottom: 32px;
  color: #1a202c;
  position: relative;
  padding-bottom: 12px;
  background: linear-gradient(135deg, #1a202c 0%, #4299e1 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 10px rgba(66, 153, 225, 0.3);
  position: relative;
  z-index: 1;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #4299e1 0%, #8a2be2 100%);
  border-radius: 2px;
  z-index: 1;
}

.app-list {
  margin-bottom: 32px;
}

.app-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 350px;
  gap: 24px;
  margin-bottom: 32px;
  min-height: 728px;
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
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08), 0 0 15px rgba(66, 153, 225, 0.1);
  background: white;
  position: relative;
  z-index: 1;
}

.app-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15), 0 0 30px rgba(66, 153, 225, 0.2);
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
  color: #4299e1;
}

.app-actions .ant-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.3);
  color: #3182ce;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 24px;
  margin-top: 32px;
  position: relative;
  z-index: 1;
}

.pagination-info {
  font-size: 14px;
  color: #666666;
}

.pagination-controls {
  display: flex;
  align-items: center;
}

.pagination-controls .ant-pagination-item-active {
  background: linear-gradient(135deg, #4299e1 0%, #8a2be2 100%);
  border-color: #4299e1;
}

.pagination-controls .ant-pagination-item-active a {
  color: white;
}

.pagination-controls .ant-pagination-item:hover {
  border-color: #4299e1;
  box-shadow: 0 0 10px rgba(66, 153, 225, 0.3);
}

/* 分页组件样式已在上方定义，此处不再重复定义 */

@media (max-width: 768px) {
  #homePage {
    padding: 0;
  }
  
  .hero-section {
    padding: 60px 24px;
    margin-bottom: 0;
  }
  
  .site-title {
    font-size: 36px;
  }
  
  .site-desc {
    font-size: 18px;
    margin-bottom: 32px;
  }
  
  .prompt-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .prompt-actions .ant-btn {
    width: 100%;
    max-width: 280px;
  }
  
  .app-section {
    padding: 24px;
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
    padding: 40px 16px;
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
