# Collide Docker 部署指南

## 🐳 概述

本指南提供了使用 Docker 部署 Collide 项目三个核心服务的完整方案：
- **collide-gateway** (端口: 9501) - API网关服务
- **collide-auth** (端口: 9502) - 认证服务  
- **collide-application** (端口: 9503) - 业务应用服务

## 🌟 特性

- ✅ **Host 网络模式**：直接访问宿主机的中间件服务
- ✅ **无中间件容器**：Redis、MySQL、Nacos 等使用宿主机服务
- ✅ **健康检查**：自动检测服务启动状态
- ✅ **日志管理**：统一日志收集和挂载
- ✅ **跨平台**：支持 Linux/Mac 和 Windows

## 📁 文件结构

```
Collide/
├── docker-compose.yml              # Docker Compose 配置文件
├── config/
│   └── docker/
│       ├── application-gateway-host.yml  # Gateway服务专用配置
│       ├── application-auth-host.yml     # Auth服务专用配置
│       ├── application-app-host.yml      # Application服务专用配置
│       ├── bootstrap-host.yml            # Bootstrap配置（Nacos）
│       └── README.md                     # 配置文件说明
├── scripts/
│   ├── docker-deploy.sh           # Linux/Mac 部署脚本
│   └── docker-deploy.bat          # Windows 部署脚本
├── logs/                          # 日志目录（自动创建）
│   ├── gateway/
│   ├── auth/
│   └── application/
└── docker.env.example            # 环境变量配置示例
```

## 🚀 快速开始

### 前置要求

#### 🖥️ 系统要求
- **Docker**: 20.10+ 
- **Docker Compose**: 2.0+ （或 docker-compose 1.29+）
- **Java**: 21+
- **Maven**: 3.8+

#### 🔧 中间件要求（需在宿主机运行）
- **MySQL**: 3306 端口（必需）
- **Redis**: 6379 端口（必需）  
- **Nacos**: 8848 端口（推荐，用于服务发现和配置管理）
- **Elasticsearch**: 9200 端口（可选，用于搜索功能）
- **XXL-Job**: 8081 端口（可选，用于定时任务）
- **Sentinel**: 8888 端口（可选，用于流量控制）
- **RocketMQ**: 9876 端口（可选，用于消息队列）

### 第一次部署

#### 1️⃣ 准备配置文件
```bash
# 复制环境变量配置文件
cp docker.env.example docker.env

# 根据实际环境修改配置
vim docker.env
```

#### 2️⃣ 启动中间件服务
确保以下核心服务在宿主机运行：
```bash
# 启动 MySQL（必需）
sudo systemctl start mysql

# 启动 Redis（必需）
sudo systemctl start redis

# 启动 Nacos（推荐）
sh startup.sh -m standalone

# 可选中间件服务
# 启动 Elasticsearch
sudo systemctl start elasticsearch

# 启动 XXL-Job
# 下载并启动 xxl-job-admin

# 启动 RocketMQ
sh mqnamesrv &
sh mqbroker -n localhost:9876 &
```

#### 3️⃣ 执行部署

**Linux/Mac:**
```bash
# 设置执行权限
chmod +x scripts/docker-deploy.sh

# 完整部署
./scripts/docker-deploy.sh

# 仅构建镜像
./scripts/docker-deploy.sh --build-only

# 部署但不等待健康检查
./scripts/docker-deploy.sh --no-wait
```

**Windows:**
```batch
REM 完整部署
scripts\docker-deploy.bat

REM 仅构建镜像  
scripts\docker-deploy.bat --build-only

REM 部署但不等待健康检查
scripts\docker-deploy.bat --no-wait
```

## 📊 服务管理

### 查看服务状态
```bash
# 查看所有服务状态
docker-compose ps

# 查看服务详细信息
docker-compose ps --services
```

### 查看日志
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs collide-gateway
docker-compose logs collide-auth  
docker-compose logs collide-application

