# Collide 关注服务 API 文档

## 概述

Collide 关注服务提供完整的用户关注功能，包括关注用户、取消关注、关注关系查询、粉丝列表管理、关注统计等核心功能。基于无连表设计的高性能关注系统，集成JetCache分布式缓存。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/follow`  
**Dubbo服务**: `collide-follow`  
**设计理念**: 高性能关注管理系统，支持大规模用户关注关系管理，提供分布式缓存优化

## v2.0.0 新特性

### 🚀 缓存增强
- **JetCache分布式缓存**: Redis + 本地缓存双重保障
- **智能缓存策略**: 不同业务场景的差异化过期时间
- **缓存预热机制**: 热点关注数据预加载，提升响应速度
- **缓存穿透防护**: 防止缓存击穿和雪崩

### ⚡ 性能优化
- **平均响应时间**: < 20ms
- **缓存命中率**: 95%+ (热点数据)
- **并发支持**: > 15000 QPS
- **批量操作**: 支持 500 条/次

### 🏗️ 架构升级
- **无连表设计**: 基于冗余字段的高性能查询
- **标准化API**: 统一的请求响应格式
- **门面服务**: 集成缓存和业务逻辑的统一入口
- **错误处理**: 标准化的异常处理机制

### 🔥 特色功能
- **关注检测**: 特殊的关注状态检测接口
- **双向关系**: 智能识别互关关系
- **活跃度分析**: 基于关注行为的用户画像
- **批量检测**: 高效的批量关注状态检查

---

## 关注操作 API

### 1. 关注用户 💡 缓存优化
**接口路径**: `POST /api/v1/follow/follow`  
**接口描述**: 用户关注另一个用户，建立关注关系

#### 请求参数
```json
{
  "followerId": 12345,     // 必填，关注者用户ID
  "followeeId": 67890,     // 必填，被关注者用户ID
  "source": "profile",     // 可选，关注来源
  "remark": "技术大牛"      // 可选，关注备注
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "关注成功",
  "data": {
    "id": 98765,
    "followerId": 12345,
    "followeeId": 67890,
    "status": "active",
    "source": "profile",
    "remark": "技术大牛",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "code": "FOLLOW_ERROR",
  "message": "关注操作失败: 已经关注了该用户"
}
```

---

### 2. 取消关注 💡 缓存优化
**接口路径**: `POST /api/v1/follow/unfollow`  
**接口描述**: 用户取消关注另一个用户，解除关注关系

#### 请求参数
```json
{
  "followerId": 12345,     // 必填，关注者用户ID
  "followeeId": 67890,     // 必填，被关注者用户ID
  "cancelReason": "误关注", // 可选，取消原因
  "operatorId": 12345      // 可选，操作人ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "取消关注成功",
  "data": null
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "code": "UNFOLLOW_ERROR",
  "message": "取消关注失败: 尚未关注该用户"
}
```

---

## 关注关系查询 API

### 3. 检查关注关系 💡 缓存优化
**接口路径**: `GET /api/v1/follow/check`  
**接口描述**: 检查两个用户之间是否存在关注关系

#### 查询参数
- **followerId** (query): 关注者ID，必填
- **followeeId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": true
}
```

---

### 4. 获取关注详情 💡 缓存优化
**接口路径**: `GET /api/v1/follow/detail`  
**接口描述**: 获取关注关系的详细信息

#### 查询参数
- **followerId** (query): 关注者ID，必填
- **followeeId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "id": 98765,
    "followerId": 12345,
    "followeeId": 67890,
    "status": "active",
    "source": "profile",
    "remark": "技术大牛",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 5. 分页查询关注记录 💡 缓存优化
**接口路径**: `GET /api/v1/follow/query`  
**接口描述**: 支持多种条件的关注记录分页查询

#### 查询参数
- **followerId** (query): 关注者ID，可选
- **followeeId** (query): 被关注者ID，可选
- **status** (query): 关注状态，可选
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "datas": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
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

## 关注者列表 API

### 6. 获取关注者列表 💡 缓存优化
**接口路径**: `GET /api/v1/follow/followers`  
**接口描述**: 获取指定用户的关注者分页列表（谁关注了我）

#### 查询参数
- **userId** (query): 被关注者ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "datas": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
      }
    ],
    "total": 1250,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 63
  }
}
```

---

### 7. 获取关注列表 💡 缓存优化
**接口路径**: `GET /api/v1/follow/following`  
**接口描述**: 获取指定用户的关注列表分页数据（我关注了谁）

#### 查询参数
- **userId** (query): 关注者ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "datas": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
      }
    ],
    "total": 356,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 18
  }
}
```

