# Goods REST API Documentation

**版本**: 2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/goods`  
**作者**: GIG Team  
**更新时间**: 2024-01-31

## 概述

商品管理REST API提供完整的商品管理功能，支持四种商品类型（金币充值包、订阅服务、付费内容、实体商品）的增删改查、库存管理、统计分析等操作。

## 接口分类

### 1. 基础CRUD操作
### 2. 查询操作
### 3. 库存管理
### 4. 统计操作
### 5. 状态管理
### 6. 业务验证
### 7. 快捷查询
### 8. 内容同步管理

---

## 1. 基础CRUD操作

### 1.1 创建商品

**接口**: `POST /api/v1/goods`

**描述**: 创建新商品，支持四种商品类型

**请求体**:
```json
{
  "name": "VIP会员月卡",
  "description": "享受VIP特权服务",
  "goodsType": "SUBSCRIPTION",
  "categoryId": 1,
  "categoryName": "会员服务",
  "sellerId": 1001,
  "sellerNickname": "官方商城",
  "originalPrice": 29.9,
  "coinAmount": 299,
  "stock": 1000,
  "coverUrl": "https://example.com/cover.jpg",
  "images": "image1.jpg,image2.jpg",
  "subscriptionDuration": 30,
  "subscriptionType": "VIP"
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 1.2 获取商品详情

**接口**: `GET /api/v1/goods/{id}`

**描述**: 根据ID获取商品详细信息，自动增加浏览量

**路径参数**:
- `id` (Long): 商品ID

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "VIP会员月卡",
    "description": "享受VIP特权服务",
    "goodsType": "SUBSCRIPTION",
    "categoryId": 1,
    "categoryName": "会员服务",
    "sellerId": 1001,
    "sellerNickname": "官方商城",
    "originalPrice": 29.9,
    "coinAmount": 299,
    "stock": 1000,
    "salesCount": 150,
    "viewCount": 2580,
    "status": "PUBLISHED",
    "coverUrl": "https://example.com/cover.jpg",
    "images": "image1.jpg,image2.jpg",
    "subscriptionDuration": 30,
    "subscriptionType": "VIP",
    "createTime": "2024-01-31T10:00:00",
    "updateTime": "2024-01-31T12:00:00"
  },
  "timestamp": 1706688000000
}
```

---

### 1.3 更新商品信息

**接口**: `PUT /api/v1/goods/{id}`

**描述**: 更新指定商品的信息

**路径参数**:
- `id` (Long): 商品ID

**请求体**: 同创建商品请求体

**响应**: 返回更新后的商品信息（格式同获取商品详情）

---

### 1.4 删除商品

**接口**: `DELETE /api/v1/goods/{id}`

**描述**: 软删除指定商品（设置为下架状态）

**路径参数**:
- `id` (Long): 商品ID

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 1.5 批量删除商品

**接口**: `DELETE /api/v1/goods/batch`

**描述**: 批量软删除多个商品

**请求体**:
```json
[1, 2, 3, 4, 5]
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

## 2. 查询操作

### 2.1 分页查询商品

**接口**: `POST /api/v1/goods/query`

**描述**: 支持多条件分页查询商品列表

**请求体**:
```json
{
  "goodsType": "SUBSCRIPTION",
  "categoryId": 1,
  "sellerId": 1001,
  "status": "PUBLISHED",
  "keyword": "VIP",
  "currentPage": 1,
  "pageSize": 20,
  "orderBy": "salesCount",
  "orderDirection": "DESC"
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5,
    "totalCount": 100,
    "datas": [
      {
        "id": 1,
        "name": "VIP会员月卡",
        "goodsType": "SUBSCRIPTION",
        "originalPrice": 29.9,
        "coinAmount": 299,
        "stock": 1000,
        "salesCount": 150,
        "status": "PUBLISHED"
      }
    ]
  },
  "timestamp": 1706688000000
}
```

---

### 2.2 根据分类查询商品

**接口**: `GET /api/v1/goods/category/{categoryId}`

**描述**: 获取指定分类下的商品列表

**路径参数**:
- `categoryId` (Long): 分类ID

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 2.3 根据商家查询商品

**接口**: `GET /api/v1/goods/seller/{sellerId}`

**描述**: 获取指定商家的商品列表

**路径参数**:
- `sellerId` (Long): 商家ID

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 2.4 获取热门商品

