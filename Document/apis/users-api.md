# Collide 用户服务 API 文档

## 概述

Collide 用户服务提供完整的用户管理功能，包括用户核心信息管理、个人资料管理、统计数据查询、钱包系统和管理员功能。采用分层架构设计，支持高并发和缓存优化。

**服务版本**: v2.0.0 (重构版)  
**基础路径**: `/api/v1/users`  
**管理路径**: `/api/admin/users`  
**模块名称**: `collide-users`  
**架构设计**: Controller-Service-Dubbo分层架构，支持微服务调用  
**设计理念**: 统一用户管理，支持多维度查询，提供完整的用户生命周期管理

## 📝 重构说明 (v2.0.0)

### 主要变更
- **三层分离**: UserController(基础功能) + UserAdminController(管理功能) + UserWalletController(钱包功能)
- **权限控制**: 完善的Sa-Token权限验证，用户只能操作自己的数据
- **参数验证**: 全面的Bean Validation验证，自动化参数校验
- **错误处理**: 统一的异常处理和用户友好的错误信息
- **API标准化**: 统一的Result返回格式和错误码体系

### 功能模块分布
| 控制器 | 路径前缀 | 主要功能 | 权限要求 |
|--------|----------|----------|----------|
| UserController | `/api/v1/users` | 用户基础信息、资料、统计 | 用户权限 |
| UserAdminController | `/api/admin/users` | 用户管理、拉黑、角色管理 | 管理员权限 |
| UserWalletController | `/api/v1/users` | 钱包管理、交易操作 | 用户权限 |

---

## 📋 API 接口清单

### 用户基础功能 (UserController)
| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|-----|---------|----------|
| `POST /` | POST | 创建用户 | 无 |
| `PUT /{userId}` | PUT | 更新用户信息 | 需要登录 |
| `GET /{userId}` | GET | 查询用户信息 | 无 |
| `GET /username/{username}` | GET | 根据用户名查询 | 无 |
| `POST /query` | POST | 分页查询用户 | 无 |
| `POST /batch` | POST | 批量查询用户 | 无 |
| `GET /check/username/{username}` | GET | 检查用户名 | 无 |
| `GET /check/email/{email}` | GET | 检查邮箱 | 无 |
| `PUT /{userId}/password` | PUT | 修改密码 | 需要登录 |
| `POST /{userId}/profile` | POST | 创建用户资料 | 需要登录 |
| `PUT /{userId}/profile` | PUT | 更新用户资料 | 需要登录 |
| `GET /{userId}/profile` | GET | 获取用户资料 | 无 |
| `PUT /{userId}/profile/avatar` | PUT | 更新头像 | 需要登录 |
| `PUT /{userId}/profile/nickname` | PUT | 更新昵称 | 需要登录 |
| `GET /profiles/search` | GET | 搜索用户资料 | 无 |
| `GET /{userId}/stats` | GET | 获取统计数据 | 无 |
| `GET /ranking/followers` | GET | 粉丝排行榜 | 无 |
| `GET /ranking/content` | GET | 内容排行榜 | 无 |
| `GET /platform/stats` | GET | 平台统计 | 无 |
| `GET /me` | GET | 获取当前用户信息 | 需要登录 |
| `PUT /me/profile` | PUT | 更新个人资料 | 需要登录 |

### 管理员功能 (UserAdminController)
| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|-----|---------|----------|
| `PUT /{userId}/status` | PUT | 更新用户状态 | 管理员 |
| `PUT /{userId}/password/reset` | PUT | 重置密码 | 管理员 |
| `DELETE /{userId}` | DELETE | 删除用户 | 管理员 |
| `POST /{userId}/block` | POST | 拉黑用户 | 管理员 |
| `DELETE /{userId}/block/{blockedUserId}` | DELETE | 取消拉黑 | 管理员 |
| `POST /blocks/query` | POST | 查询拉黑记录 | 管理员 |
| `GET /{userId}/blocks` | GET | 获取拉黑列表 | 管理员 |
| `GET /{userId}/blocked` | GET | 获取被拉黑列表 | 管理员 |
| `POST /{userId}/roles` | POST | 分配用户角色 | 管理员 |
| `DELETE /{userId}/roles/{role}` | DELETE | 撤销用户角色 | 管理员 |
| `PUT /{userId}/roles/{roleId}` | PUT | 更新用户角色 | 管理员 |
| `GET /{userId}/roles` | GET | 获取用户角色 | 管理员 |
| `POST /roles/query` | POST | 查询角色记录 | 管理员 |
| `POST /roles/batch-assign` | POST | 批量分配角色 | 管理员 |
| `POST /roles/batch-revoke` | POST | 批量撤销角色 | 管理员 |
| `DELETE /blocks/cleanup` | DELETE | 清理拉黑记录 | 管理员 |
| `GET /roles/statistics` | GET | 角色统计 | 管理员 |

