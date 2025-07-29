# Collide 微服务平台 API 文档

## 📋 概述

Collide 是一个基于 Spring Cloud Gateway + Dubbo 的微服务平台，提供内容管理、用户服务、社交互动、电商交易等完整功能。

## 🏗️ 架构说明

- **网关服务**: Spring Cloud Gateway (端口: 9501)
- **服务注册**: Nacos 注册中心
- **服务通信**: Apache Dubbo RPC
- **数据存储**: MySQL + Redis + Elasticsearch
- **消息队列**: RocketMQ

## 📚 API 文档目录

### 🔧 核心服务
- [认证服务 API](./apis/auth-api.md) - 用户认证、Token管理

### 💼 业务服务
- [用户服务 API](./apis/users-api.md) - 用户管理、个人资料
- [内容服务 API](./apis/content-api.md) - 内容发布、章节管理
- [分类服务 API](./apis/category-api.md) - 分类管理、层级结构
- [评论服务 API](./apis/comment-api.md) - 评论管理、回复功能
- [社交服务 API](./apis/social-api.md) - 动态发布、社交互动
- [标签服务 API](./apis/tag-api.md) - 标签管理、智能推荐

### 🔗 交互服务
- [点赞服务 API](./apis/like-api.md) - 点赞管理、统计
- [收藏服务 API](./apis/favorite-api.md) - 收藏管理、分组
- [关注服务 API](./apis/follow-api.md) - 关注关系、粉丝管理

### 🛒 电商服务
- [商品服务 API](./apis/goods-api.md) - 商品管理、库存管理
- [订单服务 API](./apis/order-api.md) - 订单管理、状态流转
- [支付服务 API](./apis/payment-api.md) - 支付处理、账单管理

### 🔍 工具服务
- [搜索服务 API](./apis/search-api.md) - 全文搜索、智能推荐

## 🌐 接口规范

### 基础信息
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **网关地址**: `https://api.collide.com`

### 统一响应格式
```json
{
  "success": true,                    // 是否成功
  "responseCode": "SUCCESS",          // 响应码
  "responseMessage": "操作成功",       // 响应消息
  "data": {},                         // 响应数据
  "timestamp": "2024-01-16T10:30:00"  // 响应时间
}
```

### 分页响应格式
```json
{
  "success": true,
  "responseCode": "SUCCESS", 
  "responseMessage": "查询成功",
  "data": {
    "datas": [],           // 数据列表
    "total": 100,          // 总数
    "currentPage": 1,      // 当前页
    "pageSize": 20,        // 页面大小
    "totalPage": 5         // 总页数
  }
}
```

### 通用状态码
| 状态码 | 说明 | HTTP状态码 |
|--------|------|------------|
| SUCCESS | 成功 | 200 |
| PARAM_ERROR | 参数错误 | 400 |
| UNAUTHORIZED | 未授权 | 401 |
| FORBIDDEN | 禁止访问 | 403 |
| NOT_FOUND | 资源不存在 | 404 |
| SERVER_ERROR | 服务器错误 | 500 |

### 认证机制
- **Header**: `Authorization: Bearer {token}`
- **Token类型**: JWT
- **过期时间**: 2小时
- **刷新机制**: Refresh Token

## 🚀 快速开始

### 1. 获取访问Token
```bash
curl -X POST "https://api.collide.com/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

### 2. 调用业务接口
```bash
curl -X GET "https://api.collide.com/api/v1/users/profile" \
  -H "Authorization: Bearer your_access_token"
```

## 📧 联系我们

- **技术支持**: support@collide.com
- **开发团队**: dev@collide.com
- **文档更新**: docs@collide.com

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0