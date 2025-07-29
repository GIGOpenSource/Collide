# Collide 商品服务 API 文档

## 概述

Collide 商品服务提供完整的商品管理功能，包括商品发布、管理、查询、库存管理、价格管理等核心功能。支持多种商品类型、规格管理、促销活动等特性。

**服务版本**: v1.0.0  
**基础路径**: `/api/v1/goods`  
**Dubbo服务**: `collide-goods`  
**设计理念**: 灵活的商品管理系统，支持多样化商品形态，提供完整的电商商品管理能力

---

## 商品管理 API

### 1. 创建商品
**接口路径**: `POST /api/v1/goods`  
**接口描述**: 创建新商品

#### 请求参数
```json
{
  "name": "Java编程实战教程",                     // 必填，商品名称
  "description": "从零基础到高级开发的完整教程",    // 可选，商品描述
  "categoryId": 1001,                           // 必填，商品分类ID
  "brandId": 2001,                              // 可选，品牌ID
  "goodsType": "digital",                       // 必填，商品类型：physical/digital/service
  "price": 99.00,                               // 必填，商品价格
  "originalPrice": 129.00,                      // 可选，原价
  "costPrice": 50.00,                           // 可选，成本价
  "currency": "CNY",                            // 可选，货币单位，默认CNY
  "stock": 1000,                                // 必填，库存数量
  "minStock": 10,                               // 可选，最低库存警戒线
  "maxStock": 10000,                            // 可选，最大库存
  "unit": "份",                                 // 可选，商品单位
  "weight": 0,                                  // 可选，重量（克）
  "volume": 0,                                  // 可选，体积（立方厘米）
  "images": [                                   // 可选，商品图片
    "https://example.com/goods/image1.jpg",
    "https://example.com/goods/image2.jpg"
  ],
  "mainImage": "https://example.com/goods/main.jpg", // 可选，主图
  "videoUrl": "https://example.com/goods/video.mp4", // 可选，视频介绍
  "specifications": [                           // 可选，商品规格
    {
      "name": "版本",
      "value": "标准版"
    },
    {
      "name": "格式",
      "value": "PDF+视频"
    }
  ],
  "tags": ["Java", "编程", "教程"],              // 可选，商品标签
  "sellerId": 12345,                            // 必填，卖家ID
  "status": "draft",                            // 可选，状态：draft/published/offline
  "isVirtual": true,                            // 可选，是否虚拟商品
  "needShipping": false,                        // 可选，是否需要配送
  "shippingTemplateId": null,                   // 可选，配送模板ID
  "saleStartTime": "2024-01-16T00:00:00",       // 可选，开售时间
  "saleEndTime": "2024-12-31T23:59:59"          // 可选，停售时间
}
```

#### 响应示例
**成功响应 (200)**:
```json
{
  "success": true,
  "responseCode": "SUCCESS",
  "responseMessage": "商品创建成功",
  "data": {
    "id": 789012,
    "name": "Java编程实战教程",
    "description": "从零基础到高级开发的完整教程",
    "categoryId": 1001,
    "categoryName": "编程教程",
    "brandId": 2001,
    "brandName": "技术出版社",
    "goodsType": "digital",
    "price": 99.00,
    "originalPrice": 129.00,
    "costPrice": 50.00,
    "currency": "CNY",
    "stock": 1000,
    "soldCount": 0,
    "viewCount": 0,
    "favoriteCount": 0,
    "reviewCount": 0,
    "averageRating": 0.0,
    "status": "draft",
    "isVirtual": true,
    "needShipping": false,
    "sellerId": 12345,
    "sellerName": "技术达人",
    "createTime": "2024-01-16T10:30:00",
    "updateTime": "2024-01-16T10:30:00"
  }
}
```

---

### 2. 更新商品
**接口路径**: `PUT /api/v1/goods/{id}`  
**接口描述**: 更新商品信息

#### 请求参数
- **id** (path): 商品ID，必填

```json
{
  "name": "Java编程实战教程 - 升级版",           // 可选，更新商品名称
  "price": 89.00,                             // 可选，更新价格
  "stock": 1500,                              // 可选，更新库存
  "description": "全新升级的完整教程",          // 可选，更新描述
  "status": "published"                        // 可选，更新状态
}
```

---

### 3. 删除商品
**接口路径**: `DELETE /api/v1/goods/{id}`  
**接口描述**: 删除商品（逻辑删除）

#### 请求参数
- **id** (path): 商品ID，必填
- **sellerId** (query): 卖家ID，必填

---

### 4. 获取商品详情
**接口路径**: `GET /api/v1/goods/{id}`  
**接口描述**: 获取商品详细信息

#### 请求参数
- **id** (path): 商品ID，必填
- **viewerId** (query): 查看者ID，可选（用于个性化推荐）

---

