# Content Purchase Facade Service API 文档

**Facade服务**: ContentPurchaseFacadeService  
**版本**: 2.0.0 (极简版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentPurchaseFacadeService`  
**方法数量**: 12个  
**更新时间**: 2024-01-31  

## 🚀 概述

内容购买门面服务接口 - 极简版，与UserContentPurchaseService保持一致，12个核心方法提供完整的购买记录管理功能。

**核心能力**:
- **权限管理**: 购买权限验证、访问权限检查
- **记录管理**: 购买记录的查询和管理
- **状态跟踪**: 购买状态变更和生命周期管理
- **统计分析**: 购买统计和数据分析
- **业务逻辑**: 购买完成、退款等核心业务流程

**购买状态流转**:
```
ACTIVE(有效) → EXPIRED(过期) → REFUNDED(已退款)
            ↘ CANCELLED(已取消)
```

**设计理念**:
- **极简设计**: 12个核心方法替代原有33个方法
- **万能查询**: 统一的条件查询接口
- **统一权限**: 集中的权限验证机制
- **高性能**: 优化的批量操作和查询

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **核心CRUD功能** | 2个 | 购买记录查询和删除 |
| **万能查询功能** | 3个 | 条件查询、推荐查询、过期查询 |
| **权限验证功能** | 1个 | 访问权限检查 |
| **状态管理功能** | 2个 | 状态更新、批量操作 |
| **统计功能** | 1个 | 购买统计信息 |
| **业务逻辑功能** | 3个 | 购买完成、退款、访问记录 |

---

## 🔧 1. 核心CRUD功能 (2个方法)

### 1.1 根据ID获取购买记录

**方法**: `getPurchaseById(Long id)`

**描述**: 根据购买记录ID获取详情

**参数**:
- `id` (Long): 购买记录ID

**返回值**: `Result<ContentPurchaseResponse>`

**调用示例**:
```java
Result<ContentPurchaseResponse> result = contentPurchaseFacadeService.getPurchaseById(12345L);
if (result.isSuccess()) {
    ContentPurchaseResponse purchase = result.getData();
    System.out.println("购买内容: " + purchase.getContentTitle());
    System.out.println("购买价格: " + purchase.getActualPrice());
    System.out.println("购买状态: " + purchase.getStatus());
}
```

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
    "totalReadTime": 7200
  }
}
```

### 1.2 删除购买记录

**方法**: `deletePurchase(Long id)`

**描述**: 逻辑删除指定的购买记录

**参数**:
- `id` (Long): 购买记录ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPurchaseFacadeService.deletePurchase(12345L);
if (result.isSuccess() && result.getData()) {
    System.out.println("购买记录删除成功");
}
```

---

## 🔍 2. 万能查询功能 (3个方法)

### 2.1 万能条件查询购买记录列表

**方法**: `getPurchasesByConditions(Long userId, Long contentId, String contentType, Long orderId, String orderNo, String status, Boolean isValid, Long minAmount, Long maxAmount, String orderBy, String orderDirection, Integer currentPage, Integer pageSize)`

**描述**: 根据多种条件查询购买记录列表，替代所有具体查询API

**核心功能**: 
- 替代`getUserPurchases`、`getContentPurchases`、`getUserValidPurchases`、`getUserPurchasesByContentType`等方法
- 支持按用户、内容、订单等多维度查询
- 支持金额范围和有效性筛选

**参数**:
- `userId` (Long): 用户ID（可选）
- `contentId` (Long): 内容ID（可选）
- `contentType` (String): 内容类型（可选）
- `orderId` (Long): 订单ID（可选）
- `orderNo` (String): 订单号（可选）
- `status` (String): 状态（可选）
- `isValid` (Boolean): 是否有效（可选，true=未过期，false=已过期）
- `minAmount` (Long): 最小金额（可选）
- `maxAmount` (Long): 最大金额（可选）
- `orderBy` (String): 排序字段（可选：createTime、purchaseAmount、accessCount）
- `orderDirection` (String): 排序方向（可选：ASC、DESC）
- `currentPage` (Integer): 当前页码（可选，不分页时传null）
- `pageSize` (Integer): 页面大小（可选，不分页时传null）

**返回值**: `Result<PageResponse<ContentPurchaseResponse>>`

**调用示例**:
```java
// 查询用户的购买记录（按购买时间排序）
Result<PageResponse<ContentPurchaseResponse>> result1 = contentPurchaseFacadeService
    .getPurchasesByConditions(1001L, null, null, null, null, null, null, null, null,
                            "createTime", "DESC", 1, 20);

// 查询指定内容的购买记录
Result<PageResponse<ContentPurchaseResponse>> result2 = contentPurchaseFacadeService
    .getPurchasesByConditions(null, 67890L, null, null, null, null, null, null, null,
                            "createTime", "DESC", 1, 50);

// 查询用户有效的购买记录
Result<PageResponse<ContentPurchaseResponse>> result3 = contentPurchaseFacadeService
    .getPurchasesByConditions(1001L, null, null, null, null, "ACTIVE", true, null, null,
                            "createTime", "DESC", null, null);

// 查询高消费记录（金额>100）
Result<PageResponse<ContentPurchaseResponse>> result4 = contentPurchaseFacadeService
    .getPurchasesByConditions(null, null, null, null, null, null, null, 100L, null,
                            "purchaseAmount", "DESC", 1, 20);
```

### 2.2 推荐购买记录查询

**方法**: `getRecommendedPurchases(String strategy, Long userId, String contentType, List<Long> excludeContentIds, Integer limit)`

**描述**: 获取推荐的购买记录或内容

**参数**:
- `strategy` (String): 推荐策略（HOT、SIMILAR、RECENT）
- `userId` (Long): 用户ID
- `contentType` (String): 内容类型（可选）
- `excludeContentIds` (List<Long>): 排除的内容ID列表
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPurchaseResponse>>`

**调用示例**:
```java
// 获取热门购买推荐
List<Long> excludeIds = Arrays.asList(67890L, 67891L);
Result<List<ContentPurchaseResponse>> result = contentPurchaseFacadeService
    .getRecommendedPurchases("HOT", 1001L, "NOVEL", excludeIds, 10);

if (result.isSuccess()) {
    List<ContentPurchaseResponse> recommendations = result.getData();
    System.out.println("推荐购买数量: " + recommendations.size());
}
```

### 2.3 过期相关查询

**方法**: `getPurchasesByExpireStatus(String type, LocalDateTime beforeTime, Long userId, Integer limit)`

**描述**: 查询过期相关的购买记录，替代getExpiredPurchases、getExpiringSoonPurchases等

**参数**:
- `type` (String): 查询类型（EXPIRED、EXPIRING_SOON）
- `beforeTime` (LocalDateTime): 时间点（可选）
- `userId` (Long): 用户ID（可选）
- `limit` (Integer): 数量限制（可选）

**返回值**: `Result<List<ContentPurchaseResponse>>`

**调用示例**:
```java
// 查询已过期的购买记录
Result<List<ContentPurchaseResponse>> result1 = contentPurchaseFacadeService
    .getPurchasesByExpireStatus("EXPIRED", null, null, 100);

// 查询即将过期的购买记录（7天内）
LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
Result<List<ContentPurchaseResponse>> result2 = contentPurchaseFacadeService
    .getPurchasesByExpireStatus("EXPIRING_SOON", sevenDaysLater, null, 50);

// 查询用户即将过期的购买记录
Result<List<ContentPurchaseResponse>> result3 = contentPurchaseFacadeService
    .getPurchasesByExpireStatus("EXPIRING_SOON", sevenDaysLater, 1001L, 20);
```

---

## 🔐 3. 权限验证功能 (1个方法)

### 3.1 检查访问权限

**方法**: `checkAccessPermission(Long userId, Long contentId)`

**描述**: 检查用户是否有权限访问内容（已购买且未过期）

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPurchaseFacadeService.checkAccessPermission(1001L, 67890L);
if (result.isSuccess() && result.getData()) {
    // 用户有访问权限
    System.out.println("允许用户访问内容");
} else {
    // 用户无访问权限
    System.out.println("需要购买后才能访问");
}
```

---

## ⚙️ 4. 状态管理功能 (2个方法)

### 4.1 更新购买记录状态

**方法**: `updatePurchaseStatus(Long purchaseId, String status)`

**描述**: 更新购买记录状态

**参数**:
- `purchaseId` (Long): 购买记录ID
- `status` (String): 目标状态（ACTIVE/EXPIRED/REFUNDED/CANCELLED）

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 设置购买记录为已过期
Result<Boolean> result1 = contentPurchaseFacadeService
    .updatePurchaseStatus(12345L, "EXPIRED");

// 设置购买记录为已退款
Result<Boolean> result2 = contentPurchaseFacadeService
    .updatePurchaseStatus(12345L, "REFUNDED");

if (result1.isSuccess() && result1.getData()) {
    System.out.println("状态更新成功");
}
```

### 4.2 批量更新购买记录状态

**方法**: `batchUpdateStatus(List<Long> ids, String status)`

**描述**: 批量更新购买记录状态

**参数**:
- `ids` (List<Long>): 购买记录ID列表
- `status` (String): 目标状态

**返回值**: `Result<Boolean>`

**调用示例**:
```java
List<Long> purchaseIds = Arrays.asList(12345L, 12346L, 12347L);
Result<Boolean> result = contentPurchaseFacadeService
    .batchUpdateStatus(purchaseIds, "EXPIRED");
if (result.isSuccess() && result.getData()) {
    System.out.println("批量状态更新成功");
}
```

---

## 📊 5. 统计功能 (1个方法)

### 5.1 获取购买统计信息

**方法**: `getPurchaseStats(String statsType, Map<String, Object> params)`

**描述**: 获取购买统计信息，替代getDiscountStats、getPurchaseStatsByDateRange等

**核心功能**: 
- 替代所有单个统计方法
- 支持多种统计类型
- 灵活的参数配置

**参数**:
- `statsType` (String): 统计类型（USER、CONTENT、DISCOUNT、RANKING、REVENUE_ANALYSIS）
- `params` (Map<String, Object>): 统计参数

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
// 获取用户购买统计
Map<String, Object> userParams = new HashMap<>();
userParams.put("userId", 1001L);
Result<Map<String, Object>> result1 = contentPurchaseFacadeService
    .getPurchaseStats("USER", userParams);

// 获取内容销售统计
Map<String, Object> contentParams = new HashMap<>();
contentParams.put("contentId", 67890L);
Result<Map<String, Object>> result2 = contentPurchaseFacadeService
    .getPurchaseStats("CONTENT", contentParams);

// 获取折扣统计
Map<String, Object> discountParams = new HashMap<>();
discountParams.put("userId", 1001L);
discountParams.put("startDate", "2024-01-01");
discountParams.put("endDate", "2024-01-31");
Result<Map<String, Object>> result3 = contentPurchaseFacadeService
    .getPurchaseStats("DISCOUNT", discountParams);

if (result1.isSuccess()) {
    Map<String, Object> stats = result1.getData();
    System.out.println("总购买数: " + stats.get("totalPurchases"));
    System.out.println("总消费: " + stats.get("totalExpense"));
}
```

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
    "membershipLevel": "VIP"
  }
}
```

---

## 🎯 6. 业务逻辑功能 (3个方法)

### 6.1 处理内容购买完成

**方法**: `completePurchase(Long userId, Long contentId, Long orderId, String orderNo, Long purchaseAmount, Long originalPrice, LocalDateTime expireTime)`

**描述**: 处理内容购买完成，创建购买记录

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID
- `orderId` (Long): 订单ID
- `orderNo` (String): 订单号
- `purchaseAmount` (Long): 实际支付金额
- `originalPrice` (Long): 原价
- `expireTime` (LocalDateTime): 过期时间

**返回值**: `Result<ContentPurchaseResponse>`

**调用示例**:
```java
LocalDateTime expireTime = LocalDateTime.now().plusYears(1); // 1年有效期
Result<ContentPurchaseResponse> result = contentPurchaseFacadeService
    .completePurchase(1001L, 67890L, 111222L, "ORDER2024010112345", 80L, 100L, expireTime);

if (result.isSuccess()) {
    ContentPurchaseResponse purchase = result.getData();
    System.out.println("购买记录创建成功: " + purchase.getId());
    
    // 发送购买成功通知
    notificationService.sendPurchaseSuccessNotification(purchase);
}
```

### 6.2 处理退款

**方法**: `processRefund(Long purchaseId, String refundReason, Long refundAmount)`

**描述**: 处理购买记录的退款

**参数**:
- `purchaseId` (Long): 购买记录ID
- `refundReason` (String): 退款原因
- `refundAmount` (Long): 退款金额

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPurchaseFacadeService
    .processRefund(12345L, "用户主动申请退款", 80L);

if (result.isSuccess() && result.getData()) {
    System.out.println("退款处理成功");
    
    // 发送退款成功通知
    notificationService.sendRefundSuccessNotification(purchaseId);
}
```

### 6.3 记录内容访问

**方法**: `recordContentAccess(Long userId, Long contentId)`

**描述**: 记录用户访问内容，更新访问统计

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
// 在用户访问内容时调用
Result<Boolean> result = contentPurchaseFacadeService.recordContentAccess(1001L, 67890L);
if (result.isSuccess() && result.getData()) {
    System.out.println("访问记录成功");
}
```

---

## 🎯 数据模型

### ContentPurchaseResponse 购买记录响应对象
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentPurchaseResponse {
    private Long id;                      // 购买记录ID
    private Long userId;                  // 用户ID
    private String userNickname;          // 用户昵称
    private Long contentId;               // 内容ID
    private String contentTitle;          // 内容标题
    private String contentType;           // 内容类型
    private Long authorId;                // 作者ID
    private String authorNickname;        // 作者昵称
    private Long orderId;                 // 订单ID
    private String orderNo;               // 订单号
    private String paymentType;           // 付费类型
    private Long originalPrice;           // 原价
    private Long actualPrice;             // 实际支付价格
    private Long discountAmount;          // 优惠金额
    private String discountReason;        // 优惠原因
    private String status;                // 状态
    private LocalDateTime purchaseTime;   // 购买时间
    private LocalDateTime expiryTime;     // 过期时间
    private Long accessCount;             // 访问次数
    private LocalDateTime lastAccessTime; // 最后访问时间
    private Boolean isRead;               // 是否已阅读
    private Long totalReadTime;           // 总阅读时长（秒）
    private Integer remainingDays;        // 剩余天数
    private LocalDateTime createTime;     // 创建时间
    private LocalDateTime updateTime;     // 更新时间
}
```

## 🚨 错误代码

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| PURCHASE_RECORD_NOT_FOUND | 购买记录不存在 | 检查购买记录ID |
| DELETE_PURCHASE_FAILED | 删除购买记录失败 | 确认操作权限 |
| ACCESS_PERMISSION_CHECK_FAILED | 访问权限检查失败 | 检查用户和内容信息 |
| BATCH_UPDATE_FAILED | 批量更新失败 | 检查购买记录ID列表 |
| STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| PURCHASE_COMPLETE_FAILED | 购买完成处理失败 | 检查订单信息 |
| REFUND_PROCESS_FAILED | 退款处理失败 | 检查退款条件 |
| RECORD_ACCESS_FAILED | 记录访问失败 | 检查访问参数 |

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
    port: 20887
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

### 购买流程服务
```java
@Service
@Slf4j
public class ContentPurchaseService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPurchaseFacadeService purchaseFacadeService;
    
    public PurchaseResult processPurchase(Long userId, Long contentId, Long orderId, String orderNo, Long amount, Long originalPrice) {
        try {
            // 1. 检查是否已购买
            Result<Boolean> accessResult = purchaseFacadeService.checkAccessPermission(userId, contentId);
            if (accessResult.isSuccess() && accessResult.getData()) {
                return PurchaseResult.failed("已经购买过该内容");
            }
            
            // 2. 处理购买完成
            LocalDateTime expireTime = LocalDateTime.now().plusYears(1); // 1年有效期
            Result<ContentPurchaseResponse> purchaseResult = purchaseFacadeService
                .completePurchase(userId, contentId, orderId, orderNo, amount, originalPrice, expireTime);
                
            if (!purchaseResult.isSuccess()) {
                return PurchaseResult.failed("创建购买记录失败");
            }
            
            ContentPurchaseResponse purchase = purchaseResult.getData();
            
            // 3. 发送购买成功通知
            sendPurchaseNotification(purchase);
            
            return PurchaseResult.success(purchase);
            
        } catch (Exception e) {
            log.error("处理购买失败: userId={}, contentId={}, orderId={}", 
                userId, contentId, orderId, e);
            return PurchaseResult.failed("系统异常，请稍后重试");
        }
    }
    
    private void sendPurchaseNotification(ContentPurchaseResponse purchase) {
        // 发送购买成功通知
        notificationService.sendPurchaseSuccess(purchase.getUserId(), purchase);
    }
}
```

### 用户购买历史服务
```java
@Service
public class UserPurchaseHistoryService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPurchaseFacadeService purchaseFacadeService;
    
    @Cacheable(value = "user_purchase_stats", key = "#userId")
    public UserPurchaseSummary getUserPurchaseSummary(Long userId) {
        try {
            // 并行获取用户购买信息
            CompletableFuture<Map<String, Object>> statsFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Map<String, Object> params = Map.of("userId", userId);
                    Result<Map<String, Object>> result = purchaseFacadeService.getPurchaseStats("USER", params);
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            CompletableFuture<List<ContentPurchaseResponse>> recentFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<PageResponse<ContentPurchaseResponse>> result = 
                        purchaseFacadeService.getPurchasesByConditions(userId, null, null, null, null, null, null, null, null,
                                                                      "createTime", "DESC", 1, 5);
                    return result.isSuccess() ? result.getData().getRecords() : Collections.emptyList();
                });
            
            CompletableFuture<List<ContentPurchaseResponse>> unreadFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<PageResponse<ContentPurchaseResponse>> result = 
                        purchaseFacadeService.getPurchasesByConditions(userId, null, null, null, null, "ACTIVE", true, null, null,
                                                                      "createTime", "DESC", null, null);
                    return result.isSuccess() ? result.getData().getRecords().stream()
                        .filter(p -> !Boolean.TRUE.equals(p.getIsRead()))
                        .collect(Collectors.toList()) : Collections.emptyList();
                });
            
            return UserPurchaseSummary.builder()
                .stats(statsFuture.get())
                .recentPurchases(recentFuture.get())
                .unreadPurchases(unreadFuture.get())
                .build();
                
        } catch (Exception e) {
            log.error("获取用户购买摘要失败: userId={}", userId, e);
            return UserPurchaseSummary.empty();
        }
    }
}
```

### 内容访问控制服务
```java
@Service
public class ContentAccessControlService {
    
