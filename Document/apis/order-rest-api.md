# Order模块 REST API 接口文档

## 模块概述
订单管理REST API提供完整的订单生命周期管理功能，包括订单创建、查询、支付、发货、统计分析等业务场景。支持商品订单、内容订单、订阅订单等多种业务类型，采用严格的权限验证机制确保数据安全。

**版本**: 2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/orders`  
**权限验证**: 所有用户相关操作都需要提供userId进行权限验证

---

## 🚀 基础订单操作

### 1. 创建订单
**接口地址**: `POST /api/v1/orders`  
**接口描述**: 创建新订单，支持四种商品类型和双支付模式  
**权限要求**: 需要用户认证

**请求参数**:
```json
{
  "userId": 123,
  "userNickname": "测试用户",
  "goodsId": 456,
  "goodsType": "content",
  "quantity": 1,
  "paymentMode": "coin",
  "cashAmount": 99.99,
  "coinCost": 100,
  "contentId": 789,
  "effectiveDiscountAmount": 10.00
}
```

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1001,
    "orderNo": "ORD1738284732123456789",
    "userId": 123,
    "userNickname": "测试用户",
    "goodsId": 456,
    "goodsName": "精品内容",
    "goodsType": "content",
    "quantity": 1,
    "paymentMode": "coin",
    "cashAmount": 99.99,
    "coinCost": 100,
    "totalAmount": 109.99,
    "finalAmount": 99.99,
    "status": "pending",
    "payStatus": "unpaid",
    "createTime": "2024-01-30T10:30:00",
    "updateTime": "2024-01-30T10:30:00"
  }
}
```

### 2. 获取订单详情
**接口地址**: `GET /api/v1/orders/{id}`  
**接口描述**: 根据ID获取订单详细信息，需要用户权限验证  
**权限要求**: 用户只能查看自己的订单

**请求参数**:
- `id` (Path): 订单ID，必填，最小值1
- `userId` (Query): 用户ID，用于权限验证，必填

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1001,
    "orderNo": "ORD1738284732123456789",
    "userId": 123,
    "userNickname": "测试用户",
    "goodsId": 456,
    "goodsName": "精品内容",
    "goodsType": "content",
    "status": "paid",
    "payStatus": "paid",
    "payMethod": "coin",
    "payTime": "2024-01-30T10:35:00",
    "createTime": "2024-01-30T10:30:00"
  }
}
```

### 3. 根据订单号获取详情
**接口地址**: `GET /api/v1/orders/no/{orderNo}`  
**接口描述**: 根据订单号获取订单详细信息，需要用户权限验证  
**权限要求**: 用户只能查看自己的订单

**请求参数**:
- `orderNo` (Path): 订单号，必填
- `userId` (Query): 用户ID，用于权限验证，必填

### 4. 取消订单
**接口地址**: `POST /api/v1/orders/{id}/cancel`  
**接口描述**: 取消指定订单，需要用户权限验证  
**权限要求**: 用户只能取消自己的订单

**请求参数**:
- `id` (Path): 订单ID，必填
- `reason` (Query): 取消原因，默认"用户主动取消"
- `userId` (Query): 用户ID，用于权限验证，必填

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null
}
```

### 5. 批量取消订单
**接口地址**: `POST /api/v1/orders/batch-cancel`  
**接口描述**: 批量取消多个订单，需要操作者权限  
**权限要求**: 需要操作者权限

**请求参数**:
```json
{
  "orderIds": [1001, 1002, 1003],
  "reason": "批量取消",
  "operatorId": 123
}
```

### 6. 分页查询订单
**接口地址**: `POST /api/v1/orders/query`  
**接口描述**: 支持多条件分页查询订单列表