---

## 统计信息 API

### 8. 获取关注统计 💡 缓存优化
**接口路径**: `GET /api/v1/follow/statistics`  
**接口描述**: 获取用户的关注统计信息，包括关注数量和粉丝数量

#### 查询参数
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "userId": 12345,
    "followingCount": 356,
    "followersCount": 1250,
    "mutualFollowCount": 89,
    "lastUpdateTime": "2024-01-16T15:30:00"
  }
}
```

---

### 9. 获取关注数量 💡 缓存优化
**接口路径**: `GET /api/v1/follow/following/count`  
**接口描述**: 获取用户关注的人数

#### 查询参数
- **userId** (query): 关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": 356
}
```

---

### 10. 获取粉丝数量 💡 缓存优化
**接口路径**: `GET /api/v1/follow/followers/count`  
**接口描述**: 获取关注某用户的人数

#### 查询参数
- **userId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": 1250
}
```

---

### 11. 批量检查关注状态 💡 缓存优化
**接口路径**: `POST /api/v1/follow/check/batch`  
**接口描述**: 批量检查用户对多个目标用户的关注状态

#### 查询参数
- **followerId** (query): 关注者ID，必填

#### 请求参数
```json
[67890, 67891, 67892, 67893]
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "67890": true,
    "67891": false,
    "67892": true,
    "67893": false
  }
}
```

---

## 互相关注 API

### 12. 获取互相关注列表 💡 缓存优化
**接口路径**: `GET /api/v1/follow/mutual`  
**接口描述**: 获取与指定用户互相关注的用户列表

#### 查询参数
- **userId** (query): 用户ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "datas": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
      }
    ],
    "total": 89,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5
  }
}
```

---

## 管理功能 API

### 13. 清理已取消的关注记录 💡 缓存优化
**接口路径**: `DELETE /api/v1/follow/clean`  
**接口描述**: 物理删除cancelled状态的关注记录

#### 查询参数
- **days** (query): 保留天数，默认30

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "清理完成",
  "data": 1250
}
```

---

## 特殊检测接口 🔥

### 14. 检测是否被特定用户关注 🔥
**接口路径**: `GET /api/v1/follow/detect/is-followed-by`  
**接口描述**: 检测用户是否被特定用户关注

#### 查询参数
- **userId** (query): 用户ID（被关注者），必填
- **checkUserId** (query): 检测用户ID（潜在关注者），必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": true
}
```

---

### 15. 检测是否关注了特定用户 🔥
**接口路径**: `GET /api/v1/follow/detect/is-following`  
**接口描述**: 检测用户是否关注了特定用户

#### 查询参数
- **userId** (query): 用户ID（关注者），必填
- **targetUserId** (query): 目标用户ID（被关注者），必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": false
}
```

---

### 16. 检测双向关注关系 🔥
**接口路径**: `GET /api/v1/follow/detect/relationship`  
**接口描述**: 检测两个用户之间的完整关注关系

#### 查询参数
- **userId1** (query): 用户1 ID，必填
- **userId2** (query): 用户2 ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "user1FollowsUser2": true,
    "user2FollowsUser1": false,
    "isMutualFollow": false
  }
}
```

---

### 17. 批量检测关注状态 🔥
**接口路径**: `POST /api/v1/follow/detect/batch-status`  
**接口描述**: 批量检测用户对多个目标用户的关注状态

#### 查询参数
- **userId** (query): 当前用户ID，必填

#### 请求参数
```json
[67890, 67891, 67892, 67893]
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "statusMap": {
      "67890": true,
      "67891": false,
      "67892": true,
      "67893": false
    },
    "statistics": {
      "totalChecked": 4,
      "followingCount": 2,
      "notFollowingCount": 2,
      "followingRate": 0.5
    }
  }
}
```

---

### 18. 检测用户关注活跃度 🔥
**接口路径**: `GET /api/v1/follow/detect/activity`  
**接口描述**: 检测用户在指定时间内的关注活跃度

