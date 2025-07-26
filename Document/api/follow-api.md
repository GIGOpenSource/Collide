# Follow 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Follow 模块是 Collide 社交平台的核心模块之一，负责处理用户之间的关注关系管理。

### 主要功能
- 用户关注/取消关注
- 关注关系查询
- 关注列表/粉丝列表分页查询
- 关注统计数据管理
- 相互关注检测

### 技术架构
- **框架**: Spring Boot 3.x + Spring Cloud
- **数据库**: MySQL 8.0（去连表化设计）
- **ORM**: MyBatis Plus
- **RPC**: Apache Dubbo
- **缓存**: JetCache（两级缓存）
- **锁机制**: Redis 分布式锁
- **认证**: Sa-Token
- **文档**: OpenAPI 3.0

### 去连表化设计优势
- **性能提升**: 避免复杂 JOIN 查询，单表查询性能提升 10x+
- **数据冗余**: 统计数据实时可用，无需聚合计算
- **扩展性强**: 服务独立，易于水平扩展
- **缓存友好**: 单表数据易于缓存，命中率更高

---

## 🗄️ 数据库设计

### 关注表 (t_follow) - 去连表化设计

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| id | BIGINT(20) | 是 | AUTO_INCREMENT | 关注ID，主键 |
| follower_user_id | BIGINT(20) | 是 | - | 关注者用户ID |
| followed_user_id | BIGINT(20) | 是 | - | 被关注者用户ID |
| follow_type | TINYINT(1) | 是 | 1 | 关注类型：1-普通关注，2-特别关注 |
| status | TINYINT(1) | 是 | 1 | 状态：0-已取消，1-正常，2-已屏蔽 |
| created_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT(1) | 是 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计** (去连表化优化):
- PRIMARY KEY: `id`
- UNIQUE KEY: `uk_follower_followed` (`follower_user_id`, `followed_user_id`)
- INDEX: `idx_follower_user_id`, `idx_followed_user_id`, `idx_status`, `idx_follow_type`
- INDEX: `idx_created_time`, `idx_updated_time`, `idx_deleted`
- **覆盖索引**: `idx_follower_status_cover` (`follower_user_id`, `status`, `deleted`, `followed_user_id`, `follow_type`, `created_time`)
- **覆盖索引**: `idx_followed_status_cover` (`followed_user_id`, `status`, `deleted`, `follower_user_id`, `follow_type`, `created_time`)
- **相互关注优化**: `idx_mutual_follow` (`follower_user_id`, `followed_user_id`, `status`, `deleted`)

### 关注统计表 (t_follow_statistics) - 冗余设计优化

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| user_id | BIGINT(20) | 是 | - | 用户ID，主键 |
| following_count | INT(11) | 是 | 0 | 关注数（我关注的人数） |
| follower_count | INT(11) | 是 | 0 | 粉丝数（关注我的人数） |
| mutual_follow_count | INT(11) | 是 | 0 | 相互关注数（预留字段） |
| created_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| updated_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT(1) | 是 | 0 | 逻辑删除：0-未删除，1-已删除 |

**索引设计** (统计查询优化):
- PRIMARY KEY: `user_id`
- INDEX: `idx_following_count`, `idx_follower_count`, `idx_created_time`, `idx_updated_time`, `idx_deleted`
- **排行榜优化**: `idx_follower_ranking` (`follower_count` DESC, `deleted`, `user_id`)
- **排行榜优化**: `idx_following_ranking` (`following_count` DESC, `deleted`, `user_id`)
- **活跃用户统计**: `idx_active_users` (`following_count`, `follower_count`, `deleted`)

---

## 🔗 接口列表

### 1. 关注用户

**接口描述**: 当前用户关注指定用户

**请求信息**:
- **URL**: `POST /api/v1/follow/follow`
- **Content-Type**: `application/json`
- **需要认证**: 是

**请求参数**:
```json
{
  "followedUserId": 12345,
  "followType": 1
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| followedUserId | Long | 是 | 被关注者用户ID |
| followType | Integer | 否 | 关注类型：1-普通关注，2-特别关注（默认：1） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "responseMessage": "关注成功",
    "followId": 67890,
    "newFollow": true,
    "mutualFollow": false
  }
}
```

**响应字段说明**:

| 字段名 | 类型 | 说明 |
|--------|------|------|
| success | Boolean | 操作是否成功 |
| responseMessage | String | 响应消息 |
| followId | Long | 关注记录ID |
| newFollow | Boolean | 是否为新关注（false表示重新激活） |
| mutualFollow | Boolean | 是否形成相互关注 |

---

### 2. 取消关注

**接口描述**: 当前用户取消关注指定用户

**请求信息**:
- **URL**: `POST /api/v1/follow/unfollow`
- **Content-Type**: `application/json`
- **需要认证**: 是

