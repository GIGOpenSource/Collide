# Collide 认证服务 API 文档

## 概述

Collide 认证服务提供核心的用户认证功能，包括用户登录、注册、Token验证等功能。基于Sa-Token实现认证，支持邀请码功能。

**服务版本**: v2.0.0 (简洁版)  
**基础路径**: `/api/v1/auth`  
**Dubbo服务**: `collide-auth`  
**设计理念**: 简洁高效的认证系统，基于简洁版用户API，提供核心认证功能

---

## 📋 API 接口清单

| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|-----|---------|----------|
| `POST /register` | POST | 用户注册 | 无 |
| `POST /login` | POST | 用户登录 | 无 |
| `POST /login-or-register` | POST | 登录或注册（核心接口） | 无 |
| `POST /logout` | POST | 用户登出 | 需要登录 |
| `GET /validate-invite-code` | GET | 验证邀请码 | 无 |
| `GET /my-invite-info` | GET | 获取我的邀请信息 | 需要登录 |
| `GET /me` | GET | 获取当前用户信息 | 需要登录 |
| `GET /verify-token` | GET | 验证Token | 需要登录 |
| `GET /test` | GET | 健康检查 | 无 |

---

## 用户认证 API

### 1. 用户注册
**接口路径**: `POST /api/v1/auth/register`  
**接口描述**: 用户名密码注册，支持邀请码

#### 请求参数
```json
{
  "username": "testuser",                    // 必填，用户名
  "password": "user_password",               // 必填，密码
  "inviteCode": "INVITE123"                  // 可选，邀请码
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "testuser",
      "status": "active",
      "role": "user",
      "inviteCode": "USER123456",           // 新用户的邀请码
      "createTime": "2024-01-16T10:30:00"
    },
    "token": "satoken_value_here",
    "message": "注册成功"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "USER_ALREADY_EXISTS",
  "responseMessage": "用户名已存在"
}
```

---

### 2. 用户登录
**接口路径**: `POST /api/v1/auth/login`  
**接口描述**: 用户名密码登录

#### 请求参数
```json
{
  "username": "testuser",                    // 必填，用户名
  "password": "user_password"                // 必填，密码
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "nickname": "测试用户",
      "status": "active",
      "role": "user",
      "inviteCode": "USER123456",
      "invitedCount": 5,
      "lastLoginTime": "2024-01-15T09:20:00"
    },
    "token": "satoken_value_here",
    "message": "登录成功"
  }
}
```

**失败响应 (401)**:
```json
{
  "success": false,
  "responseCode": "LOGIN_FAILED",
  "responseMessage": "用户名或密码错误"
}
```

---

### 3. 登录或注册（核心接口）
**接口路径**: `POST /api/v1/auth/login-or-register`  
**接口描述**: 核心接口：用户存在则登录，不存在则自动注册

#### 请求参数
```json
{
  "username": "newuser",                     // 必填，用户名
  "password": "user_password",               // 必填，密码
  "inviteCode": "INVITE123"                  // 可选，邀请码（仅在自动注册时使用）
}
```

#### 响应示例
**登录成功 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "newuser",
      "nickname": "新用户",
      "status": "active",
      "role": "user"
    },
    "token": "satoken_value_here",
    "message": "登录成功",
    "isNewUser": false                      // false表示是已有用户登录
  }
}
```

**自动注册并登录成功 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS", 
  "responseMessage": "操作成功",
  "data": {
    "user": {
      "id": 67890,
      "username": "newuser",
      "nickname": "newuser",
      "status": "active",
      "role": "user",
      "inviteCode": "USER789012"
    },
    "token": "satoken_value_here",
    "message": "注册并登录成功",
    "isNewUser": true                       // true表示是新注册用户
  }
}
```

---

### 4. 用户登出
**接口路径**: `POST /api/v1/auth/logout`  
**接口描述**: 退出登录
**认证要求**: 需要登录

