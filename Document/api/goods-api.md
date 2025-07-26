# Goods 模块 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [设计原则](#设计原则)
- [RPC接口](#RPC接口)
- [REST API接口](#REST-API接口)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

Goods 模块是 Collide 社交平台的商品管理服务，负责商品的创建、管理、查询和购买支持等核心功能。支持金币类商品和订阅类商品两种类型。

### 主要功能
- 商品创建与管理
- 商品库存管理
- 商品上下架
- 商品查询与搜索
- 推荐与热门商品
- 购买可行性检查
- 幂等性保障

### 技术架构
- **框架**: Spring Boot 3.x + MyBatis Plus
- **RPC**: Apache Dubbo
- **缓存**: Redis (多层缓存策略)
- **数据库**: MySQL 8.0+
- **并发控制**: 乐观锁 (版本号)
- **分布式锁**: Redis 分布式锁
- **文档**: OpenAPI 3.0

### 设计特点
- 支持金币类和订阅类商品
- 基于Redis的多层缓存策略
- 乐观锁保障库存并发安全
- 分布式幂等性机制
- 去连表化查询设计
- 完整的商品生命周期管理

---

## 🎯 设计原则

### 去连表化设计 (No-Join Design)
```
✅ 正确设计：
- 商品表包含所有必要的显示字段
- 避免复杂的 JOIN 查询
- 通过数据冗余提升查询性能
- 单表查询支持所有基础功能

❌ 避免设计：
- 多表 JOIN 查询
- 跨表关联查询
- 复杂的子查询
- 实时数据同步依赖
```

### 数据冗余策略
- **creator_id**: 商品创建者信息冗余
- **sold_count**: 销量统计冗余
- **recommended/hot**: 推荐和热门标识冗余
- **type + coin_amount/subscription_days**: 商品类型特性冗余

### 缓存层级设计
```
L1 Cache: 商品详情缓存 (TTL: 30分钟)
L2 Cache: 商品列表缓存 (TTL: 10分钟) 
L3 Cache: 热门推荐缓存 (TTL: 5分钟)
L4 Cache: 搜索结果缓存 (TTL: 3分钟)
```

---

## 🔌 RPC接口

### GoodsFacadeService (Dubbo RPC)

#### 创建商品
```java
SingleResponse<Long> createGoods(GoodsCreateRequest createRequest)
```

**功能**: 创建新商品，支持幂等性
**参数**: 
- `createRequest`: 商品创建请求，包含所有必要字段
- `idempotentKey`: 幂等性键（可选）

**返回**: 创建成功的商品ID

#### 更新商品信息
```java
SingleResponse<Void> updateGoods(GoodsUpdateRequest updateRequest)
```

**功能**: 更新商品基本信息，支持幂等性
**参数**: 
- `updateRequest`: 商品更新请求
- `idempotentKey`: 幂等性键（可选）

**特性**: 
- 乐观锁保障并发安全
- 自动缓存失效
- 版本号自动递增

#### 删除商品
```java
SingleResponse<Void> deleteGoods(Long goodsId)
```

**功能**: 逻辑删除商品
**参数**: 
- `goodsId`: 商品ID

**特性**: 
- 逻辑删除 (设置deleted=1)
- 自动清理相关缓存

#### 获取商品详情
```java
SingleResponse<GoodsInfo> getGoodsDetail(Long goodsId)
```

**功能**: 获取商品完整信息
**参数**: 
- `goodsId`: 商品ID

**特性**: 
- L1缓存优先
- 单表查询，无JOIN
- 包含所有展示字段

#### 分页查询商品
```java
PageResponse<GoodsInfo> pageQueryGoods(GoodsPageQueryRequest queryRequest)
```

**功能**: 分页查询商品列表（管理端）
**参数**: 
- `queryRequest`: 查询条件和分页参数

**支持条件**:
- 商品类型筛选
- 状态筛选
- 创建者筛选
- 关键词搜索
- 推荐/热门筛选
- 价格区间筛选

#### 商品上下架
```java
SingleResponse<Void> putOnSale(Long goodsId)
SingleResponse<Void> putOffSale(Long goodsId)
```

**功能**: 单个商品上架/下架
**特性**: 
- 状态验证
- 缓存同步更新

#### 批量上下架
```java
SingleResponse<Object> batchPutOnSale(List<Long> goodsIds)
SingleResponse<Object> batchPutOffSale(List<Long> goodsIds)
```

**功能**: 批量商品上架/下架
**特性**: 
- 事务保障
- 批量缓存清理

#### 更新库存
```java
SingleResponse<Void> updateStock(GoodsStockRequest stockRequest)
```

**功能**: 库存操作，支持设置、增加、减少
**参数**: 
- `stockRequest`: 库存操作请求
- `operationType`: SET/INCREASE/DECREASE
- `idempotentKey`: 幂等性键（可选）

**特性**: 
- 乐观锁防止超卖
- 库存不足检查
- 并发冲突重试提示

---

## 🌐 REST API接口

### GoodsController (用户端接口)

#### 获取商品详情
```http
GET /api/v1/goods/{goodsId}
```

**功能**: 用户获取可购买商品详情
**路径参数**: 
- `goodsId`: 商品ID

**响应**: 
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "goodsId": 1001,
    "name": "超值金币包",
    "description": "购买即可获得1000金币，性价比超高！",
    "type": "COIN",
    "status": "ON_SALE",
    "price": 9.99,
    "imageUrl": "https://cdn.collide.com/goods/coin-pack-1000.jpg",
    "stock": 1000,
    "soldCount": 156,
    "coinAmount": 1000,
    "recommended": true,
    "hot": true,
    "createTime": "2024-01-15 10:30:00",
    "updateTime": "2024-01-15 15:20:00",
    "creatorId": 10001
  }
}
```

#### 分页查询商品列表
```http
GET /api/v1/goods/list?pageNo=1&pageSize=20&type=COIN&recommended=true
```

**功能**: 用户浏览可购买商品列表
**查询参数**: 
- `pageNo`: 页码 (默认1)
- `pageSize`: 每页大小 (默认20)
- `type`: 商品类型 (COIN/SUBSCRIPTION)
- `recommended`: 是否推荐
- `hot`: 是否热门
- `keyword`: 关键词搜索

**特性**: 
- 仅返回ON_SALE状态商品
- 支持多维度筛选
- L2缓存加速

#### 获取推荐商品
```http
GET /api/v1/goods/recommended?limit=10
```

**功能**: 获取推荐商品列表
**查询参数**: 
- `limit`: 最大返回数量 (默认10)

**特性**: 
- L3缓存优化
- 按销量和时间排序

#### 获取热门商品
```http
GET /api/v1/goods/hot?limit=10
```

**功能**: 获取热门商品列表
**查询参数**: 
- `limit`: 最大返回数量 (默认10)

**特性**: 
- L3缓存优化
- 实时热度计算

#### 检查商品可购买性
```http
GET /api/v1/goods/{goodsId}/purchasable?quantity=1
```

**功能**: 检查商品当前是否可购买
**路径参数**: 
- `goodsId`: 商品ID

**查询参数**: 
- `quantity`: 购买数量 (默认1)

**响应**: 
```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## 📊 数据模型

