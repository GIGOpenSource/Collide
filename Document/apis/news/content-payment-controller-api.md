# Content Payment Controller REST API 文档

**控制器**: ContentPaymentController  
**版本**: 2.0.0 (内容付费版)  
**基础路径**: `/api/content/payment`  
**接口数量**: 45个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容付费管理控制器提供付费配置的管理、查询和统计接口。支持多种付费模式，包括免费、金币付费、VIP免费、VIP专享等，具备完整的权限验证、推荐排行和统计分析功能。

**付费类型**:
- `FREE` - 完全免费
- `COIN_PAY` - 金币付费
- `VIP_FREE` - VIP用户免费
- `VIP_ONLY` - VIP专享内容

**有效期类型**:
- `PERMANENT` - 永久有效
- `TIME_LIMITED` - 限时有效

## 📋 接口分类

| 分类 | 接口数量 | 功能描述 |
|------|----------|----------|
| **基础CRUD** | 4个 | 获取、删除付费配置 |
| **查询功能** | 13个 | 按类型、价格、状态等条件查询 |
| **销售统计管理** | 2个 | 更新、重置销售统计 |
| **状态管理** | 3个 | 批量更新、启用、禁用配置 |
| **权限验证** | 5个 | 购买权限、访问权限、价格计算 |
| **推荐功能** | 6个 | 热门、高价值、性价比排行 |
| **统计分析** | 8个 | 各类统计和数据分析 |
| **数据同步** | 4个 | 状态同步、收益分析 |

---

## 🔧 1. 基础CRUD (4个接口)

### 1.1 获取付费配置

**接口**: `GET /api/content/payment/{id}`

**描述**: 根据配置ID获取付费配置详情

**路径参数**:
- `id` (Long): 配置ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "contentId": 67890,
    "contentTitle": "我的玄幻小说",
    "paymentType": "COIN_PAY",
    "price": 100,
    "originalPrice": 150,
    "discountRate": 0.67,
    "isTrialEnabled": true,
    "trialChapterCount": 3,
    "validityType": "PERMANENT",
    "validityDays": null,
    "salesCount": 500,
    "totalRevenue": 50000,
    "status": "ACTIVE",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

**错误处理**:
- `PAYMENT_CONFIG_NOT_FOUND`: 付费配置不存在

### 1.2 获取内容付费配置

**接口**: `GET /api/content/payment/content/{contentId}`

**描述**: 根据内容ID获取付费配置

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "contentId": 67890,
    "paymentType": "COIN_PAY",
    "price": 100,
    "isTrialEnabled": true,
    "trialChapterCount": 3,
    "status": "ACTIVE"
  }
}
```

### 1.3 删除付费配置

**接口**: `DELETE /api/content/payment/{id}`

**描述**: 删除指定的付费配置

**路径参数**:
- `id` (Long): 配置ID

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

### 1.4 删除内容付费配置

**接口**: `DELETE /api/content/payment/content/{contentId}`

**描述**: 删除指定内容的付费配置

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long): 操作人ID

---

## 🔍 2. 查询功能 (13个接口)

### 2.1 按付费类型查询

**接口**: `GET /api/content/payment/payment-type/{paymentType}`

**描述**: 根据付费类型查询配置列表

**路径参数**:
- `paymentType` (String): 付费类型 (FREE/COIN_PAY/VIP_FREE/VIP_ONLY)

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
      "paymentType": "COIN_PAY",
      "price": 100,
      "salesCount": 500,
      "status": "ACTIVE"
    }
  ]
}
```

### 2.2 查询免费内容配置

**接口**: `GET /api/content/payment/free`

**描述**: 分页查询免费内容配置

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
        "contentTitle": "免费小说",
        "paymentType": "FREE",
        "price": 0,
        "viewCount": 10000,
        "status": "ACTIVE"
      }
    ],
    "totalCount": 50,
    "currentPage": 1,
    "pageSize": 20
  }
}
```

### 2.3 查询金币付费配置

**接口**: `GET /api/content/payment/coin-pay`

**描述**: 分页查询金币付费内容配置

### 2.4 查询VIP免费配置

**接口**: `GET /api/content/payment/vip-free`

**描述**: 分页查询VIP免费内容配置

### 2.5 查询VIP专享配置

**接口**: `GET /api/content/payment/vip-only`

**描述**: 分页查询VIP专享内容配置

### 2.6 按价格范围查询

**接口**: `GET /api/content/payment/price-range`

**描述**: 根据价格范围查询配置

**查询参数**:
- `minPrice` (Long, 可选): 最低价格
- `maxPrice` (Long, 可选): 最高价格

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 12345,
      "contentId": 67890,
      "contentTitle": "中等价位小说",
      "paymentType": "COIN_PAY",
      "price": 100,
      "originalPrice": 150,
      "discountRate": 0.67,
      "salesCount": 300
    }
  ]
}
```

