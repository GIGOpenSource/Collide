# User 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [数据库设计](#数据库设计)
- [简化认证接口](#简化认证接口)
- [用户管理接口](#用户管理接口)
- [RPC接口列表](#rpc接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

User 模块是 Collide 社交平台的核心模块，负责用户认证、信息管理、邀请码系统等功能。**已简化为纯用户名密码认证，支持邀请码和登录自动注册。**

### 主要功能
- ✨ **简化认证**: 用户名密码注册登录，无复杂验证流程
- 🎫 **邀请码系统**: 支持邀请码注册，建立邀请关系
- 🚀 **自动注册**: 登录时用户不存在可自动注册
- 👤 **用户管理**: 基础信息管理和扩展档案
- 📊 **统计数据**: 去连表设计的统计字段
- 🎖️ **博主认证**: 博主身份申请和管理

### 技术架构
- **框架**: Spring Boot 3.x + Spring Cloud Alibaba
- **数据库**: MySQL 8.0 (统一表设计)
- **ORM**: MyBatis Plus
- **RPC**: Apache Dubbo
- **认证**: Sa-Token
- **缓存**: JetCache + Redis
- **文档**: OpenAPI 3.0

---

## 🗄️ 数据库设计

### 用户统一表 (t_user_unified)

采用**去连表设计**，将用户基础信息和扩展信息合并到单表中，提升查询性能。

| 字段名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| **基础字段** |
| id | BIGINT | 是 | AUTO_INCREMENT | 用户ID，主键 |
| username | VARCHAR(50) | 是 | - | 用户名，唯一索引 |
| nickname | VARCHAR(100) | 是 | - | 用户昵称 |
| avatar | VARCHAR(500) | 否 | - | 头像URL |
| email | VARCHAR(100) | 否 | - | 邮箱地址，唯一索引 |
| phone | VARCHAR(20) | 否 | - | 手机号码，唯一索引 |
| password_hash | VARCHAR(255) | 是 | - | 密码哈希值 |
| salt | VARCHAR(50) | 是 | - | 密码盐值 |
| role | ENUM | 是 | user | 用户角色：user、blogger、admin、vip |
| status | ENUM | 是 | active | 用户状态：inactive、active、suspended、banned |
| **扩展字段** |
| bio | TEXT | 否 | - | 个人简介 |
| birthday | DATE | 否 | - | 生日 |
| gender | ENUM | 否 | unknown | 性别：male、female、unknown |
| location | VARCHAR(100) | 否 | - | 所在地 |
| **统计字段（冗余设计）** |
| follower_count | BIGINT | 否 | 0 | 粉丝数 |
| following_count | BIGINT | 否 | 0 | 关注数 |
| content_count | BIGINT | 否 | 0 | 内容数 |
| like_count | BIGINT | 否 | 0 | 获得点赞数 |
| login_count | BIGINT | 否 | 0 | 登录次数 |
| **VIP相关** |
| vip_expire_time | DATETIME | 否 | - | VIP过期时间 |
| **博主认证** |
| blogger_status | ENUM | 否 | none | 博主状态：none、applying、approved、rejected |
| blogger_apply_time | DATETIME | 否 | - | 博主申请时间 |
| blogger_approve_time | DATETIME | 否 | - | 博主认证时间 |
| **邀请系统** |
| invite_code | VARCHAR(20) | 否 | - | 邀请码，唯一索引 |
| inviter_id | BIGINT | 否 | - | 邀请人ID |
| invited_count | BIGINT | 否 | 0 | 邀请人数 |
| **系统字段** |
| last_login_time | DATETIME | 否 | - | 最后登录时间 |
| create_time | DATETIME | 是 | CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | 是 | CURRENT_TIMESTAMP | 更新时间 |
| deleted | TINYINT | 否 | 0 | 逻辑删除标记 |
| version | INT | 否 | 0 | 版本号（乐观锁） |

### 索引设计
```sql
-- 主键
PRIMARY KEY (`id`)

-- 唯一索引
UNIQUE KEY `uk_username` (`username`)
UNIQUE KEY `uk_email` (`email`)
UNIQUE KEY `uk_phone` (`phone`)
UNIQUE KEY `uk_invite_code` (`invite_code`)

-- 普通索引
KEY `idx_status` (`status`)
KEY `idx_role` (`role`)
KEY `idx_create_time` (`create_time`)
KEY `idx_last_login_time` (`last_login_time`)
KEY `idx_blogger_status` (`blogger_status`)
KEY `idx_inviter_id` (`inviter_id`)
KEY `idx_deleted` (`deleted`)
```

---

## 🔐 简化认证接口

### 1. 用户注册

**接口描述**: 简化的用户名密码注册，支持邀请码

**请求信息**:
- **URL**: `POST /api/v1/auth/register`
- **Content-Type**: `application/json`
- **需要认证**: 否

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123",
  "inviteCode": "ABC12345"
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| username | String | 是 | 用户名，3-20字符，支持字母数字下划线 |
| password | String | 是 | 密码，6-50字符 |
| inviteCode | String | 否 | 邀请码，8位字符 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "用户1703845123456",
      "avatar": null,
      "role": "user",
      "status": "active",
      "inviteCode": "XYZ78901",
      "invitedCount": 0,
      "createTime": "2024-01-15T10:30:00",
      "lastLoginTime": "2024-01-15T10:30:00"
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "message": "注册成功"
  }
}
```

---

### 2. 用户登录

**接口描述**: 标准用户名密码登录

**请求信息**:
- **URL**: `POST /api/v1/auth/login`
- **Content-Type**: `application/json`
- **需要认证**: 否

**请求参数**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "用户1703845123456",
      "avatar": null,
      "role": "user",
      "status": "active",
      "inviteCode": "XYZ78901",
      "invitedCount": 0,
      "createTime": "2024-01-15T10:30:00",
      "lastLoginTime": "2024-01-15T10:30:00"
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "message": "登录成功"
  }
}
```

---

### 3. 登录或注册 ⭐️

**接口描述**: 核心功能！用户不存在时自动注册，一个接口解决登录和注册需求

**请求信息**:
- **URL**: `POST /api/v1/auth/login-or-register`
- **Content-Type**: `application/json`
- **需要认证**: 否

**请求参数**:
```json
{
  "username": "newuser",
  "password": "password123",
  "inviteCode": "ABC12345"
}
```

**响应示例（新用户自动注册）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "user": {
      "id": 67890,
      "username": "newuser",
      "nickname": "用户1703845234567",
      "avatar": null,
      "role": "user",
      "status": "active",
      "inviteCode": "DEF45678",
      "invitedCount": 0,
      "createTime": "2024-01-15T10:35:00",
      "lastLoginTime": "2024-01-15T10:35:00"
    },
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
    "isNewUser": true,
    "message": "注册并登录成功"
  }
}
```

---

### 4. 用户登出

**接口描述**: 退出登录，清除会话

**请求信息**:
- **URL**: `POST /api/v1/auth/logout`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "登出成功"
}
```

