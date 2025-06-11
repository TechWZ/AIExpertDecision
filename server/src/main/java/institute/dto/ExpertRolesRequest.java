package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 获取专家角色列表的请求对象
 */
@Schema(description = "获取专家角色列表的请求对象")
public class ExpertRolesRequest {
    
    @Schema(description = "研究内容", required = true, example = "人工智能在医疗行业的应用前景研究")
    private String content;

    public ExpertRolesRequest() {
    }

    public ExpertRolesRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
