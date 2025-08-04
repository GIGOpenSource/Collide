# Content Purchase Facade Service API 文档

**Facade服务**: ContentPurchaseFacadeService  
**版本**: 2.0.0 (内容付费版)  
**Dubbo版本**: 5.0.0  
**超时时间**: 5000ms  
**服务路径**: `com.gig.collide.api.content.ContentPurchaseFacadeService`  
**方法数量**: 33个  
**更新时间**: 2024-01-31  

## 🚀 概述

用户内容购买记录管理Facade服务提供购买记录的全生命周期管理RPC接口。支持购买权限验证、访问记录统计、过期处理、退款管理等完整的购买流程管理功能。

**核心能力**:
- **权限管理**: 购买权限验证、访问权限检查
- **记录管理**: 购买记录的查询和管理
- **状态跟踪**: 购买状态变更和生命周期管理
- **行为分析**: 用户购买和访问行为分析
- **推荐系统**: 基于购买历史的个性化推荐

**购买状态流转**:
```
ACTIVE(有效) → EXPIRED(过期) → REFUNDED(已退款)
            ↘ CANCELLED(已取消)
```

## 📋 接口分类

| 分类 | 方法数量 | 功能描述 |
|------|----------|----------|
| **基础CRUD** | 2个 | 购买记录查询、删除 |
| **权限验证** | 4个 | 访问权限检查、批量验证 |
| **查询功能** | 10个 | 多维度购买记录查询 |
| **访问记录管理** | 3个 | 访问行为记录和统计 |
| **状态管理** | 6个 | 状态更新、过期处理、退款 |
| **统计分析** | 8个 | 购买统计、收入分析、排行榜 |

---

## 🔧 1. 基础CRUD (2个方法)

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

**方法**: `deletePurchase(Long id, Long operatorId)`

**描述**: 逻辑删除指定的购买记录

**参数**:
- `id` (Long): 购买记录ID
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

---

## 🔐 2. 权限验证 (4个方法)

### 2.1 检查用户是否已购买指定内容

**方法**: `getUserContentPurchase(Long userId, Long contentId)`

**描述**: 获取用户对指定内容的购买记录

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<ContentPurchaseResponse>`

**调用示例**:
```java
Result<ContentPurchaseResponse> result = contentPurchaseFacadeService
    .getUserContentPurchase(1001L, 67890L);
if (result.isSuccess() && result.getData() != null) {
    // 用户已购买该内容
    ContentPurchaseResponse purchase = result.getData();
    if ("ACTIVE".equals(purchase.getStatus())) {
        // 购买记录有效
        System.out.println("用户已购买并可以访问");
    }
}
```

### 2.2 检查用户是否有权限访问内容

**方法**: `hasAccessPermission(Long userId, Long contentId)`

**描述**: 检查用户是否有权限访问指定内容（已购买且未过期）

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPurchaseFacadeService.hasAccessPermission(1001L, 67890L);
if (result.isSuccess() && result.getData()) {
    // 用户有访问权限
    System.out.println("允许用户访问内容");
} else {
    // 用户无访问权限
    System.out.println("需要购买后才能访问");
}
```

### 2.3 获取用户对内容的访问权限详情

**方法**: `getValidPurchase(Long userId, Long contentId)`

**描述**: 获取用户对内容的有效购买记录

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<ContentPurchaseResponse>`

### 2.4 批量检查用户对多个内容的访问权限

**方法**: `batchCheckAccessPermission(Long userId, List<Long> contentIds)`

**描述**: 批量检查用户对多个内容的访问权限

**参数**:
- `userId` (Long): 用户ID
- `contentIds` (List<Long>): 内容ID列表

**返回值**: `Result<Map<Long, Boolean>>`

**调用示例**:
```java
List<Long> contentIds = Arrays.asList(67890L, 67891L, 67892L);
Result<Map<Long, Boolean>> result = contentPurchaseFacadeService
    .batchCheckAccessPermission(1001L, contentIds);
if (result.isSuccess()) {
    Map<Long, Boolean> permissions = result.getData();
    permissions.forEach((contentId, hasAccess) -> {
        System.out.println("内容" + contentId + "访问权限: " + hasAccess);
    });
}
```

---

## 🔍 3. 查询功能 (10个方法)

### 3.1 查询用户的购买记录列表

**方法**: `getUserPurchases(Long userId, Integer currentPage, Integer pageSize)`

**描述**: 分页查询用户的购买记录

**参数**:
- `userId` (Long): 用户ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPurchaseResponse>>`

