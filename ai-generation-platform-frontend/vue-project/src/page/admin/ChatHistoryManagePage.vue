<template>
  <div id="chatHistoryManagePage">
    <div class="page-header">
      <h1>对话管理</h1>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchParams" @finish="fetchChatHistoryList">
        <a-form-item label="应用ID">
          <a-input v-model:value="searchParams.appId" placeholder="请输入应用ID" type="number" />
        </a-form-item>
        <a-form-item label="消息类型">
          <a-select v-model:value="searchParams.messageType" placeholder="请选择消息类型" style="width: 200px;">
            <a-select-option value="user">用户消息</a-select-option>
            <a-select-option value="ai">AI消息</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 对话历史列表 -->
    <div class="chat-history-list">
      <a-table
        :columns="columns"
        :data-source="chatHistoryList"
        :pagination="pagination"
        @change="doTableChange"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space size="small">
              <a-button type="link" size="small" danger @click="doDelete(record.id)">删除</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin, deleteChatHistory } from '@/api/chatHistoryController'

// 搜索参数
const searchParams = reactive({
  appId: undefined,
  messageType: undefined,
  pageNum: 1,
  pageSize: 20
})

// 对话历史列表
const chatHistoryList = ref<Array<any>>([])
// 总数
const total = ref(0)

// 表格列配置
const columns = [
  {
    title: '消息ID',
    dataIndex: 'id',
    key: 'id'
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    key: 'appId'
  },
  {
    title: '消息类型',
    dataIndex: 'role',
    key: 'role',
    render: (text: string) => {
      return text === 'ai' ? '<span style="color: #52c41a;">AI消息</span>' : '<span style="color: #1890ff;">用户消息</span>'
    }
  },
  {
    title: '消息内容',
    dataIndex: 'content',
    key: 'content',
    ellipsis: true,
    width: 400
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    render: (text: string) => {
      return new Date(text).toLocaleString()
    }
  },
  {
    title: '操作',
    key: 'action'
  }
]

// 分页配置
const pagination = computed(() => {
  return {
    current: searchParams.pageNum,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`
  }
})

// 重置搜索
const resetSearch = () => {
  searchParams.appId = undefined
  searchParams.messageType = undefined
  searchParams.pageNum = 1
  fetchChatHistoryList()
}

// 获取对话历史列表
const fetchChatHistoryList = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({
      appId: searchParams.appId,
      messageType: searchParams.messageType,
      pageNum: searchParams.pageNum,
      pageSize: searchParams.pageSize
    })
    if (res.data && res.data.code === 0 && res.data.data) {
      chatHistoryList.value = res.data.data.records || []
      total.value = res.data.data.totalRow || 0
    }
  } catch {
    message.error('获取对话历史列表失败')
  }
}

// 表格变化处理
const doTableChange = (pagination: { current: number; pageSize: number }) => {
  searchParams.pageNum = pagination.current
  searchParams.pageSize = pagination.pageSize
  fetchChatHistoryList()
}

// 删除对话历史
const doDelete = async (id: number) => {
  try {
    const res = await deleteChatHistory({ id })
    if (res.data && res.data.code === 0) {
      message.success('对话历史删除成功')
      fetchChatHistoryList()
    } else {
      message.error('对话历史删除失败')
    }
  } catch {
    message.error('对话历史删除失败')
  }
}

// 页面加载时获取对话历史列表
onMounted(() => {
  fetchChatHistoryList()
})
</script>

<style scoped>
#chatHistoryManagePage {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 500;
  margin: 0;
  color: #333;
}

.search-section {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.chat-history-list {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

/* 响应式设计 */
@media (max-width: 768px) {
  #chatHistoryManagePage {
    padding: 12px;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .chat-history-list {
    padding: 16px;
  }
  
  .a-table {
    font-size: 14px;
  }
}
</style>