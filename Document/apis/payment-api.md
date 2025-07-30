# Collide 支付服务 API 文档

## 概述

Collide 支付服务提供完整的支付处理功能，包括支付创建、支付确认、退款处理、账单管理等核心功能。支持多种支付方式、支付渠道，提供安全可靠的支付解决方案。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/payments`  
**Dubbo服务**: `collide-payment`  
**设计理念**: 安全可靠的支付系统，支持多渠道支付，提供完整的支付生命周期管理

### 🚀 v2.0.0 新特性
- ⚡ **JetCache分布式缓存** - 大幅提升查询性能，缓存命中率 > 90%
- 🔄 **智能缓存策略** - 支付详情30分钟缓存，用户记录15分钟缓存
- 📊 **性能监控** - 实时记录API响应时间和缓存命中情况
- 🛡️ **风控增强** - 集成分布式锁防重复支付
- 🎯 **代码重构** - 对齐search模块设计风格，代码质量显著提升
- 🧪 **测试支持** - 新增模拟支付接口，方便开发测试

---

## 支付管理 API

### 1. 创建支付
**接口路径**: `POST /api/v1/payments`  
**接口描述**: 创建支付订单

#### 请求参数
```json
{
  "orderId": 987654,                         // 必填，业务订单ID
  "orderNo": "ORD202401160001",              // 必填，业务订单号
  "userId": 12345,                           // 必填，用户ID
  "amount": 237.00,                          // 必填，支付金额
  "currency": "CNY",                         // 可选，货币类型，默认CNY
  "paymentMethod": "alipay",                 // 必填，支付方式：alipay/wechat/bank/balance
  "paymentChannel": "app",                   // 必填，支付渠道：app/web/h5/qr
  "subject": "购买Java编程教程",             // 必填，支付主题
  "description": "订单号：ORD202401160001",   // 可选，支付描述
  "returnUrl": "https://app.example.com/payment/return", // 可选，支付成功返回URL
  "notifyUrl": "https://api.example.com/payment/notify", // 可选，异步通知URL
  "expireTime": 30,                          // 可选，过期时间（分钟），默认30
  "clientIp": "192.168.1.100",               // 可选，客户端IP
  "deviceInfo": {                            // 可选，设备信息
    "deviceId": "device123",
    "deviceType": "mobile",
    "appVersion": "1.0.0"
  },
  "extraData": {                             // 可选，扩展数据
    "merchantId": "merchant123",
    "storeId": "store456"
  }
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "支付创建成功",
  "data": {
    "paymentId": "PAY202401160001",
    "paymentNo": "20240116103000001",
    "orderId": 987654,
    "orderNo": "ORD202401160001",
    "userId": 12345,
    "amount": 237.00,
    "currency": "CNY",
    "paymentMethod": "alipay",
    "paymentChannel": "app",
    "status": "pending",
    "subject": "购买Java编程教程",
    "createTime": "2024-01-16T10:30:00",
    "expireTime": "2024-01-16T11:00:00",
    "paymentInfo": {
      "paymentUrl": "https://openapi.alipay.com/gateway.do?...",
      "qrCodeUrl": "https://qr.alipay.com/bax08888",
      "orderString": "alipay_sdk=alipay-sdk-java-4.10.97.ALL&charset=utf8&...",
      "appId": "2021000000000000",
      "partnerId": "2088000000000000"
    }
  }
}
```

---

### 2. 查询支付状态
**接口路径**: `GET /api/v1/payments/{paymentId}`  
**接口描述**: 查询支付状态

> 💡 **缓存优化**: 支付详情查询已启用分布式缓存，缓存时间30分钟，显著提升查询性能

#### 请求参数
- **paymentId** (path): 支付ID，必填
- **userId** (query): 用户ID，可选（用于权限验证）

#### 响应示例
```json
{
  "success": true,
  "data": {
    "paymentId": "PAY202401160001",
    "paymentNo": "20240116103000001",
    "orderId": 987654,
    "orderNo": "ORD202401160001",
    "status": "success",
    "amount": 237.00,
    "actualAmount": 237.00,
    "paymentMethod": "alipay",
    "paymentChannel": "app",
    "thirdPartyTransactionId": "2024011622001465731234567890",
    "paidTime": "2024-01-16T10:45:00",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:45:00"
  }
}
```

---

### 3. 取消支付
**接口路径**: `POST /api/v1/payments/{paymentId}/cancel`  
**接口描述**: 取消支付订单

#### 请求参数
- **paymentId** (path): 支付ID，必填

```json
{
  "userId": 12345,                          // 必填，用户ID
  "cancelReason": "用户主动取消",           // 必填，取消原因
  "operatorType": "user"                    // 必填，操作者类型：user/system/admin
}
```

---

### 4. 支付回调处理
**接口路径**: `POST /api/v1/payments/notify/{channel}`  
**接口描述**: 处理第三方支付平台的异步通知

#### 请求参数
- **channel** (path): 支付渠道：alipay/wechat/bank，必填

---

### 5. 模拟支付完成 🧪
**接口路径**: `POST /api/v1/payments/{paymentId}/mock-success`  
**接口描述**: 测试专用接口 - 模拟支付成功，直接完成关联订单的支付流程

> ⚠️ **注意**: 此接口仅用于开发和测试环境，生产环境会自动禁用

#### 请求参数
- **paymentId** (path): 支付ID，必填

```json
{
  "userId": 12345,                           // 必填，用户ID（用于权限验证）
  "mockTransactionId": "MOCK202401160001",   // 可选，模拟第三方交易号
  "mockNotifyData": {                        // 可选，模拟回调数据
    "channel": "alipay",
    "actualAmount": 237.00,
    "feeAmount": 2.37,
    "channelOrderId": "20240116220000001"
  },
  "testRemark": "测试订单支付流程"           // 可选，测试备注
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "模拟支付成功",
  "data": {
    "paymentId": "PAY202401160001",
    "paymentNo": "20240116103000001",
    "orderId": 987654,
    "orderNo": "ORD202401160001",
    "originalStatus": "pending",
    "newStatus": "success",
    "mockTransactionId": "MOCK202401160001",
    "paidTime": "2024-01-16T16:30:00",
    "mockData": {
      "isTestPayment": true,
      "mockChannel": "alipay",
      "actualAmount": 237.00,
      "processingTime": "2024-01-16T16:30:00"
    },
    "relatedOrderStatus": {
      "orderId": 987654,
      "orderStatus": "paid",
      "statusUpdateTime": "2024-01-16T16:30:05"
    }
  }
}
```

**业务流程**:
1. 验证支付记录存在且状态为 `pending`
2. 模拟第三方支付成功回调
3. 更新支付状态为 `success`
4. 触发订单状态更新为 `paid`
5. 清理相关缓存数据
6. 发送支付成功通知

---

### 6. 查询支付列表
**接口路径**: `POST /api/v1/payments/query`  
**接口描述**: 分页查询支付记录

> 💡 **缓存优化**: 用户支付记录查询已启用分布式缓存，缓存时间15分钟

#### 请求参数
```json
{
  "pageNum": 1,                            // 页码（从1开始）
  "pageSize": 20,                          // 每页大小
  "userId": 12345,                         // 可选，用户ID筛选
  "orderId": 987654,                       // 可选，订单ID筛选
  "paymentNo": "20240116103000001",        // 可选，支付单号搜索
  "status": "success",                     // 可选，支付状态
  "paymentMethod": "alipay",               // 可选，支付方式
  "paymentChannel": "app",                 // 可选，支付渠道
  "startTime": "2024-01-01T00:00:00",      // 可选，开始时间
  "endTime": "2024-01-31T23:59:59",        // 可选，结束时间
  "minAmount": 100.00,                     // 可选，最小金额
  "maxAmount": 1000.00,                    // 可选，最大金额
  "orderBy": "createTime",                 // 可选，排序字段
  "orderDirection": "DESC"                 // 可选，排序方向
}
```

---

## 退款管理 API

### 1. 申请退款
**接口路径**: `POST /api/v1/payments/{paymentId}/refund`  
**接口描述**: 申请退款

#### 请求参数
- **paymentId** (path): 支付ID，必填

```json
{
  "refundAmount": 89.00,                   // 必填，退款金额
  "refundReason": "商品质量问题",          // 必填，退款原因
  "refundType": "partial",                 // 必填，退款类型：full/partial
  "operatorId": 67890,                     // 必填，操作人ID
  "operatorType": "merchant",              // 必填，操作人类型：user/merchant/admin
  "description": "用户申请部分退款",       // 可选，退款描述
  "notifyUrl": "https://api.example.com/refund/notify", // 可选，退款通知URL
  "extraData": {                           // 可选，扩展数据
    "returnOrderId": "RET202401160001"
  }
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "refundId": "REF202401160001",
    "refundNo": "20240116140000001",
    "paymentId": "PAY202401160001",
    "orderId": 987654,
    "refundAmount": 89.00,
    "refundReason": "商品质量问题",
    "refundType": "partial",
    "status": "processing",
    "operatorId": 67890,
    "operatorType": "merchant",
    "createTime": "2024-01-16T14:00:00",
    "estimatedTime": "2024-01-17T14:00:00"
  }
}
```

---

### 2. 查询退款状态
**接口路径**: `GET /api/v1/payments/refunds/{refundId}`  
**接口描述**: 查询退款状态

#### 请求参数
- **refundId** (path): 退款ID，必填

---

### 3. 退款回调处理
**接口路径**: `POST /api/v1/payments/refund/notify/{channel}`  
**接口描述**: 处理第三方支付平台的退款通知

---

### 4. 查询退款列表
**接口路径**: `POST /api/v1/payments/refunds/query`  
**接口描述**: 分页查询退款记录

#### 请求参数
```json
{
  "pageNum": 1,
  "pageSize": 20,
  "userId": 12345,
  "paymentId": "PAY202401160001",
  "status": "success",
  "refundType": "partial",
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-01-31T23:59:59",
  "orderBy": "createTime",
  "orderDirection": "DESC"
}
```

---

## 账单管理 API

### 1. 生成账单
**接口路径**: `POST /api/v1/payments/bills`  
**接口描述**: 生成用户或商户账单

#### 请求参数
```json
{
  "billType": "user",                      // 必填，账单类型：user/merchant
  "targetId": 12345,                       // 必填，目标ID（用户ID或商户ID）
  "billPeriod": "monthly",                 // 必填，账单周期：daily/weekly/monthly/yearly
  "startTime": "2024-01-01T00:00:00",      // 必填，开始时间
  "endTime": "2024-01-31T23:59:59",        // 必填，结束时间
  "includeRefund": true,                   // 可选，是否包含退款记录
  "format": "pdf",                         // 可选，账单格式：pdf/excel/csv
  "operatorId": 99999                      // 必填，操作人ID
}
```

---

### 2. 获取账单详情
**接口路径**: `GET /api/v1/payments/bills/{billId}`  
**接口描述**: 获取账单详细信息

---

### 3. 下载账单
**接口路径**: `GET /api/v1/payments/bills/{billId}/download`  
**接口描述**: 下载账单文件

#### 请求参数
- **billId** (path): 账单ID，必填
- **format** (query): 下载格式：pdf/excel/csv，可选

---

### 4. 查询账单列表
**接口路径**: `POST /api/v1/payments/bills/query`  
**接口描述**: 分页查询账单列表

---

## 钱包管理 API

### 1. 查询余额
**接口路径**: `GET /api/v1/payments/wallet/{userId}/balance`  
**接口描述**: 查询用户钱包余额

> 💡 **缓存优化**: 用户余额查询已启用分布式缓存，缓存时间5分钟，兼顾实时性和性能

#### 请求参数
- **userId** (path): 用户ID，必填

#### 响应示例
```json
{
  "success": true,
  "data": {
    "userId": 12345,
    "totalBalance": 1250.00,             // 总余额
    "availableBalance": 1150.00,         // 可用余额
    "frozenBalance": 100.00,             // 冻结余额
    "currency": "CNY",
    "lastUpdateTime": "2024-01-16T15:30:00"
  }
}
```

---

### 2. 余额充值
**接口路径**: `POST /api/v1/payments/wallet/{userId}/recharge`  
**接口描述**: 钱包余额充值

#### 请求参数
- **userId** (path): 用户ID，必填

```json
{
  "amount": 100.00,                      // 必填，充值金额
  "paymentMethod": "alipay",             // 必填，支付方式
  "paymentChannel": "app",               // 必填，支付渠道
  "description": "钱包充值",             // 可选，充值描述
  "returnUrl": "https://app.example.com/wallet/recharge/return"
}
```

---

### 3. 余额提现
**接口路径**: `POST /api/v1/payments/wallet/{userId}/withdraw`  
**接口描述**: 钱包余额提现

#### 请求参数
- **userId** (path): 用户ID，必填

```json
{
  "amount": 500.00,                      // 必填，提现金额
  "withdrawMethod": "bank",              // 必填，提现方式：bank/alipay/wechat
  "accountInfo": {                       // 必填，账户信息
    "accountName": "张三",
    "accountNumber": "6222000000000000",
    "bankName": "中国银行",
    "bankCode": "BOC"
  },
  "withdrawReason": "个人消费",          // 可选，提现原因
  "paymentPassword": "encrypted_password" // 必填，支付密码（加密）
}
```

---

### 4. 余额转账
**接口路径**: `POST /api/v1/payments/wallet/{userId}/transfer`  
**接口描述**: 用户间余额转账

#### 请求参数
- **userId** (path): 转出用户ID，必填

```json
{
  "toUserId": 67890,                     // 必填，转入用户ID
  "amount": 50.00,                       // 必填，转账金额
  "transferReason": "朋友借款",          // 可选，转账原因
  "description": "还钱",                 // 可选，转账描述
  "paymentPassword": "encrypted_password" // 必填，支付密码（加密）
}
```

---

### 5. 钱包交易记录
**接口路径**: `POST /api/v1/payments/wallet/{userId}/transactions`  
**接口描述**: 查询钱包交易记录

#### 请求参数
- **userId** (path): 用户ID，必填

```json
{
  "pageNum": 1,
  "pageSize": 20,
  "transactionType": "all",              // 交易类型：all/recharge/withdraw/transfer/payment
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-01-31T23:59:59",
  "orderBy": "createTime",
  "orderDirection": "DESC"
}
```

---

## 支付统计 API

### 1. 支付统计概览
**接口路径**: `GET /api/v1/payments/statistics/overview`  
**接口描述**: 获取支付统计概览

> 💡 **缓存优化**: 支付统计数据已启用分布式缓存，缓存时间60分钟，实时性与性能的最佳平衡

#### 请求参数
- **timeRange** (query): 时间范围（天），可选，默认30
- **merchantId** (query): 商户ID，可选

#### 响应示例
```json
{
  "success": true,
  "data": {
    "totalPayments": 5000,               // 总支付笔数
    "totalAmount": 500000.00,            // 总支付金额
    "successPayments": 4800,             // 成功支付笔数
    "successAmount": 480000.00,          // 成功支付金额
    "successRate": 96.0,                 // 支付成功率（%）
    "averageAmount": 100.00,             // 平均支付金额
    "totalRefunds": 150,                 // 总退款笔数
    "totalRefundAmount": 15000.00,       // 总退款金额
    "refundRate": 3.1,                   // 退款率（%）
    "todayPayments": 200,                // 今日支付笔数
    "todayAmount": 20000.00,             // 今日支付金额
    "peakHour": 14,                      // 支付高峰时段
    "topPaymentMethod": "alipay"         // 最受欢迎支付方式
  }
}
```

---

### 2. 支付方式统计
**接口路径**: `GET /api/v1/payments/statistics/methods`  
**接口描述**: 获取支付方式统计

---

### 3. 支付趋势分析
**接口路径**: `GET /api/v1/payments/statistics/trend`  
**接口描述**: 获取支付趋势分析

#### 请求参数
- **timeType** (query): 时间类型：hour/day/week/month，必填
- **timeRange** (query): 时间范围，必填

---

## 风控管理 API

### 1. 风险检测
**接口路径**: `POST /api/v1/payments/risk/check`  
**接口描述**: 支付风险检测

#### 请求参数
```json
{
  "paymentId": "PAY202401160001",        // 必填，支付ID
  "userId": 12345,                       // 必填，用户ID
  "amount": 237.00,                      // 必填，支付金额
  "paymentMethod": "alipay",             // 必填，支付方式
  "clientIp": "192.168.1.100",           // 必填，客户端IP
  "deviceInfo": {                        // 可选，设备信息
    "deviceId": "device123",
    "deviceType": "mobile",
    "userAgent": "Mozilla/5.0..."
  },
  "riskLevel": "auto"                    // 可选，风险级别：auto/low/medium/high
}
```

---

### 2. 异常交易监控
**接口路径**: `GET /api/v1/payments/risk/monitor`  
**接口描述**: 获取异常交易监控列表

---

## 错误码说明

### 支付核心错误码
| 错误码 | 说明 |
|--------|------|
| PAYMENT_NOT_FOUND | 支付记录不存在 |
| PAYMENT_STATUS_ERROR | 支付状态错误 |
| PAYMENT_AMOUNT_ERROR | 支付金额错误 |
| PAYMENT_EXPIRED | 支付已过期 |
| PAYMENT_ALREADY_SUCCESS | 支付已成功 |
| PAYMENT_ALREADY_CANCELLED | 支付已取消 |
| REFUND_NOT_ALLOWED | 不允许退款 |
| REFUND_AMOUNT_EXCEEDED | 退款金额超限 |
| INSUFFICIENT_BALANCE | 余额不足 |
| PAYMENT_PASSWORD_ERROR | 支付密码错误 |
| PAYMENT_LIMIT_EXCEEDED | 超出支付限额 |
| RISK_CONTROL_REJECTED | 风控拒绝 |
| THIRD_PARTY_ERROR | 第三方支付错误 |
| ACCOUNT_FROZEN | 账户被冻结 |

### 测试专用错误码 🧪
| 错误码 | 说明 |
|--------|------|
| MOCK_PAYMENT_DISABLED | 模拟支付已禁用（生产环境） |
| MOCK_PAYMENT_STATUS_ERROR | 模拟支付状态错误 |
| MOCK_PAYMENT_NOT_PENDING | 支付状态不是待支付，无法模拟 |
| MOCK_ORDER_NOT_FOUND | 关联订单不存在 |
| MOCK_ORDER_UPDATE_FAILED | 订单状态更新失败 |

### 缓存相关错误码 ⚡
| 错误码 | 说明 |
|--------|------|
| CACHE_KEY_INVALID | 缓存键值无效 |
| CACHE_EXPIRED | 缓存已过期 |
| CACHE_UPDATE_FAILED | 缓存更新失败 |

---

**最后更新**: 2024-01-16  
**文档版本**: v2.0.0 (缓存增强版)

---

## 📝 版本更新日志

### v2.0.0 (2024-01-16) - 缓存增强版
- ⚡ 新增 JetCache 分布式缓存支持
- 🧪 新增模拟支付完成测试接口
- 📊 改进性能监控和日志记录
- 🔄 优化缓存策略和失效机制
- 🛡️ 增强风控和安全防护
- 📋 完善错误码分类和说明

### v1.0.0 (2024-01-15) - 基础版本
- 💰 支付核心功能完整实现
- 🏦 支持多种支付方式和渠道
- 💳 钱包余额管理功能
- 📊 支付统计和分析功能
- 🛡️ 基础风控和安全机制