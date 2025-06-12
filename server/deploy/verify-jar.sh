#!/bin/bash

# 验证 JAR 文件的 Java 版本脚本
echo "=== 验证 JAR 文件 Java 版本 ==="

JAR_FILE="aiexpertdecision-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 找不到 JAR 文件: $JAR_FILE"
    exit 1
fi

echo "检查的文件: $JAR_FILE"
echo "文件大小: $(ls -lh $JAR_FILE | awk '{print $5}')"
echo "修改时间: $(ls -l $JAR_FILE | awk '{print $6, $7, $8}')"
echo ""

# 创建临时目录
TEMP_DIR=$(mktemp -d)
echo "临时目录: $TEMP_DIR"

# 提取 BaseApplication.class
echo "提取 BaseApplication.class..."
unzip -q "$JAR_FILE" BOOT-INF/classes/institute/BaseApplication.class -d "$TEMP_DIR"

if [ ! -f "$TEMP_DIR/BOOT-INF/classes/institute/BaseApplication.class" ]; then
    echo "错误: 无法从 JAR 中提取 BaseApplication.class"
    rm -rf "$TEMP_DIR"
    exit 1
fi

# 检查类文件版本
echo "检查类文件版本..."
CLASS_VERSION=$(xxd "$TEMP_DIR/BOOT-INF/classes/institute/BaseApplication.class" | head -1 | cut -d' ' -f7)
echo "类文件版本字节: $CLASS_VERSION"

# 转换为十进制
if [ "$CLASS_VERSION" = "0041" ]; then
    echo "✓ 类文件版本: 65 (Java 21) - 兼容"
elif [ "$CLASS_VERSION" = "0043" ]; then
    echo "✗ 类文件版本: 67 (Java 23) - 不兼容，需要重新编译"
else
    echo "? 未知的类文件版本: $CLASS_VERSION"
fi

# 清理
rm -rf "$TEMP_DIR"

echo ""
echo "如果显示 '✓ 兼容'，则可以在 Java 21 服务器上运行"
echo "如果显示 '✗ 不兼容'，请重新编译项目"
