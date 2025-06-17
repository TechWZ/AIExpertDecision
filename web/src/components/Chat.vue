<script setup>
import { ref, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import { Promotion, QuestionFilled, Plus, Close } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useExpertStore } from '@/stores/expertStore';
import { useStepsStore } from '@/stores/stepsStore';
import { API_PATHS } from '@/config/api';

const router = useRouter();
const expertStore = useExpertStore();
const stepsStore = useStepsStore();
const newMessage = ref('');
const selectedModel = ref('deepSeekR1');
const analysisAngles = ref([]);
const inputValue = ref('');

const exampleQuestions = ref([
  '我需要评估一个医疗项目的风险与收益',
  '帮我分析这份临床试验数据的关键发现和建议',
  '我需要针对这个复杂医疗案例的专家决策支持'
]);

const modelOptions = ref([
  {
    value: 'deepSeekR1',
    label: 'DeepSeek R1',
  },
  {
    value: 'gemini2.5ProPreview',
    label: 'Gemini 2.5 Pro Preview',
  },
]);

const handleExampleClick = (example) => {
  newMessage.value = example;
};

const handleInputConfirm = () => {
  if (inputValue.value) {
    analysisAngles.value.push(inputValue.value);
    inputValue.value = '';
  }
};

const handleInputKeyup = (event) => {
  if (event.key === ' ' && inputValue.value.trim()) {
    analysisAngles.value.push(inputValue.value.trim());
    inputValue.value = '';
  }
};

const handleClose = (tag) => {
  analysisAngles.value.splice(analysisAngles.value.indexOf(tag), 1);
};

const sendMessage = async () => {
  if (newMessage.value.trim() === '') return;

  const userDecisionRequirement = newMessage.value.trim();

  // 保存用户输入的内容到store
  expertStore.setUserContent(userDecisionRequirement);
  // 保存用户选择的模型到store
  expertStore.setSelectedModel(selectedModel.value);

  try {
    const loadingMessage = ElMessage.info({
      message: '正在为您推荐专家角色并生成提示词...',
      duration: 0 // 设置为0表示不自动关闭
    });

    // 根据选择的模型调用不同的新API，传递分析角度参数
    let result;
    if (selectedModel.value === 'deepSeekR1') {
      result = await expertStore.fetchRecommendedExperts(userDecisionRequirement, API_PATHS.getExpertRolesWithPrompts, analysisAngles.value);
    } else if (selectedModel.value === 'gemini2.5ProPreview') {
      result = await expertStore.fetchRecommendedExperts(userDecisionRequirement, API_PATHS.getExpertRolesWithPrompts2Model, analysisAngles.value);
    }

    // 关闭loading消息
    loadingMessage.close();
    ElMessage.success('专家角色推荐和提示词生成成功');
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

</script>

<template>
  <!-- 中上部分欢迎内容 -->
  <el-row style="margin-top: 1%;">
    <el-col :span="24">
      <div class="welcome-content">
        <el-avatar :size="80" :src="'/AigenMed.jpeg'" />
        <h1 class="welcome-title">AI专家决策系统</h1>
        <p class="welcome-subtitle">输入您的决策需求，AI将为您提供专业分析和建议</p>

        <el-divider content-position="center" class="welcome-divider">
          <el-icon>
            <Promotion />
          </el-icon>
          <span style="margin-left: 8px;">您可以尝试提问</span>
        </el-divider>

        <div class="example-questions">
          <el-card v-for="(example, index) in exampleQuestions" :key="index" class="example-card" shadow="hover"
            @click="handleExampleClick(example)">
            <div class="example-content">
              <el-icon class="example-icon">
                <QuestionFilled />
              </el-icon>
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
        <el-row>
          <el-col>
            <el-input v-model="newMessage" type="textarea" placeholder="输入您的决策需求..." :autosize="{ minRows: 4 }"
              resize="none" class="message-input" />
            <!-- 分析角度输入域 -->
            <el-row class="analysis-angles-row">
              <el-col :span="24">
                <div class="analysis-angles-container">
                  <label class="analysis-angles-label">分析角度：</label>
                  <div class="tag-input-container">
                    <el-tag v-for="tag in analysisAngles" :key="tag" closable :disable-transitions="false"
                      @close="handleClose(tag)" class="analysis-tag">
                      {{ tag }}
                    </el-tag>
                    <el-input v-model="inputValue" size="small" @keyup="handleInputKeyup" @blur="handleInputConfirm"
                      placeholder="输入分析角度，按空格键添加" class="tag-input" style="width: 200px;" />
                  </div>
                </div>
              </el-col>
            </el-row>
          </el-col>
        </el-row>


        <div class="button-group">
          <el-button type="primary" size="large" :disabled="!newMessage.trim()" @click="sendMessage"
            class="send-button">
            推荐专家角色
          </el-button>
          <el-button size="large" class="custom-button" @click="goToExpertList">
            自定义专家
          </el-button>
          <el-select v-model="selectedModel" placeholder="选择模型" size="large" style="width: 100%">
            <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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

.analysis-angles-row {
  padding: 0 20px 20px 20px;
}

.analysis-angles-container {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.analysis-angles-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.tag-input-container {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.analysis-tag {
  margin: 0;
}

.tag-input {
  flex-shrink: 0;
}
</style>