### 钱包功能 (UserWalletController)
| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|-----|---------|----------|
| `POST /{userId}/wallet` | POST | 创建钱包 | 需要登录 |
| `GET /{userId}/wallet` | GET | 获取钱包信息 | 需要登录 |
| `POST /wallets/batch` | POST | 批量查询钱包 | 管理员 |
| `POST /{userId}/wallet/cash/deposit` | POST | 充值现金 | 需要登录 |
| `POST /{userId}/wallet/cash/consume` | POST | 现金消费 | 需要登录 |
| `POST /{userId}/wallet/cash/freeze` | POST | 冻结现金 | 管理员 |
| `POST /{userId}/wallet/cash/unfreeze` | POST | 解冻现金 | 管理员 |
| `GET /{userId}/wallet/cash/balance` | GET | 检查现金余额 | 需要登录 |
| `POST /{userId}/wallet/coin/grant` | POST | 发放金币奖励 | 需要登录 |
| `POST /{userId}/wallet/coin/consume` | POST | 金币消费 | 需要登录 |
| `GET /{userId}/wallet/coin/balance` | GET | 检查金币余额 | 需要登录 |
| `PUT /{userId}/wallet/status` | PUT | 更新钱包状态 | 管理员 |
| `POST /{userId}/wallet/freeze` | POST | 冻结钱包 | 管理员 |
| `POST /{userId}/wallet/unfreeze` | POST | 解冻钱包 | 管理员 |
| `GET /me/wallet` | GET | 获取我的钱包 | 需要登录 |
| `POST /me/wallet/cash/deposit` | POST | 我的现金充值 | 需要登录 |
| `POST /me/wallet/coin/grant` | POST | 我的金币奖励 | 需要登录 |

---

## 用户基础功能 API

### 1. 创建用户
**接口路径**: `POST /api/v1/users`  
**接口描述**: 创建新用户账户

#### 请求参数
```json
{
  "username": "newuser",           // 必填，用户名
  "password": "password123",       // 必填，密码
  "email": "user@example.com",     // 可选，邮箱
  "phone": "13800138000",          // 可选，手机号
  "inviteCode": "INVITE123"        // 可选，邀请码
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 12345,
    "username": "newuser",
    "email": "user@example.com",
    "phone": "13800138000",
    "status": 1,
    "inviteCode": "USER123456",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

**失败响应 (500)**:
```json
{
  "success": false,
  "code": "CREATE_USER_ERROR",
  "message": "创建用户失败: 用户名已存在",
  "data": null
}
```

---

### 2. 更新用户信息
**接口路径**: `PUT /api/v1/users/{userId}`  
**接口描述**: 更新用户核心信息  
**权限要求**: 需要登录，只能修改自己的信息或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "email": "newemail@example.com", // 可选，新邮箱
  "phone": "13900139000",          // 可选，新手机号
  "status": 1                      // 可选，用户状态
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 12345,
    "username": "newuser",
    "email": "newemail@example.com",
    "phone": "13900139000",
    "status": 1,
    "updateTime": "2024-01-16T11:00:00"
  }
}
```

---

### 3. 查询用户信息
**接口路径**: `GET /api/v1/users/{userId}`  
**接口描述**: 根据用户ID查询用户核心信息

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 12345,
    "username": "testuser",
    "email": "test@example.com",
    "phone": "13800138000",
    "status": 1,
    "inviteCode": "USER123456",
    "inviterId": 67890,
    "createTime": "2024-01-01T00:00:00"
  }
}
```

---

### 4. 根据用户名查询
**接口路径**: `GET /api/v1/users/username/{username}`  
**接口描述**: 根据用户名查询用户信息

#### 请求参数
- **Path参数**: `username` (String, 必填) - 用户名

#### 响应示例
与查询用户信息相同的响应格式

---

### 5. 分页查询用户
**接口路径**: `POST /api/v1/users/query`  
**接口描述**: 根据条件分页查询用户列表

#### 请求参数
```json
{
  "currentPage": 1,               // 必填，当前页码（从1开始）
  "pageSize": 20,                 // 必填，每页大小
  "userId": 12345,                // 可选，用户ID查询
  "username": "testuser",         // 可选，用户名查询
  "email": "test@example.com",    // 可选，邮箱查询
  "phone": "13800138000",         // 可选，手机号查询
  "status": 1,                    // 可选，用户状态查询
  "inviteCode": "USER123456"      // 可选，邀请码查询
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 150,
    "totalPages": 8,
    "records": [
      {
        "id": 12345,
        "username": "testuser",
        "email": "test@example.com",
        "status": 1,
        "createTime": "2024-01-01T00:00:00"
      }
    ]
  }
}
```

---

### 6. 批量查询用户
**接口路径**: `POST /api/v1/users/batch`  
**接口描述**: 根据用户ID列表批量查询用户信息

#### 请求参数
```json
[12345, 12346, 12347]
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 12345,
      "username": "user1",
      "email": "user1@example.com",
      "status": 1
    },
    {
      "id": 12346,
      "username": "user2",
      "email": "user2@example.com", 
      "status": 1
    }
  ]
}
```

---

### 7. 检查用户名是否存在
**接口路径**: `GET /api/v1/users/check/username/{username}`  
**接口描述**: 检查用户名是否已存在

#### 请求参数
- **Path参数**: `username` (String, 必填) - 要检查的用户名

#### 响应示例
**用户名不存在 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": false
}
```

