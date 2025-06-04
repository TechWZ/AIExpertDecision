<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Promotion, QuestionFilled } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useExpertStore } from '@/stores/expertStore';
import { useStepsStore } from '@/stores/stepsStore';

const router = useRouter();
const expertStore = useExpertStore();
const stepsStore = useStepsStore();
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
    
    const result = await expertStore.fetchRecommendedExperts(newMessage.value.trim());
    
    ElMessage.success('专家角色推荐成功');
    console.log('推荐结果:', result);
    
    // 更新步骤状态为1
    stepsStore.setStep(1);
    
    // 跳转到专家列表页面
    router.push('/expertlist');
  } catch (error) {
    console.error('请求失败:', error);
    ElMessage.error('推荐专家角色失败，请稍后重试');
  }
  
  newMessage.value = '';
};

const goToExpertList = () => {
  // 更新步骤状态为1
  stepsStore.setStep(1);
  router.push('/expertlist');
};

const loadTestData = () => {
  expertStore.loadTestData();
  ElMessage.success('测试数据已加载');
  // 更新步骤状态为1
  stepsStore.setStep(1);
  router.push('/expertlist');
};

</script>

<template>
    <!-- 中上部分欢迎内容 -->
    <el-row style="margin-top: 1%;">
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
            :autosize="{ minRows: 4 }" 
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
            <el-button
              size="large"
              type="success"
              class="test-button"
              @click="loadTestData"
            >
              测试数据
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
</template>

<style scoped>

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
  flex-shrink: 0;
  padding: 20px;
  min-height: 200px;
}

.input-container {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
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

.test-button {
  font-size: 14px;
  width: 100%;
  margin-left: 0;
}
</style>
