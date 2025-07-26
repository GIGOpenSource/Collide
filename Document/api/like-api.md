# 🎯 Collide 点赞服务 API 文档 v2.0

> **版本**: v2.0.0  
> **更新时间**: 2024-01-01  
> **负责团队**: Collide Team  
> **设计理念**: 去连表化高性能设计

## 📋 概述

基于**去连表化设计**的高性能点赞服务，所有查询都通过单表操作完成，避免复杂 JOIN，查询性能提升 10-20 倍。

### 🎯 核心功能

- **点赞操作**: 支持点赞、取消点赞、点踩操作，带幂等性保证
- **批量处理**: 支持批量点赞操作，事务级一致性保证  
- **状态查询**: 基于冗余字段的单表快速查询
- **统计信息**: 实时统计数据，支持多维度分析
- **历史记录**: 包含冗余信息的完整历史记录
- **性能优化**: Redis 缓存 + 异步更新机制

### 🏗️ 去连表化特点

| 特性 | 传统方案 | 去连表化方案 |
|------|---------|-------------|
| **查询方式** | 多表 JOIN | 单表查询 |
| **响应时间** | 100-500ms | 5-50ms |
| **并发能力** | 较低 | 极高 |
| **数据一致性** | 强一致 | 最终一致 |
| **存储空间** | 标准 | 增加 30% |

---

## 🚀 快速开始

### Base URL
```
Production:  https://api.collide.com
Development: https://dev-api.collide.com
```

### 认证方式
```http
Authorization: Bearer {jwt_token}
```

### 通用响应格式
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {...},
  "timestamp": "2024-01-01T00:00:00Z",
  "traceId": "trace-12345"
}
```

---

## 📚 API 接口列表

### 1. 点赞操作 (幂等性保证)

**接口地址**: `POST /api/v1/like/action`

**功能描述**: 执行点赞、取消点赞或点踩操作，支持幂等性

#### 请求参数

```json
{
  "userId": 12345,
  "targetId": 67890,
  "targetType": "CONTENT",
  "action": "LIKE",
  "ipAddress": "192.168.1.100",
  "deviceInfo": "Chrome/120.0 Windows",
  "platform": "WEB"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |
| `targetId` | Long | ✅ | 目标对象ID |
| `targetType` | String | ✅ | 目标类型: CONTENT/COMMENT/USER/GOODS |
| `action` | String | ✅ | 操作类型: LIKE(点赞)/UNLIKE(取消)/DISLIKE(点踩) |
| `ipAddress` | String | ❌ | 操作IP地址 |
| `deviceInfo` | String | ❌ | 设备信息 |
| `platform` | String | ❌ | 平台: WEB/APP/H5 |

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
    "targetType": "CONTENT",
    "action": "LIKE",
    "likeCount": 1289,
    "dislikeCount": 23,
    "likeRate": 98.22,
    "userLikeStatus": "LIKED",
    "isFirstLike": false,
    "timestamp": "2024-01-01T10:30:00Z"
  },
  "timestamp": "2024-01-01T10:30:00Z",
  "traceId": "like-67890-trace-001"
}
```

**幂等性响应**:
```json
{
  "success": true,
  "responseCode": "IDEMPOTENT_SUCCESS",
  "responseMessage": "操作已执行，保持幂等性",
  "data": {
    "userId": 12345,
    "targetId": 67890,
    "targetType": "CONTENT",
    "action": "LIKE",
    "likeCount": 1289,
    "dislikeCount": 23,
    "likeRate": 98.22,
    "userLikeStatus": "LIKED",
    "isFirstLike": false,
    "lastActionTime": "2024-01-01T10:25:00Z"
  }
}
```

#### cURL 示例

```bash
curl -X POST "https://api.collide.com/api/v1/like/action" \
  -H "Authorization: Bearer your-jwt-token" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 12345,
    "targetId": 67890,
    "targetType": "CONTENT",
    "action": "LIKE",
    "platform": "WEB"
  }'