# 实时跟踪日志
docker-compose logs -f collide-gateway
```

### 重启服务
```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart collide-gateway
docker-compose restart collide-auth
docker-compose restart collide-application
```

### 停止服务
```bash
# 停止所有服务
docker-compose down

# 停止并删除容器和网络
docker-compose down --remove-orphans

# 停止并删除所有相关资源（包括镜像）
docker-compose down --rmi all --volumes --remove-orphans
```

## 🔧 配置说明

### ⚠️ 重要：配置文件一致性

**确保以下配置文件的配置保持一致，否则服务将无法正常启动：**

1. **`collide-common/collide-base/src/main/resources/base.yml`** - 基础配置文件
2. **`docker.env.example`** - Docker环境变量模板  
3. **`config/docker/bootstrap-host.yml`** - Docker Bootstrap配置（Nacos）
4. **服务专用配置文件（根据需求选择）**：
   - **`config/docker/application-gateway-host.yml`** - Gateway服务专用配置
   - **`config/docker/application-auth-host.yml`** - Auth服务专用配置  
   - **`config/docker/application-app-host.yml`** - Application服务专用配置

所有中间件的连接信息（数据库、Redis、Nacos等）必须在这些文件中保持一致。

**特别注意**：
- `bootstrap-host.yml` 用于应用启动早期的配置，特别是 Nacos 配置中心的连接配置，它会在应用启动时首先加载。
- 各服务根据自身的 `spring.config.import` 配置加载不同的配置文件组合。
- **新的配置结构**：每个服务使用专门的配置文件，避免不必要的配置污染。

### Docker Compose 配置

```yaml
# docker-compose.yml 关键配置说明 - 各服务使用专门配置
services:
  collide-gateway:
    network_mode: host          # 使用宿主机网络
    environment:
      - SERVER_PORT=9501        # 服务端口
      - SPRING_PROFILES_ACTIVE=host  # 配置Profile
      - SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/application-gateway-host.yml  # Gateway专用配置
      - SPRING_CLOUD_BOOTSTRAP_LOCATION=file:/app/config/bootstrap-host.yml      # Bootstrap配置文件
    volumes:
      - ./logs/gateway:/app/logs     # 日志挂载
      - ./config/docker:/app/config # 配置挂载

  collide-auth:
    environment:
      - SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/application-auth-host.yml     # Auth专用配置

  collide-application:
    environment:
      - SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/application-app-host.yml      # Application专用配置
```

### 环境变量配置

在 `docker.env` 文件中配置：
```bash
# 数据库配置
MYSQL_DATABASE=collide
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456

# Redis配置
REDIS_PASSWORD=123456

# Nacos配置
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos

# Elasticsearch配置
ELASTICSEARCH_ENABLE=true
ELASTICSEARCH_URL=localhost:9200
ELASTICSEARCH_USERNAME=elastic
ELASTICSEARCH_PASSWORD=123456

# XXL-Job配置
XXL_JOB_URL=localhost:8081
XXL_JOB_APP_NAME=xxl-job-executor
XXL_JOB_ACCESS_TOKEN=default_token

# Sentinel配置
SENTINEL_URL=localhost
SENTINEL_PORT=8888

# RocketMQ配置
ROCKETMQ_URL=localhost:9876

# 服务端口
GATEWAY_PORT=9501
AUTH_PORT=9502  
APPLICATION_PORT=9503
```

### Host 网络模式配置

#### 📋 服务专用配置文件

根据各服务的 `spring.config.import` 需求，现在使用专门的配置文件：

**Gateway服务** (`config/docker/application-gateway-host.yml`)：
```yaml
# 仅包含Gateway需要的基础配置
spring:
  cloud:
    nacos:
      discovery:
        server-addr: ${NACOS_URL:localhost:8848}
collide:
  turbo:
    nacos:
      server:
        url: ${NACOS_URL:localhost:8848}
    redis:  # Gateway可能用于Sa-Token
      url: redis://${REDIS_HOST:localhost}
