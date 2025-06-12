# AI Expert Decision System - Ubuntu 服务器部署指南

## 系统要求

- Ubuntu 18.04+ 
- Java 21+ (OpenJDK 推荐)
- 最少 1GB RAM
- 最少 2GB 磁盘空间

## 部署步骤

### 1. 准备服务器环境

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装 Java 21
sudo apt install openjdk-21-jdk -y

# 验证 Java 安装
java -version
javac -version

# 安装其他必要工具
sudo apt install curl wget unzip -y
```

### 2. 上传文件到服务器

将以下文件上传到服务器的同一目录：
- `aiexpertdecision-0.0.1-SNAPSHOT.jar`
- `application.yml`
- `application-prod.yml`
- `deploy.sh`

```bash
# 创建部署目录
mkdir -p ~/aiexpert-deploy
cd ~/aiexpert-deploy

# 上传文件（使用 scp 或其他方式）
# scp aiexpertdecision-0.0.1-SNAPSHOT.jar user@server:~/aiexpert-deploy/
# scp application*.yml user@server:~/aiexpert-deploy/
# scp deploy.sh user@server:~/aiexpert-deploy/
```

### 3. 运行部署脚本

```bash
# 给部署脚本执行权限
chmod +x deploy.sh

# 以 root 身份运行部署脚本
sudo ./deploy.sh
```

### 4. 配置 API 密钥

```bash
# 编辑生产环境配置文件
sudo nano /opt/aiexpert/config/application-prod.yml

# 在文件中填入您的实际 API 密钥：
# - YOUR_DEEPSEEK_API_KEY_HERE 替换为您的 DeepSeek API 密钥
# - YOUR_OPENROUTER_API_KEY_HERE 替换为您的 OpenRouter API 密钥
```

### 5. 启动服务

```bash
# 启动服务
sudo systemctl start aiexpert

# 设置开机自启
sudo systemctl enable aiexpert

# 查看服务状态
sudo systemctl status aiexpert
```

## 服务管理命令

```bash
# 启动服务
sudo systemctl start aiexpert

# 停止服务
sudo systemctl stop aiexpert

# 重启服务
sudo systemctl restart aiexpert

# 查看服务状态
sudo systemctl status aiexpert

# 查看实时日志
sudo journalctl -u aiexpert -f

# 查看应用日志
sudo tail -f /var/log/aiexpert/application.log
```

## 日志位置

### 1. 系统日志 (systemd)
- **位置**: 通过 `journalctl` 查看
- **命令**: `sudo journalctl -u aiexpert`
- **实时查看**: `sudo journalctl -u aiexpert -f`

### 2. 应用日志
- **位置**: `/var/log/aiexpert/application.log`
- **查看**: `sudo tail -f /var/log/aiexpert/application.log`
- **日志轮转**: 自动轮转，最大 100MB/文件，保留 30 天

### 3. 目录结构
```
/opt/aiexpert/
├── bin/
│   └── aiexpertdecision-0.0.1-SNAPSHOT.jar
├── config/
│   ├── application.yml
│   └── application-prod.yml
└── logs/                    # 应用可能创建的额外日志

/var/log/aiexpert/
└── application.log          # 主要应用日志
```

## 网络配置

### 端口
- **应用端口**: 5002
- **上下文路径**: `/AIExpertDecisionServer`
- **完整访问地址**: `http://your-server:5002/AIExpertDecisionServer/`

### 防火墙配置
```bash
# 开放端口 5002
sudo ufw allow 5002

# 查看防火墙状态
sudo ufw status
```

## 健康检查

```bash
# 检查应用是否启动
curl http://localhost:5002/AIExpertDecisionServer/actuator/health

# 检查端口是否监听
sudo netstat -tlnp | grep :5002
```

## 故障排查

### 1. 服务启动失败
```bash
# 查看详细错误日志
sudo journalctl -u aiexpert -n 50

# 检查配置文件语法
sudo -u aiexpert java -jar /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar --spring.config.location=file:/opt/aiexpert/config/ --spring.profiles.active=prod --server.port=0 --spring.main.web-application-type=none
```

### 2. 端口被占用
```bash
# 查看端口占用
sudo lsof -i :5002

# 杀死占用进程
sudo kill -9 <PID>
```

### 3. 权限问题
```bash
# 重新设置权限
sudo chown -R aiexpert:aiexpert /opt/aiexpert
sudo chown -R aiexpert:aiexpert /var/log/aiexpert
```

## 更新部署

```bash
# 停止服务
sudo systemctl stop aiexpert

# 备份当前版本
sudo cp /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar.backup

# 替换新版本
sudo cp new-version.jar /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar
sudo chown aiexpert:aiexpert /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar

# 启动服务
sudo systemctl start aiexpert
```

## 监控建议

### 1. 使用 htop 监控系统资源
```bash
sudo apt install htop
htop
```

### 2. 设置日志轮转（如果需要自定义）
```bash
sudo nano /etc/logrotate.d/aiexpert
```

### 3. 监控磁盘空间
```bash
# 检查磁盘使用情况
df -h
du -sh /var/log/aiexpert/
```