**用户名已存在 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": true
}
```

---

### 8. 检查邮箱是否存在
**接口路径**: `GET /api/v1/users/check/email/{email}`  
**接口描述**: 检查邮箱是否已存在

#### 请求参数
- **Path参数**: `email` (String, 必填) - 要检查的邮箱

#### 响应示例
与检查用户名相同的响应格式

---

### 9. 修改密码
**接口路径**: `PUT /api/v1/users/{userId}/password`  
**接口描述**: 用户修改自己的密码  
**权限要求**: 需要登录，只能修改自己的密码

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**:
  - `oldPassword` (String, 必填) - 原密码
  - `newPassword` (String, 必填) - 新密码

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

**权限错误 (403)**:
```json
{
  "success": false,
  "code": "PERMISSION_DENIED",
  "message": "只能修改自己的密码",
  "data": null
}
```

---

## 用户资料管理 API

### 10. 创建用户资料
**接口路径**: `POST /api/v1/users/{userId}/profile`  
**接口描述**: 为用户创建个人资料  
**权限要求**: 需要登录，只能创建自己的资料或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "nickname": "用户昵称",          // 可选，昵称
  "avatar": "https://avatar.example.com/user.jpg", // 可选，头像URL
  "bio": "个人简介",              // 可选，个人简介
  "birthday": "1990-01-01",       // 可选，生日
  "gender": 1,                    // 可选，性别（0-未知，1-男，2-女）
  "location": "北京",             // 可选，所在地
  "website": "https://website.com" // 可选，个人网站
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "nickname": "用户昵称",
    "avatar": "https://avatar.example.com/user.jpg",
    "bio": "个人简介",
    "birthday": "1990-01-01",
    "gender": 1,
    "location": "北京",
    "website": "https://website.com",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 11. 更新用户资料
**接口路径**: `PUT /api/v1/users/{userId}/profile`  
**接口描述**: 更新用户个人资料  
**权限要求**: 需要登录，只能修改自己的资料或管理员操作

#### 请求参数
与创建用户资料相同的Body参数结构

#### 响应示例
与创建用户资料相同的响应格式，但会更新`updateTime`字段

---

### 12. 获取用户资料
**接口路径**: `GET /api/v1/users/{userId}/profile`  
**接口描述**: 根据用户ID获取个人资料

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID

#### 响应示例
与创建用户资料相同的响应格式

---

### 13. 更新头像
**接口路径**: `PUT /api/v1/users/{userId}/profile/avatar`  
**接口描述**: 更新用户头像  
**权限要求**: 需要登录，只能修改自己的头像或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `avatarUrl` (String, 必填) - 新头像URL

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

### 14. 更新昵称
**接口路径**: `PUT /api/v1/users/{userId}/profile/nickname`  
**接口描述**: 更新用户昵称  
**权限要求**: 需要登录，只能修改自己的昵称或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `nickname` (String, 必填) - 新昵称

#### 响应示例
与更新头像相同的响应格式

---

### 15. 搜索用户资料
**接口路径**: `GET /api/v1/users/profiles/search`  
**接口描述**: 根据昵称关键词搜索用户资料

#### 请求参数
- **Query参数**:
  - `keyword` (String, 必填) - 搜索关键词
  - `currentPage` (Integer, 可选, 默认1) - 当前页码
  - `pageSize` (Integer, 可选, 默认20) - 每页大小

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 50,
    "totalPages": 3,
    "records": [
      {
        "id": 1001,
        "userId": 12345,
        "nickname": "匹配的昵称",
        "avatar": "https://avatar.example.com/user.jpg",
        "bio": "个人简介",
        "location": "北京"
      }
    ]
  }
}
```

---

## 用户统计数据 API

