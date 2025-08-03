# Collide 用户服务 API 文档

## 概述

Collide 用户服务提供完整的用户管理功能，包括用户注册、信息查询、个人资料管理、博主认证、钱包系统等核心功能。支持管理员级别的用户管理操作。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/users`  
**管理路径**: `/api/admin/users`  
**Dubbo服务**: `collide-users`  
**设计理念**: 统一用户管理，支持多种查询方式，提供完整的用户生命周期管理

## 🚀 性能特性

- **🔥 缓存优化**: 集成JetCache双级缓存，用户信息命中率95%+
- **⚡ 响应时间**: 平均响应时间 < 30ms  
- **💰 钱包集成**: 完整的钱包系统，支持充值/提现/冻结等操作
- **🔒 数据一致性**: 缓存自动失效，保证数据实时性
- **📊 实时增强**: 跨模块数据聚合，提供完整用户画像

## 📝 API 设计原则

- **创建/删除操作**: 只返回成功/失败状态，不返回具体数据
- **更新操作**: 返回更新后的完整数据  
- **查询操作**: 多级缓存加速，支持复杂查询条件
- **个人信息增强**: getUserProfile接口提供包含钱包信息的完整用户数据

---

## 用户基础功能 API

### 1. 获取个人用户信息 (🆕缓存增强)
**接口路径**: `GET /api/v1/users/{id}/profile`  
**接口描述**: 获取用户完整个人信息，包含基础信息、统计数据和钱包信息

#### 请求参数
- **Path参数**:
  - `id` (Long, 必填): 用户ID

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "获取成功",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "138****8000",
    "avatar": "https://avatar.example.com/user.jpg",
    "status": "active",
    "role": "USER",
    "bio": "这是我的个人简介",
    "birthday": "1990-01-01",
    "gender": "male",
    "location": "北京",
    "followerCount": 1250,
    "followingCount": 380,
    "contentCount": 45,
    "likeCount": 8900,
    "vipExpireTime": "2024-12-31T23:59:59",
    "lastLoginTime": "2024-01-15T10:30:00",
    "loginCount": 156,
    "inviteCode": "ABC123",
    "inviterId": 67890,
    "invitedCount": 12,
    "walletBalance": "1258.50",
    "walletFrozen": "100.00",
    "walletStatus": "active",
    "createTime": "2023-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

**错误响应 (400)**:
```json
{
  "success": false,
  "responseCode": "USER_NOT_FOUND",
  "responseMessage": "用户不存在",
  "data": null
}
```

#### 性能特性
- **缓存策略**: 用户基础信息缓存120分钟，钱包信息缓存60分钟
- **实时增强**: 动态获取最新钱包余额和状态
- **容错处理**: 钱包信息获取失败不影响基础用户信息返回

---

### 2. 查询用户详细信息
**接口路径**: `POST /api/users/query`  
**接口描述**: 根据查询条件获取用户详细信息

#### 请求参数
```json
{
  "userQueryCondition": {
    "userId": 12345,        // 用户ID查询
    "username": "testuser", // 用户名查询
    "email": "test@example.com", // 邮箱查询
    "phone": "13800138000", // 手机号查询
    "inviteCode": "ABC123"  // 邀请码查询
  }
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "测试用户",
    "email": "test@example.com",
    "phone": "138****8000",
    "avatar": "https://avatar.example.com/user.jpg",
    "status": "ACTIVE",
    "role": "USER",
    "bloggerStatus": "approved",
    "inviteCode": "DEF456",
    "inviterId": 67890,
    "bio": "这是我的个人简介",
    "birthday": "1990-01-01",
    "gender": "male",
    "location": "北京",
    "vipLevel": 1,
    "points": 1000,
    "createTime": "2024-01-16T10:30:00",
    "lastLoginTime": "2024-01-16T15:45:00"
  }
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "USER_NOT_FOUND",
  "responseMessage": "用户不存在"
}
```

---

### 2. 查询用户基础信息
**接口路径**: `POST /api/users/query/basic`  
**接口描述**: 获取用户基础信息，不包含敏感数据（如邮箱、手机号等）

#### 请求参数
与查询用户详细信息相同

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 12345,
    "nickname": "测试用户",
    "avatar": "https://avatar.example.com/user.jpg",
    "bio": "这是我的个人简介",
    "location": "北京",
    "createTime": "2024-01-16T10:30:00"
  }
}
```

