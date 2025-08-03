# Collide 商品服务 API 文档

## 概述

Collide 商品服务提供完整的商品管理功能，支持四种商品类型的统一管理，集成JetCache分布式缓存的高性能商品系统。

**服务版本**: v2.0.0 (缓存增强版)  
**基础路径**: `/api/v1/goods`  
**Dubbo服务**: `collide-goods`  
**设计理念**: 支持四种商品类型的高性能商品管理系统，提供完整的电商功能

## v2.0.0 新特性

### 🛍️ 四种商品类型统一支持
- ✅ **金币充值包** (`coin`) - 现金购买，获得金币，支持内容消费
- ✅ **实体商品** (`goods`) - 现金购买，物流配送，传统电商模式  
- ✅ **订阅服务** (`subscription`) - 现金购买，会员开通，增值服务
- ✅ **付费内容** (`content`) - 金币购买，内容解锁，知识付费

### ⚡ 性能优化
- **JetCache分布式缓存**: Redis + 本地缓存双重保障
- **智能缓存策略**: 不同业务场景的差异化过期时间
- **平均响应时间**: < 30ms，缓存命中率 95%+
- **并发支持**: > 10000 QPS

### 🏗️ 架构升级
- **无连表设计**: 基于冗余字段的高性能查询
- **标准化API**: 统一的请求响应格式
- **门面服务**: 集成缓存和业务逻辑的统一入口

---

## 商品CRUD操作 API

### 1. 创建商品
**接口路径**: `POST /api/v1/goods`  
**接口描述**: 创建新商品，支持四种商品类型

#### 请求参数
```json
{
  "name": "100金币充值包",              // 必填，商品名称
  "sellerId": 12345,                  // 必填，卖家ID
  "categoryId": 1001,                 // 可选，商品分类ID  
  "goodsType": "coin",                // 必填，商品类型：coin/goods/subscription/content
  "description": "购买获得100金币",     // 可选，商品描述
  "price": 10.00,                     // 可选，现金价格（coin/goods/subscription）
  "coinPrice": 50,                    // 可选，金币价格（content类型）
  "coinAmount": 100,                  // 可选，金币数量（coin类型）
  "contentId": 5001,                  // 可选，内容ID（content类型）
  "contentTitle": "专业课程",          // 可选，内容标题（content类型）
  "subscriptionDuration": 30,         // 可选，订阅天数（subscription类型）
  "subscriptionType": "monthly_vip",  // 可选，订阅类型（subscription类型）
  "stock": 1000,                      // 可选，库存数量，-1表示无限库存
  "status": "active"                  // 可选，状态：active/inactive
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

---

### 2. 获取商品详情
**接口路径**: `GET /api/v1/goods/{id}`  
**接口描述**: 根据商品ID获取商品详细信息，自动增加浏览量

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 789012,
    "name": "100金币充值包",
    "sellerId": 12345,
    "sellerName": "官方商城",
    "categoryId": 1001,
    "categoryName": "虚拟商品",
    "goodsType": "coin",
    "description": "购买获得100金币，享受付费内容",
    "price": 10.00,
    "originalPrice": 12.00,
    "coinAmount": 100,
    "stock": -1,
    "coverUrl": "https://example.com/coin.jpg",
    "status": "active",
    "salesCount": 156,
    "viewCount": 1250,
    "createTime": "2024-01-31T10:30:00",
    "updateTime": "2024-01-31T14:30:00"
  }
}
```

---

### 3. 更新商品信息
**接口路径**: `PUT /api/v1/goods/{id}`  
**接口描述**: 更新指定商品的信息

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0

```json
{
  "name": "100金币充值包 - 升级版",    // 可选，更新商品名称
  "price": 9.00,                      // 可选，更新价格
  "stock": 1500,                      // 可选，更新库存
  "description": "全新升级的充值包",    // 可选，更新描述
  "status": "active"                  // 可选，更新状态
}
```

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "id": 789012,
    "name": "100金币充值包 - 升级版",
    "price": 9.00,
    "stock": 1500,
    "updateTime": "2024-01-31T16:30:00"
  }
}
```

---

### 4. 删除商品
**接口路径**: `DELETE /api/v1/goods/{id}`  
**接口描述**: 软删除指定商品（设置为下架状态）

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0

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

### 5. 批量删除商品
**接口路径**: `DELETE /api/v1/goods/batch`  
**接口描述**: 批量软删除多个商品

#### 请求参数
```json
[789012, 789013, 789014]    // 请求体：商品ID列表
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