### 16. 获取用户统计数据
**接口路径**: `GET /api/v1/users/{userId}/stats`  
**接口描述**: 获取用户统计数据

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 2001,
    "userId": 12345,
    "followerCount": 1250,
    "followingCount": 380,
    "contentCount": 45,
    "likeCount": 8900,
    "viewCount": 125000,
    "loginCount": 156,
    "lastLoginTime": "2024-01-15T10:30:00",
    "activityScore": "85.50",
    "influenceScore": "92.30",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 17. 获取粉丝排行榜
**接口路径**: `GET /api/v1/users/ranking/followers`  
**接口描述**: 获取粉丝数排行榜

#### 请求参数
- **Query参数**: `limit` (Integer, 可选, 默认10) - 排行榜数量限制

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "userId": 12345,
      "followerCount": 10000,
      "ranking": 1
    },
    {
      "userId": 12346,
      "followerCount": 8500,
      "ranking": 2
    }
  ]
}
```

---

### 18. 获取内容排行榜
**接口路径**: `GET /api/v1/users/ranking/content`  
**接口描述**: 获取内容数排行榜

#### 请求参数
与粉丝排行榜相同

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "userId": 12345,
      "contentCount": 500,
      "ranking": 1
    },
    {
      "userId": 12346,
      "contentCount": 420,
      "ranking": 2
    }
  ]
}
```

---

### 19. 获取平台统计数据
**接口路径**: `GET /api/v1/users/platform/stats`  
**接口描述**: 获取平台总体统计数据

#### 请求参数
无

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "totalUsers": 100000,
    "activeUsers": 85000,
    "totalContent": 500000,
    "totalLikes": 2000000,
    "avgActivityScore": "75.5",
    "avgInfluenceScore": "68.2",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

## 当前用户 API

### 20. 获取当前用户信息
**接口路径**: `GET /api/v1/users/me`  
**接口描述**: 获取当前登录用户的完整信息  
**权限要求**: 需要登录

#### 请求参数
无（通过Token获取当前用户信息）

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "currentuser",
      "email": "current@example.com",
      "phone": "13800138000",
      "status": 1
    },
    "profile": {
      "nickname": "我的昵称",
      "avatar": "https://avatar.example.com/me.jpg",
      "bio": "我的个人简介",
      "location": "北京"
    },
    "stats": {
      "followerCount": 1250,
      "followingCount": 380,
      "contentCount": 45,
      "likeCount": 8900
    }
  }
}
```

---

### 21. 更新个人资料
**接口路径**: `PUT /api/v1/users/me/profile`  
**接口描述**: 更新当前用户的个人资料  
**权限要求**: 需要登录

#### 请求参数
与更新用户资料相同的Body参数结构

#### 响应示例
与更新用户资料相同的响应格式

---

## 管理员功能 API

### 1. 更新用户状态
**接口路径**: `PUT /api/admin/users/{userId}/status`  
**接口描述**: 管理员更新用户状态（正常/暂停/封禁等）  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**:
  - `status` (Integer, 必填) - 用户状态（1-正常，2-未激活，3-暂停，4-封禁）
  - `reason` (String, 可选) - 操作原因

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

### 2. 重置用户密码
**接口路径**: `PUT /api/admin/users/{userId}/password/reset`  
**接口描述**: 管理员重置用户密码  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `newPassword` (String, 必填) - 新密码

#### 响应示例
与更新用户状态相同的响应格式

---

### 3. 删除用户
**接口路径**: `DELETE /api/admin/users/{userId}`  
**接口描述**: 管理员删除用户（物理删除，谨慎使用）  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID

#### 响应示例
与更新用户状态相同的响应格式

---

### 4. 拉黑用户
**接口路径**: `POST /api/admin/users/{userId}/block`  
**接口描述**: 管理员拉黑用户  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 被拉黑用户ID
- **Body参数**:
```json
{
  "reason": "违规行为",           // 必填，拉黑原因
  "expireTime": "2024-12-31T23:59:59", // 可选，过期时间
  "description": "详细描述"       // 可选，详细描述
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 3001,
    "userId": 1,
    "blockedUserId": 12345,
    "reason": "违规行为",
    "status": "active",
    "createTime": "2024-01-16T10:30:00"
  }
}
```

---

### 5. 取消拉黑
**接口路径**: `DELETE /api/admin/users/{userId}/block/{blockedUserId}`  
**接口描述**: 管理员取消用户拉黑  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**:
  - `userId` (Long, 必填) - 操作者用户ID
  - `blockedUserId` (Long, 必填) - 被拉黑用户ID

#### 响应示例
与更新用户状态相同的响应格式

---

### 6. 查询拉黑记录
**接口路径**: `POST /api/admin/users/blocks/query`  
**接口描述**: 管理员查询用户拉黑记录  
**权限要求**: 管理员权限

#### 请求参数
```json
{
  "currentPage": 1,               // 必填，当前页码
  "pageSize": 20,                 // 必填，每页大小
  "userId": 12345,                // 可选，拉黑者用户ID
  "blockedUserId": 12346,         // 可选，被拉黑用户ID
  "status": "active",             // 可选，状态筛选
  "reason": "违规"                // 可选，原因关键词
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalCount": 100,
    "totalPages": 5,
    "records": [
      {
        "id": 3001,
        "userId": 1,
        "blockedUserId": 12345,
        "reason": "违规行为",
        "status": "active",
        "createTime": "2024-01-16T10:30:00"
      }
    ]
  }
}
```

---

### 7. 分配用户角色
**接口路径**: `POST /api/admin/users/{userId}/roles`  
**接口描述**: 管理员为用户分配角色  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "role": "blogger",              // 必填，角色名称
  "expireTime": "2024-12-31T23:59:59", // 可选，过期时间
  "description": "博主认证"       // 可选，描述
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 4001,
    "userId": 12345,
    "role": "blogger",
    "status": "active",
    "expireTime": "2024-12-31T23:59:59",
    "assignTime": "2024-01-16T10:30:00",
    "assignBy": 1
  }
}
```

