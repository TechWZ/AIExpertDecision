package main.java.institute.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class DoubaoAiService {

    private final ChatClient chatClient;

    // Spring AI 自动配置 ChatClient.Builder，我们可以直接注入它
    // 然后根据需要构建 ChatClient 实例
    public DoubaoAiService(ChatClient.Builder chatClientBuilder) {
        // 这里可以为豆包AI特定的模型或端点进行配置，如果application.yml中没有完全指定
        // 例如:
        // this.chatClient = chatClientBuilder.defaultOptions(VolcanoChatOptions.builder()
        // .withModel("YOUR_DOUBAO_MODEL_IF_DIFFERENT_FROM_DEFAULT")
        // .build()).build();
        // 如果application.yml中的配置足够，可以直接构建
        this.chatClient = chatClientBuilder.build();
    }

    public String chatWithDoubao(String prompt) {
        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            // 建议添加更完善的异常处理
            e.printStackTrace();
            return "Error communicating with Doubao AI: " + e.getMessage();
        }
    }
}