---

### 5. 验证邀请码

**接口描述**: 检查邀请码是否有效并返回邀请人信息

**请求信息**:
- **URL**: `GET /api/v1/auth/validate-invite-code`
- **需要认证**: 否

**请求参数**:

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| inviteCode | String | 是 | 邀请码 |

**响应示例（有效邀请码）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": true,
    "inviter": {
      "id": 12345,
      "username": "testuser",
      "nickname": "用户1703845123456",
      "avatar": null
    }
  }
}
```

**响应示例（无效邀请码）**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "valid": false,
    "message": "邀请码无效"
  }
}
```

---

### 6. 获取我的邀请信息

**接口描述**: 获取当前用户的邀请码和邀请统计

**请求信息**:
- **URL**: `GET /api/v1/auth/my-invite-info`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "inviteCode": "XYZ78901",
    "totalInvitedCount": 5,
    "invitedUsers": [
      {
        "id": 67890,
        "username": "newuser1",
        "nickname": "用户1703845234567",
        "createTime": "2024-01-15T09:00:00"
      },
      {
        "id": 67891,
        "username": "newuser2",
        "nickname": "用户1703845234568",
        "createTime": "2024-01-15T10:00:00"
      }
    ]
  }
}
```

---

## 👤 用户管理接口

### 1. 获取当前用户信息

**接口描述**: 获取当前登录用户的详细信息

**请求信息**:
- **URL**: `GET /api/v1/users/me`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "John Doe",
    "avatar": "https://cdn.collide.com/avatars/12345.jpg",
    "email": "john@example.com",
    "phone": "13800138000",
    "role": "user",
    "status": "active",
    "bio": "热爱生活，热爱分享",
    "gender": "male",
    "birthday": "1990-01-01",
    "location": "北京市",
    "followerCount": 156,
    "followingCount": 89,
    "contentCount": 42,
    "likeCount": 1205,
    "loginCount": 128,
    "inviteCode": "XYZ78901",
    "invitedCount": 5,
    "vipExpireTime": null,
    "bloggerStatus": "none",
    "lastLoginTime": "2024-01-15T10:30:00",
    "createTime": "2023-12-01T08:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

---

### 2. 根据用户ID获取用户信息

**接口描述**: 根据用户ID获取指定用户的公开信息

**请求信息**:
- **URL**: `GET /api/v1/users/{userId}`
- **需要认证**: 否

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
    "id": 67890,
    "username": "janedoe",
    "nickname": "Jane Doe",
    "avatar": "https://cdn.collide.com/avatars/67890.jpg",
    "bio": "摄影爱好者，记录美好生活",
    "gender": "female",
    "location": "上海市",
    "followerCount": 203,
    "followingCount": 145,
    "contentCount": 78,
    "likeCount": 2340,
    "bloggerStatus": "approved",
    "createTime": "2023-11-15T14:20:00"
  }
}
```

