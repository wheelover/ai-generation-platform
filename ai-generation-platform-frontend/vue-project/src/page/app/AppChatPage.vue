<template>
  <div id="appChatPage">
    <!-- 顶部栏 -->
    <div class="app-header">
      <div class="app-header-left">
        <a-dropdown>
          <a-button>{{ appInfo.appName || '应用名称' }}</a-button>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="goToUpdate">编辑应用信息</a-menu-item>
              <a-menu-item @click="deleteCurrentApp">删除应用</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <div class="app-header-right">
        <a-dropdown placement="bottomRight" trigger="click">
          <a-button>应用详情</a-button>
          <template #overlay>
            <AppDetailCard 
              :appInfo="appInfo" 
              :isOwner="isOwner" 
              @edit="goToUpdate" 
              @delete="deleteCurrentApp" 
            />
          </template>
        </a-dropdown>
        <a-button type="primary" @click="deployApp" :loading="deploying">部署</a-button>
      </div>
    </div>
    
    <!-- 核心内容区域 -->
    <div class="app-content">
      <!-- 左侧对话区域 -->
      <div class="chat-section">
        <!-- 消息区域 -->
        <div class="message-area" ref="messageArea">
          <!-- 加载更多按钮 -->
          <div v-if="hasMore" class="load-more-container">
            <a-button 
              type="link" 
              @click="loadMoreHistory" 
              :loading="loadingMore"
            >
              加载更多
            </a-button>
          </div>
          <MessageItem 
            v-for="(message, index) in messages" 
            :key="message.id || index" 
            :message="{
              ...message,
              content: message.role === 'ai' ? renderMarkdown(message.content) : message.content
            }" 
          />
          <div v-if="loading" class="loading-message">
            <a-spin tip="AI 正在生成..." />
          </div>
        </div>
        
        <!-- 用户消息输入框 -->
        <div class="input-area">
          <a-form layout="vertical" :model="inputForm" @finish="sendMessage">
            <a-form-item>
              <a-input
                v-model:value="inputForm.content"
                placeholder="请描述你想生成的网站，越详细效果越好哦"
                :auto-size="{ minRows: 2, maxRows: 6 }"
                class="message-input"
                :disabled="!isOwner"
                :tooltip="{ title: !isOwner ? '无法在别人的作品下对话哦~' : '' }"
              />
            </a-form-item>
            <div class="input-actions">
              <a-button type="primary" html-type="submit" :disabled="!isOwner" icon="rocket">生成网站</a-button>
            </div>
          </a-form>
        </div>
      </div>
      
      <!-- 右侧网页展示区域 -->
      <div class="web-preview-section">
        <h3 class="preview-title">生成后的网页展示</h3>
        <div v-if="!webPreviewUrl" class="preview-placeholder">
          <a-empty description="网站生成完成后将在此展示" />
        </div>
        <iframe
          v-else
          :src="webPreviewUrl"
          frameborder="0"
          class="web-preview-iframe"
          title="网页预览"
        ></iframe>
      </div>
    </div>
  </div>
  
  <!-- 部署成功卡片 -->
  <DeploySuccessModal 
    :visible="deploySuccess" 
    :deployUrl="deployUrl" 
    @close="closeDeployCard" 
    @visit="visitWebsite" 
  />
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { getAppVoById, deployApp as deployAppApi, deleteApp } from '@/api/appController'
import { getLatestChatHistory, getMoreChatHistory } from '@/api/chatHistoryController'
import { useLoginUserStore } from '@/stores/loginUser'
import AppDetailCard from '@/components/AppDetailCard.vue'
import DeploySuccessModal from '@/components/DeploySuccessModal.vue'
import MessageItem from '@/components/MessageItem.vue'
import { getStaticPreviewUrl } from '@/utils/staticResource'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github-dark.css'

// Create markdown-it instance with code highlighting support
const md = new MarkdownIt({
  breaks: true,
  html: true,
  linkify: true,
  typographer: true,
  highlight: function(str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch {}
    }
    return hljs.highlightAuto(str).value
  }
})

// Function to render markdown content
const renderMarkdown = (content: string) => {
  return md.render(content)
}

const router = useRouter()
const route = useRoute()
const appId = Number(route.params.id)

// 登录用户 store
const loginUserStore = useLoginUserStore()
// 是否为应用所有者
const isOwner = ref(false)

// 应用信息
const appInfo = ref<API.AppVO>({})
// 消息列表
const messages = ref<Array<{ id?: number; role: 'user' | 'ai'; content: string; timestamp: string }>>([])
// 输入表单
const inputForm = reactive({ content: '' })
// 加载状态
const loading = ref(false)
// 部署状态
const deploying = ref(false)
// 网页预览URL
const webPreviewUrl = ref('')
// 部署成功卡片
const deploySuccess = ref(false)
// 部署URL
const deployUrl = ref('')
// 消息区域引用
const messageArea = ref<HTMLElement>()

