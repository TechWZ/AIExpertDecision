<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import MarkdownRender from 'vue-markdown-render'

const reportResults = ref({})
const hasData = ref(false)
const loading = ref(true)
let checkDataInterval = null

const checkReportData = () => {
  const storedResults = sessionStorage.getItem('reportResults')
  if (storedResults) {
    try {
      const parsedResults = JSON.parse(storedResults)
      if (parsedResults && typeof parsedResults === 'object') {
        reportResults.value = parsedResults
        hasData.value = Object.keys(parsedResults).length > 0
        
        // 在控制台打印报告内容
        console.log('报告页面加载的数据:', parsedResults)
        console.log('sessionStorage原始数据大小:', storedResults.length, '字符')
        
        // 详细输出每个专家的完整报告内容
        Object.entries(parsedResults).forEach(([expertName, content]) => {
          console.log(`=== ${expertName} ===`)
          console.log(`内容长度: ${content ? content.length : 0} 字符`)
          console.log(`内容开头100字符:`, content ? content.slice(0, 100) : 'null')
          console.log(`内容结尾100字符:`, content ? content.slice(-100) : 'null')
          console.log(`完整内容:`, content)
          console.log('===============================')
        })
        
        if (hasData.value) {
          loading.value = false
          // 停止定时检查
          if (checkDataInterval) {
            clearInterval(checkDataInterval)
            checkDataInterval = null
          }
        }
      }
    } catch (error) {
      console.error('解析报告数据失败:', error)
    }
  }
}

onMounted(() => {
  checkReportData()
  
  // 如果没有数据，每隔1秒检查一次
  if (!hasData.value) {
    checkDataInterval = setInterval(checkReportData, 1000)
    // 5秒后停止检查
    setTimeout(() => {
      if (checkDataInterval) {
        clearInterval(checkDataInterval)
        checkDataInterval = null
        loading.value = false
      }
    }, 5000)
  } else {
    loading.value = false
  }
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
        <h1>专家分析报告</h1>
        <el-divider />
        
        <div v-if="loading" class="loading" v-loading="true" element-loading-text="加载中...">
          <div style="height: 200px;"></div>
        </div>
        
        <div v-else-if="!hasData" class="no-data">
          <el-empty description="暂无报告数据" />
        </div>
        
        <div v-else>
          <div v-for="(content, expertName) in reportResults" :key="expertName" class="expert-report">
            <el-card class="report-card">
              <template #header>
                <h2>{{ expertName }}</h2>
              </template>
              <div class="markdown-content">
                <MarkdownRender :source="content" />
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