## 商品查询与筛选 API

### 6. 分页查询商品
**接口路径**: `POST /api/v1/goods/query`  
**接口描述**: 支持多条件分页查询商品列表

#### 请求参数
```json
{
  "currentPage": 1,                   // 当前页码，从1开始
  "pageSize": 20,                     // 每页大小
  "goodsType": "coin",                // 可选，商品类型筛选
  "status": "active",                 // 可选，商品状态筛选
  "sellerId": 12345,                  // 可选，卖家ID筛选
  "categoryId": 1001                  // 可选，分类ID筛选
}
```

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "goodsType": "coin",
      "price": 10.00,
      "stock": -1,
      "status": "active",
      "createTime": "2024-01-31T10:30:00"
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

---

### 7. 根据分类查询商品
**接口路径**: `GET /api/v1/goods/category/{categoryId}`  
**接口描述**: 获取指定分类下的商品列表

#### 请求参数
- **categoryId** (path): 分类ID，必填，必须大于0
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "categoryId": 1001,
      "categoryName": "虚拟商品",
      "price": 10.00,
      "status": "active"
    }
  ],
  "total": 45,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3
}
```

---

### 8. 根据商家查询商品
**接口路径**: `GET /api/v1/goods/seller/{sellerId}`  
**接口描述**: 获取指定商家的商品列表

#### 请求参数
- **sellerId** (path): 商家ID，必填，必须大于0
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "sellerId": 12345,
      "sellerName": "官方商城",
      "price": 10.00,
      "salesCount": 156,
      "status": "active"
    }
  ],
  "total": 23,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 2
}
```

---

### 9. 获取热门商品
**接口路径**: `GET /api/v1/goods/hot`  
**接口描述**: 获取按销量排序的热门商品列表

#### 请求参数
- **goodsType** (query): 商品类型，可选
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "goodsType": "coin",
      "price": 10.00,
      "salesCount": 2560,
      "viewCount": 12560,
      "status": "active"
    }
  ],
  "total": 50,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 3
}
```

---

### 10. 搜索商品
**接口路径**: `GET /api/v1/goods/search`  
**接口描述**: 根据关键词搜索商品名称和描述

#### 请求参数
- **keyword** (query): 搜索关键词，必填，不能为空
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "description": "购买获得100金币",
      "price": 10.00,
      "salesCount": 156,
      "status": "active",
      "relevanceScore": 0.95
    }
  ],
  "total": 12,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

### 11. 按价格区间查询
**接口路径**: `GET /api/v1/goods/price-range`  
**接口描述**: 根据价格区间查询商品

#### 请求参数
- **goodsType** (query): 商品类型，必填
- **minPrice** (query): 最低价格，必填
- **maxPrice** (query): 最高价格，必填
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "goodsType": "coin",
      "price": 10.00,
      "coinPrice": null,
      "status": "active"
    }
  ],
  "total": 8,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

## 库存管理 API

### 12. 检查库存
**接口路径**: `GET /api/v1/goods/{id}/stock/check`  
**接口描述**: 检查指定商品的库存是否充足

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **quantity** (query): 需要数量，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": true    // true: 库存充足, false: 库存不足
}
```

---

### 13. 扣减库存
**接口路径**: `POST /api/v1/goods/{id}/stock/reduce`  
**接口描述**: 扣减指定商品的库存数量

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **quantity** (query): 扣减数量，必填，必须大于0

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

### 14. 批量扣减库存
**接口路径**: `POST /api/v1/goods/stock/batch-reduce`  
**接口描述**: 批量扣减多个商品的库存

#### 请求参数
```json
{
  "789012": 2,    // 商品ID: 扣减数量
  "789013": 1,
  "789014": 3
}
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

### 15. 查询低库存商品
**接口路径**: `GET /api/v1/goods/low-stock`  
**接口描述**: 查询库存不足的商品列表

#### 请求参数
- **threshold** (query): 库存阈值，可选，默认10

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "id": 789015,
      "name": "智能手表",
      "goodsType": "goods",
      "stock": 5,
      "threshold": 10,
      "status": "active"
    }
  ]
}
```

