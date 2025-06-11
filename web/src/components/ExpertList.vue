<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useExpertStore } from '@/stores/expertStore'

const now = new Date()
const expertStore = useExpertStore()
const router = useRouter()

// 自定义专家数据
const customExperts = ref([])

// 获取推荐专家数据（来自API）
const recommendedExperts = computed(() => expertStore.recommendedExperts)

// 获取专家提示词数据
const expertPrompts = computed(() => expertStore.expertPrompts)

// 获取用户输入的内容
const userContent = computed(() => expertStore.userContent)

// 获取用户选择的模型
const selectedModel = computed(() => expertStore.selectedModel)

// 按钮是否可点击的状态 - 基于expertPrompts的计算属性
const promptButtonsEnabled = computed(() => {
  return Object.keys(expertPrompts.value).length > 0
})

// 只显示API返回的推荐专家数据，不提供默认数据
const tableData = computed(() => {
  return recommendedExperts.value || []
})

const selectedExperts = ref([])
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'

// 存储每个选中专家的输入内容
const expertInputs = ref({})

const form = ref({
  role_name: '',
  model: 'GPT-4'
})

const deleteRow = (index) => {
  customExperts.value.splice(index, 1)
}

const handleModelSelect = (model, index) => {
  if (recommendedExperts.value && recommendedExperts.value.length > 0) {
    recommendedExperts.value[index].state = model
  } else {
    tableData.value[index].state = model
  }
}

const handleCustomModelSelect = (model, index) => {
  customExperts.value[index].model = model
}

const handleExpertSelect = (value, index) => {
  if (recommendedExperts.value && recommendedExperts.value.length > 0) {
    recommendedExperts.value[index].selected = value
  } else {
    tableData.value[index].selected = value
  }

  const expert = tableData.value[index]

  if (value) {
    // 选中时，添加到选中列表并初始化输入内容
    if (!selectedExperts.value.find(e => e.id === expert.id)) {
      selectedExperts.value.push(expert)
      expertInputs.value[expert.id] = ''
    }
  } else {
    // 取消选中时，从列表中移除并删除输入内容
    const expertIndex = selectedExperts.value.findIndex(e => e.id === expert.id)
    if (expertIndex !== -1) {
      selectedExperts.value.splice(expertIndex, 1)
      delete expertInputs.value[expert.id]
    }
  }
}

const onAddItem = () => {
  dialogFormVisible.value = true
}

const confirmAddExpert = () => {
  if (form.value.role_name.trim()) {
    customExperts.value.push({
      role_name: form.value.role_name,
      model: form.value.model,
    })
    // 重置表单
    form.value.role_name = ''
    form.value.model = 'GPT-4'
  }
  dialogFormVisible.value = false
}



onMounted(() => {
  // 组件挂载时检查是否有推荐专家数据
  console.log('推荐专家数据:', recommendedExperts.value)
  console.log('专家提示词数据:', expertPrompts.value)
  console.log('按钮启用状态:', promptButtonsEnabled.value)
  
  // 添加响应式监听，当专家提示词数据变化时输出调试信息
  setTimeout(() => {
    console.log('延迟检查 - 推荐专家数据:', recommendedExperts.value)
    console.log('延迟检查 - 专家提示词数据:', expertPrompts.value)
    console.log('延迟检查 - 按钮启用状态:', promptButtonsEnabled.value)
    
    // 检查每个专家的按钮状态
    recommendedExperts.value.forEach(expert => {
      const hasPrompt = !!expertPrompts.value[expert.id]
      console.log(`专家 "${expert.date}" (ID: ${expert.id}) - 有提示词: ${hasPrompt}`)
    })
  }, 2000)
})

onUnmounted(() => {
  // 清理工作（如果需要）
})

// 写入系统提示词
const writeSystemPrompt = (expertId) => {
  console.log('写入系统提示词 - 专家ID:', expertId)
  console.log('当前专家提示词数据:', expertPrompts.value)
  
  const prompt = expertPrompts.value[expertId]
  console.log('找到的提示词:', prompt)
  
  if (prompt) {
    expertInputs.value[expertId] = prompt
    ElMessage.success('提示词已写入')
    console.log('提示词写入成功')
  } else {
    console.log('未找到该专家的提示词')
    console.log('可用的专家ID:', Object.keys(expertPrompts.value))
    ElMessage.warning('该专家暂无可用的系统提示词')
  }
}