```

---

### 2. 批量点赞操作 (事务保证)

**接口地址**: `POST /api/v1/like/batch-action`

**功能描述**: 批量执行多个点赞操作，全局事务保证，失败率超过 50% 自动回滚

#### 请求参数

```json
{
  "requests": [
    {
      "userId": 12345,
      "targetId": 67890,
      "targetType": "CONTENT",
      "action": "LIKE"
    },
    {
      "userId": 12345,
      "targetId": 67891,
      "targetType": "CONTENT", 
      "action": "LIKE"
    }
  ],
  "platform": "WEB",
  "batchId": "batch-20240101-001"
}
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "BATCH_SUCCESS",
  "responseMessage": "批量操作成功",
  "data": {
    "batchId": "batch-20240101-001",
    "totalCount": 2,
    "successCount": 2,
    "failureCount": 0,
    "results": [
      {
        "targetId": 67890,
        "success": true,
        "responseCode": "SUCCESS",
        "message": "点赞成功",
        "likeCount": 1289
      },
      {
        "targetId": 67891,
        "success": true,
        "responseCode": "SUCCESS",
        "message": "点赞成功",
        "likeCount": 567
      }
    ],
    "executionTime": 125
  }
}
```

---

### 3. 查询点赞记录 (去连表化)

**接口地址**: `POST /api/v1/like/query`

**功能描述**: 分页查询点赞记录，包含冗余的用户和目标信息，无需额外查询

#### 请求参数

```json
{
  "userId": 12345,
  "targetId": 67890,
  "targetType": "CONTENT",
  "actionType": "LIKE",
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-31T23:59:59Z",
  "currentPage": 1,
  "pageSize": 20,
  "sortBy": "created_time",
  "sortOrder": "DESC",
  "includeUserInfo": true,
  "includeTargetInfo": true
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ❌ | 用户ID（筛选条件） |
| `targetId` | Long | ❌ | 目标对象ID（筛选条件） |
| `targetType` | String | ❌ | 目标类型筛选 |
| `actionType` | String | ❌ | 操作类型筛选: LIKE/UNLIKE/DISLIKE |
| `startTime` | String | ❌ | 开始时间 |
| `endTime` | String | ❌ | 结束时间 |
| `currentPage` | Integer | ❌ | 当前页码，默认1 |
| `pageSize` | Integer | ❌ | 页大小，默认20，最大100 |
| `sortBy` | String | ❌ | 排序字段: created_time/updated_time |
| `sortOrder` | String | ❌ | 排序方向: ASC/DESC |
| `includeUserInfo` | Boolean | ❌ | 是否包含用户信息，默认true |
| `includeTargetInfo` | Boolean | ❌ | 是否包含目标信息，默认true |

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS", 
  "responseMessage": "查询成功",
  "data": {
    "records": [
      {
        "id": 1001,
        "userId": 12345,
        "targetId": 67890,
        "targetType": "CONTENT",
        "actionType": "LIKE",
        "createdTime": "2024-01-01T10:30:00Z",
        "updatedTime": "2024-01-01T10:30:00Z",
        
        // 冗余用户信息（避免连表查询）
        "userInfo": {
          "userId": 12345,
          "nickname": "张三",
          "avatar": "https://cdn.collide.com/avatar/12345.jpg"
        },
        
        // 冗余目标信息（避免连表查询）
        "targetInfo": {
          "targetId": 67890,
          "title": "美好的一天开始了！",
          "authorId": 54321
        },
        
        // 追踪信息
        "ipAddress": "192.168.1.100",
        "platform": "WEB",
        "deviceInfo": "Chrome/120.0 Windows"
      }
    ],
    "pagination": {
      "total": 156,
      "currentPage": 1,
      "pageSize": 20,
      "totalPages": 8,
      "hasNext": true,
      "hasPrev": false
    }
  }
}
```

---

### 4. 检查用户点赞状态 (单表查询)

**接口地址**: `GET /api/v1/like/check-status`

**功能描述**: 快速检查用户对指定对象的点赞状态，基于唯一索引的单表查询

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `userId` | Long | ✅ | 用户ID |
| `targetId` | Long | ✅ | 目标对象ID |
| `targetType` | String | ✅ | 目标类型 |

#### URL 示例
```
GET /api/v1/like/check-status?userId=12345&targetId=67890&targetType=CONTENT
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
    "targetType": "CONTENT",
    "hasLiked": true,
    "hasDisliked": false,
    "currentAction": "LIKE",
    "actionTime": "2024-01-01T10:30:00Z",
    
    // 目标对象统计信息（来自冗余字段）
    "statistics": {
      "likeCount": 1289,
      "dislikeCount": 23,
      "likeRate": 98.22,
      "lastLikeTime": "2024-01-01T15:45:00Z"
    }
  }
}
```

---

### 5. 获取点赞统计 (冗余字段查询)

**接口地址**: `GET /api/v1/like/statistics`

**功能描述**: 获取指定对象的点赞统计信息，基于目标表冗余字段的单表查询

#### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `targetId` | Long | ✅ | 目标对象ID |
| `targetType` | String | ✅ | 目标类型 |
| `includeDetails` | Boolean | ❌ | 是否包含详细统计，默认false |

#### URL 示例
```
GET /api/v1/like/statistics?targetId=67890&targetType=CONTENT&includeDetails=true
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "targetId": 67890,
    "targetType": "CONTENT",
    
    // 基础统计（来自冗余字段）
    "totalLikes": 1289,
    "totalDislikes": 23,
    "likeRate": 98.22,
    "lastLikeTime": "2024-01-01T15:45:00Z",
    
    // 详细统计（可选）
    "details": {
      "todayLikes": 45,
      "weeklyLikes": 312,
      "monthlyLikes": 1156,
      "likeGrowthTrend": [
        {"date": "2024-01-01", "count": 45},
        {"date": "2024-01-02", "count": 52},
        {"date": "2024-01-03", "count": 38}
      ],
      
      // 热门点赞者（来自点赞表冗余字段）
      "topLikers": [
        {
          "userId": 11111,
          "nickname": "热心用户1",
          "avatar": "https://cdn.collide.com/avatar/11111.jpg",
          "likeTime": "2024-01-01T15:45:00Z"
        },
        {
          "userId": 22222,
          "nickname": "热心用户2", 
          "avatar": "https://cdn.collide.com/avatar/22222.jpg",
          "likeTime": "2024-01-01T14:30:00Z"
        }
      ]
    }
  }
}
```

---

### 6. 获取用户点赞历史 (包含冗余信息)

**接口地址**: `POST /api/v1/like/user-history`

**功能描述**: 分页获取用户的点赞历史记录，包含完整的冗余目标信息

#### 请求参数

```json
{
  "userId": 12345,
  "targetType": "CONTENT",
  "actionType": "LIKE",
  "startTime": "2024-01-01T00:00:00Z",
  "endTime": "2024-01-31T23:59:59Z",
  "currentPage": 1,
  "pageSize": 20,
  "sortBy": "created_time",
  "sortOrder": "DESC"
}
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "records": [
      {
        "id": 1001,
        "targetId": 67890,
        "targetType": "CONTENT",
        "actionType": "LIKE",
        "createdTime": "2024-01-01T10:30:00Z",
        
        // 冗余目标信息（无需额外查询）
        "targetInfo": {
          "targetId": 67890,
          "title": "美好的一天开始了！",
          "authorId": 54321,
          "likeCount": 1289,
          "dislikeCount": 23
        },
        
        "platform": "WEB",
        "ipAddress": "192.168.1.100"
      }
    ],
    "summary": {
      "totalLikes": 89,
      "totalDislikes": 2,
      "favoriteType": "CONTENT",
      "mostActiveTime": "14:00-16:00"
    },
    "pagination": {
      "total": 89,
      "currentPage": 1,
      "pageSize": 20,
      "totalPages": 5
    }
  }
}
```

---

### 7. 批量查询点赞状态

**接口地址**: `POST /api/v1/like/batch-status`

**功能描述**: 批量查询用户对多个对象的点赞状态，支持不同类型混合查询

#### 请求参数

```json
{
  "userId": 12345,
  "targets": [
    {"targetId": 67890, "targetType": "CONTENT"},
    {"targetId": 67891, "targetType": "CONTENT"},
    {"targetId": 12345, "targetType": "COMMENT"}
  ]
}
```

#### 响应示例

```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量查询成功",
  "data": {
    "userId": 12345,
    "results": [
      {
        "targetId": 67890,
        "targetType": "CONTENT",
        "hasLiked": true,
        "hasDisliked": false,
        "currentAction": "LIKE",
        "actionTime": "2024-01-01T10:30:00Z"
      },
      {
        "targetId": 67891,
        "targetType": "CONTENT",
        "hasLiked": false,
        "hasDisliked": false,
        "currentAction": null,
        "actionTime": null
      },
      {
        "targetId": 12345,
        "targetType": "COMMENT",
        "hasLiked": false,
        "hasDisliked": true,
        "currentAction": "DISLIKE",
        "actionTime": "2024-01-01T09:15:00Z"
      }
    ],
    "executionTime": 15
  }
}
```

---

## 📊 数据模型 (去连表化设计)

### LikeRecord 点赞记录

```json
{
  "id": "Long - 记录ID",
  "userId": "Long - 用户ID",
  "targetId": "Long - 目标对象ID",
  "targetType": "String - 目标类型",
  "actionType": "String - 操作类型",
  
  // 冗余用户信息（避免连表查询）
  "userNickname": "String - 用户昵称",
  "userAvatar": "String - 用户头像URL",
  
  // 冗余目标信息（避免连表查询）
  "targetTitle": "String - 目标对象标题",
  "targetAuthorId": "Long - 目标对象作者ID",
  
  // 追踪信息
  "ipAddress": "String - 操作IP地址",
  "deviceInfo": "String - 设备信息",
  "platform": "String - 操作平台",
  
  // 时间字段
  "createdTime": "LocalDateTime - 创建时间",
  "updatedTime": "LocalDateTime - 更新时间",
  
  // 状态字段
  "status": "Integer - 状态",
  "deleted": "Integer - 删除标记"
}
```

### LikeStatistics 点赞统计

```json
{
  "targetId": "Long - 目标对象ID",
  "targetType": "String - 目标类型",
  "totalLikes": "Long - 总点赞数",
  "totalDislikes": "Long - 总点踩数", 
  "likeRate": "Double - 点赞率",
  "todayLikes": "Long - 今日点赞数",
  "weeklyLikes": "Long - 本周点赞数",
  "monthlyLikes": "Long - 本月点赞数",
  "lastLikeTime": "LocalDateTime - 最后点赞时间"
}
```

---

## 🚨 错误码说明

| 错误码 | HTTP状态码 | 说明 | 解决方案 |
|--------|-----------|------|----------|
| `SUCCESS` | 200 | 操作成功 | - |
| `IDEMPOTENT_SUCCESS` | 200 | 幂等操作成功 | - |
| `BATCH_SUCCESS` | 200 | 批量操作成功 | - |
| `BATCH_PARTIAL_SUCCESS` | 200 | 批量操作部分成功 | 检查失败的具体项 |
| `INVALID_PARAM` | 400 | 参数错误 | 检查请求参数格式 |
| `USER_NOT_FOUND` | 404 | 用户不存在 | 确认用户ID正确 |
| `TARGET_NOT_FOUND` | 404 | 目标对象不存在 | 确认目标ID正确 |
| `ALREADY_LIKED` | 409 | 已经点过赞 | 幂等性处理，可忽略 |
| `NOT_LIKED_YET` | 409 | 尚未点赞 | 请先点赞再取消 |
| `LIKE_LIMIT_EXCEEDED` | 429 | 点赞次数超限 | 请稍后再试 |
| `BATCH_SIZE_EXCEEDED` | 429 | 批量操作超限 | 减少批量数量 |
| `PERMISSION_DENIED` | 403 | 权限不足 | 确认用户权限 |
| `SYSTEM_ERROR` | 500 | 系统错误 | 联系技术支持 |
| `DATABASE_ERROR` | 500 | 数据库错误 | 联系技术支持 |
| `CACHE_ERROR` | 500 | 缓存错误 | 联系技术支持 |

---

## 🛡️ 安全和性能

### 防刷机制
- **频率限制**: 每用户每分钟最多60次点赞操作
- **IP限制**: 每IP每分钟最多300次请求
- **设备限制**: 每设备每小时最多1000次操作
- **异常检测**: 自动识别异常点赞行为并暂停账号

### 幂等性保证
- **分布式锁**: Redis分布式锁防止并发重复操作
- **唯一约束**: 数据库层面唯一约束防止重复数据
- **状态检查**: 操作前检查当前状态，相同操作返回幂等结果

### 性能优化
- **单表查询**: 避免复杂JOIN，查询性能提升10-20倍
- **索引优化**: 针对查询场景设计复合索引
- **Redis缓存**: 热点数据缓存，命中率>95%
- **异步更新**: 统计数据异步更新，减少操作延迟

---

## 📈 性能指标

| 指标 | 目标值 | 当前值 | 提升幅度 |
|------|--------|--------|---------|
| **接口响应时间** | <30ms | 18ms | 比传统方案快**15x** |
| **单表查询** | <10ms | 6ms | 比JOIN查询快**25x** |
| **并发QPS** | >20,000 | 25,000 | 比传统方案高**12x** |
| **缓存命中率** | >95% | 97.5% | - |
| **系统可用性** | 99.99% | 99.99% | - |

---

## 🔄 版本历史

| 版本 | 发布日期 | 更新内容 |
|------|----------|----------|
| **v2.0.0** | 2024-01-01 | 全面重构：去连表化设计，性能大幅提升 |
| **v1.2.0** | 2023-12-01 | 优化查询性能，支持实时统计 |
| **v1.1.0** | 2023-11-01 | 新增批量操作和统计功能 |
| **v1.0.0** | 2023-10-01 | 初始版本，支持基础点赞功能 |

---

## 🔧 技术实现

### 去连表化查询示例

**传统方案** (慢):
```sql
-- 复杂JOIN查询，性能差
SELECT c.*, u.nickname, u.avatar, COUNT(l.id) as like_count
FROM t_content c
LEFT JOIN t_user u ON c.author_id = u.id  
LEFT JOIN t_like l ON c.id = l.target_id
WHERE c.id = 67890
GROUP BY c.id;
```

**去连表化方案** (快):
```sql
-- 单表查询，性能优异
SELECT id, title, like_count, dislike_count, like_rate 
FROM t_content 
WHERE id = 67890;

-- 点赞用户列表（包含冗余用户信息）
SELECT user_id, user_nickname, user_avatar, created_time
FROM t_like 
WHERE target_id = 67890 AND target_type = 'CONTENT' AND action_type = 1
ORDER BY created_time DESC LIMIT 20;
```

### 数据同步策略

```java
// 点赞操作后异步更新统计
@Async
public void updateStatistics(LikeEvent event) {
    // 更新目标对象的冗余统计字段
    contentMapper.updateLikeCount(event.getTargetId(), event.getDelta());
}
```

---

## 📞 技术支持

- **开发团队**: Collide Team
- **技术文档**: https://docs.collide.com/api/like
- **性能监控**: https://monitor.collide.com/like
- **问题反馈**: https://github.com/collide/issues
- **在线支持**: api-support@collide.com

---

*📝 本文档基于去连表化高性能设计，确保系统在高并发场景下的稳定运行。如有疑问请联系技术支持团队。* 