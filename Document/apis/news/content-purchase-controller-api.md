# Content Purchase Controller REST API 文档

**控制器**: ContentPurchaseController  
**版本**: 2.0.0 (内容付费版)  
**基础路径**: `/api/content/purchase`  
**接口数量**: 35个  
**更新时间**: 2024-01-31  

## 🚀 概述

用户内容购买记录管理控制器提供购买记录的管理、查询和统计接口。支持购买记录的全生命周期管理，包括权限验证、访问统计、过期处理、退款管理等功能。

**核心功能**:
- **权限管理**: 购买权限验证、访问权限检查
- **记录查询**: 多维度购买记录查询
- **访问统计**: 用户访问行为记录和统计
- **生命周期**: 过期处理、退款管理
- **数据分析**: 用户消费分析、内容销售统计

**购买状态**:
- `ACTIVE` - 有效状态
- `EXPIRED` - 已过期
- `REFUNDED` - 已退款
- `CANCELLED` - 已取消

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **基础CRUD** | 2个 | 获取、删除购买记录 |
| **权限验证** | 4个 | 访问权限检查、批量权限验证 |
| **查询功能** | 11个 | 多维度购买记录查询 |
| **访问和状态管理** | 8个 | 访问记录、状态更新、过期处理 |
| **统计分析** | 9个 | 购买统计、收入分析、排行榜 |
| **业务逻辑** | 4个 | 订单处理、权限计算、推荐 |

---

## 🔧 1. 基础CRUD (2个接口)

### 1.1 获取购买记录

**接口**: `GET /api/content/purchase/{id}`

**描述**: 根据购买记录ID获取详情

**路径参数**:
- `id` (Long): 购买记录ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "userId": 1001,
    "userNickname": "用户昵称",
    "contentId": 67890,
    "contentTitle": "我的玄幻小说",
    "contentType": "NOVEL",
    "authorId": 2001,
    "authorNickname": "知名作家",
    "orderId": 111222,
    "orderNo": "ORDER2024010112345",
    "paymentType": "COIN_PAY",
    "originalPrice": 100,
    "actualPrice": 80,
    "discountAmount": 20,
    "discountReason": "VIP折扣",
    "status": "ACTIVE",
    "purchaseTime": "2024-01-01T10:00:00",
    "expiryTime": "2024-12-31T23:59:59",
    "accessCount": 15,
    "lastAccessTime": "2024-01-15T14:30:00",
    "isRead": true,
    "totalReadTime": 7200,
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

**错误处理**:
- `PURCHASE_RECORD_NOT_FOUND`: 购买记录不存在

### 1.2 删除购买记录

**接口**: `DELETE /api/content/purchase/{id}`

**描述**: 逻辑删除指定的购买记录

**路径参数**:
- `id` (Long): 购买记录ID

**查询参数**:
- `operatorId` (Long): 操作人ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误处理**:
- `DELETE_PURCHASE_FAILED`: 删除购买记录失败

---

## 🔐 2. 权限验证 (4个接口)

### 2.1 获取用户内容购买记录

**接口**: `GET /api/content/purchase/user/{userId}/content/{contentId}`

**描述**: 获取用户对指定内容的购买记录

**路径参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "userId": 1001,
    "contentId": 67890,
    "status": "ACTIVE",
    "purchaseTime": "2024-01-01T10:00:00",
    "expiryTime": "2024-12-31T23:59:59",
    "accessCount": 15,
    "lastAccessTime": "2024-01-15T14:30:00"
  }
}
```

**错误处理**:
- `USER_CONTENT_PURCHASE_NOT_FOUND`: 用户内容购买记录不存在

### 2.2 检查访问权限

**接口**: `GET /api/content/purchase/check-access`

**描述**: 检查用户是否有权限访问指定内容

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

**错误处理**:
- `ACCESS_PERMISSION_CHECK_FAILED`: 访问权限检查失败

### 2.3 获取有效购买记录

**接口**: `GET /api/content/purchase/valid-purchase`

**描述**: 获取用户对指定内容的有效购买记录

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "userId": 1001,
    "contentId": 67890,
    "status": "ACTIVE",
    "remainingDays": 350,
    "accessCount": 15,
    "isUnlimited": true
  }
}
```

### 2.4 批量检查访问权限

**接口**: `POST /api/content/purchase/batch-check-access`

**描述**: 批量检查用户对多个内容的访问权限

**查询参数**:
- `userId` (Long): 用户ID

