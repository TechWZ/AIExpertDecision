package institute.controller;

import institute.service.MultiModelAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI控制器，提供多模型AI生成的API
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    private final MultiModelAiService multiModelAiService;

    @Autowired
    public AiController(MultiModelAiService multiModelAiService) {
        this.multiModelAiService = multiModelAiService;
    }

    /**
     * 使用默认模型生成回复
     */
    @GetMapping("/generate")
    public Map<String, Object> generate(@RequestParam(defaultValue = "你好，请介绍一下自己") String prompt) {
        Map<String, Object> result = new HashMap<>();
        try {
            String response = multiModelAiService.generateWithDefault(prompt);
            result.put("success", true);
            result.put("model", "default");
            result.put("prompt", prompt);
            result.put("response", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    /**
     * 使用指定模型生成回复
     * 注意：目前所有模型都会使用默认模型，完整多模型支持需要进一步开发
     */
    @GetMapping("/generate/{modelName}")
    public Map<String, Object> generateWithModel(
            @PathVariable String modelName,
            @RequestParam(defaultValue = "你好，请介绍一下自己") String prompt) {
        Map<String, Object> result = new HashMap<>();
        try {
            String response = multiModelAiService.generateWithModel(modelName, prompt);
            result.put("success", true);
            result.put("model", modelName);
            result.put("prompt", prompt);
            result.put("response", response);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
}