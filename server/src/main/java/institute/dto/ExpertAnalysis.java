package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 专家分析结果对象
 */
@Schema(description = "专家分析结果对象")
public class ExpertAnalysis {
    
    @Schema(description = "专家角色名称")
    private String expertRole;
    
    @Schema(description = "专家分析内容")
    private String analysis;
    
    @Schema(description = "专家决策建议")
    private String recommendation;
    
    @Schema(description = "置信度评分(0-100)")
    private Integer confidenceScore;

    public ExpertAnalysis() {
    }

    public ExpertAnalysis(String expertRole, String analysis, String recommendation, Integer confidenceScore) {
        this.expertRole = expertRole;
        this.analysis = analysis;
        this.recommendation = recommendation;
        this.confidenceScore = confidenceScore;
    }

    public String getExpertRole() {
        return expertRole;
    }

    public void setExpertRole(String expertRole) {
        this.expertRole = expertRole;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