**请求体**:
```json
[67890, 67891, 67892, 67893, 67894]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "67890": true,
    "67891": false,
    "67892": true,
    "67893": false,
    "67894": true
  }
}
```

**错误处理**:
- `BATCH_ACCESS_CHECK_FAILED`: 批量访问权限检查失败

---

## 🔍 3. 查询功能 (11个接口)

### 3.1 查询用户购买记录

**接口**: `GET /api/content/purchase/user/{userId}`

**描述**: 分页查询用户的购买记录

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 12345,
        "contentId": 67890,
        "contentTitle": "我的玄幻小说",
        "contentType": "NOVEL",
        "authorNickname": "知名作家",
        "paymentType": "COIN_PAY",
        "actualPrice": 80,
        "status": "ACTIVE",
        "purchaseTime": "2024-01-01T10:00:00",
        "accessCount": 15,
        "isRead": true
      }
    ],
    "totalCount": 25,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 3.2 查询用户有效购买记录

**接口**: `GET /api/content/purchase/user/{userId}/valid`

**描述**: 查询用户的有效购买记录

**路径参数**:
- `userId` (Long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12345,
      "contentId": 67890,
      "contentTitle": "我的玄幻小说",
      "status": "ACTIVE",
      "purchaseTime": "2024-01-01T10:00:00",
      "expiryTime": "2024-12-31T23:59:59",
      "remainingDays": 350
    }
  ]
}
```

### 3.3 查询内容购买记录

**接口**: `GET /api/content/purchase/content/{contentId}`

**描述**: 分页查询指定内容的购买记录

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

### 3.4 根据订单ID查询

**接口**: `GET /api/content/purchase/order/{orderId}`

**描述**: 根据订单ID查询购买记录

**路径参数**:
- `orderId` (Long): 订单ID

### 3.5 根据订单号查询

**接口**: `GET /api/content/purchase/order-no/{orderNo}`

**描述**: 根据订单号查询购买记录

**路径参数**:
- `orderNo` (String): 订单号

### 3.6 查询用户指定类型购买

**接口**: `GET /api/content/purchase/user/{userId}/content-type/{contentType}`

**描述**: 查询用户购买的指定类型内容

**路径参数**:
- `userId` (Long): 用户ID
- `contentType` (String): 内容类型 (NOVEL/COMIC/VIDEO/AUDIO)

### 3.7 查询用户指定作者购买

**接口**: `GET /api/content/purchase/user/{userId}/author/{authorId}`

**描述**: 查询用户购买的指定作者内容

**路径参数**:
- `userId` (Long): 用户ID
- `authorId` (Long): 作者ID

### 3.8 查询用户最近购买

**接口**: `GET /api/content/purchase/user/{userId}/recent`

**描述**: 查询用户最近的购买记录

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `limit` (Integer, 默认10): 返回数量限制

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12345,
      "contentId": 67890,
      "contentTitle": "最新购买的小说",
      "contentType": "NOVEL",
      "purchaseTime": "2024-01-15T14:30:00",
      "actualPrice": 80,
      "status": "ACTIVE"
    }
  ]
}
```

### 3.9 查询用户未读购买

**接口**: `GET /api/content/purchase/user/{userId}/unread`

**描述**: 查询用户购买但未阅读的内容

**路径参数**:
- `userId` (Long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12345,
      "contentId": 67890,
      "contentTitle": "未读小说",
      "contentType": "NOVEL",
      "purchaseTime": "2024-01-10T10:00:00",
      "daysSincePurchase": 5,
      "isRead": false
    }
  ]
}
```

### 3.10 查询高价值购买记录

**接口**: `GET /api/content/purchase/high-value`

**描述**: 查询高消费金额的购买记录

**查询参数**:
- `minAmount` (Long): 最低金额
- `limit` (Integer, 默认10): 返回数量限制

### 3.11 查询用户高价值购买

**接口**: `GET /api/content/purchase/user/{userId}/high-value`

**描述**: 查询用户的高价值购买记录

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `minAmount` (Long): 最低金额

---

## 📊 4. 访问和状态管理 (8个接口)

### 4.1 查询最受欢迎购买

**接口**: `GET /api/content/purchase/most-accessed`

**描述**: 查询访问次数最多的购买记录

**查询参数**:
- `limit` (Integer, 默认10): 返回数量限制

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12345,
      "contentId": 67890,
      "contentTitle": "热门小说",
      "accessCount": 50,
      "totalUsers": 1000,
      "avgAccessPerUser": 5.2,
      "popularityScore": 95.5
    }
  ]
}
```

