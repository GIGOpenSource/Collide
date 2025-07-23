# 🔧 Collide 环境变量配置指南

## 📋 概述

Collide 项目完全支持通过**环境变量**来动态配置中间件连接信息，无需修改代码中的 `.yml` 配置文件。你可以轻松地在本地开发、测试环境、生产环境之间切换配置。

## 🏗️ 配置原理

### Spring Boot 环境变量机制

Spring Boot 使用 `${变量名:默认值}` 语法来支持环境变量：

```yaml
# application-docker.yml 中的配置
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:collide_db}
    username: ${MYSQL_USERNAME:collide}
    password: ${MYSQL_PASSWORD:your_password}
```

### 配置优先级（从高到低）

1. **环境变量** (`.env` 文件或系统环境变量)
2. **命令行参数** (`-D` 参数)
3. **配置文件** (`.yml` 文件中的默认值)

## 🚀 快速配置

### 1. 创建配置文件

```bash
# 复制配置模板
cp env.example .env

# 编辑配置文件
nano .env  # 或使用其他编辑器
```

### 2. 基础配置示例

#### 🏠 本地开发配置

```bash
# .env 文件内容
# 中间件在本机运行
NACOS_SERVER_ADDR=localhost:8848
MYSQL_HOST=localhost
MYSQL_PORT=3306
REDIS_HOST=localhost
REDIS_PORT=6379
```

#### 🏢 生产环境配置

```bash
# .env 文件内容  
# 中间件在远程服务器
NACOS_SERVER_ADDR=192.168.1.100:8848
MYSQL_HOST=192.168.1.101
MYSQL_PORT=3306
MYSQL_DATABASE=collide_db
MYSQL_USERNAME=collide
MYSQL_PASSWORD=SecurePassword123

REDIS_HOST=192.168.1.102
REDIS_PORT=6379
REDIS_PASSWORD=RedisPassword123
```

### 3. 启动应用

```bash
# 环境变量会自动生效
docker-compose up -d
```

## 📊 完整配置参数

### 🌐 核心中间件配置

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `NACOS_SERVER_ADDR` | `localhost:8848` | Nacos服务地址 |
| `NACOS_SERVER_USERNAME` | `nacos` | Nacos用户名 |
| `NACOS_SERVER_PASSWORD` | `nacos` | Nacos密码 |
| `NACOS_NAMESPACE` | ` ` | Nacos命名空间 |
| `MYSQL_HOST` | `localhost` | MySQL主机地址 |
| `MYSQL_PORT` | `3306` | MySQL端口 |
| `MYSQL_DATABASE` | `collide_db` | 数据库名 |
| `MYSQL_USERNAME` | `collide` | 数据库用户名 |
| `MYSQL_PASSWORD` | `collide123456` | 数据库密码 |
| `REDIS_HOST` | `localhost` | Redis主机地址 |
| `REDIS_PORT` | `6379` | Redis端口 |
| `REDIS_PASSWORD` | ` ` | Redis密码（无密码留空） |

### 🔧 高级配置参数

| 环境变量 | 默认值 | 说明 |
|----------|--------|------|
| `DB_POOL_MIN` | `5` | 数据库连接池最小连接数 |
| `DB_POOL_MAX` | `15` | 数据库连接池最大连接数 |
| `REDIS_POOL_MAX_ACTIVE` | `15` | Redis连接池最大活跃连接 |
| `DUBBO_TIMEOUT` | `10000` | Dubbo调用超时时间(ms) |
| `LOG_LEVEL_ROOT` | `INFO` | 根日志级别 |
| `LOG_LEVEL_APP` | `DEBUG` | 应用日志级别 |

## 🌍 多环境配置方案

### 方案一：不同 .env 文件

```bash
# 开发环境
cp env.dev .env

# 测试环境  
cp env.test .env

# 生产环境
cp env.prod .env
```

### 方案二：环境变量脚本

**开发环境启动脚本 (start-dev.bat):**

```bash
@echo off
set NACOS_SERVER_ADDR=localhost:8848
set MYSQL_HOST=localhost
set REDIS_HOST=localhost
docker-compose up -d
```

