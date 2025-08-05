# Order模块 Facade 接口文档

## 模块概述
订单门面服务接口提供完整的订单管理功能，基于order-simple.sql的去连表化设计，实现商品订单、内容订单、订阅订单等多种业务场景。采用Result<T>统一包装返回结果，确保接口调用的一致性和错误处理的标准化。

**接口名称**: `OrderFacadeService`  
**版本**: 2.0.0 (规范版)  
**包路径**: `com.gig.collide.api.order`  
**服务类型**: Dubbo RPC 服务

---

## 🚀 订单创建

### 1. createOrder
**方法签名**: `Result<OrderResponse> createOrder(OrderCreateRequest request)`  
**方法描述**: 创建订单，支持商品订单、内容订单、订阅订单等多种类型  
**业务逻辑**: 包含库存检查、价格计算、用户验证等完整业务逻辑

**请求参数**:
```java
OrderCreateRequest {
    Long userId;                    // 用户ID，必填
    String userNickname;           // 用户昵称
    Long goodsId;                  // 商品ID，必填
    String goodsType;              // 商品类型：coin/content/subscription/goods
    Integer quantity;              // 商品数量，默认1
    String paymentMode;            // 支付模式：cash/coin
    BigDecimal cashAmount;         // 现金金额
    Integer coinCost;              // 金币消耗
    Long contentId;                // 内容ID（内容类商品）
    BigDecimal effectiveDiscountAmount; // 有效折扣金额
}
```

**返回结果**:
```java
Result<OrderResponse> {
    Boolean success;               // 操作是否成功
    String code;                   // 响应码
    String message;                // 响应消息
    OrderResponse data;            // 订单详情
}
```

**异常情况**:
- `USER_NOT_FOUND`: 用户不存在
- `GOODS_NOT_FOUND`: 商品不存在
- `INSUFFICIENT_STOCK`: 库存不足
- `INVALID_PRICE`: 价格计算错误

---

## 🔍 订单查询

### 2. getOrderById
**方法签名**: `Result<OrderResponse> getOrderById(Long orderId, Long userId)`  
**方法描述**: 根据ID获取订单详情，包含权限验证，确保用户只能查看自己的订单

**权限验证**: 用户只能查看自己的订单，管理员可查看所有订单

**请求参数**:
- `orderId`: 订单ID，必填
- `userId`: 用户ID，用于权限验证，必填

**返回结果**: `Result<OrderResponse>`

### 3. getOrderByOrderNo
**方法签名**: `Result<OrderResponse> getOrderByOrderNo(String orderNo, Long userId)`  
**方法描述**: 根据订单号获取订单详情，支持用户查询自己的订单，管理员查询所有订单

### 4. queryOrders
**方法签名**: `Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request)`  
**方法描述**: 分页查询订单，支持多维度条件查询和筛选

**请求参数**:
```java
OrderQueryRequest {
    Long userId;                   // 用户ID，可选
    String status;                 // 订单状态，可选
    String goodsType;              // 商品类型，可选
    LocalDateTime startTime;       // 开始时间，可选
    LocalDateTime endTime;         // 结束时间，可选
    String keyword;                // 搜索关键词，可选
    Integer currentPage;           // 当前页码，默认1
    Integer pageSize;              // 页面大小，默认20
}
```

### 5. getUserOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getUserOrders(Long userId, String status, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询用户订单，支持按状态筛选，权限验证确保用户只能查看自己的订单

### 6. getOrdersByGoodsType
**方法签名**: `Result<PageResponse<OrderResponse>> getOrdersByGoodsType(String goodsType, String status, Integer currentPage, Integer pageSize)`  
**方法描述**: 根据商品类型查询订单，支持查看特定类型商品的订单统计和明细

### 7. getSellerOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getSellerOrders(Long sellerId, String status, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询商家订单，商家查看自己的订单明细，包含销售统计

### 8. searchOrders
**方法签名**: `Result<PageResponse<OrderResponse>> searchOrders(String keyword, Integer currentPage, Integer pageSize)`  
**方法描述**: 搜索订单，支持订单号、商品名称、用户昵称等关键词搜索