**接口**: `GET /api/v1/goods/hot`

**描述**: 获取按销量排序的热门商品列表

**查询参数**:
- `goodsType` (String): 商品类型，可选
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 2.5 搜索商品

**接口**: `GET /api/v1/goods/search`

**描述**: 根据关键词搜索商品名称和描述

**查询参数**:
- `keyword` (String): 搜索关键词，必填
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 2.6 按价格区间查询商品

**接口**: `GET /api/v1/goods/price-range`

**描述**: 根据价格区间查询商品

**查询参数**:
- `goodsType` (String): 商品类型，必填
- `minPrice` (Object): 最低价格，必填
- `maxPrice` (Object): 最高价格，必填
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

## 3. 库存管理

### 3.1 检查库存

**接口**: `GET /api/v1/goods/{id}/stock/check`

**描述**: 检查指定商品的库存是否充足

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `quantity` (Integer): 需要数量

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": true,
  "timestamp": 1706688000000
}
```

---

### 3.2 扣减库存

**接口**: `POST /api/v1/goods/{id}/stock/reduce`

**描述**: 扣减指定商品的库存数量

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `quantity` (Integer): 扣减数量

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 3.3 批量扣减库存

**接口**: `POST /api/v1/goods/stock/batch-reduce`

**描述**: 批量扣减多个商品的库存

**请求体**:
```json
{
  "1": 5,
  "2": 3,
  "3": 10
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 3.4 查询低库存商品

**接口**: `GET /api/v1/goods/low-stock`

**描述**: 查询库存不足的商品列表

**查询参数**:
- `threshold` (Integer): 库存阈值，默认10

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "VIP会员月卡",
      "stock": 5,
      "status": "PUBLISHED"
    }
  ],
  "timestamp": 1706688000000
}
```

---

## 4. 统计操作

### 4.1 增加销量

**接口**: `POST /api/v1/goods/{id}/sales/increase`

**描述**: 增加指定商品的销量统计

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `count` (Long): 增加数量

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 4.2 增加浏览量

**接口**: `POST /api/v1/goods/{id}/views/increase`

**描述**: 增加指定商品的浏览量统计

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `count` (Long): 增加数量，默认1

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 4.3 批量增加浏览量

**接口**: `POST /api/v1/goods/views/batch-increase`

**描述**: 批量增加多个商品的浏览量

**请求体**:
```json
{
  "1": 10,
  "2": 5,
  "3": 8
}
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 4.4 获取商品统计

**接口**: `GET /api/v1/goods/statistics`

**描述**: 获取各类型商品的统计信息

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "goodsType": "SUBSCRIPTION",
      "totalCount": 50,
      "publishedCount": 45,
      "totalSales": 1250,
      "totalViews": 15800
    },
    {
      "goodsType": "COIN_PACKAGE",
      "totalCount": 20,
      "publishedCount": 18,
      "totalSales": 2500,
      "totalViews": 8500
    }
  ],
  "timestamp": 1706688000000
}
```

---

### 4.5 按类型状态统计

**接口**: `GET /api/v1/goods/statistics/type-status`

**描述**: 按商品类型和状态统计商品数量

**响应**: 同获取商品统计格式

---

### 4.6 按分类统计

**接口**: `GET /api/v1/goods/statistics/category/{categoryId}`

**描述**: 统计指定分类下的商品数量

**路径参数**:
- `categoryId` (Long): 分类ID

**查询参数**:
- `status` (String): 商品状态，可选

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 25,
  "timestamp": 1706688000000
}
```

---

### 4.7 按商家统计

**接口**: `GET /api/v1/goods/statistics/seller/{sellerId}`

**描述**: 统计指定商家的商品数量

**路径参数**:
- `sellerId` (Long): 商家ID

