<template>
  <div id="appManagePage">
    <div class="page-header">
      <h1>应用管理</h1>
    </div>
    
    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchParams" @finish="fetchApps">
        <a-form-item label="应用名称">
          <a-input v-model:value="searchParams.appName" placeholder="请输入应用名称" />
        </a-form-item>
        <a-form-item label="生成类型">
          <a-select v-model:value="searchParams.codeGenType" placeholder="请选择生成类型" style="width: 200px;">
            <a-select-option v-for="option in CodeGenTypeOptions" :key="option.value" :value="option.value">{{ option.label }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" :loading="loading">搜索</a-button>
          <a-button @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 应用列表 -->
    <div class="app-list">
      <a-table
        :columns="columns"
        :data-source="appList"
        :pagination="pagination"
        @change="doTableChange"
        :loading="loading"
        row-key="id"
        :locale="{
          emptyText: '暂无应用数据，请尝试调整搜索条件'
        }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space size="small">
              <a-button type="link" size="small" @click="goToEdit(record.id)">编辑</a-button>
              <a-button type="link" size="small" danger @click="doDelete(record.id)">删除</a-button>
              <a-button type="link" size="small" @click="setAsFeatured(record.id)">{{ record.priority === 99 ? '取消精选' : '精选' }}</a-button>
            </a-space>
          </template>
        </template>
      </a-table>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAppVoByPageByAdmin, deleteAppByAdmin, updateAppByAdmin } from '@/api/appController'
import { CodeGenTypeOptions, getCodeGenTypeLabel } from '@/constants/codeGenType'

const router = useRouter()

// 搜索参数
const searchParams = reactive({
  appName: '',
  codeGenType: '',
  pageNum: 1,
  pageSize: 20
})

// 应用列表
const appList = ref<Array<API.AppVO>>([])
// 总数
const total = ref(0)
// 加载状态
const loading = ref(false)

// 表格列配置
const columns = [
  {
    title: '应用ID',
    dataIndex: 'id',
    key: 'id'
  },
  {
    title: '应用名称',
    dataIndex: 'appName',
    key: 'appName'
  },
  {
    title: '生成类型',
    dataIndex: 'codeGenType',
    key: 'codeGenType',
    render: (text: string) => {
      const label = getCodeGenTypeLabel(text) || text
      return label
    }
  },
  {
    title: '创建用户',
    dataIndex: 'user',
    key: 'user',
  render: (user: any) => {
    console.log('user object:', user); // 调试
    console.log('userName:', user?.userName); // 调试
    
    if (!user) return '-';
    return user.userName || '-';
  }
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
    render: (text: number) => {
      if (text === 99) {
        return '精选'
      } else {
        return text || text === 0 ? text : '-' 
      }
    },
    customCell: (record: any) => {
      return record && record.priority === 99 ? { style: { color: '#1890ff' } } : {}
    }
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
    render: (text: string) => {
      try {
        return text ? new Date(text).toLocaleString() : '-' 
      } catch {
        return '-' 
      }
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
  searchParams.appName = ''
  searchParams.codeGenType = ''
  searchParams.pageNum = 1
  fetchApps()
}

// 获取应用列表
const fetchApps = async () => {
  loading.value = true
  try {
    const res = await listAppVoByPageByAdmin(searchParams)
    if (res.data && res.data.code === 0 && res.data.data) {
      appList.value = res.data.data.records || []
      total.value = res.data.data.totalRow || 0
      console.log('获取应用用户名:', appList.value[0].user?.userName)
    } else {
      appList.value = []
      total.value = 0
      message.warning('获取应用列表为空')
    }
  } catch (error) {
    console.error('获取应用列表失败:', error)
    appList.value = []
    total.value = 0
    message.error('获取应用列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 表格变化处理
const doTableChange = (pagination: { current: number; pageSize: number }) => {
  searchParams.pageNum = pagination.current
  searchParams.pageSize = pagination.pageSize
  fetchApps()
}

// 跳转到编辑页面
const goToEdit = (id: number) => {
  router.push(`/app/update/${id}`)
}

// 删除应用
const doDelete = async (id: number) => {
  try {
    const res = await deleteAppByAdmin({ id })
    if (res.data && res.data.code === 0) {
      message.success('应用删除成功')
      fetchApps()
    } else {
      message.error('应用删除失败，请稍后重试')
    }
  } catch (error) {
    console.error('删除应用失败:', error)
    message.error('应用删除失败，请稍后重试')
  }
}

// 设置为精选
const setAsFeatured = async (id: number) => {
  const app = appList.value.find(item => item.id === id)
  if (!app) return
  
  const newPriority = app.priority === 99 ? 0 : 99
  try {
    const res = await updateAppByAdmin({
      id: id,
      priority: newPriority
    })
    if (res.data && res.data.code === 0) {
      message.success(newPriority === 99 ? '设置精选成功' : '取消精选成功')
      fetchApps()
    } else {
      message.error('操作失败，请稍后重试')
    }
  } catch (error) {
    console.error('设置精选失败:', error)
    message.error('操作失败，请稍后重试')
  }
}

// 页面加载时获取应用列表
onMounted(() => {
  fetchApps()
})
</script>

<style scoped>
#appManagePage {
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

.app-list {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  min-height: 400px;
}

.empty-state {
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  #appManagePage {
    padding: 12px;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .app-list {
    padding: 16px;
    min-height: 300px;
  }
  
  .a-table {
    font-size: 14px;
  }
}
</style>