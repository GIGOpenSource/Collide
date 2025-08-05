# Goods Facade Service API Documentation

**版本**: 2.0.0 (扩展版)  
**服务名**: GoodsFacadeService  
**作者**: GIG Team  
**更新时间**: 2024-01-31

## 概述

商品模块对外服务接口，提供统一的商品管理功能入口。支持四种商品类型（金币充值包、订阅服务、付费内容、实体商品）的完整业务操作，包括缓存优化和分布式事务支持。

## 接口分类

### 1. 基础CRUD操作 (5个方法)
### 2. 查询操作 (7个方法)
### 3. 库存管理 (4个方法)
### 4. 统计操作 (8个方法)
### 5. 状态管理 (5个方法)
### 6. 业务验证 (2个方法)
### 7. 快捷查询 (4个方法)
### 8. 内容同步相关 (6个方法)

**总计**: 41个Facade方法

---

## 1. 基础CRUD操作

### 1.1 createGoods

**方法签名**:
```java
Result<Void> createGoods(GoodsCreateRequest request)
```

**功能描述**: 创建商品

**参数**:
- `request` (GoodsCreateRequest): 商品创建请求对象

**返回值**: `Result<Void>` - 创建结果（仅返回状态）

**缓存策略**: 
- 创建成功后清除商品列表缓存
- 清除统计信息缓存

**业务规则**:
- 验证商品类型和必要字段
- 检查商品名称重复性
- 根据商品类型设置默认值

**示例**:
```java
GoodsCreateRequest request = new GoodsCreateRequest();
request.setName("VIP会员月卡");
request.setGoodsType("SUBSCRIPTION");
request.setCategoryId(1L);
request.setOriginalPrice(29.9);
request.setCoinAmount(299L);

Result<Void> result = goodsFacadeService.createGoods(request);
```

---

### 1.2 getGoodsById

**方法签名**:
```java
Result<GoodsResponse> getGoodsById(Long id)
```

**功能描述**: 根据ID获取商品详情

**参数**:
- `id` (Long): 商品ID

**返回值**: `Result<GoodsResponse>` - 商品信息

**缓存策略**:
- 读缓存：`goods:detail:{id}`，过期时间30分钟
- 自动增加浏览量（异步）

**业务规则**:
- 验证商品ID有效性
- 检查商品状态（已删除商品不返回）
- 自动增加浏览量统计

**异常情况**:
- 商品不存在：返回 `GOODS_NOT_FOUND` 错误
- 商品已删除：返回 `GOODS_NOT_FOUND` 错误

---

### 1.3 updateGoods

**方法签名**:
```java
Result<GoodsResponse> updateGoods(Long id, GoodsCreateRequest request)
```

**功能描述**: 更新商品信息

**参数**:
- `id` (Long): 商品ID
- `request` (GoodsCreateRequest): 更新请求

**返回值**: `Result<GoodsResponse>` - 更新后的商品信息

**缓存策略**:
- 更新缓存：`goods:detail:{id}`
- 清除相关列表缓存
- 清除统计信息缓存

**业务规则**:
- 验证商品存在性
- 检查修改权限
- 保留创建时间和统计数据

---

### 1.4 deleteGoods

**方法签名**:
```java
Result<Void> deleteGoods(Long id)
```

**功能描述**: 删除商品（软删除）

**参数**:
- `id` (Long): 商品ID

**返回值**: `Result<Void>` - 删除结果（仅返回状态）

**缓存策略**:
- 清除商品详情缓存
- 清除相关列表缓存
- 清除统计信息缓存

**业务规则**:
- 执行软删除（设置状态为DELETED）
- 检查商品是否可删除（有未完成订单的商品不可删除）

---

### 1.5 batchDeleteGoods

**方法签名**:
```java
Result<Void> batchDeleteGoods(List<Long> ids)
```

**功能描述**: 批量删除商品

**参数**:
- `ids` (List<Long>): 商品ID列表

**返回值**: `Result<Void>` - 删除结果（仅返回状态）

**缓存策略**:
- 批量清除商品详情缓存
- 清除所有列表缓存
- 清除统计信息缓存

**业务规则**:
- 批量验证商品存在性
- 事务性操作（全部成功或全部失败）

---

## 2. 查询操作