// 游标查询相关状态
const hasMore = ref(false)
const loadingMore = ref(false)
const cursor = ref<number | undefined>()
const limit = 10

// 获取应用信息
const fetchAppInfo = async () => {
  try {
    // 获取应用信息
    const res = await getAppVoById({ id: appId })
    if (res.data && res.data.code === 0 && res.data.data) {
      appInfo.value = res.data.data
      
      // 获取当前用户信息
      await loginUserStore.fetchLoginUser()
      
      // 判断是否为应用所有者
      isOwner.value = loginUserStore.loginUser.id === appInfo.value.userId
      
      // 获取聊天历史
      await fetchChatHistory()
    }
  } catch {
    message.error('获取应用信息失败')
  }
}

// 获取聊天历史
const fetchChatHistory = async () => {
  try {
    // 调用后端接口获取最新聊天历史
    const res = await getLatestChatHistory({ appId, limit })
    if (res.data && res.data.code === 0 && res.data.data) {
      const chatHistoryList = res.data.data
      
      // 转换为前端消息格式
      messages.value = chatHistoryList.map((item: any) => ({
        id: item.id,
        role: (item.role === 'ai' ? 'ai' : 'user') as 'ai' | 'user',
        content: String(item.content || ''),
        timestamp: String(item.createTime || '')
      }))
      
      // 检查是否有更多消息
      hasMore.value = chatHistoryList.length >= limit
      
      // 如果有消息，设置游标为最早消息的ID
      if (chatHistoryList.length > 0) {
        cursor.value = chatHistoryList[0].id
      }
      
      // 判断是否需要自动发送初始消息
      if (isOwner.value && messages.value.length === 0 && appInfo.value.initPrompt) {
        // 没有对话历史，自动发送初始提示词
        messages.value = [
          {
            role: 'user',
            content: appInfo.value.initPrompt,
            timestamp: new Date().toLocaleString()
          }
        ]
        sendInitialPrompt()
      }
      
      // 如果有至少2条对话记录，设置网站预览URL
      if (messages.value.length >= 2) {
        webPreviewUrl.value = getStaticPreviewUrl(appInfo.value.codeGenType || 'html', appId.toString())
      }
    }
  } catch (error) {
    console.error('获取聊天历史失败:', error)
    message.error('获取聊天历史失败')
  }
}

// 加载更多历史消息
const loadMoreHistory = async () => {
  if (loadingMore.value || !cursor.value) return
  
  loadingMore.value = true
  try {
    // 调用后端接口获取更多聊天历史
    const res = await getMoreChatHistory({ appId, cursor: cursor.value, pageSize: limit })
    if (res.data && res.data.code === 0 && res.data.data) {
      const chatHistoryList = res.data.data
      
      // 转换为前端消息格式
      const moreMessages: Array<{ id?: number; role: 'user' | 'ai'; content: string; timestamp: string }> = chatHistoryList.map((item: any) => ({
        id: item.id,
        role: (item.role === 'ai' ? 'ai' : 'user') as 'ai' | 'user',
        content: String(item.content || ''),
        timestamp: String(item.createTime || '')
      }))
      
      // 将新消息添加到列表前面
      messages.value = [...moreMessages, ...messages.value]
      
      // 检查是否有更多消息
      hasMore.value = chatHistoryList.length >= limit
      
      // 更新游标为最早消息的ID
      if (chatHistoryList.length > 0) {
        cursor.value = chatHistoryList[0].id
      }
    }
  } catch (error) {
    console.error('加载更多聊天历史失败:', error)
    message.error('加载更多聊天历史失败')
  } finally {
    loadingMore.value = false
  }
}

