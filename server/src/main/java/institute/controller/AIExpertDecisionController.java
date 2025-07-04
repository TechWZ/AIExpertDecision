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
import institute.dto.WhichModelRequest;
import institute.dto.WhichModelResponse;
import institute.service.AIExpertDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "AI专家决策", description = "AI专家决策系统接口")
public class AIExpertDecisionController {
    // 注入service层对象
    @Autowired
    private AIExpertDecisionService aiExpertDecisionService;

    @PostMapping("getExpertRoles")
    @Operation(summary = "获取专家角色列表", description = "根据研究内容和分析角度列表，让大模型推荐5个专家角色")
    public ExpertRolesResponse getExpertRoles(@RequestBody ExpertRolesRequest request) {
        return aiExpertDecisionService.getExpertRoles(request);
    }

    @PostMapping("getExpertRolesWithPrompts")
    @Operation(summary = "获取专家角色列表和提示词", description = "根据研究内容和分析角度列表，让大模型推荐5个专家角色并生成对应的提示词")
    public ExpertRolesWithPromptsResponse getExpertRolesWithPrompts(@RequestBody ExpertRolesRequest request) {
        return aiExpertDecisionService.getExpertRolesWithPrompts(request);
    }

    @PostMapping("generateExpertsPrompts")
    @Operation(summary = "生成专家提示词", description = "根据决策需求和专家角色（每个角色包含其对应的分析角度），生成各专家角色的提示词")
    public ExpertPromptsResponse generateExpertsPrompts(@RequestBody ExpertPromptsRequest request) {
        return aiExpertDecisionService.generateExpertsPrompts(request);
    }

    @PostMapping("executeAnalysisDecision")
    @Operation(summary = "执行分析决策", description = "根据研究内容、专家角色和提示词，生成各专家的分析决策和最终综合结论。支持包含现实专家的决策意见，现实专家的决策将原封不动返回，并在综合结论中被充分考虑。")
    public AnalysisDecisionResponse executeAnalysisDecision(@RequestBody AnalysisDecisionRequest request) {
        return aiExpertDecisionService.executeAnalysisDecision(request);
    }

    @PostMapping("whichModel")
    @Operation(summary = "查询当前AI模型信息", description = "询问大模型当前是哪个模型在服务，获取模型的详细信息")
    public WhichModelResponse whichModel(@RequestBody WhichModelRequest request) {
        return aiExpertDecisionService.whichModel(request);
    }

}
