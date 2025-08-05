# Content Payment Facade Service API 文档

**Facade服务**: ContentPaymentFacadeService  
**版本**: 2.0.0 (极简版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentPaymentFacadeService`  
**方法数量**: 12个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容付费配置门面服务接口 - 极简版，与ContentPaymentService保持一致，12个核心方法提供完整的付费配置管理功能。

**核心能力**:
- **配置管理**: 付费配置的创建、查询、删除
- **价格管理**: 价格设置、折扣计算、实际价格计算
- **权限验证**: 访问权限检查、购买权限验证
- **统计分析**: 销售统计、收入分析、转化率统计
- **业务逻辑**: 内容状态同步、销售数据更新

**付费类型**:
```
FREE(免费) → COIN_PAY(金币付费) → VIP_FREE(VIP免费) → VIP_ONLY(VIP专享)
```

**设计理念**:
- **极简设计**: 12个核心方法替代原有42个方法
- **万能查询**: 统一的条件查询接口
- **统一权限**: 集中的权限验证机制
- **高性能**: 优化的价格计算和统计查询

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 配置查询和删除 |
| **万能查询功能** | 2个 | 条件查询、推荐查询 |
| **状态管理功能** | 2个 | 状态更新、批量操作 |
| **价格管理功能** | 2个 | 价格更新、实际价格计算 |
| **权限验证功能** | 1个 | 访问权限检查 |
| **销售统计功能** | 1个 | 销售统计更新 |
| **统计分析功能** | 1个 | 付费统计信息 |
| **业务逻辑功能** | 1个 | 内容状态同步 |

---

## 🔧 1. 核心CRUD功能 (2个方法)

### 1.1 根据ID获取付费配置

**方法**: `getPaymentConfigById(Long id)`

**描述**: 根据配置ID获取付费配置详情

**参数**:
- `id` (Long): 配置ID

**返回值**: `Result<ContentPaymentConfigResponse>`

**调用示例**:
```java
Result<ContentPaymentConfigResponse> result = contentPaymentFacadeService.getPaymentConfigById(12345L);
if (result.isSuccess()) {
    ContentPaymentConfigResponse config = result.getData();
    System.out.println("付费类型: " + config.getPaymentType());
    System.out.println("价格: " + config.getPrice());
    System.out.println("销量: " + config.getSalesCount());
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 12345,
    "contentId": 67890,
    "contentTitle": "我的玄幻小说",
    "contentType": "NOVEL",
    "authorId": 2001,
    "authorNickname": "知名作家",
    "paymentType": "COIN_PAY",
    "price": 100,
    "originalPrice": 120,
    "discountStartTime": "2024-01-01T00:00:00",
    "discountEndTime": "2024-01-31T23:59:59",
    "isPermanent": true,
    "trialEnabled": true,
    "trialChapters": 3,
    "salesCount": 1500,
    "totalRevenue": 150000,
    "status": "ACTIVE",
    "createTime": "2024-01-01T10:00:00",
    "updateTime": "2024-01-15T14:30:00"
  }
}
```

### 1.2 删除付费配置

**方法**: `deletePaymentConfig(Long id, Long operatorId)`

**描述**: 删除指定的付费配置

**参数**:
- `id` (Long): 配置ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPaymentFacadeService.deletePaymentConfig(12345L, 2001L);
if (result.isSuccess() && result.getData()) {
    System.out.println("付费配置删除成功");
}
```

---

## 🔍 2. 万能查询功能 (2个方法)

### 2.1 万能条件查询付费配置列表

**方法**: `getPaymentsByConditions(Long contentId, String paymentType, String status, Long minPrice, Long maxPrice, Boolean trialEnabled, Boolean isPermanent, Boolean hasDiscount, String orderBy, String orderDirection, Integer currentPage, Integer pageSize)`

**描述**: 根据多种条件查询付费配置列表，替代所有具体查询API

**核心功能**: 
- 替代`getPaymentConfigByContentId`、`getFreeContentConfigs`、`getCoinPayContentConfigs`等方法
- 支持按内容、付费类型、价格范围等多维度查询
- 支持试读、永久、折扣等特性筛选

