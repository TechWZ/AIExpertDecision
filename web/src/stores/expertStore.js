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

  // 获取推荐专家数据（新API直接返回专家角色和提示词）
  const fetchRecommendedExperts = async (content, apiPath, analysisAngles = [""]) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await fetch(apiPath, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          content: content,
          analysisAngles: analysisAngles
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
      
      // 适配新的API数据格式：{expertRolesWithPrompts: [{roleName: "", matchScore: 0, prompt: ""}]}
      const expertRolesWithPrompts = result.expertRolesWithPrompts || []
      
      // 使用固定的ID生成策略，确保ID稳定
      const baseTimestamp = Date.now()
      recommendedExperts.value = expertRolesWithPrompts.map((role, index) => ({
        id: `expert_${baseTimestamp}_${index}`, // 使用字符串ID，更稳定
        date: role.roleName || `专家${index + 1}`, // 专家名称
        name: role.matchScore || 0, // 匹配度 - 直接使用后端返回的数值
        state: 'GPT-4', // 默认大模型
        selected: false,
        city: 'Los Angeles',
        address: 'No. 189, Grove St, Los Angeles',
        zip: 'CA 90036',
      }))
      
      // 直接存储专家提示词数据
      expertPrompts.value = {}
      expertRolesWithPrompts.forEach((role, index) => {
        const expertId = `expert_${baseTimestamp}_${index}`
        if (role.prompt) {
          expertPrompts.value[expertId] = role.prompt
        }
      })
      
      console.log('生成的专家数据:', recommendedExperts.value)
      console.log('生成的专家提示词:', expertPrompts.value)
      
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
    clearRecommendedExperts,
    setUserContent,
    setSelectedModel
  }
})
