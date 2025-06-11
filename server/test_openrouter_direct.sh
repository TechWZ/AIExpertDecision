#!/bin/bash

# OpenRouter API 测试脚本
API_KEY="YOUR_NEW_OPENROUTER_API_KEY_HERE"
BASE_URL="https://openrouter.ai/api/v1/chat/completions"

echo "=== 测试 OpenRouter API ==="
echo "API Key (前10字符): ${API_KEY:0:10}..."
echo "Base URL: $BASE_URL"
echo ""

# 测试1: 基本请求
echo "--- 测试1: 基本请求 ---"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -d '{
    "model": "google/gemini-2.5-pro-preview",
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

# 测试2: 带完整头部的请求
echo "--- 测试2: 带完整头部的请求 ---"
curl -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $API_KEY" \
  -H "HTTP-Referer: http://localhost:5002" \
  -H "X-Title: AI Expert Decision System" \
  -d '{
    "model": "google/gemini-2.5-pro-preview",
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

# 测试3: 验证API密钥格式
echo "--- 测试3: API密钥信息 ---"
echo "API密钥长度: ${#API_KEY}"
echo "API密钥是否以sk-or-v1-开头: $(echo $API_KEY | grep -q '^sk-or-v1-' && echo '是' || echo '否')"

echo ""
echo "=== 测试完成 ==="