### 9. getOrdersByTimeRange
**方法签名**: `Result<PageResponse<OrderResponse>> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, String status, Integer currentPage, Integer pageSize)`  
**方法描述**: 根据时间范围查询订单，支持查看特定时间段的订单数据

---

## 💳 支付处理

### 10. processPayment
**方法签名**: `Result<Map<String, Object>> processPayment(Long orderId, String payMethod, Long userId)`  
**方法描述**: 处理订单支付，支持现金支付、金币支付等多种支付方式  
**业务逻辑**: 包含支付前验证、支付渠道选择、支付结果处理

**请求参数**:
- `orderId`: 订单ID，必填
- `payMethod`: 支付方式（cash/coin），必填
- `userId`: 用户ID，用于权限验证，必填

**返回结果**:
```java
Result<Map<String, Object>> {
    "status": "success",           // 支付状态
    "orderId": 1001,              // 订单ID
    "orderNo": "ORD...",          // 订单号
    "payMethod": "coin",          // 支付方式
    "coinCost": 100,              // 金币消耗（金币支付）
    "payUrl": "https://...",      // 支付链接（现金支付）
    "message": "支付成功",         // 处理消息
    "payTime": 1638360000000      // 支付时间戳
}
```

**业务逻辑**:
1. 验证用户权限和订单状态
2. 根据支付方式处理支付逻辑
3. 金币支付：检查余额 → 扣减金币 → 更新订单状态
4. 现金支付：调用第三方支付接口 → 返回支付链接
5. 处理后续业务：内容解锁、会员开通、金币充值等

### 11. confirmPayment
**方法签名**: `Result<Void> confirmPayment(Long orderId, String payMethod, Long userId)`  
**方法描述**: 确认支付成功，用于外部支付完成后的确认处理

### 12. handlePaymentCallback
**方法签名**: `Result<Void> handlePaymentCallback(String orderNo, String payStatus, String payMethod, Map<String, Object> extraInfo)`  
**方法描述**: 处理支付回调，处理第三方支付平台的异步回调通知

**请求参数**:
- `orderNo`: 订单号，必填
- `payStatus`: 支付状态，必填
- `payMethod`: 支付方式，必填
- `extraInfo`: 回调额外信息，可选

### 13. requestRefund
**方法签名**: `Result<Map<String, Object>> requestRefund(Long orderId, String reason, Long userId)`  
**方法描述**: 申请退款，支持全额退款和部分退款，包含退款前验证

**返回结果**:
```java
Result<Map<String, Object>> {
    "success": true,              // 退款申请是否成功
    "refundId": "RF...",          // 退款单号
    "refundAmount": 99.99,        // 退款金额
    "estimatedTime": "3-5个工作日", // 预计到账时间
    "message": "退款申请成功"       // 处理消息
}
```

---

## 📦 订单管理

### 14. cancelOrder
**方法签名**: `Result<Void> cancelOrder(Long orderId, String reason, Long userId)`  
**方法描述**: 取消订单，支持用户主动取消和系统超时取消  
**业务逻辑**: 包含取消条件验证和库存回滚

### 15. shipOrder
**方法签名**: `Result<Void> shipOrder(Long orderId, Map<String, Object> shippingInfo, Long operatorId)`  
**方法描述**: 发货，商家发货操作，更新订单状态和物流信息

**请求参数**:
- `orderId`: 订单ID，必填
- `shippingInfo`: 物流信息，必填
- `operatorId`: 操作者ID（商家ID），必填

**物流信息格式**:
```java
Map<String, Object> shippingInfo {
    "logistics": "顺丰快递",       // 物流公司
    "trackingNumber": "SF1234",   // 快递单号
    "estimatedDelivery": "2024-02-01", // 预计送达时间
    "shippingAddress": "收货地址"   // 收货地址
}
```