### 2.1 queryGoods

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> queryGoods(GoodsQueryRequest request)
```

**功能描述**: 分页查询商品

**参数**:
- `request` (GoodsQueryRequest): 查询请求

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：基于查询条件生成缓存key
- 过期时间：10分钟

**查询条件**:
- 商品类型、分类ID、商家ID
- 商品状态、关键词搜索
- 价格区间、库存状态
- 排序字段和方向

---

### 2.2 getGoodsByCategory

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getGoodsByCategory(Long categoryId, Integer currentPage, Integer pageSize)
```

**功能描述**: 根据分类查询商品

**参数**:
- `categoryId` (Long): 分类ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:category:{categoryId}:page:{currentPage}:{pageSize}`
- 过期时间：15分钟

---

### 2.3 getGoodsBySeller

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getGoodsBySeller(Long sellerId, Integer currentPage, Integer pageSize)
```

**功能描述**: 根据商家查询商品

**参数**:
- `sellerId` (Long): 商家ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:seller:{sellerId}:page:{currentPage}:{pageSize}`
- 过期时间：15分钟

---

### 2.4 getGoodsByContentId

**方法签名**:
```java
Result<GoodsResponse> getGoodsByContentId(Long contentId, String goodsType)
```

**功能描述**: 根据内容ID获取商品信息

**参数**:
- `contentId` (Long): 内容ID
- `goodsType` (String): 商品类型

**返回值**: `Result<GoodsResponse>` - 商品信息

**缓存策略**:
- 读缓存：`goods:content:{contentId}:{goodsType}`
- 过期时间：30分钟

**业务规则**:
- 用于内容购买流程中获取对应的商品信息
- 仅返回PUBLISHED状态的商品

---

### 2.5 getHotGoods

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getHotGoods(String goodsType, Integer currentPage, Integer pageSize)
```

**功能描述**: 获取热门商品

**参数**:
- `goodsType` (String): 商品类型（可为空）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:hot:{goodsType}:page:{currentPage}:{pageSize}`
- 过期时间：5分钟

**业务规则**:
- 按销量降序排序
- 仅包含PUBLISHED状态的商品

---

### 2.6 searchGoods

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> searchGoods(String keyword, Integer currentPage, Integer pageSize)
```

**功能描述**: 搜索商品

**参数**:
- `keyword` (String): 搜索关键词
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:search:{keyword}:page:{currentPage}:{pageSize}`
- 过期时间：10分钟

**业务规则**:
- 支持商品名称和描述的全文搜索
- 使用MySQL全文索引优化搜索性能

---

### 2.7 getGoodsByPriceRange

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getGoodsByPriceRange(String goodsType, Object minPrice, Object maxPrice, Integer currentPage, Integer pageSize)
```

**功能描述**: 按价格区间查询商品

**参数**:
- `goodsType` (String): 商品类型
- `minPrice` (Object): 最低价格
- `maxPrice` (Object): 最高价格
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：基于价格区间生成缓存key
- 过期时间：10分钟

---

## 3. 库存管理

### 3.1 checkStock

**方法签名**:
```java
Result<Boolean> checkStock(Long goodsId, Integer quantity)
```

**功能描述**: 检查库存是否充足

**参数**:
- `goodsId` (Long): 商品ID
- `quantity` (Integer): 需要数量

**返回值**: `Result<Boolean>` - 检查结果

**缓存策略**:
- 读缓存：`goods:stock:{goodsId}`
- 过期时间：1分钟

**业务规则**:
- 检查商品状态（仅PUBLISHED状态可检查）
- 虚拟商品（订阅、内容等）返回true

---

### 3.2 reduceStock

**方法签名**:
```java
Result<Void> reduceStock(Long goodsId, Integer quantity)
```

**功能描述**: 扣减库存

**参数**:
- `goodsId` (Long): 商品ID
- `quantity` (Integer): 扣减数量

**返回值**: `Result<Void>` - 扣减结果（仅返回状态）

**缓存策略**:
- 更新缓存：`goods:stock:{goodsId}`
- 清除商品详情缓存

**业务规则**:
- 原子性操作（使用数据库行锁）
- 扣减前检查库存充足性
- 虚拟商品跳过库存检查

---

### 3.3 batchReduceStock

**方法签名**:
```java
Result<Void> batchReduceStock(Map<Long, Integer> stockMap)
```

**功能描述**: 批量扣减库存

**参数**:
- `stockMap` (Map<Long, Integer>): 商品ID和扣减数量的映射

**返回值**: `Result<Void>` - 扣减结果（仅返回状态）

**缓存策略**:
- 批量更新库存缓存
- 批量清除商品详情缓存