**请求参数**:
```json
{
  "userId": 123,
  "status": "paid",
  "goodsType": "content",
  "currentPage": 1,
  "pageSize": 20
}
```

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "datas": [
      {
        "id": 1001,
        "orderNo": "ORD1738284732123456789",
        "userId": 123,
        "goodsName": "精品内容",
        "status": "paid",
        "finalAmount": 99.99,
        "createTime": "2024-01-30T10:30:00"
      }
    ],
    "total": 50,
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 3
  }
}
```

### 7. 查询用户订单
**接口地址**: `GET /api/v1/orders/user/{userId}`  
**接口描述**: 获取指定用户的订单列表，需要权限验证

**请求参数**:
- `userId` (Path): 用户ID，必填
- `status` (Query): 订单状态，可选
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 8. 模拟支付成功
**接口地址**: `POST /api/v1/orders/{id}/mock-payment`  
**接口描述**: 测试专用 - 模拟订单支付成功，自动处理金币充值  
**权限要求**: 测试环境专用

**请求参数**:
- `id` (Path): 订单ID，必填
- `userId` (Query): 用户ID，必填

---

## 🔍 查询类端点

### 9. 根据商品类型查询订单
**接口地址**: `GET /api/v1/orders/goods-type/{goodsType}`  
**接口描述**: 查询特定类型商品的订单

**请求参数**:
- `goodsType` (Path): 商品类型（coin/content/subscription/goods），必填
- `status` (Query): 订单状态，可选
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 10. 查询商家订单
**接口地址**: `GET /api/v1/orders/seller/{sellerId}`  
**接口描述**: 查询指定商家的订单列表

**请求参数**:
- `sellerId` (Path): 商家ID，必填
- `status` (Query): 订单状态，可选
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 11. 搜索订单
**接口地址**: `GET /api/v1/orders/search`  
**接口描述**: 根据关键词搜索订单

**请求参数**:
- `keyword` (Query): 搜索关键词，必填
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 12. 根据时间范围查询订单
**接口地址**: `GET /api/v1/orders/time-range`  
**接口描述**: 查询指定时间范围内的订单

**请求参数**:
- `startTime` (Query): 开始时间，格式："2024-01-30T00:00:00"
- `endTime` (Query): 结束时间，格式："2024-01-30T23:59:59"
- `status` (Query): 订单状态，可选
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 13. 查询用户金币消费订单
**接口地址**: `GET /api/v1/orders/user/{userId}/coin-orders`  
**接口描述**: 查看用户使用金币购买内容的订单记录

### 14. 查询用户充值订单
**接口地址**: `GET /api/v1/orders/user/{userId}/recharge-orders`  
**接口描述**: 查看用户购买金币的充值订单记录

### 15. 查询内容购买订单
**接口地址**: `GET /api/v1/orders/content-orders`  
**接口描述**: 查看特定内容的购买订单

**请求参数**:
- `contentId` (Query): 内容ID，可选（为空则查询所有内容订单）
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 16. 查询订阅订单
**接口地址**: `GET /api/v1/orders/subscription-orders`  
**接口描述**: 查看用户的会员订阅、VIP等订阅类服务订单

**请求参数**:
- `subscriptionType` (Query): 订阅类型（VIP/PREMIUM），可选
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 17. 获取待支付订单
**接口地址**: `GET /api/v1/orders/pending`  
**接口描述**: 查询用户或系统中待支付的订单

**请求参数**:
- `userId` (Query): 用户ID，可选（为空则查询系统所有待支付订单）
- `currentPage` (Query): 当前页码，默认1
- `pageSize` (Query): 页面大小，默认20

### 18. 获取已完成订单
**接口地址**: `GET /api/v1/orders/completed`  
**接口描述**: 查询用户或系统中已完成的订单

### 19. 获取今日订单
**接口地址**: `GET /api/v1/orders/today`  
**接口描述**: 查询今天创建的所有订单

---

## 💳 支付相关端点

### 20. 处理订单支付
**接口地址**: `POST /api/v1/orders/{id}/payment/process`  
**接口描述**: 处理订单支付请求，需要用户权限验证  
**权限要求**: 用户只能支付自己的订单

**请求参数**:
- `id` (Path): 订单ID，必填
- `payMethod` (Query): 支付方式（cash/coin），必填
- `userId` (Query): 用户ID，用于权限验证，必填

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "status": "success",
    "orderId": 1001,
    "orderNo": "ORD1738284732123456789",
    "payMethod": "coin",
    "coinCost": 100,
    "message": "金币支付成功",
    "payTime": 1738284732000
  }
}
```

### 21. 确认支付成功
**接口地址**: `POST /api/v1/orders/{id}/payment/confirm`  
**接口描述**: 确认订单支付成功，需要用户权限验证