```

**Auth服务** (`config/docker/application-auth-host.yml`)：
```yaml
# 包含Auth需要的RPC和缓存配置，但不包含数据源
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
  redis:
    redisson:
      config: |  # Redisson配置
dubbo:
  registry:
    address: nacos://${NACOS_URL:localhost:8848}
jetcache:  # JetCache缓存配置
  local:
    default:
      type: caffeine
```

**Application服务** (`config/docker/application-app-host.yml`)：
```yaml
# 完整配置，包含数据源、RPC、缓存、Prometheus等
spring:
  autoconfigure:
    exclude: org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration
  datasource:
    url: jdbc:mysql://localhost:3306/collide
    type: com.alibaba.druid.pool.DruidDataSource  # 使用Druid连接池
  data:
    redis:
      host: localhost
dubbo:
  registry:
    address: nacos://${NACOS_URL:localhost:8848}
mybatis:  # MyBatis配置
  configuration:
    map-underscore-to-camel-case: true
  mapper-locations: classpath:mapper/*.xml
```

#### Bootstrap配置文件 (`config/docker/bootstrap-host.yml`)：
```yaml
spring:
  cloud:
    nacos:
      username: ${NACOS_USERNAME:nacos}
      password: ${NACOS_PASSWORD:nacos}
      discovery:
        server-addr: ${NACOS_URL:localhost:8848}
      config:
        server-addr: ${NACOS_URL:localhost:8848}
```

**注意**：Bootstrap配置在应用启动时首先加载，确保 Nacos 配置中心能够正常连接。

**重要改进**：现在使用服务专用配置文件，解决了以下问题：
- ✅ **配置精确匹配**：每个服务只包含其 `spring.config.import` 所需的配置
- ✅ **避免配置污染**：Gateway不再包含不需要的数据源配置
- ✅ **减少资源浪费**：Auth服务不再初始化数据源连接池
- ✅ **配置职责分离**：各服务配置文件职责明确，便于维护
- ✅ **启动优化**：避免不必要的组件初始化，提升启动速度

**配置文件功能对比**：
| 服务 | Gateway | Auth | Application |
|------|---------|------|-------------|
| 基础配置(base.yml) | ✅ | ✅ | ✅ |
| Nacos配置(config.yml) | ✅ | ✅ | ✅ |
| 数据源(datasource.yml) | ❌ | ❌ | ✅ |
| RPC配置(rpc.yml) | ❌ | ✅ | ✅ |
| 缓存配置(cache.yml) | ❌ | ✅ | ✅ |
| 监控配置(prometheus.yml) | ❌ | ❌ | ✅ |

### 配置文件导入详解

各个应用的配置导入情况如下：

#### Gateway 应用
```yaml
spring:
  config:
    import: classpath:base.yml,classpath:config.yml
```

#### Auth 应用  
```yaml
spring:
  config:
    import: classpath:base.yml,classpath:config.yml,classpath:rpc.yml,classpath:cache.yml
```

#### Application 应用
```yaml
spring:
  config:
    import: classpath:base.yml,classpath:config.yml,classpath:datasource.yml,classpath:rpc.yml,classpath:cache.yml,classpath:prometheus.yml
```

#### 配置文件说明
- **`base.yml`**: 基础配置，包含所有中间件的连接信息
- **`config.yml`**: Nacos 配置中心设置
- **`datasource.yml`**: 数据库连接池配置  
- **`rpc.yml`**: Dubbo RPC 配置
- **`cache.yml`**: Redis 缓存配置
- **`prometheus.yml`**: 监控和指标配置

## 🔍 健康检查

### 服务健康端点
```bash
# Gateway 健康检查
curl http://localhost:9501/actuator/health

# Auth 健康检查  
curl http://localhost:9502/actuator/health

# Application 健康检查
curl http://localhost:9503/actuator/health
```

### 容器健康状态
```bash
# 查看容器健康状态
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 查看健康检查历史
docker inspect collide-gateway --format='{{range .State.Health.Log}}{{.Output}}{{end}}'
```

## 🛠️ 故障排查

### 常见问题

#### 问题1：容器启动失败
**现象**: 容器启动后立即退出
**排查**:
```bash
# 查看容器日志
docker-compose logs collide-gateway

# 查看容器退出状态
docker ps -a
```

#### 问题2：无法连接中间件
**现象**: 应用日志显示连接超时
**解决**:
1. 检查宿主机中间件服务状态
2. 确认端口和配置正确
3. 检查防火墙设置

#### 问题5：Nacos配置中心连接失败
**现象**: 应用启动时报 Nacos 连接异常
**排查**:
```bash
# 检查Nacos服务状态
curl http://localhost:8848/nacos/v1/ns/operator/metrics

# 检查bootstrap配置是否正确加载
docker-compose logs collide-gateway | grep -i nacos

# 验证环境变量是否正确传递
docker exec collide-gateway env | grep NACOS
```

#### 问题3：端口冲突
**现象**: 端口已被占用错误
**解决**:
```bash
# 查看端口占用
netstat -tulpn | grep :9501

# 修改 docker-compose.yml 中的端口配置
```

#### 问题4：健康检查失败
**现象**: 健康检查一直失败
**排查**:
```bash
# 手动测试健康端点
curl -v http://localhost:9501/actuator/health

# 检查容器内网络
docker exec collide-gateway curl localhost:9501/actuator/health
```

### 调试技巧

#### 进入容器调试
```bash
# 进入运行中的容器
docker exec -it collide-gateway /bin/sh

# 查看容器内进程
docker exec collide-gateway ps aux

# 查看容器内端口
docker exec collide-gateway netstat -tulpn
```

#### 查看详细信息
```bash
# 查看镜像构建历史
docker history collide/gateway:latest

# 查看容器配置
docker inspect collide-gateway

# 查看资源使用情况
docker stats collide-gateway collide-auth collide-application
```

## 📈 监控和运维

### 资源监控
```bash
# 查看容器资源使用
docker stats --no-stream

# 查看系统资源
htop
df -h
free -h
```

### 日志管理
```bash
# 清理旧日志（谨慎操作）
docker system prune -f

# 查看日志文件大小
du -sh logs/*

# 压缩历史日志
tar -czf logs-backup-$(date +%Y%m%d).tar.gz logs/
```

### 备份和恢复
```bash
# 导出镜像
docker save collide/gateway:latest | gzip > collide-gateway.tar.gz

# 导入镜像
gunzip -c collide-gateway.tar.gz | docker load

# 备份配置
tar -czf config-backup.tar.gz config/ docker-compose.yml
```

## 🔐 安全建议

1. **用户权限**: 容器内使用非root用户运行
2. **网络隔离**: 虽然使用host网络，注意防火墙配置
3. **密码管理**: 使用环境变量管理敏感信息
4. **镜像安全**: 定期更新基础镜像
5. **日志安全**: 避免在日志中记录敏感信息

## 🎯 最佳实践

1. **资源限制**: 在生产环境设置合适的内存和CPU限制
2. **健康检查**: 配置合理的健康检查间隔和超时
3. **日志轮转**: 配置日志轮转避免磁盘占满
4. **监控告警**: 集成监控系统，设置告警规则
5. **版本管理**: 使用具体的镜像版本而非latest

## 📚 相关资源

- [Docker 官方文档](https://docs.docker.com/)
- [Docker Compose 参考](https://docs.docker.com/compose/)
- [Spring Boot Docker 指南](https://spring.io/guides/gs/spring-boot-docker/)
- [项目架构规范](README.md)

---

**🐳 享受 Docker 容器化部署的便利！如有问题，请查看日志或联系开发团队。** 