### GoodsInfo (商品信息DTO)

```json
{
  "goodsId": "Long | 商品ID",
  "name": "String | 商品名称",
  "description": "String | 商品描述", 
  "type": "String | 商品类型 (COIN/SUBSCRIPTION)",
  "status": "String | 商品状态 (DRAFT/ON_SALE/OFF_SALE/SOLD_OUT/DISABLED)",
  "price": "BigDecimal | 商品价格（元）",
  "imageUrl": "String | 商品图片URL",
  "stock": "Integer | 库存数量 (-1表示无限库存)",
  "soldCount": "Integer | 已售数量",
  "subscriptionDays": "Integer | 订阅周期天数 (订阅类商品)",
  "coinAmount": "Integer | 金币数量 (金币类商品)",
  "recommended": "Boolean | 是否推荐",
  "hot": "Boolean | 是否热门",
  "createTime": "LocalDateTime | 创建时间",
  "updateTime": "LocalDateTime | 更新时间", 
  "creatorId": "Long | 创建者ID"
}
```

### GoodsCreateRequest (商品创建请求)

```json
{
  "name": "String | 商品名称 [必填]",
  "description": "String | 商品描述",
  "type": "String | 商品类型 (COIN/SUBSCRIPTION) [必填]",
  "price": "BigDecimal | 商品价格 [必填]",
  "imageUrl": "String | 商品图片URL",
  "stock": "Integer | 库存数量",
  "subscriptionDays": "Integer | 订阅天数 (订阅类商品必填)",
  "coinAmount": "Integer | 金币数量 (金币类商品必填)",
  "recommended": "Boolean | 是否推荐",
  "hot": "Boolean | 是否热门",
  "idempotentKey": "String | 幂等性键 (可选)"
}
```

