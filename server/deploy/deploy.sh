#!/bin/bash

# AI Expert Decision System 部署脚本
# 适用于 Ubuntu 服务器

set -e

APP_NAME="aiexpertdecision"
APP_VERSION="0.0.1-SNAPSHOT-java21"
JAR_FILE="${APP_NAME}-${APP_VERSION}.jar"
SERVICE_NAME="aiexpert"
APP_USER="aiexpert"
APP_HOME="/opt/aiexpert"
LOG_DIR="/var/log/aiexpert"

echo "=== AI Expert Decision System 部署脚本 ==="

# 检查是否以 root 身份运行
if [ "$EUID" -ne 0 ]; then
    echo "请以 root 身份运行此脚本: sudo ./deploy.sh"
    exit 1
fi

# 创建应用用户（如果不存在）
if ! id "$APP_USER" &>/dev/null; then
    echo "创建应用用户: $APP_USER"
    useradd -r -m -U -d "$APP_HOME" -s /bin/bash "$APP_USER"
else
    echo "用户 $APP_USER 已存在"
fi

# 创建目录
echo "创建应用目录..."
mkdir -p "$APP_HOME"/{bin,config,logs}
mkdir -p "$LOG_DIR"

# 复制 JAR 文件
echo "复制应用文件..."
cp "$JAR_FILE" "$APP_HOME/bin/"

# 复制配置文件
cp application.yml "$APP_HOME/config/"
cp application-prod.yml "$APP_HOME/config/"
# 注意：application-local.yml 包含敏感信息，需要手动创建

# 创建生产环境配置文件模板
cat > "$APP_HOME/config/application-prod.yml" << 'EOF'
spring:
  ai:
    openai:
      # DeepSeek API key - 请在此处配置您的密钥
      api-key: YOUR_DEEPSEEK_API_KEY_HERE

# OpenRouter配置（生产环境）
openrouter:
  # 请在此处配置您的 OpenRouter API 密钥
  api-key: YOUR_OPENROUTER_API_KEY_HERE

# 生产环境日志配置
logging:
  level:
    root: INFO
    institute: INFO
  file:
    name: /var/log/aiexpert/application.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
      total-size-cap: 1GB
EOF

# 设置权限
echo "设置文件权限..."
chown -R "$APP_USER:$APP_USER" "$APP_HOME"
chown -R "$APP_USER:$APP_USER" "$LOG_DIR"
chmod +x "$APP_HOME/bin/$JAR_FILE"

# 创建 systemd 服务文件
echo "创建 systemd 服务..."

# 检测正确的 Java 路径
JAVA_PATH=$(which java)
if [ -z "$JAVA_PATH" ]; then
    echo "错误: 找不到 Java 执行文件"
    exit 1
fi

# 检测 JAVA_HOME
JAVA_HOME_PATH=""
POSSIBLE_JAVA_HOMES=(
    "/usr/lib/jvm/java-21-openjdk-amd64"
    "/usr/lib/jvm/java-17-openjdk-amd64"
    "/usr/lib/jvm/java-11-openjdk-amd64"
    "/usr/lib/jvm/default-java"
)

for jh in "${POSSIBLE_JAVA_HOMES[@]}"; do
    if [ -d "$jh" ]; then
        JAVA_HOME_PATH="$jh"
        break
    fi
done

echo "使用 Java 路径: $JAVA_PATH"
echo "使用 JAVA_HOME: $JAVA_HOME_PATH"

cat > "/etc/systemd/system/${SERVICE_NAME}.service" << EOF
[Unit]
Description=AI Expert Decision System
After=network.target

[Service]
Type=simple
User=$APP_USER
Group=$APP_USER
WorkingDirectory=$APP_HOME
ExecStart=$JAVA_PATH -jar -Xms512m -Xmx1024m -Dspring.profiles.active=prod -Dspring.config.location=file:$APP_HOME/config/ $APP_HOME/bin/$JAR_FILE
ExecStop=/bin/kill -TERM \$MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal
SyslogIdentifier=$SERVICE_NAME

Environment=JAVA_HOME=$JAVA_HOME_PATH

[Install]
WantedBy=multi-user.target
EOF

# 重新加载 systemd
systemctl daemon-reload

echo ""
echo "=== 部署完成！==="
echo ""
echo "下一步操作："
echo "1. 编辑配置文件并添加您的 API 密钥："
echo "   sudo nano $APP_HOME/config/application-prod.yml"
echo ""
echo "2. 启动服务："
echo "   sudo systemctl start $SERVICE_NAME"
echo ""
echo "3. 设置开机自启："
echo "   sudo systemctl enable $SERVICE_NAME"
echo ""
echo "4. 查看服务状态："
echo "   sudo systemctl status $SERVICE_NAME"
echo ""
echo "5. 查看日志："
echo "   sudo journalctl -u $SERVICE_NAME -f"
echo "   或查看应用日志: tail -f $LOG_DIR/application.log"
echo ""
echo "应用将在端口 5002 上运行"
echo "健康检查: http://your-server:5002/AIExpertDecisionServer/actuator/health"
