# Collide 钱包服务 API 文档

## 概述

Collide 钱包服务提供完整的用户钱包管理功能，包括余额查询、充值提现、资金冻结解冻、余额校验等核心功能。支持内部服务间的资金操作调用。

**服务版本**: v2.0  
**基础路径**: `/api/v1/users/{userId}/wallet`  
**内部服务**: Dubbo RPC 调用  
**设计理念**: 安全可靠的资金管理，支持事务性操作，提供完整的钱包生命周期管理

---

## 钱包基础功能 API

### 1. 获取用户钱包信息
**接口路径**: `GET /api/v1/users/{userId}/wallet`  
**接口描述**: 获取用户钱包详细信息，包括余额、冻结金额、收支统计等

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": null,
  "responseMessage": null,
  "id": 1,
  "userId": 12345,
  "balance": 1250.50,
  "frozenAmount": 100.00,
  "availableBalance": 1150.50,
  "totalIncome": 5000.00,
  "totalExpense": 3749.50,
  "status": "active",
  "createTime": "2024-01-16T10:30:00",
  "updateTime": "2024-01-20T15:45:00"
}
```

**失败响应 (404)**:
```json
{
  "success": false,
  "responseCode": "WALLET_NOT_FOUND",
  "responseMessage": "用户钱包不存在，系统将自动创建"
}
```

---

### 2. 钱包操作（统一接口）
**接口路径**: `POST /api/v1/users/{userId}/wallet/operation`  
**接口描述**: 执行钱包操作，支持充值、提现、冻结、解冻等操作

#### 请求参数
```json
{
  "amount": 100.00,                    // 必填，操作金额（最小0.01）
  "operationType": "recharge",         // 必填，操作类型：recharge/withdraw/freeze/unfreeze
  "description": "账户充值",           // 可选，操作描述
  "businessId": "ORDER_20240116001"    // 可选，业务关联ID
}
```

#### 操作类型说明
- **recharge**: 充值 - 增加用户钱包余额
- **withdraw**: 提现 - 减少用户钱包余额  
- **freeze**: 冻结 - 将可用余额转为冻结金额
- **unfreeze**: 解冻 - 将冻结金额转为可用余额

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": null,
  "responseMessage": null,
  "id": 1,
  "userId": 12345,
  "balance": 1350.50,
  "frozenAmount": 100.00,
  "availableBalance": 1250.50,
  "totalIncome": 5100.00,
  "totalExpense": 3749.50,
  "status": "active",
  "createTime": "2024-01-16T10:30:00",
  "updateTime": "2024-01-20T16:00:00"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "INSUFFICIENT_BALANCE",
  "responseMessage": "余额不足"
}
```

---

### 3. 检查余额是否充足
**接口路径**: `GET /api/v1/users/{userId}/wallet/balance/check`  
**接口描述**: 检查用户可用余额是否满足指定金额要求

#### 请求参数
- **userId** (path): 用户ID，必填
- **amount** (query): 检查金额，必填

#### 响应示例
**成功响应 (200)**:
```json
true  // 余额充足
```

```json
false // 余额不足
```

---

## 钱包管理 API

### 4. 钱包充值
**接口路径**: `POST /api/v1/users/{userId}/wallet/recharge`  
**接口描述**: 用户钱包充值操作

#### 请求参数
- **userId** (path): 用户ID，必填
- **amount** (query): 充值金额，必填
- **description** (query): 充值描述，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": null,
  "responseMessage": null,
  "id": 1,
  "userId": 12345,
  "balance": 1350.50,
  "frozenAmount": 100.00,
  "availableBalance": 1250.50,
  "totalIncome": 5100.00,
  "totalExpense": 3749.50,
  "status": "active",
  "createTime": "2024-01-16T10:30:00",
  "updateTime": "2024-01-20T16:00:00"
}
```

---

### 5. 钱包提现
**接口路径**: `POST /api/v1/users/{userId}/wallet/withdraw`  
**接口描述**: 用户钱包提现操作

#### 请求参数
- **userId** (path): 用户ID，必填
- **amount** (query): 提现金额，必填
- **description** (query): 提现描述，可选

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": null,
  "responseMessage": null,
  "id": 1,
  "userId": 12345,
  "balance": 1250.50,
  "frozenAmount": 100.00,
  "availableBalance": 1150.50,
  "totalIncome": 5000.00,
  "totalExpense": 3849.50,
  "status": "active",
  "createTime": "2024-01-16T10:30:00",
  "updateTime": "2024-01-20T16:10:00"
}
```

**失败响应 (400)**:
```json
{
  "success": false,
  "responseCode": "INSUFFICIENT_BALANCE",
  "responseMessage": "可用余额不足"
}
```

---

## 内部服务接口（Dubbo RPC）

### 6. 钱包扣款（内部接口）
**接口描述**: 供其他微服务调用，执行钱包扣款操作
**调用方式**: `userFacadeService.deductWalletBalance()`

#### 方法签名
```java
Result<Void> deductWalletBalance(Long userId, BigDecimal amount, String businessId, String description)
```

#### 参数说明
- **userId**: 用户ID
- **amount**: 扣款金额
- **businessId**: 业务ID（如订单号）
- **description**: 操作描述

#### 响应示例
```java
// 成功
Result.success(null)

// 失败
Result.error("WALLET_DEDUCT_ERROR", "扣款失败：余额不足或钱包状态异常")
```

---

### 7. 钱包充值（内部接口）
**接口描述**: 供其他微服务调用，执行钱包充值操作
**调用方式**: `userFacadeService.addWalletBalance()`

#### 方法签名
```java
Result<Void> addWalletBalance(Long userId, BigDecimal amount, String businessId, String description)
```

