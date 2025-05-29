package institute.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI配置类
 * 
 * 目前仅定义其他模型的OpenAiApi对象，完整的多模型支持需要进一步研究
 * Spring AI 1.0.0的自动配置机制和API设计
 */
@Configuration
public class AiConfig {
    
    // 为DeepSeek模型创建OpenAiApi
    @Bean("deepseekApi")
    public OpenAiApi deepseekApi(
            @Value("${custom.ai.providers.deepseek.base-url}") String baseUrl,
            @Value("${custom.ai.providers.deepseek.api-key}") String apiKey) {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }
    
    // 为THUDM模型创建OpenAiApi
    @Bean("thuDmApi")
    public OpenAiApi thuDmApi(
            @Value("${custom.ai.providers.anotherprovider.base-url}") String baseUrl,
            @Value("${custom.ai.providers.anotherprovider.api-key}") String apiKey) {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }
}