// 确认提交，跳转到报告页面
const confirmSubmit = async () => {
  try {
    // 构建请求数据
    const requestData = {
      content: userContent.value,
      expertPrompts: {}
    }
    
    // 添加选中的专家数据
    selectedExperts.value.forEach(expert => {
      const prompt = expertInputs.value[expert.id] || ''
      if (prompt.trim()) {
        requestData.expertPrompts[expert.date] = prompt
      }
    })
    
    // 添加自定义专家数据
    customExperts.value.forEach(expert => {
      if (expert.role_name.trim()) {
        // 自定义专家没有提示词，使用空字符串
        requestData.expertPrompts[expert.role_name] = ''
      }
    })
    
    if (Object.keys(requestData.expertPrompts).length === 0) {
      ElMessage.warning('请先选择专家并填写提示词')
      return
    }
    
    // 设置API调用状态标识
    sessionStorage.setItem('apiCallInProgress', 'true')
    sessionStorage.removeItem('reportResults') // 清除之前的数据
    
    // 显示持续的loading消息
    const loadingMessage = ElMessage({
      message: '正在生成分析报告，请耐心等待...',
      type: 'info',
      duration: 0, // 不自动关闭
      showClose: false
    })
    
    // 根据选择的模型确定API路径
    let apiPath = '/server/executeAnalysisDecision';
    if (selectedModel.value === 'gemini2.5ProPreview') {
      apiPath = '/server/executeAnalysisDecision2Model';
    }
    
    // 调用API
    const response = await fetch(apiPath, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestData)
    })
    
    // 关闭loading消息
    loadingMessage.close()
    
    if (!response.ok) {
      // API调用失败，清除进行中状态
      sessionStorage.removeItem('apiCallInProgress')
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const result = await response.json()
    
    // 详细调试API返回的数据
    console.log('API完整返回数据:', result)
    
    // 检查API响应格式
    if (!result.finalConclusion) {
      // API调用失败，清除进行中状态
      sessionStorage.removeItem('apiCallInProgress')
      throw new Error('API响应格式不正确，缺少finalConclusion字段');
    }
    
    // 将结果存储到sessionStorage中，供Report页面使用
    const resultsJson = JSON.stringify(result)
    console.log('存储到sessionStorage的数据大小:', resultsJson.length, '字符')
    sessionStorage.setItem('reportResults', resultsJson)
    
    // API调用完成，清除进行中状态
    sessionStorage.removeItem('apiCallInProgress')
    
    // 验证存储是否成功
    const storedData = sessionStorage.getItem('reportResults')
    console.log('从sessionStorage读取的数据大小:', storedData ? storedData.length : 0, '字符')
    console.log('存储前后数据大小对比:', resultsJson.length === (storedData ? storedData.length : 0) ? '一致' : '不一致')
    
    ElMessage.success('分析报告生成成功')
    
    // API调用完成后再打开新标签页
    const reportWindow = window.open(router.resolve('/report').href, '_blank')
    
    // 刷新报告页面以显示新数据
    if (reportWindow && !reportWindow.closed) {
      reportWindow.location.reload()
    }
    
  } catch (error) {
    console.error('生成报告失败:', error)
    // API调用失败，清除进行中状态
    sessionStorage.removeItem('apiCallInProgress')
    ElMessage.error('生成报告失败，请稍后重试')
  }
}
</script>

