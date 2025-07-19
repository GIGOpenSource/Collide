# Collide 认证服务接口文档

## 目录

- [服务介绍](#服务介绍)
- [统一响应格式](#统一响应格式)
- [接口列表](#接口列表)
  - [发送验证码](#发送验证码)
  - [手机号注册](#手机号注册)
  - [用户名注册](#用户名注册)
  - [手机号登录](#手机号登录)
  - [用户名登录](#用户名登录)
  - [用户登出](#用户登出)
  - [获取Token](#获取token)
- [错误码说明](#错误码说明)
- [数据字典](#数据字典)
- [注意事项](#注意事项)

---

## 服务介绍

**服务名称：** collide-auth  
**服务端口：** 8082  
**网关访问：** http://localhost:8081  
**服务描述：** 负责用户认证、注册、登录、Token管理等功能

---

## 统一响应格式

```json
{
    "code": "string",      // 响应码
    "success": boolean,    // 是否成功
    "message": "string",   // 响应消息
    "data": object        // 响应数据
}
```

---

## 接口列表

### 发送验证码

**接口地址：** `GET /auth/sendCaptcha`

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 | 验证规则 |
|-------|------|------|------|------|----------|
| telephone | string | 是 | 手机号 | 13800000001 | 11位手机号格式 |

**请求示例：**
```http
GET /auth/sendCaptcha?telephone=13800000001
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**说明：**
- 验证码有效期：5分钟
- 同一手机号1分钟内只能发送一次
- 开发环境万能验证码：8888

---

### 手机号注册

**接口地址：** `POST /auth/register`

**请求体：**
```json
{
    "telephone": "13800000001",  // 手机号(必填)
    "captcha": "1234",           // 验证码(必填)
    "inviteCode": "ABC123"       // 邀请码(可选)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 验证规则 |
|-------|------|------|------|----------|
| telephone | string | 是 | 手机号 | 11位手机号格式，使用@IsMobile验证 |
| captcha | string | 是 | 验证码 | 非空，@NotBlank |
| inviteCode | string | 否 | 邀请码 | 可选，6位字符串 |

**请求示例：**
```http
POST /auth/register
Content-Type: application/json

{
    "telephone": "13800000001",
    "captcha": "1234",
    "inviteCode": "ABC123"
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `VERIFICATION_CODE_WRONG`: 验证码错误
- `DUPLICATE_TELEPHONE_NUMBER`: 手机号已注册

**说明：**
- 注册成功后会自动生成昵称（格式：CD_随机字符+手机号后4位）
- 生成6位随机邀请码
- 用户初始状态为INIT
- 默认角色为CUSTOMER

---

### 用户名注册

**接口地址：** `POST /auth/username/register`

**请求体：**
```json
{
    "userName": "testuser",      // 用户名(必填)
    "password": "123456",        // 密码(必填)
    "inviteCode": "ABC123"       // 邀请码(可选)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 验证规则 |
|-------|------|------|------|----------|
| userName | string | 是 | 用户名 | 非空，@NotBlank |
| password | string | 是 | 密码 | 非空，@NotBlank |
| inviteCode | string | 否 | 邀请码 | 可选，6位字符串 |

**请求示例：**
```http
POST /auth/username/register
Content-Type: application/json

{
    "userName": "testuser",
    "password": "123456",
    "inviteCode": "ABC123"
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**可能的错误：**
- `DUPLICATE_USERNAME_NUMBER`: 用户名已存在

**说明：**
- 密码使用MD5加密存储
- 自动生成昵称（格式：CD_随机6位字符）
- 生成6位随机邀请码
- 用户初始状态为INIT

---

### 手机号登录

**接口地址：** `POST /auth/login`

**请求体：**
```json
{
    "telephone": "13800000001",  // 手机号(必填)
    "captcha": "1234",           // 验证码(必填)
    "inviteCode": "ABC123",      // 邀请码(可选)
    "rememberMe": true           // 记住我(可选)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 默认值 |
|-------|------|------|------|-------|
| telephone | string | 是 | 手机号 | - |
| captcha | string | 是 | 验证码 | - |
| inviteCode | string | 否 | 邀请码(首次登录自动注册时使用) | - |
| rememberMe | boolean | 否 | 记住我 | false |

**请求示例：**
```http
POST /auth/login
Content-Type: application/json

{
    "telephone": "13800000001",
    "captcha": "8888",
    "rememberMe": true
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": {
        "userId": "123",                    // 用户ID
        "token": "eyJ0eXAiOiJKV1Q...",     // 访问令牌
        "tokenExpiration": 1640995200      // 令牌过期时间(时间戳)
    }
}
```

**可能的错误：**
- `VERIFICATION_CODE_WRONG`: 验证码错误
- `USER_NOT_EXIST`: 用户不存在（在自动注册失败时）

**说明：**
- 如果用户不存在，会自动注册后登录
- 登录成功后，后续请求需要携带token
- 使用验证码"8888"可以跳过验证码验证（开发环境）
- rememberMe=true时延长token有效期
- 默认token有效期：7天

---

### 用户名登录

**接口地址：** `POST /auth/username/login`

**请求体：**
```json
{
    "userName": "testuser",      // 用户名(必填)
    "password": "123456",        // 密码(必填)
    "inviteCode": "ABC123",      // 邀请码(可选)
    "rememberMe": true           // 记住我(可选)
}
```

**参数说明：**
| 参数名 | 类型 | 必填 | 描述 | 默认值 |
|-------|------|------|------|-------|
| userName | string | 是 | 用户名 | - |
| password | string | 是 | 密码 | - |
| inviteCode | string | 否 | 邀请码(首次登录自动注册时使用) | - |
| rememberMe | boolean | 否 | 记住我 | false |

**请求示例：**
```http
POST /auth/username/login
Content-Type: application/json

{
    "userName": "testuser",
    "password": "123456",
    "rememberMe": true
}
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": {
        "userId": "123",                    // 用户ID
        "token": "eyJ0eXAiOiJKV1Q...",     // 访问令牌
        "tokenExpiration": 1640995200      // 令牌过期时间(时间戳)
    }
}
```

**可能的错误：**
- `DUPLICATE_USERNAME_NUMBER`: 用户名已存在（在自动注册失败时）
- `USER_NOT_EXIST`: 用户不存在（在自动注册失败时）

**说明：**
- 如果用户不存在，会使用提供的用户名和密码自动注册后登录
- 无需验证码验证
- 登录成功后，后续请求需要携带token
- rememberMe=true时延长token有效期
- 默认token有效期：7天
- 密码使用MD5加密验证

---

### 用户登出

**接口地址：** `POST /auth/logout`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求示例：**
```http
POST /auth/logout
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": true
}
```

**说明：**
- 登出后token立即失效
- 清除服务端session信息

---

### 获取Token

**接口地址：** `GET /token/get`

**请求头：**
```http
Authorization: Bearer {token}
```

**请求参数：**
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|-------|------|------|------|------|
| scene | string | 是 | 场景标识 | token:buy:clc |
| key | string | 是 | 密钥 | 12345 |

**请求示例：**
```http
GET /token/get?scene=token:buy:clc&key=12345
Authorization: Bearer eyJ0eXAiOiJKV1Q...
```

**响应示例：**
```json
{
    "code": "SUCCESS",
    "success": true,
    "message": "SUCCESS",
    "data": "YZdkYfQ8fy7biSTsS5oZrbsB8eN7dHPgtCV0dw36AHSfDQzWOj+ULNEcMluHvep"
}
```

**可能的错误：**
- `USER_NOT_LOGIN`: 用户未登录

**说明：**
- 用于获取特定场景的临时token
- token有效期：30分钟
- 支持的场景：
  - `token:buy:clc`: 购买藏品
  - `token:buy:blb`: 购买盲盒

---

## 错误码说明

### 认证相关错误码

| 错误码 | 描述 | HTTP状态码 | 说明 |
|-------|------|-----------|------|
| USER_STATUS_IS_NOT_ACTIVE | 用户状态不可用 | 200 | 用户被冻结或状态异常 |
| VERIFICATION_CODE_WRONG | 验证码错误 | 200 | 验证码不正确或已过期 |
| USER_QUERY_FAILED | 用户信息查询失败 | 200 | 内部查询异常 |
| USER_NOT_LOGIN | 用户未登录 | 401 | 需要先登录 |
| USER_NOT_EXIST | 用户不存在 | 200 | 用户不存在 |
| DUPLICATE_USERNAME_NUMBER | 用户名已存在 | 200 | 用户名重复 |

### 通用错误码

| 错误码 | 描述 | HTTP状态码 |
|-------|------|-----------|
| SYSTEM_ERROR | 系统错误 | 200 |
| SUCCESS | 成功 | 200 |

---

## 数据字典

### Token场景枚举 (TokenSceneEnum)

| 场景值 | 描述 | 用途 |
|-------|------|------|
| token:buy:clc | 购买藏品 | 藏品购买时的防重复提交 |
| token:buy:blb | 购买盲盒 | 盲盒购买时的防重复提交 |

### 登录响应对象 (LoginVO)

| 字段名 | 类型 | 描述 |
|-------|------|------|
| userId | string | 用户ID |
| token | string | 访问令牌 |
| tokenExpiration | long | 令牌过期时间戳 |

---

## 注意事项

### 1. 认证机制
- **Token认证：** 使用Sa-Token框架进行认证
- **Token格式：** `Authorization: Bearer {token}`
- **Token有效期：** 默认7天，rememberMe可延长
- **Session管理：** 服务端维护用户session信息

### 2. 验证码机制
- **发送限制：** 同一手机号1分钟内只能发送一次
- **有效期：** 5分钟
- **存储：** 使用Redis存储，key格式：`captcha:{telephone}`
- **开发环境：** 可使用万能验证码"8888"

### 3. 注册流程
- **手机号注册：** 需要验证码验证
- **用户名注册：** 无需验证码
- **自动生成：** 昵称、邀请码自动生成
- **邀请关系：** 支持邀请码建立邀请关系

### 4. 登录流程
- **手机号登录：** 需要验证码验证，用户不存在时自动注册
- **用户名登录：** 基于用户名+密码验证，用户不存在时自动注册
- **验证码验证：** 仅手机号登录需要，开发环境可使用"8888"跳过
- **Session存储：** 登录成功后将用户信息存入session

### 5. 安全措施
- **密码加密：** 使用MD5加密存储
- **Token防护：** 防止token泄露和重放攻击
- **并发控制：** 注册操作使用分布式锁

### 6. 错误处理
- **业务异常：** 返回具体错误码和错误信息
- **系统异常：** 返回通用错误提示
- **参数验证：** 使用Bean Validation进行参数校验

### 7. 开发调试
- **万能验证码：** "8888"（仅手机号登录）
- **日志输出：** 关键操作有详细日志
- **调试端口：** 直连8082端口进行调试

### 8. 依赖服务
- **Redis：** 验证码存储、Session存储
- **Nacos：** 配置中心
- **用户服务：** 通过Dubbo调用用户相关接口
- **通知服务：** 通过Dubbo调用短信发送接口