**参数**:
- `contentId` (Long): 内容ID（可选）
- `paymentType` (String): 付费类型（可选：FREE、COIN_PAY、VIP_FREE、VIP_ONLY）
- `status` (String): 状态（可选）
- `minPrice` (Long): 最小价格（可选）
- `maxPrice` (Long): 最大价格（可选）
- `trialEnabled` (Boolean): 是否支持试读（可选）
- `isPermanent` (Boolean): 是否永久（可选）
- `hasDiscount` (Boolean): 是否有折扣（可选）
- `orderBy` (String): 排序字段（可选：createTime、price、salesCount、totalRevenue）
- `orderDirection` (String): 排序方向（可选：ASC、DESC）
- `currentPage` (Integer): 当前页码（可选，不分页时传null）
- `pageSize` (Integer): 页面大小（可选，不分页时传null）

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

**调用示例**:
```java
// 查询指定内容的付费配置
Result<PageResponse<ContentPaymentConfigResponse>> result1 = contentPaymentFacadeService
    .getPaymentsByConditions(67890L, null, "ACTIVE", null, null, null, null, null,
                           "createTime", "DESC", null, null);

// 查询金币付费的内容（按销量排序）
Result<PageResponse<ContentPaymentConfigResponse>> result2 = contentPaymentFacadeService
    .getPaymentsByConditions(null, "COIN_PAY", "ACTIVE", null, null, null, null, null,
                           "salesCount", "DESC", 1, 20);

// 查询价格在50-200之间的付费内容
Result<PageResponse<ContentPaymentConfigResponse>> result3 = contentPaymentFacadeService
    .getPaymentsByConditions(null, null, "ACTIVE", 50L, 200L, null, null, null,
                           "price", "ASC", 1, 50);

// 查询支持试读且有折扣的内容
Result<PageResponse<ContentPaymentConfigResponse>> result4 = contentPaymentFacadeService
    .getPaymentsByConditions(null, null, "ACTIVE", null, null, true, null, true,
                           "totalRevenue", "DESC", 1, 30);
```

### 2.2 推荐付费内容查询

**方法**: `getRecommendedPayments(String strategy, String paymentType, List<Long> excludeContentIds, Integer limit)`

**描述**: 获取推荐的付费内容，替代所有推荐类查询

**核心功能**: 
- 替代`getHotPaidContent`、`getHighValueContent`、`getValueForMoneyContent`、`getSalesRanking`等方法
- 支持多种推荐策略
- 支持内容类型和排除列表

**参数**:
- `strategy` (String): 推荐策略（HOT、HIGH_VALUE、VALUE_FOR_MONEY、SALES_RANKING）
- `paymentType` (String): 付费类型（可选）
- `excludeContentIds` (List<Long>): 排除的内容ID列表
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

**调用示例**:
```java
// 获取热门付费内容
List<Long> excludeIds = Arrays.asList(67890L, 67891L);
Result<List<ContentPaymentConfigResponse>> result1 = contentPaymentFacadeService
    .getRecommendedPayments("HOT", "COIN_PAY", excludeIds, 10);

// 获取高价值内容（性价比高）
Result<List<ContentPaymentConfigResponse>> result2 = contentPaymentFacadeService
    .getRecommendedPayments("VALUE_FOR_MONEY", null, Collections.emptyList(), 20);

// 获取销量排行
Result<List<ContentPaymentConfigResponse>> result3 = contentPaymentFacadeService
    .getRecommendedPayments("SALES_RANKING", null, null, 50);

if (result1.isSuccess()) {
    List<ContentPaymentConfigResponse> hotContents = result1.getData();
    System.out.println("热门付费内容数量: " + hotContents.size());
}
```

---

## ⚙️ 3. 状态管理功能 (2个方法)

### 3.1 更新付费配置状态

**方法**: `updatePaymentStatus(Long configId, String status)`

**描述**: 更新付费配置状态

**参数**:
- `configId` (Long): 配置ID
- `status` (String): 目标状态（ACTIVE/INACTIVE/EXPIRED）

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 激活付费配置
Result<Boolean> result1 = contentPaymentFacadeService
    .updatePaymentStatus(12345L, "ACTIVE");

