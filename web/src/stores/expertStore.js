import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { API_PATHS } from '@/config/api'

export const useExpertStore = defineStore('expert', () => {
  // 状态数据
  const recommendedExperts = ref([])
  const expertPrompts = ref({}) // 存储专家提示词，键为专家ID，值为提示词内容
  const userContent = ref('') // 存储用户输入的决策需求内容
  const selectedModel = ref('deepSeekR1') // 存储用户选择的模型
  const isLoading = ref(false)
  const error = ref(null)

  // 获取推荐专家数据
  const fetchRecommendedExperts = async (content, apiPath = '/server/submit_content') => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await fetch(apiPath, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content: content
        })
      })
      
      // 检查HTTP状态码
      if (response.status !== 200) {
        ElMessage({
          message: '系统异常，请检查网络',
          type: 'error',
          plain: true,
        })
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      console.log('API返回结果:', result)
      
      // 适配新的API数据格式：{expertRoles: [{roleName: "", matchScore: 0}]}
      const expertRoles = result.expertRoles || []
      
      // 使用固定的ID生成策略，确保ID稳定
      const baseTimestamp = Date.now()
      recommendedExperts.value = expertRoles.map((role, index) => ({
        id: `expert_${baseTimestamp}_${index}`, // 使用字符串ID，更稳定
        date: role.roleName || `专家${index + 1}`, // 专家名称
        name: role.matchScore || 0, // 匹配度 - 直接使用后端返回的数值
        state: 'GPT-4', // 默认大模型
        selected: false,
        city: 'Los Angeles',
        address: 'No. 189, Grove St, Los Angeles',
        zip: 'CA 90036',
      }))
      
      console.log('生成的专家数据:', recommendedExperts.value)
      
      return result
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      isLoading.value = false
    }
  }

  // 清空推荐专家数据
  const clearRecommendedExperts = () => {
    recommendedExperts.value = []
    error.value = null
  }

  // 获取专家提示词
  const fetchExpertPrompts = async (expertRoles, decisionRequirement, selectedModel = 'deepSeekR1') => {
    try {
      // 根据选择的模型确定API路径
      let apiPath = selectedModel === 'gemini2.5ProPreview' 
        ? API_PATHS.generateExpertsPrompts2Model
        : API_PATHS.generateExpertsPrompts
      
      console.log('获取专家提示词 - 请求参数:', {
        expertRoles,
        decisionRequirement,
        selectedModel,
        apiPath
      })
      
      const response = await fetch(apiPath, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          expertRoles: expertRoles,
          decisionRequirement: decisionRequirement
        })
      })
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      
      const result = await response.json()
      console.log('专家提示词API返回结果:', result)
      
      // 存储专家提示词数据到状态中
      if (result.aiResponse && result.aiResponse.expertPrompts) {
        console.log('当前专家列表:', recommendedExperts.value)
        console.log('API返回的专家提示词:', result.aiResponse.expertPrompts)
        
        // API返回格式为 { aiResponse: { expertPrompts: { "专家名称": "提示词内容", ... } } }
        Object.keys(result.aiResponse.expertPrompts).forEach(expertName => {
          // 找到对应的专家ID - 使用date字段（存储的是专家中文名称）进行匹配
          const expert = recommendedExperts.value.find(e => {
            // 先尝试精确匹配
            if (e.date === expertName) {
              return true
            }
            // 如果不匹配，尝试提取中文部分进行匹配（处理中英文混合格式）
            const chineseName = e.date.split(' (')[0] // 提取括号前的中文部分
            return chineseName === expertName
          })
          
          console.log(`查找专家 "${expertName}":`, expert)
          
          if (expert) {
            expertPrompts.value[expert.id] = result.aiResponse.expertPrompts[expertName]
            console.log(`成功存储专家 ${expertName} (ID: ${expert.id}) 的提示词`)
          } else {
            console.warn(`未找到专家 "${expertName}"，当前专家列表:`, recommendedExperts.value.map(e => ({ name: e.date, id: e.id })))
          }
        })
        
        console.log('最终存储的专家提示词:', expertPrompts.value)
      }
      
      return result
    } catch (err) {
      console.error('获取专家提示词失败:', err)
      throw err
    }
  }

  // 设置用户内容
  const setUserContent = (content) => {
    userContent.value = content
  }

  // 设置选择的模型
  const setSelectedModel = (model) => {
    selectedModel.value = model
  }

  return {
    recommendedExperts,
    expertPrompts,
    userContent,
    selectedModel,
    isLoading,
    error,
    fetchRecommendedExperts,
    fetchExpertPrompts,
    clearRecommendedExperts,
    setUserContent,
    setSelectedModel
  }
})