**请求参数**:
```json
{
  "followedUserId": 12345
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| followedUserId | Long | 是 | 被关注者用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "success": true,
    "responseMessage": "取消关注成功"
  }
}
```

---

### 3. 检查关注关系

**接口描述**: 检查当前用户是否关注指定用户

**请求信息**:
- **URL**: `GET /api/v1/follow/check/{followedUserId}`
- **需要认证**: 是

**路径参数**:

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| followedUserId | Long | 是 | 被关注者用户ID |

**查询参数**:

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| checkMutual | Boolean | 否 | true | 是否检查相互关注 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 67890,
    "followerUserId": 9999,
    "followedUserId": 12345,
    "followType": 1,
    "status": 1,
    "mutualFollow": true,
    "createTime": "2024-01-15T10:30:00"
  }
}
```

**注意**: 如果未关注，data 字段为 null。

---

### 4. 获取关注列表

**接口描述**: 分页获取当前用户的关注列表

**请求信息**:
- **URL**: `GET /api/v1/follow/following`
- **需要认证**: 是

**查询参数**:

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| pageNo | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 每页大小（最大100） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 67890,
        "followerUserId": 9999,
        "followedUserId": 12345,
        "followType": 1,
        "status": 1,
        "mutualFollow": true,
        "createTime": "2024-01-15T10:30:00"
      }
    ],
    "total": 156,
    "size": 20,
    "current": 1,
    "pages": 8
  }
}
```

---

### 5. 获取粉丝列表

**接口描述**: 分页获取当前用户的粉丝列表

**请求信息**:
- **URL**: `GET /api/v1/follow/followers`
- **需要认证**: 是

**查询参数**:

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| pageNo | Integer | 否 | 1 | 页码 |
| pageSize | Integer | 否 | 20 | 每页大小（最大100） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 67891,
        "followerUserId": 12346,
        "followedUserId": 9999,
        "followType": 1,
        "status": 1,
        "mutualFollow": false,
        "createTime": "2024-01-14T15:20:00"
      }
    ],
    "total": 89,
    "size": 20,
    "current": 1,
    "pages": 5
  }
}
```

---

### 6. 获取关注统计

**接口描述**: 获取当前用户的关注统计信息

**请求信息**:
- **URL**: `GET /api/v1/follow/statistics`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 9999,
    "followingCount": 156,
    "followerCount": 89,
    "mutualFollowCount": 0
  }
}
```

---

### 7. 获取用户关注统计

**接口描述**: 获取指定用户的关注统计信息

**请求信息**:
- **URL**: `GET /api/v1/follow/statistics/{userId}`
- **需要认证**: 是

**路径参数**:

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| userId | Long | 是 | 用户ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 12345,
    "followingCount": 203,
    "followerCount": 456,
    "mutualFollowCount": 0
  }
}
```

---

### 8. 批量检查关注关系

**接口描述**: 批量检查当前用户与指定用户列表的关注关系

**请求信息**:
- **URL**: `POST /api/v1/follow/batch-check`
- **Content-Type**: `application/json`
- **需要认证**: 是

**请求参数**:
```json
{
  "userIds": [12345, 12346, 12347, 12348]
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| userIds | List&lt;Long&gt; | 是 | 要检查的用户ID列表（最多100个） |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67890,
      "followerUserId": 9999,
      "followedUserId": 12345,
      "followType": 1,
      "status": 1,
      "mutualFollow": true,
      "createTime": "2024-01-15T10:30:00"
    },
    {
      "id": 67891,
      "followerUserId": 9999,
      "followedUserId": 12346,
      "followType": 1,
      "status": 1,
      "mutualFollow": false,
      "createTime": "2024-01-14T15:20:00"
    }
  ]
}
```

**注意**: 只返回当前用户已关注的用户信息，未关注的用户不在结果列表中。

---

### 9. 获取相互关注列表

**接口描述**: 获取当前用户的相互关注列表

**请求信息**:
- **URL**: `GET /api/v1/follow/mutual`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 67892,
      "followerUserId": 9999,
      "followedUserId": 12345,
      "followType": 1,
      "status": 1,
      "mutualFollow": true,
      "createTime": "2024-01-15T10:30:00"
    },
    {
      "id": 67893,
      "followerUserId": 9999,
      "followedUserId": 12350,
      "followType": 2,
      "status": 1,
      "mutualFollow": true,
      "createTime": "2024-01-13T09:15:00"
    }
  ]
}
```

---

## 📊 数据模型

### FollowInfo

关注信息对象

