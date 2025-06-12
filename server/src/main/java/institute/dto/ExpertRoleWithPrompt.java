package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 包含提示词的专家角色对象
 */
@Schema(description = "包含提示词的专家角色对象")
public class ExpertRoleWithPrompt {
    
    @Schema(description = "专家角色名称", example = "人工智能技术专家")
    private String roleName;
    
    @Schema(description = "匹配度参考值 (0-100)", example = "95")
    private Integer matchScore;
    
    @Schema(description = "专家角色的提示词", example = "作为人工智能技术专家，您需要从技术角度分析...")
    private String prompt;

    public ExpertRoleWithPrompt() {
    }

    public ExpertRoleWithPrompt(String roleName, Integer matchScore, String prompt) {
        this.roleName = roleName;
        this.matchScore = matchScore;
        this.prompt = prompt;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(Integer matchScore) {
        this.matchScore = matchScore;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return "ExpertRoleWithPrompt{" +
                "roleName='" + roleName + '\'' +
                ", matchScore=" + matchScore +
                ", prompt='" + prompt + '\'' +
                '}';
    }
}