---

### 8. 撤销用户角色
**接口路径**: `DELETE /api/admin/users/{userId}/roles/{role}`  
**接口描述**: 管理员撤销用户角色  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**:
  - `userId` (Long, 必填) - 用户ID
  - `role` (String, 必填) - 角色名称
- **Query参数**: `reason` (String, 可选) - 撤销原因

#### 响应示例
与更新用户状态相同的响应格式

---

### 9. 批量分配角色
**接口路径**: `POST /api/admin/users/roles/batch-assign`  
**接口描述**: 管理员批量为用户分配角色  
**权限要求**: 管理员权限

#### 请求参数
- **Query参数**:
  - `userIds` (List<Long>, 必填) - 用户ID列表
  - `role` (String, 必填) - 角色名称
  - `expireTime` (String, 可选) - 过期时间，格式：yyyy-MM-dd HH:mm:ss

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 5
}
```

---

### 10. 批量撤销角色
**接口路径**: `POST /api/admin/users/roles/batch-revoke`  
**接口描述**: 管理员批量撤销用户角色  
**权限要求**: 管理员权限

#### 请求参数
- **Query参数**:
  - `userIds` (List<Long>, 必填) - 用户ID列表
  - `role` (String, 必填) - 角色名称

#### 响应示例
与批量分配角色相同的响应格式

---

## 钱包功能 API

### 1. 创建用户钱包
**接口路径**: `POST /api/v1/users/{userId}/wallet`  
**接口描述**: 为用户创建钱包账户  
**权限要求**: 需要登录，只能为自己创建钱包或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "initialCashBalance": "0.00",   // 可选，初始现金余额
  "initialCoinBalance": 0,        // 可选，初始金币余额
  "description": "钱包创建"       // 可选，描述
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 5001,
    "userId": 12345,
    "cashBalance": "0.00",
    "cashFrozen": "0.00",
    "coinBalance": 0,
    "coinFrozen": 0,
    "status": "active",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 获取钱包信息
**接口路径**: `GET /api/v1/users/{userId}/wallet`  
**接口描述**: 获取用户钱包详细信息  
**权限要求**: 需要登录，只能查看自己的钱包或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID

#### 响应示例
与创建用户钱包相同的响应格式

---

### 3. 充值现金
**接口路径**: `POST /api/v1/users/{userId}/wallet/cash/deposit`  
**接口描述**: 用户现金充值  
**权限要求**: 需要登录，只能为自己充值或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "cashAmount": "100.00",         // 必填，充值金额
  "businessId": "ORDER_12345",    // 必填，业务ID
  "description": "在线充值"       // 必填，描述
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 5001,
    "userId": 12345,
    "cashBalance": "100.00",
    "cashFrozen": "0.00",
    "coinBalance": 0,
    "coinFrozen": 0,
    "status": "active",
    "updateTime": "2024-01-16T10:45:00"
  }
}
```

---

### 4. 现金消费
**接口路径**: `POST /api/v1/users/{userId}/wallet/cash/consume`  
**接口描述**: 用户现金消费  
**权限要求**: 需要登录，只能消费自己的现金或管理员操作

#### 请求参数
与充值现金相同的Body参数结构

#### 响应示例
与充值现金相同的响应格式，但余额会相应减少

---

