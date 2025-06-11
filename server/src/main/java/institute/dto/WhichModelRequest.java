package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 查询当前模型的请求对象
 */
@Schema(description = "查询当前模型的请求对象")
public class WhichModelRequest {
    
    @Schema(description = "指定要使用的模型名称（可选，不传则使用配置文件默认值）", example = "deepseek-reasoner")
    private String model;
    
    @Schema(description = "附加查询信息（可选）", example = "请详细介绍你的模型信息")
    private String additionalQuery;

    public WhichModelRequest() {
    }

    public WhichModelRequest(String model, String additionalQuery) {
        this.model = model;
        this.additionalQuery = additionalQuery;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAdditionalQuery() {
        return additionalQuery;
    }

    public void setAdditionalQuery(String additionalQuery) {
        this.additionalQuery = additionalQuery;
    }

    @Override
    public String toString() {
        return "WhichModelRequest{" +
                "model='" + model + '\'' +
                ", additionalQuery='" + additionalQuery + '\'' +
                '}';
    }
}
