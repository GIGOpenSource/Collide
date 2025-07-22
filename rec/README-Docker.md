# 🐳 Collide 项目 Docker 部署文件总览

## 📁 文件结构

```
Collide/
├── 🐳 Docker 相关文件
│   ├── docker-compose.yml               # Docker编排文件 (主文件)
│   ├── collide-gateway/Dockerfile       # 网关服务镜像
│   ├── collide-auth/Dockerfile          # 认证服务镜像  
│   ├── collide-application/Dockerfile   # 应用服务镜像
│   ├── config/rocketmq/broker.conf      # RocketMQ配置
│   └── sql/01-init-database.sql         # 数据库初始化脚本
│
├── 🚀 启动脚本
│   ├── quick-start.sh                   # Linux/Mac快速启动脚本
│   └── quick-start.bat                  # Windows快速启动脚本
│
└── 📖 文档
    ├── docker-deployment-guide.md       # 详细部署指南
    └── README-Docker.md                 # 本文件
```

## 🎯 快速开始

### 方法1: 使用快速启动脚本 (推荐)

**Linux/Mac:**
```bash
chmod +x quick-start.sh
./quick-start.sh
```

**Windows:**
```cmd
quick-start.bat
```

### 方法2: 手动启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

## 📋 服务清单

### 🏗️ 基础设施服务

| 服务 | 端口 | 说明 | 访问地址 |
|------|------|------|----------|
| MySQL | 3306 | 数据库 | localhost:3306 |
| Redis | 6379 | 缓存 | localhost:6379 |
| Nacos | 8848 | 配置中心 | http://localhost:8848/nacos |
| RocketMQ NameServer | 9876 | 消息队列 | - |
| RocketMQ Broker | 10911 | 消息队列 | - |
| RocketMQ Console | 19876 | 消息队列控制台 | http://localhost:19876 |

### 🚀 应用服务

| 服务 | 端口 | 说明 | 访问地址 |
|------|------|------|----------|
| Gateway | 8081 | 网关服务 | http://localhost:8081 |
| Auth | 8082 | 认证服务 | http://localhost:8082 |
| Application | 8085 | 应用服务 | http://localhost:8085 |

## 🛠️ 常用命令

### 启动和停止

```bash
# 启动所有服务
docker-compose up -d

# 启动指定服务
docker-compose up -d mysql redis

# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

### 查看状态和日志

```bash
# 查看服务状态
docker-compose ps

# 查看所有日志
docker-compose logs -f

# 查看指定服务日志
docker-compose logs -f collide-application

# 查看最近100行日志
docker-compose logs --tail=100 collide-gateway
```

### 重启和重建

```bash
# 重启指定服务
docker-compose restart collide-application

# 重建并重启
docker-compose up -d --build collide-application

# 扩容服务实例
docker-compose up -d --scale collide-application=3
```

## 🔧 配置说明

### 数据库配置

- **数据库名**: collide
- **用户名**: collide  
- **密码**: collide123
- **Root密码**: root123

### Redis配置

- **密码**: redis123
- **持久化**: 已启用AOF

### Nacos配置

- **用户名**: nacos
- **密码**: nacos
- **数据库**: 使用MySQL存储

## 🎛️ 环境变量

服务通过环境变量进行配置，主要变量：

```bash
# 数据库
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/collide...
SPRING_DATASOURCE_USERNAME=collide
SPRING_DATASOURCE_PASSWORD=collide123

# Redis
SPRING_REDIS_HOST=redis
SPRING_REDIS_PASSWORD=redis123

# Nacos
SPRING_CLOUD_NACOS_DISCOVERY_SERVER_ADDR=nacos:8848

# RocketMQ
ROCKETMQ_NAME_SERVER=rocketmq-namesrv:9876
```

## 🔍 故障排查

### 1. 服务启动失败

```bash
# 查看服务状态
docker-compose ps

# 查看错误日志
docker-compose logs [service-name]

# 进入容器调试
docker-compose exec [service-name] bash
```

### 2. 端口冲突

检查并释放被占用的端口：
```bash
# Linux/Mac
netstat -tulpn | grep [port]
lsof -i :[port]

# Windows
netstat -ano | findstr [port]
```

### 3. 内存不足

```bash
# 查看资源使用
docker stats

# 调整JVM内存 (修改docker-compose.yml)
environment:
  - JAVA_OPTS=-Xmx512m -Xms256m
```

## 📈 性能优化

### JVM调优

在 `docker-compose.yml` 中调整JVM参数：

```yaml
environment:
  - JAVA_OPTS=-Xmx1024m -Xms512m -XX:+UseG1GC
```

### 数据库优化

```yaml
mysql:
  command: >
    --innodb-buffer-pool-size=512M
    --max-connections=1000
```

### Redis优化

```yaml
redis:
  command: >
    redis-server --maxmemory 256mb --maxmemory-policy allkeys-lru
```

## 🔐 安全配置

### 生产环境建议

1. **修改默认密码**
2. **仅暴露必要端口**
3. **启用TLS/SSL**
4. **配置防火墙规则**
5. **定期更新镜像**

### 数据备份

```bash
# 备份MySQL数据
docker-compose exec mysql mysqldump -u root -proot123 collide > backup.sql

# 备份数据卷
docker run --rm -v collide_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```

## 🎯 下一步

1. **阅读详细文档**: [docker-deployment-guide.md](docker-deployment-guide.md)
2. **配置监控**: 添加Prometheus + Grafana
3. **CI/CD集成**: 配置自动化部署
4. **负载均衡**: 配置Nginx反向代理
5. **高可用部署**: 多实例部署

## 🆘 获取帮助

- 📖 查看详细部署指南: `docker-deployment-guide.md`
- 🐳 Docker官方文档: https://docs.docker.com/
- ☸️ Docker Compose文档: https://docs.docker.com/compose/
- 🔧 故障排查: 使用 `docker-compose logs -f [service]` 查看日志

---

**快速开始您的Collide项目之旅！** 🚀 