<template>
  <el-row class="content-row" :gutter="20">
    <el-col :span="12">
      <el-card class="table-card">
        <template #header>
          <h3>推荐专家</h3>
        </template>
        <el-table :data="tableData" style="width: 100%">
          <el-table-column fixed prop="date" label="专家" width="150" />
          <el-table-column prop="name" label="匹配度（%）" width="120" />
          <el-table-column label="大模型" width="150">
            <template #default="scope">
              <el-dropdown @command="(model) => handleModelSelect(model, scope.$index)">
                <span class="el-dropdown-link">
                  {{ scope.row.state }}
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="GPT-4">GPT-4</el-dropdown-item>
                    <el-dropdown-item command="Claude-3">Claude-3</el-dropdown-item>
                    <el-dropdown-item command="Gemini">Gemini</el-dropdown-item>
                    <el-dropdown-item command="ChatGLM">ChatGLM</el-dropdown-item>
                    <el-dropdown-item command="文心一言">文心一言</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="" min-width="120">
            <template #default="scope">
              <div class="status-cell">
                <el-button :type="scope.row.selected ? 'primary' : ''" :icon="scope.row.selected ? Check : ''" circle
                  @click="handleExpertSelect(!scope.row.selected, scope.$index)" />
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
      <br>
      <el-card shadow="never" class="table-card">
        <template #header>
          <h3>自定义专家</h3>
        </template>
        <el-table :data="customExperts" style="width: 100%">
          <el-table-column fixed prop="role_name" label="专家" width="150" />
          <el-table-column label="大模型" width="120">
            <template #default="scope">
              <el-dropdown @command="(model) => handleCustomModelSelect(model, scope.$index)">
                <span class="el-dropdown-link">
                  {{ scope.row.model }}
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="GPT-4">GPT-4</el-dropdown-item>
                    <el-dropdown-item command="Claude-3">Claude-3</el-dropdown-item>
                    <el-dropdown-item command="Gemini">Gemini</el-dropdown-item>
                    <el-dropdown-item command="ChatGLM">ChatGLM</el-dropdown-item>
                    <el-dropdown-item command="文心一言">文心一言</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" min-width="120">
            <template #default="scope">
              <el-button link type="primary" size="small" @click.prevent="deleteRow(scope.$index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button class="mt-4" style="width: 100%" @click="onAddItem">
          添加自定义专家
        </el-button>
      </el-card>
    </el-col>
    <el-col :span="12">
      <!-- 提示词 -->
      <div v-for="expert in selectedExperts" :key="expert.id" style="margin-bottom: 20px;">
        <el-card style="max-width: 480px">
          <template #header>
            <div class="card-header">
              <span>{{ expert.date }}</span>
              <el-button type="primary" size="small" 
                :disabled="!promptButtonsEnabled || !expertPrompts[expert.id]"
                @click="writeSystemPrompt(expert.id)">
                写入系统提示词
              </el-button>
            </div>
          </template>
          <el-input v-model="expertInputs[expert.id]" style="width: 100%" :autosize="{ minRows: 2 }" type="textarea"
            placeholder="请输入提示词" />
        </el-card>
      </div>
    </el-col>
  </el-row>

  <!-- 确认执行按钮 -->
  <el-button type="primary" plain class="centered-button" @click="confirmSubmit">确认提交</el-button>

  <!-- 添加自定义专家对话框 -->
  <el-dialog v-model="dialogFormVisible" title="添加自定义专家" width="500">
    <el-form :model="form">
      <el-form-item label="专家名称" :label-width="formLabelWidth">
        <el-input v-model="form.role_name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="大模型" :label-width="formLabelWidth">
        <el-select v-model="form.model" placeholder="请选择大模型">
          <el-option label="GPT-4" value="GPT-4" />
          <el-option label="Claude-3" value="Claude-3" />
          <el-option label="Gemini" value="Gemini" />
          <el-option label="ChatGLM" value="ChatGLM" />
          <el-option label="文心一言" value="文心一言" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddExpert">
          确认
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.content-row {
  /* flex: 1; */
  margin-top: 1%;
  /* overflow-y: auto; */
}

/* .content-area {
  max-width: 1200px;
  margin: 0 auto;
} */

.table-card h3 {
  margin: 0;
  color: #303133;
}

.el-dropdown-link {
  cursor: pointer;
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
  outline: none;
  min-height: 32px;
}

.status-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.centered-button {
  display: block;
  margin-left: auto;
  margin-right: auto;
  margin-top: 10%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>