### 4.2 查询用户最近访问

**接口**: `GET /api/content/purchase/user/{userId}/recent-accessed`

**描述**: 查询用户最近访问的购买记录

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `limit` (Integer, 默认10): 返回数量限制

### 4.3 获取用户优惠统计

**接口**: `GET /api/content/purchase/user/{userId}/discount-stats`

**描述**: 获取用户的优惠统计信息

**路径参数**:
- `userId` (Long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSavings": 500,
    "vipSavings": 300,
    "promotionSavings": 200,
    "avgDiscountRate": 0.15,
    "totalPurchases": 25,
    "discountedPurchases": 20,
    "maxSavingsInSinglePurchase": 50,
    "savingsHistory": [
      {
        "month": "2024-01",
        "savings": 150,
        "purchases": 8
      }
    ]
  }
}
```

### 4.4 记录内容访问

**接口**: `POST /api/content/purchase/record-access`

**描述**: 记录用户访问内容

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.5 批量更新访问统计

**接口**: `PUT /api/content/purchase/batch-update-access-stats`

**描述**: 批量更新购买记录的访问统计

**请求体**:
```json
[12345, 12346, 12347]
```

### 4.6 处理过期购买记录

**接口**: `POST /api/content/purchase/process-expired`

**描述**: 处理过期的购买记录

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 25
}
```

### 4.7 查询即将过期购买

**接口**: `GET /api/content/purchase/expiring-soon`

**描述**: 查询即将过期的购买记录

**查询参数**:
- `beforeTime` (LocalDateTime): 过期时间点

### 4.8 查询已过期购买

**接口**: `GET /api/content/purchase/expired`

**描述**: 查询已过期的购买记录

---

## 📈 5. 统计分析 (9个接口)

### 5.1 批量更新状态

**接口**: `PUT /api/content/purchase/batch-status`

**描述**: 批量更新购买记录状态

**查询参数**:
- `ids` (List<Long>): 购买记录ID列表
- `status` (String): 目标状态

### 5.2 退款处理

**接口**: `PUT /api/content/purchase/{purchaseId}/refund`

**描述**: 处理购买记录的退款申请

**路径参数**:
- `purchaseId` (Long): 购买记录ID

**查询参数**:
- `reason` (String): 退款原因
- `operatorId` (Long): 操作人ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 5.3 统计用户购买总数

**接口**: `GET /api/content/purchase/user/{userId}/count`

**描述**: 统计用户的购买总数

**路径参数**:
- `userId` (Long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 25
}
```

### 5.4 统计用户有效购买数

**接口**: `GET /api/content/purchase/user/{userId}/valid-count`

**描述**: 统计用户的有效购买数量

### 5.5 统计内容购买总数

**接口**: `GET /api/content/purchase/content/{contentId}/count`

**描述**: 统计内容的购买总数

### 5.6 统计内容收入

**接口**: `GET /api/content/purchase/content/{contentId}/revenue`

**描述**: 统计内容的收入总额

### 5.7 统计用户消费

**接口**: `GET /api/content/purchase/user/{userId}/expense`

**描述**: 统计用户的消费总额

### 5.8 获取热门内容排行

**接口**: `GET /api/content/purchase/popular-ranking`

**描述**: 获取热门购买内容排行榜

**查询参数**:
- `limit` (Integer, 默认10): 返回数量限制

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "contentId": 67890,
      "contentTitle": "热门小说第一名",
      "contentType": "NOVEL",
      "authorNickname": "知名作家",
      "purchaseCount": 1000,
      "totalRevenue": 100000,
      "avgPrice": 100,
      "rating": 9.2,
      "rank": 1
    }
  ]
}
```

### 5.9 获取用户购买统计

**接口**: `GET /api/content/purchase/user/{userId}/stats`

**描述**: 获取用户的购买统计信息

