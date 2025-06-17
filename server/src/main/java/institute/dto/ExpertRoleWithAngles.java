package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 专家角色对象（包含分析角度）
 */
@Schema(description = "专家角色对象（包含分析角度）")
public class ExpertRoleWithAngles {
    
    @Schema(description = "专家角色名称", example = "数据科学专家")
    private String roleName;
    
    @Schema(description = "该专家角色的分析角度列表", example = "[\"技术可行性\", \"数据质量评估\"]")
    private List<String> analysisAngles;

    public ExpertRoleWithAngles() {
    }

    public ExpertRoleWithAngles(String roleName, List<String> analysisAngles) {
        this.roleName = roleName;
        this.analysisAngles = analysisAngles;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<String> getAnalysisAngles() {
        return analysisAngles;
    }

    public void setAnalysisAngles(List<String> analysisAngles) {
        this.analysisAngles = analysisAngles;
    }

    @Override
    public String toString() {
        return "ExpertRoleWithAngles{" +
                "roleName='" + roleName + '\'' +
                ", analysisAngles=" + analysisAngles +
                '}';
    }
}