### 2.7 查询试读配置

**接口**: `GET /api/content/payment/trial-enabled`

**描述**: 分页查询支持试读的内容配置

### 2.8 查询永久配置

**接口**: `GET /api/content/payment/permanent`

**描述**: 分页查询永久有效的内容配置

### 2.9 查询限时配置

**接口**: `GET /api/content/payment/time-limited`

**描述**: 分页查询限时内容配置

### 2.10 查询折扣配置

**接口**: `GET /api/content/payment/discounted`

**描述**: 分页查询有折扣的内容配置

### 2.11 按状态查询配置

**接口**: `GET /api/content/payment/status/{status}`

**描述**: 根据状态查询配置列表

**路径参数**:
- `status` (String): 配置状态 (ACTIVE/INACTIVE/DELETED)

---

## 📊 3. 销售统计管理 (2个接口)

### 3.1 更新销售统计

**接口**: `PUT /api/content/payment/content/{contentId}/sales-stats`

**描述**: 更新指定内容的销售统计

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `salesIncrement` (Long): 销售增量
- `revenueIncrement` (Long): 收入增量

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 3.2 重置销售统计

**接口**: `PUT /api/content/payment/content/{contentId}/reset-sales-stats`

**描述**: 重置指定内容的销售统计

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## ⚙️ 4. 状态管理 (3个接口)

### 4.1 批量更新状态

**接口**: `PUT /api/content/payment/batch-status`

**描述**: 批量更新付费配置状态

**查询参数**:
- `contentIds` (List<Long>): 内容ID列表
- `status` (String): 目标状态

**请求示例**:
```
PUT /api/content/payment/batch-status?contentIds=67890,67891,67892&status=ACTIVE
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.2 启用付费配置

**接口**: `PUT /api/content/payment/content/{contentId}/enable`

**描述**: 启用指定内容的付费配置

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `operatorId` (Long): 操作人ID

### 4.3 禁用付费配置

**接口**: `PUT /api/content/payment/content/{contentId}/disable`

**描述**: 禁用指定内容的付费配置

---

## 🔐 5. 权限验证 (5个接口)

### 5.1 检查购买权限

**接口**: `GET /api/content/payment/check-purchase-permission`

**描述**: 检查用户是否有权限购买指定内容

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

### 5.2 检查免费访问权限

**接口**: `GET /api/content/payment/check-free-access`

**描述**: 检查用户是否可以免费访问指定内容

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": false
}
```

### 5.3 获取访问策略

**接口**: `GET /api/content/payment/access-policy`

**描述**: 获取用户对指定内容的访问策略

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "canAccess": false,
    "accessType": "PURCHASE_REQUIRED",
    "needPurchase": true,
    "paymentType": "COIN_PAY",
    "price": 100,
    "originalPrice": 150,
    "discountRate": 0.67,
    "isTrialEnabled": true,
    "trialChapters": 3,
    "userBalance": 500,
    "sufficientBalance": true,
    "reason": "需要购买后访问"
  }
}
```

### 5.4 计算实际价格

**接口**: `GET /api/content/payment/calculate-price`

**描述**: 计算用户购买指定内容的实际价格

**查询参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 80
}
```

### 5.5 获取价格信息

**接口**: `GET /api/content/payment/content/{contentId}/price-info`

**描述**: 获取指定内容的价格信息

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentType": "COIN_PAY",
    "price": 100,
    "originalPrice": 150,
    "discountRate": 0.67,
    "discountAmount": 50,
    "isTrialEnabled": true,
    "trialChapterCount": 3,
    "validityType": "PERMANENT",
    "validityDays": null,
    "salesCount": 500,
    "avgRating": 8.5,
    "description": "高质量付费内容"
  }
}
```

---

## 🏆 6. 推荐功能 (6个接口)

### 6.1 获取热门付费内容

**接口**: `GET /api/content/payment/hot`

**描述**: 获取热门付费内容排行

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
      "paymentType": "COIN_PAY",
      "price": 100,
      "salesCount": 1000,
      "totalRevenue": 100000,
      "rating": 9.2,
      "hotScore": 95.5
    }
  ]
}
```

### 6.2 获取高价值内容

**接口**: `GET /api/content/payment/high-value`

**描述**: 获取高价值内容排行

### 6.3 获取性价比内容

**接口**: `GET /api/content/payment/value-for-money`

**描述**: 获取性价比内容排行

### 6.4 获取新付费内容

**接口**: `GET /api/content/payment/new`

**描述**: 获取新上线的付费内容

### 6.5 获取销售排行榜

**接口**: `GET /api/content/payment/sales-ranking`

**描述**: 获取内容销售排行榜

### 6.6 获取收入排行榜

**接口**: `GET /api/content/payment/revenue-ranking`

