# 🚀 Collide 应用服务 Docker 快速部署

## 📋 概述

本方案提供 Collide 项目三个核心应用服务的 Docker 快速部署，包括：

- **🌐 collide-gateway** (8081) - 网关服务
- **🔐 collide-auth** (8082) - 认证服务  
- **🚀 collide-application** (8085) - 业务服务

> **📢 前提条件**: 确保您的中间件服务（MySQL、Redis、Nacos、RocketMQ）已正常运行

## 🎯 快速开始

### 方法1: 一键启动脚本 (推荐)

**Windows:**
```cmd
quick-start.bat
```

**Linux/Mac:**
```bash
chmod +x quick-start.sh
./quick-start.sh
```

### 方法2: 手动 Docker Compose

```bash
# 启动所有应用服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 🔧 中间件连接配置

应用服务通过 `host.docker.internal` 连接到宿主机的中间件服务：

```yaml
# 默认连接地址
MySQL:    host.docker.internal:3306
Redis:    host.docker.internal:6379
Nacos:    host.docker.internal:8848
RocketMQ: host.docker.internal:9876
```

如需修改连接地址，请编辑 `docker-compose.yml` 中的环境变量。

## 📊 服务详情

| 服务 | 端口 | 健康检查 | 说明 |
|------|------|----------|------|
| collide-gateway | 8081 | `/actuator/health` | API网关，路由转发 |
| collide-auth | 8082 | `/actuator/health` | 用户认证，Token管理 |
| collide-application | 8085 | `/actuator/health` | 核心业务逻辑 |

## 🛠️ 常用命令

### 启动和停止

```bash
# 启动所有服务
docker-compose up -d

# 启动指定服务
docker-compose up -d collide-auth

# 停止所有服务
docker-compose down

# 重启服务
docker-compose restart collide-application
```

### 查看状态和日志

```bash
# 查看服务状态
docker-compose ps

# 查看所有日志
docker-compose logs -f

# 查看指定服务日志
docker-compose logs -f collide-application

# 实时查看最新日志
docker-compose logs -f --tail=100 collide-gateway
```

### 重建镜像

```bash
# 重建并启动
docker-compose up -d --build

# 重建指定服务
docker-compose up -d --build collide-auth

# 强制重建
docker-compose build --no-cache collide-application
```

## 🔍 故障排查

### 1. 启动失败

```bash
# 检查服务状态
docker-compose ps

# 查看详细日志
docker-compose logs collide-application

# 进入容器调试
docker-compose exec collide-application bash
```

### 2. 中间件连接问题

启动脚本会自动检查中间件连通性：

```bash
# 手动检查连通性 (Linux/Mac)
nc -z localhost 3306  # MySQL
nc -z localhost 6379  # Redis
nc -z localhost 8848  # Nacos
nc -z localhost 9876  # RocketMQ

# Windows
netstat -an | findstr "127.0.0.1:3306"
```

### 3. 端口冲突

```bash
# 检查端口占用
netstat -tulpn | grep 8081  # Linux/Mac
netstat -ano | findstr 8081  # Windows

# 停止占用端口的进程
kill -9 <PID>  # Linux/Mac
taskkill /PID <PID> /F  # Windows
```

## ⚡ 性能调优

### JVM 内存配置

修改 `docker-compose.yml` 中的环境变量：

```yaml
environment:
  # 网关服务 (轻量级)
  - JAVA_OPTS=-Xmx512m -Xms256m -XX:+UseG1GC
  
  # 应用服务 (重量级)
  - JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC
```

### 容器资源限制

```yaml
services:
  collide-application:
    # ... other config
    deploy:
      resources:
        limits:
          memory: 1G
          cpus: '1.0'
        reservations:
          memory: 512M
          cpus: '0.5'
```

## 🔐 生产环境配置

### 1. 修改默认密码

编辑 `docker-compose.yml` 中的环境变量：

```yaml
environment:
  SPRING_DATASOURCE_PASSWORD: your_secure_password
  SPRING_REDIS_PASSWORD: your_secure_redis_password
```

### 2. 使用 Docker Secrets

```yaml
services:
  collide-auth:
    secrets:
      - db_password
      - redis_password
    environment:
      SPRING_DATASOURCE_PASSWORD_FILE: /run/secrets/db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

### 3. 网络隔离

```yaml
networks:
  frontend:
    driver: bridge
  backend:
    driver: bridge
    internal: true  # 内部网络，不能访问外网
```

## 📈 监控和日志

### 健康检查

```bash
# 检查所有服务健康状态
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health  
curl http://localhost:8085/actuator/health
```

### 应用指标

访问以下地址查看应用指标：

- **Prometheus指标**: http://localhost:8085/actuator/prometheus
- **JVM指标**: http://localhost:8085/actuator/metrics
- **应用信息**: http://localhost:8085/actuator/info

### 日志配置

配置日志轮转避免磁盘空间不足：

```yaml
services:
  collide-application:
    logging:
      driver: "json-file"
      options:
        max-size: "100m"
        max-file: "3"
```

## 🚀 扩容和负载均衡

### 水平扩容

```bash
# 扩容应用服务到3个实例
docker-compose up -d --scale collide-application=3

# 扩容认证服务到2个实例
docker-compose up -d --scale collide-auth=2
```

### 负载均衡

配合 Nginx 实现负载均衡：

```nginx
upstream collide_app {
    server localhost:8085;
    server localhost:8086;
    server localhost:8087;
}

server {
    listen 80;
    location / {
        proxy_pass http://collide_app;
    }
}
```

## 📝 脚本命令参考

### Linux/Mac (quick-start.sh)

```bash
./quick-start.sh                    # 启动所有应用服务
./quick-start.sh stop               # 停止所有应用服务
./quick-start.sh restart            # 重启所有应用服务
./quick-start.sh status             # 查看服务状态
./quick-start.sh logs               # 查看所有日志
./quick-start.sh logs collide-auth  # 查看认证服务日志
./quick-start.sh help               # 显示帮助信息
```

### Windows (quick-start.bat)

```cmd
quick-start.bat                     # 启动所有应用服务
quick-start.bat stop               # 停止所有应用服务  
quick-start.bat restart            # 重启所有应用服务
quick-start.bat status             # 查看服务状态
quick-start.bat logs               # 查看所有日志
quick-start.bat help               # 显示帮助信息
```

## 🔧 自定义配置

### 修改端口映射

```yaml
ports:
  - "9081:8081"  # 网关服务映射到9081
  - "9082:8082"  # 认证服务映射到9082
  - "9085:8085"  # 应用服务映射到9085
```

### 挂载配置文件

```yaml
volumes:
  - ./config/application.yml:/app/config/application.yml
  - ./logs:/app/logs
```

### 自定义环境变量

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=production
  - LOGGING_LEVEL_ROOT=WARN
  - SPRING_JPA_SHOW_SQL=false
```

## 🆘 获取帮助

如果遇到问题：

1. **查看日志**: `docker-compose logs -f [service-name]`
2. **检查服务状态**: `docker-compose ps`
3. **检查中间件连通性**: 运行启动脚本会自动检查
4. **重启服务**: `docker-compose restart [service-name]`
5. **重建镜像**: `docker-compose up -d --build [service-name]`

---

**🎉 享受您的 Collide 应用服务之旅！** 