### GoodsUpdateRequest (商品更新请求)

```json
{
  "goodsId": "Long | 商品ID [必填]",
  "name": "String | 商品名称",
  "description": "String | 商品描述",
  "price": "BigDecimal | 商品价格",
  "imageUrl": "String | 商品图片URL",
  "subscriptionDays": "Integer | 订阅天数",
  "coinAmount": "Integer | 金币数量",
  "recommended": "Boolean | 是否推荐",
  "hot": "Boolean | 是否热门",
  "idempotentKey": "String | 幂等性键 (可选)"
}
```

### GoodsStockRequest (库存操作请求)

```json
{
  "goodsId": "Long | 商品ID [必填]",
  "operationType": "String | 操作类型 (SET/INCREASE/DECREASE) [必填]",
  "stock": "Integer | 库存数量 [必填]",
  "idempotentKey": "String | 幂等性键 (可选)"
}
```

### GoodsPageQueryRequest (分页查询请求)

```json
{
  "pageNo": "Integer | 页码 (默认1)",
  "pageSize": "Integer | 每页大小 (默认20)",
  "type": "String | 商品类型筛选",
  "status": "String | 状态筛选",
  "recommended": "Boolean | 推荐筛选",
  "hot": "Boolean | 热门筛选",
  "keyword": "String | 关键词搜索",
  "creatorId": "Long | 创建者筛选",
  "minPrice": "BigDecimal | 最低价格",
  "maxPrice": "BigDecimal | 最高价格"
}
```

---

## ❌ 错误码定义

### 系统级错误
| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| `PARAM_ERROR` | 参数错误 | 请求参数不合法 |
| `SYSTEM_ERROR` | 系统异常，请稍后重试 | 系统内部错误 |
| `DUPLICATE_REQUEST` | 重复请求，请勿重复提交 | 幂等性检查失败 |

### 业务级错误
| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| `GOODS_NOT_FOUND` | 商品不存在 | 指定商品ID不存在 |
| `GOODS_NOT_AVAILABLE` | 商品当前不可购买 | 商品未上架或已下架 |
| `STOCK_NOT_ENOUGH` | 库存不足 | 商品库存不足以满足购买需求 |
| `STOCK_UPDATE_CONFLICT` | 库存更新失败，可能存在并发冲突，请重试 | 乐观锁冲突 |
| `INVALID_GOODS_TYPE` | 无效的商品类型 | 商品类型不在支持范围内 |
| `INVALID_GOODS_STATUS` | 无效的商品状态 | 商品状态不在支持范围内 |
| `GOODS_ALREADY_ON_SALE` | 商品已上架 | 重复上架操作 |
| `GOODS_ALREADY_OFF_SALE` | 商品已下架 | 重复下架操作 |
| `CREATE_GOODS_ERROR` | 创建商品失败 | 商品创建过程中出现错误 |
| `UPDATE_GOODS_ERROR` | 更新商品失败 | 商品更新过程中出现错误 |

---

## 🔧 使用示例

### 示例1: 创建金币类商品 (RPC)