#### 参数说明
- **userId**: 用户ID
- **amount**: 充值金额
- **businessId**: 业务ID（如订单号）
- **description**: 操作描述

#### 响应示例
```java
// 成功
Result.success(null)

// 失败
Result.error("WALLET_ADD_ERROR", "充值失败：钱包状态异常")
```

---

### 8. 检查钱包余额（内部接口）
**接口描述**: 供其他微服务调用，检查用户余额是否充足
**调用方式**: `userFacadeService.checkWalletBalance()`

#### 方法签名
```java
Result<Boolean> checkWalletBalance(Long userId, BigDecimal amount)
```

#### 响应示例
```java
// 余额充足
Result.success(true)

// 余额不足
Result.success(false)
```

---

## 错误码定义

| 错误码 | 错误描述 | 解决方案 |
|--------|----------|----------|
| WALLET_NOT_FOUND | 钱包不存在 | 系统自动创建用户钱包 |
| WALLET_CREATE_ERROR | 创建钱包失败 | 检查用户ID是否有效 |
| WALLET_GET_ERROR | 获取钱包失败 | 检查用户ID和网络连接 |
| WALLET_OPERATION_ERROR | 钱包操作失败 | 检查操作类型和参数 |
| WALLET_CHECK_ERROR | 检查余额失败 | 检查用户ID和金额参数 |
| WALLET_DEDUCT_ERROR | 扣款失败 | 检查余额是否充足 |
| WALLET_ADD_ERROR | 充值失败 | 检查钱包状态和金额 |
| INSUFFICIENT_BALANCE | 余额不足 | 用户需要先充值 |
| INVALID_OPERATION | 无效操作类型 | 检查operationType参数 |
| INVALID_AMOUNT | 无效金额 | 金额必须大于0.01 |
| WALLET_FROZEN | 钱包已冻结 | 联系客服解冻钱包 |

---

## 业务流程示例

### 用户充值流程
```javascript
// 1. 检查钱包是否存在
const walletResponse = await fetch('/api/v1/users/12345/wallet');

// 2. 执行充值操作
const rechargeResponse = await fetch('/api/v1/users/12345/wallet/recharge', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded'
  },
  body: 'amount=100.00&description=支付宝充值'
});

console.log('充值成功:', rechargeResponse);
```

### 订单支付流程（内部服务调用）
```java
// 1. 检查余额是否充足
Result<Boolean> checkResult = userFacadeService.checkWalletBalance(userId, orderAmount);
if (!checkResult.getData()) {
    return Result.error("INSUFFICIENT_BALANCE", "余额不足，请先充值");
}

// 2. 执行扣款
Result<Void> deductResult = userFacadeService.deductWalletBalance(
    userId, orderAmount, orderNo, "订单支付"
);

if (deductResult.isSuccess()) {
    // 3. 处理订单支付成功逻辑
    processOrderPayment(orderNo);
} else {
    // 4. 处理支付失败
    handlePaymentFailure(orderNo, deductResult.getMessage());
}
```

### 退款流程（内部服务调用）
```java
// 订单退款时，将钱退回用户钱包
Result<Void> refundResult = userFacadeService.addWalletBalance(
    userId, refundAmount, orderNo, "订单退款"
);

if (refundResult.isSuccess()) {
    updateOrderStatus(orderNo, "REFUNDED");
} else {
    // 退款失败处理
    handleRefundFailure(orderNo);
}
```

### 资金冻结流程
```javascript
// 1. 冻结用户资金（如预订商品）
const freezeResponse = await fetch('/api/v1/users/12345/wallet/operation', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    amount: 299.00,
    operationType: 'freeze',
    description: '商品预订冻结资金',
    businessId: 'BOOKING_20240116001'
  })
});

// 2. 确认购买后解冻并扣款
const unfreezeResponse = await fetch('/api/v1/users/12345/wallet/operation', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    amount: 299.00,
    operationType: 'unfreeze',
    description: '取消冻结',
    businessId: 'BOOKING_20240116001'
  })
});
```

---

## 安全性说明

### 1. 数据安全
- 所有金额操作使用 `BigDecimal` 类型，确保精度
- 数据库层面使用原子操作，防止并发问题
- 所有操作都有事务保护

### 2. 权限控制
- 用户只能操作自己的钱包
- 内部服务接口通过 Dubbo RPC 调用，不对外暴露
- 敏感操作需要额外的身份验证

### 3. 操作日志
- 所有钱包操作都有详细日志记录
- 支持操作追踪和审计
- 异常情况自动告警

### 4. 风控机制
- 大额操作需要额外验证
- 异常操作自动风控
- 支持钱包冻结和解冻

---

## 技术实现

### 1. 数据库设计
```sql
CREATE TABLE `t_user_wallet` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '钱包ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `balance` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '余额',
    `frozen_amount` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
    `total_income` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '总收入',
    `total_expense` DECIMAL(15,2) NOT NULL DEFAULT 0.00 COMMENT '总支出',
    `status` VARCHAR(20) NOT NULL DEFAULT 'active' COMMENT '状态',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户钱包表';
```

### 2. 核心特性
- 支持微服务架构，通过 Dubbo 提供服务
- 使用 MyBatis Plus 进行数据访问
- 完整的异常处理和错误码机制
- 支持 Spring Boot 自动配置

### 3. 性能优化
- 数据库连接池优化
- 缓存热点数据
- 异步操作日志记录
- 批量操作支持

---

**注意**: 本文档基于 Collide v2.0 微服务架构设计，钱包功能集成在用户服务中，通过统一的API网关对外提供服务。