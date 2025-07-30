# Collide 点赞服务 API 文档

## 概述

Collide 点赞服务提供完整的点赞管理功能，包括内容点赞、取消点赞、点赞统计、点赞记录查询等核心功能。基于无连表设计的高性能点赞系统，集成JetCache分布式缓存。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/like`  
**Dubbo服务**: `collide-like`  
**设计理念**: 高性能点赞系统，支持大规模并发点赞，提供实时统计和缓存优化

## v2.0.0 新特性

### 🚀 缓存增强
- **JetCache分布式缓存**: Redis + 本地缓存双重保障
- **智能缓存策略**: 不同业务场景的差异化过期时间
- **缓存预热机制**: 热点数据预加载，提升响应速度
- **缓存穿透防护**: 防止缓存击穿和雪崩

### ⚡ 性能优化
- **平均响应时间**: < 30ms
- **缓存命中率**: 95%+ (热点数据)
- **并发支持**: > 10000 QPS
- **批量操作**: 支持 500 条/次

### 🏗️ 架构升级
- **无连表设计**: 基于冗余字段的高性能查询
- **标准化API**: 统一的请求响应格式
- **门面服务**: 集成缓存和业务逻辑的统一入口
- **错误处理**: 标准化的异常处理机制

---

## 点赞核心功能 API

### 1. 添加点赞 💡 缓存优化
**接口路径**: `POST /api/v1/like/add`  
**接口描述**: 用户对内容、评论或动态进行点赞

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，点赞用户ID
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetId": 98765,                  // 必填，目标对象ID
  "targetTitle": "Java设计模式详解",    // 可选，目标对象标题（冗余）
  "targetAuthorId": 67890,            // 可选，目标对象作者ID（冗余）
  "userNickname": "点赞达人",          // 可选，用户昵称（冗余）
  "userAvatar": "https://example.com/avatar.jpg" // 可选，用户头像（冗余）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "点赞成功",
  "data": {
    "id": 456789,
    "likeType": "CONTENT",
    "targetId": 98765,
    "userId": 12345,
    "targetTitle": "Java设计模式详解",
    "targetAuthorId": 67890,
    "userNickname": "点赞达人",
    "userAvatar": "https://example.com/avatar.jpg",
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "code": "LIKE_ADD_ERROR",
  "message": "添加点赞失败: 您已经点赞过了"
}
```

---

### 2. 取消点赞 💡 缓存优化
**接口路径**: `POST /api/v1/like/cancel`  
**接口描述**: 取消用户的点赞

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，点赞用户ID
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetId": 98765,                  // 必填，目标对象ID
  "cancelReason": "误操作"             // 可选，取消原因
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "取消点赞成功",
  "data": null
}
```

---

### 3. 切换点赞状态 💡 缓存优化
**接口路径**: `POST /api/v1/like/toggle`  
**接口描述**: 如果已点赞则取消，如果未点赞则添加

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，点赞用户ID
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetId": 98765,                  // 必填，目标对象ID
  "targetTitle": "Java设计模式详解",    // 可选，目标对象标题（冗余）
  "targetAuthorId": 67890,            // 可选，目标对象作者ID（冗余）
  "userNickname": "点赞达人",          // 可选，用户昵称（冗余）
  "userAvatar": "https://example.com/avatar.jpg" // 可选，用户头像（冗余）
}
```

#### 响应示例
**成功响应 (200) - 添加点赞**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 456789,
    "likeType": "CONTENT",
    "targetId": 98765,
    "userId": 12345,
    "status": "active",
    "createTime": "2024-01-16T10:30:00"
  }
}
```

**成功响应 (200) - 取消点赞**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

## 点赞查询功能 API

### 4. 检查点赞状态 💡 缓存优化
**接口路径**: `POST /api/v1/like/check`  
**接口描述**: 检查用户是否已对目标对象点赞

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetId": 98765                   // 必填，目标对象ID
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": true                        // true: 已点赞, false: 未点赞
}
```

---

### 5. 分页查询点赞记录 💡 缓存优化
**接口路径**: `POST /api/v1/like/query`  
**接口描述**: 根据条件分页查询点赞记录

