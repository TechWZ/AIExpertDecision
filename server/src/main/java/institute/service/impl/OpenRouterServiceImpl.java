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
import institute.dto.ExpertRolesWithPromptsResponse;
import institute.dto.ExpertRole;
import institute.dto.ExpertRoleWithPrompt;
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
        String prompt = buildExpertRolesPrompt(request.getContent(), request.getAnalysisAngles());
        
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
                                           aiResponse);
        } catch (Exception e) {
            return new ExpertPromptsResponse(false, request.getDecisionRequirement(), 
                                           "OpenRouter API调用失败: " + e.getMessage());
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
    private String buildExpertRolesPrompt(String content, List<String> analysisAngles) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家推荐系统，请根据以下研究内容推荐5个最合适的专家角色。\n\n");
        promptBuilder.append("研究内容：").append(content).append("\n\n");
        
        // 如果提供了分析角度，将其加入提示词
        if (analysisAngles != null && !analysisAngles.isEmpty()) {
            promptBuilder.append("分析角度：\n");
            for (int i = 0; i < analysisAngles.size(); i++) {
                promptBuilder.append((i + 1)).append(". ").append(analysisAngles.get(i)).append("\n");
            }
            promptBuilder.append("\n");
            promptBuilder.append("请综合考虑研究内容和指定的分析角度，推荐5个能够从不同专业角度提供有价值分析的专家角色。\n\n");
        } else {
            promptBuilder.append("请分析这个研究内容，并推荐5个能够从不同专业角度提供有价值分析的专家角色。\n\n");
        }
        
        promptBuilder.append("要求：\n");
        promptBuilder.append("1. 专家角色应该涵盖该研究领域的核心方面\n");
        promptBuilder.append("2. 每个专家都应该能够从其专业领域提供独特见解\n");
        promptBuilder.append("3. 专家角色应该具有互补性，避免重复\n");
        promptBuilder.append("4. 专家角色名称应该具体明确，体现专业领域\n");
        
        if (analysisAngles != null && !analysisAngles.isEmpty()) {
            promptBuilder.append("5. 专家角色的选择应该与指定的分析角度相关联\n");
            promptBuilder.append("6. 为每个专家角色提供匹配度评分(0-100分)\n\n");
        } else {
            promptBuilder.append("5. 为每个专家角色提供匹配度评分(0-100分)\n\n");
        }
        
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
        promptBuilder.append("作为AI专家决策系统，请根据以下决策需求和专家角色（包含每个专家的分析角度），为每个专家角色编写专业的提示词。\n\n");
        promptBuilder.append("决策需求：").append(request.getDecisionRequirement()).append("\n\n");
        
        promptBuilder.append("专家角色及其分析角度：\n");
        int i = 1;
        for (Map.Entry<String, List<String>> entry : request.getExpertRoles().entrySet()) {
            String roleName = entry.getKey();
            List<String> analysisAngles = entry.getValue();
            
            promptBuilder.append(i++).append(". ").append(roleName).append("\n");
            promptBuilder.append("   分析角度：");
            if (analysisAngles != null && !analysisAngles.isEmpty()) {
                for (int j = 0; j < analysisAngles.size(); j++) {
                    if (j > 0) promptBuilder.append(", ");
                    promptBuilder.append(analysisAngles.get(j));
                }
            } else {
                promptBuilder.append("通用分析");
            }
            promptBuilder.append("\n");
        }
        
        promptBuilder.append("\n请为每个专家角色编写详细的提示词，确保：\n");
        promptBuilder.append("1. 每个提示词都针对该专家的专业领域\n");
        promptBuilder.append("2. 提示词包含角色背景、专业能力描述\n");
        promptBuilder.append("3. 明确该专家在此决策中的分析重点\n");
        promptBuilder.append("4. 提供具体的分析框架或方法论\n");
        promptBuilder.append("5. 特别结合该专家对应的分析角度来构建提示词，确保专家能从指定角度进行深入分析\n");
        
        promptBuilder.append("\n请按以下JSON格式返回结果：\n");
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
        
        // 添加AI专家角色及其提示词
        promptBuilder.append("AI专家角色及其提示词：\n");
        request.getExpertPrompts().forEach((role, prompt) -> {
            promptBuilder.append("## ").append(role).append("\n");
            promptBuilder.append("**提示词：** ").append(prompt).append("\n\n");
        });
        
        // 添加现实专家决策（如果存在）
        if (request.getRealExpertDecisions() != null && !request.getRealExpertDecisions().isEmpty()) {
            promptBuilder.append("现实专家决策：\n");
            request.getRealExpertDecisions().forEach((expertName, decision) -> {
                promptBuilder.append("## ").append(expertName).append("\n");
                promptBuilder.append("**决策结论：** ").append(decision).append("\n\n");
            });
        }
        
        promptBuilder.append("请按照以下要求生成专家分析决策报告：\n");
        promptBuilder.append("1. 为每个AI专家角色提供详细的分析观点\n");
        promptBuilder.append("2. 每个AI专家的建议和决策\n");
        promptBuilder.append("3. 各AI专家观点的置信度评估\n");
        if (request.getRealExpertDecisions() != null && !request.getRealExpertDecisions().isEmpty()) {
            promptBuilder.append("4. 原封不动地包含所有现实专家的决策结论\n");
            promptBuilder.append("5. 在综合分析结论中，充分考虑现实专家的决策意见，结合AI专家的分析，提供最终的综合评估和建议\n");
        } else {
            promptBuilder.append("4. 最后提供综合分析结论和整体评分\n");
        }
        promptBuilder.append("\n请使用清晰的markdown格式组织报告，包含标题、子标题、列表等格式元素，使报告易于阅读。");
        
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

    @Override
    public ExpertRolesWithPromptsResponse getExpertRolesWithPrompts(ExpertRolesRequest request) {
        // 验证输入参数
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("研究内容不能为空");
        }
        
        // 构建发送给大模型的提示词，获取专家角色和提示词
        String prompt = buildExpertRolesWithPromptsPrompt(request.getContent(), request.getAnalysisAngles());
        
        // 调用OpenRouter API获取响应
        try {
            String aiResponseString = callOpenRouterAPI(prompt, "google/gemini-2.5-pro-preview");
            
            // 解析AI响应获取专家角色和提示词列表
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
                                                    expertRolesWithPrompts, "专家角色和提示词推荐完成 (基于OpenRouter)");
            
        } catch (Exception e) {
            // 如果解析失败，返回错误响应
            return new ExpertRolesWithPromptsResponse(false, request.getContent(), 
                                                    new ArrayList<>(), "OpenRouter API分析过程中出现错误: " + e.getMessage());
        }
    }

    /**
     * 构建获取专家角色和提示词的提示词
     */
    private String buildExpertRolesWithPromptsPrompt(String content, List<String> analysisAngles) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("作为AI专家推荐系统，请根据以下研究内容推荐5个最合适的专家角色，并为每个专家角色编写详细的提示词。\n\n");
        promptBuilder.append("研究内容：").append(content).append("\n\n");
        
        // 如果提供了分析角度，将其加入提示词
        if (analysisAngles != null && !analysisAngles.isEmpty()) {
            promptBuilder.append("分析角度：\n");
            for (int i = 0; i < analysisAngles.size(); i++) {
                promptBuilder.append((i + 1)).append(". ").append(analysisAngles.get(i)).append("\n");
            }
            promptBuilder.append("\n");
            promptBuilder.append("请综合考虑研究内容和指定的分析角度，推荐5个能够从不同专业角度提供有价值分析的专家角色，同时为每个专家编写相应的提示词。\n\n");
        } else {
            promptBuilder.append("请分析这个研究内容，并推荐5个能够从不同专业角度提供有价值分析的专家角色，同时为每个专家编写相应的提示词。\n\n");
        }
        
        promptBuilder.append("要求：\n");
        promptBuilder.append("1. 专家角色应该涵盖该研究领域的核心方面\n");
        promptBuilder.append("2. 每个专家都应该能够从其专业领域提供独特见解\n");
        promptBuilder.append("3. 专家角色应该具有互补性，避免重复\n");
        promptBuilder.append("4. 专家角色名称应该具体明确，体现专业领域\n");
        
        if (analysisAngles != null && !analysisAngles.isEmpty()) {
            promptBuilder.append("5. 专家角色的选择应该与指定的分析角度相关联\n");
            promptBuilder.append("6. 为每个专家角色提供匹配度评分(0-100分)\n");
            promptBuilder.append("7. 为每个专家编写详细的提示词，包含角色背景、专业能力描述、分析重点和方法论，特别要结合指定的分析角度\n\n");
        } else {
            promptBuilder.append("5. 为每个专家角色提供匹配度评分(0-100分)\n");
            promptBuilder.append("6. 为每个专家编写详细的提示词，包含角色背景、专业能力描述、分析重点和方法论\n\n");
        }
        
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

    /**
     * 获取模拟响应（当API密钥无效时使用）
     */
    private String getMockResponse(String prompt, String model) {
        System.out.println("使用模拟响应模式 - API密钥无效");
        
        // 根据提示词内容判断返回类型
        if (prompt.contains("推荐") && prompt.contains("专家角色")) {
            // 专家角色推荐的模拟响应
            if (prompt.contains("提示词")) {
                return "```json\n{\n  \"experts\": [\n    {\n      \"roleName\": \"技术架构专家\",\n      \"matchScore\": 95,\n      \"prompt\": \"作为技术架构专家，请从系统设计、技术选型、性能优化等角度分析该项目的技术可行性和实施方案。\"\n    },\n    {\n      \"roleName\": \"项目管理专家\",\n      \"matchScore\": 90,\n      \"prompt\": \"作为项目管理专家，请从项目规划、资源配置、风险管控等角度评估项目的可执行性和管理策略。\"\n    },\n    {\n      \"roleName\": \"用户体验专家\",\n      \"matchScore\": 85,\n      \"prompt\": \"作为用户体验专家，请从用户需求、交互设计、可用性测试等角度分析产品的用户接受度和体验优化方向。\"\n    },\n    {\n      \"roleName\": \"市场分析专家\",\n      \"matchScore\": 80,\n      \"prompt\": \"作为市场分析专家，请从市场需求、竞争分析、商业模式等角度评估项目的市场前景和商业价值。\"\n    },\n    {\n      \"roleName\": \"风险评估专家\",\n      \"matchScore\": 75,\n      \"prompt\": \"作为风险评估专家，请从技术风险、市场风险、运营风险等角度识别和评估项目可能面临的各类风险。\"\n    }\n  ]\n}\n```";
            } else {
                return "```json\n{\n  \"expertRoles\": [\n    {\n      \"roleName\": \"技术架构专家\",\n      \"matchScore\": 95\n    },\n    {\n      \"roleName\": \"项目管理专家\",\n      \"matchScore\": 90\n    },\n    {\n      \"roleName\": \"用户体验专家\",\n      \"matchScore\": 85\n    },\n    {\n      \"roleName\": \"市场分析专家\",\n      \"matchScore\": 80\n    },\n    {\n      \"roleName\": \"风险评估专家\",\n      \"matchScore\": 75\n    }\n  ]\n}\n```";
            }
        } else if (prompt.contains("专家分析决策报告") || prompt.contains("现实专家决策")) {
            // 专家分析决策报告的模拟响应
            StringBuilder mockReport = new StringBuilder();
            mockReport.append("# AI专家决策分析报告\n\n");
            
            mockReport.append("## AI专家分析\n\n");
            mockReport.append("### 技术架构专家分析\n\n");
            mockReport.append("**分析观点**：从技术实现角度来看，该项目具有较高的技术可行性。建议采用现代化的技术栈，确保系统的可扩展性和可维护性。\n\n");
            mockReport.append("**决策建议**：\n");
            mockReport.append("1. 采用微服务架构设计\n");
            mockReport.append("2. 使用容器化部署\n");
            mockReport.append("3. 实施持续集成/持续部署（CI/CD）\n");
            mockReport.append("4. 建立完善的监控和日志系统\n\n");
            mockReport.append("**置信度**：90%\n\n");
            
            mockReport.append("### 项目管理专家分析\n\n");
            mockReport.append("**分析观点**：项目具备清晰的目标和可行的实施路径，但需要合理的资源配置和风险管控措施。\n\n");
            mockReport.append("**决策建议**：\n");
            mockReport.append("1. 制定详细的项目计划和里程碑\n");
            mockReport.append("2. 建立跨部门协作机制\n");
            mockReport.append("3. 实施敏捷开发方法\n");
            mockReport.append("4. 定期进行项目评审和调整\n\n");
            mockReport.append("**置信度**：85%\n\n");
            
            // 如果提示词中包含现实专家，则添加现实专家部分
            if (prompt.contains("现实专家决策")) {
                mockReport.append("## 现实专家决策\n\n");
                mockReport.append("### 张教授\n");
                mockReport.append("**决策结论**：基于多年临床经验，建议采用A方案，该方案在实际应用中具有更好的可操作性和安全性。\n\n");
                mockReport.append("### 李主任\n");
                mockReport.append("**决策结论**：从管理角度考虑，推荐B方案，可以更好地整合现有资源，降低实施风险。\n\n");
            }
            
            mockReport.append("## 综合分析结论\n\n");
            if (prompt.contains("现实专家决策")) {
                mockReport.append("基于AI专家的深度分析和现实专家的实践经验，该项目整体可行性较高。现实专家的意见为技术实施提供了重要的实践指导，特别是在方案选择和风险控制方面具有重要价值。\n\n");
                mockReport.append("**综合建议**：\n");
                mockReport.append("1. 优先采纳现实专家推荐的A方案作为核心技术路径\n");
                mockReport.append("2. 结合AI专家的技术架构建议，完善系统设计\n");
                mockReport.append("3. 参考项目管理专家的流程建议，确保项目顺利实施\n");
                mockReport.append("4. 持续关注现实专家的反馈，及时调整实施策略\n\n");
            } else {
                mockReport.append("基于各AI专家的分析意见，该项目整体可行性较高，建议在充分准备的基础上推进实施。需要重点关注技术实现和项目管理两个方面，确保项目按时交付并达到预期效果。\n\n");
                mockReport.append("**关键建议**：\n");
                mockReport.append("1. 优先完成技术架构设计\n");
                mockReport.append("2. 建立有效的项目管理机制\n");
                mockReport.append("3. 持续关注用户反馈和市场变化\n");
                mockReport.append("4. 制定完善的风险应对策略\n\n");
            }
            
            mockReport.append("**整体评分**：").append(prompt.contains("现实专家决策") ? "92" : "85").append("/100");
            
            return mockReport.toString();
        } else {
            // 生成专家提示词的模拟响应
            return "根据您提供的研究内容和专家角色，我为每个专家生成了相应的分析提示词。这些提示词将指导专家从各自的专业角度进行深入分析，为您的决策提供全面的专业意见。每个专家将基于其专业背景和分析角度，提供具体的建议和评估。";
        }
    }
}
