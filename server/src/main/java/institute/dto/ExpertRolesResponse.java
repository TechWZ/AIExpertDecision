package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 获取专家角色列表的响应对象
 */
@Schema(description = "获取专家角色列表的响应对象")
public class ExpertRolesResponse {
    
    @Schema(description = "操作是否成功")
    private boolean success;
    
    @Schema(description = "研究内容")
    private String content;
    
    @Schema(description = "推荐的专家角色列表（包含匹配度）")
    private List<ExpertRole> expertRoles;
    
    @Schema(description = "AI分析说明")
    private String analysisExplanation;
    
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;

    public ExpertRolesResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ExpertRolesResponse(boolean success, String content, List<ExpertRole> expertRoles, String analysisExplanation) {
        this.success = success;
        this.content = content;
        this.expertRoles = expertRoles;
        this.analysisExplanation = analysisExplanation;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ExpertRole> getExpertRoles() {
        return expertRoles;
    }

    public void setExpertRoles(List<ExpertRole> expertRoles) {
        this.expertRoles = expertRoles;
    }

    public String getAnalysisExplanation() {
        return analysisExplanation;
    }

    public void setAnalysisExplanation(String analysisExplanation) {
        this.analysisExplanation = analysisExplanation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
