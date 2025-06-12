package institute.service.impl;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.dto.ExpertRolesRequest;
import institute.dto.ExpertRolesResponse;
import institute.dto.ExpertRolesWithPromptsResponse;
import institute.dto.ExpertRole;
import institute.dto.ExpertRoleWithPrompt;
import institute.dto.AnalysisDecisionRequest;
import institute.dto.AnalysisDecisionResponse;
import institute.dto.WhichModelRequest;
import institute.dto.WhichModelResponse;
import institute.service.AIExpertDecisionService;
import institute.service.DeepSeekService;
import institute.service.OpenRouterService;

@Service
public class AIExpertDecisionServiceImpl implements AIExpertDecisionService {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @Autowired
    private OpenRouterService openRouterService;

    @Override
    public ExpertRolesResponse getExpertRoles(ExpertRolesRequest request) {
        // 验证输入参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("研究内容不能为空");
        }
        
        // 构建发送给大模型的提示词
        String prompt = buildExpertRolesPrompt(request.getContent());
        
        // 调用AI服务获取响应
        String aiResponseString = deepSeekService.getCompletion(prompt);
        
        // 解析AI响应获取专家角色列表
        try {
            // 清理AI响应，提取JSON内容
            String cleanedResponse = cleanAiResponse(aiResponseString);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            List<ExpertRole> expertRoles = new ArrayList<>();
            JsonNode rolesNode = jsonNode.get("expertRoles");
            if (rolesNode != null && rolesNode.isArray()) {
                for (JsonNode roleNode : rolesNode) {
                    String roleName = roleNode.get("roleName").asText();
                    Integer matchScore = roleNode.get("matchScore").asInt();
                    expertRoles.add(new ExpertRole(roleName, matchScore));
                }
            }
            
            return new ExpertRolesResponse(true, request.getContent(), expertRoles, "专家角色推荐完成");
            
        } catch (Exception e) {
            // 如果解析失败，返回错误响应
            return new ExpertRolesResponse(false, request.getContent(), 
                new ArrayList<>(), "AI分析过程中出现错误: " + e.getMessage());
        }
    }

    @Override
    public ExpertPromptsResponse generateExpertsPrompts(ExpertPromptsRequest request) {
        // 验证输入参数
        if (request.getExpertRoles() == null || request.getExpertRoles().isEmpty()) {
            throw new IllegalArgumentException("专家角色列表不能为空");
        }
        if (request.getDecisionRequirement() == null || request.getDecisionRequirement().trim().isEmpty()) {
            throw new IllegalArgumentException("决策需求不能为空");
        }
        
        // 构建发送给大模型的提示词
        String prompt = buildPrompt(request);
        
        // 调用AI服务获取响应
        String aiResponseString = deepSeekService.getCompletion(prompt);
        
        // 解析AI响应为对象
        Object aiResponse = parseAiResponse(aiResponseString);
        
        // 构建并返回响应对象
        return new ExpertPromptsResponse(true, request.getDecisionRequirement(), 
                                       request.getExpertRoles(), aiResponse);
    }

    @Override
    public AnalysisDecisionResponse executeAnalysisDecision(AnalysisDecisionRequest request) {
        // 验证输入参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("研究内容不能为空");
        }
        if (request.getExpertPrompts() == null || request.getExpertPrompts().isEmpty()) {
            throw new IllegalArgumentException("专家提示词不能为空");
        }
        
        // 构建发送给大模型的提示词
        String prompt = buildAnalysisDecisionPrompt(request);
        
        // 调用AI服务获取响应
        String aiResponseString = deepSeekService.getCompletion(prompt);
        
        // 直接返回AI生成的markdown格式报告，保留原始格式
        return new AnalysisDecisionResponse(true, request.getContent(), new ArrayList<>(), aiResponseString, 100);
    }

    @Override
    public WhichModelResponse whichModel(WhichModelRequest request) {
        // 构建询问模型信息的提示词
        String prompt = buildWhichModelPrompt(request);
        
        // 使用OpenRouter API直接调用，完全独立于Spring AI
        try {
            String aiResponseString = openRouterService.callOpenRouterAPI(prompt, request.getModel());
            return new WhichModelResponse(true, aiResponseString);
        } catch (Exception e) {
            System.err.println("OpenRouter API调用失败: " + e.getMessage());
            // 返回错误信息，不回退到Spring AI
            String errorMessage = "OpenRouter API调用失败: " + e.getMessage() + 
                                 "\n请检查API密钥配置和网络连接。";
            return new WhichModelResponse(false, errorMessage);
        }
    }
    
    /**
     * 构建获取专家角色的提示词
     */
    private String buildExpertRolesPrompt(String content) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家推荐系统，请根据以下研究内容推荐5个最合适的专家角色。\n\n");
        promptBuilder.append("研究内容：").append(content).append("\n\n");
        promptBuilder.append("请分析这个研究内容，并推荐5个能够从不同专业角度提供有价值分析的专家角色。\n\n");
        promptBuilder.append("要求：\n");
        promptBuilder.append("1. 专家角色应该涵盖该研究领域的核心方面\n");
        promptBuilder.append("2. 每个专家都应该能够从其专业领域提供独特见解\n");
        promptBuilder.append("3. 专家角色应该具有互补性，避免重复\n");
        promptBuilder.append("4. 专家角色名称应该具体明确，体现专业领域\n");
        promptBuilder.append("5. 为每个专家角色提供匹配度评分(0-100分)\n\n");
        promptBuilder.append("请按以下JSON格式返回结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"expertRoles\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色1\",\n");
        promptBuilder.append("      \"matchScore\": 95\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色2\",\n");
        promptBuilder.append("      \"matchScore\": 90\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色3\",\n");
        promptBuilder.append("      \"matchScore\": 85\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色4\",\n");
        promptBuilder.append("      \"matchScore\": 80\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色5\",\n");
        promptBuilder.append("      \"matchScore\": 75\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}");
        return promptBuilder.toString();
    }
    
    /**
     * 构建发送给AI的提示词
     */
    private String buildPrompt(ExpertPromptsRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家决策系统，请根据以下决策需求和专家角色列表，为每个专家角色编写专业的提示词。\n\n");
        promptBuilder.append("决策需求：").append(request.getDecisionRequirement()).append("\n\n");
        promptBuilder.append("专家角色列表：\n");
        for (int i = 0; i < request.getExpertRoles().size(); i++) {
            promptBuilder.append((i + 1)).append(". ").append(request.getExpertRoles().get(i)).append("\n");
        }
        promptBuilder.append("\n请为每个专家角色编写详细的提示词，确保：\n");
        promptBuilder.append("1. 每个提示词都针对该专家的专业领域\n");
        promptBuilder.append("2. 提示词包含角色背景、专业能力描述\n");
        promptBuilder.append("3. 明确该专家在此决策中的分析重点\n");
        promptBuilder.append("4. 提供具体的分析框架或方法论\n\n");
        promptBuilder.append("请按以下JSON格式返回结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"expertPrompts\": {\n");
        promptBuilder.append("    \"专家角色名称1\": \"对应的提示词内容\",\n");
        promptBuilder.append("    \"专家角色名称2\": \"对应的提示词内容\"\n");
        promptBuilder.append("  }\n");
        promptBuilder.append("}");
        return promptBuilder.toString();
    }
    
    /**
     * 构建发送给AI的分析决策提示词
     */
    private String buildAnalysisDecisionPrompt(AnalysisDecisionRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家决策系统，请根据以下研究内容和各专家角色的提示词，生成一份完整的专家分析决策报告。\n\n");
        promptBuilder.append("研究内容：").append(request.getContent()).append("\n\n");
        promptBuilder.append("专家角色及其提示词：\n");
        request.getExpertPrompts().forEach((role, prompt) -> {
            promptBuilder.append("## ").append(role).append("\n");
            promptBuilder.append("**提示词：** ").append(prompt).append("\n\n");
        });
        
        promptBuilder.append("请按照以下要求生成专家分析决策报告：\n");
        promptBuilder.append("1. 为每个专家角色提供详细的分析观点\n");
        promptBuilder.append("2. 每个专家的建议和决策\n");
        promptBuilder.append("3. 各专家观点的置信度评估\n");
        promptBuilder.append("4. 最后提供综合分析结论和整体评分\n\n");
        promptBuilder.append("请使用清晰的markdown格式组织报告，包含标题、子标题、列表等格式元素，使报告易于阅读。");
        
        return promptBuilder.toString();
    }
    
    /**
     * 清理AI响应，提取JSON内容
     */
    private String cleanAiResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return "{}";
        }
        
        // 移除markdown代码块标记
        String cleaned = aiResponse.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        
        // 查找JSON对象的开始和结束
        int jsonStart = cleaned.indexOf('{');
        int jsonEnd = cleaned.lastIndexOf('}');
        
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            return cleaned.substring(jsonStart, jsonEnd + 1).trim();
        }
        
        return cleaned.trim();
    }

    /**
     * 解析AI响应为对象
     */
    private Object parseAiResponse(String aiResponseString) {
        try {
            // 清理AI响应，提取JSON内容
            String cleanedResponse = cleanAiResponse(aiResponseString);
            
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(cleanedResponse, Object.class);
        } catch (Exception e) {
            // 如果解析失败，将原始字符串作为备选方案
            return aiResponseString;
        }
    }
    
    /**
     * 构建询问模型信息的提示词
     */
    private String buildWhichModelPrompt(WhichModelRequest request) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请简洁地告诉我你的具体模型型号和名称，不需要其他详细介绍。\n");
        
        if (request.getAdditionalQuery() != null && !request.getAdditionalQuery().trim().isEmpty()) {
            promptBuilder.append("另外，用户还希望了解：").append(request.getAdditionalQuery()).append("\n\n");
        }
        
        promptBuilder.append("请用简洁明了的方式回答。");
        
        return promptBuilder.toString();
    }

    @Override
    public ExpertRolesWithPromptsResponse getExpertRolesWithPrompts(ExpertRolesRequest request) {
        // 验证输入参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("研究内容不能为空");
        }
        
        // 构建发送给大模型的提示词
        String prompt = buildExpertRolesWithPromptsPrompt(request.getContent());
        
        // 调用AI服务获取响应
        String aiResponseString = deepSeekService.getCompletion(prompt);
        
        // 解析AI响应获取专家角色和提示词列表
        try {
            // 清理AI响应，提取JSON内容
            String cleanedResponse = cleanAiResponse(aiResponseString);
            
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            List<ExpertRoleWithPrompt> expertRolesWithPrompts = new ArrayList<>();
            
            JsonNode expertsNode = jsonNode.get("experts");
            if (expertsNode != null && expertsNode.isArray()) {
                for (JsonNode expertNode : expertsNode) {
                    String roleName = expertNode.get("roleName").asText();
                    Integer matchScore = expertNode.get("matchScore").asInt();
                    String promptText = expertNode.get("prompt").asText();
                    
                    expertRolesWithPrompts.add(new ExpertRoleWithPrompt(roleName, matchScore, promptText));
                }
            }
            
            return new ExpertRolesWithPromptsResponse(true, request.getContent(), 
                                                    expertRolesWithPrompts, "专家角色和提示词推荐完成");
            
        } catch (Exception e) {
            // 如果解析失败，返回错误响应
            return new ExpertRolesWithPromptsResponse(false, request.getContent(), 
                                                    new ArrayList<>(), "AI分析过程中出现错误: " + e.getMessage());
        }
    }

    /**
     * 构建获取专家角色和提示词的提示词
     */
    private String buildExpertRolesWithPromptsPrompt(String content) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家推荐系统，请根据以下研究内容推荐5个最合适的专家角色，并为每个专家角色编写详细的提示词。\n\n");
        promptBuilder.append("研究内容：").append(content).append("\n\n");
        promptBuilder.append("请分析这个研究内容，并推荐5个能够从不同专业角度提供有价值分析的专家角色，同时为每个专家编写相应的提示词。\n\n");
        promptBuilder.append("要求：\n");
        promptBuilder.append("1. 专家角色应该涵盖该研究领域的核心方面\n");
        promptBuilder.append("2. 每个专家都应该能够从其专业领域提供独特见解\n");
        promptBuilder.append("3. 专家角色应该具有互补性，避免重复\n");
        promptBuilder.append("4. 专家角色名称应该具体明确，体现专业领域\n");
        promptBuilder.append("5. 为每个专家角色提供匹配度评分(0-100分)\n");
        promptBuilder.append("6. 为每个专家编写详细的提示词，包含角色背景、专业能力描述、分析重点和方法论\n\n");
        promptBuilder.append("请按以下JSON格式返回结果：\n");
        promptBuilder.append("{\n");
        promptBuilder.append("  \"experts\": [\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色名称\",\n");
        promptBuilder.append("      \"matchScore\": 95,\n");
        promptBuilder.append("      \"prompt\": \"详细的专家提示词内容，包括角色背景、专业能力、分析重点等\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色名称2\",\n");
        promptBuilder.append("      \"matchScore\": 90,\n");
        promptBuilder.append("      \"prompt\": \"详细的专家提示词内容2\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色名称3\",\n");
        promptBuilder.append("      \"matchScore\": 85,\n");
        promptBuilder.append("      \"prompt\": \"详细的专家提示词内容3\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色名称4\",\n");
        promptBuilder.append("      \"matchScore\": 80,\n");
        promptBuilder.append("      \"prompt\": \"详细的专家提示词内容4\"\n");
        promptBuilder.append("    },\n");
        promptBuilder.append("    {\n");
        promptBuilder.append("      \"roleName\": \"专家角色名称5\",\n");
        promptBuilder.append("      \"matchScore\": 75,\n");
        promptBuilder.append("      \"prompt\": \"详细的专家提示词内容5\"\n");
        promptBuilder.append("    }\n");
        promptBuilder.append("  ]\n");
        promptBuilder.append("}");
        return promptBuilder.toString();
    }
}
