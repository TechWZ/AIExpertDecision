package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * 执行分析决策的请求对象
 */
@Schema(description = "执行分析决策的请求对象")
public class AnalysisDecisionRequest {
    
    @Schema(description = "研究内容", required = true, example = "人工智能在医疗行业的应用前景研究")
    private String content;
    
    @Schema(description = "各专家角色的提示词（键为专家角色名称，值为提示词内容）", required = true, example = "{\"AI技术专家\": \"作为AI技术专家，请从技术角度分析...\"}")
    private Map<String, String> expertPrompts;
    
    @Schema(description = "现实专家决策（键为现实专家名称，值为对应的决策结论）", required = false, example = "{\"张教授\": \"基于多年临床经验，建议采用A方案...\"}")
    private Map<String, String> realExpertDecisions;

    public AnalysisDecisionRequest() {
    }

    public AnalysisDecisionRequest(String content, Map<String, String> expertPrompts) {
        this.content = content;
        this.expertPrompts = expertPrompts;
    }

    public AnalysisDecisionRequest(String content, Map<String, String> expertPrompts, Map<String, String> realExpertDecisions) {
        this.content = content;
        this.expertPrompts = expertPrompts;
        this.realExpertDecisions = realExpertDecisions;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getExpertPrompts() {
        return expertPrompts;
    }

    public void setExpertPrompts(Map<String, String> expertPrompts) {
        this.expertPrompts = expertPrompts;
    }

    public Map<String, String> getRealExpertDecisions() {
        return realExpertDecisions;
    }

    public void setRealExpertDecisions(Map<String, String> realExpertDecisions) {
        this.realExpertDecisions = realExpertDecisions;
    }
}
