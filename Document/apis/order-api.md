# Collide 订单服务 API 文档

## 概述

Collide 订单服务提供完整的订单管理功能，包括订单创建、支付、发货、收货、退款等全生命周期管理。支持多种订单类型、状态流转、物流追踪等特性。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/orders`  
**Dubbo服务**: `collide-order`  
**设计理念**: 完整的订单管理系统，支持复杂业务场景，提供可靠的订单处理能力

---

## 订单管理 API

### 1. 创建订单
**接口路径**: `POST /api/v1/orders`  
**接口描述**: 创建新订单

#### 请求参数
```json
{
  "userId": 12345,                           // 必填，下单用户ID
  "orderType": "goods",                      // 必填，订单类型：goods/service/recharge
  "items": [                                 // 必填，订单商品列表
    {
      "goodsId": 789012,
      "goodsName": "Java编程实战教程",
      "price": 89.00,
      "quantity": 2,
      "specifications": [
        {
          "name": "版本",
          "value": "标准版"
        }
      ]
    },
    {
      "goodsId": 789013,
      "goodsName": "Python入门指南",
      "price": 69.00,
      "quantity": 1,
      "specifications": []
    }
  ],
  "receiverInfo": {                          // 必填（实物商品），收货信息
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "朝阳区",
    "district": "望京街道",
    "address": "阜通东大街6号院",
    "postcode": "100102"
  },
  "couponId": 5001,                          // 可选，优惠券ID
  "discountAmount": 10.00,                   // 可选，优惠金额
  "shippingFee": 0.00,                       // 可选，运费
  "remark": "请尽快发货",                     // 可选，订单备注
  "source": "app",                           // 可选，订单来源：app/web/wechat
  "paymentMethod": "alipay"                  // 可选，支付方式：alipay/wechat/bank
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "订单创建成功",
  "data": {
    "id": 987654,
    "orderNo": "ORD202401160001",
    "userId": 12345,
    "orderType": "goods",
    "status": "pending_payment",
    "paymentStatus": "unpaid",
    "shippingStatus": "pending",
    "totalAmount": 247.00,
    "discountAmount": 10.00,
    "shippingFee": 0.00,
    "actualAmount": 237.00,
    "items": [
      {
        "id": 1001,
        "goodsId": 789012,
        "goodsName": "Java编程实战教程",
        "price": 89.00,
        "quantity": 2,
        "totalPrice": 178.00,
        "specifications": [
          {
            "name": "版本",
            "value": "标准版"
          }
        ]
      },
      {
        "id": 1002,
        "goodsId": 789013,
        "goodsName": "Python入门指南",
        "price": 69.00,
        "quantity": 1,
        "totalPrice": 69.00,
        "specifications": []
      }
    ],
    "receiverInfo": {
      "name": "张三",
      "phone": "13800138000",
      "province": "北京市",
      "city": "朝阳区",
      "district": "望京街道",
      "address": "阜通东大街6号院",
      "postcode": "100102"
    },
    "createTime": "2024-01-16T10:30:00",
    "expireTime": "2024-01-16T11:30:00"
  }
}
```

---

### 2. 更新订单
**接口路径**: `PUT /api/v1/orders/{id}`  
**接口描述**: 更新订单信息（仅限未支付订单）

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "receiverInfo": {                          // 可选，更新收货信息
    "name": "李四",
    "phone": "13900139000",
    "address": "新的收货地址"
  },
  "remark": "更新后的备注",                  // 可选，更新备注
  "paymentMethod": "wechat"                  // 可选，更新支付方式
}
```

---