// 停用付费配置
Result<Boolean> result2 = contentPaymentFacadeService
    .updatePaymentStatus(12345L, "INACTIVE");

if (result1.isSuccess() && result1.getData()) {
    System.out.println("状态更新成功");
}
```

### 3.2 批量更新状态

**方法**: `batchUpdateStatus(List<Long> ids, String status)`

**描述**: 批量更新付费配置状态

**参数**:
- `ids` (List<Long>): 配置ID列表
- `status` (String): 目标状态

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> configIds = Arrays.asList(12345L, 12346L, 12347L);
Result<Boolean> result = contentPaymentFacadeService.batchUpdateStatus(configIds, "ACTIVE");
if (result.isSuccess() && result.getData()) {
    System.out.println("批量状态更新成功");
}
```

---

## 💰 4. 价格管理功能 (2个方法)

### 4.1 更新付费配置价格信息

**方法**: `updatePaymentPrice(Long configId, Long price, Long originalPrice, LocalDateTime discountStartTime, LocalDateTime discountEndTime)`

**描述**: 更新付费配置的价格信息

**参数**:
- `configId` (Long): 配置ID
- `price` (Long): 当前价格
- `originalPrice` (Long): 原价
- `discountStartTime` (LocalDateTime): 折扣开始时间
- `discountEndTime` (LocalDateTime): 折扣结束时间

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 设置折扣价格
LocalDateTime discountStart = LocalDateTime.now();
LocalDateTime discountEnd = LocalDateTime.now().plusDays(30);

Result<Boolean> result = contentPaymentFacadeService
    .updatePaymentPrice(12345L, 80L, 100L, discountStart, discountEnd);

if (result.isSuccess() && result.getData()) {
    System.out.println("价格更新成功");
}
```

### 4.2 计算用户实际需要支付的价格

**方法**: `calculateActualPrice(Long userId, Long contentId)`

**描述**: 根据用户级别、内容配置计算实际需要支付的价格

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Long>`

**调用示例**:
```java
Result<Long> result = contentPaymentFacadeService.calculateActualPrice(1001L, 67890L);
if (result.isSuccess()) {
    Long actualPrice = result.getData();
    if (actualPrice == 0) {
        System.out.println("用户可以免费访问");
    } else {
        System.out.println("用户需要支付: " + actualPrice + " 金币");
    }
}
```

---

## 🔐 5. 权限验证功能 (1个方法)

### 5.1 检查访问权限

**方法**: `checkAccessPermission(Long userId, Long contentId)`

**描述**: 检查访问权限，包含购买权限和免费访问检查

**核心功能**: 
- 替代`checkPurchasePermission`、`checkFreeAccess`、`getAccessPolicy`等方法
- 统一的权限验证接口
- 返回详细的权限信息

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
Result<Map<String, Object>> result = contentPaymentFacadeService.checkAccessPermission(1001L, 67890L);
if (result.isSuccess()) {
    Map<String, Object> permission = result.getData();
    Boolean canAccess = (Boolean) permission.get("canAccess");
    String accessType = (String) permission.get("accessType");
    Long price = (Long) permission.get("price");
    
    if (canAccess) {
        System.out.println("用户可以访问，访问类型: " + accessType);
    } else {
        System.out.println("需要支付 " + price + " 金币");
    }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "canAccess": false,
    "accessType": "COIN_PAY",
    "price": 80,
    "originalPrice": 100,
    "discountAmount": 20,
    "discountReason": "VIP折扣",
    "trialEnabled": true,
    "trialChapters": 3,
    "isPermanent": true,
    "userLevel": "VIP",
    "hasDiscount": true
  }
}
```

---

## 📊 6. 销售统计功能 (1个方法)

### 6.1 更新销售统计

**方法**: `updateSalesStats(Long configId, Long salesIncrement, Long revenueIncrement)`

**描述**: 更新销售统计数据

**参数**:
- `configId` (Long): 配置ID
- `salesIncrement` (Long): 销量增量
- `revenueIncrement` (Long): 收入增量

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 记录一次销售（价格80金币）
Result<Boolean> result = contentPaymentFacadeService.updateSalesStats(12345L, 1L, 80L);
if (result.isSuccess() && result.getData()) {
    System.out.println("销售统计更新成功");
}
```

