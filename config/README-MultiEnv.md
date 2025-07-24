# 🌍 Collide 多环境配置指南

## 📋 环境配置概览

Collide 项目支持三个环境的配置：

| 环境 | Profile | 端口配置 | 用途 |
|------|---------|----------|------|
| **开发环境** | `dev` | Gateway:9501, Auth:9502, Business:9503 | 本地开发调试 |
| **测试环境** | `test` | Gateway:8080, Auth:8081, Business:8082 | 集成测试 |
| **生产环境** | `prod` | Gateway:8080, Auth:8081, Business:8082 | 生产运行 |

## 🚀 快速启动

### 1. 选择环境方式

#### 方式一：环境变量（推荐）
```bash
# 设置环境变量
export SPRING_PROFILES_ACTIVE=dev

# 或在启动时指定
java -jar -Dspring.profiles.active=dev app.jar
```

#### 方式二：配置文件
复制对应环境的配置文件：
```bash
# 开发环境
cp config/env/dev.env.example .env

# 测试环境  
cp config/env/test.env.example .env

# 生产环境
cp config/env/prod.env.example .env
```

### 2. 启动服务

```bash
# 启动网关服务
cd collide-gateway
mvn spring-boot:run

# 启动认证服务
cd collide-auth  
mvn spring-boot:run

# 启动业务服务
cd collide-application/collide-app
mvn spring-boot:run
```

## 📁 配置文件结构

```
collide/
├── config/
│   ├── env/                          # 环境变量模板
│   │   ├── dev.env.example          # 开发环境模板
│   │   ├── test.env.example         # 测试环境模板
│   │   └── prod.env.example         # 生产环境模板
│   └── README-MultiEnv.md           # 本文档
├── collide-common/                   # 公共配置模块
│   ├── collide-base/
│   │   └── src/main/resources/base.yml      # 基础配置（多环境）
│   ├── collide-cache/
│   │   └── src/main/resources/cache.yml     # 缓存配置（多环境）
│   ├── collide-config/
│   │   └── src/main/resources/config.yml    # Nacos配置
│   ├── collide-datasource/
│   │   └── src/main/resources/datasource.yml  # 数据源配置（多环境）
│   └── collide-rpc/
│       └── src/main/resources/rpc.yml       # RPC配置
├── collide-gateway/
│   └── src/main/resources/
│       ├── application.yml          # 网关配置（多环境）
│       └── bootstrap.yml            # 网关启动配置
├── collide-auth/
│   └── src/main/resources/
│       ├── application.yml          # 认证配置（多环境）
│       └── bootstrap.yml            # 认证启动配置
└── collide-application/collide-app/
    └── src/main/resources/
        ├── application.yml          # 业务配置（多环境）
        └── bootstrap.yml            # 业务启动配置
```

## ⚙️ 环境特性对比

### 开发环境 (dev)
- **数据库**: `collide_dev` - 开发数据库
- **Redis**: database=0 - 独立缓存空间  
- **Sa-Token**: 2小时过期，允许多地登录
- **日志级别**: DEBUG - 详细调试信息
- **Swagger**: 启用 - API文档可访问
- **连接池**: 小规模配置 (5-20连接)
- **限流**: 宽松 - 便于调试

### 测试环境 (test)  
- **数据库**: `collide_test` - 测试数据库
- **Redis**: database=1 - 测试缓存空间
- **Sa-Token**: 1天过期，不允许多地登录
- **日志级别**: INFO - 常规信息
- **Swagger**: 启用 - 便于接口测试
- **连接池**: 中等配置 (10-50连接)
- **监控**: 启用Druid监控

### 生产环境 (prod)
- **数据库**: `collide_prod` - 生产数据库  
- **Redis**: database=0 - 生产缓存
- **Sa-Token**: 30天过期，严格单点登录
- **日志级别**: WARN - 只记录警告和错误
- **Swagger**: 禁用 - 安全考虑
- **连接池**: 大规模配置 (50-200连接)
- **监控**: 完整监控体系

## 🔧 重要配置说明

### 1. 数据库配置
```yaml
# 开发环境
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/collide_dev
    
# 测试环境  
spring:
  datasource:
    url: jdbc:mysql://test-mysql.collide.com:3306/collide_test
    
# 生产环境
spring:
  datasource:
    url: jdbc:mysql://prod-mysql.collide.com:3306/collide_prod
```

### 2. 缓存配置
```yaml
# 不同环境使用不同的缓存前缀和过期时间
jetcache:
  remote:
    default:
      keyPrefix: ${spring.application.name}:${spring.profiles.active}
      defaultExpireInMillis: 
        dev: 180000    # 3分钟
        test: 600000   # 10分钟  
        prod: 1800000  # 30分钟
```

### 3. 服务发现配置
```yaml
# 不同环境使用不同的Nacos命名空间
dubbo:
  registry:
    parameters:
      namespace: 
        dev: "dev"
        test: "test"  
        prod: "prod"
```

## 🔒 安全注意事项

### 1. 敏感信息管理
- **开发环境**: 可以使用明文配置
- **测试环境**: 建议使用占位符 + 环境变量
- **生产环境**: 必须使用密钥管理系统 (如HashiCorp Vault)

### 2. 生产环境密码安全
```bash
# 不要在配置文件中明文存储密码
MYSQL_PASSWORD=${MYSQL_PROD_PASSWORD}
REDIS_PASSWORD=${REDIS_PROD_PASSWORD}
NACOS_PASSWORD=${NACOS_PROD_PASSWORD}
```

### 3. 网络安全
- 生产环境限制Druid监控访问IP
- 禁用不必要的管理端点
- 使用HTTPS传输

## 🐳 Docker部署

### 1. Docker Compose 环境变量
```yaml
# docker-compose.yml
services:
  collide-gateway:
    environment:
      - SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
      - MYSQL_HOST=${MYSQL_HOST}
      - REDIS_HOST=${REDIS_HOST}
```

### 2. Kubernetes 配置
```yaml  
# k8s-deployment.yaml
env:
- name: SPRING_PROFILES_ACTIVE
  value: "prod"
- name: MYSQL_PASSWORD
  valueFrom:
    secretKeyRef:
      name: mysql-secret
      key: password
```

## 📊 监控和日志

### 1. 日志配置
- **开发环境**: 控制台输出 + 本地文件
- **测试环境**: 文件 + ELK收集  
- **生产环境**: 文件 + 集中式日志系统

### 2. 监控端点
- **开发环境**: 暴露所有端点
- **测试环境**: 暴露基础监控端点
- **生产环境**: 只暴露必要端点，需要认证

## 🚨 故障排查

### 1. 环境变量不生效
```bash
# 检查环境变量是否设置
echo $SPRING_PROFILES_ACTIVE

# 检查JVM参数
jps -v | grep spring.profiles.active
```

### 2. 配置文件加载问题
```bash
# 启动时添加调试参数
java -jar -Dspring.profiles.active=dev -Ddebug=true app.jar
```

### 3. 数据库连接问题
- 检查数据库地址和端口
- 验证用户名密码
- 确认数据库是否存在

### 4. 服务注册问题  
- 检查Nacos地址和命名空间
- 验证Nacos认证信息
- 确认网络连通性

## 📞 获取帮助

如有问题，请参考：
- [项目主文档](../README.md)
- [API设计规范](../Document/api-design.md)
- [部署文档](../DOCKER-DEPLOYMENT.md)

---

**⚡ 快速提示**: 建议开发时使用 `dev` 环境，测试部署使用 `test` 环境，生产部署使用 `prod` 环境。 