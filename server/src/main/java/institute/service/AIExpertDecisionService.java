package institute.service;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.dto.ExpertRolesRequest;
import institute.dto.ExpertRolesResponse;
import institute.dto.ExpertRolesWithPromptsResponse;
import institute.dto.AnalysisDecisionRequest;
import institute.dto.AnalysisDecisionResponse;
import institute.dto.WhichModelRequest;
import institute.dto.WhichModelResponse;

public interface AIExpertDecisionService {

    /**
     * 获取专家角色列表
     * @param request 包含研究内容的请求对象
     * @return 专家角色列表响应对象
     */
    ExpertRolesResponse getExpertRoles(ExpertRolesRequest request);
    
    /**
     * 获取专家角色列表和提示词
     * @param request 包含研究内容的请求对象
     * @return 包含专家角色和提示词的响应对象
     */
    ExpertRolesWithPromptsResponse getExpertRolesWithPrompts(ExpertRolesRequest request);
    
    /**
     * 生成专家提示词
     * @param request 包含专家角色列表和决策需求的请求对象
     * @return 专家提示词响应对象
     */
    ExpertPromptsResponse generateExpertsPrompts(ExpertPromptsRequest request);
    
    /**
     * 执行分析决策
     * @param request 包含研究内容、专家角色和提示词的请求对象
     * @return 分析决策响应对象
     */
    AnalysisDecisionResponse executeAnalysisDecision(AnalysisDecisionRequest request);
    
    /**
     * 查询当前服务的AI模型信息
     * @param request 查询模型信息的请求对象
     * @return 模型信息响应对象
     */
    WhichModelResponse whichModel(WhichModelRequest request);
}
