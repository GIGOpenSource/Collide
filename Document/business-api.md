# 🎛️ Collide 业务聚合服务 API 文档

> **版本**: v1.0.0  
> **更新时间**: 2024-01-01  
> **负责团队**: Collide Team

## 📋 概述

业务聚合服务作为 Collide 社交平台的统一入口和管理中心，提供系统信息查询、服务状态监控、健康检查等功能。作为微服务架构中的聚合层，整合了用户、内容、社交、评论等多个业务模块。

### 🎯 核心功能

- **服务信息**: 提供系统版本、模块信息、技术栈详情
- **健康检查**: 实时监控各个组件的运行状态
- **API索引**: 提供所有业务模块的接口列表
- **系统监控**: 服务运行状态和性能指标展示
- **欢迎页面**: 平台介绍和快速导航
- **服务发现**: 微服务模块的注册和发现信息

### 🏗️ 技术特点

| 特性 | 说明 |
|------|------|
| **微服务聚合** | 统一入口，整合多个业务模块 |
| **服务发现** | 基于Nacos的动态服务发现 |
| **健康监控** | 实时检测各组件运行状态 |
| **负载均衡** | 智能路由和负载分发 |
| **链路追踪** | 分布式调用链路监控 |
| **统一认证** | 基于Sa-Token的统一身份验证 |

---

## 🚀 快速开始

### Base URL
```
https://api.collide.com
```

### 认证方式
- **公开接口**: 无需认证
- **管理接口**: 需要管理员权限

```http
Authorization: Bearer {admin-token}
```

### 通用响应格式
```json
{
  "status": "success",
  "message": "操作成功",
  "data": {...},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 📚 API 接口列表

### 1. 获取服务信息

**接口地址**: `GET /business/info`

**功能描述**: 获取聚合应用的基本信息和服务状态

#### 请求参数

无需参数

#### 响应示例

```json
{
  "application": "Collide Business Application",
  "version": "1.0.0",
  "description": "Collide社交平台业务聚合服务",
  "startTime": "2024-01-01T09:00:00Z",
  "modules": {
    "users": "用户管理服务 - 用户注册、认证、个人信息管理",
    "follow": "关注关系服务 - 用户关注、粉丝管理",
    "content": "内容服务 - 内容创作、发布、管理",
    "comment": "评论服务 - 评论发布、回复、管理",
    "like": "点赞服务 - 点赞、点踩互动功能",
    "favorite": "收藏服务 - 内容收藏、收藏夹管理"
  },
  "technology": {
    "framework": "Spring Boot 3.x",
    "cloud": "Spring Cloud with Nacos",
    "database": "MySQL with MyBatis Plus",
    "cache": "Redis",
    "architecture": "DDD + 去连表化设计"
  }
}
```

#### cURL 示例

```bash
curl -X GET "https://api.collide.com/business/info" \
  -H "Content-Type: application/json"