    @DubboReference(version = "5.0.0", timeout = 3000)
    private ContentPurchaseFacadeService purchaseFacadeService;
    
    public AccessDecision checkContentAccess(Long userId, Long contentId) {
        try {
            // 检查访问权限
            Result<Boolean> accessResult = purchaseFacadeService.checkAccessPermission(userId, contentId);
            if (!accessResult.isSuccess()) {
                return AccessDecision.systemError("权限检查失败");
            }
            
            if (accessResult.getData()) {
                // 记录访问行为
                purchaseFacadeService.recordContentAccess(userId, contentId);
                return AccessDecision.allowed("有效购买记录");
            }
            
            return AccessDecision.denied("需要购买");
            
        } catch (Exception e) {
            log.error("检查内容访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return AccessDecision.systemError("系统异常");
        }
    }
    
    public Map<Long, Boolean> batchCheckAccess(Long userId, List<Long> contentIds) {
        try {
            Map<Long, Boolean> result = new HashMap<>();
            for (Long contentId : contentIds) {
                Result<Boolean> accessResult = purchaseFacadeService.checkAccessPermission(userId, contentId);
                result.put(contentId, accessResult.isSuccess() && accessResult.getData());
            }
            return result;
        } catch (Exception e) {
            log.error("批量检查访问权限失败: userId={}, contentIds={}", userId, contentIds, e);
            return Collections.emptyMap();
        }
    }
}
```

### 推荐系统集成
```java
@Service
public class PersonalizedRecommendationService {
    