**业务规则**:
- 事务性操作（全部成功或全部回滚）
- 按商品ID排序避免死锁

---

### 3.4 getLowStockGoods

**方法签名**:
```java
Result<List<GoodsResponse>> getLowStockGoods(Integer threshold)
```

**功能描述**: 查询低库存商品

**参数**:
- `threshold` (Integer): 库存阈值

**返回值**: `Result<List<GoodsResponse>>` - 商品列表

**缓存策略**:
- 读缓存：`goods:lowstock:{threshold}`
- 过期时间：5分钟

**业务规则**:
- 仅查询实体商品
- 按库存数量升序排序

---

## 4. 统计操作

### 4.1 increaseSalesCount

**方法签名**:
```java
Result<Void> increaseSalesCount(Long goodsId, Long count)
```

**功能描述**: 增加商品销量

**参数**:
- `goodsId` (Long): 商品ID
- `count` (Long): 增加数量

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 清除商品详情缓存
- 清除热门商品缓存
- 清除统计信息缓存

**业务规则**:
- 原子性更新销量统计
- 异步更新热门商品排序

**对应Service方法**: `increaseSalesCount`

---

### 4.2 increaseViewCount

**方法签名**:
```java
Result<Void> increaseViewCount(Long goodsId, Long count)
```

**功能描述**: 增加商品浏览量

**参数**:
- `goodsId` (Long): 商品ID
- `count` (Long): 增加数量

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 清除商品详情缓存
- 使用缓存批量更新策略

**业务规则**:
- 高频操作，使用缓存聚合后批量更新
- 允许一定程度的数据延迟

**对应Service方法**: `increaseViewCount`

---

### 4.3 batchIncreaseViewCount

**方法签名**:
```java
Result<Void> batchIncreaseViewCount(Map<Long, Long> viewMap)
```

**功能描述**: 批量增加浏览量

**参数**:
- `viewMap` (Map<Long, Long>): 商品ID和浏览量的映射

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 批量清除商品详情缓存
- 异步批量更新数据库

**业务规则**:
- 用于批量处理缓存中的浏览量数据
- 使用批量SQL优化性能

**对应Service方法**: `batchIncreaseViewCount`

---

### 4.4 countByTypeAndStatus

**方法签名**:
```java
Result<List<Map<String, Object>>> countByTypeAndStatus()
```

**功能描述**: 按类型和状态统计商品

**返回值**: `Result<List<Map<String, Object>>>` - 统计结果

**缓存策略**:
- 读缓存：`goods:stats:typeStatus`
- 过期时间：30分钟

**返回数据结构**:
```json
[
  {
    "goodsType": "SUBSCRIPTION",
    "status": "PUBLISHED", 
    "count": 45
  }
]
```

**对应Service方法**: `countByTypeAndStatus`

---

### 4.5 getGoodsStatistics

**方法签名**:
```java
Result<List<Map<String, Object>>> getGoodsStatistics()
```

**功能描述**: 获取商品统计信息

**返回值**: `Result<List<Map<String, Object>>>` - 统计结果

**缓存策略**:
- 读缓存：`goods:stats:summary`
- 过期时间：30分钟

**业务规则**:
- 提供更丰富的统计数据
- 包含销量、浏览量等聚合信息

**数据来源**: 基于 `countByTypeAndStatus` 的业务层扩展

---

### 4.6 countByCategory

**方法签名**:
```java
Result<Long> countByCategory(Long categoryId, String status)
```

**功能描述**: 根据分类统计商品数量

**参数**:
- `categoryId` (Long): 分类ID
- `status` (String): 商品状态（可为空）

**返回值**: `Result<Long>` - 商品数量

**缓存策略**:
- 读缓存：`goods:count:category:{categoryId}:{status}`
- 过期时间：15分钟

**对应Service方法**: `countByCategory`

---

### 4.7 countBySeller

**方法签名**:
```java
Result<Long> countBySeller(Long sellerId, String status)
```

**功能描述**: 根据商家统计商品数量

**参数**:
- `sellerId` (Long): 商家ID
- `status` (String): 商品状态（可为空）

**返回值**: `Result<Long>` - 商品数量

**缓存策略**:
- 读缓存：`goods:count:seller:{sellerId}:{status}`
- 过期时间：15分钟

**对应Service方法**: `countBySeller`

---