**路径参数**:
- `userId` (Long): 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalPurchases": 25,
    "validPurchases": 20,
    "expiredPurchases": 3,
    "refundedPurchases": 2,
    "totalExpense": 2500,
    "totalSavings": 500,
    "avgExpensePerPurchase": 100,
    "favoriteContentType": "NOVEL",
    "favoriteAuthor": {
      "authorId": 2001,
      "authorNickname": "知名作家",
      "purchaseCount": 8
    },
    "mostAccessedContent": {
      "contentId": 67890,
      "contentTitle": "最爱的小说",
      "accessCount": 50
    },
    "purchaseFrequency": "每月2-3次",
    "membershipLevel": "VIP",
    "joinDate": "2023-06-01",
    "monthlyStats": [
      {
        "month": "2024-01",
        "purchases": 3,
        "expense": 300,
        "savings": 50
      }
    ]
  }
}
```

---

## 🎯 6. 业务逻辑 (4个接口)

### 6.1 获取内容销售统计

**接口**: `GET /api/content/purchase/content/{contentId}/sales-stats`

**描述**: 获取内容的销售统计信息

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSales": 1000,
    "totalRevenue": 100000,
    "avgPrice": 100,
    "uniqueBuyers": 950,
    "repeatPurchaseRate": 0.05,
    "refundRate": 0.02,
    "conversionRate": 0.15,
    "salesTrend": [
      {
        "date": "2024-01-01",
        "sales": 10,
        "revenue": 1000
      }
    ],
    "buyerAnalysis": {
      "newBuyers": 900,
      "returningBuyers": 100,
      "vipBuyers": 300,
      "regularBuyers": 700
    }
  }
}
```

### 6.2 获取作者收入统计

**接口**: `GET /api/content/purchase/author/{authorId}/revenue-stats`

**描述**: 获取作者的收入统计信息

**路径参数**:
- `authorId` (Long): 作者ID

### 6.3 获取日期范围统计

**接口**: `GET /api/content/purchase/stats/date-range`

**描述**: 获取指定日期范围内的购买统计

**查询参数**:
- `startDate` (LocalDateTime): 开始时间
- `endDate` (LocalDateTime): 结束时间

### 6.4 处理订单支付成功

**接口**: `POST /api/content/purchase/handle-payment-success/{orderId}`

**描述**: 处理订单支付成功后的购买记录创建

**路径参数**:
- `orderId` (Long): 订单ID

### 6.5 验证购买权限

**接口**: `GET /api/content/purchase/validate-purchase-permission`

**描述**: 验证用户是否有权限购买指定内容

### 6.6 计算内容访问权限

**接口**: `GET /api/content/purchase/calculate-content-access`

**描述**: 计算用户对内容的访问权限详情

### 6.7 获取用户内容推荐

**接口**: `GET /api/content/purchase/user/{userId}/recommendations`

**描述**: 获取基于购买历史的内容推荐

**路径参数**:
- `userId` (Long): 用户ID

