# 🐳 Collide Docker 部署指南

## 📋 概述

本指南介绍如何使用 Docker 和 Docker Compose 部署 Collide 微服务应用。此部署方案专为**外部中间件**环境设计，应用服务通过环境变量连接到独立部署的 MySQL、Redis、Nacos 等中间件。

## 🏗️ 架构说明

### 服务架构

```
┌─────────────────────────────────────────────────────────────┐
│                    Collide 应用服务                          │
├─────────────────────────────────────────────────────────────┤
│  🌐 网关服务 (9500)  │  🔐 认证服务 (9501)  │  💼 业务服务 (9502) │
│  - API Gateway       │  - 用户认证          │  - 业务逻辑         │
│  - 路由转发          │  - Token 管理         │  - 数据处理         │
│  - 负载均衡          │  - 权限验证          │  - 业务接口         │
└─────────────────────────────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                    外部中间件服务                             │
├─────────────────────────────────────────────────────────────┤
│  📊 Nacos (8848)     │  🗄️  MySQL (3306)    │  🔄 Redis (6379)   │
│  - 服务注册          │  - 主数据存储         │  - 缓存服务         │
│  - 配置管理          │  - 业务数据           │  - 会话存储         │
│  - 服务发现          │  - 元数据管理         │  - 分布式锁         │
└─────────────────────────────────────────────────────────────┘
```

### 端口分配策略

为避免与中间件端口冲突，应用服务端口从 **9500** 开始：

| 服务 | 容器端口 | 主机端口 | 功能描述 |
|------|----------|----------|----------|
| **collide-gateway** | 9500 | 9500 | API网关，统一入口 |
| **collide-auth** | 9501 | 9501 | 认证服务，用户管理 |
| **collide-application** | 9502 | 9502 | 业务服务，核心功能 |

### 中间件端口（外部部署）

| 中间件 | 默认端口 | 说明 |
|--------|----------|------|
| **Nacos** | 8848 | 服务注册与配置中心 |
| **MySQL** | 3306 | 关系型数据库 |
| **Redis** | 6379 | 内存缓存数据库 |
| **Dubbo** | 20901-20902 | RPC通信端口 |

## 🚀 快速开始

### 1. 环境准备

确保已安装以下软件：

```bash
# 检查 Docker 版本
docker --version

# 检查 Docker Compose 版本  
docker-compose --version

# 检查 Maven 版本
mvn --version

# 检查 Java 版本
java --version
```

### 2. 外部中间件确认

在启动应用服务前，确保以下中间件服务已启动并可访问：

```bash
# 测试 Nacos 连通性
curl http://localhost:8848/nacos

# 测试 MySQL 连通性
mysql -h localhost -P 3306 -u collide -p

# 测试 Redis 连通性
redis-cli -h localhost -p 6379 ping
```

### 3. 配置环境变量

#### 方法1: 本地开发配置

```bash
# 复制本地开发配置
cp env.local.example .env

# 查看配置内容
cat .env
```

配置内容示例：
```bash
# 中间件地址 (本地)
NACOS_SERVER_ADDR=localhost:8848
MYSQL_HOST=localhost
REDIS_HOST=localhost

# 应用服务端口
GATEWAY_PORT=9500
AUTH_PORT=9501
BUSINESS_PORT=9502
```

#### 方法2: 生产环境配置

```bash
# 复制生产环境配置模板
cp env.production.example .env

# 修改为实际的服务器地址
nano .env
```

配置内容示例：
```bash
# 中间件地址 (生产环境)
NACOS_SERVER_ADDR=192.168.1.100:8848
MYSQL_HOST=192.168.1.101
REDIS_HOST=192.168.1.102
MYSQL_PASSWORD=ProductionPassword123
REDIS_PASSWORD=ProductionRedisPassword
```

### 4. 一键部署

#### 完整构建部署

```bash
# 执行完整的构建和部署流程
./build-and-deploy.bat

# Linux/Mac 用户
chmod +x build-and-deploy.sh && ./build-and-deploy.sh
```

#### 快速启动

```bash
# 快速启动（如果镜像已存在）
./docker-quick-start.bat

# Linux/Mac 用户  
chmod +x docker-quick-start.sh && ./docker-quick-start.sh
```

## 📊 服务验证

### 1. 检查服务状态

```bash
# 查看所有服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f collide-gateway
```

### 2. 健康检查

```bash
# 自动健康检查
./test-deployment.bat

# 手动健康检查
curl http://localhost:9500/actuator/health  # 网关
curl http://localhost:9501/actuator/health  # 认证
curl http://localhost:9502/actuator/health  # 业务
```

### 3. 服务访问测试

```bash
# 网关服务测试
curl http://localhost:9500/actuator/info

# 认证服务测试
curl http://localhost:9501/actuator/info

# 业务服务测试  
curl http://localhost:9502/actuator/info
```

## 🔧 配置管理

### 环境变量完整列表

