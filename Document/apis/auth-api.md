# Collide 认证服务 API 文档

## 概述

Collide 认证服务提供完整的用户认证和授权功能，包括用户登录、注册、Token管理、权限验证等核心功能。基于JWT实现无状态认证，支持多种登录方式和安全机制。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/auth`  
**Dubbo服务**: `collide-auth`  
**设计理念**: 安全可靠的认证系统，支持多种认证方式，提供完整的用户认证生命周期管理

---

## 用户认证 API

### 1. 用户登录
**接口路径**: `POST /api/v1/auth/login`  
**接口描述**: 用户账号密码登录

#### 请求参数
```json
{
  "loginType": "username",                   // 必填，登录类型：username/email/phone
  "username": "testuser",                    // 条件必填，用户名（loginType=username时）
  "email": "test@example.com",               // 条件必填，邮箱（loginType=email时）
  "phone": "13800138000",                    // 条件必填，手机号（loginType=phone时）
  "password": "encrypted_password",          // 必填，密码（前端加密）
  "captchaToken": "captcha_token_123",       // 可选，验证码Token
  "captchaCode": "1234",                     // 可选，验证码
  "rememberMe": true,                        // 可选，是否记住登录，默认false
  "clientInfo": {                            // 可选，客户端信息
    "deviceId": "device_123",
    "deviceType": "mobile",
    "appVersion": "1.0.0",
    "platform": "android",
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
  "responseMessage": "登录成功",
  "data": {
    "userId": 12345,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "138****8000",
    "avatar": "https://avatar.example.com/user.jpg",
    "status": "ACTIVE",
    "role": "USER",
    "permissions": ["content:read", "comment:create"],
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "tokenType": "Bearer",
    "expiresIn": 7200,                       // Token过期时间（秒）
    "refreshExpiresIn": 2592000,             // RefreshToken过期时间（秒）
    "loginTime": "2024-01-16T10:30:00",
    "lastLoginTime": "2024-01-15T09:20:00",
    "loginCount": 156
  }
}
```

**失败响应 (401)**:
```json
{
  "success": false,
  "responseCode": "INVALID_CREDENTIALS",
  "responseMessage": "用户名或密码错误"
}
```

---

### 2. 用户注册
**接口路径**: `POST /api/v1/auth/register`  
**接口描述**: 用户账号注册

#### 请求参数
```json
{
  "username": "newuser",                     // 必填，用户名（3-20字符）
  "password": "encrypted_password",          // 必填，密码（前端加密）
  "confirmPassword": "encrypted_password",   // 必填，确认密码
  "email": "newuser@example.com",            // 可选，邮箱
  "phone": "13900139000",                    // 可选，手机号
  "nickname": "新用户",                      // 可选，昵称
  "verificationCode": "123456",              // 条件必填，验证码（手机/邮箱注册时）
  "verificationToken": "verify_token_123",   // 条件必填，验证码Token
  "inviteCode": "INVITE123",                 // 可选，邀请码
  "agreeTerms": true,                        // 必填，是否同意用户协议
  "clientInfo": {                            // 可选，客户端信息
    "deviceId": "device_123",
    "deviceType": "mobile",
    "platform": "android"
  }
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "注册成功",
  "data": {
    "userId": 67890,
    "username": "newuser",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "phone": "139****9000",
    "status": "ACTIVE",
    "inviteCode": "NEW123",                  // 新用户的邀请码
    "registerTime": "2024-01-16T10:30:00",
    "needEmailVerification": false,
    "needPhoneVerification": false
  }
}
```

---

### 3. 第三方登录
**接口路径**: `POST /api/v1/auth/oauth/{provider}`  
**接口描述**: 第三方平台登录

#### 请求参数
- **provider** (path): 第三方平台：wechat/qq/github/google，必填

```json
{
  "authCode": "oauth_code_from_provider",    // 必填，第三方授权码
  "state": "random_state_string",            // 必填，防CSRF攻击的随机字符串
  "redirectUri": "https://app.example.com/oauth/callback", // 可选，回调地址
  "clientInfo": {                            // 可选，客户端信息
    "deviceId": "device_123",
    "platform": "wechat_miniprogram"
  }
}
```

---

### 4. 刷新Token
**接口路径**: `POST /api/v1/auth/refresh`  
**接口描述**: 使用RefreshToken刷新AccessToken

#### 请求参数
```json
{
  "refreshToken": "refresh_token_here",      // 必填，RefreshToken
  "deviceId": "device_123"                   // 可选，设备ID
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "accessToken": "new_access_token_here",
    "refreshToken": "new_refresh_token_here",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "refreshExpiresIn": 2592000
  }
}
```

---

### 5. 退出登录
**接口路径**: `POST /api/v1/auth/logout`  
**接口描述**: 用户退出登录

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "deviceId": "device_123",                 // 可选，设备ID
  "logoutType": "normal"                    // 可选，退出类型：normal/all_devices
}
```

---

