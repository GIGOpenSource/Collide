# Collide API 文档索引

## 📋 概述

本文档是 Collide 微服务架构的 API 文档索引，包含了所有模块的接口文档。

**API 基础地址**: `http://localhost:9503`

**Swagger UI**: `http://localhost:9503/swagger-ui.html`

---

## 🔗 模块文档

### 🔐 认证授权模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [认证服务API](./auth-api.md) | 用户注册、登录、认证相关接口 | `/api/v1/auth` |
| [Token管理API](./token-api.md) | Token生成、验证、刷新相关接口 | `/api/v1/token` |

### 👤 用户管理模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [用户管理API](./user-api.md) | 用户注册、认证、个人信息管理相关接口 | `/api/v1/users` |
| [博主申请API](./blogger-api.md) | 博主申请、审核相关接口 | `/api/v1/blogger` |
| [文件上传API](./file-upload-api.md) | 文件上传相关接口 | `/api/v1/files` |

### 📝 内容管理模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [内容管理API](./content-api.md) | 内容创作、发布、管理相关接口 | `/api/v1/content` |
| [分类管理API](./category-api.md) | 内容分类管理相关接口 | `/api/v1/categories` |
| [标签管理API](./tag-api.md) | 标签和用户兴趣管理相关接口 | `/api/v1/tags` |

### 💬 社交互动模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [关注管理API](./follow-api.md) | 用户关注、粉丝管理相关接口 | `/api/v1/follow` |
| [评论管理API](./comment-api.md) | 评论发布、回复、管理相关接口 | `/api/v1/comments` |
| [点赞服务API](./like-api.md) | 点赞相关接口 | `/api/v1/like` |
| [收藏服务API](./favorite-api.md) | 收藏相关接口 | `/api/v1/favorite` |
| [社交服务API](./social-api.md) | 社交动态、推荐相关接口 | `/api/v1/social` |

### 🔍 搜索服务模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [搜索模块API](./search-module-api.md) | 内容搜索相关接口 | `/api/v1/search` |

### 🏢 业务聚合模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [业务聚合API](./business-api.md) | 业务聚合相关接口 | `/api/v1/business` |

### 🛠️ 系统监控模块

| 文档 | 描述 | 基础路径 |
|------|------|----------|
| [健康检查API](./health-api.md) | 系统健康检查相关接口 | `/health` |
| [测试接口API](./test-api.md) | 系统测试相关接口 | `/api/v1/test` |
| [Swagger文档](./swagger-api-docs.md) | API文档配置说明 | - |

---

## 📊 接口统计

| 模块 | 接口数量 | 状态 |
|------|----------|------|
| 认证授权 | 15+ | ✅ 完整 |
| 用户管理 | 20+ | ✅ 完整 |
| 内容管理 | 25+ | ✅ 完整 |
| 社交互动 | 30+ | ✅ 完整 |
| 搜索服务 | 10+ | ✅ 完整 |
| 业务聚合 | 15+ | ✅ 完整 |
| 系统监控 | 15+ | ✅ 完整 |

**总计**: 130+ 个接口

---

## 🚀 快速开始

### 1. 环境准备

```bash
# 启动应用
cd /path/to/collide
mvn spring-boot:run -pl collide-application/collide-app

# 或者使用 Docker
docker-compose up -d
```

### 2. 访问API文档

```bash
# Swagger UI
http://localhost:9503/swagger-ui.html

# OpenAPI 规范
http://localhost:9503/v3/api-docs
```

### 3. 基础测试

```bash
# 健康检查
curl -X GET "http://localhost:9503/health"

# 系统信息
curl -X GET "http://localhost:9503/api/v1/test/info"

# Hello测试
curl -X GET "http://localhost:9503/api/v1/test/hello"
```

---

## 🔐 认证说明

### Token认证

大部分API需要Token认证，请在请求头中添加：

```http
Authorization: Bearer {your-token}
```

### 获取Token

```bash
# 用户登录获取Token
curl -X POST "http://localhost:9503/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123"
  }'
```

---

## 📝 响应格式

所有API都使用统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  },
  "timestamp": 1640995200000
}
```

### 响应码说明

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 🔧 开发工具

### Postman 集合

可以导入以下Postman集合进行API测试：

```json
{
  "info": {
    "name": "Collide API Collection",
    "description": "Collide 微服务架构 API 集合"
  },
  "item": [
    {
      "name": "认证服务",
      "item": [
        {
          "name": "用户注册",
          "request": {
            "method": "POST",
            "url": "{{baseUrl}}/api/v1/auth/register"
          }
        }
      ]
    }
  ]
}
```

### 环境变量

```bash
# 基础URL
baseUrl=http://localhost:9503

# Token变量
token=your-jwt-token
```

---

## 📚 相关资源

- [项目README](../README.md)
- [部署文档](../DOCKER-DEPLOYMENT.md)
- [架构设计](../auth-system-design.md)
- [数据库设计](../database-design.md)

---

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

---

## 📞 联系方式

- **项目地址**: https://github.com/GIG-Team/Collide
- **问题反馈**: https://github.com/GIG-Team/Collide/issues
- **邮箱**: support@gig.com

---

## 📄 许可证

本项目采用 [Apache License 2.0](../LICENSES/LICENSE) 许可证。 