    @DubboReference(version = "5.0.0", timeout = 5000)
    private ContentPurchaseFacadeService purchaseFacadeService;
    
    @Cacheable(value = "user_recommendations", key = "#userId")
    public List<Long> getUserRecommendations(Long userId, Integer limit) {
        try {
            Result<List<ContentPurchaseResponse>> result = purchaseFacadeService
                .getRecommendedPurchases("HOT", userId, null, Collections.emptyList(), limit);
            
            if (result.isSuccess()) {
                return result.getData().stream()
                    .map(ContentPurchaseResponse::getContentId)
                    .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("获取用户推荐失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }
    
    public RecommendationContext buildRecommendationContext(Long userId) {
        try {
            // 获取用户购买统计
            Map<String, Object> params = Map.of("userId", userId);
            Result<Map<String, Object>> statsResult = purchaseFacadeService.getPurchaseStats("USER", params);
            Map<String, Object> stats = statsResult.isSuccess() ? 
                statsResult.getData() : Collections.emptyMap();
            
            // 获取最近购买
            Result<PageResponse<ContentPurchaseResponse>> recentResult = 
                purchaseFacadeService.getPurchasesByConditions(userId, null, null, null, null, null, null, null, null,
                                                              "createTime", "DESC", 1, 10);
            List<ContentPurchaseResponse> recentPurchases = recentResult.isSuccess() ? 
                recentResult.getData().getRecords() : Collections.emptyList();
            
            return RecommendationContext.builder()
                .userId(userId)
                .purchaseStats(stats)
                .recentPurchases(recentPurchases)
                .favoriteContentType((String) stats.get("favoriteContentType"))
                .build();
                
        } catch (Exception e) {
            log.error("构建推荐上下文失败: userId={}", userId, e);
            return RecommendationContext.empty(userId);
        }
    }
}
```

## 🔧 性能优化建议

1. **缓存策略**:
   - 用户购买记录: TTL 5分钟
   - 访问权限: TTL 2分钟
   - 统计数据: TTL 10分钟

2. **批量操作优化**:
   ```yaml
   dubbo:
     consumer:
       actives: 200      # 提高并发数
       timeout: 5000     # 合理超时时间
   ```

3. **异步处理**:
   - 访问记录异步写入
   - 统计计算异步更新
   - 推荐算法离线计算

4. **查询优化**:
   - 使用万能查询减少接口调用
   - 合理使用分页避免大结果集
   - 权限检查使用缓存

## 🚀 极简设计优势

1. **方法精简**: 从33个方法缩减到12个，学习成本降低65%
2. **万能查询**: 一个方法替代10个具体查询方法
3. **统一权限**: 集中的权限验证机制
4. **批量优化**: 支持批量操作，提升性能
5. **业务集成**: 核心业务逻辑内置，简化调用

## 🔗 相关文档

- [ContentPurchaseController REST API 文档](../news/content-purchase-controller-api.md)
- [ContentPaymentFacadeService 文档](./content-payment-facade-service-api.md)
- [ContentFacadeService 文档](./content-facade-service-api.md)
- [ContentChapterFacadeService 文档](./content-chapter-facade-service-api.md)

---

**联系信息**:  
- Facade服务: ContentPurchaseFacadeService  
- 版本: 2.0.0 (极简版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31