```json
{
  "id": 67890,
  "followerUserId": 9999,
  "followedUserId": 12345,
  "followType": 1,
  "status": 1,
  "mutualFollow": true,
  "followerUser": null,
  "followedUser": null,
  "createTime": "2024-01-15T10:30:00",
  "updateTime": "2024-01-15T10:30:00"
}
```

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 关注记录ID |
| followerUserId | Long | 关注者用户ID |
| followedUserId | Long | 被关注者用户ID |
| followType | Integer | 关注类型：1-普通关注，2-特别关注 |
| status | Integer | 状态：0-已取消，1-正常，2-已屏蔽 |
| mutualFollow | Boolean | 是否相互关注 |
| followerUser | UserInfo | 关注者用户信息（可选） |
| followedUser | UserInfo | 被关注者用户信息（可选） |
| createTime | DateTime | 创建时间 |
| updateTime | DateTime | 更新时间 |

### FollowStatistics

关注统计对象

```json
{
  "userId": 9999,
  "followingCount": 156,
  "followerCount": 89,
  "mutualFollowCount": 12,
  "createTime": "2024-01-10T08:00:00",
  "updateTime": "2024-01-15T10:30:00"
}
```

| 字段名 | 类型 | 说明 |
|--------|------|------|
| userId | Long | 用户ID |
| followingCount | Integer | 关注数（我关注的人数） |
| followerCount | Integer | 粉丝数（关注我的人数） |
| mutualFollowCount | Integer | 相互关注数（预留字段） |
| createTime | DateTime | 创建时间 |
| updateTime | DateTime | 更新时间 |

---

## ❌ 错误码定义

### 通用错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| 200 | 200 | 成功 |
| 400 | 400 | 请求参数错误 |
| 401 | 401 | 未登录或登录已过期 |
| 403 | 403 | 权限不足 |
| 500 | 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 说明 |
|--------|------|
| FOLLOW_ERROR | 关注操作失败 |
| UNFOLLOW_ERROR | 取消关注操作失败 |
| PARAM_ERROR | 参数错误 |
| USER_NOT_FOUND | 用户不存在 |

---

## 💡 使用示例

### 1. 用户关注流程示例

```bash
# 1. 用户登录获取token
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 2. 关注用户
curl -X POST "http://localhost:8080/api/v1/follow/follow" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "followedUserId": 12345,
    "followType": 1
  }'

# 3. 检查关注状态
curl -X GET "http://localhost:8080/api/v1/follow/check/12345?checkMutual=true" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 4. 查看关注列表
curl -X GET "http://localhost:8080/api/v1/follow/following?pageNo=1&pageSize=20" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 5. 查看关注统计
curl -X GET "http://localhost:8080/api/v1/follow/statistics" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. JavaScript 调用示例

```javascript
// 关注用户
async function followUser(followedUserId) {
  try {
    const response = await fetch('/api/v1/follow/follow', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        followedUserId: followedUserId,
        followType: 1
      })
    });

    const result = await response.json();
    
    if (result.code === 200 && result.data.success) {
      console.log('关注成功:', result.data);
      if (result.data.mutualFollow) {
        console.log('已形成相互关注！');
      }
    } else {
      console.error('关注失败:', result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}

// 获取关注列表
async function getFollowingList(pageNo = 1, pageSize = 20) {
  try {
    const response = await fetch(
      `/api/v1/follow/following?pageNo=${pageNo}&pageSize=${pageSize}`,
      {
        headers: {
          'Authorization': `Bearer ${getToken()}`
        }
      }
    );

    const result = await response.json();
    
    if (result.code === 200) {
      console.log('关注列表:', result.data);
      return result.data;
    } else {
      console.error('获取关注列表失败:', result.message);
    }
  } catch (error) {
    console.error('请求失败:', error);
  }
}
```

---

## 📝 注意事项

### 1. 幂等性
- 重复关注同一用户不会产生错误，返回现有关注记录
- 取消不存在的关注关系会返回失败状态

### 2. 性能建议（去连表化优化）
- **查询性能**: 去连表化设计，查询性能提升 **10x+**
- **分页查询**: 关注列表查询建议每页不超过100条
- **批量操作**: 批量检查关注关系最多支持100个用户ID
- **缓存策略**: 
  - 关注关系查询使用两级缓存（本地+远程）
  - 统计数据缓存30分钟
  - 热点用户数据预热缓存
- **索引优化**: 使用覆盖索引减少回表查询

### 3. 数据一致性（分布式锁保障）
- **幂等性保障**: 使用 Redis 分布式锁防止并发重复操作
- **事务处理**: 关注操作采用事务确保数据一致性
- **统计同步**: 统计数据通过 UPSERT 语法保持同步
- **数据修复**: 支持统计数据重新计算功能
- **异步处理**: 关注事件发布，异步处理相关业务

### 4. 安全性
- 所有接口都需要用户认证
- 防止用户关注自己
- 参数验证和SQL注入防护

---

## 📞 技术支持

- **开发团队**: Collide Team
- **文档版本**: v1.0
- **更新日期**: 2024-01-15
- **联系方式**: tech@collide.com

---

*本文档基于 Follow 模块 v1.0.0 版本生成，如有疑问请联系技术团队。* 