---

### 3. 分页查询用户
**接口路径**: `POST /api/users/page`  
**接口描述**: 分页获取用户列表，支持多种筛选条件

#### 请求参数
```json
{
  "currentPage": 1,           // 页码（从1开始）
  "pageSize": 20,         // 每页大小（默认10，最大100）
  "userQueryCondition": {
    "status": "ACTIVE",   // 用户状态筛选
    "role": "BLOGGER",    // 角色筛选
    "vipLevel": 1,        // VIP等级筛选
    "keyword": "test"     // 关键字搜索（用户名、昵称）
  }
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "totalCount": 1500,
  "currentPage": 1,
  "pageSize": 20,
  "pages": 75,
  "data": [
    {
      "id": 12345,
      "username": "testuser",
      "nickname": "测试用户",
      "avatar": "https://avatar.example.com/user.jpg",
      "status": "ACTIVE",
      "role": "BLOGGER",
      "vipLevel": 1,
      "createTime": "2024-01-16T10:30:00"
    }
  ]
}
```

---

### 4. 用户注册 (🔄API更新)
**接口路径**: `POST /api/v1/users`  
**接口描述**: 新用户注册，支持邀请码机制，现在只返回成功状态

#### 请求参数
```json
{
  "username": "newuser",         // 必填，用户名（3-20字符，仅字母数字下划线）
  "nickname": "新用户",          // 可选，昵称（为空时使用用户名）
  "phone": "13800138000",        // 可选，手机号
  "email": "new@example.com",    // 可选，邮箱
  "password": "123456",          // 必填，密码（6-20字符）
  "inviteCode": "ABC123",        // 可选，邀请码
  "inviterId": 67890,           // 可选，邀请人ID
  "avatar": "https://avatar.example.com/new.jpg", // 可选，头像URL
  "bio": "我是新用户",           // 可选，个人简介
  "gender": "male",             // 可选，性别
  "location": "上海"            // 可选，所在地
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "注册成功",
  "data": null
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "USERNAME_ALREADY_EXISTS",
  "responseMessage": "用户名已存在"
}
```

---

### 5. 用户信息修改 (🔄API更新)
**接口路径**: `PUT /api/v1/users/{userId}`  
**接口描述**: 修改用户个人信息，返回更新后的完整数据  
**权限要求**: 需要登录

#### 请求参数
- **Path参数**: `userId` (Long, 必填) - 用户ID
- **Body参数**:
```json
{
  "nickname": "新昵称",          // 可选，昵称
  "email": "newemail@example.com", // 可选，邮箱
  "phone": "13900139000",        // 可选，手机号
  "avatar": "https://avatar.example.com/new.jpg", // 可选，头像
  "bio": "更新后的个人简介",     // 可选，个人简介
  "birthday": "1990-01-01",      // 可选，生日
  "gender": "female",           // 可选，性别
  "location": "深圳"            // 可选，所在地
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "修改成功",
  "data": {
    "id": 12345,
    "username": "testuser",
    "nickname": "新昵称",
    "email": "newemail@example.com",
    "phone": "13900139000",
    "avatar": "https://avatar.example.com/new.jpg",
    "status": "active",
    "role": "USER",
    "bio": "更新后的个人简介",
    "birthday": "1990-01-01",
    "gender": "female",
    "location": "深圳",
    "followerCount": 1250,
    "followingCount": 380,
    "contentCount": 45,
    "likeCount": 8900,
    "updateTime": "2024-01-16T12:00:00"
  }
}
```

#### 性能特性
- **缓存更新**: 自动更新用户详情缓存，失效相关列表缓存
- **实时性**: 更新后立即生效，缓存命中返回最新数据

---

