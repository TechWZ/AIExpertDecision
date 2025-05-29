<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Promotion, QuestionFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const router = useRouter();
const newMessage = ref('');

const exampleQuestions = ref([
  '我需要评估一个医疗项目的风险与收益',
  '帮我分析这份临床试验数据的关键发现和建议',
  '我需要针对这个复杂医疗案例的专家决策支持'
]);

const handleExampleClick = (example) => {
  newMessage.value = example;
};

const sendMessage = async () => {
  if (newMessage.value.trim() === '') return;
  
  try {
    ElMessage.info('正在为您推荐专家角色...');
    
    const response = await fetch('http://39.103.63.72:5001/api/submit_content', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        content: newMessage.value.trim()
      })
    });
    
    if (response.ok) {
      const result = await response.json();
      ElMessage.success('专家角色推荐成功');
      console.log('推荐结果:', result);
      // 这里可以根据返回的结果进行后续处理
    } else {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
  } catch (error) {
    console.error('请求失败:', error);
    ElMessage.error('推荐专家角色失败，请稍后重试');
  }
  
  newMessage.value = '';
};

const goToExpertList = () => {
  router.push('/expertlist');
};

</script>

<template>
  <div class="chat-container">
    <!-- 中上部分欢迎内容 -->
    <el-row class="welcome-row">
      <el-col :span="24">
        <div class="welcome-content">
          <el-avatar :size="80" :src="'/AigenMed.jpeg'"/>
          <h1 class="welcome-title">AI专家决策系统</h1>
          <p class="welcome-subtitle">输入您的决策需求，AI将为您提供专业分析和建议</p>
          
          <el-divider content-position="center" class="welcome-divider">
            <el-icon><Promotion /></el-icon>
            <span style="margin-left: 8px;">您可以尝试提问</span>
          </el-divider>
          
          <div class="example-questions">
            <el-card 
              v-for="(example, index) in exampleQuestions" 
              :key="index"
              class="example-card" 
              shadow="hover"
              @click="handleExampleClick(example)"
            >
              <div class="example-content">
                <el-icon class="example-icon"><QuestionFilled /></el-icon>
                <span class="example-text">{{ example }}</span>
              </div>
            </el-card>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 输入区域 -->
    <el-row class="input-row">
      <el-col :span="24">
        <div class="input-container">
          <el-input
            v-model="newMessage"
            type="textarea"
            placeholder="输入您的决策需求..."
            :autosize="{ minRows: 4, maxRows: 6 }" 
            resize="none"
            class="message-input"
          />
          <div class="button-group">
            <el-button
              type="primary"
              size="large"
              :disabled="!newMessage.trim()"
              @click="sendMessage"
              class="send-button"
            >
              推荐专家角色
            </el-button>
            <el-button
              size="large"
              class="custom-button"
              @click="goToExpertList"
            >
              自定义专家
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
}

.welcome-row {
  height: 66.67%;
  padding: 40px 20px;
}

.welcome-content {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.welcome-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 12px;
}

.welcome-subtitle {
  font-size: 16px;
  color: #606266;
  margin-bottom: 32px;
}

.welcome-divider {
  margin: 32px 0;
}

.example-questions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.example-card {
  cursor: pointer;
}

.example-content {
  display: flex;
  align-items: center;
  padding: 16px;
  gap: 12px;
}

.example-icon {
  color: var(--el-color-primary);
  font-size: 18px;
}

.example-text {
  flex: 1;
  text-align: left;
  font-size: 14px;
}

.input-row {
  height: 33.33%;
  padding-top: 40px;
}

.input-container {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  height: 100%;
}

.message-input {
  flex: 1;
}

.button-group {
  display: flex;
  flex-direction: column;
  row-gap: 10px;
  width: 140px;
}

.send-button {
  font-size: 14px;
  width: 100%;
}

.custom-button {
  font-size: 14px;
  width: 100%;
  margin-left: 0;
}
</style>