### 16. confirmReceipt
**方法签名**: `Result<Void> confirmReceipt(Long orderId, Long userId)`  
**方法描述**: 确认收货，用户确认收到商品，订单状态变更为已完成

### 17. completeOrder
**方法签名**: `Result<Void> completeOrder(Long orderId, Long operatorId)`  
**方法描述**: 完成订单，系统或管理员完成订单的最终处理

---

## 📊 统计分析

### 18. getUserOrderStatistics
**方法签名**: `Result<Map<String, Object>> getUserOrderStatistics(Long userId)`  
**方法描述**: 获取用户订单统计，统计用户的订单总数、金额、状态分布等信息

**返回结果**:
```java
Result<Map<String, Object>> {
    "totalOrders": 150,           // 总订单数
    "paidOrders": 145,            // 已支付订单数
    "cancelledOrders": 5,         // 已取消订单数
    "totalAmount": 15999.50,      // 总消费金额
    "monthlyAmount": 2999.90,     // 本月消费金额
    "favoriteType": "content",    // 偏好商品类型
    "lastOrderTime": "2024-01-30T10:30:00" // 最后下单时间
}
```

### 19. getGoodsSalesStatistics
**方法签名**: `Result<Map<String, Object>> getGoodsSalesStatistics(Long goodsId)`  
**方法描述**: 获取商品销售统计，统计商品的销量、营收、订单状态分布

### 20. getOrderStatisticsByType
**方法签名**: `Result<List<Map<String, Object>>> getOrderStatisticsByType()`  
**方法描述**: 按商品类型统计订单，统计各类型商品的订单分布和营收情况

### 21. getHotGoods
**方法签名**: `Result<List<Map<String, Object>>> getHotGoods(Integer limit)`  
**方法描述**: 获取热门商品，根据销量和订单数量统计最受欢迎的商品

### 22. getDailyRevenue
**方法签名**: `Result<List<Map<String, Object>>> getDailyRevenue(String startDate, String endDate)`  
**方法描述**: 获取日营收统计，统计指定时间范围内的每日营收数据

### 23. getUserRecentOrders
**方法签名**: `Result<List<OrderResponse>> getUserRecentOrders(Long userId, Integer limit)`  
**方法描述**: 获取用户最近购买记录，查看用户最近的购买历史，用于推荐和分析

---

## 🎯 专用查询

### 24. getUserCoinOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getUserCoinOrders(Long userId, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询用户的金币消费订单，用于查看用户使用金币购买内容或服务的订单记录

### 25. getUserRechargeOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getUserRechargeOrders(Long userId, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询用户的充值订单，用于查看用户购买金币的充值订单记录

### 26. getContentOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getContentOrders(Long contentId, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询内容购买订单，查看特定内容的购买订单，用于内容统计分析

**请求参数**:
- `contentId`: 内容ID，可选，为空则查询所有内容订单
- `currentPage`: 当前页码，必填
- `pageSize`: 页面大小，必填

### 27. getSubscriptionOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getSubscriptionOrders(String subscriptionType, Integer currentPage, Integer pageSize)`  
**方法描述**: 查询订阅订单，查看用户的会员订阅、VIP等订阅类服务订单

**请求参数**:
- `subscriptionType`: 订阅类型，可选，如VIP/PREMIUM
- `currentPage`: 当前页码，必填
- `pageSize`: 页面大小，必填

---

## ✅ 业务验证

### 28. validatePayment
**方法签名**: `Result<Map<String, Object>> validatePayment(Long orderId, Long userId)`  
**方法描述**: 验证订单是否可以支付，检查订单状态、库存、用户余额等支付前置条件

**返回结果**:
```java
Result<Map<String, Object>> {
    "valid": true,                // 是否可以支付
    "message": "订单可以支付",      // 验证消息
    "paymentMethods": ["cash", "coin"], // 可用支付方式
    "requiredCoin": 100,          // 需要金币数量
    "userCoinBalance": 500,       // 用户金币余额
    "order": { ... }              // 订单详情
}
```