### 6. 用户激活
**接口路径**: `POST /api/users/activate`  
**接口描述**: 激活用户账户

#### 请求参数
```json
{
  "userId": 12345,             // 必填，用户ID
  "activationCode": "ACT123"   // 必填，激活码
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "激活成功"
}
```

---

### 7. 申请博主认证
**接口路径**: `POST /api/users/apply-blogger`  
**接口描述**: 用户申请成为认证博主  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,             // 必填，用户ID
  "applyReason": "我有丰富的写作经验，希望分享技术文章", // 必填，申请理由
  "attachments": [             // 可选，附件材料
    "https://example.com/cert1.jpg",
    "https://example.com/cert2.jpg"
  ]
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "申请已提交，等待审核"
}
```

---

### 8. 获取用户博主信息
**接口路径**: `GET /api/users/{userId}/blogger-info`  
**接口描述**: 获取用户详细信息，包含博主认证相关信息

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "查询成功",
  "data": {
    "id": 12345,
    "username": "blogger_user",
    "nickname": "技术博主",
    "bloggerStatus": "approved",
    "role": "BLOGGER",
    "bio": "专注于Java技术分享",
    "articlesCount": 50,
    "followersCount": 1200,
    "createTime": "2024-01-16T10:30:00"
  }
}
```

---

### 9-11. 可用性检查接口

#### 9. 检查用户名是否可用
**接口路径**: `GET /api/users/check/username/{username}`

#### 10. 检查邮箱是否可用
**接口路径**: `GET /api/users/check/email/{email}`

#### 11. 检查手机号是否可用
**接口路径**: `GET /api/users/check/phone/{phone}`

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "message": "用户名可用"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "message": "用户名已存在"
}
```

---

### 12. 生成用户邀请码
**接口路径**: `POST /api/users/{userId}/generate-invite-code`  
**接口描述**: 为用户生成新的邀请码  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "邀请码生成成功: XYZ789"
}
```

---

## 用户管理功能 API（管理员）

### 1. 管理员创建用户
**接口路径**: `POST /api/admin/users/create`  
**接口描述**: 管理员创建新用户  
**权限要求**: 管理员权限

#### 请求参数
与用户注册相同的参数结构

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "用户创建成功",
  "data": {
    "userId": 12347,
    "username": "admin_created_user"
  }
}
```

---

### 2. 博主认证审批
**接口路径**: `POST /api/admin/users/approve-blogger`  
**接口描述**: 管理员审批博主认证申请  
**权限要求**: 管理员权限

#### 请求参数
```json
{
  "userId": 12345,           // 必填，用户ID
  "approved": true,          // 必填，是否通过
  "comment": "审核通过，欢迎成为认证博主" // 可选，审核意见
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "审批完成"
}
```

---

### 3-6. 用户状态管理

#### 3. 冻结用户
**接口路径**: `POST /api/admin/users/{userId}/freeze`

#### 4. 解冻用户
**接口路径**: `POST /api/admin/users/{userId}/unfreeze`

#### 5. 封禁用户
**接口路径**: `POST /api/admin/users/{userId}/ban`

#### 6. 解封用户
**接口路径**: `POST /api/admin/users/{userId}/unban`

#### 请求参数
- **userId** (path): 用户ID，必填
- **reason** (query): 操作原因，必填
- **operatorId** (query): 操作员ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "操作成功"
}
```

---

### 7. 强制激活用户
**接口路径**: `POST /api/admin/users/{userId}/force-activate`  
**接口描述**: 管理员强制激活用户账户

#### 请求参数
- **userId** (path): 用户ID，必填
- **operatorId** (query): 操作员ID，必填

---

### 8. 重置用户密码
**接口路径**: `POST /api/admin/users/{userId}/reset-password`  
**接口描述**: 管理员重置用户密码

#### 请求参数
- **userId** (path): 用户ID，必填
- **newPassword** (query): 新密码，必填
- **operatorId** (query): 操作员ID，必填

---

### 9. 设置用户VIP等级
**接口路径**: `POST /api/admin/users/{userId}/set-vip`  
**接口描述**: 管理员设置用户VIP等级