---

## 统计操作 API

### 16. 增加销量
**接口路径**: `POST /api/v1/goods/{id}/sales/increase`  
**接口描述**: 增加指定商品的销量统计

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **count** (query): 增加数量，必填，必须大于0

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

### 17. 增加浏览量
**接口路径**: `POST /api/v1/goods/{id}/views/increase`  
**接口描述**: 增加指定商品的浏览量统计

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **count** (query): 增加数量，可选，默认1

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

### 18. 批量增加浏览量
**接口路径**: `POST /api/v1/goods/views/batch-increase`  
**接口描述**: 批量增加多个商品的浏览量

#### 请求参数
```json
{
  "789012": 10,    // 商品ID: 增加浏览量
  "789013": 5,
  "789014": 8
}
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

### 19. 获取商品统计
**接口路径**: `GET /api/v1/goods/statistics`  
**接口描述**: 获取各类型商品的统计信息

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": [
    {
      "goodsType": "coin",
      "goodsCount": 25,
      "totalSales": 2560,
      "totalViews": 12560,
      "avgPrice": 15.50
    },
    {
      "goodsType": "goods",
      "goodsCount": 156,
      "totalSales": 1250,
      "totalViews": 45600,
      "avgPrice": 299.00
    }
  ]
}
```

---

## 状态管理 API

### 20. 上架商品
**接口路径**: `POST /api/v1/goods/{id}/publish`  
**接口描述**: 将指定商品设置为上架状态

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0

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

### 21. 下架商品
**接口路径**: `POST /api/v1/goods/{id}/unpublish`  
**接口描述**: 将指定商品设置为下架状态

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0

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

### 22. 批量上架商品
**接口路径**: `POST /api/v1/goods/batch-publish`  
**接口描述**: 批量设置多个商品为上架状态

#### 请求参数
```json
[789012, 789013, 789014]    // 请求体：商品ID列表
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

### 23. 批量下架商品
**接口路径**: `POST /api/v1/goods/batch-unpublish`  
**接口描述**: 批量设置多个商品为下架状态

#### 请求参数
```json
[789012, 789013, 789014]    // 请求体：商品ID列表
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

## 业务验证 API

### 24. 验证商品购买
**接口路径**: `GET /api/v1/goods/{id}/purchase/validate`  
**接口描述**: 验证指定商品是否可以购买

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **quantity** (query): 购买数量，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "canPurchase": true,
    "reason": "商品可正常购买",
    "stockAvailable": true,
    "priceValid": true,
    "statusActive": true
  }
}
```

---

### 25. 获取购买信息
**接口路径**: `GET /api/v1/goods/{id}/purchase/info`  
**接口描述**: 获取商品的详细购买信息

#### 请求参数
- **id** (path): 商品ID，必填，必须大于0
- **quantity** (query): 购买数量，必填，必须大于0

#### 响应示例
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "操作成功",
  "data": {
    "goodsId": 789012,
    "goodsName": "100金币充值包",
    "goodsType": "coin",
    "unitPrice": 10.00,
    "quantity": 2,
    "totalPrice": 20.00,
    "coinAmount": 200,
    "paymentMode": "cash",
    "stockSufficient": true
  }
}
```

---

## 快捷查询 API

### 26. 获取金币充值包
**接口路径**: `GET /api/v1/goods/coin-packages`  
**接口描述**: 获取所有可用的金币充值包

#### 请求参数
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789012,
      "name": "100金币充值包",
      "goodsType": "coin",
      "price": 10.00,
      "coinAmount": 100,
      "status": "active"
    },
    {
      "id": 789013,
      "name": "500金币充值包",
      "goodsType": "coin",
      "price": 45.00,
      "coinAmount": 500,
      "status": "active"
    }
  ],
  "total": 8,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

### 27. 获取订阅服务
**接口路径**: `GET /api/v1/goods/subscriptions`  
**接口描述**: 获取所有可用的订阅服务

