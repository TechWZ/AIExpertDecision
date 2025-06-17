package institute.dto;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "生成专家提示词的请求对象")
public class ExpertPromptsRequest {
    @Schema(description = "专家角色及其分析角度映射（key: 专家角色名称, value: 该专家的分析角度列表）", 
            example = "{\"数据科学专家\": [\"技术可行性\", \"数据质量评估\"], \"业务分析专家\": [\"商业价值\", \"市场需求分析\"]}")
    private Map<String, List<String>> expertRoles;
    
    @Schema(description = "决策需求/研究内容", example = "分析电商用户购买行为")
    private String decisionRequirement;

    public ExpertPromptsRequest() {
    }

    public ExpertPromptsRequest(Map<String, List<String>> expertRoles, String decisionRequirement) {
        this.expertRoles = expertRoles;
        this.decisionRequirement = decisionRequirement;
    }

    public Map<String, List<String>> getExpertRoles() {
        return expertRoles;
    }

    public void setExpertRoles(Map<String, List<String>> expertRoles) {
        this.expertRoles = expertRoles;
    }

    public String getDecisionRequirement() {
        return decisionRequirement;
    }

    public void setDecisionRequirement(String decisionRequirement) {
        this.decisionRequirement = decisionRequirement;
    }
}
