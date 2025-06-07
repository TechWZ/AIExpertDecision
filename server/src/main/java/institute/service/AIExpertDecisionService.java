package institute.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import institute.pojo.Directory;
import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;

public interface AIExpertDecisionService {

    
    /**
     * 生成专家提示词
     * @param request 包含专家角色列表和决策需求的请求对象
     * @return 专家提示词响应对象
     */
    ExpertPromptsResponse generateExpertsPrompts(ExpertPromptsRequest request);
}
