# 故障排查指南

## 问题诊断步骤

当遇到服务启动失败（状态码 203/EXEC）时，请按以下步骤排查：

### 1. 运行诊断脚本

```bash
# 上传并运行诊断脚本
sudo ./diagnose.sh
```

### 2. 手动检查 Java 环境

```bash
# 检查 Java 是否安装
java -version
javac -version

# 检查 Java 路径
which java
ls -la $(which java)

# 如果 Java 未安装，安装 Java 21
sudo apt update
sudo apt install openjdk-21-jdk -y
```

### 3. 检查用户和权限

```bash
# 检查 aiexpert 用户是否存在
id aiexpert

# 检查文件权限
ls -la /opt/aiexpert/
ls -la /opt/aiexpert/bin/

# 修复权限（如果需要）
sudo chown -R aiexpert:aiexpert /opt/aiexpert
sudo chmod +x /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar
```

### 4. 手动测试启动

```bash
# 使用测试脚本
sudo ./test-manual.sh

# 或者手动执行
sudo -u aiexpert java -jar \
    -Dspring.profiles.active=prod \
    -Dspring.config.location=file:/opt/aiexpert/config/ \
    /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar
```

### 5. 检查配置文件

```bash
# 确保配置文件存在且格式正确
sudo nano /opt/aiexpert/config/application-prod.yml

# 检查是否配置了 API 密钥
grep -n "api-key" /opt/aiexpert/config/application-prod.yml
```

### 6. 修复 systemd 服务

如果诊断脚本运行成功，它会自动修复 systemd 服务文件。手动修复步骤：

```bash
# 停止服务
sudo systemctl stop aiexpert

# 编辑服务文件
sudo nano /etc/systemd/system/aiexpert.service

# 确保 ExecStart 行使用正确的 Java 路径，例如：
# ExecStart=/usr/bin/java -jar -Xms512m -Xmx1024m -Dspring.profiles.active=prod -Dspring.config.location=file:/opt/aiexpert/config/ /opt/aiexpert/bin/aiexpertdecision-0.0.1-SNAPSHOT.jar

# 重新加载配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start aiexpert
```

## 常见问题解决方案

### 问题 1: Java 未找到或路径错误

**错误**: `Main process exited, code=exited, status=203/EXEC`

**解决方案**:
```bash
# 安装 Java
sudo apt install openjdk-21-jdk

# 查找正确的 Java 路径
which java
# 通常是 /usr/bin/java

# 更新服务文件中的 ExecStart 行
sudo nano /etc/systemd/system/aiexpert.service
```

### 问题 2: 权限问题

**错误**: 权限被拒绝

**解决方案**:
```bash
# 修复文件权限
sudo chown -R aiexpert:aiexpert /opt/aiexpert
sudo chmod +x /opt/aiexpert/bin/*.jar

# 修复日志目录权限
sudo chown -R aiexpert:aiexpert /var/log/aiexpert
```

### 问题 3: 配置文件问题

**错误**: 应用启动失败，配置错误

**解决方案**:
```bash
# 检查 YAML 语法
python3 -c "import yaml; yaml.safe_load(open('/opt/aiexpert/config/application-prod.yml'))"

# 确保 API 密钥已配置
grep "YOUR_.*_API_KEY_HERE" /opt/aiexpert/config/application-prod.yml
# 如果有输出，说明还有未配置的密钥
```

### 问题 4: 端口被占用

**错误**: 端口 5002 被占用

**解决方案**:
```bash
# 查看端口占用
sudo lsof -i :5002
sudo netstat -tlnp | grep :5002

# 杀死占用进程
sudo kill -9 <PID>
```

## 完整的修复流程

1. **停止服务**:
   ```bash
   sudo systemctl stop aiexpert
   ```

2. **运行诊断**:
   ```bash
   sudo ./diagnose.sh
   ```

3. **配置 API 密钥**:
   ```bash
   sudo nano /opt/aiexpert/config/application-prod.yml
   # 替换所有 YOUR_*_API_KEY_HERE 为实际密钥
   ```

4. **手动测试**:
   ```bash
   sudo ./test-manual.sh
   ```

5. **启动服务**:
   ```bash
   sudo systemctl start aiexpert
   sudo systemctl enable aiexpert
   ```

6. **验证启动**:
   ```bash
   sudo systemctl status aiexpert
   sudo journalctl -u aiexpert -f
   ```

## 日志查看

```bash
# 查看系统服务日志
sudo journalctl -u aiexpert -f

# 查看应用日志
sudo tail -f /var/log/aiexpert/application.log

# 查看最近的错误
sudo journalctl -u aiexpert --since "10 minutes ago"
```