## 密码管理 API

### 1. 修改密码
**接口路径**: `POST /api/v1/auth/password/change`  
**接口描述**: 用户修改密码

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "oldPassword": "old_encrypted_password",   // 必填，原密码（加密）
  "newPassword": "new_encrypted_password",   // 必填，新密码（加密）
  "confirmPassword": "new_encrypted_password" // 必填，确认新密码
}
```

---

### 2. 忘记密码
**接口路径**: `POST /api/v1/auth/password/forgot`  
**接口描述**: 忘记密码申请重置

#### 请求参数
```json
{
  "resetType": "email",                      // 必填，重置方式：email/phone
  "email": "user@example.com",               // 条件必填，邮箱（resetType=email时）
  "phone": "13800138000",                    // 条件必填，手机号（resetType=phone时）
  "captchaToken": "captcha_token_123",       // 可选，验证码Token
  "captchaCode": "1234"                      // 可选，验证码
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "resetToken": "reset_token_here",
    "expiresIn": 1800,                       // 重置Token过期时间（秒）
    "message": "重置密码邮件已发送，请查收"
  }
}
```

---

### 3. 重置密码
**接口路径**: `POST /api/v1/auth/password/reset`  
**接口描述**: 通过重置Token重置密码

#### 请求参数
```json
{
  "resetToken": "reset_token_here",          // 必填，重置Token
  "verificationCode": "123456",              // 必填，验证码
  "newPassword": "new_encrypted_password",   // 必填，新密码（加密）
  "confirmPassword": "new_encrypted_password" // 必填，确认新密码
}
```

---

## 验证码管理 API

### 1. 发送验证码
**接口路径**: `POST /api/v1/auth/verification/send`  
**接口描述**: 发送手机或邮箱验证码

#### 请求参数
```json
{
  "type": "email",                          // 必填，验证码类型：email/sms
  "purpose": "register",                    // 必填，用途：register/login/reset_password/bind
  "email": "user@example.com",              // 条件必填，邮箱（type=email时）
  "phone": "13800138000",                   // 条件必填，手机号（type=sms时）
  "captchaToken": "captcha_token_123",      // 可选，验证码Token
  "captchaCode": "1234"                     // 可选，验证码
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "verificationToken": "verify_token_here",
    "expiresIn": 300,                        // 验证码过期时间（秒）
    "cooldownTime": 60,                      // 重发冷却时间（秒）
    "message": "验证码已发送，请查收"
  }
}
```

---

### 2. 验证验证码
**接口路径**: `POST /api/v1/auth/verification/verify`  
**接口描述**: 验证验证码

#### 请求参数
```json
{
  "verificationToken": "verify_token_here",  // 必填，验证Token
  "verificationCode": "123456",             // 必填，验证码
  "purpose": "register"                     // 必填，用途
}
```

---

## Token管理 API

### 1. 验证Token
**接口路径**: `POST /api/v1/auth/token/validate`  
**接口描述**: 验证AccessToken有效性

#### 请求参数
```json
{
  "accessToken": "access_token_here",        // 必填，AccessToken
  "requiredPermissions": ["content:read"]    // 可选，需要的权限列表
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "valid": true,
    "userId": 12345,
    "username": "testuser",
    "roles": ["USER"],
    "permissions": ["content:read", "comment:create"],
    "expiresAt": "2024-01-16T12:30:00",
    "remainingTime": 3600                    // 剩余有效时间（秒）
  }
}
```

---

### 2. 撤销Token
**接口路径**: `POST /api/v1/auth/token/revoke`  
**接口描述**: 撤销指定Token

#### 请求参数
```json
{
  "tokenType": "refresh",                    // 必填，Token类型：access/refresh/all
  "token": "token_to_revoke",                // 条件必填，要撤销的Token
  "userId": 12345,                           // 可选，用户ID
  "deviceId": "device_123"                   // 可选，设备ID
}
```

---

### 3. 获取用户Token列表
**接口路径**: `GET /api/v1/auth/tokens/{userId}`  
**接口描述**: 获取用户的活跃Token列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **tokenType** (query): Token类型，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

## 权限管理 API

### 1. 检查权限
**接口路径**: `POST /api/v1/auth/permission/check`  
**接口描述**: 检查用户是否有指定权限

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "permissions": ["content:create", "comment:delete"], // 必填，权限列表
  "resourceId": "content_123",               // 可选，资源ID
  "resourceType": "content"                  // 可选，资源类型
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "hasPermission": true,
    "deniedPermissions": [],
    "permissionDetails": [
      {
        "permission": "content:create",
        "granted": true,
        "source": "role"
      },
      {
        "permission": "comment:delete",
        "granted": false,
        "reason": "insufficient_role"
      }
    ]
  }
}
```

---

### 2. 获取用户权限
**接口路径**: `GET /api/v1/auth/permissions/{userId}`  
**接口描述**: 获取用户的所有权限

