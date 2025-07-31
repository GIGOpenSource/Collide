# Collide 订单服务 API 文档

## 概述

Collide 订单服务提供完整的订单管理功能，支持四种商品类型和双支付模式的订单全生命周期管理。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/orders`  
**Dubbo服务**: `collide-order`  
**设计理念**: 支持四种商品类型（coin/goods/subscription/content）和双支付模式（现金/金币）的统一订单管理系统

## v2.0.0 新特性 ⚡

🎯 **四种商品类型统一支持**
- ✅ **金币充值包** (`coin`) - 现金支付 → 金币到账 → 支持内容消费
- ✅ **实体商品** (`goods`) - 现金支付 → 物流配送 → 传统电商模式  
- ✅ **订阅服务** (`subscription`) - 现金支付 → 会员开通 → 增值服务
- ✅ **付费内容** (`content`) - 金币支付 → 内容解锁 → 知识付费

💳 **智能双支付路由**
- 🚀 现金支付模式：支付宝/微信/余额 → 用于 coin/goods/subscription
- 🚀 金币支付模式：钱包金币扣减 → 专用于 content

🔄 **完整业务闭环**
- 📦 任务完成 → 金币奖励 → 金币充值 → 内容购买 → 订阅服务 → 实体商品

---

## 订单管理 API

### 1. 创建订单
**接口路径**: `POST /api/v1/orders`  
**接口描述**: 创建新订单，支持四种商品类型和双支付模式

#### 请求参数
```json
{
  "userId": 12345,                    // 必填，下单用户ID
  "goodsId": 789012,                  // 必填，商品ID
  "goodsType": "coin",                // 必填，商品类型：coin/goods/subscription/content
  "paymentMode": "cash",              // 必填，支付模式：cash/coin
  "quantity": 1,                      // 必填，购买数量
  "remark": "请尽快发货"               // 可选，订单备注
}
```

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

**失败响应 (400)**:
```json
{
  "success": false,
  "code": "CREATE_ORDER_ERROR",
  "message": "订单创建失败: 库存不足"
}
```

---

### 2. 获取订单详情
**接口路径**: `GET /api/v1/orders/{id}`  
**接口描述**: 根据订单ID获取订单详细信息

#### 请求参数
- **id** (path): 订单ID，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS", 
  "message": "操作成功",
  "data": {
    "id": 987654,
    "orderNo": "ORD202401310001",
    "userId": 12345,
    "userNickname": "测试用户",
    "goodsId": 789012,
    "goodsName": "100金币充值包",
    "goodsType": "coin",
    "goodsCategory": "虚拟商品",
    "coinAmount": 100,
    "quantity": 1,
    "paymentMode": "cash",
    "cashAmount": 10.00,
    "totalAmount": 10.00,
    "finalAmount": 10.00,
    "status": "completed",
    "payStatus": "paid",
    "payMethod": "alipay",
    "payTime": "2024-01-31T10:30:00",
    "createTime": "2024-01-31T10:25:00",
    "updateTime": "2024-01-31T10:30:00"
  }
}
```

---

### 3. 根据订单号获取详情
**接口路径**: `GET /api/v1/orders/no/{orderNo}`  
**接口描述**: 根据订单号获取订单详细信息

#### 请求参数
- **orderNo** (path): 订单号，必填，不能为空

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 987654,
    "orderNo": "ORD202401310001",
    "userId": 12345,
    "status": "completed"
  }
}
```

---

### 4. 取消订单
**接口路径**: `POST /api/v1/orders/{id}/cancel`  
**接口描述**: 取消指定订单

#### 请求参数
- **id** (path): 订单ID，必填，必须大于0
- **reason** (query): 取消原因，可选，默认"用户主动取消"

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

### 5. 批量取消订单
**接口路径**: `POST /api/v1/orders/batch-cancel`  
**接口描述**: 批量取消多个订单

#### 请求参数
- **reason** (query): 取消原因，可选，默认"批量取消"

```json
[987654, 987655, 987656]    // 请求体：订单ID列表
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

---

### 6. 分页查询订单
**接口路径**: `POST /api/v1/orders/query`  
**接口描述**: 支持多条件分页查询订单列表

#### 请求参数
```json
{
  "currentPage": 1,                   // 当前页码，从1开始
  "pageSize": 20,                     // 每页大小
  "userId": 12345,                    // 可选，用户ID筛选
  "status": "completed",              // 可选，订单状态
  "payStatus": "paid",                // 可选，支付状态
  "goodsType": "coin",                // 可选，商品类型
  "paymentMode": "cash"               // 可选，支付模式
}
```

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 987654,
      "orderNo": "ORD202401310001",
      "userId": 12345,
      "goodsName": "100金币充值包",
      "goodsType": "coin",
      "totalAmount": 10.00,
      "status": "completed",
      "createTime": "2024-01-31T10:25:00"
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

---

### 7. 根据用户查询订单
**接口路径**: `GET /api/v1/orders/user/{userId}`  
**接口描述**: 获取指定用户的订单列表

#### 请求参数
- **userId** (path): 用户ID，必填，必须大于0
- **status** (query): 订单状态，可选
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 987654,
      "orderNo": "ORD202401310001",
      "goodsName": "100金币充值包",
      "totalAmount": 10.00,
      "status": "completed",
      "createTime": "2024-01-31T10:25:00"
    }
  ],
  "total": 23,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2
}
```

---

## 订单支付 API

### 8. 处理订单支付
**接口路径**: `POST /api/v1/orders/{id}/payment/process`  
**接口描述**: 处理订单支付请求，支持双支付模式

#### 请求参数
- **id** (path): 订单ID，必填，必须大于0
- **payMethod** (query): 支付方式，必填，支持：alipay/wechat/balance/coin

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "paymentId": "PAY202401310001",
    "paymentMethod": "alipay",
    "paymentUrl": "https://openapi.alipay.com/gateway.do?...",
    "expireTime": "2024-01-31T11:30:00",
    "coinReward": 100              // 金币充值订单完成后获得的金币数量
  }
}
```

