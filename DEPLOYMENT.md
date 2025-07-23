# Collide 项目部署指南 (Ubuntu 22.04)

本文档提供了在Ubuntu 22.04环境下部署Collide项目的完整指南，包括物理机启动脚本和systemd服务配置。

## 项目结构

```
Collide/
├── scripts/                     # 启动脚本目录
│   ├── start-collide-application.sh    # Application服务启动脚本
│   ├── start-collide-auth.sh           # Auth服务启动脚本
│   ├── start-collide-gateway.sh        # Gateway服务启动脚本
│   └── deploy.sh                       # 一键部署脚本
├── systemd/                     # systemd服务配置目录
│   ├── collide-application.service     # Application服务配置
│   ├── collide-auth.service            # Auth服务配置
│   └── collide-gateway.service         # Gateway服务配置
└── DEPLOYMENT.md                # 本文档
```

## 服务信息

| 服务名 | 端口 | 主启动类 | 内存配置 | 健康检查地址 |
|--------|------|----------|----------|-------------|
| collide-auth | 9500 | `com.gig.collide.auth.CollideAuthApplication` | 256m-512m | `/api/v1/auth/test` |
| collide-application | 8085 | `com.gig.collide.CollideBusinessApplication` | 512m-1024m | `/actuator/health` |
| collide-gateway | 8081 | `com.gig.collide.gateway.CollideGatewayApplication` | 256m-512m | `/actuator/health` |

## Ubuntu 22.04 环境准备

### 1. 更新系统
```bash
sudo apt update && sudo apt upgrade -y
```

### 2. 安装Java 21
```bash
# 安装OpenJDK 21
sudo apt install openjdk-21-jdk -y

# 验证安装
java -version
javac -version

# 设置JAVA_HOME (可选)
echo 'export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

### 3. 安装Maven
```bash
# 安装Maven
sudo apt install maven -y

# 验证安装
mvn -version
```

### 4. 安装必要工具
```bash
# 安装curl和其他工具
sudo apt install curl net-tools -y
```

## 快速部署

### 一键部署（推荐）

```bash
# 1. 进入项目根目录
cd /path/to/Collide

