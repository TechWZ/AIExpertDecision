<script setup>
import { ref, computed, onMounted } from 'vue'
import dayjs from 'dayjs'
import { ArrowDown, Check } from '@element-plus/icons-vue'
import { useExpertStore } from '@/stores/expertStore'

const now = new Date()
const expertStore = useExpertStore()

// 自定义专家数据
const customExperts = ref([])

// 获取推荐专家数据（来自API）
const recommendedExperts = computed(() => expertStore.recommendedExperts)

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
})
</script>

<template>
  <div class="expert-list-container">
    <el-row class="header-row">
      <el-col :span="24">
        <div class="header-content">
          <h1 class="page-title">自定义专家</h1>
        </div>
      </el-col>
    </el-row>
    
    <el-row class="content-row">
      <el-col :span="24">
        <div class="content-area">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="never" class="table-card">
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
                        <el-button 
                          :type="scope.row.selected ? 'primary' : ''"
                          :icon="scope.row.selected ? Check : ''"
                          circle
                          @click="handleExpertSelect(!scope.row.selected, scope.$index)"
                        />
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
                      <el-button
                        link
                        type="primary"
                        size="small"
                        @click.prevent="deleteRow(scope.$index)"
                      >
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
                    </div>
                  </template>
                  <el-input
                    v-model="expertInputs[expert.id]"
                    style="width: 100%"
                    :autosize="{ minRows: 2 }"
                    type="textarea"
                    placeholder="请输入提示词"
                  />
                </el-card>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
    
    <!-- 确认执行按钮 -->
    <el-button type="primary" plain>Primary</el-button>

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
  </div>
</template>

<style scoped>
.expert-list-container {
  height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header-row {
  flex-shrink: 0;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
}

.header-content {
  display: flex;
  align-items: center;
  padding: 16px 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #303133;
}

.content-row {
  /* flex: 1; */
  padding: 24px;
  /* overflow-y: auto; */
}

.content-area {
  max-width: 1200px;
  margin: 0 auto;
}

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
</style>