### 3. 取消订单
**接口路径**: `POST /api/v1/orders/{id}/cancel`  
**接口描述**: 取消订单

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "userId": 12345,                          // 必填，操作用户ID
  "cancelReason": "不想要了",                // 必填，取消原因
  "cancelType": "user"                       // 必填，取消类型：user/system/admin
}
```

---

### 4. 获取订单详情
**接口路径**: `GET /api/v1/orders/{id}`  
**接口描述**: 获取订单详细信息

#### 请求参数
- **id** (path): 订单ID，必填
- **userId** (query): 用户ID，可选（用于权限验证）

---

### 5. 查询订单列表
**接口路径**: `POST /api/v1/orders/query`  
**接口描述**: 分页查询订单列表

#### 请求参数
```json
{
  "pageNum": 1,                            // 页码（从1开始）
  "pageSize": 20,                          // 每页大小
  "userId": 12345,                         // 可选，用户ID筛选
  "orderNo": "ORD202401160001",            // 可选，订单号搜索
  "status": "pending_payment",             // 可选，订单状态
  "paymentStatus": "unpaid",               // 可选，支付状态
  "shippingStatus": "pending",             // 可选，配送状态
  "orderType": "goods",                    // 可选，订单类型
  "startTime": "2024-01-01T00:00:00",      // 可选，开始时间
  "endTime": "2024-01-31T23:59:59",        // 可选，结束时间
  "minAmount": 100.00,                     // 可选，最小金额
  "maxAmount": 1000.00,                    // 可选，最大金额
  "orderBy": "createTime",                 // 可选，排序字段
  "orderDirection": "DESC"                 // 可选，排序方向
}
```

---

## 订单支付 API

### 1. 发起支付
**接口路径**: `POST /api/v1/orders/{id}/pay`  
**接口描述**: 发起订单支付

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "paymentMethod": "alipay",               // 必填，支付方式：alipay/wechat/bank
  "paymentChannel": "app",                 // 必填，支付渠道：app/web/h5
  "returnUrl": "https://app.example.com/payment/return", // 可选，支付成功返回URL
  "notifyUrl": "https://api.example.com/payment/notify"  // 可选，异步通知URL
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "paymentId": "PAY202401160001",
    "paymentMethod": "alipay",
    "paymentUrl": "https://openapi.alipay.com/gateway.do?...",
    "qrCodeUrl": "https://qr.alipay.com/bax08888",
    "expireTime": "2024-01-16T11:30:00",
    "paymentInfo": {
      "appId": "2021000000000000",
      "orderString": "alipay_sdk=..."
    }
  }
}
```

---

### 2. 支付回调
**接口路径**: `POST /api/v1/orders/payment/notify`  
**接口描述**: 接收支付平台的异步通知

---

### 3. 查询支付状态
**接口路径**: `GET /api/v1/orders/{id}/payment/status`  
**接口描述**: 查询订单支付状态

#### 请求参数
- **id** (path): 订单ID，必填

---

## 订单发货 API

### 1. 确认发货
**接口路径**: `POST /api/v1/orders/{id}/ship`  
**接口描述**: 确认订单发货

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "sellerId": 67890,                       // 必填，卖家ID
  "logisticsCompany": "顺丰速运",           // 必填，物流公司
  "logisticsCode": "SF",                   // 必填，物流公司代码
  "trackingNumber": "SF1234567890",        // 必填，物流单号
  "shippingTime": "2024-01-16T14:00:00",   // 可选，发货时间
  "remark": "已通过顺丰发货，注意查收"      // 可选，发货备注
}
```

---

### 2. 更新物流信息
**接口路径**: `POST /api/v1/orders/{id}/logistics`  
**接口描述**: 更新订单物流信息

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "trackingNumber": "SF1234567890",        // 必填，物流单号
  "status": "in_transit",                  // 必填，物流状态
  "location": "北京分拣中心",              // 可选，当前位置
  "updateTime": "2024-01-16T16:00:00",     // 必填，更新时间
  "description": "快件已从北京分拣中心发出" // 可选，状态描述
}
```

---

### 3. 获取物流追踪
**接口路径**: `GET /api/v1/orders/{id}/logistics/track`  
**接口描述**: 获取订单物流追踪信息

#### 响应示例
```json
{
  "success": true,
  "data": {
    "orderId": 987654,
    "trackingNumber": "SF1234567890",
    "logisticsCompany": "顺丰速运",
    "currentStatus": "in_transit",
    "estimatedDelivery": "2024-01-17T18:00:00",
    "tracks": [
      {
        "time": "2024-01-16T14:00:00",
        "location": "北京华贸中心营业点",
        "status": "picked_up",
        "description": "快件已被收取"
      },
      {
        "time": "2024-01-16T16:00:00",
        "location": "北京分拣中心",
        "status": "in_transit",
        "description": "快件已从北京分拣中心发出"
      }
    ]
  }
}
```

---

## 订单收货 API

### 1. 确认收货
**接口路径**: `POST /api/v1/orders/{id}/receive`  
**接口描述**: 确认收货

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "userId": 12345,                         // 必填，用户ID
  "receiveTime": "2024-01-17T10:30:00",    // 可选，收货时间
  "satisfaction": "satisfied",             // 可选，满意度：satisfied/unsatisfied
  "comment": "商品质量很好，包装完整"      // 可选，收货评价
}
```

---

### 2. 申请退货
**接口路径**: `POST /api/v1/orders/{id}/return`  
**接口描述**: 申请退货

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "userId": 12345,                         // 必填，用户ID
  "returnReason": "商品有质量问题",         // 必填，退货原因
  "returnType": "refund_and_return",       // 必填，退货类型：refund_only/refund_and_return
  "description": "收到商品后发现有明显瑕疵", // 可选，详细描述
  "images": [                              // 可选，问题图片
    "https://example.com/return/image1.jpg",
    "https://example.com/return/image2.jpg"
  ],
  "requestAmount": 89.00                   // 可选，申请退款金额
}
```