**注意**: 公开接口不返回敏感信息如邮箱、手机号、邀请码等。

---

### 3. 更新用户信息

**接口描述**: 更新当前用户的基础信息和扩展信息

**请求信息**:
- **URL**: `PUT /api/v1/users/me`
- **Content-Type**: `application/json`
- **需要认证**: 是

**请求参数**:
```json
{
  "nickname": "New Nickname",
  "avatar": "https://cdn.collide.com/avatars/new-avatar.jpg",
  "email": "newemail@example.com",
  "phone": "13900139000",
  "bio": "更新后的个人简介",
  "gender": "male",
  "birthday": "1990-01-01",
  "location": "深圳市"
}
```

| 参数名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| nickname | String | 否 | 用户昵称，长度2-100字符 |
| avatar | String | 否 | 头像URL |
| email | String | 否 | 邮箱地址，需要格式验证 |
| phone | String | 否 | 手机号码 |
| bio | String | 否 | 个人简介，最长500字符 |
| gender | String | 否 | 性别：male、female、unknown |
| birthday | String | 否 | 生日，格式：YYYY-MM-DD |
| location | String | 否 | 所在地，最长100字符 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "New Nickname",
    "avatar": "https://cdn.collide.com/avatars/new-avatar.jpg",
    "email": "newemail@example.com",
    "bio": "更新后的个人简介",
    "gender": "male",
    "birthday": "1990-01-01",
    "location": "深圳市",
    "updateTime": "2024-01-15T11:45:00"
  }
}
```

---

### 4. 获取用户统计信息

**接口描述**: 获取指定用户的统计信息

**请求信息**:
- **URL**: `GET /api/v1/users/{userId}/statistics`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": 12345,
    "username": "testuser",
    "nickname": "John Doe",
    "avatar": "https://cdn.collide.com/avatars/12345.jpg",
    "role": "user",
    "status": "active",
    "bloggerStatus": "none",
    "followerCount": 156,
    "followingCount": 89,
    "contentCount": 42,
    "likeCount": 1205,
    "invitedCount": 5,
    "createTime": "2023-12-01T08:00:00",
    "lastLoginTime": "2024-01-15T10:30:00",
    "vipExpireTime": null,
    "isVip": false,
    "isBlogger": false
  }
}
```

---

### 5. 分页查询用户列表

**接口描述**: 管理员分页查询用户列表

**请求信息**:
- **URL**: `GET /api/v1/users`
- **需要认证**: 是
- **需要权限**: ADMIN

**请求参数**:

