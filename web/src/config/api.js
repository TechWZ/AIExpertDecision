// API配置文件
// 根据环境自动选择API基础地址

// 判断是否为开发环境
const isDevelopment = import.meta.env.DEV

// 开发环境使用代理，生产环境使用nginx反向代理
const API_BASE_URL = isDevelopment 
  ? '/server'  // 开发环境使用代理
  : '/api'  // 生产环境使用nginx反向代理

// API路径配置
const API_PATHS = {
  // DeepSeek R1 模型的API路径
  getExpertRolesWithPrompts: `${API_BASE_URL}/getExpertRolesWithPrompts`,
  generateExpertsPrompts: `${API_BASE_URL}/generateExpertsPrompts`,
  executeAnalysisDecision: `${API_BASE_URL}/executeAnalysisDecision`,
  
  // Gemini 2.5 Pro Preview 模型的API路径
  getExpertRolesWithPrompts2Model: `${API_BASE_URL}/getExpertRolesWithPrompts2Model`,
  generateExpertsPrompts2Model: `${API_BASE_URL}/generateExpertsPrompts2Model`,
  executeAnalysisDecision2Model: `${API_BASE_URL}/executeAnalysisDecision2Model`,
  
  // 其他API
  getChat: `${API_BASE_URL}/getChat`,
}

// 环境信息
console.log('API配置信息:', {
  isDevelopment,
  API_BASE_URL,
  environment: import.meta.env.MODE
})

// 导出配置
export { API_PATHS, API_BASE_URL }
