# Content Payment Facade Service API 文档

**Facade服务**: ContentPaymentFacadeService  
**版本**: 2.0.0 (内容付费版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentPaymentFacadeService`  
**方法数量**: 31个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容付费管理Facade服务提供完整的付费配置管理、权限验证、推荐排行和统计分析的RPC接口。支持多种付费模式，具备灵活的定价策略和精准的权限控制机制。

**核心功能**:
- **配置管理**: 付费配置的CRUD操作
- **权限验证**: 购买权限、访问权限、价格计算
- **推荐系统**: 热门、高价值、性价比内容推荐
- **统计分析**: 销售数据、转化率、收益分析
- **数据同步**: 内容状态同步、收益优化建议

**付费模式**:
- `FREE` - 完全免费
- `COIN_PAY` - 金币付费
- `VIP_FREE` - VIP用户免费
- `VIP_ONLY` - VIP专享内容

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **基础CRUD** | 4个 | 配置查询、删除操作 |
| **查询功能** | 9个 | 多维度配置查询 |
| **销售统计管理** | 2个 | 销售数据更新 |
| **状态管理** | 3个 | 配置状态管理 |
| **权限验证** | 5个 | 权限检查、价格计算 |
| **推荐功能** | 6个 | 内容推荐排行 |
| **统计分析** | 6个 | 数据统计分析 |
| **业务逻辑** | 4个 | 业务逻辑处理 |

---

## 🔧 1. 基础CRUD (4个方法)

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

### 1.2 根据内容ID获取付费配置

**方法**: `getPaymentConfigByContentId(Long contentId)`

**描述**: 根据内容ID获取付费配置

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<ContentPaymentConfigResponse>`

### 1.3 删除付费配置

**方法**: `deletePaymentConfig(Long id, Long operatorId)`

**描述**: 删除指定的付费配置

**参数**:
- `id` (Long): 配置ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

### 1.4 删除内容的付费配置

**方法**: `deleteByContentId(Long contentId, Long operatorId)`

**描述**: 删除指定内容的付费配置

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

---

## 🔍 2. 查询功能 (9个方法)

### 2.1 根据付费类型查询配置列表

**方法**: `getConfigsByPaymentType(String paymentType)`

**描述**: 根据付费类型查询配置列表

**参数**:
- `paymentType` (String): 付费类型 (FREE/COIN_PAY/VIP_FREE/VIP_ONLY)

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

**调用示例**:
```java
Result<List<ContentPaymentConfigResponse>> result = 
    contentPaymentFacadeService.getConfigsByPaymentType("COIN_PAY");
```

### 2.2 查询免费内容配置

**方法**: `getFreeContentConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询免费内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.3 查询金币付费内容配置

**方法**: `getCoinPayContentConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询金币付费内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.4 查询VIP免费内容配置

**方法**: `getVipFreeContentConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询VIP免费内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.5 查询VIP专享内容配置

**方法**: `getVipOnlyContentConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询VIP专享内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.6 根据价格范围查询配置

**方法**: `getConfigsByPriceRange(Long minPrice, Long maxPrice)`

**描述**: 根据价格范围查询配置

**参数**:
- `minPrice` (Long): 最低价格（可选）
- `maxPrice` (Long): 最高价格（可选）

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

**调用示例**:
```java
// 查询价格在50-200金币之间的内容
Result<List<ContentPaymentConfigResponse>> result = 
    contentPaymentFacadeService.getConfigsByPriceRange(50L, 200L);
```

### 2.7 查询支持试读的内容配置

**方法**: `getTrialEnabledConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询支持试读的内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.8 查询永久有效的内容配置

**方法**: `getPermanentContentConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询永久有效的内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

### 2.9 查询限时内容配置

**方法**: `getTimeLimitedConfigs(Integer currentPage, Integer pageSize)`

**描述**: 分页查询限时内容配置

**参数**:
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPaymentConfigResponse>>`

---

## 📊 3. 销售统计管理 (2个方法)

### 3.1 更新销售统计

**方法**: `updateSalesStats(Long contentId, Long salesIncrement, Long revenueIncrement)`

**描述**: 更新指定内容的销售统计

**参数**:
- `contentId` (Long): 内容ID
- `salesIncrement` (Long): 销售增量
- `revenueIncrement` (Long): 收入增量

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 更新销售数据：销量+1，收入+100
Result<Boolean> result = contentPaymentFacadeService.updateSalesStats(67890L, 1L, 100L);
```

### 3.2 重置销售统计

**方法**: `resetSalesStats(Long contentId)`

**描述**: 重置指定内容的销售统计

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

---

## ⚙️ 4. 状态管理 (3个方法)

### 4.1 批量更新状态

**方法**: `batchUpdateStatus(List<Long> contentIds, String status)`

**描述**: 批量更新付费配置状态

**参数**:
- `contentIds` (List<Long>): 内容ID列表
- `status` (String): 目标状态 (ACTIVE/INACTIVE/DELETED)

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> contentIds = Arrays.asList(67890L, 67891L, 67892L);
Result<Boolean> result = contentPaymentFacadeService.batchUpdateStatus(contentIds, "ACTIVE");
```

### 4.2 启用付费配置

**方法**: `enablePaymentConfig(Long contentId, Long operatorId)`

**描述**: 启用指定内容的付费配置

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

### 4.3 禁用付费配置

**方法**: `disablePaymentConfig(Long contentId, Long operatorId)`

**描述**: 禁用指定内容的付费配置

**参数**:
- `contentId` (Long): 内容ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

---

## 🔐 5. 权限验证 (5个方法)

### 5.1 检查用户是否有购买权限

**方法**: `checkPurchasePermission(Long userId, Long contentId)`

**描述**: 检查用户是否有权限购买指定内容

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPaymentFacadeService.checkPurchasePermission(1001L, 67890L);
if (result.isSuccess() && result.getData()) {
    // 用户有购买权限
    System.out.println("用户可以购买此内容");
}
```

### 5.2 检查用户是否可以免费访问

**方法**: `checkFreeAccess(Long userId, Long contentId)`

**描述**: 检查用户是否可以免费访问指定内容

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

### 5.3 获取用户对内容的访问策略

**方法**: `getAccessPolicy(Long userId, Long contentId)`

**描述**: 获取用户对指定内容的访问策略

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
Result<Map<String, Object>> result = contentPaymentFacadeService.getAccessPolicy(1001L, 67890L);
if (result.isSuccess()) {
    Map<String, Object> policy = result.getData();
    Boolean canAccess = (Boolean) policy.get("canAccess");
    String accessType = (String) policy.get("accessType");
    Boolean needPurchase = (Boolean) policy.get("needPurchase");
}
```

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

### 5.4 计算用户实际需要支付的价格

**方法**: `calculateActualPrice(Long userId, Long contentId)`

**描述**: 计算用户购买指定内容的实际价格

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Long>`

**调用示例**:
```java
Result<Long> result = contentPaymentFacadeService.calculateActualPrice(1001L, 67890L);
if (result.isSuccess()) {
    Long actualPrice = result.getData();
    System.out.println("实际支付价格: " + actualPrice + " 金币");
}
```

### 5.5 获取内容的价格信息

**方法**: `getContentPriceInfo(Long contentId)`

**描述**: 获取指定内容的价格信息

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

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

## 🏆 6. 推荐功能 (6个方法)

### 6.1 获取热门付费内容

**方法**: `getHotPaidContent(Integer limit)`

**描述**: 获取热门付费内容排行（按销量排序）

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

**调用示例**:
```java
Result<List<ContentPaymentConfigResponse>> result = 
    contentPaymentFacadeService.getHotPaidContent(10);
```

### 6.2 获取高价值内容

**方法**: `getHighValueContent(Integer limit)`

**描述**: 获取高价值内容排行（按单价排序）

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

### 6.3 获取性价比内容

**方法**: `getValueForMoneyContent(Integer limit)`

**描述**: 获取性价比内容排行（按销量/价格比排序）

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

### 6.4 获取新上线的付费内容

**方法**: `getNewPaidContent(Integer limit)`

**描述**: 获取新上线的付费内容

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

### 6.5 获取销售排行榜

**方法**: `getSalesRanking(Integer limit)`

**描述**: 获取内容销售排行榜

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

### 6.6 获取收入排行榜

**方法**: `getRevenueRanking(Integer limit)`

**描述**: 获取内容收入排行榜

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPaymentConfigResponse>>`

---

## 📈 7. 统计分析 (6个方法)

### 7.1 统计各付费类型的数量

**方法**: `countByPaymentType()`

**描述**: 统计各付费类型的数量

**返回值**: `Result<Map<String, Long>>`

**调用示例**:
```java
Result<Map<String, Long>> result = contentPaymentFacadeService.countByPaymentType();
if (result.isSuccess()) {
    Map<String, Long> stats = result.getData();
    System.out.println("免费内容: " + stats.get("FREE"));
    System.out.println("金币付费: " + stats.get("COIN_PAY"));
    System.out.println("VIP免费: " + stats.get("VIP_FREE"));
    System.out.println("VIP专享: " + stats.get("VIP_ONLY"));
}
```

### 7.2 统计活跃配置数量

**方法**: `countActiveConfigs()`

**描述**: 统计活跃的付费配置数量

**返回值**: `Result<Long>`

### 7.3 获取价格统计信息

**方法**: `getPriceStats()`

**描述**: 获取价格统计信息

**返回值**: `Result<Map<String, Object>>`

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

**方法**: `getTotalSalesStats()`

**描述**: 获取总销售统计信息

**返回值**: `Result<Map<String, Object>>`

### 7.5 获取月度销售统计

**方法**: `getMonthlySalesStats(Integer months)`

**描述**: 获取近期月度销售统计

**参数**:
- `months` (Integer): 月份数

**返回值**: `Result<List<Map<String, Object>>>`

### 7.6 获取付费转化率统计

**方法**: `getConversionStats()`

**描述**: 获取付费转化率统计

**返回值**: `Result<Map<String, Object>>`

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

## 🔄 8. 业务逻辑 (4个方法)

### 8.1 同步内容状态

**方法**: `syncContentStatus(Long contentId, String contentStatus)`

**描述**: 同步内容状态到付费配置

**参数**:
- `contentId` (Long): 内容ID
- `contentStatus` (String): 内容状态

**返回值**: `Result<Boolean>`

### 8.2 批量同步内容状态

**方法**: `batchSyncContentStatus(Map<Long, String> contentStatusMap)`

**描述**: 批量同步内容状态到付费配置

**参数**:
- `contentStatusMap` (Map<Long, String>): 内容状态映射

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Map<Long, String> statusMap = Map.of(
    67890L, "PUBLISHED",
    67891L, "OFFLINE",
    67892L, "PUBLISHED"
);
Result<Boolean> result = contentPaymentFacadeService.batchSyncContentStatus(statusMap);
```

### 8.3 获取内容收益分析

**方法**: `getContentRevenueAnalysis(Long contentId)`

**描述**: 获取指定内容的收益分析

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

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

**方法**: `getPriceOptimizationSuggestion(Long contentId)`

**描述**: 获取指定内容的价格优化建议

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

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
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPaymentConfigResponse {
    private Long id;                        // 配置ID
    private Long contentId;                 // 内容ID
    private String contentTitle;            // 内容标题
    private String paymentType;             // 付费类型
    private Long price;                     // 价格（金币）
    private Long originalPrice;             // 原价
    private Double discountRate;            // 折扣率
    private Long discountAmount;            // 折扣金额
    private Boolean isTrialEnabled;         // 是否支持试读
    private Integer trialChapterCount;      // 试读章节数
    private String validityType;            // 有效期类型
    private Integer validityDays;           // 有效天数
    private Long salesCount;                // 销售数量
    private Long totalRevenue;              // 总收入
    private String status;                  // 状态
    private LocalDateTime createTime;       // 创建时间
    private LocalDateTime updateTime;       // 更新时间
}
```

### AccessPolicy 访问策略对象
```java
@Data
@Builder
public class AccessPolicy {
    private Boolean canAccess;              // 是否可以访问
    private String accessType;              // 访问类型
    private Boolean needPurchase;           // 是否需要购买
    private String paymentType;             // 付费类型
    private Long price;                     // 价格
    private Long originalPrice;             // 原价
    private Double discountRate;            // 折扣率
    private Boolean isTrialEnabled;         // 是否支持试读
    private Integer trialChapters;          // 试读章节数
    private Long userBalance;               // 用户余额
    private Boolean sufficientBalance;      // 余额是否充足
    private String reason;                  // 访问策略说明
}
```

## 🚨 错误代码

| 错误码 | 描述 | 业务场景 |
|--------|------|----------|
| PAYMENT_CONFIG_NOT_FOUND | 付费配置不存在 | 查询不存在的配置 |
| INVALID_PAYMENT_TYPE | 无效的付费类型 | 使用不支持的付费类型 |
| INVALID_PRICE_RANGE | 无效的价格范围 | 价格范围参数错误 |
| INSUFFICIENT_PERMISSION | 权限不足 | 没有操作权限 |
| SALES_STATS_UPDATE_FAILED | 销售统计更新失败 | 统计数据更新异常 |
| BATCH_UPDATE_FAILED | 批量更新失败 | 批量操作异常 |
| PURCHASE_PERMISSION_DENIED | 购买权限被拒绝 | 用户被限制购买 |
| INSUFFICIENT_BALANCE | 余额不足 | 用户金币不够 |
| CONTENT_NOT_FOR_SALE | 内容不可购买 | 内容状态异常 |
| PRICE_CALCULATION_FAILED | 价格计算失败 | 价格计算逻辑异常 |

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
    port: 20886
  provider:
    timeout: 5000
    retries: 0
    version: 5.0.0
```

### 服务消费者配置
```yaml
dubbo:
  application:
    name: collide-payment
  registry:
    address: nacos://localhost:8848
  consumer:
    timeout: 5000
    retries: 1
    version: 5.0.0
```

## 📈 使用示例

### 购买权限验证服务
```java
@Service
@Slf4j
public class PurchasePermissionService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    public PurchaseDecision checkPurchasePermission(Long userId, Long contentId) {
        try {
            // 检查购买权限
            Result<Boolean> permissionResult = paymentFacadeService.checkPurchasePermission(userId, contentId);
            if (!permissionResult.isSuccess() || !permissionResult.getData()) {
                return PurchaseDecision.builder()
                    .canPurchase(false)
                    .reason("无购买权限")
                    .build();
            }
            
            // 获取访问策略
            Result<Map<String, Object>> policyResult = paymentFacadeService.getAccessPolicy(userId, contentId);
            if (!policyResult.isSuccess()) {
                throw new BusinessException("获取访问策略失败");
            }
            
            Map<String, Object> policy = policyResult.getData();
            Boolean canAccess = (Boolean) policy.get("canAccess");
            Boolean needPurchase = (Boolean) policy.get("needPurchase");
            
            if (canAccess) {
                return PurchaseDecision.builder()
                    .canPurchase(false)
                    .reason("已有访问权限，无需购买")
                    .build();
            }
            
            if (needPurchase) {
                // 计算实际价格
                Result<Long> priceResult = paymentFacadeService.calculateActualPrice(userId, contentId);
                Long actualPrice = priceResult.getData();
                
                return PurchaseDecision.builder()
                    .canPurchase(true)
                    .price(actualPrice)
                    .policy(policy)
                    .build();
            }
            
            return PurchaseDecision.builder()
                .canPurchase(false)
                .reason("未知原因")
                .build();
                
        } catch (Exception e) {
            log.error("检查购买权限失败: userId={}, contentId={}", userId, contentId, e);
            throw new BusinessException("系统异常，请稍后重试");
        }
    }
}
```

### 推荐内容服务
```java
@Service
public class ContentRecommendationService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    @Cacheable(value = "content_recommendations", key = "'all'")
    public ContentRecommendations getRecommendations(Integer limit) {
        try {
            CompletableFuture<List<ContentPaymentConfigResponse>> hotFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<List<ContentPaymentConfigResponse>> result = 
                        paymentFacadeService.getHotPaidContent(limit);
                    return result.isSuccess() ? result.getData() : Collections.emptyList();
                });
            
            CompletableFuture<List<ContentPaymentConfigResponse>> valueFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<List<ContentPaymentConfigResponse>> result = 
                        paymentFacadeService.getValueForMoneyContent(limit);
                    return result.isSuccess() ? result.getData() : Collections.emptyList();
                });
            
            CompletableFuture<List<ContentPaymentConfigResponse>> newFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<List<ContentPaymentConfigResponse>> result = 
                        paymentFacadeService.getNewPaidContent(limit);
                    return result.isSuccess() ? result.getData() : Collections.emptyList();
                });
            
            return ContentRecommendations.builder()
                .hotContent(hotFuture.get())
                .valueForMoneyContent(valueFuture.get())
                .newContent(newFuture.get())
                .build();
                
        } catch (Exception e) {
            log.error("获取推荐内容失败", e);
            return ContentRecommendations.empty();
        }
    }
}
```

### 统计分析服务
```java
@Service
public class PaymentAnalyticsService {
    
    @DubboReference(version = "5.0.0", timeout = 10000)
    private ContentPaymentFacadeService paymentFacadeService;
    
    @Cacheable(value = "payment_analytics", key = "'dashboard'", unless = "#result == null")
    public PaymentAnalyticsDashboard getDashboard() {
        try {
            // 并行获取各种统计数据
            CompletableFuture<Map<String, Long>> typesStatsFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<Map<String, Long>> result = paymentFacadeService.countByPaymentType();
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            CompletableFuture<Map<String, Object>> priceStatsFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<Map<String, Object>> result = paymentFacadeService.getPriceStats();
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            CompletableFuture<Map<String, Object>> salesStatsFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<Map<String, Object>> result = paymentFacadeService.getTotalSalesStats();
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            CompletableFuture<Map<String, Object>> conversionStatsFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<Map<String, Object>> result = paymentFacadeService.getConversionStats();
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            return PaymentAnalyticsDashboard.builder()
                .typeStats(typesStatsFuture.get())
                .priceStats(priceStatsFuture.get())
                .salesStats(salesStatsFuture.get())
                .conversionStats(conversionStatsFuture.get())
                .lastUpdateTime(LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            log.error("获取支付分析面板失败", e);
            return PaymentAnalyticsDashboard.empty();
        }
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 付费配置: TTL 30分钟
   - 推荐列表: TTL 10分钟
   - 统计数据: TTL 5分钟

2. **并发控制**:
   ```yaml
   dubbo:
     consumer:
       actives: 100      # 每个方法最大并发数
       timeout: 5000     # 调用超时时间
   ```

3. **异步处理**:
   - 销售统计更新建议异步处理
   - 推荐排行计算建议定时任务

4. **批量操作优化**:
   - 状态同步建议批量处理
   - 价格计算建议缓存结果

## 🔗 相关文档

- [ContentPaymentController REST API 文档](./content-payment-controller-api.md)
- [ContentPurchaseFacadeService 文档](./content-purchase-facade-service-api.md)
- [付费系统设计文档](../design/payment-system-design.md)
- [权限验证机制](../design/permission-design.md)

---

**联系信息**:  
- Facade服务: ContentPaymentFacadeService  
- 版本: 2.0.0 (内容付费版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31