#### 查询参数
- **userId** (query): 用户ID，必填
- **days** (query): 统计天数，默认7

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "userId": 12345,
    "statisticsDays": 7,
    "baseStatistics": {
      "followingCount": 356,
      "followersCount": 1250
    },
    "activityLevel": "HIGH",
    "recommendations": [
      "保持当前的关注活跃度"
    ]
  }
}
```

---

## 核心错误码说明

### 关注核心错误码
| 错误码 | 说明 |
|--------|------|
| FOLLOW_ERROR | 关注操作失败 |
| UNFOLLOW_ERROR | 取消关注失败 |
| CHECK_FOLLOW_ERROR | 检查关注关系失败 |
| GET_FOLLOW_ERROR | 获取关注详情失败 |
| QUERY_FOLLOWS_ERROR | 分页查询关注记录失败 |

### 列表查询错误码
| 错误码 | 说明 |
|--------|------|
| GET_FOLLOWERS_ERROR | 获取关注者列表失败 |
| GET_FOLLOWING_ERROR | 获取关注列表失败 |
| GET_MUTUAL_ERROR | 获取互相关注列表失败 |
| BATCH_CHECK_FOLLOW_ERROR | 批量检查关注状态失败 |

### 统计信息错误码
| 错误码 | 说明 |
|--------|------|
| GET_STATISTICS_ERROR | 获取关注统计失败 |
| GET_FOLLOWING_COUNT_ERROR | 获取关注数量失败 |
| GET_FOLLOWERS_COUNT_ERROR | 获取粉丝数量失败 |

### 管理功能错误码
| 错误码 | 说明 |
|--------|------|
| CLEAN_FOLLOWS_ERROR | 清理已取消的关注记录失败 |

### 特殊检测错误码 🔥
| 错误码 | 说明 |
|--------|------|
| DETECT_FOLLOWED_ERROR | 检测是否被关注失败 |
| DETECT_FOLLOWING_ERROR | 检测是否关注失败 |
| DETECT_RELATIONSHIP_ERROR | 检测双向关注关系失败 |
| BATCH_DETECT_ERROR | 批量检测关注状态失败 |
| DETECT_ACTIVITY_ERROR | 检测关注活跃度失败 |

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
| FOLLOW_PARAM_ERROR | 关注参数验证失败 |
| FOLLOW_STATE_ERROR | 关注状态检查失败 |
| FOLLOW_NOT_FOUND | 关注关系不存在 |
| USER_NOT_FOUND | 用户不存在 |
| CANNOT_FOLLOW_SELF | 不能关注自己 |
| ALREADY_FOLLOWED | 已经关注该用户 |
| NOT_FOLLOWED | 尚未关注该用户 |

---

## 数据模型

### FollowResponse
```typescript
interface FollowResponse {
  id: number;                    // 关注关系ID
  followerId: number;            // 关注者ID
  followeeId: number;            // 被关注者ID
  status: string;                // 关注状态（active/cancelled）
  source?: string;               // 关注来源
  remark?: string;               // 关注备注
  createTime: string;            // 创建时间（ISO 8601格式）
  updateTime: string;            // 更新时间（ISO 8601格式）
}
```

### PageResponse
```typescript
interface PageResponse<T> {
  datas: T[];                    // 数据列表
  total: number;                 // 总记录数
  currentPage: number;           // 当前页码
  pageSize: number;              // 每页大小
  totalPage: number;             // 总页数
}
```

### FollowStatistics
```typescript
interface FollowStatistics {
  userId: number;                // 用户ID
  followingCount: number;        // 关注数量
  followersCount: number;        // 粉丝数量
  mutualFollowCount: number;     // 互关数量
  lastUpdateTime: string;        // 最后更新时间
}
```

### RelationshipDetection 🔥
```typescript
interface RelationshipDetection {
  user1FollowsUser2: boolean;    // 用户1是否关注用户2
  user2FollowsUser1: boolean;    // 用户2是否关注用户1
  isMutualFollow: boolean;       // 是否互相关注
}
```

### BatchDetectionResult 🔥
```typescript
interface BatchDetectionResult {
  statusMap: Record<string, boolean>;  // 关注状态映射
  statistics: {
    totalChecked: number;              // 检查总数
    followingCount: number;            // 关注数量
    notFollowingCount: number;         // 未关注数量
    followingRate: number;             // 关注率
  };
}
```

### ActivityDetection 🔥
```typescript
interface ActivityDetection {
  userId: number;                      // 用户ID
  statisticsDays: number;              // 统计天数
  baseStatistics: FollowStatistics;   // 基础统计
  activityLevel: string;               // 活跃度等级（HIGH/MEDIUM/LOW）
  recommendations: string[];           // 推荐建议
}
```

---

## 使用示例

### 关注用户流程
```javascript
// 1. 检查是否已关注
const checkResponse = await fetch('/api/v1/follow/check?followerId=12345&followeeId=67890');
const isFollowing = await checkResponse.json();

