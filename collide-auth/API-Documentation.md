# Collide 认证服务 API 文档

## 概述

Collide 认证服务提供简化的用户认证功能，参考 nft-turbo 设计哲学，实现基于用户名密码的注册登录系统。

**服务版本**: v2.0  
**基础路径**: `/api/v1/auth`  
**设计理念**: 简化认证流程，支持自动注册，提供邀请码功能

---

## 接口列表

### 1. 用户注册
**接口路径**: `POST /api/v1/auth/register`  
**接口描述**: 用户名密码注册，支持邀请码，注册成功后自动登录

#### 请求参数
```json
{
  "username": "testuser",     // 必填，用户名（3-20字符，仅字母数字下划线）
  "password": "123456",       // 必填，密码（6-20字符）
  "inviteCode": "ABC123"      // 可选，邀请码
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "testuser",
      "avatar": null,
      "role": "user",
      "status": "inactive",
      "createTime": "2024-01-16T10:30:00",
      "inviteCode": null,
      "inviterId": null
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "message": "注册成功"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "errorCode": "USERNAME_ALREADY_EXISTS",
  "errorMessage": "用户名已存在"
}
```

---

### 2. 用户登录
**接口路径**: `POST /api/v1/auth/login`  
**接口描述**: 用户名密码登录，如果用户不存在则自动注册（参考 nft-turbo 设计）

#### 请求参数
```json
{
  "username": "testuser",     // 必填，用户名
  "password": "123456",       // 必填，密码
  "inviteCode": "ABC123",     // 可选，邀请码（仅在自动注册时使用）
  "rememberMe": false         // 可选，记住我（默认false）
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "testuser",
      "avatar": null,
      "role": "user",
      "status": "ACTIVE",
      "createTime": "2024-01-16T10:30:00",
      "inviteCode": "DEF456",
      "inviterId": null
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isNewUser": false,
    "message": "登录成功"
  }
}
```

**自动注册响应 (200)**:
```json
{
  "success": true,
  "data": {
    "user": { /* 用户信息 */ },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "isNewUser": true,
    "message": "注册并登录成功"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "errorCode": "LOGIN_ERROR",
  "errorMessage": "用户名或密码错误"
}
```

---

### 3. 登录或注册（推荐接口）
**接口路径**: `POST /api/v1/auth/login-or-register`  
**接口描述**: 核心功能接口，用户不存在时自动注册，一个接口解决登录和注册需求

#### 请求参数
```json
{
  "username": "testuser",     // 必填，用户名（3-20字符）
  "password": "123456",       // 必填，密码（6-50字符）
  "inviteCode": "ABC123"      // 可选，邀请码
}
```

#### 响应示例
**响应格式与登录接口相同**，通过 `isNewUser` 字段区分是登录还是注册。

---

### 4. 用户登出
**接口路径**: `POST /api/v1/auth/logout`  
**接口描述**: 退出登录，清除会话信息  
**权限要求**: 需要登录

#### 请求参数
无需请求体，通过 Header 中的 token 识别用户

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "data": "登出成功"
}
```

---

### 5. 验证邀请码
**接口路径**: `GET /api/v1/auth/validate-invite-code`  
**接口描述**: 检查邀请码是否有效并返回邀请人信息

#### 请求参数
- **inviteCode** (query): 邀请码，必填

**请求示例**: `/api/v1/auth/validate-invite-code?inviteCode=ABC123`

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "data": {
    "valid": true,
    "inviter": {
      "id": 12345,
      "username": "inviter_user",
      "nickname": "邀请用户",
      "avatar": ""
    }
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "errorCode": "INVALID_INVITE_CODE",
  "errorMessage": "邀请码不能为空"
}
```

---

### 6. 获取我的邀请信息
**接口路径**: `GET /api/v1/auth/my-invite-info`  
**接口描述**: 获取当前用户的邀请码和邀请统计  
**权限要求**: 需要登录

#### 请求参数
无需参数，通过 token 识别当前用户

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "data": {
    "inviteCode": "ABC12345",
    "totalInvitedCount": 5,
    "invitedUsers": [
      {
        "id": 67890,
        "username": "invited_user1",
        "nickname": "被邀请用户1",
        "createTime": "2024-01-15T09:00:00"
      }
    ]
  }
}
```

---

### 7. 服务健康检查
**接口路径**: `GET /api/v1/auth/test`  
**接口描述**: 检查认证服务是否正常运行

#### 响应示例
```
Collide Auth Service v2.0 is running! (Simplified Authentication System)
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| `USERNAME_ALREADY_EXISTS` | 用户名已存在 |
| `USER_STATUS_IS_NOT_ACTIVE` | 用户状态不可用 |
| `LOGIN_ERROR` | 登录失败（用户名或密码错误） |
| `REGISTER_ERROR` | 注册失败 |
| `LOGIN_REGISTER_ERROR` | 登录或注册操作失败 |
| `LOGOUT_ERROR` | 登出失败 |
| `INVALID_INVITE_CODE` | 邀请码无效 |
| `VALIDATE_ERROR` | 验证失败 |
| `GET_INVITE_INFO_ERROR` | 获取邀请信息失败 |
| `SYSTEM_ERROR` | 系统异常 |

---

## 认证机制

### Token 使用
- 认证成功后返回 JWT token
- 后续请求需在 Header 中携带：`Authorization: Bearer {token}`
- Token 默认有效期：7天
- 支持"记住我"功能（login 接口）

### 会话管理
- 基于 Sa-Token 框架
- 支持单点登录
- 自动会话延期

---

## 设计特色

### 🚀 **参考 nft-turbo 设计哲学**
- **自动注册**: 用户不存在时自动注册，提升用户体验
- **简化流程**: 只需用户名+密码即可完成注册
- **统一接口**: `/login-or-register` 一个接口解决所有需求

### 🔐 **安全特性**
- 密码加盐哈希存储
- 完整的参数验证
- 详细的操作日志
- 用户状态检查

### 🎯 **易用性**
- 清晰的响应格式
- 详细的错误提示
- 完整的邀请码系统
- 支持昵称自动生成

---

## 使用示例

### 快速上手
```javascript
// 1. 登录或注册（推荐）
const response = await fetch('/api/v1/auth/login-or-register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'testuser',
    password: '123456'
  })
});

// 2. 获取 token
const { data } = await response.json();
const token = data.token;

// 3. 后续请求携带 token
const userInfo = await fetch('/api/v1/auth/my-invite-info', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

### 注册流程
```javascript
// 方式一：直接注册
await fetch('/api/v1/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'newuser',
    password: '123456',
    inviteCode: 'ABC123'  // 可选
  })
});

// 方式二：登录时自动注册（推荐）
await fetch('/api/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'newuser',
    password: '123456',
    rememberMe: true
  })
});
```

---

**文档版本**: v2.0  
**最后更新**: 2024-01-16  
**维护团队**: Collide Team 