**调用示例**:
```java
Result<PageResponse<ContentPurchaseResponse>> result = contentPurchaseFacadeService
    .getUserPurchases(1001L, 1, 20);
if (result.isSuccess()) {
    PageResponse<ContentPurchaseResponse> pageResponse = result.getData();
    System.out.println("总购买记录数: " + pageResponse.getTotalCount());
    pageResponse.getRecords().forEach(purchase -> {
        System.out.println("购买内容: " + purchase.getContentTitle());
    });
}
```

### 3.2 查询用户的有效购买记录

**方法**: `getUserValidPurchases(Long userId)`

**描述**: 查询用户的有效购买记录

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 3.3 查询内容的购买记录列表

**方法**: `getContentPurchases(Long contentId, Integer currentPage, Integer pageSize)`

**描述**: 分页查询指定内容的购买记录

**参数**:
- `contentId` (Long): 内容ID
- `currentPage` (Integer): 当前页码
- `pageSize` (Integer): 页面大小

**返回值**: `Result<PageResponse<ContentPurchaseResponse>>`

### 3.4 根据订单ID查询购买记录

**方法**: `getPurchaseByOrderId(Long orderId)`

**描述**: 根据订单ID查询购买记录

**参数**:
- `orderId` (Long): 订单ID

**返回值**: `Result<ContentPurchaseResponse>`

### 3.5 根据订单号查询购买记录

**方法**: `getPurchaseByOrderNo(String orderNo)`

**描述**: 根据订单号查询购买记录

**参数**:
- `orderNo` (String): 订单号

**返回值**: `Result<ContentPurchaseResponse>`

### 3.6 查询用户指定内容类型的购买记录

**方法**: `getUserPurchasesByContentType(Long userId, String contentType)`

**描述**: 查询用户购买的指定类型内容

**参数**:
- `userId` (Long): 用户ID
- `contentType` (String): 内容类型 (NOVEL/COMIC/VIDEO/AUDIO)

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 3.7 查询用户指定作者的购买记录

**方法**: `getUserPurchasesByAuthor(Long userId, Long authorId)`

**描述**: 查询用户购买的指定作者内容

**参数**:
- `userId` (Long): 用户ID
- `authorId` (Long): 作者ID

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 3.8 查询用户最近购买的内容

**方法**: `getUserRecentPurchases(Long userId, Integer limit)`

**描述**: 查询用户最近的购买记录

**参数**:
- `userId` (Long): 用户ID
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 3.9 查询用户购买但未访问的内容

**方法**: `getUserUnreadPurchases(Long userId)`

**描述**: 查询用户购买但未阅读的内容

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<List<ContentPurchaseResponse>>`

**调用示例**:
```java
Result<List<ContentPurchaseResponse>> result = contentPurchaseFacadeService
    .getUserUnreadPurchases(1001L);
if (result.isSuccess()) {
    List<ContentPurchaseResponse> unreadPurchases = result.getData();
    System.out.println("未读购买内容数量: " + unreadPurchases.size());
    unreadPurchases.forEach(purchase -> {
        System.out.println("未读内容: " + purchase.getContentTitle());
        System.out.println("购买时间: " + purchase.getPurchaseTime());
    });
}
```

### 3.10 查询高消费金额的购买记录

**方法**: `getHighValuePurchases(Long minAmount, Integer limit)`

**描述**: 查询高消费金额的购买记录

**参数**:
- `minAmount` (Long): 最低金额
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<ContentPurchaseResponse>>`

---

## 📊 4. 访问记录管理 (3个方法)

### 4.1 记录用户访问内容

**方法**: `recordContentAccess(Long userId, Long contentId)`

**描述**: 记录用户访问内容

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

### 4.2 批量更新访问统计

**方法**: `batchUpdateAccessStats(List<Long> purchaseIds)`

**描述**: 批量更新购买记录的访问统计

**参数**:
- `purchaseIds` (List<Long>): 购买记录ID列表

**返回值**: `Result<Boolean>`

### 4.3 获取折扣统计信息

**方法**: `getDiscountStats(Long userId)`