**查询参数**:
- `status` (String): 商品状态，可选

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 35,
  "timestamp": 1706688000000
}
```

---

## 5. 高级查询

### 5.1 复合条件查询

**接口**: `POST /api/v1/goods/search/advanced`

**描述**: 支持多种查询条件和排序方式的复合查询

**查询参数**:
- `categoryId` (Long): 分类ID，可选
- `sellerId` (Long): 商家ID，可选
- `goodsType` (String): 商品类型，可选
- `nameKeyword` (String): 名称关键词，可选
- `minPrice` (Object): 最低现金价格，可选
- `maxPrice` (Object): 最高现金价格，可选
- `minCoinPrice` (Object): 最低金币价格，可选
- `maxCoinPrice` (Object): 最高金币价格，可选
- `hasStock` (Boolean): 是否有库存，可选
- `status` (String): 商品状态，可选
- `orderBy` (String): 排序字段，可选
- `orderDirection` (String): 排序方向，可选
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

## 6. 状态管理

### 6.1 上架商品

**接口**: `POST /api/v1/goods/{id}/publish`

**描述**: 将指定商品设置为上架状态

**路径参数**:
- `id` (Long): 商品ID

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 6.2 下架商品

**接口**: `POST /api/v1/goods/{id}/unpublish`

**描述**: 将指定商品设置为下架状态

**路径参数**:
- `id` (Long): 商品ID

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 6.3 批量上架商品

**接口**: `POST /api/v1/goods/batch-publish`

**描述**: 批量设置多个商品为上架状态

**请求体**:
```json
[1, 2, 3, 4, 5]
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 6.4 批量下架商品

**接口**: `POST /api/v1/goods/batch-unpublish`

**描述**: 批量设置多个商品为下架状态

**请求体**:
```json
[1, 2, 3, 4, 5]
```

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 6.5 批量更新状态

**接口**: `POST /api/v1/goods/batch-update-status`

**描述**: 批量更新多个商品的状态

**请求体**:
```json
[1, 2, 3, 4, 5]
```

**查询参数**:
- `status` (String): 新状态，必填

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": 5,
  "timestamp": 1706688000000
}
```

---

## 7. 业务验证

### 7.1 验证商品购买

**接口**: `GET /api/v1/goods/{id}/purchase/validate`

**描述**: 验证指定商品是否可以购买

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `quantity` (Integer): 购买数量

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "canPurchase": true,
    "reason": null,
    "stock": 100,
    "maxQuantity": 50
  },
  "timestamp": 1706688000000
}
```

---

### 7.2 获取购买信息

**接口**: `GET /api/v1/goods/{id}/purchase/info`

**描述**: 获取商品的详细购买信息

**路径参数**:
- `id` (Long): 商品ID

**查询参数**:
- `quantity` (Integer): 购买数量

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "goodsId": 1,
    "name": "VIP会员月卡",
    "quantity": 1,
    "unitPrice": 29.9,
    "totalPrice": 29.9,
    "coinAmount": 299,
    "totalCoinAmount": 299,
    "availableStock": 100
  },
  "timestamp": 1706688000000
}
```

---

## 8. 快捷查询

### 8.1 获取金币充值包

**接口**: `GET /api/v1/goods/coin-packages`

**描述**: 获取所有可用的金币充值包

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 8.2 获取订阅服务

**接口**: `GET /api/v1/goods/subscriptions`

**描述**: 获取所有可用的订阅服务

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 8.3 获取付费内容

**接口**: `GET /api/v1/goods/contents`

**描述**: 获取所有可用的付费内容

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

### 8.4 获取实体商品

**接口**: `GET /api/v1/goods/physical`

**描述**: 获取所有可用的实体商品

**查询参数**:
- `currentPage` (Integer): 当前页码，默认1
- `pageSize` (Integer): 页面大小，默认20

**响应**: 同分页查询响应格式

---

## 9. 内容同步管理

### 9.1 根据内容创建商品

**接口**: `POST /api/v1/goods/sync/content/create`

**描述**: 当新内容发布时，自动创建对应的商品记录

**查询参数**:
- `contentId` (Long): 内容ID，必填
- `contentTitle` (String): 内容标题，必填
- `contentDesc` (String): 内容描述，可选
- `categoryId` (Long): 分类ID，必填
- `categoryName` (String): 分类名称，必填
- `authorId` (Long): 作者ID，必填
- `authorNickname` (String): 作者昵称，必填
- `coverUrl` (String): 封面图URL，可选
- `coinPrice` (Long): 金币价格，默认0
- `contentStatus` (String): 内容状态，默认PUBLISHED

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 9.2 同步内容信息到商品

**接口**: `PUT /api/v1/goods/sync/content/{contentId}/info`

**描述**: 当内容信息更新时，同步更新商品信息

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `contentTitle` (String): 内容标题，必填
- `contentDesc` (String): 内容描述，可选
- `categoryId` (Long): 分类ID，必填
- `categoryName` (String): 分类名称，必填
- `authorId` (Long): 作者ID，必填
- `authorNickname` (String): 作者昵称，必填
- `coverUrl` (String): 封面图URL，可选

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 9.3 同步内容状态到商品

**接口**: `PUT /api/v1/goods/sync/content/{contentId}/status`

**描述**: 当内容状态变更时，同步更新商品状态

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `contentStatus` (String): 内容状态，必填

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 9.4 同步内容价格到商品

**接口**: `PUT /api/v1/goods/sync/content/{contentId}/price`

**描述**: 当内容付费配置变更时，同步更新商品价格

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `coinPrice` (Long): 金币价格，必填
- `isActive` (Boolean): 是否启用付费，可选

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 9.5 批量同步内容商品

**接口**: `POST /api/v1/goods/sync/content/batch`

**描述**: 批量检查和同步内容与商品的一致性

**查询参数**:
- `batchSize` (Integer): 批处理大小，默认100

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "processedCount": 150,
    "successCount": 148,
    "failureCount": 2,
    "createdCount": 25,
    "updatedCount": 123,
    "skippedCount": 2
  },
  "timestamp": 1706688000000
}
```

