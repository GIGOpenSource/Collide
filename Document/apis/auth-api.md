# Collide 认证服务 API 文档

## 概述

Collide 认证服务提供核心的用户认证功能，包括用户注册、登录、登出、Token验证等。基于Sa-Token实现认证机制，支持邀请码功能。

**服务版本**: v2.0.0 (重构优化版)  
**基础路径**: `/api/v1/auth`  
**模块名称**: `collide-auth`  
**架构设计**: Controller-Service-Dubbo分层架构，职责清晰，维护友好  
**设计理念**: 专注核心认证功能，模块职责边界清晰，安全可靠

## 📝 重构说明 (v2.0.0)

### 主要变更
- **职责聚焦**: 专注核心认证功能，移除用户信息查询、邀请码管理等非认证功能
- **架构优化**: 采用三层架构（Controller-Service-Dubbo），提升代码质量和可维护性
- **参数验证**: 完善的Bean Validation注解验证，自动化参数校验
- **错误处理**: 安全的错误处理机制，不暴露系统内部信息
- **代码质量**: 零编译错误，零警告，达到企业级标准

### 移除的功能及新归属
| 原功能 | 新归属模块 | 新接口路径 |
|--------|------------|------------|
| 验证邀请码 | 用户模块 | `POST /api/v1/users/invite/validate` |
| 获取邀请信息 | 用户模块 | `GET /api/v1/users/me/invite-info` |
| 获取当前用户信息 | 用户模块 | `GET /api/v1/users/me` |

---

## 📋 API 接口清单

| 接口路径 | 方法 | 功能描述 | 认证要求 |
|---------|-----|---------|----------|
| `POST /register` | POST | 用户注册 | 无 |
| `POST /login` | POST | 用户登录 | 无 |
| `POST /login-or-register` | POST | 登录或注册（核心接口） | 无 |
| `POST /logout` | POST | 用户登出 | 需要登录 |
| `GET /verify-token` | GET | 验证Token | 需要登录 |
| `GET /test` | GET | 健康检查 | 无 |

---

## 用户认证 API

### 1. 用户注册
**接口路径**: `POST /api/v1/auth/register`  
**接口描述**: 用户名密码注册，支持邀请码，注册成功后自动登录

#### 请求参数
```json
{
  "username": "testuser",                    // 必填，用户名，3-50字符，只能包含字母、数字、下划线
  "password": "userpassword",                // 必填，密码，6-20字符
  "inviteCode": "INVITE123"                  // 可选，邀请码，最大20字符
}
```

#### 参数验证规则
- `username`: 必填，长度3-50字符，正则 `^[a-zA-Z0-9_]+$`
- `password`: 必填，长度6-20字符
- `inviteCode`: 可选，最大20字符

#### 响应示例
**注册成功并自动登录 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "注册成功",
  "data": {
    "token": "satoken_value_here"           // 自动登录后的Token
  }
}
```

**注册成功但自动登录失败 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "注册成功",
  "data": null                              // 需要用户手动登录
}
```

**参数错误 (400)**:
```json
{
  "success": false,
  "code": "INVALID_PARAMETER",
  "message": "用户名长度必须在3-50个字符之间",
  "data": null
}
```

**注册失败 (500)**:
```json
{
  "success": false,
  "code": "USER_REGISTER_ERROR",
  "message": "注册失败，请稍后重试",
  "data": null
}
```

---

### 2. 用户登录
**接口路径**: `POST /api/v1/auth/login`  
**接口描述**: 用户名密码登录

#### 请求参数
```json
{
  "username": "testuser",                    // 必填，用户名，最大50字符
  "password": "userpassword"                 // 必填，密码，最大100字符
}
```

#### 参数验证规则
- `username`: 必填，长度1-50字符
- `password`: 必填，长度1-100字符