### 4.8 findWithConditions

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> findWithConditions(Long categoryId, Long sellerId, String goodsType, String nameKeyword, Object minPrice, Object maxPrice, Object minCoinPrice, Object maxCoinPrice, Boolean hasStock, String status, String orderBy, String orderDirection, Integer currentPage, Integer pageSize)
```

**功能描述**: 复合条件查询商品

**参数**:
- `categoryId` (Long): 分类ID（可为空）
- `sellerId` (Long): 商家ID（可为空）
- `goodsType` (String): 商品类型（可为空）
- `nameKeyword` (String): 名称关键词（可为空）
- `minPrice` (Object): 最低现金价格（可为空）
- `maxPrice` (Object): 最高现金价格（可为空）
- `minCoinPrice` (Object): 最低金币价格（可为空）
- `maxCoinPrice` (Object): 最高金币价格（可为空）
- `hasStock` (Boolean): 是否有库存（可为空）
- `status` (String): 商品状态（可为空）
- `orderBy` (String): 排序字段（可为空）
- `orderDirection` (String): 排序方向（可为空）
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 商品列表

**缓存策略**:
- 复杂查询，缓存时间较短：5分钟
- 缓存key基于所有查询条件hash生成

**业务规则**:
- 支持多种查询条件和排序方式的组合
- 使用优化的索引提升查询性能

**对应Service方法**: `findWithConditions`

---

## 5. 状态管理

### 5.1 publishGoods

**方法签名**:
```java
Result<Void> publishGoods(Long goodsId)
```

**功能描述**: 上架商品

**参数**:
- `goodsId` (Long): 商品ID

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 更新商品详情缓存
- 清除列表缓存
- 清除统计缓存

**业务规则**:
- 验证商品信息完整性
- 检查上架权限

---

### 5.2 unpublishGoods

**方法签名**:
```java
Result<Void> unpublishGoods(Long goodsId)
```

**功能描述**: 下架商品

**参数**:
- `goodsId` (Long): 商品ID

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 更新商品详情缓存
- 清除列表缓存
- 清除统计缓存

**业务规则**:
- 检查是否有未完成的订单
- 允许紧急下架

---

### 5.3 batchPublishGoods

**方法签名**:
```java
Result<Void> batchPublishGoods(List<Long> goodsIds)
```

**功能描述**: 批量上架商品

**参数**:
- `goodsIds` (List<Long>): 商品ID列表

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 批量更新缓存
- 清除所有相关缓存

**业务规则**:
- 事务性操作
- 批量验证商品状态

---

### 5.4 batchUnpublishGoods

**方法签名**:
```java
Result<Void> batchUnpublishGoods(List<Long> goodsIds)
```

**功能描述**: 批量下架商品

**参数**:
- `goodsIds` (List<Long>): 商品ID列表

**返回值**: `Result<Void>` - 操作结果（仅返回状态）

**缓存策略**:
- 批量更新缓存
- 清除所有相关缓存

---

### 5.5 batchUpdateStatus

**方法签名**:
```java
Result<Integer> batchUpdateStatus(List<Long> goodsIds, String status)
```

**功能描述**: 批量更新商品状态

**参数**:
- `goodsIds` (List<Long>): 商品ID列表
- `status` (String): 新状态

**返回值**: `Result<Integer>` - 操作结果（返回影响行数）

**缓存策略**:
- 批量清除商品详情缓存
- 清除列表缓存和统计缓存

**业务规则**:
- 用于批量上架、下架等操作
- 支持事务回滚

**对应Service方法**: `batchUpdateStatus`

---

## 6. 业务验证

### 6.1 validatePurchase

**方法签名**:
```java
Result<Map<String, Object>> validatePurchase(Long goodsId, Integer quantity)
```

**功能描述**: 验证商品是否可购买

**参数**:
- `goodsId` (Long): 商品ID
- `quantity` (Integer): 购买数量

**返回值**: `Result<Map<String, Object>>` - 验证结果

**返回数据结构**:
```json
{
  "canPurchase": true,
  "reason": null,
  "stock": 100,
  "maxQuantity": 50,
  "needLogin": false,
  "needVip": false
}
```

**业务规则**:
- 检查商品状态
- 检查库存充足性
- 检查用户购买权限
- 检查购买限制

---

### 6.2 getGoodsPurchaseInfo

**方法签名**:
```java
Result<Map<String, Object>> getGoodsPurchaseInfo(Long goodsId, Integer quantity)
```

**功能描述**: 获取商品购买信息（用于订单创建）

**参数**:
- `goodsId` (Long): 商品ID
- `quantity` (Integer): 购买数量

**返回值**: `Result<Map<String, Object>>` - 购买信息

**返回数据结构**:
```json
{
  "goodsId": 1,
  "name": "VIP会员月卡",
  "quantity": 1,
  "unitPrice": 29.9,
  "totalPrice": 29.9,
  "coinAmount": 299,
  "totalCoinAmount": 299,
  "discountAmount": 0,
  "finalAmount": 29.9
}
```

**业务规则**:
- 计算实际购买价格
- 应用优惠策略
- 预计算订单金额

---

## 7. 快捷查询

### 7.1 getCoinPackages

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getCoinPackages(Integer currentPage, Integer pageSize)
```