// 发送初始提示词
const sendInitialPrompt = async () => {
  if (!appInfo.value.initPrompt) return
  
  loading.value = true
  try {
    const aiMessageIndex = messages.value.length
    messages.value.push({
      role: 'ai',
      content: '',
      timestamp: new Date().toLocaleString()
    })
    
    const url = `${import.meta.env.VITE_API_BASE_URL}/app/chat/gen/code?appId=${appId}&message=${encodeURIComponent(appInfo.value.initPrompt)}`
    const response = await fetch(url, {
      credentials: 'include'
    })
    
    if (!response.ok) {
      throw new Error('Network response was not ok')
    }
    
    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('No response body')
    }
    
    const decoder = new TextDecoder()
    let completeContent = ''
    
    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }
      
      const chunk = decoder.decode(value, { stream: true })
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.substring(5).trim()
          if (data) {
            try {
              const parsed = JSON.parse(data)
              if (parsed.d) {
                completeContent += parsed.d
                messages.value[aiMessageIndex].content = completeContent
                scrollToBottom()
              }
            } catch (e) {
              console.error('解析SSE数据失败:', e)
            }
          }
        } else if (line.startsWith('event: done')) {
          // 处理完成事件
          break
        }
      }
    }
    
    // 尝试解析JSON格式
    let displayContent = completeContent
    try {
      // 提取JSON部分（如果有）
      const jsonMatch = completeContent.match(/```json\s*([\s\S]*?)\s*```/)
      if (jsonMatch && jsonMatch[1]) {
        const jsonString = jsonMatch[1]
        const parsedData = JSON.parse(jsonString)
        if (parsedData.htmlCode) {
          displayContent = parsedData.htmlCode
        }
      }
    } catch (jsonError) {
      // JSON解析失败，显示原始内容
      console.error('JSON解析失败:', jsonError)
    }
    
    messages.value[aiMessageIndex].content = displayContent
    // 生成完成后，设置网页预览URL
    webPreviewUrl.value = `${import.meta.env.VITE_API_BASE_URL}/static/${appInfo.value.codeGenType || 'react'}_${appId}/`
    
    // 重新获取聊天历史，确保消息列表与后端同步
    await fetchChatHistory()
  } catch (error) {
    console.error('AI生成失败:', error)
    message.error('AI生成失败')
  } finally {
    loading.value = false
    // 滚动到底部
    scrollToBottom()
  }
}

// 发送消息
const sendMessage = async () => {
  const content = inputForm.content.trim()
  if (!content) return
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: content,
    timestamp: new Date().toLocaleString()
  })
  
  // 清空输入框
  inputForm.content = ''
  
  // 滚动到底部
  scrollToBottom()
  
  loading.value = true
  try {
    const aiMessageIndex = messages.value.length
    messages.value.push({
      role: 'ai',
      content: '',
      timestamp: new Date().toLocaleString()
    })
    
    const url = `${import.meta.env.VITE_API_BASE_URL}/app/chat/gen/code?appId=${appId}&message=${encodeURIComponent(content)}`
    const response = await fetch(url, {
      credentials: 'include'
    })
    
    if (!response.ok) {
      throw new Error('Network response was not ok')
    }
    
    const reader = response.body?.getReader()
    if (!reader) {
      throw new Error('No response body')
    }
    
    const decoder = new TextDecoder()
    let completeContent = ''
    
    while (true) {
      const { done, value } = await reader.read()
      if (done) {
        break
      }
      
      const chunk = decoder.decode(value, { stream: true })
      const lines = chunk.split('\n')
      
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.substring(5).trim()
          if (data) {
            try {
              const parsed = JSON.parse(data)
              if (parsed.d) {
                completeContent += parsed.d
                messages.value[aiMessageIndex].content = completeContent
                scrollToBottom()
              }
            } catch (e) {
              console.error('解析SSE数据失败:', e)
            }
          }
        } else if (line.startsWith('event: done')) {
          // 处理完成事件
          break
        }
      }
    }
    
    // 尝试解析JSON格式
    let displayContent = completeContent
    try {
      // 提取JSON部分（如果有）
      const jsonMatch = completeContent.match(/```json\s*([\s\S]*?)\s*```/)
      if (jsonMatch && jsonMatch[1]) {
        const jsonString = jsonMatch[1]
        const parsedData = JSON.parse(jsonString)
        if (parsedData.htmlCode) {
          displayContent = parsedData.htmlCode
        }
      }
    } catch (jsonError) {
      // JSON解析失败，显示原始内容
      console.error('JSON解析失败:', jsonError)
    }
    
    messages.value[aiMessageIndex].content = displayContent
    // 生成完成后，更新网页预览URL
    webPreviewUrl.value = `${import.meta.env.VITE_API_BASE_URL}/static/${appInfo.value.codeGenType || 'react'}_${appId}/`
    
    // 重新获取聊天历史，确保消息列表与后端同步
    await fetchChatHistory()
  } catch (error) {
    console.error('AI生成失败:', error)
    message.error('AI生成失败')
  } finally {
    loading.value = false
    // 滚动到底部
    scrollToBottom()
  }
}

// 部署应用
const deployApp = async () => {
  deploying.value = true
  try {
    const res = await deployAppApi({
      appId: appId
    })
    if (res.data && res.data.code === 0 && res.data.data) {
      message.success('部署成功')
      // 显示部署成功卡片
      deployUrl.value = res.data.data
      deploySuccess.value = true
    } else {
      message.error('部署失败')
    }
  } catch {
    message.error('部署失败')
  } finally {
    deploying.value = false
  }
}