#### 请求参数
- **userId** (path): 用户ID，必填
- **includeInherited** (query): 是否包含继承权限，可选，默认true

---

### 3. 分配角色
**接口路径**: `POST /api/v1/auth/roles/assign`  
**接口描述**: 为用户分配角色

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "roles": ["BLOGGER", "VIP"],               // 必填，角色列表
  "operatorId": 99999,                      // 必填，操作人ID
  "reason": "用户申请博主认证通过"           // 可选，分配原因
}
```

---

## 安全管理 API

### 1. 账号安全检查
**接口路径**: `POST /api/v1/auth/security/check`  
**接口描述**: 账号安全状态检查

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "checkType": "login",                     // 必填，检查类型：login/operation/payment
  "clientInfo": {                           // 可选，客户端信息
    "ip": "192.168.1.100",
    "deviceId": "device_123",
    "userAgent": "Mozilla/5.0..."
  }
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "securityLevel": "normal",               // 安全等级：safe/normal/warning/danger
    "riskFactors": [],                       // 风险因素列表
    "needAdditionalVerification": false,     // 是否需要额外验证
    "recommendActions": [],                  // 建议操作
    "lastSecurityCheck": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 异常登录通知
**接口路径**: `POST /api/v1/auth/security/suspicious-login`  
**接口描述**: 异常登录行为通知

#### 请求参数
```json
{
  "userId": 12345,                          // 必填，用户ID
  "loginInfo": {                            // 必填，登录信息
    "ip": "192.168.1.100",
    "location": "北京市朝阳区",
    "deviceInfo": "iPhone 13",
    "loginTime": "2024-01-16T10:30:00"
  },
  "riskLevel": "medium",                    // 必填，风险等级：low/medium/high
  "notificationMethods": ["email", "sms"]   // 可选，通知方式
}
```

---

### 3. 设备管理
**接口路径**: `GET /api/v1/auth/devices/{userId}`  
**接口描述**: 获取用户登录设备列表

#### 请求参数
- **userId** (path): 用户ID，必填
- **status** (query): 设备状态：active/inactive，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "datas": [
      {
        "deviceId": "device_123",
        "deviceName": "iPhone 13",
        "deviceType": "mobile",
        "platform": "ios",
        "lastLoginTime": "2024-01-16T10:30:00",
        "lastLoginIp": "192.168.1.100",
        "lastLoginLocation": "北京市朝阳区",
        "status": "active",
        "isCurrent": true
      }
    ],
    "total": 3,
    "currentPage": 1
  }
}
```

---

### 4. 移除设备
**接口路径**: `DELETE /api/v1/auth/devices/{userId}/{deviceId}`  
**接口描述**: 移除用户登录设备

#### 请求参数
- **userId** (path): 用户ID，必填
- **deviceId** (path): 设备ID，必填

---

## 认证统计 API

### 1. 登录统计
**接口路径**: `GET /api/v1/auth/statistics/login`  
**接口描述**: 获取登录统计信息

#### 请求参数
- **timeRange** (query): 时间范围（天），可选，默认30
- **groupBy** (query): 分组方式：day/hour/platform，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "totalLogins": 15000,                    // 总登录次数
    "uniqueUsers": 5000,                     // 独立登录用户数
    "successRate": 95.2,                     // 登录成功率（%）
    "averageSessionTime": 3600,              // 平均会话时长（秒）
    "platformDistribution": {                // 平台分布
      "web": 6000,
      "mobile": 8000,
      "miniprogram": 1000
    },
    "loginTrend": [                          // 登录趋势
      {
        "date": "2024-01-16",
        "count": 500
      }
    ]
  }
}
```

---

### 2. 用户认证状态
**接口路径**: `GET /api/v1/auth/statistics/user/{userId}`  
**接口描述**: 获取用户认证状态统计

#### 请求参数
- **userId** (path): 用户ID，必填

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| INVALID_CREDENTIALS | 用户名或密码错误 |
| ACCOUNT_LOCKED | 账户被锁定 |
| ACCOUNT_DISABLED | 账户被禁用 |
| PASSWORD_EXPIRED | 密码已过期 |
| TOKEN_EXPIRED | Token已过期 |
| TOKEN_INVALID | Token无效 |
| REFRESH_TOKEN_EXPIRED | RefreshToken已过期 |
| PERMISSION_DENIED | 权限不足 |
| VERIFICATION_CODE_EXPIRED | 验证码已过期 |
| VERIFICATION_CODE_INVALID | 验证码错误 |
| CAPTCHA_REQUIRED | 需要验证码 |
| CAPTCHA_INVALID | 验证码错误 |
| TOO_MANY_ATTEMPTS | 尝试次数过多 |
| DEVICE_NOT_TRUSTED | 设备不受信任 |
| SUSPICIOUS_LOGIN | 异常登录 |
| USERNAME_EXISTS | 用户名已存在 |
| EMAIL_EXISTS | 邮箱已存在 |
| PHONE_EXISTS | 手机号已存在 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0