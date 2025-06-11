package institute.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * 查询当前模型的响应对象
 */
@Schema(description = "查询当前模型的响应对象")
public class WhichModelResponse {
    
    @Schema(description = "请求是否成功", example = "true")
    private boolean success;
    
    @Schema(description = "模型回复的完整内容", example = "我是DeepSeek-V3模型，一个由深度求索开发的大型语言模型...")
    private String modelInfo;
    
    @Schema(description = "响应时间戳")
    private LocalDateTime timestamp;

    public WhichModelResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public WhichModelResponse(boolean success, String modelInfo) {
        this.success = success;
        this.modelInfo = modelInfo;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getModelInfo() {
        return modelInfo;
    }

    public void setModelInfo(String modelInfo) {
        this.modelInfo = modelInfo;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "WhichModelResponse{" +
                "success=" + success +
                ", modelInfo='" + modelInfo + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
