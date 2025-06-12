package institute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.dto.ExpertRolesRequest;
import institute.dto.ExpertRolesResponse;
import institute.dto.ExpertRolesWithPromptsResponse;
import institute.dto.AnalysisDecisionRequest;
import institute.dto.AnalysisDecisionResponse;
import institute.service.OpenRouterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "OpenRouter AI专家决策", description = "基于OpenRouter的AI专家决策系统接口")
public class OpenRouterController {
    // 注入service层对象
    @Autowired
    private OpenRouterService openRouterService;

    @PostMapping("getExpertRoles2Model")
    @Operation(summary = "获取专家角色列表 (OpenRouter)", description = "基于OpenRouter API，根据研究内容让大模型推荐5个专家角色")
    public ExpertRolesResponse getExpertRoles(@RequestBody ExpertRolesRequest request) {
        return openRouterService.getExpertRoles(request);
    }

    @PostMapping("getExpertRolesWithPrompts2Model")
    @Operation(summary = "获取专家角色列表和提示词 (OpenRouter)", description = "基于OpenRouter API，根据研究内容让大模型推荐5个专家角色并生成对应的提示词")
    public ExpertRolesWithPromptsResponse getExpertRolesWithPrompts(@RequestBody ExpertRolesRequest request) {
        return openRouterService.getExpertRolesWithPrompts(request);
    }

    @PostMapping("generateExpertsPrompts2Model")
    @Operation(summary = "生成专家提示词 (OpenRouter)", description = "基于OpenRouter API，根据决策需求和专家角色列表生成各专家角色的提示词")
    public ExpertPromptsResponse generateExpertsPrompts(@RequestBody ExpertPromptsRequest request) {
        return openRouterService.generateExpertsPrompts(request);
    }

    @PostMapping("executeAnalysisDecision2Model")
    @Operation(summary = "执行分析决策 (OpenRouter)", description = "基于OpenRouter API，根据研究内容、专家角色和提示词生成各专家的分析决策和最终综合结论")
    public AnalysisDecisionResponse executeAnalysisDecision(@RequestBody AnalysisDecisionRequest request) {
        return openRouterService.executeAnalysisDecision(request);
    }
}