#### 请求参数
- **userId** (path): 用户ID，必填
- **vipLevel** (query): VIP等级，必填
- **operatorId** (query): 操作员ID，必填

---

### 10. 撤销博主认证
**接口路径**: `POST /api/admin/users/{userId}/revoke-blogger`  
**接口描述**: 管理员撤销用户的博主认证

#### 请求参数
- **userId** (path): 用户ID，必填
- **reason** (query): 撤销原因，必填
- **operatorId** (query): 操作员ID，必填

---

### 11. 批量操作用户状态
**接口路径**: `POST /api/admin/users/batch-operate`  
**接口描述**: 批量修改用户状态

#### 请求参数
- **userIds** (query): 用户ID数组，必填
- **operation** (query): 操作类型（freeze/unfreeze/ban/unban），必填
- **reason** (query): 操作原因，必填
- **operatorId** (query): 操作员ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "批量操作完成，成功处理5个用户"
}
```

---

## 💰 钱包系统 API

### 1. 获取用户钱包信息
**接口路径**: `GET /api/v1/wallet/{userId}`  
**接口描述**: 获取用户钱包详细信息，如余额、冻结金额、状态等  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "获取成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "balance": "1258.50",
    "frozenAmount": "100.00",
    "totalAmount": "1358.50",
    "status": "active",
    "createTime": "2023-01-01T00:00:00",
    "updateTime": "2024-01-15T10:30:00"
  }
}
```

#### 性能特性
- **缓存策略**: 钱包详情缓存60分钟
- **自动创建**: 用户首次查询时自动创建钱包

---

### 2. 钱包充值
**接口路径**: `POST /api/v1/wallet/recharge`  
**接口描述**: 用户钱包充值操作  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "amount": "100.00",
  "description": "在线充值"
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "充值成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "balance": "1358.50",
    "frozenAmount": "100.00",
    "totalAmount": "1458.50",
    "status": "active",
    "updateTime": "2024-01-15T11:00:00"
  }
}
```

---

### 3. 钱包提现
**接口路径**: `POST /api/v1/wallet/withdraw`  
**接口描述**: 用户钱包提现操作  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "amount": "50.00",
  "description": "提现到银行卡"
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS", 
  "responseMessage": "提现成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "balance": "1308.50",
    "frozenAmount": "100.00",
    "totalAmount": "1408.50",
    "status": "active",
    "updateTime": "2024-01-15T11:15:00"
  }
}
```

**错误响应 (400)**:
```json
{
  "success": false,
  "responseCode": "INSUFFICIENT_BALANCE",
  "responseMessage": "余额不足",
  "data": null
}
```

---

### 4. 冻结金额
**接口路径**: `POST /api/v1/wallet/freeze`  
**接口描述**: 冻结用户钱包中的指定金额  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "amount": "50.00",
  "description": "订单冻结"
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "冻结成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "balance": "1258.50",
    "frozenAmount": "150.00",
    "totalAmount": "1408.50",
    "status": "active",
    "updateTime": "2024-01-15T11:30:00"
  }
}
```

---

### 5. 解冻金额
**接口路径**: `POST /api/v1/wallet/unfreeze`  
**接口描述**: 解冻用户钱包中的指定金额  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "amount": "50.00",
  "description": "订单取消解冻"
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "解冻成功",
  "data": {
    "id": 1001,
    "userId": 12345,
    "balance": "1308.50",
    "frozenAmount": "100.00",
    "totalAmount": "1408.50",
    "status": "active",
    "updateTime": "2024-01-15T11:45:00"
  }
}
```

---

### 6. 检查余额充足性
**接口路径**: `GET /api/v1/wallet/{userId}/check-balance`  
**接口描述**: 检查用户钱包余额是否充足  
**权限要求**: 需要登录

#### 请求参数
- **userId** (path): 用户ID，必填
- **amount** (query): 检查金额，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "余额充足",
  "data": true
}
```

**余额不足响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "余额不足",
  "data": false
}
```

---