### 5. 查询商品列表
**接口路径**: `POST /api/v1/goods/query`  
**接口描述**: 分页查询商品列表

#### 请求参数
```json
{
  "pageNum": 1,                    // 页码（从1开始）
  "pageSize": 20,                  // 每页大小
  "keyword": "Java",               // 可选，关键词搜索
  "categoryId": 1001,              // 可选，分类ID
  "brandId": 2001,                 // 可选，品牌ID
  "sellerId": 12345,               // 可选，卖家ID
  "goodsType": "digital",          // 可选，商品类型
  "status": "published",           // 可选，状态
  "minPrice": 50.00,               // 可选，最低价格
  "maxPrice": 200.00,              // 可选，最高价格
  "hasStock": true,                // 可选，是否有库存
  "tags": ["Java", "编程"],        // 可选，标签筛选
  "orderBy": "createTime",         // 可选，排序字段
  "orderDirection": "DESC"         // 可选，排序方向
}
```

---

## 库存管理 API

### 1. 更新库存
**接口路径**: `POST /api/v1/goods/{id}/stock`  
**接口描述**: 更新商品库存

#### 请求参数
- **id** (path): 商品ID，必填

```json
{
  "operation": "increase",         // 必填，操作类型：increase/decrease/set
  "quantity": 100,                 // 必填，数量
  "reason": "补货",                // 必填，操作原因
  "operatorId": 67890,             // 必填，操作人ID
  "sourceOrderId": null,           // 可选，关联订单ID
  "batchCode": "BATCH-2024-001"    // 可选，批次号
}
```

#### 响应示例
```json
{
  "success": true,
  "data": {
    "goodsId": 789012,
    "beforeStock": 1000,
    "afterStock": 1100,
    "changeQuantity": 100,
    "operation": "increase",
    "reason": "补货",
    "operatorId": 67890,
    "operateTime": "2024-01-16T14:30:00"
  }
}
```

---

### 2. 获取库存记录
**接口路径**: `GET /api/v1/goods/{id}/stock/history`  
**接口描述**: 获取商品库存变更记录

#### 请求参数
- **id** (path): 商品ID，必填
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选
- **startTime** (query): 开始时间，可选
- **endTime** (query): 结束时间，可选

---

### 3. 批量更新库存
**接口路径**: `POST /api/v1/goods/batch-stock`  
**接口描述**: 批量更新多个商品库存

#### 请求参数
```json
{
  "operations": [
    {
      "goodsId": 789012,
      "operation": "decrease",
      "quantity": 5,
      "reason": "销售",
      "sourceOrderId": 123456
    },
    {
      "goodsId": 789013,
      "operation": "increase", 
      "quantity": 50,
      "reason": "补货"
    }
  ],
  "operatorId": 67890
}
```

---

### 4. 库存预警
**接口路径**: `GET /api/v1/goods/stock/alerts`  
**接口描述**: 获取库存预警商品列表

#### 请求参数
- **sellerId** (query): 卖家ID，可选
- **alertType** (query): 预警类型：low_stock/out_of_stock，可选
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

## 价格管理 API

### 1. 更新商品价格
**接口路径**: `POST /api/v1/goods/{id}/price`  
**接口描述**: 更新商品价格

#### 请求参数
- **id** (path): 商品ID，必填

```json
{
  "price": 79.00,                  // 必填，新价格
  "originalPrice": 99.00,          // 可选，新原价
  "effectiveTime": "2024-01-17T00:00:00", // 可选，生效时间
  "reason": "促销活动",            // 必填，调价原因
  "operatorId": 67890              // 必填，操作人ID
}
```

---

### 2. 获取价格历史
**接口路径**: `GET /api/v1/goods/{id}/price/history`  
**接口描述**: 获取商品价格变更历史

---

### 3. 批量调价
**接口路径**: `POST /api/v1/goods/batch-price`  
**接口描述**: 批量调整商品价格

#### 请求参数
```json
{
  "adjustmentType": "percentage",  // 必填，调整类型：percentage/fixed
  "adjustmentValue": -10,          // 必填，调整值（百分比或固定金额）
  "goodsIds": [789012, 789013],    // 可选，指定商品ID列表
  "categoryIds": [1001],           // 可选，指定分类ID列表
  "minPrice": 50.00,               // 可选，价格范围筛选
  "maxPrice": 200.00,
  "reason": "年终促销",            // 必填，调价原因
  "effectiveTime": "2024-01-20T00:00:00", // 可选，生效时间
  "operatorId": 67890              // 必填，操作人ID
}
```

---

## 商品查询 API

### 1. 根据分类查询商品
**接口路径**: `GET /api/v1/goods/category/{categoryId}`  
**接口描述**: 查询分类下的商品列表

