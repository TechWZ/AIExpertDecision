#!/bin/bash

# 诊断和修复脚本
echo "=== AI Expert Decision System 诊断和修复脚本 ==="

# 1. 检查 Java 环境
echo "1. 检查 Java 环境..."
if command -v java &> /dev/null; then
    echo "✓ Java 已安装"
    java -version
    JAVA_PATH=$(which java)
    echo "Java 路径: $JAVA_PATH"
else
    echo "✗ Java 未安装或不在 PATH 中"
    echo "请安装 Java: sudo apt install openjdk-21-jdk"
    exit 1
fi

# 2. 查找正确的 JAVA_HOME
echo ""
echo "2. 查找 JAVA_HOME..."
if [ -n "$JAVA_HOME" ]; then
    echo "当前 JAVA_HOME: $JAVA_HOME"
else
    # 尝试找到 JAVA_HOME
    POSSIBLE_JAVA_HOMES=(
        "/usr/lib/jvm/java-21-openjdk-amd64"
        "/usr/lib/jvm/java-17-openjdk-amd64"
        "/usr/lib/jvm/java-11-openjdk-amd64"
        "/usr/lib/jvm/default-java"
    )
    
    for jh in "${POSSIBLE_JAVA_HOMES[@]}"; do
        if [ -d "$jh" ]; then
            JAVA_HOME="$jh"
            echo "找到 JAVA_HOME: $JAVA_HOME"
            break
        fi
    done
fi

# 3. 检查应用文件
echo ""
echo "3. 检查应用文件..."
APP_JAR="/opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar"
if [ -f "$APP_JAR" ]; then
    echo "✓ JAR 文件存在: $APP_JAR"
    ls -la "$APP_JAR"
else
    echo "✗ JAR 文件不存在: $APP_JAR"
    exit 1
fi

# 4. 检查用户和权限
echo ""
echo "4. 检查用户和权限..."
if id "aiexpert" &>/dev/null; then
    echo "✓ 用户 aiexpert 存在"
else
    echo "✗ 用户 aiexpert 不存在"
    echo "创建用户: sudo useradd -r -m -U -d /opt/aiexpert -s /bin/bash aiexpert"
    exit 1
fi

# 检查文件权限
echo "文件权限:"
ls -la /opt/aiexpert/
ls -la /opt/aiexpert/bin/

# 5. 测试 Java 执行
echo ""
echo "5. 测试 Java 执行..."
sudo -u aiexpert $JAVA_PATH -version
if [ $? -eq 0 ]; then
    echo "✓ aiexpert 用户可以执行 Java"
else
    echo "✗ aiexpert 用户无法执行 Java"
fi

# 6. 创建修复后的 systemd 服务文件
echo ""
echo "6. 创建修复后的 systemd 服务文件..."

cat > /tmp/aiexpert.service << EOF
[Unit]
Description=AI Expert Decision System
After=network.target

[Service]
Type=simple
User=aiexpert
Group=aiexpert
WorkingDirectory=/opt/aiexpert
ExecStart=$JAVA_PATH -jar -Xms512m -Xmx1024m -Dspring.profiles.active=prod -Dspring.config.location=file:/opt/aiexpert/config/ /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar
ExecStop=/bin/kill -TERM \$MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=aiexpert

Environment=JAVA_HOME=$JAVA_HOME

[Install]
WantedBy=multi-user.target
EOF

echo "新的服务文件内容:"
cat /tmp/aiexpert.service

# 7. 应用修复
echo ""
echo "7. 应用修复..."
if [ "$EUID" -eq 0 ]; then
    # 停止现有服务
    systemctl stop aiexpert 2>/dev/null || true
    
    # 替换服务文件
    cp /tmp/aiexpert.service /etc/systemd/system/aiexpert.service
    
    # 重新加载 systemd
    systemctl daemon-reload
    
    # 设置正确的权限
    chown -R aiexpert:aiexpert /opt/aiexpert
    chmod +x /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar
    
    echo "✓ 修复完成"
    echo ""
    echo "现在可以启动服务:"
    echo "sudo systemctl start aiexpert"
    echo "sudo systemctl status aiexpert"
else
    echo "请以 root 身份运行此脚本进行修复: sudo ./diagnose.sh"
fi