#### 请求参数
无需请求体

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": "登出成功"
}
```

---

## 邀请码管理 API

### 1. 验证邀请码
**接口路径**: `GET /api/v1/auth/validate-invite-code`  
**接口描述**: 检查邀请码是否有效并返回邀请人信息

#### 请求参数
- **inviteCode** (query): 邀请码，必填

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
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

---

### 2. 获取我的邀请信息
**接口路径**: `GET /api/v1/auth/my-invite-info`  
**接口描述**: 获取当前用户的邀请码和邀请统计
**认证要求**: 需要登录

#### 请求参数
无

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "inviteCode": "USER123456",             // 我的邀请码
    "totalInvitedCount": 5,                 // 总邀请人数
    "invitedUsers": []                      // 邀请用户列表（暂未实现）
  }
}
```

---

## Token管理 API

### 1. 获取当前用户信息
**接口路径**: `GET /api/v1/auth/me`  
**接口描述**: 通过Token验证并返回当前用户信息
**认证要求**: 需要登录

#### 请求参数
无（通过Header中的Token认证）

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "138****8000",
    "avatar": "https://avatar.example.com/user.jpg",
    "status": "active",
    "role": "user",
    "inviteCode": "USER123456",
    "invitedCount": 5,
    "createTime": "2024-01-01T00:00:00",
    "lastLoginTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 验证Token
**接口路径**: `GET /api/v1/auth/verify-token`  
**接口描述**: 验证当前Token是否有效
**认证要求**: 需要登录

#### 请求参数
无（通过Header中的Token认证）

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": {
    "valid": true,
    "userId": 12345,
    "token": "satoken_value_here",
    "message": "Token有效"
  }
}
```

---

## 系统管理 API

### 1. 健康检查
**接口路径**: `GET /api/v1/auth/test`  
**接口描述**: 检查认证服务状态

#### 请求参数
无

#### 响应示例
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功",
  "data": "Collide Auth v2.0 - 简洁版认证服务运行正常"
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| SUCCESS | 操作成功 |
| USER_ALREADY_EXISTS | 用户名已存在 |
| LOGIN_FAILED | 登录失败（用户名或密码错误） |
| PASSWORD_ERROR | 密码错误 |
| USER_NOT_FOUND | 用户信息获取失败 |
| USER_INFO_ERROR | 注册成功但获取用户信息失败 |
| USER_REGISTER_FAILED | 用户注册失败 |
| AUTO_REGISTER_FAILED | 自动注册失败 |
| LOGOUT_ERROR | 登出失败 |
| TOKEN_INVALID | Token验证失败 |
| INVALID_INVITE_CODE | 邀请码不能为空 |
| VALIDATE_ERROR | 验证失败 |
| GET_INVITE_INFO_ERROR | 获取邀请信息失败 |
| SYSTEM_ERROR | 系统错误，请稍后重试 |

---

## 🔧 技术说明

### 认证机制
- **认证框架**: Sa-Token
- **Token类型**: 无状态Token
- **Session管理**: 基于Sa-Token的Session机制
- **权限存储**: Session中存储用户基础信息供网关鉴权使用

### 密码安全
- **加密方式**: BCrypt（在用户服务中处理）
- **传输安全**: 建议前端加密后传输
- **存储安全**: 数据库中存储加密密码

### 邀请码机制
- **邀请码生成**: 由用户服务生成唯一邀请码
- **邀请统计**: 支持邀请人数统计
- **邀请关系**: 建立邀请人与被邀请人的关系

---

## 🚀 使用示例

### 前端集成示例

```javascript
// 1. 用户注册
const registerResponse = await fetch('/api/v1/auth/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'newuser',
    password: 'userpassword',
    inviteCode: 'INVITE123'
  })
});

// 2. 用户登录
const loginResponse = await fetch('/api/v1/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'testuser',
    password: 'userpassword'
  })
});

// 3. 获取用户信息（需要Token）
const userResponse = await fetch('/api/v1/auth/me', {
  headers: { 
    'Authorization': 'Bearer ' + token,
    'satoken': token  // Sa-Token推荐的Header名称
  }
});

// 4. 验证邀请码
const validateResponse = await fetch('/api/v1/auth/validate-invite-code?inviteCode=INVITE123');

// 5. 用户登出
const logoutResponse = await fetch('/api/v1/auth/logout', {
  method: 'POST',
  headers: { 'satoken': token }
});
```

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (简洁版)