---

### 9.6 删除内容对应的商品

**接口**: `DELETE /api/v1/goods/sync/content/{contentId}`

**描述**: 当内容被删除时，删除对应的商品记录

**路径参数**:
- `contentId` (Long): 内容ID

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": null,
  "timestamp": 1706688000000
}
```

---

### 9.7 根据内容ID获取商品

**接口**: `GET /api/v1/goods/sync/content/{contentId}`

**描述**: 查询内容对应的商品信息

**路径参数**:
- `contentId` (Long): 内容ID

**查询参数**:
- `goodsType` (String): 商品类型，默认content

**响应**:
```json
{
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "精品小说：《星际争霸》",
    "contentId": 1001,
    "contentTitle": "精品小说：《星际争霸》",
    "coinAmount": 299,
    "status": "PUBLISHED"
  },
  "timestamp": 1706688000000
}
```

---

## 错误码说明

| 错误码 | 说明 | HTTP状态码 |
|--------|------|-----------|
| SUCCESS | 操作成功 | 200 |
| GOODS_NOT_FOUND | 商品不存在 | 404 |
| GOODS_ALREADY_EXISTS | 商品已存在 | 409 |
| INSUFFICIENT_STOCK | 库存不足 | 400 |
| GOODS_STATUS_ERROR | 商品状态错误 | 400 |
| INVALID_GOODS_TYPE | 无效的商品类型 | 400 |
| INVALID_PARAMETER | 参数错误 | 400 |
| PERMISSION_DENIED | 权限不足 | 403 |
| SYSTEM_ERROR | 系统错误 | 500 |

## 商品类型枚举

| 类型 | 描述 | 特殊字段 |
|------|------|----------|
| COIN_PACKAGE | 金币充值包 | coinAmount |
| SUBSCRIPTION | 订阅服务 | subscriptionDuration, subscriptionType |
| CONTENT | 付费内容 | contentId, contentTitle |
| PHYSICAL | 实体商品 | stock, 物流相关字段 |

## 商品状态枚举

| 状态 | 描述 |
|------|------|
| DRAFT | 草稿 |
| PUBLISHED | 已上架 |
| OFFLINE | 已下架 |
| DELETED | 已删除 |

## 使用示例

### 创建订阅服务商品
```bash
curl -X POST "http://localhost:8080/api/v1/goods" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "VIP会员年卡",
    "description": "享受VIP特权服务一整年",
    "goodsType": "SUBSCRIPTION",
    "categoryId": 1,
    "categoryName": "会员服务",
    "sellerId": 1001,
    "sellerNickname": "官方商城",
    "originalPrice": 299.0,
    "coinAmount": 2990,
    "stock": 1000,
    "subscriptionDuration": 365,
    "subscriptionType": "VIP"
  }'
```

### 搜索商品
```bash
curl -X GET "http://localhost:8080/api/v1/goods/search?keyword=VIP&currentPage=1&pageSize=10"
```

### 批量扣减库存
```bash
curl -X POST "http://localhost:8080/api/v1/goods/stock/batch-reduce" \
  -H "Content-Type: application/json" \
  -d '{"1": 5, "2": 3, "3": 10}'
```

---

**总计**: 40个REST接口，覆盖商品管理的所有功能场景。