---

## 📈 7. 统计分析功能 (1个方法)

### 7.1 获取付费统计信息

**方法**: `getPaymentStats(String statsType, Map<String, Object> params)`

**描述**: 获取付费统计信息，替代所有统计分析方法

**核心功能**: 
- 替代`countByPaymentType`、`getPriceStats`、`getTotalSalesStats`、`getConversionStats`等方法
- 支持多种统计类型
- 灵活的参数配置

**参数**:
- `statsType` (String): 统计类型（PAYMENT_TYPE、PRICE、SALES、CONVERSION、REVENUE_ANALYSIS）
- `params` (Map<String, Object>): 统计参数

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
// 获取付费类型统计
Map<String, Object> typeParams = new HashMap<>();
typeParams.put("contentType", "NOVEL");
Result<Map<String, Object>> result1 = contentPaymentFacadeService
    .getPaymentStats("PAYMENT_TYPE", typeParams);

// 获取价格分布统计
Map<String, Object> priceParams = new HashMap<>();
priceParams.put("minPrice", 50L);
priceParams.put("maxPrice", 200L);
Result<Map<String, Object>> result2 = contentPaymentFacadeService
    .getPaymentStats("PRICE", priceParams);

// 获取销售统计
Map<String, Object> salesParams = new HashMap<>();
salesParams.put("authorId", 2001L);
salesParams.put("startDate", "2024-01-01");
salesParams.put("endDate", "2024-01-31");
Result<Map<String, Object>> result3 = contentPaymentFacadeService
    .getPaymentStats("SALES", salesParams);

if (result1.isSuccess()) {
    Map<String, Object> stats = result1.getData();
    System.out.println("免费内容数: " + stats.get("freeCount"));
    System.out.println("付费内容数: " + stats.get("paidCount"));
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "freeCount": 150,
    "coinPayCount": 800,
    "vipFreeCount": 200,
    "vipOnlyCount": 50,
    "totalCount": 1200,
    "avgPrice": 85.5,
    "maxPrice": 500,
    "minPrice": 10,
    "totalRevenue": 2500000,
    "totalSales": 30000
  }
}
```

---

## 🔄 8. 业务逻辑功能 (1个方法)

### 8.1 同步内容状态

**方法**: `syncContentStatus(String operationType, Map<String, Object> operationData)`

**描述**: 统一业务逻辑处理，可实现内容状态同步、批量同步等

**核心功能**: 
- 替代`syncContentStatus`、`batchSyncContentStatus`、`getPriceOptimizationSuggestion`等方法
- 统一的业务逻辑接口
- 支持多种操作类型

**参数**:
- `operationType` (String): 操作类型（SYNC_STATUS、BATCH_SYNC、PRICE_OPTIMIZATION）
- `operationData` (Map<String, Object>): 操作数据

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
// 同步单个内容状态
Map<String, Object> syncData = new HashMap<>();
syncData.put("contentId", 67890L);
syncData.put("newStatus", "PUBLISHED");
Result<Map<String, Object>> result1 = contentPaymentFacadeService
    .syncContentStatus("SYNC_STATUS", syncData);

// 批量同步内容状态
Map<String, Object> batchData = new HashMap<>();
batchData.put("contentIds", Arrays.asList(67890L, 67891L, 67892L));
batchData.put("newStatus", "OFFLINE");
Result<Map<String, Object>> result2 = contentPaymentFacadeService
    .syncContentStatus("BATCH_SYNC", batchData);

// 获取价格优化建议
Map<String, Object> optimizeData = new HashMap<>();
optimizeData.put("contentId", 67890L);
optimizeData.put("targetIncrease", 0.2); // 目标增长20%
Result<Map<String, Object>> result3 = contentPaymentFacadeService
    .syncContentStatus("PRICE_OPTIMIZATION", optimizeData);

if (result3.isSuccess()) {
    Map<String, Object> suggestion = result3.getData();
    System.out.println("建议价格: " + suggestion.get("suggestedPrice"));
    System.out.println("预期收入: " + suggestion.get("expectedRevenue"));
}
```