# 2. 赋予脚本执行权限
chmod +x scripts/*.sh

# 3. 一键安装所有服务
sudo ./scripts/deploy.sh all install

# 4. 启动服务（按推荐顺序）
sudo systemctl start collide-auth
sleep 10
sudo systemctl start collide-application  
sleep 10
sudo systemctl start collide-gateway

# 5. 检查服务状态
sudo systemctl status collide-auth
sudo systemctl status collide-application
sudo systemctl status collide-gateway
```

### 验证部署

```bash
# 检查所有服务是否正常运行
curl http://localhost:9500/api/v1/auth/test
curl http://localhost:8085/actuator/health
curl http://localhost:8081/actuator/health

# 查看端口监听情况
sudo netstat -tlnp | grep -E ':(8081|8085|9500)'
```

## 手动部署步骤

### 1. 编译项目
```bash
cd /path/to/Collide
mvn clean package -DskipTests
```

### 2. 创建系统用户
```bash
# 创建collide用户和组
sudo groupadd collide
sudo useradd -r -g collide -s /bin/bash -d /opt/collide collide

# 创建必要目录
sudo mkdir -p /opt/collide
sudo mkdir -p /var/log/collide-{auth,application,gateway}

# 设置目录权限
sudo chown -R collide:collide /opt/collide
sudo chown -R collide:collide /var/log/collide-*
```

### 3. 部署应用文件
```bash
# 复制项目文件
sudo cp -r /path/to/Collide/* /opt/collide/
sudo chown -R collide:collide /opt/collide

# 设置脚本权限
sudo chmod +x /opt/collide/scripts/*.sh
```

### 4. 配置systemd服务
```bash
# 复制服务配置文件
sudo cp /opt/collide/systemd/*.service /etc/systemd/system/

# 重新加载systemd配置
sudo systemctl daemon-reload

# 启用服务（开机自启）
sudo systemctl enable collide-auth.service
sudo systemctl enable collide-application.service
sudo systemctl enable collide-gateway.service
```

## 服务管理

### 使用systemd命令管理

```bash
# 启动服务
sudo systemctl start collide-auth
sudo systemctl start collide-application
sudo systemctl start collide-gateway

# 停止服务
sudo systemctl stop collide-gateway
sudo systemctl stop collide-application
sudo systemctl stop collide-auth

# 重启服务
sudo systemctl restart collide-auth

# 查看服务状态
sudo systemctl status collide-auth

# 查看服务日志
sudo journalctl -u collide-auth -f --no-pager

# 开机自启管理
sudo systemctl enable collide-auth   # 启用开机自启
sudo systemctl disable collide-auth  # 禁用开机自启
```

### 使用启动脚本管理

```bash
# 启动服务
sudo /opt/collide/scripts/start-collide-auth.sh start
sudo /opt/collide/scripts/start-collide-application.sh start
sudo /opt/collide/scripts/start-collide-gateway.sh start

# 停止服务
sudo /opt/collide/scripts/start-collide-auth.sh stop

# 重启服务
sudo /opt/collide/scripts/start-collide-auth.sh restart

# 查看服务状态
sudo /opt/collide/scripts/start-collide-auth.sh status

# 查看实时日志
sudo /opt/collide/scripts/start-collide-auth.sh logs
```

## 推荐启动顺序

**重要**: 请按以下顺序启动服务，并在每个服务启动后等待其完全启动：

```bash
# 1. 首先启动认证服务
sudo systemctl start collide-auth
sleep 15

# 验证认证服务启动成功
curl http://localhost:9500/api/v1/auth/test

# 2. 启动业务应用服务
sudo systemctl start collide-application
sleep 15

# 验证应用服务启动成功
curl http://localhost:8085/actuator/health

# 3. 最后启动网关服务
sudo systemctl start collide-gateway
sleep 15

# 验证网关服务启动成功
curl http://localhost:8081/actuator/health
```

## 健康检查与监控

### 服务健康检查
```bash
#!/bin/bash
# 健康检查脚本

echo "=== Collide 服务健康检查 ==="

# 检查认证服务
echo "检查认证服务 (端口 9500)..."
if curl -s http://localhost:9500/api/v1/auth/test > /dev/null; then
    echo "✅ 认证服务正常"
else
    echo "❌ 认证服务异常"
fi

# 检查应用服务
echo "检查应用服务 (端口 8085)..."
if curl -s http://localhost:8085/actuator/health > /dev/null; then
    echo "✅ 应用服务正常"
else
    echo "❌ 应用服务异常"
fi

# 检查网关服务
echo "检查网关服务 (端口 8081)..."
if curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "✅ 网关服务正常"
else
    echo "❌ 网关服务异常"
fi

# 检查端口监听
echo -e "\n端口监听情况:"
sudo netstat -tlnp | grep -E ':(8081|8085|9500)'
```

### 查看系统资源使用
```bash
# 查看内存使用
free -h

# 查看CPU使用
top -p $(pgrep -f collide)

# 查看磁盘使用
df -h

# 查看Java进程
ps aux | grep java
```

## 日志管理

### 日志文件位置
- **认证服务**: `/var/log/collide-auth/collide-auth.log`
- **应用服务**: `/var/log/collide-application/collide-application.log`
- **网关服务**: `/var/log/collide-gateway/collide-gateway.log`

### 日志查看命令
```bash
# 查看实时日志
tail -f /var/log/collide-auth/collide-auth.log

# 查看最近100行日志
tail -n 100 /var/log/collide-auth/collide-auth.log

# 搜索错误日志
grep -i error /var/log/collide-auth/collide-auth.log

# 使用systemd查看日志
sudo journalctl -u collide-auth -f
sudo journalctl -u collide-auth --since "1 hour ago"
sudo journalctl -u collide-auth --lines=100
```

### 日志轮转配置
```bash
# 创建日志轮转配置
sudo tee /etc/logrotate.d/collide << EOF
/var/log/collide-*/*.log {
    daily
    missingok
    rotate 30
    compress
    notifempty
    create 0644 collide collide
    postrotate
        systemctl reload collide-auth collide-application collide-gateway 2>/dev/null || true
    endscript
}
EOF
```

## 故障排除

### 常见问题解决

#### 1. 服务启动失败
```bash
# 检查Java环境
java -version
which java

