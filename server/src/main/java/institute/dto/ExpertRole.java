package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 专家角色对象，包含角色名称和匹配度
 */
@Schema(description = "专家角色对象")
public class ExpertRole {
    
    @Schema(description = "专家角色名称", example = "人工智能技术专家")
    private String roleName;
    
    @Schema(description = "匹配度参考值 (0-100)", example = "95")
    private Integer matchScore;

    public ExpertRole() {
    }

    public ExpertRole(String roleName, Integer matchScore) {
        this.roleName = roleName;
        this.matchScore = matchScore;
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

    @Override
    public String toString() {
        return "ExpertRole{" +
                "roleName='" + roleName + '\'' +
                ", matchScore=" + matchScore +
                '}';
    }
}
