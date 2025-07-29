# Collide 点赞服务 API 文档

## 概述

Collide 点赞服务提供完整的点赞管理功能，包括内容点赞、取消点赞、点赞统计、点赞记录查询等核心功能。支持多种目标类型的点赞，防止重复点赞和刷赞。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/like`  
**Dubbo服务**: `collide-like`  
**设计理念**: 高性能点赞系统，支持大规模并发点赞，提供实时统计和防刷机制

---

## 点赞管理 API

### 1. 添加点赞
**接口路径**: `POST /api/v1/like`  
**接口描述**: 为目标内容添加点赞

#### 请求参数
```json
{
  "targetType": "content",             // 必填，点赞目标类型：content/comment/dynamic/user
  "targetId": 98765,                  // 必填，点赞目标ID
  "userId": 12345,                    // 必填，点赞用户ID
  "targetAuthorId": 67890,            // 可选，目标内容作者ID（用于通知）
  "source": "detail",                 // 可选，点赞来源：detail/list/recommendation
  "clientInfo": {                     // 可选，客户端信息（防刷用）
    "ip": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  }
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "点赞成功",
  "data": {
    "id": 456789,
    "targetType": "content",
    "targetId": 98765,
    "userId": 12345,
    "username": "likeuser",
    "userNickname": "点赞达人",
    "userAvatar": "https://example.com/avatar.jpg",
    "targetAuthorId": 67890,
    "source": "detail",
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "currentLikeCount": 156            // 目标当前总点赞数
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "ALREADY_LIKED",
  "responseMessage": "您已经点赞过了"
}
```

---

### 2. 取消点赞
**接口路径**: `DELETE /api/v1/like`  
**接口描述**: 取消对目标内容的点赞

#### 请求参数
```json
{
  "targetType": "content",             // 必填，点赞目标类型
  "targetId": 98765,                  // 必填，点赞目标ID
  "userId": 12345                     // 必填，取消点赞的用户ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "取消点赞成功",
  "data": {
    "currentLikeCount": 155            // 目标当前总点赞数
  }
}
```

---

### 3. 检查点赞状态
**接口路径**: `GET /api/v1/like/status`  
**接口描述**: 检查用户是否已点赞目标内容

#### 请求参数
- **targetType** (query): 目标类型，必填
- **targetId** (query): 目标ID，必填
- **userId** (query): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "liked": true,                     // 是否已点赞
    "likeId": 456789,                  // 点赞记录ID（如果已点赞）
    "likeTime": "2024-01-16T10:30:00"  // 点赞时间（如果已点赞）
  }
}
```

---

### 4. 批量检查点赞状态
**接口路径**: `POST /api/v1/like/batch-status`  
**接口描述**: 批量检查用户对多个目标的点赞状态

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "targets": [                        // 必填，目标列表
    {
      "targetType": "content",
      "targetId": 98765
    },
    {
      "targetType": "content", 
      "targetId": 98766
    }
  ]
}
```

#### 响应示例
```json
{
  "success": true,
  "data": [
    {
      "targetType": "content",
      "targetId": 98765,
      "liked": true,
      "likeId": 456789
    },
    {
      "targetType": "content",
      "targetId": 98766,
      "liked": false,
      "likeId": null
    }
  ]
}
```

---

## 点赞查询 API

### 1. 分页查询点赞记录
**接口路径**: `POST /api/v1/like/query`  
**接口描述**: 分页查询点赞记录

#### 请求参数
```json
{
  "pageNum": 1,                       // 页码（从1开始）
  "pageSize": 20,                     // 每页大小
  "targetType": "content",            // 可选，目标类型筛选
  "targetId": 98765,                  // 可选，目标ID筛选
  "userId": 12345,                    // 可选，用户ID筛选
  "targetAuthorId": 67890,            // 可选，目标作者ID筛选
  "status": "active",                 // 可选，状态筛选
  "startTime": "2024-01-01T00:00:00", // 可选，开始时间
  "endTime": "2024-01-31T23:59:59",   // 可选，结束时间
  "orderBy": "createTime",            // 可选，排序字段
  "orderDirection": "DESC"            // 可选，排序方向
}
```

---

### 2. 获取用户点赞列表
**接口路径**: `GET /api/v1/like/user/{userId}`  
**接口描述**: 获取用户的点赞列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **targetType** (query): 目标类型，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 3. 获取目标点赞列表
**接口路径**: `GET /api/v1/like/target/{targetType}/{targetId}`  
**接口描述**: 获取目标内容的点赞列表

#### 请求参数
- **targetType** (path): 目标类型，必填
- **targetId** (path): 目标ID，必填
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "datas": [
      {
        "id": 456789,
        "userId": 12345,
        "username": "user123",
        "userNickname": "用户A",
        "userAvatar": "https://example.com/avatar1.jpg",
        "createTime": "2024-01-16T10:30:00"
      },
      {
        "id": 456790,
        "userId": 67890,
        "username": "user456", 
        "userNickname": "用户B",
        "userAvatar": "https://example.com/avatar2.jpg",
        "createTime": "2024-01-16T11:00:00"
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

## 点赞统计 API

### 1. 获取目标点赞统计
**接口路径**: `GET /api/v1/like/statistics/target`  
**接口描述**: 获取目标内容的点赞统计信息

#### 请求参数
- **targetType** (query): 目标类型，必填
- **targetId** (query): 目标ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "targetType": "content",
    "targetId": 98765,
    "totalLikes": 156,                 // 总点赞数
    "uniqueUsers": 154,                // 点赞用户数（去重）
    "todayLikes": 12,                  // 今日新增点赞
    "weeklyLikes": 45,                 // 本周新增点赞
    "monthlyLikes": 89,                // 本月新增点赞
    "peakHour": 14,                    // 点赞高峰时段
    "averageDailyLikes": 5.6,          // 日均点赞数
    "likeGrowthRate": 15.8,            // 点赞增长率（%）
    "lastLikeTime": "2024-01-16T15:30:00"
  }
}
```