# 检查JAR文件
ls -la /opt/collide/*/target/*.jar

# 查看详细启动日志
sudo journalctl -u collide-auth --no-pager -l

# 检查端口占用
sudo lsof -i :9500
```

#### 2. 端口冲突
```bash
# 检查端口使用情况
sudo netstat -tlnp | grep :9500

# 杀死占用端口的进程
sudo kill -9 <PID>

# 或者使用fuser命令
sudo fuser -k 9500/tcp
```

#### 3. 权限问题
```bash
# 检查文件权限
ls -la /opt/collide/
ls -la /var/log/collide-*/

# 修复权限
sudo chown -R collide:collide /opt/collide
sudo chown -R collide:collide /var/log/collide-*
sudo chmod +x /opt/collide/scripts/*.sh
```

#### 4. 内存不足
```bash
# 检查内存使用
free -h
ps aux --sort=-%mem | head

# 调整JVM内存参数（编辑systemd服务文件）
sudo nano /etc/systemd/system/collide-auth.service
# 修改: Environment=JAVA_OPTS=-Xms128m -Xmx256m ...

# 重新加载配置并重启
sudo systemctl daemon-reload
sudo systemctl restart collide-auth
```

### Ubuntu 22.04 特定问题

#### AppArmor 相关问题
```bash
# 检查AppArmor状态
sudo aa-status

# 如果需要，可以临时禁用
sudo systemctl stop apparmor
sudo systemctl disable apparmor
```

#### 防火墙配置
```bash
# 检查UFW状态
sudo ufw status

# 开放必要端口
sudo ufw allow 8081/tcp
sudo ufw allow 8085/tcp
sudo ufw allow 9500/tcp

# 或者临时禁用防火墙（不推荐生产环境）
sudo ufw disable
```

## 性能优化

### JVM 调优参数
```bash
# 编辑systemd服务文件
sudo nano /etc/systemd/system/collide-application.service

# 优化JAVA_OPTS参数
Environment=JAVA_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/collide-application/
```

### 系统级优化
```bash
# 调整文件描述符限制
echo "collide soft nofile 65536" | sudo tee -a /etc/security/limits.conf
echo "collide hard nofile 65536" | sudo tee -a /etc/security/limits.conf

# 调整内核参数
echo "net.core.somaxconn=65535" | sudo tee -a /etc/sysctl.conf
echo "net.ipv4.tcp_max_syn_backlog=65535" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p
```

## 卸载与清理

### 完全卸载
```bash
# 停止所有服务
sudo systemctl stop collide-gateway collide-application collide-auth

# 禁用服务
sudo systemctl disable collide-gateway collide-application collide-auth

# 删除systemd配置
sudo rm -f /etc/systemd/system/collide-*.service
sudo systemctl daemon-reload

# 删除用户和文件
sudo userdel collide
sudo groupdel collide
sudo rm -rf /opt/collide
sudo rm -rf /var/log/collide-*

# 清理防火墙规则（如果设置过）
sudo ufw delete allow 8081/tcp
sudo ufw delete allow 8085/tcp
sudo ufw delete allow 9500/tcp
```

### 使用脚本卸载
```bash
# 卸载所有服务
sudo ./scripts/deploy.sh all uninstall

# 或卸载单个服务
sudo ./scripts/deploy.sh auth uninstall
```

## 生产环境配置建议

### 1. 安全配置
```bash
# 创建专用用户，限制权限
sudo usermod -s /usr/sbin/nologin collide

# 配置SSL证书（如果需要HTTPS）
# 在网关服务中配置SSL
```

### 2. 监控配置
```bash
# 安装监控工具
sudo apt install htop iotop

# 配置日志监控
# 可以集成ELK栈或其他日志系统
```

### 3. 备份策略
```bash
# 配置定期备份
sudo crontab -e
# 添加: 0 2 * * * tar -czf /backup/collide-$(date +\%Y\%m\%d).tar.gz /opt/collide
```

## 常用维护命令

```bash
# 一键检查所有服务状态
sudo systemctl status collide-* --no-pager

# 一键重启所有服务
sudo systemctl restart collide-auth collide-application collide-gateway

# 查看所有服务日志
sudo journalctl -u collide-* -f

# 检查系统资源
sudo systemctl status
df -h
free -h
```

---

**注意**: 
1. 请根据实际生产环境调整JVM内存参数和端口配置
2. 建议在生产环境中配置反向代理（如Nginx）
3. 定期检查和更新Java和Maven版本
4. 建立完善的监控和告警机制 