```java
// 1. 准备创建请求
GoodsCreateRequest request = new GoodsCreateRequest();
request.setName("超值金币包");
request.setDescription("购买即可获得1000金币，性价比超高！");
request.setType("COIN");
request.setPrice(new BigDecimal("9.99"));
request.setImageUrl("https://cdn.collide.com/goods/coin-pack-1000.jpg");
request.setStock(1000);
request.setCoinAmount(1000);
request.setRecommended(true);
request.setHot(true);
request.setIdempotentKey("create_goods_" + System.currentTimeMillis());

// 2. 调用RPC接口
SingleResponse<Long> response = goodsFacadeService.createGoods(request);

// 3. 处理响应
if (response.isSuccess()) {
    Long goodsId = response.getData();
    System.out.println("商品创建成功，ID：" + goodsId);
} else {
    System.out.println("创建失败：" + response.getErrMessage());
}
```

### 示例2: 更新库存 (RPC)

```java
// 1. 准备库存更新请求
GoodsStockRequest stockRequest = new GoodsStockRequest();
stockRequest.setGoodsId(1001L);
stockRequest.setOperationType("DECREASE");
stockRequest.setStock(10);
stockRequest.setIdempotentKey("stock_update_" + orderId);

// 2. 调用库存更新接口
SingleResponse<Void> response = goodsFacadeService.updateStock(stockRequest);

// 3. 处理响应
if (response.isSuccess()) {
    System.out.println("库存更新成功");
} else if ("STOCK_UPDATE_CONFLICT".equals(response.getErrCode())) {
    // 乐观锁冲突，建议重试
    System.out.println("并发冲突，请重试：" + response.getErrMessage());
} else if ("STOCK_NOT_ENOUGH".equals(response.getErrCode())) {
    System.out.println("库存不足：" + response.getErrMessage());
} else {
    System.out.println("更新失败：" + response.getErrMessage());
}
```

### 示例3: 用户浏览商品 (REST API)

```javascript
// 1. 获取推荐商品列表
fetch('/api/v1/goods/recommended?limit=5')
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      console.log('推荐商品：', data.data);
    }
  });

// 2. 搜索金币类商品
fetch('/api/v1/goods/list?type=COIN&keyword=金币&pageNo=1&pageSize=10')
  .then(response => response.json())
  .then(data => {
    if (data.code === 200) {
      console.log('搜索结果：', data.data.records);
      console.log('总数：', data.data.total);
    }
  });

// 3. 检查商品可购买性
fetch('/api/v1/goods/1001/purchasable?quantity=2')
  .then(response => response.json())
  .then(data => {
    if (data.code === 200 && data.data === true) {
      console.log('商品可以购买');
    } else {
      console.log('商品当前不可购买');
    }
  });
```

### 示例4: 批量上架商品 (RPC)

```java
// 1. 准备商品ID列表
List<Long> goodsIds = Arrays.asList(1001L, 1002L, 1003L);

// 2. 批量上架
SingleResponse<Object> response = goodsFacadeService.batchPutOnSale(goodsIds);

// 3. 处理响应
if (response.isSuccess()) {
    System.out.println("批量上架成功");
} else {
    System.out.println("批量上架失败：" + response.getErrMessage());
}
```

---

## 📈 性能优化

### 缓存策略
1. **L1缓存**: 商品详情缓存，TTL 30分钟
2. **L2缓存**: 商品列表缓存，TTL 10分钟  
3. **L3缓存**: 热门推荐缓存，TTL 5分钟
4. **L4缓存**: 搜索结果缓存，TTL 3分钟

### 查询优化
1. **索引设计**: 基于查询场景的复合索引
2. **分页查询**: 支持深度分页优化
3. **单表查询**: 避免JOIN操作，提升查询效率
4. **数据预热**: 启动时预加载热门数据

### 并发控制
1. **乐观锁**: 基于版本号的库存并发控制
2. **分布式锁**: Redis实现的幂等性保障
3. **事务管理**: 精确的事务边界控制
4. **重试机制**: 自动化并发冲突重试

---

## 🔍 监控与告警

### 关键指标
- 商品创建成功率
- 库存更新并发冲突率
- 缓存命中率
- 接口响应时间
- 商品查询QPS

### 告警规则
- 库存并发冲突率 > 5%
- 缓存命中率 < 80%
- 接口响应时间 > 500ms
- 商品创建失败率 > 1%

---

**文档版本**: v1.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Team 