**查询参数**:
- `limit` (Integer, 默认10): 返回数量限制

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [67891, 67892, 67893, 67894, 67895]
}
```

---

## 🎯 数据模型

### ContentPurchaseResponse 购买记录响应对象
```json
{
  "id": "购买记录ID",
  "userId": "用户ID",
  "userNickname": "用户昵称",
  "contentId": "内容ID",
  "contentTitle": "内容标题",
  "contentType": "内容类型",
  "authorId": "作者ID",
  "authorNickname": "作者昵称",
  "orderId": "订单ID",
  "orderNo": "订单号",
  "paymentType": "付费类型",
  "originalPrice": "原价",
  "actualPrice": "实际支付价格",
  "discountAmount": "优惠金额",
  "discountReason": "优惠原因",
  "status": "状态（ACTIVE/EXPIRED/REFUNDED/CANCELLED）",
  "purchaseTime": "购买时间",
  "expiryTime": "过期时间",
  "accessCount": "访问次数",
  "lastAccessTime": "最后访问时间",
  "isRead": "是否已阅读",
  "totalReadTime": "总阅读时长（秒）",
  "remainingDays": "剩余天数",
  "createTime": "创建时间",
  "updateTime": "更新时间"
}
```

### UserPurchaseStats 用户购买统计对象
```json
{
  "totalPurchases": "总购买数",
  "validPurchases": "有效购买数",
  "expiredPurchases": "过期购买数",
  "refundedPurchases": "退款购买数",
  "totalExpense": "总消费金额",
  "totalSavings": "总节省金额",
  "avgExpensePerPurchase": "平均每次消费",
  "favoriteContentType": "偏爱的内容类型",
  "favoriteAuthor": "偏爱的作者",
  "mostAccessedContent": "最常访问的内容",
  "purchaseFrequency": "购买频率",
  "membershipLevel": "会员等级",
  "joinDate": "加入日期",
  "monthlyStats": "月度统计"
}
```

### ContentSalesStats 内容销售统计对象
```json
{
  "totalSales": "总销量",
  "totalRevenue": "总收入",
  "avgPrice": "平均价格",
  "uniqueBuyers": "独立买家数",
  "repeatPurchaseRate": "重复购买率",
  "refundRate": "退款率",
  "conversionRate": "转化率",
  "salesTrend": "销售趋势",
  "buyerAnalysis": "买家分析"
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| PURCHASE_RECORD_NOT_FOUND | 购买记录不存在 | 检查购买记录ID |
| DELETE_PURCHASE_FAILED | 删除购买记录失败 | 确认操作权限 |
| USER_CONTENT_PURCHASE_NOT_FOUND | 用户内容购买记录不存在 | 用户未购买该内容 |
| ACCESS_PERMISSION_CHECK_FAILED | 访问权限检查失败 | 检查用户和内容信息 |
| BATCH_ACCESS_CHECK_FAILED | 批量访问权限检查失败 | 检查请求参数 |
| RECORD_ACCESS_FAILED | 记录内容访问失败 | 检查访问参数 |
| BATCH_UPDATE_ACCESS_STATS_FAILED | 批量更新访问统计失败 | 检查购买记录ID列表 |
| PROCESS_EXPIRED_FAILED | 处理过期购买记录失败 | 系统处理异常 |
| BATCH_STATUS_UPDATE_FAILED | 批量更新状态失败 | 检查状态参数 |
| REFUND_PROCESS_FAILED | 退款处理失败 | 检查退款条件 |
| STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| PAYMENT_SUCCESS_HANDLE_FAILED | 处理订单支付成功失败 | 检查订单信息 |
| PURCHASE_PERMISSION_VALIDATION_FAILED | 购买权限验证失败 | 检查用户状态 |
| CONTENT_ACCESS_CALCULATION_FAILED | 内容访问权限计算失败 | 检查权限参数 |
| RECOMMENDATION_GENERATION_FAILED | 推荐生成失败 | 检查用户购买历史 |

## 📈 使用场景

### 1. 用户购买历史管理
```javascript
// 获取用户购买历史
const getUserPurchaseHistory = async (userId, page = 1) => {
  const response = await fetch(
    `/api/content/purchase/user/${userId}?currentPage=${page}&pageSize=20`
  );
  return response.json();
};

// 获取用户未读内容
const getUnreadContent = async (userId) => {
  const response = await fetch(
    `/api/content/purchase/user/${userId}/unread`
  );
  return response.json();
};
```

### 2. 访问权限验证
```javascript
// 检查单个内容访问权限
const checkAccess = async (userId, contentId) => {
  const response = await fetch(
    `/api/content/purchase/check-access?userId=${userId}&contentId=${contentId}`
  );
  const result = await response.json();
  return result.data;
};

// 批量检查访问权限
const batchCheckAccess = async (userId, contentIds) => {
  const response = await fetch(
    `/api/content/purchase/batch-check-access?userId=${userId}`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(contentIds)
    }
  );
  return response.json();
};
```

### 3. 统计分析面板
```javascript
// 获取用户购买统计
const getUserStats = async (userId) => {
  const response = await fetch(
    `/api/content/purchase/user/${userId}/stats`
  );
  return response.json();
};

// 获取热门内容排行
const getPopularRanking = async (limit = 10) => {
  const response = await fetch(
    `/api/content/purchase/popular-ranking?limit=${limit}`
  );
  return response.json();
};
```

### 4. 推荐系统集成
```javascript
// 获取个性化推荐
const getRecommendations = async (userId, limit = 10) => {
  const response = await fetch(
    `/api/content/purchase/user/${userId}/recommendations?limit=${limit}`
  );
  return response.json();
};
```

## 🔧 性能优化建议

1. **缓存策略**: 用户购买记录、访问权限建议使用Redis缓存，TTL设置为10分钟
2. **批量操作优化**: 批量权限检查使用并行处理，提升响应速度
3. **统计数据优化**: 复杂统计可以通过定时任务预计算并缓存
4. **访问记录优化**: 访问行为可以异步记录，避免影响用户体验
5. **推荐算法优化**: 基于用户购买历史的推荐可以离线计算

## 🔗 相关文档

- [ContentPaymentController API 文档](./content-payment-controller-api.md)
- [ContentPurchaseFacadeService 文档](./content-purchase-facade-service-api.md)
- [购买流程设计](../design/purchase-flow-design.md)
- [用户推荐算法](../algorithm/user-recommendation.md)

---

**联系信息**:  
- 控制器: ContentPurchaseController  
- 版本: 2.0.0 (内容付费版)  
- 维护: GIG团队  
- 更新: 2024-01-31