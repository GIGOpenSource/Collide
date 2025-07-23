# Collide 微服务 Systemd 部署脚本

## 📋 概述

本目录包含了 Collide 微服务项目的 Systemd 服务配置文件和部署脚本，支持将应用作为系统服务运行。

## 🚀 快速部署

### 一键部署所有服务

```bash
cd scripts/systemd
sudo bash deploy-all-services.sh
```

### 单独部署服务

```bash
# 部署Gateway服务
sudo bash create-gateway-service.sh

# 部署Auth服务  
sudo bash create-auth-service.sh

# 部署Application服务
sudo bash create-app-service.sh
```

## 📁 文件说明

| 文件 | 说明 |
|------|------|
| `create-gateway-service.sh` | Gateway服务部署脚本 |
| `create-auth-service.sh` | Auth服务部署脚本 |
| `create-app-service.sh` | Application服务部署脚本 |  
| `deploy-all-services.sh` | 一键部署所有服务 |
| `collide-gateway.service` | Gateway服务systemd配置模板 |
| `install-gateway-service.sh` | Gateway服务详细安装脚本 |

## 🔧 服务配置

### 默认配置

| 服务 | 端口 | 内存 | 日志路径 |
|------|------|------|----------|
| Gateway | 9501 | 1G-2G | /root/logs/gateway/ |
| Auth | 9502 | 1G-1G | /root/logs/auth/ |
| Application | 9503 | 2G-2G | /root/logs/app/ |

### 环境参数

```bash
DUBBO_IP_TO_REGISTRY=139.11.11.11
NACOS_DISCOVERY_IP=18.166.150.123
SPRING_PROFILES_ACTIVE=prod
```

## 📊 常用命令

### 服务管理

```bash
# 查看所有服务状态
sudo systemctl status collide-*

# 启动服务
sudo systemctl start collide-gateway
sudo systemctl start collide-auth
sudo systemctl start collide-application

# 停止服务
sudo systemctl stop collide-gateway
sudo systemctl stop collide-auth  
sudo systemctl stop collide-application

# 重启服务
sudo systemctl restart collide-gateway
sudo systemctl restart collide-auth
sudo systemctl restart collide-application

# 开机自启
sudo systemctl enable collide-gateway
sudo systemctl enable collide-auth
sudo systemctl enable collide-application

# 禁用开机自启
sudo systemctl disable collide-gateway
sudo systemctl disable collide-auth
sudo systemctl disable collide-application
```

### 日志查看

```bash
# 查看systemd日志
sudo journalctl -u collide-gateway -f
sudo journalctl -u collide-auth -f
sudo journalctl -u collide-application -f

# 查看应用日志
sudo tail -f /root/logs/gateway/app.log
sudo tail -f /root/logs/auth/app.log
sudo tail -f /root/logs/app/app.log

# 查看错误日志
sudo tail -f /root/logs/gateway/error.log
sudo tail -f /root/logs/auth/error.log
sudo tail -f /root/logs/app/error.log

# 查看GC日志
sudo tail -f /root/logs/gateway/gc.log
sudo tail -f /root/logs/auth/gc.log
sudo tail -f /root/logs/app/gc.log
```

## 🔍 故障排查

### 服务启动失败

```bash
# 检查服务状态
sudo systemctl status collide-gateway

# 查看详细错误日志
sudo journalctl -u collide-gateway -n 50

# 检查jar包是否存在
ls -la /www/Collide/collide-gateway/target/*.jar

# 检查端口是否被占用
sudo netstat -tulnp | grep :9501
```

### 服务无法连接

```bash
# 检查服务是否在运行
sudo systemctl is-active collide-gateway

# 检查端口监听
sudo ss -tulnp | grep :9501

# 测试服务健康检查
curl http://localhost:9501/actuator/health
```

## 📋 部署前检查

1. **Java环境**: 确保安装了OpenJDK 21
2. **jar包**: 确保所有jar包都已编译并放在正确路径
3. **目录权限**: 确保/www/Collide/目录权限正确
4. **端口**: 确保9501、9502、9503端口未被占用
5. **依赖服务**: 确保Nacos、MySQL、Redis等中间件已启动

## 🛠️ 自定义配置

如需修改配置，请编辑对应的创建脚本，主要修改项：

- 内存配置：`-Xms` 和 `-Xmx` 参数
- 服务发现IP：`-Dspring.cloud.nacos.discovery.ip`
- Dubbo注册IP：`-DDUBBO_IP_TO_REGISTRY`
- 工作目录：`WorkingDirectory`
- 日志路径：`StandardOutput` 和 `StandardError` 