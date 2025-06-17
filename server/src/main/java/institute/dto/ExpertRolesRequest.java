package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 获取专家角色列表的请求对象
 */
@Schema(description = "获取专家角色列表的请求对象")
public class ExpertRolesRequest {
    
    @Schema(description = "研究内容", required = true, example = "人工智能在医疗行业的应用前景研究")
    private String content;
    
    @Schema(description = "分析角度列表", required = false, example = "[\"技术可行性分析\", \"市场前景分析\", \"风险评估分析\"]")
    private List<String> analysisAngles;

    public ExpertRolesRequest() {
    }

    public ExpertRolesRequest(String content) {
        this.content = content;
    }

    public ExpertRolesRequest(String content, List<String> analysisAngles) {
        this.content = content;
        this.analysisAngles = analysisAngles;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getAnalysisAngles() {
        return analysisAngles;
    }

    public void setAnalysisAngles(List<String> analysisAngles) {
        this.analysisAngles = analysisAngles;
    }
}