#### 响应示例
**登录成功 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "testuser",
      "email": "test@example.com",
      "phone": "138****8000",
      "status": 1,                          // 用户状态：1-正常，2-未激活，3-已暂停，4-已封禁
      "statusDesc": "正常",
      "role": "user",                       // 用户角色
      "nickname": "测试用户",               // 用户昵称（来自用户资料）
      "avatar": "https://avatar.example.com/user.jpg"  // 用户头像（来自用户资料）
    },
    "token": "satoken_value_here",
    "message": "登录成功"
  }
}
```

**登录失败 (500)**:
```json
{
  "success": false,
  "code": "USER_LOGIN_ERROR",
  "message": "登录失败，请检查用户名和密码",
  "data": null
}
```

---

### 3. 登录或注册（核心接口）
**接口路径**: `POST /api/v1/auth/login-or-register`  
**接口描述**: 核心接口：用户存在则登录，不存在则自动注册

#### 请求参数
```json
{
  "username": "newuser",                     // 必填，用户名，3-50字符，只能包含字母、数字、下划线
  "password": "userpassword",                // 必填，密码，6-20字符
  "inviteCode": "INVITE123"                  // 可选，邀请码，最大20字符（仅在自动注册时使用）
}
```

#### 参数验证规则
- `username`: 必填，长度3-50字符，正则 `^[a-zA-Z0-9_]+$`
- `password`: 必填，长度6-20字符
- `inviteCode`: 可选，最大20字符

#### 响应示例
**登录成功 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "user": {
      "id": 12345,
      "username": "newuser",
      "email": "user@example.com",
      "phone": "138****8001",
      "status": 1,
      "statusDesc": "正常",
      "role": "user",
      "nickname": "新用户",
      "avatar": "https://avatar.example.com/newuser.jpg"
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
  "code": "SUCCESS", 
  "message": "操作成功",
  "data": {
    "user": {
      "id": 67890,
      "username": "newuser",
      "email": null,
      "phone": null,
      "status": 1,
      "statusDesc": "正常",
      "role": "user",
      "nickname": "newuser",               // 默认昵称与用户名相同
      "avatar": null
    },
    "token": "satoken_value_here",
    "message": "注册并登录成功",
    "isNewUser": true                       // true表示是新注册用户
  }
}
```

**操作失败 (500)**:
```json
{
  "success": false,
  "code": "LOGIN_OR_REGISTER_ERROR",
  "message": "操作失败，请稍后重试",
  "data": null
}
```

---

### 4. 用户登出
**接口路径**: `POST /api/v1/auth/logout`  
**接口描述**: 退出登录，清理用户会话  
**认证要求**: 需要登录（@SaCheckLogin）

#### 请求参数
无需请求体

#### 请求头
```http
satoken: your_token_here
# 或
Authorization: Bearer your_token_here
```

#### 响应示例
**登出成功 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": "登出成功"
}
```

**登出失败 (500)**:
```json
{
  "success": false,
  "code": "LOGOUT_ERROR",
  "message": "登出失败: 具体错误信息",
  "data": null
}
```

---

## Token管理 API

### 1. 验证Token
**接口路径**: `GET /api/v1/auth/verify-token`  
**接口描述**: 验证当前Token是否有效  
**认证要求**: 需要登录（@SaCheckLogin）

#### 请求参数
无需请求参数

#### 请求头
```http
satoken: your_token_here
# 或
Authorization: Bearer your_token_here
```

#### 响应示例
**Token有效 (200)**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "valid": true,
    "userId": 12345,
    "token": "satoken_value_here",
    "message": "Token有效"
  }
}
```

**Token无效 (500)**:
```json
{
  "success": false,
  "code": "TOKEN_INVALID",
  "message": "Token验证失败: 具体错误信息",
  "data": null
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
  "code": "SUCCESS",
  "message": "操作成功",
  "data": "Collide Auth v2.0 - 简洁版认证服务运行正常"
}
```

---

## 错误码说明

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| SUCCESS | 200 | 操作成功 |
| INVALID_PARAMETER | 400 | 参数错误（如：用户名格式不正确、密码长度不符合要求等） |
| USER_REGISTER_ERROR | 500 | 注册失败（系统错误） |
| USER_LOGIN_ERROR | 500 | 登录失败（用户名或密码错误，或系统错误） |
| LOGIN_OR_REGISTER_ERROR | 500 | 登录或注册操作失败 |
| LOGOUT_ERROR | 500 | 登出失败 |
| TOKEN_INVALID | 500 | Token验证失败 |

---

## 🔧 技术说明

### 架构设计
- **分层架构**: Controller-Service-Dubbo三层架构
- **职责分离**: Controller处理HTTP请求和参数验证，Service处理业务逻辑
- **服务调用**: 通过Dubbo调用用户服务，保持微服务解耦
- **模块专注**: 认证模块专注认证功能，用户管理功能由用户模块负责

### 认证机制
- **认证框架**: Sa-Token
- **Token类型**: 无状态Token，默认有效期7天
- **Session管理**: 基于Sa-Token的Session机制
- **权限存储**: Session中存储用户基础信息供网关鉴权使用
- **自动登录**: 注册成功后自动生成Token，优化用户体验

