# AIExpertDecision
AI专家决策
## 当前进度
- 主线功能6月17日已完成
- 未完成
  - bug未进行解决，刚要开始陆续解决
  - 边缘功能
## 使用方式
### server
- 服务端Spring+Java工程
- 使用时请在application.yml同目录中创建application-local.yml文件，并在application-local.yml文件中写入如下内容：
```yaml
spring:
  ai:
    openai:
      api-key: [github出现api key的话，该api key会被封禁]
openrouter:
    api-key: [github出现api key的话，该api key会被封禁]
```
### web
- web端Vue3+ElementPlus工程
