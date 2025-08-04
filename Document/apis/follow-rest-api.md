# Follow REST API 接口文档

## 文档信息
- **模块名称**: 关注管理模块 (Follow)
- **API版本**: v1
- **文档版本**: 2.0.0
- **最后更新**: 2024-01-01

## 概述

关注管理模块提供完整的用户关注功能HTTP接口，基于 `follow-simple.sql` 的单表设计，通过 `FollowFacadeService` 提供缓存优化的业务处理。

### 功能模块
- **基础操作**: 关注/取消关注/检查状态/获取详情
- **列表查询**: 关注列表/粉丝列表/互关列表/分页查询
- **搜索功能**: 昵称搜索/关系链查询/批量检查
- **统计功能**: 关注数/粉丝数/关注统计/活跃度检测
- **管理功能**: 用户信息同步/数据清理/关系激活/参数验证
- **特殊检测**: 双向关系/批量状态/活跃度分析

### API设计特点
- 统一的 `Result<T>` 响应格式
- 标准化的分页参数（currentPage, pageSize）
- 完整的参数验证和错误处理
- 详细的操作日志记录
- Swagger/OpenAPI文档支持
- 缓存优化 💡 标识的接口使用了缓存

## 基本信息

- **Base URL**: `/api/v1/follow`
- **Content-Type**: `application/json`
- **响应格式**: 统一使用 `Result<T>` 包装

### 通用响应结构

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {...},
  "timestamp": 1699999999999
}
```

### 分页响应结构

```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 10,
    "total": 200,
    "datas": [...]
  }
}
```

## 接口列表

### 1. 基础操作

#### 1.1 关注用户

**接口描述**: 用户关注另一个用户，建立关注关系  

**基本信息**:
- **接口地址**: `POST /api/v1/follow/follow`
- **缓存优化**: ✅

**请求参数**:
```json
{
  "followerId": 1001,
  "followeeId": 1002,
  "nickname": "用户昵称",
  "avatar": "头像URL"
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |
| nickname | String | ❌ | 昵称（冗余字段） |
| avatar | String | ❌ | 头像（冗余字段） |

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "关注成功",
  "data": {
    "id": 1,
    "followerId": 1001,
    "followeeId": 1002,
    "followerNickname": "关注者昵称",
    "followerAvatar": "关注者头像",
    "followeeNickname": "被关注者昵称",
    "followeeAvatar": "被关注者头像",
    "status": "active",
    "followTime": "2024-01-01T10:00:00"
  }
}
```

**错误响应**:
- `FOLLOW_ERROR`: 关注操作失败

---

#### 1.2 取消关注

**接口描述**: 用户取消关注另一个用户，解除关注关系

**基本信息**:
- **接口地址**: `POST /api/v1/follow/unfollow`
- **缓存优化**: ✅

**请求参数**:
```json
{
  "followerId": 1001,
  "followeeId": 1002
}
```

**参数说明**:
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| followerId | Long | ✅ | 关注者ID |
| followeeId | Long | ✅ | 被关注者ID |

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "取消关注成功",
  "data": null
}
```

**错误响应**:
- `UNFOLLOW_ERROR`: 取消关注失败

---

### 2. 关注关系查询

#### 2.1 检查关注关系

**接口描述**: 检查两个用户之间是否存在关注关系

**基本信息**:
- **接口地址**: `GET /api/v1/follow/check`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/check?followerId=1001&followeeId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检查完成",
  "data": true
}
```

**错误响应**:
- `CHECK_FOLLOW_ERROR`: 检查关注关系失败

---

#### 2.2 获取关注详情

**接口描述**: 获取关注关系的详细信息

**基本信息**:
- **接口地址**: `GET /api/v1/follow/detail`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/detail?followerId=1001&followeeId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": {
    "id": 1,
    "followerId": 1001,
    "followeeId": 1002,
    "followerNickname": "关注者昵称",
    "followerAvatar": "关注者头像",
    "followeeNickname": "被关注者昵称",
    "followeeAvatar": "被关注者头像",
    "status": "active",
    "followTime": "2024-01-01T10:00:00"
  }
}
```

**错误响应**:
- `GET_FOLLOW_ERROR`: 获取关注详情失败

---

#### 2.3 分页查询关注记录

**接口描述**: 支持多种条件的关注记录分页查询

