import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export const useExpertStore = defineStore('expert', () => {
  // 状态数据
  const recommendedExperts = ref([])
  const expertPrompts = ref({}) // 存储专家提示词，键为专家ID，值为提示词内容
  const userContent = ref('') // 存储用户输入的决策需求内容
  const isLoading = ref(false)
  const error = ref(null)

  // 获取推荐专家数据
  const fetchRecommendedExperts = async (content) => {
    isLoading.value = true
    error.value = null
    
    try {
      const response = await fetch('http://39.103.63.72:5001/api/submit_content', {
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
      
      // 直接使用固定的API数据格式
      const roles = result.roles || []
      
      recommendedExperts.value = roles.map((role, index) => ({
        id: Date.now() + index,
        date: role.role_name || `专家${index + 1}`, // 专家名称
        name: role.score || 0, // 匹配度 - 直接使用后端返回的数值
        state: 'GPT-4', // 默认大模型
        selected: false,
        city: 'Los Angeles',
        address: 'No. 189, Grove St, Los Angeles',
        zip: 'CA 90036',
      }))
      
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
  const fetchExpertPrompts = async (expertRoles, decisionRequirement) => {
    try {
      const response = await fetch('/AIExpertDecisionServer/generateExpertsPrompts', {
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
        // API返回格式为 { aiResponse: { expertPrompts: { "专家名称": "提示词内容", ... } } }
        Object.keys(result.aiResponse.expertPrompts).forEach(expertName => {
          // 找到对应的专家ID
          const expert = recommendedExperts.value.find(e => e.date === expertName)
          if (expert) {
            expertPrompts.value[expert.id] = result.aiResponse.expertPrompts[expertName]
          }
        })
      }
      
      return result
    } catch (err) {
      console.error('获取专家提示词失败:', err)
      throw err
    }  }

  // 设置用户内容
  const setUserContent = (content) => {
    userContent.value = content
  }

  return {
    recommendedExperts,
    expertPrompts,
    userContent,
    isLoading,
    error,
    fetchRecommendedExperts,
    fetchExpertPrompts,
    clearRecommendedExperts,
    setUserContent
  }
})