if (!isFollowing.data) {
  // 2. 关注用户
  const followResponse = await fetch('/api/v1/follow/follow', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      followerId: 12345,
      followeeId: 67890,
      source: "profile",
      remark: "技术大牛"
    })
  });
  
  const followResult = await followResponse.json();
  console.log("关注成功:", followResult.data);
}

// 3. 获取关注统计
const statsResponse = await fetch('/api/v1/follow/statistics?userId=12345');
const stats = await statsResponse.json();
console.log("关注统计:", stats.data);
```

### 获取关注列表
```javascript
// 获取我的关注列表
const followingResponse = await fetch('/api/v1/follow/following?userId=12345&page=1&size=20');
const following = await followingResponse.json();

// 获取我的粉丝列表
const followersResponse = await fetch('/api/v1/follow/followers?userId=12345&page=1&size=20');
const followers = await followersResponse.json();

// 获取互相关注的朋友
const mutualResponse = await fetch('/api/v1/follow/mutual?userId=12345&page=1&size=20');
const mutualFriends = await mutualResponse.json();
```

### 特殊检测功能 🔥
```javascript
// 检测双向关注关系
const relationshipResponse = await fetch('/api/v1/follow/detect/relationship?userId1=12345&userId2=67890');
const relationship = await relationshipResponse.json();

if (relationship.data.isMutualFollow) {
  console.log("互相关注");
} else if (relationship.data.user1FollowsUser2) {
  console.log("我关注了他");
} else if (relationship.data.user2FollowsUser1) {
  console.log("他关注了我");
} else {
  console.log("没有关注关系");
}

// 批量检测关注状态（增强版）
const batchResponse = await fetch('/api/v1/follow/detect/batch-status?userId=12345', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify([67890, 67891, 67892, 67893])
});

const batchResult = await batchResponse.json();
console.log("关注状态:", batchResult.data.statusMap);
console.log("统计信息:", batchResult.data.statistics);

// 检测用户活跃度
const activityResponse = await fetch('/api/v1/follow/detect/activity?userId=12345&days=7');
const activity = await activityResponse.json();
console.log("活跃度等级:", activity.data.activityLevel);
console.log("推荐建议:", activity.data.recommendations);
```

### 批量操作
```javascript
// 传统批量检查关注状态
const userIds = [67890, 67891, 67892];
const batchResponse = await fetch('/api/v1/follow/check/batch?followerId=12345', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify(userIds)
});

const batchStatus = await batchResponse.json();

// 根据结果显示不同的按钮状态
userIds.forEach(userId => {
  const isFollowing = batchStatus.data[userId];
  updateFollowButton(userId, isFollowing);
});
```

---

## 版本更新日志

### v2.0.0 (2024-01-16) - 缓存增强版
- ✅ 集成JetCache分布式缓存
- ✅ 添加缓存注解和优化策略
- ✅ 无连表设计的高性能架构
- ✅ 标准化API请求响应格式
- ✅ 门面服务模式集成
- ✅ 完善的错误处理机制
- ✅ 新增特殊检测接口 🔥
  - 检测是否被特定用户关注
  - 检测是否关注了特定用户
  - 检测双向关注关系
  - 批量检测关注状态（增强版）
  - 检测用户关注活跃度
- ✅ 性能监控和日志增强
- ✅ 互关关系智能识别
- ✅ 活跃度分析和推荐系统

### v1.0.0 (2024-01-01) - 基础版
- ✅ 基础关注管理功能
- ✅ 简单的关注关系查询
- ✅ 基本的API接口

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (缓存增强版)  
**服务状态**: ✅ 生产可用