#### 🌐 中间件配置

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `NACOS_SERVER_ADDR` | `localhost:8848` | Nacos服务地址 |
| `NACOS_SERVER_USERNAME` | `nacos` | Nacos用户名 |
| `NACOS_SERVER_PASSWORD` | `nacos` | Nacos密码 |
| `MYSQL_HOST` | `localhost` | MySQL主机地址 |
| `MYSQL_PORT` | `3306` | MySQL端口 |
| `MYSQL_DATABASE` | `collide_db` | 数据库名 |
| `MYSQL_USERNAME` | `collide` | 数据库用户名 |
| `MYSQL_PASSWORD` | `your_password` | 数据库密码 |
| `REDIS_HOST` | `localhost` | Redis主机地址 |
| `REDIS_PORT` | `6379` | Redis端口 |
| `REDIS_PASSWORD` | ` ` | Redis密码 |

#### 🚀 应用服务配置

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `GATEWAY_PORT` | `9500` | 网关服务主机端口 |
| `AUTH_PORT` | `9501` | 认证服务主机端口 |
| `BUSINESS_PORT` | `9502` | 业务服务主机端口 |
| `APP_VERSION` | `1.0.0` | 应用版本 |
| `APP_ENVIRONMENT` | `docker` | 环境标识 |

### 配置文件位置

| 配置文件 | 说明 |
|----------|------|
| `.env` | 主配置文件（从模板复制而来） |
| `env.example` | 完整配置模板 |
| `env.local.example` | 本地开发配置模板 |
| `env.production.example` | 生产环境配置模板 |
| `CONFIG-GUIDE.md` | 详细配置指南 |

## 🌍 多环境部署

### 开发环境

```bash
# 使用本地中间件
cp env.local.example .env
./docker-quick-start.bat
```

### 测试环境

```bash
# 修改为测试服务器地址
export NACOS_SERVER_ADDR=test-nacos:8848
export MYSQL_HOST=test-mysql
export REDIS_HOST=test-redis
docker-compose up -d
```

### 生产环境

```bash
# 使用生产配置
cp env.production.example .env
# 修改 .env 文件中的生产环境地址
./build-and-deploy.bat
```

## 🚨 故障排除

### 常见问题

#### 1. 服务启动失败

**现象**: 容器启动后立即退出

**排查步骤**:
```bash
# 查看容器日志
docker-compose logs collide-auth

# 检查配置文件
cat .env

# 验证中间件连通性
telnet $MYSQL_HOST 3306
telnet $REDIS_HOST 6379
```

#### 2. 中间件连接失败

**现象**: 健康检查失败，日志显示连接错误

**解决方案**:
```bash
# 检查中间件服务状态
systemctl status mysql
systemctl status redis
systemctl status nacos

# 验证网络连通性
ping $MYSQL_HOST
nc -zv $REDIS_HOST 6379

# 检查防火墙设置
netstat -tlnp | grep 3306
```

#### 3. 端口冲突

**现象**: 端口被占用错误

**解决方案**:
```bash
# 检查端口占用
netstat -tlnp | grep 9500

# 修改端口配置
export GATEWAY_PORT=9600
export AUTH_PORT=9601  
export BUSINESS_PORT=9602

# 重启服务
docker-compose down && docker-compose up -d
```

### 日志分析

```bash
# 查看启动日志
docker-compose logs --tail=100 collide-gateway

# 实时查看日志
docker-compose logs -f collide-auth

# 查看所有服务日志
docker-compose logs --tail=50
```

## 📈 性能优化

### 容器资源限制

```yaml
# docker-compose.yml 添加资源限制
services:
  collide-gateway:
    deploy:
      resources:
        limits:
          memory: 512M
          cpus: '0.5'
        reservations:
          memory: 256M
          cpus: '0.25'
```

### JVM 参数优化

Dockerfile 中已包含优化的 JVM 参数：

```dockerfile
# 生产环境 JVM 优化
ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 数据库连接池调优

通过环境变量调整连接池：

```bash
# 业务服务连接池配置
DB_POOL_MIN=10
DB_POOL_MAX=50
DB_CONN_TIMEOUT=30000

# Redis 连接池配置
REDIS_POOL_MAX_ACTIVE=30
REDIS_POOL_MAX_IDLE=15
```

## 📚 参考资料

### 官方文档

- [Docker Compose 文档](https://docs.docker.com/compose/)
- [Spring Boot Docker 指南](https://spring.io/guides/gs/spring-boot-docker/)
- [Nacos Docker 部署](https://nacos.io/zh-cn/docs/quick-start-docker.html)

### 脚本说明

| 脚本文件 | 功能描述 |
|----------|----------|
| `build-and-deploy.bat` | 完整构建和部署流程 |
| `docker-quick-start.bat` | 快速启动已有镜像 |
| `test-deployment.bat` | 自动化健康检查 |

---

## 🎉 总结

通过本部署方案，你可以：

✅ **快速部署** - 一键构建和启动所有应用服务  
✅ **灵活配置** - 通过环境变量适配不同环境  
✅ **避免冲突** - 使用9500+端口避免中间件冲突  
✅ **外部中间件** - 连接独立部署的基础服务  
✅ **健康监控** - 自动化健康检查和故障诊断  
✅ **生产就绪** - 包含性能优化和安全配置  

现在你的 Collide 应用已经可以在任何支持 Docker 的环境中稳定运行！🚀 