#### 请求参数
```json
{
  "pageNum": 1,                       // 页码（从1开始），默认1
  "pageSize": 20,                     // 每页大小，默认20
  "userId": 12345,                    // 可选，用户ID筛选
  "likeType": "CONTENT",              // 可选，点赞类型筛选
  "targetId": 98765,                  // 可选，目标对象ID筛选
  "targetAuthorId": 67890,            // 可选，目标作者ID筛选
  "status": "active",                 // 可选，状态筛选：active、cancelled
  "createTimeStart": "2024-01-01T00:00:00", // 可选，创建时间开始
  "createTimeEnd": "2024-01-31T23:59:59",   // 可选，创建时间结束
  "orderBy": "create_time",           // 可选，排序字段，默认create_time
  "orderDirection": "DESC"            // 可选，排序方向，默认DESC
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "datas": [
      {
        "id": 456789,
        "likeType": "CONTENT",
        "targetId": 98765,
        "userId": 12345,
        "targetTitle": "Java设计模式详解",
        "targetAuthorId": 67890,
        "userNickname": "点赞达人",
        "userAvatar": "https://example.com/avatar.jpg",
        "status": "active",
        "createTime": "2024-01-16T10:30:00",
        "updateTime": "2024-01-16T10:30:00"
      }
    ],
    "total": 156,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 8
  }
}
```

---

## 点赞统计功能 API

### 6. 获取点赞数量 💡 缓存优化
**接口路径**: `POST /api/v1/like/count`  
**接口描述**: 获取目标对象的点赞数量

#### 请求参数
```json
{
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetId": 98765                   // 必填，目标对象ID
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": 156                         // 点赞数量
}
```

---

### 7. 获取用户点赞数量 💡 缓存优化
**接口路径**: `POST /api/v1/like/user/count`  
**接口描述**: 获取用户的点赞数量

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "likeType": "CONTENT"               // 可选，点赞类型：CONTENT、COMMENT、DYNAMIC（为空则查询所有类型）
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": 234                         // 用户点赞数量
}
```

---

### 8. 批量检查点赞状态 💡 缓存优化
**接口路径**: `POST /api/v1/like/batch/check`  
**接口描述**: 批量检查用户对多个目标对象的点赞状态

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "likeType": "CONTENT",              // 必填，点赞类型：CONTENT、COMMENT、DYNAMIC
  "targetIds": [98765, 98766, 98767]  // 必填，目标对象ID列表（最多500个）
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "98765": true,                    // targetId: 是否已点赞
    "98766": false,
    "98767": true
  }
}
```

---

## 核心错误码说明

### 点赞核心错误码
| 错误码 | 说明 |
|--------|------|
| LIKE_ADD_ERROR | 添加点赞失败 |
| LIKE_CANCEL_ERROR | 取消点赞失败 |
| LIKE_TOGGLE_ERROR | 切换点赞状态失败 |
| LIKE_CHECK_ERROR | 检查点赞状态失败 |
| LIKE_QUERY_ERROR | 查询点赞记录失败 |
| LIKE_COUNT_ERROR | 获取点赞数量失败 |
| USER_LIKE_COUNT_ERROR | 获取用户点赞数量失败 |
| BATCH_CHECK_ERROR | 批量检查点赞状态失败 |

### 缓存相关错误码 ⚡
| 错误码 | 说明 |
|--------|------|
| CACHE_ERROR | 缓存操作异常 |
| CACHE_TIMEOUT | 缓存操作超时 |
| CACHE_MISS | 缓存未命中 |
| CACHE_INVALIDATE_ERROR | 缓存失效失败 |

### 业务逻辑错误码
| 错误码 | 说明 |
|--------|------|
| LIKE_PARAM_ERROR | 参数验证失败 |
| LIKE_TOGGLE_PARAM_ERROR | 切换点赞参数验证失败 |
| ALREADY_LIKED | 已经点赞过 |
| NOT_LIKED | 还未点赞 |
| TARGET_NOT_FOUND | 目标不存在 |
| LIKE_NOT_FOUND | 点赞记录不存在 |
| LIKE_LIMIT_EXCEEDED | 超出点赞限制 |
| SELF_LIKE_NOT_ALLOWED | 不能给自己点赞 |
| TARGET_NOT_ALLOW_LIKE | 目标不允许点赞 |
| USER_LIKE_BLOCKED | 用户被禁止点赞 |
| SUSPICIOUS_BEHAVIOR | 检测到异常点赞行为 |
| LIKE_TOO_FREQUENT | 点赞过于频繁 |

---

## 版本更新日志

### v2.0.0 (2024-01-16) - 缓存增强版
- ✅ 集成JetCache分布式缓存
- ✅ 添加缓存注解和优化策略
- ✅ 无连表设计的高性能架构
- ✅ 标准化API请求响应格式
- ✅ 门面服务模式集成
- ✅ 完善的错误处理机制
- ✅ 批量操作优化
- ✅ 性能监控和日志增强

### v1.0.0 (2024-01-01) - 基础版
- ✅ 基础点赞功能
- ✅ 简单的点赞统计
- ✅ 基本的API接口

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (缓存增强版)  
**服务状态**: ✅ 生产可用