package institute.dto;

import java.time.LocalDateTime;

public class ExpertPromptsResponse {
    private boolean success;
    private String decisionRequirement;
    private Object aiResponse;
    private LocalDateTime timestamp;

    public ExpertPromptsResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ExpertPromptsResponse(boolean success, String decisionRequirement, Object aiResponse) {
        this.success = success;
        this.decisionRequirement = decisionRequirement;
        this.aiResponse = aiResponse;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDecisionRequirement() {
        return decisionRequirement;
    }

    public void setDecisionRequirement(String decisionRequirement) {
        this.decisionRequirement = decisionRequirement;
    }

    public Object getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(Object aiResponse) {
        this.aiResponse = aiResponse;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