```

---

### 2. 健康检查

**接口地址**: `GET /business/health`

**功能描述**: 检查聚合应用和各个组件的健康状态

#### 请求参数

无需参数

#### 响应示例

**健康状态**:
```json
{
  "status": "UP",
  "timestamp": "2024-01-01T10:30:00Z",
  "components": {
    "spring-context": "UP",
    "database": "UP",
    "cache": "UP",
    "discovery": "UP"
  }
}
```

**异常状态**:
```json
{
  "status": "DOWN",
  "timestamp": "2024-01-01T10:30:00Z",
  "components": {
    "spring-context": "UP",
    "database": "DOWN",
    "cache": "UP",
    "discovery": "UP",
    "error": "Connection to database failed"
  }
}
```

---

### 3. 获取API端点列表

**接口地址**: `GET /business/endpoints`

**功能描述**: 获取所有可用业务模块的API端点信息

#### 请求参数

无需参数

#### 响应示例

```json
{
  "用户服务": {
    "注册": "POST /api/users/register",
    "登录": "POST /api/users/login",
    "用户信息": "GET /api/users/profile/{userId}"
  },
  "关注服务": {
    "关注用户": "POST /api/follow",
    "取消关注": "DELETE /api/follow",
    "关注列表": "GET /api/follow/following",
    "粉丝列表": "GET /api/follow/followers"
  },
  "内容服务": {
    "发布内容": "POST /api/content",
    "内容列表": "GET /api/content/list",
    "内容详情": "GET /api/content/{contentId}"
  },
  "评论服务": {
    "发表评论": "POST /api/comment",
    "评论列表": "GET /api/comment/list",
    "回复评论": "POST /api/comment/reply"
  },
  "点赞服务": {
    "点赞": "POST /api/like",
    "取消点赞": "DELETE /api/like",
    "点赞状态": "GET /api/like/status"
  },
  "收藏服务": {
    "收藏": "POST /api/favorite",
    "取消收藏": "DELETE /api/favorite",
    "收藏列表": "GET /api/favorite/list"
  }
}
```

---

### 4. 欢迎页面

**接口地址**: `GET /business/welcome`

**功能描述**: 获取平台欢迎信息和功能介绍

#### 请求参数

无需参数

#### 响应示例

```json
{
  "message": "欢迎使用 Collide 社交平台！",
  "description": "这是一个基于去连表化设计的高性能社交平台",
  "features": [
    "📱 用户管理 - 注册登录、个人资料",
    "👥 社交关系 - 关注、粉丝管理",
    "📝 内容创作 - 发布文字、图片、视频",
    "💬 互动评论 - 评论回复、嵌套评论",
    "👍 点赞互动 - 点赞点踩、情感表达",
    "⭐ 内容收藏 - 收藏管理、分类整理"
  ],
  "documentation": "/api/swagger-ui.html",
  "health": "/api/actuator/health"
}
```

---

### 5. 系统统计信息

**接口地址**: `GET /business/statistics`

**功能描述**: 获取系统运行统计信息（需要管理员权限）

#### 请求参数

无需参数

#### 请求头

```http
Authorization: Bearer {admin-token}
```

#### 响应示例

```json
{
  "systemInfo": {
    "uptime": "7 days, 12 hours, 30 minutes",
    "jvmMemory": {
      "used": "512MB",
      "max": "2GB",
      "usage": "25%"
    },
    "cpuUsage": "15%",
    "diskUsage": "45%"
  },
  "businessMetrics": {
    "totalUsers": 125648,
    "activeUsers": 8962,
    "totalPosts": 45230,
    "totalComments": 128956,
    "totalLikes": 892340
  },
  "serviceStatus": {
    "collide-users": "UP",
    "collide-content": "UP", 
    "collide-comment": "UP",
    "collide-like": "UP",
    "collide-follow": "UP"
  },
  "requestMetrics": {
    "totalRequests": 2156890,
    "requestsPerSecond": 125,
    "averageResponseTime": "85ms",
    "errorRate": "0.05%"
  }
}
```

---

## 📊 数据模型

### ServiceInfo 服务信息

```json
{
  "application": "String - 应用名称",
  "version": "String - 版本号",
  "description": "String - 应用描述",
  "startTime": "LocalDateTime - 启动时间",
  "modules": "Object - 业务模块信息",
  "technology": "Object - 技术栈信息"
}
```

### HealthStatus 健康状态

```json
{
  "status": "String - 总体状态: UP/DOWN",
  "timestamp": "LocalDateTime - 检查时间",
  "components": "Object - 各组件状态",
  "error": "String - 错误信息（可选）"
}
```

### SystemStatistics 系统统计

```json
{
  "systemInfo": "Object - 系统基础信息",
  "businessMetrics": "Object - 业务指标",
  "serviceStatus": "Object - 服务状态",
  "requestMetrics": "Object - 请求指标"
}
```

---

## 🚨 错误码说明

| 错误码 | HTTP状态码 | 说明 | 解决方案 |
|--------|-----------|------|----------|
| `SUCCESS` | 200 | 操作成功 | - |
| `INVALID_REQUEST` | 400 | 请求格式错误 | 检查请求格式 |
| `UNAUTHORIZED` | 401 | 未认证 | 提供有效的认证令牌 |
| `FORBIDDEN` | 403 | 权限不足 | 需要管理员权限 |
| `NOT_FOUND` | 404 | 资源不存在 | 检查请求路径 |
| `SERVICE_UNAVAILABLE` | 503 | 服务不可用 | 稍后重试或联系管理员 |
| `INTERNAL_ERROR` | 500 | 内部错误 | 联系技术支持 |

---

## 🛡️ 安全说明

### 权限控制
- **公开接口**: 服务信息、健康检查、欢迎页面等无需认证
- **管理接口**: 系统统计等敏感信息需要管理员权限
- **访问限制**: 基于IP白名单的访问控制

### 数据安全
- **敏感信息**: 隐藏内部配置和敏感参数
- **访问日志**: 记录所有接口访问日志
- **审计跟踪**: 管理操作的完整审计记录

---

## 📈 监控指标

### 系统指标

| 指标 | 说明 | 正常范围 |
|------|------|----------|
| **CPU使用率** | 系统CPU占用 | <80% |
| **内存使用率** | JVM内存占用 | <85% |
| **磁盘使用率** | 存储空间占用 | <90% |
| **响应时间** | 接口平均响应时间 | <100ms |

### 业务指标

| 指标 | 说明 | 监控方式 |
|------|------|----------|
| **活跃用户数** | 实时在线用户 | 实时统计 |
| **请求QPS** | 每秒请求数 | 实时监控 |
| **错误率** | 接口错误比例 | 实时告警 |
| **服务可用性** | 各模块可用状态 | 健康检查 |

---

## 💡 使用场景

### 1. 系统监控面板
```javascript
// 获取系统健康状态
const getSystemHealth = async () => {
  const response = await fetch('/business/health');
  const health = await response.json();
  updateHealthStatus(health);
};