**功能描述**: 获取金币充值包列表

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:coinPackages:page:{currentPage}:{pageSize}`
- 过期时间：30分钟

**业务规则**:
- 仅返回COIN_PACKAGE类型的商品
- 按金币数量升序排序

---

### 7.2 getSubscriptionServices

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getSubscriptionServices(Integer currentPage, Integer pageSize)
```

**功能描述**: 获取订阅服务列表

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:subscriptions:page:{currentPage}:{pageSize}`
- 过期时间：30分钟

**业务规则**:
- 仅返回SUBSCRIPTION类型的商品
- 按订阅时长排序

---

### 7.3 getContentGoods

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getContentGoods(Integer currentPage, Integer pageSize)
```

**功能描述**: 获取付费内容列表

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:contents:page:{currentPage}:{pageSize}`
- 过期时间：15分钟

**业务规则**:
- 仅返回CONTENT类型的商品
- 按创建时间降序排序

---

### 7.4 getPhysicalGoods

**方法签名**:
```java
Result<PageResponse<GoodsResponse>> getPhysicalGoods(Integer currentPage, Integer pageSize)
```

**功能描述**: 获取实体商品列表

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<GoodsResponse>>` - 分页结果

**缓存策略**:
- 读缓存：`goods:physical:page:{currentPage}:{pageSize}`
- 过期时间：20分钟

**业务规则**:
- 仅返回PHYSICAL类型的商品
- 按销量降序排序

---

## 8. 内容同步相关方法

### 8.1 createGoodsFromContent

**方法签名**:
```java
Result<Void> createGoodsFromContent(Long contentId, String contentTitle, String contentDesc, Long categoryId, String categoryName, Long authorId, String authorNickname, String coverUrl, Long coinPrice, String contentStatus)
```

**功能描述**: 根据内容信息自动创建商品

**参数**:
- `contentId` (Long): 内容ID
- `contentTitle` (String): 内容标题
- `contentDesc` (String): 内容描述
- `categoryId` (Long): 分类ID
- `categoryName` (String): 分类名称
- `authorId` (Long): 作者ID
- `authorNickname` (String): 作者昵称
- `coverUrl` (String): 封面图URL
- `coinPrice` (Long): 金币价格
- `contentStatus` (String): 内容状态

**返回值**: `Result<Void>` - 创建结果

**业务规则**:
- 当新内容发布时调用
- 自动为内容创建对应的商品记录
- 根据内容状态设置商品状态

**缓存策略**:
- 清除相关缓存
- 更新统计信息缓存

---

### 8.2 syncContentToGoods

**方法签名**:
```java
Result<Void> syncContentToGoods(Long contentId, String contentTitle, String contentDesc, Long categoryId, String categoryName, Long authorId, String authorNickname, String coverUrl)
```

**功能描述**: 同步内容信息到商品

**参数**:
- `contentId` (Long): 内容ID
- `contentTitle` (String): 内容标题
- `contentDesc` (String): 内容描述
- `categoryId` (Long): 分类ID
- `categoryName` (String): 分类名称
- `authorId` (Long): 作者ID
- `authorNickname` (String): 作者昵称
- `coverUrl` (String): 封面图URL

**返回值**: `Result<Void>` - 同步结果

**业务规则**:
- 当内容信息更新时调用
- 同步更新对应的商品信息
- 保持内容与商品的一致性

---

### 8.3 syncContentStatusToGoods

**方法签名**:
```java
Result<Void> syncContentStatusToGoods(Long contentId, String contentStatus)
```

**功能描述**: 同步内容状态到商品

**参数**:
- `contentId` (Long): 内容ID
- `contentStatus` (String): 内容状态 (PUBLISHED, OFFLINE, DRAFT, etc.)

**返回值**: `Result<Void>` - 同步结果