**描述**: 获取用户的优惠统计信息

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<Map<String, Object>>`

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

---

## ⚙️ 5. 状态管理 (6个方法)

### 5.1 处理过期的购买记录

**方法**: `processExpiredPurchases()`

**描述**: 处理过期的购买记录

**返回值**: `Result<Integer>` - 返回处理的记录数

**调用示例**:
```java
// 定时任务调用
Result<Integer> result = contentPurchaseFacadeService.processExpiredPurchases();
if (result.isSuccess()) {
    Integer processedCount = result.getData();
    System.out.println("处理过期购买记录数: " + processedCount);
}
```

### 5.2 查询即将过期的购买记录

**方法**: `getExpiringSoonPurchases(LocalDateTime beforeTime)`

**描述**: 查询即将过期的购买记录

**参数**:
- `beforeTime` (LocalDateTime): 过期时间点

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 5.3 查询已过期的购买记录

**方法**: `getExpiredPurchases()`

**描述**: 查询已过期的购买记录

**返回值**: `Result<List<ContentPurchaseResponse>>`

### 5.4 批量更新购买记录状态

**方法**: `batchUpdateStatus(List<Long> ids, String status)`

**描述**: 批量更新购买记录状态

**参数**:
- `ids` (List<Long>): 购买记录ID列表
- `status` (String): 目标状态 (ACTIVE/EXPIRED/REFUNDED/CANCELLED)

**返回值**: `Result<Boolean>`

### 5.5 退款处理

**方法**: `refundPurchase(Long purchaseId, String reason, Long operatorId)`

**描述**: 处理购买记录的退款申请

**参数**:
- `purchaseId` (Long): 购买记录ID
- `reason` (String): 退款原因
- `operatorId` (Long): 操作人ID

**返回值**: `Result<Boolean>`

**调用示例**:
```java
Result<Boolean> result = contentPurchaseFacadeService.refundPurchase(
    12345L, "用户主动申请退款", 2001L);
if (result.isSuccess() && result.getData()) {
    System.out.println("退款处理成功");
}
```

---

## 📈 6. 统计分析 (8个方法)

### 6.1 统计用户的购买总数

**方法**: `countUserPurchases(Long userId)`

**描述**: 统计用户的购买总数

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<Long>`

### 6.2 统计用户有效购买数

**方法**: `countUserValidPurchases(Long userId)`

**描述**: 统计用户的有效购买数量

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<Long>`

### 6.3 统计内容的购买总数

**方法**: `countContentPurchases(Long contentId)`

**描述**: 统计内容的购买总数

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Long>`

### 6.4 统计内容的收入总额

**方法**: `sumContentRevenue(Long contentId)`

**描述**: 统计内容的收入总额

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Long>`

### 6.5 统计用户的消费总额

**方法**: `sumUserExpense(Long userId)`

**描述**: 统计用户的消费总额

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<Long>`

**调用示例**:
```java
Result<Long> result = contentPurchaseFacadeService.sumUserExpense(1001L);
if (result.isSuccess()) {
    Long totalExpense = result.getData();
    System.out.println("用户总消费: " + totalExpense + " 金币");
}
```

### 6.6 获取热门购买内容排行

**方法**: `getPopularContentRanking(Integer limit)`

**描述**: 获取热门购买内容排行榜