### 29. validateCancel
**方法签名**: `Result<Map<String, Object>> validateCancel(Long orderId, Long userId)`  
**方法描述**: 验证订单是否可以取消，检查订单状态和取消规则，确定是否允许取消

### 30. validateRefund
**方法签名**: `Result<Map<String, Object>> validateRefund(Long orderId, Long userId)`  
**方法描述**: 验证订单是否可以退款，检查退款政策、订单状态和退款条件

---

## 🚀 快捷查询

### 31. getPendingOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getPendingOrders(Long userId, Integer currentPage, Integer pageSize)`  
**方法描述**: 获取待支付订单，查询用户或系统中待支付的订单

**请求参数**:
- `userId`: 用户ID，可选，为空则查询系统所有待支付订单
- `currentPage`: 当前页码，必填
- `pageSize`: 页面大小，必填

### 32. getCompletedOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getCompletedOrders(Long userId, Integer currentPage, Integer pageSize)`  
**方法描述**: 获取已完成订单，查询用户或系统中已完成的订单

### 33. getTodayOrders
**方法签名**: `Result<PageResponse<OrderResponse>> getTodayOrders(Integer currentPage, Integer pageSize)`  
**方法描述**: 获取今日订单，查询今天创建的所有订单

---

## 🔄 批量操作

### 34. batchCancelOrders
**方法签名**: `Result<Void> batchCancelOrders(List<Long> orderIds, String reason, Long operatorId)`  
**方法描述**: 批量取消订单，支持用户批量取消自己的订单，或管理员批量取消订单

**请求参数**:
- `orderIds`: 订单ID列表，必填
- `reason`: 取消原因，必填
- `operatorId`: 操作者ID，必填

**业务逻辑**:
1. 验证操作者权限
2. 逐个验证订单是否可取消
3. 批量更新订单状态
4. 处理库存回滚和用户通知

---

## ⚙️ 系统管理

### 35. getTimeoutOrders
**方法签名**: `Result<List<OrderResponse>> getTimeoutOrders(Integer timeoutMinutes)`  
**方法描述**: 获取超时订单，查询超过指定时间仍未支付的订单

### 36. autoCancelTimeoutOrders
**方法签名**: `Result<Integer> autoCancelTimeoutOrders(Integer timeoutMinutes)`  
**方法描述**: 自动取消超时订单，系统定时任务，自动取消超时未支付的订单

**返回结果**: `Result<Integer>` - 返回取消的订单数量

### 37. autoCompleteShippedOrders
**方法签名**: `Result<Integer> autoCompleteShippedOrders(Integer days)`  
**方法描述**: 自动完成已发货订单，系统定时任务，自动完成长时间未确认收货的订单

**请求参数**:
- `days`: 发货后天数，必填

### 38. countOrdersByGoodsId
**方法签名**: `Result<Long> countOrdersByGoodsId(Long goodsId, String status)`  
**方法描述**: 根据商品ID统计订单数

**请求参数**:
- `goodsId`: 商品ID，必填
- `status`: 订单状态，可选

### 39. countOrdersByUserId
**方法签名**: `Result<Long> countOrdersByUserId(Long userId, String status)`  
**方法描述**: 根据用户ID统计订单数

### 40. updatePaymentInfo
**方法签名**: `Result<Void> updatePaymentInfo(Long orderId, String payStatus, String payMethod, LocalDateTime payTime)`  
**方法描述**: 更新订单支付信息，内部系统调用，更新订单的支付状态和时间

**请求参数**:
- `orderId`: 订单ID，必填
- `payStatus`: 支付状态，必填
- `payMethod`: 支付方式，必填
- `payTime`: 支付时间，必填

**业务逻辑**:
1. 更新订单支付信息
2. 如果支付成功，自动处理金币充值
3. 触发相关业务逻辑（内容解锁、会员开通等）

### 41. healthCheck
**方法签名**: `Result<String> healthCheck()`  
**方法描述**: 订单系统健康检查，检查订单系统的运行状态

**返回结果**:
```java
Result<String> {
    "订单系统运行正常, 数据库连接正常, 缓存服务正常, 用户服务连接正常"
}
```