| 参数名 | 类型 | 是否必填 | 默认值 | 说明 |
|--------|------|----------|--------|------|
| pageNum | Integer | 否 | 1 | 页码，从1开始 |
| pageSize | Integer | 否 | 10 | 每页大小，最大100 |
| usernameKeyword | String | 否 | - | 用户名关键词 |
| status | String | 否 | - | 用户状态筛选 |
| role | String | 否 | - | 用户角色筛选 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "username": "testuser",
        "nickname": "John Doe",
        "avatar": "https://cdn.collide.com/avatars/12345.jpg",
        "role": "user",
        "status": "active"
      }
    ],
    "total": 1000,
    "current": 1,
    "size": 10,
    "pages": 100
  }
}
```

---

## 🎖️ 博主认证接口

### 1. 申请博主认证

**接口描述**: 当前用户申请成为认证博主

**请求信息**:
- **URL**: `POST /api/v1/users/blogger/apply`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "博主认证申请已提交，我们将在3-5个工作日内完成审核"
}
```

---

### 2. 查询博主申请状态

**接口描述**: 查询当前用户的博主申请状态

**请求信息**:
- **URL**: `GET /api/v1/users/blogger/status`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "bloggerStatus": "applying",
    "applyTime": "2024-01-15T10:00:00",
    "message": "您的博主申请正在审核中，请耐心等待"
  }
}
```

---

### 3. 取消博主申请

**接口描述**: 取消博主认证申请

**请求信息**:
- **URL**: `DELETE /api/v1/users/blogger/apply`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": "博主申请已取消"
}
```

---

### 4. 检查博主权限

**接口描述**: 检查当前用户是否具有博主权限

**请求信息**:
- **URL**: `GET /api/v1/users/blogger/permission`
- **需要认证**: 是

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "isBlogger": true,
    "bloggerStatus": "approved",
    "approveTime": "2024-01-10T15:30:00"
  }
}
```

---

## 🎯 RPC 接口列表

### 1. 用户信息查询 (RPC)

**服务接口**: `UserFacadeService.query()`

**请求参数**:
```json
{
  "userIdQueryCondition": {
    "userId": 12345
  }
}
```

**或者**:
```json
{
  "userUserNameQueryCondition": {
    "userName": "testuser"
  }
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "userId": 12345,
    "userName": "testuser",
    "nickName": "John Doe",
    "profilePhotoUrl": "https://cdn.collide.com/avatars/12345.jpg",
    "email": "john@example.com",
    "role": "user",
    "status": "active"
  }
}
```

---

### 2. 用户分页查询 (RPC)

**服务接口**: `UserFacadeService.pageQuery()`

**请求参数**:
```json
{
  "pageNo": 1,
  "pageSize": 20,
  "role": "user",
  "status": "active"
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "records": [
      {
        "userId": 12345,
        "userName": "testuser",
        "nickName": "John Doe",
        "profilePhotoUrl": "https://cdn.collide.com/avatars/12345.jpg",
        "role": "user",
        "status": "active"
      }
    ],
    "total": 1000,
    "current": 1,
    "size": 20
  }
}
```

---

### 3. 用户状态更新 (RPC)

**服务接口**: `UserFacadeService.updateStatus()`

**请求参数**:
```json
{
  "userId": 12345,
  "active": true
}
```

**响应示例**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "用户状态已更新为激活",
  "data": null
}
```

---

## 📊 数据模型

### UserInfo（用户信息模型）
```json
{
  "id": 12345,
  "username": "testuser",
  "nickname": "John Doe",
  "avatar": "https://cdn.collide.com/avatars/12345.jpg",
  "email": "john@example.com",
  "phone": "13800138000",
  "role": "user",
  "status": "active",
  "bio": "热爱生活，热爱分享",
  "gender": "male",
  "birthday": "1990-01-01",
  "location": "北京市",
  "followerCount": 156,
  "followingCount": 89,
  "contentCount": 42,
  "likeCount": 1205,
  "loginCount": 128,
  "inviteCode": "XYZ78901",
  "invitedCount": 5,
  "vipExpireTime": null,
  "bloggerStatus": "none",
  "lastLoginTime": "2024-01-15T10:30:00",
  "createTime": "2023-12-01T08:00:00",
  "updateTime": "2024-01-15T10:30:00"
}
```