### 5. 发放金币奖励
**接口路径**: `POST /api/v1/users/{userId}/wallet/coin/grant`  
**接口描述**: 为用户发放金币奖励  
**权限要求**: 需要登录，只能为自己发放或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "coinAmount": 100,              // 必填，金币数量
  "businessId": "TASK_12345",     // 必填，业务ID
  "description": "任务奖励"       // 必填，描述
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 5001,
    "userId": 12345,
    "cashBalance": "100.00",
    "cashFrozen": "0.00",
    "coinBalance": 100,
    "coinFrozen": 0,
    "status": "active",
    "updateTime": "2024-01-16T11:00:00"
  }
}
```

---

### 6. 金币消费
**接口路径**: `POST /api/v1/users/{userId}/wallet/coin/consume`  
**接口描述**: 用户金币消费  
**权限要求**: 需要登录，只能消费自己的金币或管理员操作

#### 请求参数
与发放金币奖励相同的Body参数结构

#### 响应示例
与发放金币奖励相同的响应格式，但金币余额会相应减少

---

### 7. 检查现金余额
**接口路径**: `GET /api/v1/users/{userId}/wallet/cash/balance`  
**接口描述**: 检查用户现金余额是否充足  
**权限要求**: 需要登录，只能查看自己的余额或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `amount` (BigDecimal, 必填) - 检查金额

#### 响应示例
**余额充足 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": true
}
```