#### 请求参数
- **categoryId** (path): 分类ID，必填
- **includeSubcategory** (query): 是否包含子分类，可选，默认true
- **pageNum** (query): 页码，可选
- **pageSize** (query): 页面大小，可选

---

### 2. 根据卖家查询商品
**接口路径**: `GET /api/v1/goods/seller/{sellerId}`  
**接口描述**: 查询卖家的商品列表

---

### 3. 搜索商品
**接口路径**: `POST /api/v1/goods/search`  
**接口描述**: 搜索商品

#### 请求参数
```json
{
  "keyword": "Java编程",           // 必填，搜索关键词
  "categoryId": 1001,              // 可选，分类筛选
  "minPrice": 50.00,               // 可选，价格范围
  "maxPrice": 200.00,
  "hasStock": true,                // 可选，是否有库存
  "sortBy": "sales",               // 可选，排序：price/sales/rating/createTime
  "sortOrder": "desc",             // 可选，排序方向
  "pageNum": 1,
  "pageSize": 20
}
```

---

### 4. 获取热门商品
**接口路径**: `GET /api/v1/goods/hot`  
**接口描述**: 获取热门商品列表

#### 请求参数
- **categoryId** (query): 分类ID，可选
- **timeRange** (query): 时间范围（天），可选，默认7
- **limit** (query): 返回数量，可选，默认10

---

### 5. 获取推荐商品
**接口路径**: `GET /api/v1/goods/recommend`  
**接口描述**: 获取推荐商品列表

#### 请求参数
- **userId** (query): 用户ID，可选（用于个性化推荐）
- **goodsId** (query): 商品ID，可选（获取相关商品推荐）
- **type** (query): 推荐类型：similar/collaborative/hot，可选
- **limit** (query): 返回数量，可选，默认10

---

## 商品统计 API

### 1. 获取商品统计
**接口路径**: `GET /api/v1/goods/{id}/statistics`  
**接口描述**: 获取商品统计信息

#### 响应示例
```json
{
  "success": true,
  "data": {
    "goodsId": 789012,
    "viewCount": 1250,               // 浏览量
    "favoriteCount": 89,             // 收藏数
    "shareCount": 34,                // 分享数
    "soldCount": 156,                // 销售量
    "reviewCount": 45,               // 评价数
    "averageRating": 4.6,            // 平均评分
    "conversionRate": 12.5,          // 转化率（%）
    "revenueTotal": 15444.00,        // 总收入
    "profitTotal": 7722.00,          // 总利润
    "refundRate": 2.3,               // 退款率（%）
    "todayViews": 45,                // 今日浏览
    "todaySales": 8,                 // 今日销售
    "stockTurnover": 3.2             // 库存周转率
  }
}
```

---

### 2. 获取卖家商品统计
**接口路径**: `GET /api/v1/goods/seller/{sellerId}/statistics`  
**接口描述**: 获取卖家的商品统计信息

---

### 3. 获取分类商品统计
**接口路径**: `GET /api/v1/goods/category/{categoryId}/statistics`  
**接口描述**: 获取分类的商品统计信息

---

## 商品审核 API

### 1. 提交审核
**接口路径**: `POST /api/v1/goods/{id}/submit-review`  
**接口描述**: 提交商品审核

#### 请求参数
- **id** (path): 商品ID，必填
- **sellerId** (query): 卖家ID，必填

---

### 2. 审核商品
**接口路径**: `POST /api/v1/goods/{id}/review`  
**接口描述**: 审核商品

#### 请求参数
- **id** (path): 商品ID，必填

```json
{
  "reviewStatus": "approved",      // 必填，审核状态：approved/rejected
  "reviewerId": 99999,             // 必填，审核人ID
  "reviewComment": "商品信息完整，通过审核", // 可选，审核意见
  "rejectReason": null             // 可选，拒绝原因
}
```

---

### 3. 获取待审核商品
**接口路径**: `GET /api/v1/goods/pending-review`  
**接口描述**: 获取待审核商品列表

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| GOODS_NOT_FOUND | 商品不存在 |
| GOODS_OFFLINE | 商品已下线 |
| INSUFFICIENT_STOCK | 库存不足 |
| GOODS_OUT_OF_STOCK | 商品缺货 |
| PRICE_INVALID | 价格无效 |
| SELLER_PERMISSION_DENIED | 卖家权限不足 |
| GOODS_UNDER_REVIEW | 商品正在审核中 |
| GOODS_REVIEW_REJECTED | 商品审核被拒绝 |
| CATEGORY_NOT_FOUND | 分类不存在 |
| BRAND_NOT_FOUND | 品牌不存在 |
| STOCK_OPERATION_FAILED | 库存操作失败 |
| PRICE_CHANGE_TOO_FREQUENT | 价格变更过于频繁 |

---

**最后更新**: 2024-01-16  
**文档版本**: v1.0.0