**业务规则**:
- 当内容状态变更时调用
- 同步更新商品状态
- 状态映射规则：
  - PUBLISHED → PUBLISHED
  - OFFLINE → OFFLINE  
  - DRAFT → DRAFT
  - DELETED → DELETED

---

### 8.4 syncContentPriceToGoods

**方法签名**:
```java
Result<Void> syncContentPriceToGoods(Long contentId, Long coinPrice, Boolean isActive)
```

**功能描述**: 同步内容价格到商品

**参数**:
- `contentId` (Long): 内容ID
- `coinPrice` (Long): 金币价格
- `isActive` (Boolean): 是否启用付费

**返回值**: `Result<Void>` - 同步结果

**业务规则**:
- 当内容付费配置变更时调用
- 同步更新商品价格
- 根据isActive决定商品状态

---

### 8.5 batchSyncContentGoods

**方法签名**:
```java
Result<Map<String, Object>> batchSyncContentGoods(Integer batchSize)
```

**功能描述**: 批量同步内容商品

**参数**:
- `batchSize` (Integer): 批处理大小

**返回值**: `Result<Map<String, Object>>` - 同步结果（包含处理统计信息）

**返回数据结构**:
```json
{
  "processedCount": 150,
  "successCount": 148, 
  "failureCount": 2,
  "createdCount": 25,
  "updatedCount": 123,
  "skippedCount": 2,
  "errors": ["error1", "error2"]
}
```

**业务规则**:
- 用于系统维护，批量检查和同步内容与商品的一致性
- 分批处理避免长时间锁定
- 记录处理过程和错误信息

---

### 8.6 deleteGoodsByContentId

**方法签名**:
```java
Result<Void> deleteGoodsByContentId(Long contentId)
```

**功能描述**: 删除内容对应的商品

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Void>` - 删除结果

**业务规则**:
- 当内容被删除时调用
- 删除对应的商品记录
- 检查是否有关联的未完成订单

**缓存策略**:
- 清除内容相关的商品缓存
- 更新统计信息缓存

---

## 缓存策略说明

### 缓存键命名规范

| 缓存类型 | 键格式 | 过期时间 |
|----------|--------|----------|
| 商品详情 | `goods:detail:{id}` | 30分钟 |
| 分类商品 | `goods:category:{categoryId}:page:{page}:{size}` | 15分钟 |
| 商家商品 | `goods:seller:{sellerId}:page:{page}:{size}` | 15分钟 |
| 热门商品 | `goods:hot:{type}:page:{page}:{size}` | 5分钟 |
| 搜索结果 | `goods:search:{keyword}:page:{page}:{size}` | 10分钟 |
| 库存信息 | `goods:stock:{id}` | 1分钟 |
| 统计信息 | `goods:stats:{type}` | 30分钟 |
| 内容商品 | `goods:content:{contentId}:{type}` | 30分钟 |

### 缓存更新策略

1. **写透模式**: 更新操作同时更新数据库和缓存
2. **失效模式**: 删除操作清除相关缓存
3. **异步更新**: 统计类数据使用异步更新缓存
4. **批量操作**: 批量操作使用批量缓存更新

## 分布式事务支持

### Dubbo服务配置
```java
@Service
@DubboService(version = "2.0.0", timeout = 5000)
```

### 依赖服务
- `UserFacadeService` (version = "1.0.0"): 用户信息验证

### 事务传播
- 支持分布式事务传播
- 使用Seata进行分布式事务管理
- 关键操作支持事务回滚

## 错误处理

### 常见错误码
- `GOODS_NOT_FOUND`: 商品不存在
- `GOODS_ALREADY_EXISTS`: 商品已存在
- `INSUFFICIENT_STOCK`: 库存不足
- `GOODS_STATUS_ERROR`: 商品状态错误
- `INVALID_GOODS_TYPE`: 无效的商品类型
- `CONTENT_SYNC_ERROR`: 内容同步失败

### 异常处理策略
1. 参数验证失败：返回具体错误信息
2. 业务规则违反：返回业务错误码
3. 系统异常：记录日志并返回通用错误
4. 分布式调用失败：支持降级和重试

## 性能优化

### 查询优化
- 使用MySQL 8.0复合索引
- 支持全文搜索索引
- 分页查询优化

### 缓存优化
- 多级缓存策略
- 缓存预热机制
- 缓存穿透保护

### 批量操作优化
- 批量SQL操作
- 异步处理机制
- 分批处理大数据量

---

**文档版本**: 2.0.0  
**最后更新**: 2024-01-31  
**总接口数**: 41个Facade方法