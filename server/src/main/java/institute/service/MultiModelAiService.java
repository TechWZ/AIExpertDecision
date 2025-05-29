package institute.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 多模型AI服务类
 * 注意：目前仅支持默认模型(SiliconFlow Qwen)
 * 扩展其他模型支持需要进一步研究Spring AI的底层实现
 */
@Service
public class MultiModelAiService {

    private final ChatClient defaultChatClient;

    public MultiModelAiService(ChatClient chatClient) {
        this.defaultChatClient = chatClient;
    }
    
    /**
     * 使用默认模型生成回复
     */
    public String generateWithDefault(String prompt) {
        return defaultChatClient
                .prompt(prompt)
                .call()
                .content();
    }
    
    /**
     * 使用默认模型处理多轮对话
     */
    public String generateWithDefaultChat(List<Message> messages) {
        return defaultChatClient
                .prompt(new Prompt(messages))
                .call()
                .content();
    }
    
    /**
     * 通过模型名称选择模型生成回复
     * 注意：目前所有请求都会使用默认模型，
     * 需要进一步研究Spring AI 1.0.0的API以添加其他模型支持
     */
    public String generateWithModel(String modelName, String prompt) {
        // 目前所有模型都使用默认的ChatClient
        // 后续将根据项目的需求和对Spring AI API的研究添加多模型支持
        return generateWithDefault(prompt);
    }
}