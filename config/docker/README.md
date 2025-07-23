# Docker 配置文件说明

## 📁 文件结构

```
config/docker/
├── bootstrap-host.yml           # 所有服务共用的Bootstrap配置（Nacos连接）
├── application-gateway-host.yml # Gateway服务专用配置
├── application-auth-host.yml    # Auth服务专用配置
├── application-app-host.yml     # Application服务专用配置
└── README.md                    # 本说明文件
```

## 🎯 配置文件设计原则

### 📋 各服务的 spring.config.import 需求分析

| 服务 | spring.config.import 配置 | 所需配置组件 |
|------|---------------------------|-------------|
| **Gateway** | `base.yml,config.yml` | 基础配置 + Nacos |
| **Auth** | `base.yml,config.yml,rpc.yml,cache.yml` | 基础配置 + Nacos + RPC + 缓存 |
| **Application** | `base.yml,config.yml,datasource.yml,rpc.yml,cache.yml,prometheus.yml` | 所有配置 |

### 🔧 配置文件功能对比

| 配置组件 | Gateway | Auth | Application | 说明 |
|----------|---------|------|-------------|------|
| 基础配置(base.yml) | ✅ | ✅ | ✅ | 中间件连接信息 |
| Nacos配置(config.yml) | ✅ | ✅ | ✅ | 服务发现和配置中心 |
| 数据源(datasource.yml) | ❌ | ❌ | ✅ | 数据库连接池和MyBatis |
| RPC配置(rpc.yml) | ❌ | ✅ | ✅ | Dubbo服务调用 |
| 缓存配置(cache.yml) | ❌ | ✅ | ✅ | Redis和JetCache |
| 监控配置(prometheus.yml) | ❌ | ❌ | ✅ | 性能监控指标 |

## 📝 各配置文件详解

### 🌟 bootstrap-host.yml (所有服务共用)
- **用途**: 应用启动早期配置，主要用于Nacos配置中心连接
- **加载时机**: Spring Boot应用启动时首先加载
- **关键配置**: Nacos服务地址、用户名、密码

### 🚪 application-gateway-host.yml (Gateway专用)
- **用途**: API网关服务配置
- **包含配置**: 
  - Nacos服务发现配置
  - 基础日志和监控端点配置
  - 可能的Sa-Token Redis配置
- **不包含**: 数据源、RPC、JetCache等配置

### 🔐 application-auth-host.yml (Auth专用)  
- **用途**: 认证服务配置
- **包含配置**:
  - Nacos服务发现配置
  - Redis缓存配置 (Redisson + JetCache)
  - Dubbo RPC配置
  - 基础日志和监控配置
- **不包含**: 数据源配置

### 📱 application-app-host.yml (Application专用)
- **用途**: 业务应用服务配置
- **包含配置**:
  - 完整的数据源配置 (Druid连接池 + MyBias)
  - Redis缓存配置 (Redisson + JetCache)
  - Dubbo RPC配置
  - RocketMQ消息队列配置
  - Prometheus监控配置
  - 日志和监控端点配置
- **特殊配置**: ShardingSphere自动配置排除

## ⚠️ 重要注意事项

### 1. 配置一致性
确保所有配置文件中的环境变量引用保持一致：
- `${MYSQL_HOST:localhost}` - 数据库主机
- `${REDIS_HOST:localhost}` - Redis主机  
- `${NACOS_URL:localhost:8848}` - Nacos地址
- 等等...

### 2. 环境变量覆盖
所有配置都支持通过环境变量覆盖，在 `docker.env` 或 `docker-compose.yml` 中设置。

### 3. 配置加载顺序
1. `bootstrap-host.yml` (首先加载)
2. `base.yml` (从classpath加载)
3. `config.yml` (从classpath加载)
4. 其他配置文件根据各服务的import配置加载
5. 对应的 `application-*-host.yml` (最后加载，可覆盖前面的配置)

### 4. 文件挂载
在Docker容器中，这些配置文件通过volume挂载到 `/app/config/` 目录，并通过环境变量指定加载路径：
```yaml
environment:
  - SPRING_CONFIG_ADDITIONAL_LOCATION=file:/app/config/application-gateway-host.yml
  - SPRING_CLOUD_BOOTSTRAP_LOCATION=file:/app/config/bootstrap-host.yml
```

## 🚀 最佳实践

1. **修改配置时**: 先修改对应的专用配置文件，然后重启相关服务
2. **添加新配置**: 根据服务的config.import需求，添加到对应的配置文件中  
3. **环境变量管理**: 在 `docker.env` 中统一管理环境变量
4. **配置验证**: 部署前检查各服务日志，确认配置正确加载

---

**📚 相关文档**: 详细的部署说明请参考 [DOCKER-DEPLOYMENT.md](../../DOCKER-DEPLOYMENT.md) 