**描述**: 获取内容收入排行榜

---

## 📈 7. 统计分析 (8个接口)

### 7.1 统计付费类型

**接口**: `GET /api/content/payment/stats/payment-type`

**描述**: 统计各付费类型的数量

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "FREE": 100,
    "COIN_PAY": 250,
    "VIP_FREE": 80,
    "VIP_ONLY": 30
  }
}
```

### 7.2 统计活跃配置数

**接口**: `GET /api/content/payment/stats/active-count`

**描述**: 统计活跃的付费配置数量

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": 380
}
```

### 7.3 获取价格统计

**接口**: `GET /api/content/payment/stats/price`

**描述**: 获取价格统计信息

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "avgPrice": 85.5,
    "maxPrice": 500,
    "minPrice": 10,
    "medianPrice": 80,
    "priceRanges": {
      "0-50": 100,
      "51-100": 150,
      "101-200": 80,
      "201+": 20
    }
  }
}
```

### 7.4 获取总销售统计

**接口**: `GET /api/content/payment/stats/total-sales`

**描述**: 获取总销售统计信息

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalSales": 5000,
    "totalRevenue": 500000,
    "avgRevenuePerSale": 100,
    "topSellingContent": {
      "contentId": 67890,
      "title": "热门小说",
      "sales": 1000
    }
  }
}
```

### 7.5 获取月度销售统计

**接口**: `GET /api/content/payment/stats/monthly-sales`

**描述**: 获取近期月度销售统计

**查询参数**:
- `months` (Integer, 默认12): 月份数

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "month": "2024-01",
      "sales": 500,
      "revenue": 50000,
      "newConfigs": 20,
      "avgPrice": 100
    },
    {
      "month": "2024-02",
      "sales": 600,
      "revenue": 60000,
      "newConfigs": 25,
      "avgPrice": 100
    }
  ]
}
```

### 7.6 获取转化率统计

**接口**: `GET /api/content/payment/stats/conversion`

**描述**: 获取付费转化率统计

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "overallConversionRate": 0.15,
    "trialConversionRate": 0.25,
    "byPaymentType": {
      "COIN_PAY": 0.18,
      "VIP_ONLY": 0.45
    },
    "byPriceRange": {
      "0-50": 0.22,
      "51-100": 0.15,
      "101-200": 0.08,
      "201+": 0.03
    }
  }
}
```

---

## 🔄 8. 数据同步 (4个接口)

### 8.1 同步内容状态

**接口**: `PUT /api/content/payment/sync/content/{contentId}/status`

**描述**: 同步内容状态到付费配置

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `contentStatus` (String): 内容状态

### 8.2 批量同步内容状态

**接口**: `PUT /api/content/payment/sync/batch-content-status`

**描述**: 批量同步内容状态到付费配置

**请求体**:
```json
{
  "67890": "PUBLISHED",
  "67891": "OFFLINE",
  "67892": "PUBLISHED"
}
```

### 8.3 获取收益分析

**接口**: `GET /api/content/payment/content/{contentId}/revenue-analysis`

**描述**: 获取指定内容的收益分析

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalRevenue": 100000,
    "salesCount": 1000,
    "avgRevenue": 100,
    "dailyRevenue": [
      {
        "date": "2024-01-01",
        "revenue": 1000,
        "sales": 10
      }
    ],
    "revenueGrowth": 0.15,
    "marketRanking": 15,
    "competitorComparison": {
      "avgMarketPrice": 120,
      "priceAdvantage": -20
    }
  }
}
```

### 8.4 获取价格优化建议

**接口**: `GET /api/content/payment/content/{contentId}/price-optimization`

**描述**: 获取指定内容的价格优化建议

**路径参数**:
- `contentId` (Long): 内容ID

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "currentPrice": 100,
    "suggestedPrice": 120,
    "priceChangeReason": "基于质量和市场对比，建议提价",
    "expectedImpact": {
      "revenueIncrease": 0.15,
      "salesDecrease": 0.05,
      "netBenefit": 0.12
    },
    "marketAnalysis": {
      "similarContentAvgPrice": 125,
      "competitorPrices": [110, 115, 130],
      "demandLevel": "HIGH"
    },
    "recommendations": [
      "价格略低于市场平均水平，可适当提价",
      "建议增加限时折扣活动提升销量",
      "考虑推出VIP专享版本"
    ]
  }
}
```

---

## 🎯 数据模型

