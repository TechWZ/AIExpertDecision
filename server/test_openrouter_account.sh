#!/bin/bash

# 测试 OpenRouter 账户信息和可用模型
API_KEY="YOUR_NEW_OPENROUTER_API_KEY_HERE"

echo "=== 检查 OpenRouter 账户和模型 ==="
echo ""

# 检查可用模型
echo "--- 1. 检查可用模型 ---"
curl -X GET "https://openrouter.ai/api/v1/models" \
  -H "Authorization: Bearer $API_KEY" \
  -w "\n状态码: %{http_code}\n" \
  -s | head -20

echo ""

# 检查账户余额（如果API支持）
echo "--- 2. 检查账户信息 ---"
curl -X GET "https://openrouter.ai/api/v1/auth/key" \
  -H "Authorization: Bearer $API_KEY" \
  -w "\n状态码: %{http_code}\n" \
  -s

echo ""

# 尝试更简单的免费模型
echo "--- 3. 尝试免费模型 ---"
curl -X POST "https://openrouter.ai/api/v1/chat/completions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -d '{
    "model": "microsoft/phi-3-mini-128k-instruct:free",
    "messages": [
      {
        "role": "user",
        "content": "Hello"
      }
    ]
  }' \
  -w "\n状态码: %{http_code}\n" \
  -s

echo ""
echo "=== 检查完成 ==="