### LoginResult（登录结果模型）
```json
{
  "user": { ... },
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "isNewUser": false,
  "message": "登录成功"
}
```

### InviteStatistics（邀请统计模型）
```json
{
  "inviteCode": "XYZ78901",
  "totalInvitedCount": 5,
  "invitedUsers": [
    {
      "id": 67890,
      "username": "newuser1",
      "nickname": "用户1703845234567",
      "createTime": "2024-01-15T09:00:00"
    }
  ]
}
```

---

## ❌ 错误码定义

### 认证相关错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| REGISTER_ERROR | 注册失败 | 400 |
| LOGIN_ERROR | 登录失败 | 400 |
| LOGIN_REGISTER_ERROR | 登录或注册失败 | 400 |
| LOGOUT_ERROR | 登出失败 | 400 |
| INVALID_INVITE_CODE | 无效邀请码 | 400 |
| VALIDATE_ERROR | 验证失败 | 400 |
| GET_INVITE_INFO_ERROR | 获取邀请信息失败 | 400 |

### 用户管理错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| USER_NOT_FOUND | 用户不存在 | 404 |
| USERNAME_ALREADY_EXISTS | 用户名已存在 | 400 |
| EMAIL_ALREADY_EXISTS | 邮箱已存在 | 400 |
| PHONE_ALREADY_EXISTS | 手机号已存在 | 400 |
| USER_STATUS_INVALID | 用户状态无效 | 400 |
| USER_DISABLED | 用户已被禁用 | 403 |
| INVALID_GENDER | 性别参数无效 | 400 |

### 博主认证错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| BLOGGER_APPLY_ERROR | 博主申请失败 | 400 |
| BLOGGER_STATUS_ERROR | 博主状态错误 | 400 |
| BLOGGER_PERMISSION_DENIED | 博主权限不足 | 403 |

### 系统错误码

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| SYSTEM_ERROR | 系统异常 | 500 |
| INVALID_STATUS_TRANSITION | 无效状态转换 | 400 |
| STATUS_UPDATE_CONFLICT | 状态更新冲突 | 409 |
| STATUS_UPDATE_ERROR | 状态更新错误 | 500 |

---

## 🎉 使用示例

### 前端完整认证流程

```javascript
class AuthService {
  /**
   * 用户注册
   */
  async register(username, password, inviteCode = null) {
    const response = await fetch('/api/v1/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username,
        password,
        inviteCode
      })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      // 保存token和用户信息
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
      return result.data;
    }
    throw new Error(result.message);
  }

  /**
   * 用户登录
   */
  async login(username, password) {
    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
      return result.data;
    }
    throw new Error(result.message);
  }

  /**
   * 一键登录注册（推荐）
   */
  async loginOrRegister(username, password, inviteCode = null) {
    const response = await fetch('/api/v1/auth/login-or-register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username,
        password,
        inviteCode
      })
    });
    
    const result = await response.json();
    if (result.code === 200) {
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
      
      if (result.data.isNewUser) {
        console.log('新用户自动注册成功！');
      } else {
        console.log('用户登录成功！');
      }
      
      return result.data;
    }
    throw new Error(result.message);
  }

  /**
   * 验证邀请码
   */
  async validateInviteCode(inviteCode) {
    const response = await fetch(`/api/v1/auth/validate-invite-code?inviteCode=${inviteCode}`);
    const result = await response.json();
    
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message);
  }

  /**
   * 获取邀请信息
   */
  async getMyInviteInfo() {
    const token = localStorage.getItem('token');
    const response = await fetch('/api/v1/auth/my-invite-info', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const result = await response.json();
    if (result.code === 200) {
      return result.data;
    }
    throw new Error(result.message);
  }

  /**
   * 登出
   */
  async logout() {
    const token = localStorage.getItem('token');
    const response = await fetch('/api/v1/auth/logout', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    return await response.json();
  }
}

// 使用示例
const authService = new AuthService();

// 方式1：传统注册登录
async function traditionalAuth() {
  try {
    // 注册
    await authService.register('newuser', 'password123', 'ABC12345');
    console.log('注册成功');
    
    // 登录
    await authService.login('newuser', 'password123');
    console.log('登录成功');
  } catch (error) {
    console.error('认证失败:', error.message);
  }
}

// 方式2：一键登录注册（推荐）
async function oneClickAuth() {
  try {
    const result = await authService.loginOrRegister('anyuser', 'password123', 'ABC12345');
    
    if (result.isNewUser) {
      console.log('欢迎新用户！自动注册并登录成功');
    } else {
      console.log('欢迎回来！登录成功');
    }
    
    // 获取邀请信息
    const inviteInfo = await authService.getMyInviteInfo();
    console.log('我的邀请码:', inviteInfo.inviteCode);
    console.log('已邀请人数:', inviteInfo.totalInvitedCount);
    
  } catch (error) {
    console.error('认证失败:', error.message);
  }
}
```