**基本信息**:
- **接口地址**: `GET /api/v1/follow/query`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| followerId | Long | Query | ❌ | - | 关注者ID |
| followeeId | Long | Query | ❌ | - | 被关注者ID |
| status | String | Query | ❌ | - | 关注状态 |
| currentPage | Integer | Query | ❌ | 1 | 页码 |
| pageSize | Integer | Query | ❌ | 20 | 每页大小 |

**示例**: `/api/v1/follow/query?followerId=1001&currentPage=1&pageSize=20`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5,
    "total": 100,
    "datas": [
      {
        "id": 1,
        "followerId": 1001,
        "followeeId": 1002,
        "followerNickname": "关注者昵称",
        "followeeNickname": "被关注者昵称",
        "status": "active",
        "followTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

**错误响应**:
- `QUERY_FOLLOWS_ERROR`: 分页查询关注记录失败

---

### 3. 关注者列表

#### 3.1 获取关注者列表（谁关注了我）

**接口描述**: 获取指定用户的关注者分页列表

**基本信息**:
- **接口地址**: `GET /api/v1/follow/followers`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| followeeId | Long | Query | ✅ | - | 被关注者ID |
| currentPage | Integer | Query | ❌ | 1 | 页码 |
| pageSize | Integer | Query | ❌ | 20 | 每页大小 |

**示例**: `/api/v1/follow/followers?followeeId=1001&currentPage=1&pageSize=20`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3,
    "total": 50,
    "datas": [
      {
        "id": 1,
        "followerId": 1002,
        "followeeId": 1001,
        "followerNickname": "关注者昵称",
        "followerAvatar": "关注者头像",
        "status": "active",
        "followTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

**错误响应**:
- `GET_FOLLOWERS_ERROR`: 获取关注者列表失败

---

#### 3.2 获取关注列表（我关注了谁）

**接口描述**: 获取指定用户的关注列表分页数据

**基本信息**:
- **接口地址**: `GET /api/v1/follow/following`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| followerId | Long | Query | ✅ | - | 关注者ID |
| currentPage | Integer | Query | ❌ | 1 | 页码 |
| pageSize | Integer | Query | ❌ | 20 | 每页大小 |

**示例**: `/api/v1/follow/following?followerId=1001&currentPage=1&pageSize=20`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 2,
    "total": 30,
    "datas": [
      {
        "id": 1,
        "followerId": 1001,
        "followeeId": 1002,
        "followeeNickname": "被关注者昵称",
        "followeeAvatar": "被关注者头像",
        "status": "active",
        "followTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

**错误响应**:
- `GET_FOLLOWING_ERROR`: 获取关注列表失败

---

### 4. 统计信息

#### 4.1 获取关注统计

**接口描述**: 获取用户的关注统计信息，包括关注数量和粉丝数量

**基本信息**:
- **接口地址**: `GET /api/v1/follow/statistics`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 用户ID |

**示例**: `/api/v1/follow/statistics?userId=1001`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": {
    "followingCount": 120,
    "followersCount": 85,
    "mutualFollowCount": 45
  }
}
```

**错误响应**:
- `GET_STATISTICS_ERROR`: 获取关注统计失败

---

#### 4.2 获取关注数量

**接口描述**: 获取用户关注的人数

**基本信息**:
- **接口地址**: `GET /api/v1/follow/following/count`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 关注者ID |

**示例**: `/api/v1/follow/following/count?userId=1001`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": 120
}
```

**错误响应**:
- `GET_FOLLOWING_COUNT_ERROR`: 获取关注数量失败

---

#### 4.3 获取粉丝数量

**接口描述**: 获取关注某用户的人数

**基本信息**:
- **接口地址**: `GET /api/v1/follow/followers/count`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/followers/count?userId=1001`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": 85
}
```

**错误响应**:
- `GET_FOLLOWERS_COUNT_ERROR`: 获取粉丝数量失败

---

#### 4.4 批量检查关注状态

**接口描述**: 批量检查用户对多个目标用户的关注状态

**基本信息**:
- **接口地址**: `POST /api/v1/follow/check/batch`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeIds | List&lt;Long&gt; | Body | ✅ | 被关注者ID列表 |

**请求示例**:
```
POST /api/v1/follow/check/batch?followerId=1001
Content-Type: application/json

[1002, 1003, 1004, 1005]
```

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "批量检查完成",
  "data": {
    "1002": true,
    "1003": false,
    "1004": true,
    "1005": false
  }
}
```

**错误响应**:
- `BATCH_CHECK_FOLLOW_ERROR`: 批量检查关注状态失败

---

### 5. 互相关注

#### 5.1 获取互相关注列表

**接口描述**: 获取与指定用户互相关注的用户列表

**基本信息**:
- **接口地址**: `GET /api/v1/follow/mutual`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| userId | Long | Query | ✅ | - | 用户ID |
| currentPage | Integer | Query | ❌ | 1 | 页码 |
| pageSize | Integer | Query | ❌ | 20 | 每页大小 |

**示例**: `/api/v1/follow/mutual?userId=1001&currentPage=1&pageSize=20`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "获取成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3,
    "total": 45,
    "datas": [
      {
        "id": 1,
        "followerId": 1001,
        "followeeId": 1002,
        "followeeNickname": "互关好友昵称",
        "followeeAvatar": "互关好友头像",
        "status": "active",
        "followTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

**错误响应**:
- `GET_MUTUAL_ERROR`: 获取互相关注列表失败

---

### 6. 管理功能

#### 6.1 清理已取消的关注记录

**接口描述**: 物理删除cancelled状态的关注记录

**基本信息**:
- **接口地址**: `DELETE /api/v1/follow/clean`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| days | Integer | Query | ❌ | 30 | 保留天数 |

**示例**: `/api/v1/follow/clean?days=30`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "清理完成",
  "data": 15
}
```

**错误响应**:
- `CLEAN_FOLLOWS_ERROR`: 清理已取消的关注记录失败

---

#### 6.2 根据昵称搜索关注关系

**接口描述**: 根据关注者或被关注者昵称进行模糊搜索

**基本信息**:
- **接口地址**: `GET /api/v1/follow/search/nickname`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| followerId | Long | Query | ❌ | - | 关注者ID |
| followeeId | Long | Query | ❌ | - | 被关注者ID |
| nicknameKeyword | String | Query | ✅ | - | 昵称关键词 |
| currentPage | Integer | Query | ❌ | 1 | 页码 |
| pageSize | Integer | Query | ❌ | 20 | 每页大小 |

**示例**: `/api/v1/follow/search/nickname?nicknameKeyword=张三&currentPage=1&pageSize=20`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "搜索完成",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 1,
    "total": 5,
    "datas": [
      {
        "id": 1,
        "followerId": 1001,
        "followeeId": 1002,
        "followerNickname": "张三",
        "followeeNickname": "李四",
        "status": "active",
        "followTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

**错误响应**:
- `SEARCH_NICKNAME_ERROR`: 昵称搜索失败

---

#### 6.3 更新用户信息冗余字段

**接口描述**: 当用户信息变更时，同步更新关注表中的冗余信息

**基本信息**:
- **接口地址**: `PUT /api/v1/follow/user/info`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 用户ID |
| nickname | String | Query | ❌ | 新昵称 |
| avatar | String | Query | ❌ | 新头像 |

**示例**: `/api/v1/follow/user/info?userId=1001&nickname=新昵称&avatar=新头像URL`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "更新完成",
  "data": 25
}
```

**错误响应**:
- `UPDATE_USER_INFO_ERROR`: 更新用户信息失败

---

#### 6.4 查询用户间关注关系链

**接口描述**: 检查两个用户之间的双向关注关系

**基本信息**:
- **接口地址**: `GET /api/v1/follow/relation-chain`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userIdA | Long | Query | ✅ | 用户A ID |
| userIdB | Long | Query | ✅ | 用户B ID |

**示例**: `/api/v1/follow/relation-chain?userIdA=1001&userIdB=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "查询完成",
  "data": [
    {
      "id": 1,
      "followerId": 1001,
      "followeeId": 1002,
      "followerNickname": "用户A",
      "followeeNickname": "用户B",
      "status": "active",
      "followTime": "2024-01-01T10:00:00"
    },
    {
      "id": 2,
      "followerId": 1002,
      "followeeId": 1001,
      "followerNickname": "用户B",
      "followeeNickname": "用户A",
      "status": "active",
      "followTime": "2024-01-01T11:00:00"
    }
  ]
}
```

**错误响应**:
- `GET_RELATION_CHAIN_ERROR`: 查询关系链失败

---

#### 6.5 验证关注请求参数

**接口描述**: 校验关注请求参数的有效性

**基本信息**:
- **接口地址**: `POST /api/v1/follow/validate`

**请求参数**:
```json
{
  "followerId": 1001,
  "followeeId": 1002
}
```

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "验证成功",
  "data": "参数有效"
}
```

**错误响应**:
- `VALIDATE_REQUEST_ERROR`: 验证请求失败

---

#### 6.6 检查是否可以关注

**接口描述**: 检查业务规则是否允许关注

**基本信息**:
- **接口地址**: `GET /api/v1/follow/check/can-follow`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/check/can-follow?followerId=1001&followeeId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检查完成",
  "data": "可以关注"
}
```

**错误响应**:
- `CHECK_CAN_FOLLOW_ERROR`: 检查权限失败

---

#### 6.7 检查关注关系是否存在

**接口描述**: 检查是否已经存在关注关系，包括已取消的关注关系

**基本信息**:
- **接口地址**: `GET /api/v1/follow/exists`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/exists?followerId=1001&followeeId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检查完成",
  "data": true
}
```

**错误响应**:
- `CHECK_RELATION_EXISTS_ERROR`: 检查关系失败

---

#### 6.8 重新激活已取消的关注关系

**接口描述**: 将cancelled状态的关注重新设置为active

**基本信息**:
- **接口地址**: `POST /api/v1/follow/reactivate`
- **缓存优化**: ✅

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| followerId | Long | Query | ✅ | 关注者ID |
| followeeId | Long | Query | ✅ | 被关注者ID |

**示例**: `/api/v1/follow/reactivate?followerId=1001&followeeId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "重新激活成功",
  "data": true
}
```

**错误响应**:
- `REACTIVATE_FOLLOW_ERROR`: 重新激活失败

---

### 7. 特殊检测接口 🔥

#### 7.1 检测是否被特定用户关注

**接口描述**: 检测用户是否被特定用户关注

**基本信息**:
- **接口地址**: `GET /api/v1/follow/detect/is-followed-by`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 用户ID（被关注者） |
| checkUserId | Long | Query | ✅ | 检测用户ID（潜在关注者） |

**示例**: `/api/v1/follow/detect/is-followed-by?userId=1001&checkUserId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检测完成",
  "data": true
}
```

**错误响应**:
- `DETECT_FOLLOWED_ERROR`: 检测是否被关注失败

---

#### 7.2 检测是否关注了特定用户

**接口描述**: 检测用户是否关注了特定用户

**基本信息**:
- **接口地址**: `GET /api/v1/follow/detect/is-following`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 用户ID（关注者） |
| targetUserId | Long | Query | ✅ | 目标用户ID（被关注者） |

**示例**: `/api/v1/follow/detect/is-following?userId=1001&targetUserId=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检测完成",
  "data": true
}
```

**错误响应**:
- `DETECT_FOLLOWING_ERROR`: 检测是否关注失败

---

#### 7.3 检测双向关注关系

**接口描述**: 检测两个用户之间的完整关注关系

**基本信息**:
- **接口地址**: `GET /api/v1/follow/detect/relationship`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId1 | Long | Query | ✅ | 用户1 ID |
| userId2 | Long | Query | ✅ | 用户2 ID |

**示例**: `/api/v1/follow/detect/relationship?userId1=1001&userId2=1002`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检测完成",
  "data": {
    "user1FollowsUser2": true,
    "user2FollowsUser1": false,
    "isMutualFollow": false
  }
}
```

**错误响应**:
- `DETECT_RELATIONSHIP_ERROR`: 检测双向关注关系失败

---

#### 7.4 批量检测关注状态

**接口描述**: 批量检测用户对多个目标用户的关注状态

**基本信息**:
- **接口地址**: `POST /api/v1/follow/detect/batch-status`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 说明 |
|--------|------|------|------|------|
| userId | Long | Query | ✅ | 当前用户ID |
| targetUserIds | List&lt;Long&gt; | Body | ✅ | 目标用户ID列表 |

**请求示例**:
```
POST /api/v1/follow/detect/batch-status?userId=1001
Content-Type: application/json

[1002, 1003, 1004, 1005]
```

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "批量检测完成",
  "data": {
    "statusMap": {
      "1002": true,
      "1003": false,
      "1004": true,
      "1005": false
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

**错误响应**:
- `BATCH_DETECT_ERROR`: 批量检测关注状态失败

---

#### 7.5 检测用户关注活跃度

**接口描述**: 检测用户在指定时间内的关注活跃度

**基本信息**:
- **接口地址**: `GET /api/v1/follow/detect/activity`

**请求参数**:
| 参数名 | 类型 | 位置 | 必填 | 默认值 | 说明 |
|--------|------|------|------|--------|------|
| userId | Long | Query | ✅ | - | 用户ID |
| days | Integer | Query | ❌ | 7 | 统计天数 |

**示例**: `/api/v1/follow/detect/activity?userId=1001&days=7`

**成功响应**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "检测完成",
  "data": {
    "userId": 1001,
    "statisticsDays": 7,
    "baseStatistics": {
      "followingCount": 120,
      "followersCount": 85
    },
    "activityLevel": "MEDIUM",
    "recommendations": [
      "可以关注更多用户来丰富动态",
      "提升内容质量来增加粉丝数量"
    ]
  }
}
```

**错误响应**:
- `DETECT_ACTIVITY_ERROR`: 检测关注活跃度失败

---

## 错误码说明

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| SUCCESS | 操作成功 | 200 |
| FOLLOW_ERROR | 关注操作失败 | 400 |
| UNFOLLOW_ERROR | 取消关注失败 | 400 |
| CHECK_FOLLOW_ERROR | 检查关注关系失败 | 400 |
| GET_FOLLOW_ERROR | 获取关注详情失败 | 400 |
| QUERY_FOLLOWS_ERROR | 分页查询关注记录失败 | 400 |
| GET_FOLLOWERS_ERROR | 获取关注者列表失败 | 400 |
| GET_FOLLOWING_ERROR | 获取关注列表失败 | 400 |
| GET_STATISTICS_ERROR | 获取关注统计失败 | 400 |
| GET_FOLLOWING_COUNT_ERROR | 获取关注数量失败 | 400 |
| GET_FOLLOWERS_COUNT_ERROR | 获取粉丝数量失败 | 400 |
| BATCH_CHECK_FOLLOW_ERROR | 批量检查关注状态失败 | 400 |
| GET_MUTUAL_ERROR | 获取互相关注列表失败 | 400 |
| CLEAN_FOLLOWS_ERROR | 清理已取消的关注记录失败 | 400 |
| SEARCH_NICKNAME_ERROR | 昵称搜索失败 | 400 |
| UPDATE_USER_INFO_ERROR | 更新用户信息失败 | 400 |
| GET_RELATION_CHAIN_ERROR | 查询关系链失败 | 400 |
| VALIDATE_REQUEST_ERROR | 验证请求失败 | 400 |
| CHECK_CAN_FOLLOW_ERROR | 检查权限失败 | 400 |
| CHECK_RELATION_EXISTS_ERROR | 检查关系失败 | 400 |
| REACTIVATE_FOLLOW_ERROR | 重新激活失败 | 400 |
| DETECT_FOLLOWED_ERROR | 检测是否被关注失败 | 400 |
| DETECT_FOLLOWING_ERROR | 检测是否关注失败 | 400 |
| DETECT_RELATIONSHIP_ERROR | 检测双向关注关系失败 | 400 |
| BATCH_DETECT_ERROR | 批量检测关注状态失败 | 400 |
| DETECT_ACTIVITY_ERROR | 检测关注活跃度失败 | 400 |

## 业务规则

### 关注状态说明
- `active`: 正在关注
- `cancelled`: 已取消关注

### 限制规则
1. 用户不能关注自己
2. 不能重复关注同一个用户（除非先取消关注）
3. 批量操作最多支持100个ID

### 缓存策略
- 关注关系检查：缓存时间30分钟
- 统计信息：缓存时间10分钟
- 列表查询：缓存时间5分钟

## 性能说明

### 优化特性
- 💡 标识的接口使用了缓存优化
- 支持MySQL 8.0/8.4的复合索引和全文检索
- 冗余字段设计避免多表联查

### 建议
- 高频查询接口使用缓存
- 批量操作时控制单次请求数量
- 定期清理已取消的关注记录

---

**总结**: Follow REST API提供了25个接口，覆盖关注管理的所有核心功能，包括基础操作、列表查询、统计分析和特殊检测等场景，具有完整的缓存优化和性能保障。