// 跳转到应用信息修改页
const goToUpdate = () => {
  router.push(`/app/update/${appId}`)
}

// 删除当前应用
const deleteCurrentApp = async () => {
  try {
    const res = await deleteApp({ id: appId })
    if (res.data && res.data.code === 0) {
      message.success('应用删除成功')
      router.push('/')
    } else {
      message.error('应用删除失败')
    }
  } catch {
    message.error('应用删除失败')
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageArea.value) {
      messageArea.value.scrollTop = messageArea.value.scrollHeight
    }
  })
}

// 格式化日期
const formatDate = (date: string | Date): string => {
  return new Date(date).toLocaleString()
}

// 关闭部署成功卡片
const closeDeployCard = () => {
  deploySuccess.value = false
}

// 访问网站
const visitWebsite = () => {
  if (deployUrl.value) {
    window.open(deployUrl.value, '_blank')
  }
}



// 监听消息变化，自动滚动到底部
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

// 页面加载时获取应用信息
onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
#appChatPage {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8f9fa;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: #ffffff;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.app-header-left {
  display: flex;
  align-items: center;
}

.app-header-left .ant-dropdown-trigger {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.app-header-left .ant-dropdown-trigger:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.app-header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.app-header-right .ant-btn {
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.app-header-right .ant-btn:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.app-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  position: relative;
}

.chat-section {
  width: 40%;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e9ecef;
  background: #ffffff;
  transition: all 0.3s ease;
}

.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f8f9fa;
}

.message-area::-webkit-scrollbar {
  width: 6px;
}

.message-area::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.message-area::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.message-area::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.load-more-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 16px;
}

.loading-message {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  margin: 16px 0;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0% {
    opacity: 0.6;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0.6;
  }
}

.input-area {
  padding: 16px 20px;
  background: #ffffff;
  border-top: 1px solid #e9ecef;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  border-radius: 0;
  margin: 0;
}

.message-input {
  font-size: 14px;
  border-radius: 16px;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #f8f9fa;
  padding: 12px 16px;
}

.message-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
  background: #ffffff;
}

.input-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 12px;
  align-items: center;
}

.input-actions .ant-btn {
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-size: 14px;
  width: 120px;
  height: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-style: normal;
  line-height: 1;
  padding: 0 16px;
}

.input-actions .ant-btn span {
  display: inline-flex;
  align-items: center;
  line-height: 1;
}

.input-actions .ant-btn:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px);
}

.input-actions .ant-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.15);
}

.icon {
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-actions .ant-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.input-actions .ant-btn-primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a428f 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.input-actions .ant-btn-primary:active {
  background: linear-gradient(135deg, #4d60c4 0%, #5a3778 100%);
  box-shadow: 0 3px 8px rgba(102, 126, 234, 0.3);
}

.web-preview-section {
  width: 60%;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  transition: background-color 0.3s ease;
}

.preview-title {
  font-size: 18px;
  font-weight: 600;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  margin: 0;
  color: #333333;
  background: #f8f9fa;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.preview-placeholder {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
  overflow: hidden;
}

.preview-placeholder::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.1"%3E%3Cpath d="M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E');
  opacity: 0.3;
}

.web-preview-iframe {
  flex: 1;
  width: 100%;
  height: 100%;
  border: none;
  transition: opacity 0.3s ease;
}

.web-preview-iframe {
  opacity: 0;
}

.web-preview-iframe[src] {
  opacity: 1;
}

@media (max-width: 1200px) {
  .chat-section {
    width: 50%;
  }
  
  .web-preview-section {
    width: 50%;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 12px 16px;
  }
  
  .app-content {
    flex-direction: column;
  }
  
  .chat-section {
    width: 100%;
    height: 60%;
    border-right: none;
    border-bottom: 1px solid #e9ecef;
  }
  
  .message-area {
    padding: 16px;
  }
  
  .input-area {
    padding: 16px;
  }
  
  .web-preview-section {
    width: 100%;
    height: 40%;
  }
  
  .preview-title {
    padding: 16px;
    font-size: 16px;
  }
}





@media (max-width: 480px) {
  .app-header-right {
    gap: 8px;
  }
  
  .app-header-right .ant-btn {
    font-size: 12px;
    padding: 4px 12px;
  }
  
  .message-content {
    max-width: 90%;
    padding: 12px 16px;
  }
  
  .input-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .input-actions .ant-btn {
    width: 100%;
    max-width: 200px;
  }
  
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