**生产环境启动脚本 (start-prod.bat):**

```bash
@echo off
set NACOS_SERVER_ADDR=prod-nacos:8848
set MYSQL_HOST=prod-mysql
set REDIS_HOST=prod-redis
docker-compose up -d
```

### 方案三：Docker Compose 覆盖

**docker-compose.override.yml (本地开发):**

```yaml
version: '3.8'
services:
  collide-auth:
    environment:
      MYSQL_HOST: localhost
      REDIS_HOST: localhost
      NACOS_SERVER_ADDR: localhost:8848
```

## 💡 实际使用场景

### 场景1：从本地切换到测试环境

```bash
# 修改 .env 文件
sed -i 's/localhost/test-server/g' .env

# 重启服务
docker-compose down && docker-compose up -d
```

### 场景2：动态修改数据库连接

```bash
# 临时使用不同的数据库
export MYSQL_HOST=backup-mysql
export MYSQL_DATABASE=collide_backup
docker-compose restart
```

### 场景3：调试时修改日志级别

```bash
# 开启详细日志
export LOG_LEVEL_ROOT=DEBUG
export LOG_LEVEL_MYBATIS=DEBUG
docker-compose restart collide-application
```

## 🔍 配置验证

### 1. 检查环境变量是否生效

```bash
# 查看容器内的环境变量
docker exec collide-auth env | grep MYSQL

# 查看应用启动日志
docker-compose logs collide-auth | grep -i "datasource\|mysql"
```

### 2. 验证中间件连接

```bash
# 运行健康检查
curl http://localhost:8081/actuator/health

# 查看详细健康信息
curl http://localhost:8081/actuator/health | jq .
```

### 3. 测试配置变更

```bash
# 修改Redis主机
export REDIS_HOST=new-redis-host

# 重启服务验证
docker-compose restart collide-gateway
docker-compose logs collide-gateway | grep -i redis
```

## 🚨 故障排除

### 问题1：环境变量没有生效

**可能原因：**
- `.env` 文件位于错误位置
- 环境变量名称拼写错误
- Docker Compose 没有重新加载配置

**解决方案：**
```bash
# 确认 .env 文件位置
ls -la .env

# 重新构建并启动
docker-compose down
docker-compose up -d --force-recreate
```

### 问题2：中间件连接失败

**可能原因：**
- 中间件服务未启动
- 网络不通
- 认证信息错误

**解决方案：**
```bash
# 测试网络连通性
ping $MYSQL_HOST
telnet $REDIS_HOST $REDIS_PORT

# 检查配置
docker exec collide-auth env | grep -E "(MYSQL|REDIS|NACOS)"
```

### 问题3：配置覆盖不生效

**可能原因：**
- 配置文件中没有对应的环境变量占位符
- 环境变量值格式错误

**解决方案：**
```bash
# 检查yml文件中的占位符
grep -r "\${" collide-*/src/main/resources/

# 验证环境变量格式
echo $NACOS_SERVER_ADDR  # 应该是 host:port 格式
```

## 📚 参考资料

### Spring Boot 外部化配置

- [Spring Boot 外部化配置官方文档](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Docker Compose 环境变量](https://docs.docker.com/compose/environment-variables/)

### 配置文件位置

| 服务 | 配置文件路径 |
|------|-------------|
| 网关 | `collide-gateway/src/main/resources/application-docker.yml` |
| 认证 | `collide-auth/src/main/resources/application-docker.yml` |
| 业务 | `collide-application/collide-app/src/main/resources/application-docker.yml` |

---

## 🎉 总结

通过环境变量配置，你可以：

✅ **灵活切换环境** - 本地开发、测试、生产环境一键切换  
✅ **无需修改代码** - 所有配置都在 `.env` 文件中管理  
✅ **支持动态调整** - 运行时修改配置并重启服务  
✅ **配置集中管理** - 所有环境变量在一个文件中  
✅ **安全配置** - 敏感信息不会提交到代码库  

现在你可以根据实际需求，灵活配置你的 Collide 应用服务了！🚀 