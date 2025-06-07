package institute.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import institute.mapper.DirectoriesMapper;
import institute.pojo.Directory;
import institute.dto.ExpertPromptsRequest;
import institute.dto.ExpertPromptsResponse;
import institute.service.AIExpertDecisionService;
import institute.service.DeepSeekService;

@Service
public class AIExpertDecisionServiceImpl implements AIExpertDecisionService {
    // 注入mapper对象
    @Autowired
    private DirectoriesMapper directoriesMapper;
    
    @Autowired
    private DeepSeekService deepSeekService;


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
     * 解析AI响应为对象
     */
    private Object parseAiResponse(String aiResponseString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(aiResponseString, Object.class);
        } catch (Exception e) {
            // 如果解析失败，将原始字符串作为备选方案
            return aiResponseString;
        }
    }
}