**检查项目**:
1. 基本服务状态
2. 数据库连接状态
3. 缓存服务状态
4. 外部服务依赖状态（用户服务、钱包服务等）

---

## 📋 数据结构

### OrderResponse
```java
public class OrderResponse implements Serializable {
    private Long id;                    // 订单ID
    private String orderNo;             // 订单号
    private Long userId;                // 用户ID
    private String userNickname;        // 用户昵称
    private Long goodsId;               // 商品ID
    private String goodsName;           // 商品名称
    private String goodsType;           // 商品类型
    private Integer quantity;           // 商品数量
    private String paymentMode;         // 支付模式
    private BigDecimal cashAmount;      // 现金金额
    private Integer coinCost;           // 金币消耗
    private BigDecimal totalAmount;     // 订单总额
    private BigDecimal finalAmount;     // 实际支付金额
    private String status;              // 订单状态
    private String payStatus;           // 支付状态
    private String payMethod;           // 支付方式
    private LocalDateTime payTime;      // 支付时间
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private Long contentId;             // 内容ID（内容类商品）
    private String contentTitle;        // 内容标题（内容类商品）
    private Integer subscriptionDuration; // 订阅时长（订阅类商品）
    private String subscriptionType;    // 订阅类型（订阅类商品）
    private Integer coinAmount;         // 金币数量（金币充值商品）
}
```

### PageResponse
```java
public class PageResponse<T> implements Serializable {
    private List<T> datas;              // 数据列表
    private Long total;                 // 总记录数
    private Integer currentPage;        // 当前页码
    private Integer pageSize;           // 页面大小
    private Integer totalPage;          // 总页数
}
```

### Result
```java
public class Result<T> implements Serializable {
    private Boolean success;            // 操作是否成功
    private String code;                // 响应码
    private String message;             // 响应消息
    private T data;                     // 业务数据
}
```

---

## 🔒 权限控制

### 用户权限验证
所有涉及用户数据的方法都包含userId参数进行权限验证：
- 用户只能查看、操作自己的订单
- 商家只能查看、操作自己店铺的订单
- 管理员可以查看、操作所有订单

### 操作者权限验证
涉及管理操作的方法需要operatorId参数：
- 发货操作：需要商家权限
- 批量操作：需要管理员权限
- 系统管理：需要系统权限

---

## 🚨 异常处理

### 标准错误码
- `SUCCESS`: 操作成功
- `USER_NOT_FOUND`: 用户不存在
- `ORDER_NOT_FOUND`: 订单不存在
- `ACCESS_DENIED`: 无权限访问
- `INVALID_PARAM`: 参数错误
- `PAYMENT_FAILED`: 支付失败
- `INSUFFICIENT_BALANCE`: 余额不足
- `ORDER_STATUS_ERROR`: 订单状态错误
- `REFUND_FAILED`: 退款失败
- `VALIDATE_FAILED`: 验证失败

### 异常处理原则
1. 所有方法都使用Result<T>包装返回结果
2. 异常情况返回具体的错误码和错误信息
3. 不抛出未处理的异常到调用方
4. 关键操作失败记录详细日志

---

## 🔄 集成服务

### 用户服务集成
- `UserFacadeService.getUserById()`: 验证用户存在性
- 权限验证和用户信息获取

### 钱包服务集成
- `WalletFacadeService.checkCoinBalance()`: 检查金币余额
- `WalletFacadeService.consumeCoin()`: 扣减金币
- `WalletFacadeService.grantCoinReward()`: 发放金币

### 缓存策略
- 订单详情缓存：30分钟
- 用户订单列表缓存：15分钟
- 统计数据缓存：60分钟
- 热门商品缓存：120分钟

---

**接口总计**: 45个方法  
**完整性**: 覆盖订单全生命周期管理  
**一致性**: 统一的Result<T>包装和错误处理  
**安全性**: 严格的权限验证和业务校验  
**性能**: JetCache缓存优化和分页查询支持