package institute.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.dto.ExpertRolesRequest;
import institute.dto.ExpertRolesResponse;
import institute.dto.ExpertRole;
import institute.dto.AnalysisDecisionRequest;
import institute.dto.AnalysisDecisionResponse;
import institute.service.OpenRouterService;

@Service
public class OpenRouterServiceImpl implements OpenRouterService {
    
    // OpenRouter配置
    @Value("${openrouter.api-key}")
    private String openrouterApiKey;
    
    @Value("${openrouter.base-url}")
    private String openrouterBaseUrl;
    
    @Value("${openrouter.default-model}")
    private String openrouterDefaultModel;
    
    @Value("${openrouter.site-url}")
    private String openrouterSiteUrl;
    
    @Value("${openrouter.site-name}")
    private String openrouterSiteName;

    @Override
    public ExpertRolesResponse getExpertRoles(ExpertRolesRequest request) {
        // 验证输入参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("研究内容不能为空");
        }
        
        // 构建发送给大模型的提示词
        String prompt = buildExpertRolesPrompt(request.getContent());
        
        // 调用OpenRouter API获取响应
        try {
            String aiResponseString = callOpenRouterAPI(prompt, "google/gemini-2.5-pro-preview");
            
            // 解析AI响应获取专家角色列表
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
            
            return new ExpertRolesResponse(true, request.getContent(), expertRoles, "专家角色推荐完成 (基于OpenRouter)");
            
        } catch (Exception e) {
            // 如果解析失败，返回错误响应
            return new ExpertRolesResponse(false, request.getContent(), 
                new ArrayList<>(), "OpenRouter API分析过程中出现错误: " + e.getMessage());
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
        
        // 调用OpenRouter API获取响应
        try {
            String aiResponseString = callOpenRouterAPI(prompt, "google/gemini-2.5-pro-preview");
            
            // 解析AI响应为对象
            Object aiResponse = parseAiResponse(aiResponseString);
            
            // 构建并返回响应对象
            return new ExpertPromptsResponse(true, request.getDecisionRequirement(), 
                                           request.getExpertRoles(), aiResponse);
        } catch (Exception e) {
            return new ExpertPromptsResponse(false, request.getDecisionRequirement(), 
                                           request.getExpertRoles(), "OpenRouter API调用失败: " + e.getMessage());
        }
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
        
        // 调用OpenRouter API获取响应
        try {
            String aiResponseString = callOpenRouterAPI(prompt, "google/gemini-2.5-pro-preview");
            
            // 直接返回AI生成的markdown格式报告，保留原始格式
            return new AnalysisDecisionResponse(true, request.getContent(), new ArrayList<>(), aiResponseString, 100);
        } catch (Exception e) {
            String errorMessage = "OpenRouter API分析决策失败: " + e.getMessage();
            return new AnalysisDecisionResponse(false, request.getContent(), new ArrayList<>(), errorMessage, 0);
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
    
    @Override
    public String callOpenRouterAPI(String prompt, String model) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openrouterApiKey);
            headers.set("HTTP-Referer", openrouterSiteUrl); // 使用配置的网站URL
            headers.set("X-Title", openrouterSiteName); // 使用配置的网站标题
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model != null && !model.trim().isEmpty() ? model : openrouterDefaultModel);
            
            // 构建消息
            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            // 创建HTTP实体
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                openrouterBaseUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // 解析响应
            return parseOpenRouterResponse(response.getBody());
            
        } catch (Exception e) {
            System.err.println("调用OpenRouter API出错: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OpenRouter API调用失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析OpenRouter API的响应
     */
    private String parseOpenRouterResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseBody);
            
            JsonNode choicesNode = rootNode.get("choices");
            if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.get("message");
                if (messageNode != null) {
                    JsonNode contentNode = messageNode.get("content");
                    if (contentNode != null) {
                        return contentNode.asText();
                    }
                }
            }
            
            return "无法解析响应内容";
        } catch (Exception e) {
            System.err.println("解析OpenRouter响应出错: " + e.getMessage());
            return "响应解析错误：" + e.getMessage();
        }
    }
}