---

## 🎯 数据模型

### ContentPaymentConfigResponse 付费配置响应对象
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPaymentConfigResponse {
    private Long id;                      // 配置ID
    private Long contentId;               // 内容ID
    private String contentTitle;          // 内容标题
    private String contentType;           // 内容类型
    private Long authorId;                // 作者ID
    private String authorNickname;        // 作者昵称
    private String paymentType;           // 付费类型
    private Long price;                   // 当前价格
    private Long originalPrice;           // 原价
    private LocalDateTime discountStartTime; // 折扣开始时间
    private LocalDateTime discountEndTime;   // 折扣结束时间
    private Boolean isPermanent;          // 是否永久
    private Boolean trialEnabled;         // 是否支持试读
    private Integer trialChapters;        // 试读章节数
    private Long salesCount;              // 销量
    private Long totalRevenue;            // 总收入
    private String status;                // 状态
    private LocalDateTime createTime;     // 创建时间
    private LocalDateTime updateTime;     // 更新时间
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| PAYMENT_CONFIG_NOT_FOUND | 付费配置不存在 | 检查配置ID |
| DELETE_CONFIG_FAILED | 删除配置失败 | 确认操作权限 |
| PRICE_UPDATE_FAILED | 价格更新失败 | 检查价格参数 |
| BATCH_UPDATE_FAILED | 批量更新失败 | 检查配置ID列表 |
| STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| ACCESS_CHECK_FAILED | 权限检查失败 | 检查用户和内容信息 |
| SALES_UPDATE_FAILED | 销售统计更新失败 | 检查统计数据 |
| SYNC_OPERATION_FAILED | 同步操作失败 | 检查操作参数 |

## 🔧 Dubbo配置

### 服务提供者配置
```yaml
dubbo:
  application:
    name: collide-content
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20888
  provider:
    timeout: 5000
    retries: 0
    version: 5.0.0
```

### 服务消费者配置
```yaml
dubbo:
  application:
    name: collide-order
  registry:
    address: nacos://localhost:8848
  consumer:
    timeout: 5000
    retries: 1
    version: 5.0.0
```

## 📈 使用示例

### 付费内容管理服务
```java
@Service
@Slf4j
public class ContentPaymentService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    public AccessResult checkUserAccess(Long userId, Long contentId) {
        try {
            Result<Map<String, Object>> result = paymentFacadeService.checkAccessPermission(userId, contentId);
            if (!result.isSuccess()) {
                return AccessResult.systemError("权限检查失败");
            }
            
            Map<String, Object> permission = result.getData();
            Boolean canAccess = (Boolean) permission.get("canAccess");
            
            if (canAccess) {
                return AccessResult.allowed((String) permission.get("accessType"));
            } else {
                Long price = (Long) permission.get("price");
                return AccessResult.needPay(price, permission);
            }
            
        } catch (Exception e) {
            log.error("检查用户访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return AccessResult.systemError("系统异常");
        }
    }
    
    public PriceResult calculatePrice(Long userId, Long contentId) {
        try {
            Result<Long> result = paymentFacadeService.calculateActualPrice(userId, contentId);
            if (result.isSuccess()) {
                return PriceResult.success(result.getData());
            }
            return PriceResult.failed("价格计算失败");
        } catch (Exception e) {
            log.error("计算价格失败: userId={}, contentId={}", userId, contentId, e);
            return PriceResult.failed("系统异常");
        }
    }
}
```

### 付费内容推荐服务
```java
@Service
public class PaymentRecommendationService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    @Cacheable(value = "hot_paid_contents", key = "#paymentType")
    public List<ContentPaymentConfigResponse> getHotPaidContents(String paymentType, Integer limit) {
        Result<List<ContentPaymentConfigResponse>> result = paymentFacadeService
            .getRecommendedPayments("HOT", paymentType, Collections.emptyList(), limit);
        return result.isSuccess() ? result.getData() : Collections.emptyList();
    }
    
    @Cacheable(value = "value_contents", key = "#limit")
    public List<ContentPaymentConfigResponse> getValueForMoneyContents(Integer limit) {
        Result<List<ContentPaymentConfigResponse>> result = paymentFacadeService
            .getRecommendedPayments("VALUE_FOR_MONEY", null, Collections.emptyList(), limit);
        return result.isSuccess() ? result.getData() : Collections.emptyList();
    }
    
    public List<ContentPaymentConfigResponse> getSalesRanking(Integer limit) {
        Result<List<ContentPaymentConfigResponse>> result = paymentFacadeService
            .getRecommendedPayments("SALES_RANKING", null, null, limit);
        return result.isSuccess() ? result.getData() : Collections.emptyList();
    }
}
```

### 销售统计服务
```java
@Service
public class SalesStatsService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    @Async
    public CompletableFuture<Void> recordSaleAsync(Long configId, Long amount) {
        return CompletableFuture.runAsync(() -> {
            paymentFacadeService.updateSalesStats(configId, 1L, amount);
        });
    }
    
    @Cacheable(value = "payment_type_stats", key = "#contentType")
    public Map<String, Object> getPaymentTypeStats(String contentType) {
        Map<String, Object> params = Map.of("contentType", contentType);
        Result<Map<String, Object>> result = paymentFacadeService.getPaymentStats("PAYMENT_TYPE", params);
        return result.isSuccess() ? result.getData() : Collections.emptyMap();
    }
    
    public Map<String, Object> getAuthorSalesStats(Long authorId, String startDate, String endDate) {
        Map<String, Object> params = Map.of(
            "authorId", authorId,
            "startDate", startDate,
            "endDate", endDate
        );
        Result<Map<String, Object>> result = paymentFacadeService.getPaymentStats("SALES", params);
        return result.isSuccess() ? result.getData() : Collections.emptyMap();
    }
}
```

### 价格管理服务
```java
@Service
public class PriceManagementService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    public boolean setDiscount(Long configId, Long discountPrice, Long originalPrice, int durationDays) {
        try {
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusDays(durationDays);
            
            Result<Boolean> result = paymentFacadeService
                .updatePaymentPrice(configId, discountPrice, originalPrice, startTime, endTime);
            
            return result.isSuccess() && result.getData();
        } catch (Exception e) {
            log.error("设置折扣失败: configId={}, price={}", configId, discountPrice, e);
            return false;
        }
    }
    
    public Map<String, Object> getPriceOptimizationSuggestion(Long contentId, double targetIncrease) {
        try {
            Map<String, Object> data = Map.of(
                "contentId", contentId,
                "targetIncrease", targetIncrease
            );
            
            Result<Map<String, Object>> result = paymentFacadeService
                .syncContentStatus("PRICE_OPTIMIZATION", data);
            
            return result.isSuccess() ? result.getData() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("获取价格优化建议失败: contentId={}", contentId, e);
            return Collections.emptyMap();
        }
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 付费配置: TTL 30分钟
   - 权限检查: TTL 5分钟
   - 推荐列表: TTL 10分钟

2. **查询优化**:
   - 使用万能查询减少接口调用
   - 价格计算使用缓存
   - 统计数据定期预计算

3. **连接池配置**:
   ```yaml
   dubbo:
     consumer:
       connections: 8   # 适中的连接数
       actives: 150     # 每个连接的最大活跃请求数
       timeout: 5000    # 合理超时时间
   ```

4. **异步处理**:
   - 销售统计异步更新
   - 价格变更异步通知
   - 批量操作异步执行

## 🚀 极简设计优势

1. **方法精简**: 从42个方法缩减到12个，学习成本降低71%
2. **万能查询**: 一个方法替代多个具体查询方法
3. **统一权限**: 集中的权限验证机制
4. **智能推荐**: 内置多种推荐策略
5. **业务集成**: 核心业务逻辑内置，简化调用

## 🔗 相关文档

- [ContentPaymentController REST API 文档](../news/content-payment-controller-api.md)
- [ContentPurchaseFacadeService 文档](./content-purchase-facade-service-api.md)
- [ContentFacadeService 文档](./content-facade-service-api.md)
- [ContentChapterFacadeService 文档](./content-chapter-facade-service-api.md)

---

**联系信息**:  
- Facade服务: ContentPaymentFacadeService  
- 版本: 2.0.0 (极简版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31