# 🎯 Collide 点赞服务 API 文档

> **版本**: v1.0.0  
> **更新时间**: 2024-01-01  
> **负责团队**: Collide Team

## 📋 概述

点赞服务提供完整的点赞互动功能，支持点赞、点踩、批量操作等多种互动方式。基于去连表化设计，提供高性能的点赞统计和状态查询能力。

### 🎯 核心功能

- **点赞操作**: 支持点赞、取消点赞、点踩操作
- **批量处理**: 支持批量点赞操作，提升性能
- **状态查询**: 快速查询用户点赞状态
- **统计信息**: 实时获取点赞统计数据
- **历史记录**: 查询用户点赞历史记录
- **实时更新**: 基于Redis缓存的实时数据同步

### 🏗️ 技术特点

| 特性 | 说明 |
|------|------|
| **高性能** | Redis缓存 + 数据库双写，查询延迟<10ms |
| **防重复** | 基于唯一索引防止重复点赞 |
| **批量操作** | 支持批量点赞，减少网络开销 |
| **实时统计** | 点赞数实时更新，支持热门排序 |
| **去连表化** | 冗余存储，避免复杂连表查询 |

---

## 🚀 快速开始

### Base URL
```
https://api.collide.com
```

### 认证方式
```http
Authorization: Bearer {token}
```

### 通用响应格式
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {...},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

## 📚 API 接口列表

### 1. 点赞/取消点赞操作

**接口地址**: `POST /api/like/action`

**功能描述**: 执行点赞、取消点赞或点踩操作

#### 请求参数

```json
{
  "userId": 12345,
  "targetId": 67890,
  "likeType": "POST",
  "action": "LIKE"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |
| `targetId` | Long | ✅ | 目标对象ID |
| `likeType` | String | ✅ | 点赞类型: POST(动态), COMMENT(评论), USER(用户) |
| `action` | String | ✅ | 操作类型: LIKE(点赞), UNLIKE(取消点赞), DISLIKE(点踩) |

#### 响应示例

**成功响应**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "点赞成功",
  "data": {
    "userId": 12345,
    "targetId": 67890,
    "likeType": "POST",
    "action": "LIKE",
    "likeCount": 128,
    "isLiked": true,
    "timestamp": "2024-01-01T10:30:00Z"
  }
}
```

**失败响应**:
```json
{
  "success": false,
  "responseCode": "ALREADY_LIKED",
  "responseMessage": "您已经点过赞了",
  "data": null
}
```

#### cURL 示例

```bash
curl -X POST "https://api.collide.com/api/like/action" \
  -H "Authorization: Bearer your-token" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 12345,
    "targetId": 67890,
    "likeType": "POST",
    "action": "LIKE"
  }'
```

---

### 2. 批量点赞操作

**接口地址**: `POST /api/like/batch-action`

**功能描述**: 批量执行多个点赞操作，提升性能

#### 请求参数

```json
[
  {
    "userId": 12345,
    "targetId": 67890,
    "likeType": "POST",
    "action": "LIKE"
  },
  {
    "userId": 12345,
    "targetId": 67891,
    "likeType": "POST", 
    "action": "LIKE"
  }
]
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量操作完成",
  "data": {
    "totalCount": 2,
    "successCount": 2,
    "failureCount": 0,
    "results": [
      {
        "targetId": 67890,
        "success": true,
        "message": "点赞成功"
      },
      {
        "targetId": 67891,
        "success": true,
        "message": "点赞成功"
      }
    ]
  }
}
```

---

### 3. 查询点赞记录

**接口地址**: `POST /api/like/query`

**功能描述**: 分页查询点赞记录

#### 请求参数

