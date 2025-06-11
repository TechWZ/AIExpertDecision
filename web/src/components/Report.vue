<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import MarkdownRender from 'vue-markdown-render'

const reportResults = ref({})
const hasData = ref(false)
const loading = ref(true)
let checkDataInterval = null

// 计算是否API还在进行中
const apiInProgress = computed(() => {
  return sessionStorage.getItem('apiCallInProgress') === 'true'
})

const checkReportData = () => {
  const apiInProgress = sessionStorage.getItem('apiCallInProgress')
  const storedResults = sessionStorage.getItem('reportResults')
  
  // 如果API还在进行中，继续等待
  if (apiInProgress === 'true') {
    loading.value = true
    hasData.value = false
    return
  }
  
  // API已完成，检查是否有数据
  if (storedResults) {
    try {
      const parsedResults = JSON.parse(storedResults)
      if (parsedResults && typeof parsedResults === 'object' && parsedResults.finalConclusion) {
        reportResults.value = parsedResults
        hasData.value = true
        loading.value = false
        
        // 在控制台打印报告内容
        console.log('报告页面加载的数据:', parsedResults)
        console.log('sessionStorage原始数据大小:', storedResults.length, '字符')
        
        // 停止定时检查
        if (checkDataInterval) {
          clearInterval(checkDataInterval)
          checkDataInterval = null
        }
      } else {
        // 数据格式不正确，继续等待
        loading.value = true
        hasData.value = false
      }
    } catch (error) {
      console.error('解析报告数据失败:', error)
      // 数据解析失败，继续等待而不是立即停止
      loading.value = true
      hasData.value = false
    }
  } else {
    // 如果API状态已清除但没有数据，说明可能出错了
    if (apiInProgress !== 'true') {
      hasData.value = false
      loading.value = false
      // 停止定时检查
      if (checkDataInterval) {
        clearInterval(checkDataInterval)
        checkDataInterval = null
      }
    } else {
      // 继续等待
      loading.value = true
      hasData.value = false
    }
  }
}

onMounted(() => {
  checkReportData()
  
  // 每隔1秒检查一次数据状态
  checkDataInterval = setInterval(checkReportData, 1000)
})

onUnmounted(() => {
  if (checkDataInterval) {
    clearInterval(checkDataInterval)
  }
})
</script>

<template>
  <div class="report-container">
    <el-row>
      <el-col :span="24">
        <!-- <h1>专家分析报告</h1>
        <el-divider /> -->
        
        <div v-if="loading || apiInProgress" class="loading" v-loading="true" element-loading-text="正在生成专家分析报告，请耐心等待...">
          <div style="height: 200px;"></div>
        </div>
        
        <div v-else-if="!hasData" class="no-data">
          <el-empty description="暂无报告数据" />
        </div>
        
        <div v-else>
          <!-- 显示finalConclusion内容 -->
          <div class="final-conclusion">
            <el-card class="report-card">
              <template #header>
                <h2>专家分析决策报告</h2>
              </template>
              <div class="markdown-content">
                <MarkdownRender :source="reportResults.finalConclusion" />
              </div>
            </el-card>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.report-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.no-data {
  text-align: center;
  margin: 50px 0;
}

.expert-report {
  margin-bottom: 30px;
}

.report-card .markdown-content {
  line-height: 1.6;
}

.report-card h2 {
  margin: 0;
  color: #303133;
}
</style>