**余额不足 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": false
}
```

---

### 8. 检查金币余额
**接口路径**: `GET /api/v1/users/{userId}/wallet/coin/balance`  
**接口描述**: 检查用户金币余额是否充足  
**权限要求**: 需要登录，只能查看自己的余额或管理员操作

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `amount` (Long, 必填) - 检查金币数量

#### 响应示例
与检查现金余额相同的响应格式

---

### 9. 更新钱包状态（管理员）
**接口路径**: `PUT /api/v1/users/{userId}/wallet/status`  
**接口描述**: 更新用户钱包状态（管理员功能）  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**:
  - `status` (String, 必填) - 钱包状态（active-正常，frozen-冻结）
  - `reason` (String, 可选) - 操作原因

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

### 10. 冻结钱包（管理员）
**接口路径**: `POST /api/v1/users/{userId}/wallet/freeze`  
**接口描述**: 冻结用户钱包（管理员功能）  
**权限要求**: 管理员权限

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Query参数**: `reason` (String, 必填) - 冻结原因

#### 响应示例
与更新钱包状态相同的响应格式

---

### 11. 解冻钱包（管理员）
**接口路径**: `POST /api/v1/users/{userId}/wallet/unfreeze`  
**接口描述**: 解冻用户钱包（管理员功能）  
**权限要求**: 管理员权限

#### 请求参数
与冻结钱包相同的请求参数

#### 响应示例
与更新钱包状态相同的响应格式

---

### 12. 获取我的钱包信息
**接口路径**: `GET /api/v1/users/me/wallet`  
**接口描述**: 获取当前用户的钱包信息  
**权限要求**: 需要登录

#### 请求参数
无（通过Token获取当前用户信息）

#### 响应示例
与获取钱包信息相同的响应格式

---

### 13. 我的现金充值
**接口路径**: `POST /api/v1/users/me/wallet/cash/deposit`  
**接口描述**: 当前用户现金充值  
**权限要求**: 需要登录

#### 请求参数
与充值现金相同的Body参数结构（无需userId）

#### 响应示例
与充值现金相同的响应格式

---

### 14. 我的金币奖励发放
**接口路径**: `POST /api/v1/users/me/wallet/coin/grant`  
**接口描述**: 当前用户获得金币奖励  
**权限要求**: 需要登录

#### 请求参数
与发放金币奖励相同的Body参数结构（无需userId）

#### 响应示例
与发放金币奖励相同的响应格式

---

## 错误码说明

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| SUCCESS | 200 | 操作成功 |
| PERMISSION_DENIED | 403 | 权限不足 |
| CREATE_USER_ERROR | 500 | 创建用户失败 |
| UPDATE_USER_ERROR | 500 | 更新用户失败 |
| GET_USER_ERROR | 500 | 查询用户失败 |
| GET_USER_BY_USERNAME_ERROR | 500 | 根据用户名查询用户失败 |
| QUERY_USERS_ERROR | 500 | 分页查询用户失败 |
| BATCH_GET_USERS_ERROR | 500 | 批量查询用户失败 |
| CHECK_USERNAME_ERROR | 500 | 检查用户名失败 |
| CHECK_EMAIL_ERROR | 500 | 检查邮箱失败 |
| CHANGE_PASSWORD_ERROR | 500 | 修改密码失败 |
| CREATE_PROFILE_ERROR | 500 | 创建用户资料失败 |
| UPDATE_PROFILE_ERROR | 500 | 更新用户资料失败 |
| GET_PROFILE_ERROR | 500 | 获取用户资料失败 |
| UPDATE_AVATAR_ERROR | 500 | 更新头像失败 |
| UPDATE_NICKNAME_ERROR | 500 | 更新昵称失败 |
| SEARCH_PROFILES_ERROR | 500 | 搜索用户资料失败 |
| GET_STATS_ERROR | 500 | 获取统计数据失败 |
| GET_FOLLOWER_RANKING_ERROR | 500 | 获取粉丝排行榜失败 |
| GET_CONTENT_RANKING_ERROR | 500 | 获取内容排行榜失败 |
| GET_PLATFORM_STATS_ERROR | 500 | 获取平台统计失败 |
| GET_CURRENT_USER_ERROR | 500 | 获取当前用户信息失败 |
| UPDATE_MY_PROFILE_ERROR | 500 | 更新个人资料失败 |
| UPDATE_USER_STATUS_ERROR | 500 | 更新用户状态失败 |
| RESET_PASSWORD_ERROR | 500 | 重置密码失败 |
| DELETE_USER_ERROR | 500 | 删除用户失败 |
| BLOCK_USER_ERROR | 500 | 拉黑用户失败 |
| UNBLOCK_USER_ERROR | 500 | 取消拉黑失败 |
| QUERY_BLOCKS_ERROR | 500 | 查询拉黑记录失败 |
| GET_USER_BLOCKS_ERROR | 500 | 获取拉黑列表失败 |
| GET_USER_BLOCKED_ERROR | 500 | 获取被拉黑列表失败 |
| ASSIGN_ROLE_ERROR | 500 | 分配角色失败 |
| REVOKE_ROLE_ERROR | 500 | 撤销角色失败 |
| UPDATE_ROLE_ERROR | 500 | 更新角色失败 |
| GET_USER_ROLES_ERROR | 500 | 获取用户角色失败 |
| QUERY_ROLES_ERROR | 500 | 查询角色记录失败 |
| BATCH_ASSIGN_ROLE_ERROR | 500 | 批量分配角色失败 |
| BATCH_REVOKE_ROLE_ERROR | 500 | 批量撤销角色失败 |
| CLEAN_BLOCKS_ERROR | 500 | 清理拉黑记录失败 |
| GET_ROLE_STATISTICS_ERROR | 500 | 获取角色统计失败 |
| CREATE_WALLET_ERROR | 500 | 创建钱包失败 |
| GET_WALLET_ERROR | 500 | 获取钱包信息失败 |
| BATCH_GET_WALLETS_ERROR | 500 | 批量查询钱包失败 |
| DEPOSIT_CASH_ERROR | 500 | 现金充值失败 |
| CONSUME_CASH_ERROR | 500 | 现金消费失败 |
| FREEZE_CASH_ERROR | 500 | 冻结现金失败 |
| UNFREEZE_CASH_ERROR | 500 | 解冻现金失败 |
| CHECK_CASH_BALANCE_ERROR | 500 | 检查现金余额失败 |
| GRANT_COIN_ERROR | 500 | 发放金币失败 |
| CONSUME_COIN_ERROR | 500 | 金币消费失败 |
| CHECK_COIN_BALANCE_ERROR | 500 | 检查金币余额失败 |
| UPDATE_WALLET_STATUS_ERROR | 500 | 更新钱包状态失败 |
| FREEZE_WALLET_ERROR | 500 | 冻结钱包失败 |
| UNFREEZE_WALLET_ERROR | 500 | 解冻钱包失败 |
| GET_MY_WALLET_ERROR | 500 | 获取钱包信息失败 |
| MY_DEPOSIT_CASH_ERROR | 500 | 现金充值失败 |
| MY_GRANT_COIN_ERROR | 500 | 获得金币奖励失败 |

---

## 数据结构说明

### 用户状态枚举
- `1`: 正常状态
- `2`: 未激活状态
- `3`: 暂停状态
- `4`: 封禁状态

### 用户性别枚举
- `0`: 未知
- `1`: 男性
- `2`: 女性

### 钱包状态枚举
- `active`: 正常状态，可以进行所有操作
- `frozen`: 冻结状态，禁止交易操作

### 拉黑状态枚举
- `active`: 生效中
- `cancelled`: 已取消

### 角色状态枚举
- `active`: 生效中
- `revoked`: 已撤销
- `expired`: 已过期

---

## 🚀 使用示例

### 前端集成示例

```javascript
// 1. 创建用户
const createUser = async (userData) => {
  try {
    const response = await fetch('/api/v1/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(userData)
    });
    
    const result = await response.json();
    if (result.success) {
      console.log('用户创建成功:', result.data);
      return { success: true, user: result.data };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 2. 获取当前用户完整信息
const getCurrentUserInfo = async () => {
  const token = localStorage.getItem('token');
  if (!token) return { success: false, message: '请先登录' };
  
  try {
    const response = await fetch('/api/v1/users/me', {
      headers: { 'satoken': token }
    });
    
    const result = await response.json();
    if (result.success) {
      return { success: true, data: result.data };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 3. 更新用户资料
const updateProfile = async (userId, profileData) => {
  const token = localStorage.getItem('token');
  if (!token) return { success: false, message: '请先登录' };
  
  try {
    const response = await fetch(`/api/v1/users/${userId}/profile`, {
      method: 'PUT',
      headers: { 
        'Content-Type': 'application/json',
        'satoken': token 
      },
      body: JSON.stringify(profileData)
    });
    
    const result = await response.json();
    if (result.success) {
      return { success: true, profile: result.data };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 4. 钱包操作
const walletOperations = {
  // 获取钱包信息
  getWallet: async (userId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`/api/v1/users/${userId}/wallet`, {
        headers: { 'satoken': token }
      });
      return await response.json();
    } catch (error) {
      return { success: false, message: '获取钱包信息失败' };
    }
  },
  
  // 现金充值
  depositCash: async (userId, amount, description) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`/api/v1/users/${userId}/wallet/cash/deposit`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'satoken': token 
        },
        body: JSON.stringify({
          cashAmount: amount,
          businessId: `DEPOSIT_${Date.now()}`,
          description: description
        })
      });
      return await response.json();
    } catch (error) {
      return { success: false, message: '充值失败' };
    }
  },
  
  // 检查余额
  checkBalance: async (userId, amount, type = 'cash') => {
    const token = localStorage.getItem('token');
    const endpoint = type === 'cash' ? 'cash' : 'coin';
    try {
      const response = await fetch(`/api/v1/users/${userId}/wallet/${endpoint}/balance?amount=${amount}`, {
        headers: { 'satoken': token }
      });
      return await response.json();
    } catch (error) {
      return { success: false, message: '检查余额失败' };
    }
  }
};

// 5. 管理员操作（需要管理员权限）
const adminOperations = {
  // 更新用户状态
  updateUserStatus: async (userId, status, reason) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`/api/admin/users/${userId}/status?status=${status}&reason=${encodeURIComponent(reason)}`, {
        method: 'PUT',
        headers: { 'satoken': token }
      });
      return await response.json();
    } catch (error) {
      return { success: false, message: '更新用户状态失败' };
    }
  },
  
  // 批量分配角色
  batchAssignRole: async (userIds, role, expireTime) => {
    const token = localStorage.getItem('token');
    try {
      const params = new URLSearchParams({
        userIds: userIds.join(','),
        role: role
      });
      if (expireTime) params.append('expireTime', expireTime);
      
      const response = await fetch(`/api/admin/users/roles/batch-assign`, {
        method: 'POST',
        headers: { 'satoken': token },
        body: params
      });
      return await response.json();
    } catch (error) {
      return { success: false, message: '批量分配角色失败' };
    }
  }
};