```json
{
  "userId": 12345,
  "targetId": 67890,
  "likeType": "POST",
  "action": "LIKE",
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-31T23:59:59Z",
  "currentPage": 1,
  "pageSize": 20,
  "sortBy": "createTime",
  "sortOrder": "DESC"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ❌ | 用户ID（筛选条件） |
| `targetId` | Long | ❌ | 目标对象ID（筛选条件） |
| `likeType` | String | ❌ | 点赞类型筛选 |
| `action` | String | ❌ | 操作类型筛选 |
| `startTime` | String | ❌ | 开始时间 |
| `endTime` | String | ❌ | 结束时间 |
| `currentPage` | Integer | ❌ | 当前页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认20 |
| `sortBy` | String | ❌ | 排序字段，默认createTime |
| `sortOrder` | String | ❌ | 排序方向，默认DESC |

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS", 
  "responseMessage": "查询成功",
  "datas": [
    {
      "id": 1001,
      "userId": 12345,
      "userName": "张三",
      "userAvatar": "https://cdn.collide.com/avatar/12345.jpg",
      "targetId": 67890,
      "likeType": "POST",
      "action": "LIKE",
      "createTime": "2024-01-01T10:30:00Z"
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

---

### 4. 检查用户点赞状态

**接口地址**: `GET /api/like/check-status`

**功能描述**: 检查用户对指定对象的点赞状态

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |
| `targetId` | Long | ✅ | 目标对象ID |
| `likeType` | String | ✅ | 点赞类型 |

#### URL 示例
```
GET /api/like/check-status?userId=12345&targetId=67890&likeType=POST
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "userId": 12345,
    "targetId": 67890,
    "likeType": "POST",
    "isLiked": true,
    "isDisliked": false,
    "likeTime": "2024-01-01T10:30:00Z",
    "likeCount": 128,
    "dislikeCount": 3
  }
}
```

---

### 5. 获取点赞统计

**接口地址**: `GET /api/like/statistics`

**功能描述**: 获取指定对象的点赞统计信息

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 目标对象ID |
| `likeType` | String | ✅ | 点赞类型 |

#### URL 示例
```
GET /api/like/statistics?targetId=67890&likeType=POST
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "targetId": 67890,
    "likeType": "POST",
    "totalLikes": 1248,
    "totalDislikes": 23,
    "likeRatio": 98.15,
    "todayLikes": 45,
    "weeklyLikes": 312,
    "monthlyLikes": 1156,
    "topLikers": [
      {
        "userId": 12345,
        "userName": "张三",
        "userAvatar": "https://cdn.collide.com/avatar/12345.jpg",
        "likeTime": "2024-01-01T10:30:00Z"
      }
    ],
    "likeGrowthTrend": [
      {"date": "2024-01-01", "count": 45},
      {"date": "2024-01-02", "count": 52}
    ]
  }
}
```

---

### 6. 获取用户点赞历史

**接口地址**: `POST /api/like/user-history`

**功能描述**: 分页获取用户的点赞历史记录

#### 请求参数

```json
{
  "userId": 12345,
  "likeType": "POST",
  "action": "LIKE",
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-31T23:59:59Z",
  "currentPage": 1,
  "pageSize": 20,
  "includeTargetInfo": true
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |
| `likeType` | String | ❌ | 点赞类型筛选 |
| `action` | String | ❌ | 操作类型筛选 |
| `startTime` | String | ❌ | 开始时间 |
| `endTime` | String | ❌ | 结束时间 |
| `currentPage` | Integer | ❌ | 当前页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认20 |
| `includeTargetInfo` | Boolean | ❌ | 是否包含目标对象信息，默认false |

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "datas": [
    {
      "id": 1001,
      "targetId": 67890,
      "likeType": "POST",
      "action": "LIKE",
      "createTime": "2024-01-01T10:30:00Z",
      "targetInfo": {
        "id": 67890,
        "title": "美好的一天开始了！",
        "authorName": "李四",
        "authorAvatar": "https://cdn.collide.com/avatar/67890.jpg",
        "createTime": "2024-01-01T09:00:00Z"
      }
    }
  ],
  "total": 89,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 5
}
```

---

## 📊 数据模型

### LikeRecord 点赞记录

```json
{
  "id": "Long - 记录ID",
  "userId": "Long - 用户ID", 
  "userName": "String - 用户名（冗余）",
  "userAvatar": "String - 用户头像（冗余）",
  "targetId": "Long - 目标对象ID",
  "targetType": "String - 目标类型",
  "likeType": "String - 点赞类型",
  "action": "String - 操作类型",
  "createTime": "LocalDateTime - 创建时间",
  "updateTime": "LocalDateTime - 更新时间"
}
```

### LikeStatistics 点赞统计

```json
{
  "targetId": "Long - 目标对象ID",
  "likeType": "String - 点赞类型",
  "totalLikes": "Long - 总点赞数",
  "totalDislikes": "Long - 总点踩数", 
  "likeRatio": "Double - 点赞率",
  "todayLikes": "Long - 今日点赞数",
  "weeklyLikes": "Long - 本周点赞数",
  "monthlyLikes": "Long - 本月点赞数"
}
```

---

## 🚨 错误码说明

| 错误码 | HTTP状态码 | 说明 | 解决方案 |
|--------|-----------|------|----------|
| `SUCCESS` | 200 | 操作成功 | - |
| `INVALID_PARAM` | 400 | 参数错误 | 检查请求参数格式 |
| `USER_NOT_FOUND` | 404 | 用户不存在 | 确认用户ID正确 |
| `TARGET_NOT_FOUND` | 404 | 目标对象不存在 | 确认目标ID正确 |
| `ALREADY_LIKED` | 409 | 已经点过赞 | 无需重复操作 |
| `NOT_LIKED_YET` | 409 | 尚未点赞 | 请先点赞再取消 |
| `LIKE_LIMIT_EXCEEDED` | 429 | 点赞次数超限 | 请稍后再试 |
| `PERMISSION_DENIED` | 403 | 权限不足 | 确认用户权限 |
| `SYSTEM_ERROR` | 500 | 系统错误 | 联系技术支持 |

---

## 🛡️ 安全说明

### 防刷机制
- **频率限制**: 每用户每分钟最多60次点赞操作
- **IP限制**: 每IP每分钟最多300次请求
- **异常检测**: 自动识别异常点赞行为

### 数据安全
- **参数验证**: 严格的参数格式验证
- **SQL注入**: 使用参数化查询防止注入
- **XSS防护**: 输出数据自动转义
- **权限控制**: 基于JWT的用户身份验证

---

## 📈 性能指标

| 指标 | 目标值 | 当前值 |
|------|--------|--------|
| **响应时间** | <50ms | 35ms |
| **QPS** | >10,000 | 12,000 |
| **可用性** | 99.99% | 99.99% |
| **缓存命中率** | >95% | 97% |

---

## 🔄 版本历史

| 版本 | 发布日期 | 更新内容 |
|------|----------|----------|
| **v1.0.0** | 2024-01-01 | 初始版本，支持基础点赞功能 |
| **v1.1.0** | 2024-02-01 | 新增批量操作和统计功能 |
| **v1.2.0** | 2024-03-01 | 优化性能，支持实时数据 |

---

## 📞 技术支持

- **开发团队**: Collide Team
- **技术文档**: https://docs.collide.com
- **问题反馈**: https://github.com/collide/issues
- **在线支持**: support@collide.com

---

*📝 本文档由 Collide 开发团队维护，如有疑问请联系技术支持。* 