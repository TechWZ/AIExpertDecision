package institute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.service.AIExpertDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "AI专家决策", description = "AI专家决策系统接口")
public class AIExpertDecisionController {
    // 注入service层对象
    @Autowired
    private AIExpertDecisionService aiExpertDecisionService;

    @PostMapping("generateExpertsPrompts")
    @Operation(summary = "生成专家提示词", description = "根据决策需求和专家角色列表，生成各专家角色的提示词")
    public ExpertPromptsResponse generateExpertsPrompts(@RequestBody ExpertPromptsRequest request) {
        return aiExpertDecisionService.generateExpertsPrompts(request);
    }

}
