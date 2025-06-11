package institute.service;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.dto.ExpertRolesRequest;
import institute.dto.ExpertRolesResponse;
import institute.dto.AnalysisDecisionRequest;
import institute.dto.AnalysisDecisionResponse;

public interface OpenRouterService {

    /**
     * 获取专家角色列表 (基于OpenRouter)
     * @param request 包含研究内容的请求对象
     * @return 专家角色列表响应对象
     */
    ExpertRolesResponse getExpertRoles(ExpertRolesRequest request);
    
    /**
     * 生成专家提示词 (基于OpenRouter)
     * @param request 包含专家角色列表和决策需求的请求对象
     * @return 专家提示词响应对象
     */
    ExpertPromptsResponse generateExpertsPrompts(ExpertPromptsRequest request);
    
    /**
     * 执行分析决策 (基于OpenRouter)
     * @param request 包含研究内容、专家角色和提示词的请求对象
     * @return 分析决策响应对象
     */
    AnalysisDecisionResponse executeAnalysisDecision(AnalysisDecisionRequest request);
    
    /**
     * 直接调用OpenRouter API
     * @param prompt 用户提示词
     * @param model 指定的模型（可选）
     * @return AI响应内容
     */
    String callOpenRouterAPI(String prompt, String model);
}
