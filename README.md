# AIExpertDecision
AI专家决策
## 当前进度
打通Spring AI调用单个大模型功能。
## 使用方式
### server
- 服务端Spring+Java工程
- 使用时请在application.yml同目录中创建application-local.yml文件，并在application-local.yml文件中写入如下内容：
```yaml
spring:
  ai:
    openai:
      # api-key: YOUR_ACTUAL_DEEPSEEK_API_KEY # 请将 YOUR_ACTUAL_DEEPSEEK_API_KEY 替换为你的真实的硅基流动的API Key，或者在此文件中添加其它大模型api信息以覆盖application.yml中的相关配置。
```
### web
- web端Vue3+ElementPlus工程
