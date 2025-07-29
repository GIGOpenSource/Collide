# Collide 关注服务 API 文档

## 概述

Collide 关注服务提供完整的用户关注功能，包括关注用户、取消关注、关注关系查询、粉丝列表管理、关注统计等核心功能。支持互相关注检测和关注关系分析。

**服务版本**: v2.0  
**基础路径**: `/api/follow`  
**设计理念**: 简洁高效的关注系统，支持大规模用户关注关系管理，提供丰富的关注数据分析

---

## 关注基础功能 API

### 1. 关注用户
**接口路径**: `POST /api/follow/follow`  
**接口描述**: 用户关注另一个用户，建立关注关系

#### 请求参数
```json
{
  "followerId": 12345,     // 必填，关注者用户ID
  "followeeId": 67890,     // 必填，被关注者用户ID
  "source": "profile",     // 可选，关注来源（profile/recommend/search）
  "remark": "技术大牛"      // 可选，关注备注
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "关注成功",
  "data": {
    "id": 98765,
    "followerId": 12345,
    "followerUsername": "user123",
    "followerNickname": "小明",
    "followeeId": 67890,
    "followeeUsername": "techguru",
    "followeeNickname": "技术大牛",
    "status": "active",
    "source": "profile",
    "remark": "技术大牛",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00",
    "mutualFollow": false
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "ALREADY_FOLLOWED",
  "responseMessage": "您已经关注了该用户"
}
```

---

### 2. 取消关注
**接口路径**: `POST /api/follow/unfollow`  
**接口描述**: 用户取消关注另一个用户，解除关注关系

#### 请求参数
```json
{
  "followerId": 12345,     // 必填，关注者用户ID
  "followeeId": 67890      // 必填，被关注者用户ID
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "取消关注成功"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "NOT_FOLLOWED",
  "responseMessage": "您尚未关注该用户"
}
```

---

## 关注关系查询 API

### 3. 检查关注关系
**接口路径**: `GET /api/follow/check`  
**接口描述**: 检查两个用户之间是否存在关注关系

#### 查询参数
- **followerId** (query): 关注者ID，必填
- **followeeId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": true
}
```

---

### 4. 获取关注详情
**接口路径**: `GET /api/follow/detail`  
**接口描述**: 获取关注关系的详细信息

#### 查询参数
- **followerId** (query): 关注者ID，必填
- **followeeId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 98765,
    "followerId": 12345,
    "followerUsername": "user123",
    "followerNickname": "小明",
    "followeeId": 67890,
    "followeeUsername": "techguru",
    "followeeNickname": "技术大牛",
    "status": "active",
    "source": "profile",
    "remark": "技术大牛",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00",
    "mutualFollow": true
  }
}
```

---

### 5. 分页查询关注记录
**接口路径**: `GET /api/follow/query`  
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
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 156,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 8,
    "list": [
      {
        "id": 98765,
        "followerId": 12345,
        "followerUsername": "user123",
        "followeeId": 67890,
        "followeeUsername": "techguru",
        "status": "active",
        "createTime": "2024-01-16T10:30:00",
        "mutualFollow": true
      }
    ]
  }
}
```

---

## 关注者列表 API

### 6. 获取关注者列表（谁关注了我）
**接口路径**: `GET /api/follow/followers`  
**接口描述**: 获取指定用户的关注者分页列表

#### 查询参数
- **userId** (query): 被关注者ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 1250,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 63,
    "list": [
      {
        "id": 98765,
        "followerId": 12345,
        "followerUsername": "user123",
        "followerNickname": "小明",
        "followerAvatar": "https://avatar.example.com/user123.jpg",
        "followeeId": 67890,
        "status": "active",
        "createTime": "2024-01-16T10:30:00",
        "mutualFollow": true,
        "remark": "技术大牛"
      }
    ]
  }
}
```

---

### 7. 获取关注列表（我关注了谁）
**接口路径**: `GET /api/follow/following`  
**接口描述**: 获取指定用户的关注列表分页数据

#### 查询参数
- **userId** (query): 关注者ID，必填
- **page** (query): 页码，默认1
- **size** (query): 每页大小，默认20

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 356,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 18,
    "list": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "followeeUsername": "techguru",
        "followeeNickname": "技术大牛",
        "followeeAvatar": "https://avatar.example.com/techguru.jpg",
        "status": "active",
        "createTime": "2024-01-16T10:30:00",
        "mutualFollow": true,
        "source": "profile",
        "remark": "技术大牛"
      }
    ]
  }
}
```

---

## 关注统计功能 API

### 8. 获取关注统计
**接口路径**: `GET /api/follow/statistics`  
**接口描述**: 获取用户的关注统计信息，包括关注数量和粉丝数量

#### 查询参数
- **userId** (query): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "userId": 12345,
    "followingCount": 356,        // 关注数量
    "followersCount": 1250,       // 粉丝数量
    "mutualFollowCount": 89,      // 互关数量
    "newFollowersToday": 15,      // 今日新增粉丝
    "newFollowingToday": 3,       // 今日新增关注
    "growthRate": 12.5,           // 粉丝增长率（%）
    "lastFollowTime": "2024-01-16T15:30:00", // 最后关注时间
    "lastFollowedTime": "2024-01-16T14:20:00" // 最后被关注时间
  }
}
```

---