// 6. HTTP拦截器示例（axios）
import axios from 'axios';

// 请求拦截器：自动添加Token
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['satoken'] = token;
  }
  return config;
});

// 响应拦截器：处理权限和错误
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 403) {
      // 权限不足
      alert('权限不足，请联系管理员');
    } else if (error.response?.status === 401) {
      // Token失效
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 📋 接口测试

### cURL示例

```bash
# 1. 创建用户
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'

# 2. 查询用户信息
curl -X GET "http://localhost:8080/api/v1/users/12345"

# 3. 获取当前用户信息（需要Token）
curl -X GET "http://localhost:8080/api/v1/users/me" \
  -H "satoken: your_token_here"

# 4. 更新用户资料（需要Token）
curl -X PUT "http://localhost:8080/api/v1/users/12345/profile" \
  -H "Content-Type: application/json" \
  -H "satoken: your_token_here" \
  -d '{
    "nickname": "新昵称",
    "bio": "更新后的个人简介"
  }'

# 5. 钱包充值（需要Token）
curl -X POST "http://localhost:8080/api/v1/users/12345/wallet/cash/deposit" \
  -H "Content-Type: application/json" \
  -H "satoken: your_token_here" \
  -d '{
    "cashAmount": "100.00",
    "businessId": "DEPOSIT_12345",
    "description": "在线充值"
  }'

# 6. 管理员更新用户状态（需要管理员Token）
curl -X PUT "http://localhost:8080/api/admin/users/12345/status?status=3&reason=违规操作" \
  -H "satoken: admin_token_here"

# 7. 搜索用户资料
curl -X GET "http://localhost:8080/api/v1/users/profiles/search?keyword=测试&currentPage=1&pageSize=10"

# 8. 获取钱包信息（需要Token）
curl -X GET "http://localhost:8080/api/v1/users/12345/wallet" \
  -H "satoken: your_token_here"
```

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (重构版)  
**架构说明**: 采用Controller-Service-Dubbo三层架构，分模块管理用户基础功能、管理员功能和钱包功能，权限控制完善，API标准化