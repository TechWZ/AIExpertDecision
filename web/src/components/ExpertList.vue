<script setup>
import { ref } from 'vue'
import dayjs from 'dayjs'
import { ArrowDown, Check } from '@element-plus/icons-vue'

const now = new Date()

const tableData = ref([
  {
    date: '心血管专家',
    name: '95%',
    state: 'GPT-4',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
    selected: false,
  },
  {
    date: '神经科专家',
    name: '88%',
    state: 'Claude-3',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
    selected: true,
  },
  {
    date: '肿瘤专家',
    name: '92%',
    state: 'Gemini',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
    selected: false,
  },
])

const selectedExperts = ref([])

const deleteRow = (index) => {
  tableData.value.splice(index, 1)
}

const handleModelSelect = (model, index) => {
  tableData.value[index].state = model
}

const handleExpertSelect = (value, index) => {
  tableData.value[index].selected = value
  // 更新选中的专家列表
  selectedExperts.value = tableData.value
    .filter(expert => expert.selected)
    .map(expert => expert.date)
}

const onAddItem = () => {
  now.setDate(now.getDate() + 1)
  tableData.value.push({
    date: '新专家',
    name: '85%',
    state: 'GPT-4',
    city: 'Los Angeles',
    address: 'No. 189, Grove St, Los Angeles',
    zip: 'CA 90036',
  })
}
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
                <el-table :data="tableData" style="width: 100%" max-height="250">
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
            </el-col>
            <el-col :span="12">
              <el-card shadow="never" class="table-card">
                <template #header>
                  <h3>自定义专家</h3>
                </template>
                <el-table :data="tableData" style="width: 100%" max-height="250">
                  <el-table-column fixed prop="date" label="Date" width="150" />
                  <el-table-column prop="name" label="Name" width="120" />
                  <el-table-column prop="state" label="State" width="120" />
                  <el-table-column prop="city" label="City" width="120" />
                  <el-table-column prop="address" label="Address" width="600" />
                  <el-table-column prop="zip" label="Zip" width="120" />
                  <el-table-column fixed="right" label="Operations" min-width="120">
                    <template #default="scope">
                      <el-button
                        link
                        type="primary"
                        size="small"
                        @click.prevent="deleteRow(scope.$index)"
                      >
                        Remove
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <el-button class="mt-4" style="width: 100%" @click="onAddItem">
                  Add Item
                </el-button>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.expert-list-container {
  height: 100vh;
  background-color: #f8f9fa;
  display: flex;
  flex-direction: column;
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
  flex: 1;
  padding: 24px;
}

.content-area {
  max-width: 1200px;
  margin: 0 auto;
  height: 100%;
}

.table-card {
  height: 100%;
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