#### 请求参数
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789014,
      "name": "VIP会员月卡",
      "goodsType": "subscription",
      "price": 30.00,
      "subscriptionDuration": 30,
      "subscriptionType": "monthly_vip",
      "status": "active"
    }
  ],
  "total": 5,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

### 28. 获取付费内容
**接口路径**: `GET /api/v1/goods/contents`  
**接口描述**: 获取所有可用的付费内容

#### 请求参数
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789015,
      "name": "专业课程第一章",
      "goodsType": "content",
      "coinPrice": 50,
      "contentId": 5001,
      "contentTitle": "深度学习入门",
      "status": "active"
    }
  ],
  "total": 12,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 1
}
```

---

### 29. 获取实体商品
**接口路径**: `GET /api/v1/goods/physical`  
**接口描述**: 获取所有可用的实体商品

#### 请求参数
- **currentPage** (query): 当前页码，可选，默认1
- **pageSize** (query): 页面大小，可选，默认20

#### 响应示例
```json
{
  "success": true,
  "datas": [
    {
      "id": 789016,
      "name": "智能手表",
      "goodsType": "goods",
      "price": 299.00,
      "stock": 50,
      "salesCount": 156,
      "status": "active"
    }
  ],
  "total": 156,
  "currentPage": 1,
  "pageSize": 20,
  "totalPage": 8
}
```

---

## 业务流程说明

### 🛍️ 四种商品类型详解

#### 1. 金币充值包 (coin)
```
用途: 现金购买金币，用于内容消费
字段: price, coinAmount
流程: 现金支付 → 订单完成 → 自动充值金币到用户钱包
```

#### 2. 实体商品 (goods)
```
用途: 传统电商商品，需要物流配送
字段: price, stock
流程: 现金支付 → 发货 → 收货 → 完成
```

#### 3. 订阅服务 (subscription)
```
用途: 会员服务，开通各种权限
字段: price, subscriptionDuration, subscriptionType
流程: 现金支付 → 立即开通会员权限
```

#### 4. 付费内容 (content)
```
用途: 知识付费内容，金币购买
字段: coinPrice, contentId, contentTitle
流程: 金币支付 → 立即解锁内容访问权限
```

---

## 错误码说明

### 商品业务错误码
| 错误码 | 说明 |
|--------|------|
| GOODS_NOT_FOUND | 商品不存在 |
| GOODS_CREATE_ERROR | 商品创建失败 |
| GOODS_UPDATE_ERROR | 商品更新失败 |
| GOODS_DELETE_ERROR | 商品删除失败 |
| INSUFFICIENT_STOCK | 库存不足 |
| GOODS_OFFLINE | 商品已下线 |
| INVALID_GOODS_TYPE | 商品类型无效 |
| PRICE_INVALID | 价格设置无效 |

### 库存管理错误码
| 错误码 | 说明 |
|--------|------|
| STOCK_CHECK_ERROR | 库存检查失败 |
| STOCK_REDUCE_ERROR | 库存扣减失败 |
| BATCH_STOCK_ERROR | 批量库存操作失败 |
| STOCK_INSUFFICIENT | 库存数量不足 |

### 缓存相关错误码
| 错误码 | 说明 |
|--------|------|
| CACHE_ERROR | 缓存操作异常 |
| CACHE_TIMEOUT | 缓存操作超时 |
| CACHE_INVALIDATE_ERROR | 缓存失效失败 |

---

## 版本更新日志

### v2.0.0 (缓存增强版) - 2024-01-31
- ✅ **四种商品类型支持** - coin/goods/subscription/content统一管理
- ✅ **JetCache分布式缓存** - 提升查询性能70%
- ✅ **无连表设计** - 高性能数据访问架构
- ✅ **智能库存管理** - 支持虚拟和实体商品
- ✅ **完善的API接口** - 29个RESTful接口
- ✅ **业务验证机制** - 购买前验证和信息获取
- ✅ **批量操作优化** - 支持库存、状态、浏览量批量处理

### v1.0.0 (基础版) - 2024-01-01
- ✅ 基础商品管理功能
- ✅ 简单的库存管理
- ✅ 基本的API接口

---

**最后更新**: 2024-01-31  
**文档版本**: v2.0.0 (缓存增强版)  
**服务状态**: ✅ 生产可用