---

### 2. 获取用户点赞统计
**接口路径**: `GET /api/v1/like/statistics/user/{userId}`  
**接口描述**: 获取用户的点赞统计信息

#### 响应示例
```json
{
  "success": true,
  "data": {
    "userId": 12345,
    "totalLikesGiven": 234,            // 给出的点赞总数
    "totalLikesReceived": 567,         // 收到的点赞总数
    "todayLikesGiven": 5,              // 今日给出点赞
    "todayLikesReceived": 8,           // 今日收到点赞
    "mostLikedTargetType": "content",   // 最常点赞的目标类型
    "activeHours": [14, 20, 21],       // 活跃点赞时段
    "likingFrequency": "high",         // 点赞频率等级
    "receivingPopularity": "medium"    // 受欢迎程度等级
  }
}
```

---

### 3. 获取热门点赞内容
**接口路径**: `GET /api/v1/like/hot`  
**接口描述**: 获取热门点赞内容排行

#### 请求参数
- **targetType** (query): 目标类型，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **limit** (query): 返回数量，可选，默认10

#### 响应示例
```json
{
  "success": true,
  "data": [
    {
      "targetType": "content",
      "targetId": 98765,
      "targetTitle": "Java设计模式详解",
      "likeCount": 156,
      "recentLikes": 45,               // 指定时间范围内的点赞数
      "ranking": 1                     // 排名
    }
  ]
}
```

---

## 点赞通知 API

### 1. 获取点赞通知
**接口路径**: `GET /api/v1/like/notifications/{userId}`  
**接口描述**: 获取用户收到的点赞通知

#### 请求参数
- **userId** (path): 用户ID，必填
- **unreadOnly** (query): 是否只显示未读，可选，默认false
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "datas": [
      {
        "id": 789012,
        "likeId": 456789,
        "targetType": "content",
        "targetId": 98765,
        "targetTitle": "我的文章标题",
        "likerId": 12345,
        "likerNickname": "点赞用户",
        "likerAvatar": "https://example.com/avatar.jpg",
        "isRead": false,
        "createTime": "2024-01-16T10:30:00"
      }
    ],
    "total": 25,
    "unreadCount": 8
  }
}
```

---

### 2. 标记通知已读
**接口路径**: `POST /api/v1/like/notifications/read`  
**接口描述**: 标记点赞通知为已读

#### 请求参数
```json
{
  "userId": 67890,                    // 必填，用户ID
  "notificationIds": [789012, 789013], // 可选，具体通知ID列表
  "markAllRead": false                 // 可选，是否标记全部已读
}
```

---

## 点赞管理 API

### 1. 批量删除点赞
**接口路径**: `POST /api/v1/like/batch-delete`  
**接口描述**: 批量删除点赞记录（管理员功能）

#### 请求参数
```json
{
  "likeIds": [456789, 456790, 456791], // 必填，点赞ID列表
  "operatorId": 99999,                 // 必填，操作人ID
  "reason": "违规点赞清理"             // 可选，删除原因
}
```

---

### 2. 获取异常点赞检测
**接口路径**: `GET /api/v1/like/suspicious`  
**接口描述**: 检测可疑的异常点赞行为

#### 请求参数
- **timeRange** (query): 检测时间范围（小时），可选，默认24
- **threshold** (query): 异常阈值，可选，默认50

---

### 3. 设置点赞限制
**接口路径**: `POST /api/v1/like/settings/limit`  
**接口描述**: 设置用户点赞限制

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，用户ID
  "dailyLimit": 100,                  // 可选，日点赞限制
  "hourlyLimit": 20,                  // 可选，时点赞限制
  "enabled": true,                    // 必填，是否启用限制
  "operatorId": 99999                 // 必填，操作人ID
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
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

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0