**请求参数**:
- `id` (Path): 订单ID，必填
- `payMethod` (Query): 支付方式，必填
- `userId` (Query): 用户ID，必填

### 22. 支付回调处理
**接口地址**: `POST /api/v1/orders/payment/callback`  
**接口描述**: 处理第三方支付平台的回调通知

**请求参数**:
- `orderNo` (Query): 订单号，必填
- `payStatus` (Query): 支付状态，必填
- `payMethod` (Query): 支付方式，必填
- `extraInfo` (Body): 回调额外信息，可选

**响应**: 返回"success"表示回调处理成功

### 23. 申请退款
**接口地址**: `POST /api/v1/orders/{id}/refund`  
**接口描述**: 申请订单退款，需要用户权限验证

**请求参数**:
- `id` (Path): 订单ID，必填
- `reason` (Query): 退款原因，必填
- `userId` (Query): 用户ID，必填

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "退款申请成功",
    "refundId": "RF202401301234567890",
    "refundAmount": 99.99,
    "estimatedTime": "3-5个工作日"
  }
}
```

---

## 📦 订单管理端点

### 24. 订单发货
**接口地址**: `POST /api/v1/orders/{id}/ship`  
**接口描述**: 商家发货操作，需要操作者权限

**请求参数**:
- `id` (Path): 订单ID，必填
- `shippingInfo` (Body): 物流信息，必填
- `operatorId` (Query): 操作者ID（商家ID），必填

**请求体示例**:
```json
{
  "logistics": "顺丰快递",
  "trackingNumber": "SF1234567890",
  "estimatedDelivery": "2024-02-01",
  "shippingAddress": "北京市朝阳区xxx"
}
```

### 25. 确认收货
**接口地址**: `POST /api/v1/orders/{id}/confirm-receipt`  
**接口描述**: 用户确认收到商品

**请求参数**:
- `id` (Path): 订单ID，必填
- `userId` (Query): 用户ID，必填

### 26. 完成订单
**接口地址**: `POST /api/v1/orders/{id}/complete`  
**接口描述**: 系统或管理员完成订单的最终处理

**请求参数**:
- `id` (Path): 订单ID，必填
- `operatorId` (Query): 操作者ID，必填

---

## 📊 统计分析端点

### 27. 用户订单统计
**接口地址**: `GET /api/v1/orders/statistics/user/{userId}`  
**接口描述**: 获取用户订单统计信息

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "totalOrders": 150,
    "paidOrders": 145,
    "cancelledOrders": 5,
    "totalAmount": 15999.50,
    "monthlyAmount": 2999.90,
    "favoriteType": "content",
    "lastOrderTime": "2024-01-30T10:30:00"
  }
}
```

### 28. 商品销售统计
**接口地址**: `GET /api/v1/orders/statistics/goods/{goodsId}`  
**接口描述**: 获取商品的销量和营收统计

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "goodsId": 456,
    "totalSales": 1250,
    "totalRevenue": 124999.50,
    "monthlyRevenue": 25999.90,
    "averageRating": 4.8,
    "returnRate": 0.02
  }
}
```

### 29. 按商品类型统计订单
**接口地址**: `GET /api/v1/orders/statistics/by-type`  
**接口描述**: 统计各类型商品的订单分布和营收情况

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "goodsType": "content",
      "orderCount": 850,
      "revenue": 85999.50,
      "percentage": 45.2
    },
    {
      "goodsType": "coin",
      "orderCount": 650,
      "revenue": 65999.00,
      "percentage": 34.7
    }
  ]
}
```

### 30. 获取热门商品
**接口地址**: `GET /api/v1/orders/statistics/hot-goods`  
**接口描述**: 根据销量统计最受欢迎的商品

**请求参数**:
- `limit` (Query): 返回数量限制，默认10

### 31. 获取日营收统计
**接口地址**: `GET /api/v1/orders/statistics/daily-revenue`  
**接口描述**: 统计指定时间范围内的每日营收数据

**请求参数**:
- `startDate` (Query): 开始日期，格式："2024-01-01"，必填
- `endDate` (Query): 结束日期，格式："2024-01-31"，必填

### 32. 获取用户最近购买记录
**接口地址**: `GET /api/v1/orders/user/{userId}/recent`  
**接口描述**: 查看用户最近的购买历史

