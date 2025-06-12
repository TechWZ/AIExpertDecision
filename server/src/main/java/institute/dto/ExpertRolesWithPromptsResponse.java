package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 获取专家角色和提示词的响应对象
 */
@Schema(description = "获取专家角色和提示词的响应对象")
public class ExpertRolesWithPromptsResponse {
    
    @Schema(description = "操作是否成功")
    private boolean success;
    
    @Schema(description = "研究内容")
    private String content;
    
    @Schema(description = "包含提示词的专家角色列表（包含匹配度和提示词）")
    private List<ExpertRoleWithPrompt> expertRolesWithPrompts;
    
    @Schema(description = "AI分析说明")
    private String analysisExplanation;
    
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;

    public ExpertRolesWithPromptsResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ExpertRolesWithPromptsResponse(boolean success, String content, 
                                        List<ExpertRoleWithPrompt> expertRolesWithPrompts, 
                                        String analysisExplanation) {
        this.success = success;
        this.content = content;
        this.expertRolesWithPrompts = expertRolesWithPrompts;
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

    public List<ExpertRoleWithPrompt> getExpertRolesWithPrompts() {
        return expertRolesWithPrompts;
    }

    public void setExpertRolesWithPrompts(List<ExpertRoleWithPrompt> expertRolesWithPrompts) {
        this.expertRolesWithPrompts = expertRolesWithPrompts;
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