### 7. 钱包操作统一接口
**接口路径**: `POST /api/v1/wallet/operation`  
**接口描述**: 统一的钱包操作接口，支持充值、提现、冻结、解冻  
**权限要求**: 需要登录

#### 请求参数
```json
{
  "userId": 12345,
  "operationType": "recharge",
  "amount": "100.00",
  "description": "统一操作接口充值"
}
```

#### 操作类型说明
- `recharge`: 充值
- `withdraw`: 提现
- `freeze`: 冻结
- `unfreeze`: 解冻

#### 响应示例
与对应的单独操作接口响应格式相同

---

### 钱包错误码说明

| 错误码 | 说明 |
|--------|------|
| `WALLET_NOT_FOUND` | 钱包不存在 |
| `INSUFFICIENT_BALANCE` | 余额不足 |
| `INSUFFICIENT_FROZEN_AMOUNT` | 冻结金额不足 |
| `INVALID_AMOUNT` | 无效金额 |
| `WALLET_FROZEN` | 钱包已冻结 |
| `OPERATION_NOT_SUPPORTED` | 不支持的操作类型 |
| `WALLET_CREATE_ERROR` | 钱包创建失败 |
| `WALLET_OPERATION_ERROR` | 钱包操作失败 |

---

### 钱包状态说明

| 状态 | 说明 |
|------|------|
| `active` | 正常状态 |
| `frozen` | 冻结状态 |
| `closed` | 关闭状态 |

---

### 钱包使用示例

#### 完整充值流程
```javascript
// 1. 检查钱包余额
const balanceCheck = await fetch('/api/v1/wallet/12345/check-balance?amount=100.00');

// 2. 执行充值
const rechargeResponse = await fetch('/api/v1/wallet/recharge', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': 'Bearer user_token'
  },
  body: JSON.stringify({
    userId: 12345,
    amount: '100.00',
    description: '充值操作'
  })
});

// 3. 获取最新钱包信息
const walletInfo = await fetch('/api/v1/wallet/12345');
```

#### 订单支付流程
```javascript
// 1. 冻结订单金额
const freezeResponse = await fetch('/api/v1/wallet/freeze', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 12345,
    amount: '50.00',
    description: '订单支付冻结'
  })
});

// 2. 订单完成后扣除冻结金额（实际支付）
const withdrawResponse = await fetch('/api/v1/wallet/withdraw', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 12345,
    amount: '50.00',
    description: '订单支付'
  })
});
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| `SUCCESS` | 操作成功 |
| `USER_NOT_FOUND` | 用户不存在 |
| `USERNAME_ALREADY_EXISTS` | 用户名已存在 |
| `EMAIL_ALREADY_EXISTS` | 邮箱已存在 |
| `PHONE_ALREADY_EXISTS` | 手机号已存在 |
| `INVALID_USER_STATUS` | 用户状态不可用 |
| `BLOGGER_APPLY_ERROR` | 博主申请失败 |
| `PERMISSION_DENIED` | 权限不足 |
| `VALIDATION_ERROR` | 参数验证失败 |
| `SYSTEM_ERROR` | 系统异常 |
| `WALLET_NOT_FOUND` | 钱包不存在 |
| `INSUFFICIENT_BALANCE` | 余额不足 |
| `INSUFFICIENT_FROZEN_AMOUNT` | 冻结金额不足 |
| `INVALID_AMOUNT` | 无效金额 |
| `WALLET_FROZEN` | 钱包已冻结 |
| `OPERATION_NOT_SUPPORTED` | 不支持的操作类型 |
| `WALLET_CREATE_ERROR` | 钱包创建失败 |
| `WALLET_OPERATION_ERROR` | 钱包操作失败 |

---

## 数据结构说明

### 用户状态枚举
- `INACTIVE`: 未激活
- `ACTIVE`: 已激活
- `FROZEN`: 已冻结
- `BANNED`: 已封禁

### 用户角色枚举
- `USER`: 普通用户
- `BLOGGER`: 认证博主
- `ADMIN`: 管理员

### 博主认证状态
- `pending`: 待审核
- `approved`: 已通过
- `rejected`: 已拒绝

### 钱包状态枚举
- `active`: 正常状态，可以进行所有操作
- `frozen`: 冻结状态，禁止提现和转账
- `closed`: 关闭状态，禁止所有操作

### 钱包操作类型
- `recharge`: 充值操作
- `withdraw`: 提现操作
- `freeze`: 冻结金额操作
- `unfreeze`: 解冻金额操作

---

## 使用示例

### 获取用户完整信息流程 (🆕缓存增强)
```javascript
// 1. 获取用户完整个人信息（包含钱包信息）
const userProfileResponse = await fetch('/api/v1/users/12345/profile', {
  headers: { 'Authorization': 'Bearer user_token' }
});