**请求参数**:
- `userId` (Path): 用户ID，必填
- `limit` (Query): 记录数量限制，默认10

---

## ✅ 业务验证端点

### 33. 验证订单支付条件
**接口地址**: `GET /api/v1/orders/{id}/validate-payment`  
**接口描述**: 检查订单状态和支付前置条件

**请求参数**:
- `id` (Path): 订单ID，必填
- `userId` (Query): 用户ID，必填

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "valid": true,
    "message": "订单可以支付",
    "paymentMethods": ["cash", "coin"],
    "requiredCoin": 100,
    "userCoinBalance": 500
  }
}
```

### 34. 验证订单取消条件
**接口地址**: `GET /api/v1/orders/{id}/validate-cancel`  
**接口描述**: 检查订单状态和取消规则

### 35. 验证订单退款条件
**接口地址**: `GET /api/v1/orders/{id}/validate-refund`  
**接口描述**: 检查退款政策和退款条件

---

## ⚙️ 系统管理端点

### 36. 获取超时订单
**接口地址**: `GET /api/v1/orders/timeout`  
**接口描述**: 查询超过指定时间仍未支付的订单

**请求参数**:
- `timeoutMinutes` (Query): 超时分钟数，默认30

### 37. 自动取消超时订单
**接口地址**: `POST /api/v1/orders/auto-cancel-timeout`  
**接口描述**: 系统定时任务，自动取消超时未支付的订单

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 15
}
```

### 38. 自动完成已发货订单
**接口地址**: `POST /api/v1/orders/auto-complete-shipped`  
**接口描述**: 系统定时任务，自动完成长时间未确认收货的订单

**请求参数**:
- `days` (Query): 发货后天数，默认7

### 39. 统计商品订单数
**接口地址**: `GET /api/v1/orders/count/goods/{goodsId}`  
**接口描述**: 根据商品ID统计订单数量

**请求参数**:
- `goodsId` (Path): 商品ID，必填
- `status` (Query): 订单状态，可选

### 40. 统计用户订单数
**接口地址**: `GET /api/v1/orders/count/user/{userId}`  
**接口描述**: 根据用户ID统计订单数量

### 41. 更新订单支付信息
**接口地址**: `POST /api/v1/orders/{id}/update-payment-info`  
**接口描述**: 内部系统调用，更新订单的支付状态和时间

**请求参数**:
- `id` (Path): 订单ID，必填
- `payStatus` (Query): 支付状态，必填
- `payMethod` (Query): 支付方式，必填
- `payTime` (Query): 支付时间，必填

### 42. 订单系统健康检查
**接口地址**: `GET /api/v1/orders/health`  
**接口描述**: 检查订单系统的运行状态

**响应示例**:
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": "订单系统运行正常, 数据库连接正常, 缓存服务正常, 用户服务连接正常"
}
```

---

## 📝 通用说明

### 状态码说明
- `SUCCESS`: 操作成功
- `USER_NOT_FOUND`: 用户不存在
- `ORDER_NOT_FOUND`: 订单不存在
- `ACCESS_DENIED`: 无权限访问
- `INVALID_PARAM`: 参数错误
- `PAYMENT_FAILED`: 支付失败
- `QUERY_FAILED`: 查询失败

### 订单状态
- `pending`: 待支付
- `paid`: 已支付
- `shipped`: 已发货
- `completed`: 已完成
- `cancelled`: 已取消
- `refunded`: 已退款

### 支付状态
- `unpaid`: 未支付
- `paid`: 已支付
- `failed`: 支付失败
- `refunded`: 已退款

### 商品类型
- `coin`: 金币充值
- `content`: 付费内容
- `subscription`: 订阅服务
- `goods`: 实体商品

### 支付方式
- `cash`: 现金支付
- `coin`: 金币支付

### 权限验证
所有涉及用户数据的接口都需要传入userId进行权限验证，确保用户只能操作自己的数据。管理员操作需要传入operatorId标识操作者身份。

---

**API总计**: 42个REST端点  
**完整性**: 覆盖订单全生命周期管理  
**安全性**: 严格的权限验证和数据校验  
**易用性**: 标准化的RESTful设计和响应格式