// 定期检查（每30秒）
setInterval(getSystemHealth, 30000);
```

### 2. 服务发现
```javascript
// 获取可用的API端点
const getApiEndpoints = async () => {
  const response = await fetch('/business/endpoints');
  const endpoints = await response.json();
  renderApiDocumentation(endpoints);
};
```

### 3. 系统统计展示
```javascript
// 管理员获取系统统计（需要权限）
const getSystemStats = async (adminToken) => {
  const response = await fetch('/business/statistics', {
    headers: {
      'Authorization': `Bearer ${adminToken}`
    }
  });
  const stats = await response.json();
  renderDashboard(stats);
};
```

---

## 🔄 集成示例

### Spring Boot Actuator集成

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

### 服务发现集成

```java
@RestController
@RequestMapping("/business")
public class BusinessController {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @GetMapping("/services")
    public List<String> getAvailableServices() {
        return discoveryClient.getServices();
    }
}
```

### 监控集成

```java
@Component
public class SystemMetrics {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    @EventListener
    public void handleRequest(RequestEvent event) {
        Counter.builder("business.requests")
            .tag("method", event.getMethod())
            .register(meterRegistry)
            .increment();
    }
}
```

---

## 📋 运维管理

### 日志管理
```bash
# 查看业务日志
tail -f logs/business.log

# 查看错误日志
grep "ERROR" logs/business.log

# 查看访问日志
tail -f logs/access.log
```

### 性能调优
```bash
# JVM参数优化
-Xms1g -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200

# 连接池配置
spring.datasource.hikari.maximum-pool-size=20
spring.redis.lettuce.pool.max-active=20
```

### 部署配置
```yaml
# docker-compose.yml
version: '3.8'
services:
  collide-business:
    image: collide-business:latest
    ports:
      - "9502:9502"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9502/business/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

---

## 🔄 版本历史

| 版本 | 发布日期 | 更新内容 |
|------|----------|----------|
| **v1.0.0** | 2024-01-01 | 初始版本，基础服务信息和健康检查 |
| **v1.1.0** | 2024-02-01 | 新增系统统计和监控功能 |
| **v1.2.0** | 2024-03-01 | 增强权限控制和安全审计 |

---

## 📞 技术支持

- **开发团队**: Collide Team
- **技术文档**: https://docs.collide.com
- **监控面板**: https://monitor.collide.com
- **问题反馈**: https://github.com/collide/issues
- **在线支持**: support@collide.com

---

## 🎯 未来规划

### 短期计划
- **服务网格**: 集成Istio服务网格
- **分布式追踪**: 完善链路追踪功能
- **自动扩容**: 基于负载的自动扩缩容

### 长期计划
- **多云部署**: 支持多云环境部署
- **智能运维**: AI驱动的智能运维
- **边缘计算**: 支持边缘节点部署

---

*📝 本文档由 Collide 开发团队维护，如有疑问请联系技术支持。* 