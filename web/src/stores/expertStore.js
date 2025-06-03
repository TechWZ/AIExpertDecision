import { defineStore } from 'pinia'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export const useExpertStore = defineStore('expert', () => {
  // 状态数据
  const recommendedExperts = ref([])
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

  // 添加测试数据
  const loadTestData = () => {
    recommendedExperts.value = [
      {
        id: 1001,
        date: '心血管专家',
        name: 95,
        state: 'GPT-4',
        selected: false,
      },
      {
        id: 1002,
        date: '肿瘤学专家', 
        name: 88,
        state: 'Claude-3',
        selected: false,
      },
      {
        id: 1003,
        date: '神经内科专家',
        name: 92,
        state: 'GPT-4',
        selected: false,
      }
    ]
  }

  return {
    recommendedExperts,
    isLoading,
    error,
    fetchRecommendedExperts,
    clearRecommendedExperts,
    loadTestData
  }
})