---

## 订单统计 API

### 9. 用户订单统计
**接口路径**: `GET /api/v1/orders/statistics/user/{userId}`  
**接口描述**: 获取用户订单统计信息

#### 请求参数
- **userId** (path): 用户ID，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "totalOrders": 25,                    // 总订单数
    "completedOrders": 20,                // 已完成订单数
    "cancelledOrders": 5,                 // 已取消订单数
    "totalCashSpent": 500.00,             // 总现金消费
    "totalCoinsSpent": 1000,              // 总金币消费
    "coinRechargeOrders": 5,              // 金币充值订单数
    "contentPurchaseOrders": 8,           // 内容购买订单数
    "subscriptionOrders": 2,              // 订阅服务订单数
    "goodsOrders": 10                     // 实体商品订单数
  }
}
```

---

## 测试专用 API

### 10. 模拟支付成功 🧪
**接口路径**: `POST /api/v1/orders/{id}/mock-payment`  
**接口描述**: 测试专用 - 模拟订单支付成功，自动处理金币充值

⚠️ **重要提示**: 此接口仅用于开发测试，生产环境请禁用！

#### 请求参数
- **id** (path): 订单ID，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "status": "success",
    "paymentId": "MOCK_PAY_1706688600000",
    "payMethod": "mock",
    "payTime": "2024-01-31T15:30:00",
    "coinRecharge": {
      "processed": true,
      "userId": 12345,
      "coinAmount": 100,
      "message": "金币充值包购买自动到账"
    }
  }
}
```

#### 使用场景
```bash
# 1. 创建金币充值订单
POST /api/v1/orders
{
  "userId": 12345,
  "goodsId": 789012,    # 金币充值包ID
  "goodsType": "coin",
  "paymentMode": "cash",
  "quantity": 1
}

# 2. 模拟支付成功（会自动充值金币到用户钱包）
POST /api/v1/orders/987654/mock-payment

# 3. 查看用户金币余额变化
GET /api/v1/wallet/users/12345/coin/balance
```

---

## 业务流程说明

### 🔄 完整业务闭环
```
1. 用户完成任务 → 系统发放金币奖励
2. 用户金币不足 → 购买金币充值包（现金支付）
3. 金币充值成功 → 自动到账用户钱包
4. 用户使用金币 → 购买付费内容（金币支付）
5. 用户现金支付 → 购买订阅服务/实体商品
```

### 💳 支付模式路由
```
商品类型          支付模式       支付方式
coin              cash          alipay/wechat/balance
goods             cash          alipay/wechat/balance  
subscription      cash          alipay/wechat/balance
content           coin          wallet_coin
```

### 🎯 金币充值流程（重点）
```
1. 创建金币充值订单 → goods_type: 'coin', payment_mode: 'cash'
2. 用户选择支付方式 → alipay/wechat/balance
3. 支付成功回调 → 订单状态更新为 'completed'
4. 系统自动检测 → 金币类型订单，调用钱包服务
5. 钱包服务充值 → 根据coin_amount给用户充值金币
6. 充值完成通知 → 用户可查看金币余额变化
```

---

## 错误码说明

### 订单业务错误码
| 错误码 | 说明 |
|--------|------|
| ORDER_NOT_FOUND | 订单不存在 |
| ORDER_CREATE_ERROR | 订单创建失败 |
| ORDER_CANCEL_ERROR | 订单取消失败 |
| ORDER_PAYMENT_ERROR | 订单支付失败 |
| INSUFFICIENT_STOCK | 库存不足 |
| INSUFFICIENT_COINS | 金币余额不足 |
| INVALID_PAYMENT_MODE | 支付模式错误 |
| GOODS_TYPE_MISMATCH | 商品类型不匹配 |

### 支付相关错误码
| 错误码 | 说明 |
|--------|------|
| PAYMENT_METHOD_ERROR | 支付方式错误 |
| PAYMENT_AMOUNT_ERROR | 支付金额错误 |
| WALLET_SERVICE_ERROR | 钱包服务异常 |
| COIN_RECHARGE_ERROR | 金币充值失败 |

---

## 版本更新日志

### v2.0.0 (扩展版) - 2024-01-31

🎯 **重大升级**
- ⚡ **四种商品类型支持** - coin/goods/subscription/content统一管理
- 💳 **双支付模式** - 现金支付与金币支付智能路由
- 🔄 **完整业务闭环** - 从任务奖励到内容消费的完整链路
- 🎮 **金币充值自动化** - 支付成功后自动充值用户钱包
- 🏗️ **缓存增强架构** - JetCache分布式缓存提升性能

🚀 **核心功能**
- 金币充值包购买流程完善
- 付费内容金币支付支持
- 订阅服务会员开通功能
- 实体商品传统电商流程
- 跨模块钱包服务集成

### v1.0.0 (基础版) - 2024-01-01
- 基础订单管理功能
- 简单的支付处理

---

**最后更新**: 2024-01-31  
**文档版本**: v2.0.0 (扩展版)  
**服务状态**: ✅ 生产可用