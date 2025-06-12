#!/bin/bash

# 快速启动脚本（开发/测试用）
# 使用方法: ./quick-start.sh

JAR_FILE="aiexpertdecision-0.0.1-SNAPSHOT-java21.jar"
CONFIG_DIR="."

echo "=== AI Expert Decision System 快速启动 ==="

# 检查 JAR 文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 找不到 JAR 文件: $JAR_FILE"
    echo "请确保您在正确的目录中运行此脚本"
    exit 1
fi

# 检查 Java 环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java 运行环境"
    echo "请安装 Java 21 或更高版本"
    exit 1
fi

echo "Java 版本:"
java -version

echo ""
echo "启动应用..."
echo "配置文件目录: $CONFIG_DIR"
echo "JAR 文件: $JAR_FILE"
echo ""

# 启动应用
java -jar \
    -Xms512m \
    -Xmx1024m \
    -Dspring.profiles.active=prod \
    -Dspring.config.location=file:$CONFIG_DIR/ \
    "$JAR_FILE"
