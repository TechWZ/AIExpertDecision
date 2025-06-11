package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 执行分析决策的响应对象
 */
@Schema(description = "执行分析决策的响应对象")
public class AnalysisDecisionResponse {
    
    @Schema(description = "操作是否成功")
    private boolean success;
    
    @Schema(description = "研究内容")
    private String content;
    
    @Schema(description = "各专家的分析结果")
    private List<ExpertAnalysis> expertAnalyses;
    
    @Schema(description = "最终综合结论")
    private String finalConclusion;
    
    @Schema(description = "综合评分(0-100)")
    private Integer overallScore;
    
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;

    public AnalysisDecisionResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public AnalysisDecisionResponse(boolean success, String content, List<ExpertAnalysis> expertAnalyses, 
                                   String finalConclusion, Integer overallScore) {
        this.success = success;
        this.content = content;
        this.expertAnalyses = expertAnalyses;
        this.finalConclusion = finalConclusion;
        this.overallScore = overallScore;
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

    public List<ExpertAnalysis> getExpertAnalyses() {
        return expertAnalyses;
    }

    public void setExpertAnalyses(List<ExpertAnalysis> expertAnalyses) {
        this.expertAnalyses = expertAnalyses;
    }

    public String getFinalConclusion() {
        return finalConclusion;
    }

    public void setFinalConclusion(String finalConclusion) {
        this.finalConclusion = finalConclusion;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
