#!/bin/bash

# 手动测试脚本 - 验证应用是否可以正常启动
echo "=== AI Expert Decision System 手动测试 ==="

APP_JAR="/opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT-java21.jar"
CONFIG_DIR="/opt/aiexpert/config"

# 检查文件是否存在
if [ ! -f "$APP_JAR" ]; then
    echo "错误: JAR 文件不存在: $APP_JAR"
    exit 1
fi

if [ ! -d "$CONFIG_DIR" ]; then
    echo "错误: 配置目录不存在: $CONFIG_DIR"
    exit 1
fi

echo "文件检查通过"
echo "JAR 文件: $APP_JAR"
echo "配置目录: $CONFIG_DIR"
echo ""

# 检查 Java 版本
echo "Java 版本:"
java -version
echo ""

# 检查配置文件
echo "配置文件:"
ls -la $CONFIG_DIR/
echo ""

# 以 aiexpert 用户身份测试启动
echo "以 aiexpert 用户测试启动应用..."
echo "如果出现错误，请检查 API 密钥配置"
echo ""

sudo -u aiexpert java -jar \
    -Xms512m \
    -Xmx1024m \
    -Dspring.profiles.active=prod \
    -Dspring.config.location=file:$CONFIG_DIR/ \
    "$APP_JAR"