**参数**:
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<Map<String, Object>>>`

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

### 6.7 获取用户购买统计

**方法**: `getUserPurchaseStats(Long userId)`

**描述**: 获取用户的购买统计信息

**参数**:
- `userId` (Long): 用户ID

**返回值**: `Result<Map<String, Object>>`

**调用示例**:
```java
Result<Map<String, Object>> result = contentPurchaseFacadeService.getUserPurchaseStats(1001L);
if (result.isSuccess()) {
    Map<String, Object> stats = result.getData();
    System.out.println("总购买数: " + stats.get("totalPurchases"));
    System.out.println("总消费: " + stats.get("totalExpense"));
    System.out.println("偏爱类型: " + stats.get("favoriteContentType"));
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

### 6.8 获取内容销售统计

**方法**: `getContentSalesStats(Long contentId)`

**描述**: 获取内容的销售统计信息

**参数**:
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

---

## 🎯 7. 业务逻辑 (4个方法)

### 7.1 处理订单支付成功后的购买记录创建

**方法**: `handleOrderPaymentSuccess(Long orderId)`

**描述**: 处理订单支付成功后的购买记录创建

**参数**:
- `orderId` (Long): 订单ID

**返回值**: `Result<ContentPurchaseResponse>`

**调用示例**:
```java
// 支付成功回调
Result<ContentPurchaseResponse> result = contentPurchaseFacadeService
    .handleOrderPaymentSuccess(111222L);
if (result.isSuccess()) {
    ContentPurchaseResponse purchase = result.getData();
    System.out.println("购买记录创建成功: " + purchase.getId());
    // 发送购买成功通知
    notificationService.sendPurchaseSuccessNotification(purchase);
}
```

### 7.2 验证购买权限

**方法**: `validatePurchasePermission(Long userId, Long contentId)`

**描述**: 验证用户是否有权限购买指定内容

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Boolean>`

### 7.3 计算内容访问权限

**方法**: `calculateContentAccess(Long userId, Long contentId)`

**描述**: 计算用户对内容的访问权限详情

**参数**:
- `userId` (Long): 用户ID
- `contentId` (Long): 内容ID

**返回值**: `Result<Map<String, Object>>`

### 7.4 获取用户的内容推荐

**方法**: `getUserContentRecommendations(Long userId, Integer limit)`

**描述**: 获取基于购买历史的内容推荐

**参数**:
- `userId` (Long): 用户ID
- `limit` (Integer): 返回数量限制

**返回值**: `Result<List<Long>>` - 返回推荐的内容ID列表

**调用示例**:
```java
Result<List<Long>> result = contentPurchaseFacadeService
    .getUserContentRecommendations(1001L, 10);
if (result.isSuccess()) {
    List<Long> recommendedContentIds = result.getData();
    System.out.println("推荐内容数量: " + recommendedContentIds.size());
    // 根据内容ID获取详细信息
    recommendedContentIds.forEach(contentId -> {
        // 调用内容服务获取详情
    });
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

### UserPurchaseStats 用户购买统计对象
```java
@Data
@Builder
public class UserPurchaseStats {
    private Long totalPurchases;          // 总购买数
    private Long validPurchases;          // 有效购买数
    private Long expiredPurchases;        // 过期购买数
    private Long refundedPurchases;       // 退款购买数
    private Long totalExpense;            // 总消费金额
    private Long totalSavings;            // 总节省金额
    private Double avgExpensePerPurchase; // 平均每次消费
    private String favoriteContentType;   // 偏爱的内容类型
    private Map<String, Object> favoriteAuthor; // 偏爱的作者
    private Map<String, Object> mostAccessedContent; // 最常访问的内容
    private String purchaseFrequency;     // 购买频率
    private String membershipLevel;       // 会员等级
    private LocalDateTime joinDate;       // 加入日期
    private List<Map<String, Object>> monthlyStats; // 月度统计
}
```

### ContentSalesStats 内容销售统计对象
```java
@Data
@Builder
public class ContentSalesStats {
    private Long totalSales;              // 总销量
    private Long totalRevenue;            // 总收入
    private Double avgPrice;              // 平均价格
    private Long uniqueBuyers;            // 独立买家数
    private Double repeatPurchaseRate;    // 重复购买率
    private Double refundRate;            // 退款率
    private Double conversionRate;        // 转化率
    private List<Map<String, Object>> salesTrend; // 销售趋势
    private Map<String, Object> buyerAnalysis; // 买家分析
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
| PROCESS_EXPIRED_FAILED | 处理过期购买记录失败 | 系统处理异常 |
| REFUND_PROCESS_FAILED | 退款处理失败 | 检查退款条件 |
| STATS_CALCULATION_FAILED | 统计计算失败 | 检查统计参数 |
| PAYMENT_SUCCESS_HANDLE_FAILED | 处理订单支付成功失败 | 检查订单信息 |
| RECOMMENDATION_GENERATION_FAILED | 推荐生成失败 | 检查用户购买历史 |

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
    
    public PurchaseResult processPurchase(Long userId, Long contentId, Long orderId) {
        try {
            // 1. 验证购买权限
            Result<Boolean> permissionResult = purchaseFacadeService
                .validatePurchasePermission(userId, contentId);
            if (!permissionResult.isSuccess() || !permissionResult.getData()) {
                return PurchaseResult.failed("没有购买权限");
            }
            
            // 2. 检查是否已购买
            Result<ContentPurchaseResponse> existingResult = purchaseFacadeService
                .getUserContentPurchase(userId, contentId);
            if (existingResult.isSuccess() && existingResult.getData() != null) {
                ContentPurchaseResponse existing = existingResult.getData();
                if ("ACTIVE".equals(existing.getStatus())) {
                    return PurchaseResult.failed("已经购买过该内容");
                }
            }
            
            // 3. 处理支付成功回调
            Result<ContentPurchaseResponse> purchaseResult = purchaseFacadeService
                .handleOrderPaymentSuccess(orderId);
            if (!purchaseResult.isSuccess()) {
                return PurchaseResult.failed("创建购买记录失败");
            }
            
            ContentPurchaseResponse purchase = purchaseResult.getData();
            
            // 4. 发送购买成功通知
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
                    Result<Map<String, Object>> result = purchaseFacadeService.getUserPurchaseStats(userId);
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            CompletableFuture<List<ContentPurchaseResponse>> recentFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<List<ContentPurchaseResponse>> result = 
                        purchaseFacadeService.getUserRecentPurchases(userId, 5);
                    return result.isSuccess() ? result.getData() : Collections.emptyList();
                });
            
            CompletableFuture<List<ContentPurchaseResponse>> unreadFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<List<ContentPurchaseResponse>> result = 
                        purchaseFacadeService.getUserUnreadPurchases(userId);
                    return result.isSuccess() ? result.getData() : Collections.emptyList();
                });
            
            CompletableFuture<Map<String, Object>> discountFuture = 
                CompletableFuture.supplyAsync(() -> {
                    Result<Map<String, Object>> result = purchaseFacadeService.getDiscountStats(userId);
                    return result.isSuccess() ? result.getData() : Collections.emptyMap();
                });
            
            return UserPurchaseSummary.builder()
                .stats(statsFuture.get())
                .recentPurchases(recentFuture.get())
                .unreadPurchases(unreadFuture.get())
                .discountStats(discountFuture.get())
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
            Result<Boolean> accessResult = purchaseFacadeService.hasAccessPermission(userId, contentId);
            if (!accessResult.isSuccess()) {
                return AccessDecision.systemError("权限检查失败");
            }
            
            if (accessResult.getData()) {
                // 记录访问行为
                purchaseFacadeService.recordContentAccess(userId, contentId);
                return AccessDecision.allowed("有效购买记录");
            }
            
            // 计算访问权限详情
            Result<Map<String, Object>> detailResult = purchaseFacadeService
                .calculateContentAccess(userId, contentId);
            if (detailResult.isSuccess()) {
                Map<String, Object> detail = detailResult.getData();
                return AccessDecision.denied("需要购买", detail);
            }
            
            return AccessDecision.denied("无访问权限");
            
        } catch (Exception e) {
            log.error("检查内容访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return AccessDecision.systemError("系统异常");
        }
    }
    
    public Map<Long, Boolean> batchCheckAccess(Long userId, List<Long> contentIds) {
        try {
            Result<Map<Long, Boolean>> result = purchaseFacadeService
                .batchCheckAccessPermission(userId, contentIds);
            return result.isSuccess() ? result.getData() : Collections.emptyMap();
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
            Result<List<Long>> result = purchaseFacadeService
                .getUserContentRecommendations(userId, limit);
            return result.isSuccess() ? result.getData() : Collections.emptyList();
        } catch (Exception e) {
            log.error("获取用户推荐失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }
    
    public RecommendationContext buildRecommendationContext(Long userId) {
        try {
            // 获取用户购买统计
            Result<Map<String, Object>> statsResult = purchaseFacadeService.getUserPurchaseStats(userId);
            Map<String, Object> stats = statsResult.isSuccess() ? 
                statsResult.getData() : Collections.emptyMap();
            
            // 获取最近购买
            Result<List<ContentPurchaseResponse>> recentResult = 
                purchaseFacadeService.getUserRecentPurchases(userId, 10);
            List<ContentPurchaseResponse> recentPurchases = recentResult.isSuccess() ? 
                recentResult.getData() : Collections.emptyList();
            
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

4. **数据库优化**:
   - 购买记录按用户分片
   - 访问统计使用时序数据库
   - 统计数据定期预计算

## 🔗 相关文档

- [ContentPurchaseController REST API 文档](./content-purchase-controller-api.md)
- [ContentPaymentFacadeService 文档](./content-payment-facade-service-api.md)
- [购买流程设计文档](../design/purchase-flow-design.md)
- [用户推荐算法](../algorithm/user-recommendation.md)

---

**联系信息**:  
- Facade服务: ContentPurchaseFacadeService  
- 版本: 2.0.0 (内容付费版)  
- Dubbo版本: 5.0.0  
- 维护: GIG团队  
- 更新: 2024-01-31