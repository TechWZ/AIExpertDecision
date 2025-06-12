#!/bin/bash

# 更新 JAR 文件脚本
echo "=== 更新 AI Expert Decision System JAR 文件 ==="

JAR_FILE="aiexpertdecision-0.0.1-SNAPSHOT-java21.jar"
TARGET_DIR="/opt/aiexpert/bin"

# 检查是否以 root 身份运行
if [ "$EUID" -ne 0 ]; then
    echo "请以 root 身份运行此脚本: sudo ./update-jar.sh"
    exit 1
fi

# 检查新的 JAR 文件是否存在
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 找不到新的 JAR 文件: $JAR_FILE"
    echo "请确保您在包含 JAR 文件的目录中运行此脚本"
    exit 1
fi

echo "1. 停止服务..."
systemctl stop aiexpert

echo "2. 备份旧的 JAR 文件..."
if [ -f "$TARGET_DIR/$JAR_FILE" ]; then
    cp "$TARGET_DIR/$JAR_FILE" "$TARGET_DIR/${JAR_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
    echo "   备份文件: $TARGET_DIR/${JAR_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
fi

echo "3. 复制新的 JAR 文件..."
cp "$JAR_FILE" "$TARGET_DIR/"
chown aiexpert:aiexpert "$TARGET_DIR/$JAR_FILE"
chmod +x "$TARGET_DIR/$JAR_FILE"

echo "4. 启动服务..."
systemctl start aiexpert

echo "5. 检查服务状态..."
sleep 3
systemctl status aiexpert --no-pager

echo ""
echo "=== 更新完成 ==="
echo "使用以下命令查看日志:"
echo "sudo journalctl -u aiexpert -f"
