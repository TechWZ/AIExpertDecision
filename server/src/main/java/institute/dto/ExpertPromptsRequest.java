package institute.dto;

import java.util.List;

public class ExpertPromptsRequest {
    private List<String> expertRoles;
    private String decisionRequirement;

    public ExpertPromptsRequest() {
    }

    public ExpertPromptsRequest(List<String> expertRoles, String decisionRequirement) {
        this.expertRoles = expertRoles;
        this.decisionRequirement = decisionRequirement;
    }

    public List<String> getExpertRoles() {
        return expertRoles;
    }

    public void setExpertRoles(List<String> expertRoles) {
        this.expertRoles = expertRoles;
    }

    public String getDecisionRequirement() {
        return decisionRequirement;
    }

    public void setDecisionRequirement(String decisionRequirement) {
        this.decisionRequirement = decisionRequirement;
    }
}