### ContentPaymentConfigResponse 付费配置响应对象
```json
{
  "id": "配置ID",
  "contentId": "内容ID",
  "contentTitle": "内容标题",
  "paymentType": "付费类型（FREE/COIN_PAY/VIP_FREE/VIP_ONLY）",
  "price": "价格（金币）",
  "originalPrice": "原价",
  "discountRate": "折扣率",
  "discountAmount": "折扣金额",
  "isTrialEnabled": "是否支持试读",
  "trialChapterCount": "试读章节数",
  "validityType": "有效期类型（PERMANENT/TIME_LIMITED）",
  "validityDays": "有效天数",
  "salesCount": "销售数量",
  "totalRevenue": "总收入",
  "status": "状态（ACTIVE/INACTIVE/DELETED）",
  "createTime": "创建时间",
  "updateTime": "更新时间"
}
```

### AccessPolicy 访问策略对象
```json
{
  "canAccess": "是否可以访问",
  "accessType": "访问类型",
  "needPurchase": "是否需要购买",
  "paymentType": "付费类型",
  "price": "价格",
  "originalPrice": "原价",
  "discountRate": "折扣率",
  "isTrialEnabled": "是否支持试读",
  "trialChapters": "试读章节数",
  "userBalance": "用户余额",
  "sufficientBalance": "余额是否充足",
  "reason": "访问策略说明"
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| PAYMENT_CONFIG_NOT_FOUND | 付费配置不存在 | 检查配置ID或内容ID |
| INVALID_PAYMENT_TYPE | 无效的付费类型 | 检查付费类型值 |
| INVALID_PRICE_RANGE | 无效的价格范围 | 检查价格范围参数 |
| INSUFFICIENT_PERMISSION | 权限不足 | 确认操作权限 |
| SALES_STATS_UPDATE_FAILED | 销售统计更新失败 | 检查统计参数 |
| BATCH_UPDATE_FAILED | 批量更新失败 | 检查批量操作参数 |
| PURCHASE_PERMISSION_DENIED | 购买权限被拒绝 | 检查用户状态和内容状态 |
| INSUFFICIENT_BALANCE | 余额不足 | 用户需要充值 |
| CONTENT_NOT_FOR_SALE | 内容不可购买 | 检查内容状态 |
| PRICE_CALCULATION_FAILED | 价格计算失败 | 检查用户和内容信息 |

## 📈 使用场景

### 1. 内容购买流程
```javascript
// 检查购买权限和价格
const checkPurchase = async (userId, contentId) => {
  // 获取访问策略
  const policyResponse = await fetch(
    `/api/content/payment/access-policy?userId=${userId}&contentId=${contentId}`
  );
  const policy = await policyResponse.json();
  
  if (policy.data.canAccess) {
    return { canAccess: true, needPurchase: false };
  }
  
  if (policy.data.needPurchase) {
    // 计算实际价格
    const priceResponse = await fetch(
      `/api/content/payment/calculate-price?userId=${userId}&contentId=${contentId}`
    );
    const actualPrice = await priceResponse.json();
    
    return {
      canAccess: false,
      needPurchase: true,
      price: actualPrice.data,
      policy: policy.data
    };
  }
  
  return { canAccess: false, needPurchase: false };
};
```

### 2. 推荐系统
```javascript
// 获取推荐内容
const getRecommendations = async () => {
  const [hot, valueForMoney, newContent] = await Promise.all([
    fetch('/api/content/payment/hot?limit=5'),
    fetch('/api/content/payment/value-for-money?limit=5'),
    fetch('/api/content/payment/new?limit=5')
  ]);
  
  return {
    hot: await hot.json(),
    valueForMoney: await valueForMoney.json(),
    new: await newContent.json()
  };
};
```

### 3. 管理后台统计
```javascript
// 获取销售统计面板
const getSalesStatistics = async () => {
  const [totalStats, monthlyStats, conversionStats] = await Promise.all([
    fetch('/api/content/payment/stats/total-sales'),
    fetch('/api/content/payment/stats/monthly-sales?months=6'),
    fetch('/api/content/payment/stats/conversion')
  ]);
  
  return {
    total: await totalStats.json(),
    monthly: await monthlyStats.json(),
    conversion: await conversionStats.json()
  };
};
```

## 🔧 性能优化建议

1. **缓存策略**: 付费配置、价格信息建议使用Redis缓存，TTL设置为30分钟
2. **权限验证优化**: 用户权限信息可以缓存到会话中，减少重复查询
3. **统计数据优化**: 销售统计可以通过定时任务预计算并缓存
4. **推荐算法优化**: 推荐排行可以异步计算，定时更新缓存
5. **价格计算优化**: 复杂的价格计算逻辑可以使用分布式缓存

## 🔗 相关文档

- [ContentPurchaseController API 文档](./content-purchase-controller-api.md)
- [ContentPaymentFacadeService 文档](./content-payment-facade-service-api.md)
- [付费系统设计](../design/payment-system-design.md)
- [权限验证机制](../design/permission-design.md)

---

**联系信息**:  
- 控制器: ContentPaymentController  
- 版本: 2.0.0 (内容付费版)  
- 维护: GIG团队  
- 更新: 2024-01-31