### 参数验证
- **验证框架**: Jakarta Bean Validation (JSR-303)
- **自动验证**: 使用@Valid注解自动触发参数验证
- **验证规则**: 
  - 用户名：3-50字符，仅支持字母、数字、下划线
  - 密码：注册时6-20字符，登录时最大100字符（支持多种密码类型）
  - 邀请码：可选，最大20字符

### 安全性
- **密码安全**: 密码通过用户服务使用BCrypt加密存储
- **错误处理**: 不暴露系统内部异常信息，返回用户友好的错误消息
- **参数过滤**: 严格的参数验证防止恶意输入
- **状态验证**: 登录时验证用户状态，防止被禁用用户登录

### 依赖服务
- **UserFacadeService**: 用户核心操作（创建、验证、查询）
- **UserProfileFacadeService**: 用户资料管理（注册时创建默认资料）
- **UserRoleFacadeService**: 用户角色管理（注册时分配默认角色，登录时获取角色）

---

## 🚀 使用示例

### 前端集成示例

```javascript
// 1. 用户注册
const registerUser = async (username, password, inviteCode) => {
  try {
    const response = await fetch('/api/v1/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, inviteCode })
    });
    
    const result = await response.json();
    if (result.success && result.data?.token) {
      // 注册成功并自动登录
      localStorage.setItem('token', result.data.token);
      return { success: true, message: result.message };
    } else if (result.success) {
      // 注册成功但需要手动登录
      return { success: true, message: result.message, needLogin: true };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 2. 用户登录
const loginUser = async (username, password) => {
  try {
    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    
    const result = await response.json();
    if (result.success) {
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
      return { success: true, user: result.data.user };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 3. 登录或注册（推荐使用）
const loginOrRegister = async (username, password, inviteCode) => {
  try {
    const response = await fetch('/api/v1/auth/login-or-register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, inviteCode })
    });
    
    const result = await response.json();
    if (result.success) {
      localStorage.setItem('token', result.data.token);
      localStorage.setItem('user', JSON.stringify(result.data.user));
      return { 
        success: true, 
        user: result.data.user, 
        isNewUser: result.data.isNewUser,
        message: result.data.message
      };
    } else {
      return { success: false, message: result.message };
    }
  } catch (error) {
    return { success: false, message: '网络错误，请稍后重试' };
  }
};

// 4. 用户登出
const logoutUser = async () => {
  const token = localStorage.getItem('token');
  if (!token) return { success: true };
  
  try {
    const response = await fetch('/api/v1/auth/logout', {
      method: 'POST',
      headers: { 'satoken': token }
    });
    
    const result = await response.json();
    // 无论成功失败都清理本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    return { success: true, message: result.message || '登出成功' };
  } catch (error) {
    // 网络错误也清理本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    return { success: true, message: '登出成功' };
  }
};

// 5. 验证Token
const verifyToken = async () => {
  const token = localStorage.getItem('token');
  if (!token) return { valid: false };
  
  try {
    const response = await fetch('/api/v1/auth/verify-token', {
      headers: { 'satoken': token }
    });
    
    const result = await response.json();
    if (result.success) {
      return { valid: true, userId: result.data.userId };
    } else {
      // Token无效，清理本地存储
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      return { valid: false };
    }
  } catch (error) {
    return { valid: false };
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

// 响应拦截器：处理Token失效
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Token失效，清理本地存储并跳转登录
      localStorage.removeItem('token');
      localStorage.removeItem('user');
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
# 1. 用户注册
curl -X POST "http://localhost:8080/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "inviteCode": "INVITE123"
  }'

# 2. 用户登录
curl -X POST "http://localhost:8080/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }'

# 3. 登录或注册
curl -X POST "http://localhost:8080/api/v1/auth/login-or-register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "newpass123",
    "inviteCode": "INVITE456"
  }'

# 4. 用户登出（需要Token）
curl -X POST "http://localhost:8080/api/v1/auth/logout" \
  -H "satoken: your_token_here"

# 5. 验证Token（需要Token）
curl -X GET "http://localhost:8080/api/v1/auth/verify-token" \
  -H "satoken: your_token_here"

# 6. 健康检查
curl -X GET "http://localhost:8080/api/v1/auth/test"
```

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (重构优化版)  
**架构说明**: 采用Controller-Service-Dubbo三层架构，专注核心认证功能，模块职责边界清晰