---

### 3. 处理退货申请
**接口路径**: `POST /api/v1/orders/{id}/return/process`  
**接口描述**: 处理退货申请

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "sellerId": 67890,                       // 必填，卖家ID
  "action": "approve",                     // 必填，处理动作：approve/reject
  "refundAmount": 89.00,                   // 可选，退款金额
  "returnAddress": {                       // 可选，退货地址
    "name": "客服中心",
    "phone": "400-123-4567",
    "address": "北京市朝阳区退货处理中心"
  },
  "comment": "同意退货，请按地址寄回商品"   // 可选，处理意见
}
```

---

## 订单统计 API

### 1. 获取订单统计
**接口路径**: `GET /api/v1/orders/statistics`  
**接口描述**: 获取订单统计信息

#### 请求参数
- **userId** (query): 用户ID，可选
- **sellerId** (query): 卖家ID，可选
- **timeRange** (query): 时间范围（天），可选，默认30

#### 响应示例
```json
{
  "success": true,
  "data": {
    "totalOrders": 1250,                   // 总订单数
    "totalAmount": 125000.00,              // 总订单金额
    "paidOrders": 1100,                    // 已支付订单数
    "paidAmount": 110000.00,               // 已支付金额
    "completedOrders": 950,                // 已完成订单数
    "completedAmount": 95000.00,           // 已完成金额
    "cancelledOrders": 150,                // 已取消订单数
    "returnOrders": 45,                    // 退货订单数
    "returnRate": 4.5,                     // 退货率（%）
    "averageOrderAmount": 100.00,          // 平均订单金额
    "conversionRate": 88.0,                // 支付转化率（%）
    "todayOrders": 25,                     // 今日订单数
    "todayAmount": 2500.00                 // 今日订单金额
  }
}
```

---

### 2. 订单状态分布
**接口路径**: `GET /api/v1/orders/statistics/status`  
**接口描述**: 获取订单状态分布统计

---

### 3. 销售趋势分析
**接口路径**: `GET /api/v1/orders/statistics/trend`  
**接口描述**: 获取销售趋势分析

#### 请求参数
- **timeType** (query): 时间类型：day/week/month，必填
- **timeRange** (query): 时间范围，必填
- **sellerId** (query): 卖家ID，可选

---

## 订单评价 API

### 1. 提交评价
**接口路径**: `POST /api/v1/orders/{id}/review`  
**接口描述**: 提交订单评价

#### 请求参数
- **id** (path): 订单ID，必填

```json
{
  "userId": 12345,                         // 必填，用户ID
  "overallRating": 5,                      // 必填，总体评分（1-5）
  "goodsRating": 5,                        // 必填，商品评分（1-5）
  "serviceRating": 4,                      // 必填，服务评分（1-5）
  "logisticsRating": 5,                    // 必填，物流评分（1-5）
  "comment": "商品质量很好，服务态度佳",    // 可选，评价内容
  "tags": ["质量好", "发货快"],            // 可选，评价标签
  "images": [                              // 可选，评价图片
    "https://example.com/review/image1.jpg"
  ],
  "isAnonymous": false                     // 可选，是否匿名评价
}
```

---

### 2. 获取订单评价
**接口路径**: `GET /api/v1/orders/{id}/review`  
**接口描述**: 获取订单评价信息

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| ORDER_NOT_FOUND | 订单不存在 |
| ORDER_STATUS_ERROR | 订单状态错误 |
| ORDER_PERMISSION_DENIED | 订单访问权限不足 |
| ORDER_EXPIRED | 订单已过期 |
| ORDER_ALREADY_PAID | 订单已支付 |
| ORDER_ALREADY_CANCELLED | 订单已取消 |
| ORDER_CANNOT_CANCEL | 订单无法取消 |
| INSUFFICIENT_STOCK | 库存不足 |
| PAYMENT_FAILED | 支付失败 |
| SHIPPING_INFO_INCOMPLETE | 收货信息不完整 |
| RETURN_NOT_ALLOWED | 不允许退货 |
| RETURN_TIME_EXPIRED | 退货时间已过期 |
| LOGISTICS_UPDATE_FAILED | 物流信息更新失败 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0