// 响应包含完整用户数据和钱包信息
const userProfile = await userProfileResponse.json();
console.log('用户余额:', userProfile.data.walletBalance);
console.log('冻结金额:', userProfile.data.walletFrozen);
```

### 用户注册和查询流程
```javascript
// 1. 检查用户名是否可用
const checkResult = await fetch('/api/users/check/username/newuser');

// 2. 用户注册（现在返回成功状态）
const registerResponse = await fetch('/api/v1/users', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'newuser',
    password: '123456',
    nickname: '新用户',
    email: 'new@example.com'
  })
});

// 响应: {"success": true, "code": "SUCCESS", "data": null}

// 3. 查询用户基础信息
const userResponse = await fetch('/api/v1/users/12346', {
  headers: { 'Authorization': 'Bearer user_token' }
});

// 4. 获取用户完整信息（包含钱包）
const profileResponse = await fetch('/api/v1/users/12346/profile', {
  headers: { 'Authorization': 'Bearer user_token' }
});
```

### 钱包完整操作流程
```javascript
// 1. 获取钱包信息
const walletResponse = await fetch('/api/v1/wallet/12345', {
  headers: { 'Authorization': 'Bearer user_token' }
});

// 2. 检查余额充足性
const balanceCheck = await fetch('/api/v1/wallet/12345/check-balance?amount=100.00');

// 3. 充值操作
const rechargeResponse = await fetch('/api/v1/wallet/recharge', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json',
    'Authorization': 'Bearer user_token'
  },
  body: JSON.stringify({
    userId: 12345,
    amount: '100.00',
    description: '在线充值'
  })
});

// 4. 使用统一操作接口
const operationResponse = await fetch('/api/v1/wallet/operation', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    userId: 12345,
    operationType: 'withdraw',
    amount: '50.00',
    description: '提现操作'
  })
});
```

### 管理员操作示例
```javascript
// 管理员冻结用户
const freezeResponse = await fetch('/api/admin/users/12345/freeze?reason=违规行为&operatorId=1', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer admin_token' }
});

// 批量操作用户
const batchResponse = await fetch('/api/admin/users/batch-operate', {
  method: 'POST',
  headers: { 'Authorization': 'Bearer admin_token' },
  body: new URLSearchParams({
    userIds: [12345, 12346, 12347],
    operation: 'freeze',
    reason: '批量处理',
    operatorId: 1
  })
});
```

---

## 📊 缓存性能指标

### 缓存策略总览
- **用户详情**: 120分钟缓存，双级缓存（本地+Redis）
- **用户列表**: 30分钟缓存，支持分页查询
- **用户名查询**: 60分钟缓存，登录场景优化
- **钱包详情**: 60分钟缓存，金融数据安全
- **钱包余额**: 30分钟缓存，实时性平衡

### 预期性能提升
- **响应时间**: 缓存命中 < 10ms，平均响应 < 30ms
- **命中率**: 预期达到95%+
- **并发支持**: 单实例支持10K+ QPS
- **数据一致性**: 智能缓存失效，保证99.9%一致性

---

**文档版本**: v2.0.0 (缓存增强版)  
**最后更新**: 2024-01-16  
**维护团队**: Collide Team  
**技术栈**: Spring Boot 3.x + JetCache + Dubbo + MySQL 