### 9. 获取关注数量
**接口路径**: `GET /api/follow/following/count`  
**接口描述**: 获取用户关注的人数

#### 查询参数
- **userId** (query): 关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": 356
}
```

---

### 10. 获取粉丝数量
**接口路径**: `GET /api/follow/followers/count`  
**接口描述**: 获取关注某用户的人数

#### 查询参数
- **userId** (query): 被关注者ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": 1250
}
```

---

## 批量操作 API

### 11. 批量检查关注状态
**接口路径**: `POST /api/follow/check/batch`  
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
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "67890": true,
    "67891": false,
    "67892": true,
    "67893": false
  }
}
```

---

## 互相关注功能 API

### 12. 获取互相关注列表
**接口路径**: `GET /api/follow/mutual`  
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
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "total": 89,
    "pageNum": 1,
    "pageSize": 20,
    "pages": 5,
    "list": [
      {
        "id": 98765,
        "followerId": 12345,
        "followeeId": 67890,
        "followeeUsername": "techguru",
        "followeeNickname": "技术大牛",
        "followeeAvatar": "https://avatar.example.com/techguru.jpg",
        "mutualFollowTime": "2024-01-16T10:30:00", // 双向关注建立时间
        "followTime": "2024-01-15T10:30:00",        // 我关注他的时间
        "followedTime": "2024-01-16T10:30:00"       // 他关注我的时间
      }
    ]
  }
}
```

---

## 管理功能 API

### 13. 清理已取消的关注记录
**接口路径**: `DELETE /api/follow/clean`  
**接口描述**: 物理删除cancelled状态的关注记录

#### 查询参数
- **days** (query): 保留天数，默认30

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "清理完成",
  "data": 1250
}
```

---

## 错误码说明

| 错误码 | 错误描述 | 解决方案 |
|--------|----------|----------|
| USER_NOT_FOUND | 用户不存在 | 检查用户ID是否正确 |
| ALREADY_FOLLOWED | 已经关注该用户 | 用户已经在关注列表中 |
| NOT_FOLLOWED | 尚未关注该用户 | 无法取消未关注的用户 |
| CANNOT_FOLLOW_SELF | 不能关注自己 | 关注者和被关注者不能是同一人 |
| FOLLOW_LIMIT_EXCEEDED | 关注数量超出限制 | 减少关注数量或升级账户 |
| FOLLOW_PERMISSION_DENIED | 无关注权限 | 检查用户权限或账户状态 |
| FOLLOWEE_PRIVACY_PROTECTED | 被关注者设置了隐私保护 | 需要发送关注申请 |

---

## 数据模型

### FollowResponse
```typescript
interface FollowResponse {
  id: number;                    // 关注关系ID
  followerId: number;            // 关注者ID
  followerUsername?: string;     // 关注者用户名
  followerNickname?: string;     // 关注者昵称
  followerAvatar?: string;       // 关注者头像
  followeeId: number;            // 被关注者ID
  followeeUsername?: string;     // 被关注者用户名
  followeeNickname?: string;     // 被关注者昵称
  followeeAvatar?: string;       // 被关注者头像
  status: string;                // 关注状态（active/cancelled）
  source?: string;               // 关注来源
  remark?: string;               // 关注备注
  createTime: string;            // 创建时间（ISO 8601格式）
  updateTime: string;            // 更新时间（ISO 8601格式）
  mutualFollow: boolean;         // 是否互相关注
  mutualFollowTime?: string;     // 双向关注建立时间
  followTime?: string;           // 关注时间
  followedTime?: string;         // 被关注时间
}
```

### FollowStatistics
```typescript
interface FollowStatistics {
  userId: number;                // 用户ID
  followingCount: number;        // 关注数量
  followersCount: number;        // 粉丝数量
  mutualFollowCount: number;     // 互关数量
  newFollowersToday: number;     // 今日新增粉丝
  newFollowingToday: number;     // 今日新增关注
  growthRate: number;            // 粉丝增长率（%）
  lastFollowTime: string;        // 最后关注时间
  lastFollowedTime: string;      // 最后被关注时间
}
```

---

## 使用示例

### 关注用户流程
```javascript
// 1. 检查是否已关注
const isFollowing = await checkFollowStatus(12345, 67890);

if (!isFollowing.data) {
  // 2. 关注用户
  const followResult = await followUser({
    followerId: 12345,
    followeeId: 67890,
    source: "profile",
    remark: "技术大牛"
  });
  
  console.log("关注成功:", followResult.data);
}

// 3. 获取关注统计
const stats = await getFollowStatistics(12345);
console.log("关注统计:", stats.data);
```

### 获取关注列表
```javascript
// 获取我的关注列表
const following = await getFollowing(12345, {
  page: 1,
  size: 20
});

// 获取我的粉丝列表
const followers = await getFollowers(12345, {
  page: 1,
  size: 20
});

// 获取互相关注的朋友
const mutualFriends = await getMutualFollows(12345, {
  page: 1,
  size: 20
});
```

### 批量操作
```javascript
// 批量检查关注状态
const userIds = [67890, 67891, 67892];
const batchStatus = await batchCheckFollowStatus(12345, userIds);

// 根据结果显示不同的按钮状态
userIds.forEach(userId => {
  const isFollowing = batchStatus.data[userId];
  // 更新UI显示
  updateFollowButton(userId, isFollowing);
});
```