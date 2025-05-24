package institute.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

@Service
public class DeepSeekAiService { 

    private final ChatClient chatClient;

    public DeepSeekAiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        String response = chatClient.prompt("Tell me a joke").call().content();							
        System.out.println(response);
    }

    public String getCompletion(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    public String getChatCompletion(List<Message> messages) {
        return chatClient.prompt(new Prompt(messages))
                .call()
                .content();
    }
}