### Java 后端调用示例

```java
@Service
@RequiredArgsConstructor
public class UserServiceExample {
    
    private final UserDomainService userDomainService;
    
    /**
     * 用户注册示例
     */
    public UserUnified registerUser(String username, String password, String inviteCode) {
        try {
            return userDomainService.simpleRegister(username, password, inviteCode);
        } catch (BizException e) {
            log.error("注册失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 用户登录示例
     */
    public UserDomainService.LoginResult loginUser(String username, String password) {
        return userDomainService.login(username, password);
    }
    
    /**
     * 一键登录注册示例
     */
    public UserDomainService.LoginResult loginOrRegister(String username, String password, String inviteCode) {
        return userDomainService.loginWithAutoRegister(username, password, true, inviteCode);
    }
    
    /**
     * 获取用户信息示例
     */
    public UserUnified getUserInfo(Long userId) {
        return userDomainService.getUserById(userId);
    }
    
    /**
     * 验证邀请码示例
     */
    public UserUnified validateInviteCode(String inviteCode) {
        return userDomainService.findInviterByCode(inviteCode);
    }
    
    /**
     * 获取邀请统计示例
     */
    public UserDomainService.InviteStatistics getInviteStats(Long userId) {
        return userDomainService.getInviteStatistics(userId);
    }
}
```

---

## 📝 注意事项

### 1. 数据安全
- 用户密码使用BCrypt加密存储，加盐处理
- 敏感信息（手机号、邮箱）仅对本人可见
- 邀请码系统防止恶意刷取
- SA-Token提供会话安全保障

### 2. 性能优化
- ✨ **去连表设计**: 用户信息统一存储，避免JOIN查询
- 🚀 **冗余统计字段**: 直接存储统计数据，避免实时计算
- 📦 **多级缓存**: JetCache本地缓存 + Redis分布式缓存
- 🔍 **索引优化**: 合理的数据库索引设计
- ⚡ **乐观锁**: 版本号控制并发更新

### 3. 业务规则
- 用户名注册后不可修改，全局唯一
- 昵称可以重复，支持修改
- 每用户一个固定邀请码，自动生成
- 博主认证有严格的审核流程
- 用户状态变更支持幂等性操作

### 4. 接口限制
- 个人信息更新频率限制：每分钟最多3次
- 博主申请频率限制：每30天最多1次
- 头像上传大小限制：最大2MB
- 个人简介长度限制：最多500字符
- 邀请码生成防重试机制

### 5. 简化特性
- ❌ 无邮件/短信验证：注册即激活
- ❌ 无复杂密码策略：最低6位即可
- ❌ 无图形验证码：防止用户体验下降
- ✅ 支持邀请码：建立用户关系
- ✅ 登录自动注册：降低注册门槛

---

## 📞 技术支持

- **开发团队**: Collide Team
- **架构版本**: v2.0 (简化认证系统)
- **文档版本**: v2.0
- **更新日期**: 2024-01-16
- **联系方式**: tech@collide.com

---

*本文档基于 User 模块 v2.0.0 版本生成，采用去连表设计和简化认证系统。如有疑问请联系技术团队。* 
*本文档基于 User 模块 v1.0.0 版本生成，如有疑问请联系技术团队。* 