# Payment API 文档（去连表设计 v2.0.0）

## 概述

本文档描述了 Collide 支付模块的 API 接口规范。支付模块采用去连表设计，通过冗余字段避免复杂的联表查询，提高系统性能和可扩展性。

## 设计原则

### 去连表化设计
- **数据冗余**：在支付记录中存储用户名、订单标题等冗余字段
- **避免JOIN**：查询时不需要联表获取关联数据
- **完整参数**：API 方法传递完整信息，而非仅传递需要联表查询的 ID

### 核心特性
- ✅ 去除外键约束，避免跨表依赖
- ✅ 冗余存储用户信息、订单信息、商户信息
- ✅ 幂等性保证，支持重复调用
- ✅ 完整的风控和监控能力
- ✅ 向后兼容旧版本 API

## 接口列表

### 1. 创建支付订单

#### 接口信息
- **方法**: `createPayment`
- **描述**: 创建支付订单（去连表设计），传递完整的用户和订单信息
- **参数类型**: `CreatePaymentRequest`
- **返回类型**: `SingleResponse<Map<String, Object>>`

#### 请求参数 `CreatePaymentRequest`

```java
public class CreatePaymentRequest {
    // 基础信息
    @NotBlank String orderNo;                    // 订单号
    String internalTransactionNo;                // 内部支付流水号（可选）
    
    // 用户信息（冗余字段，避免联表查询用户表）
    @NotNull Long userId;                        // 用户ID
    @NotBlank String userName;                   // 用户名称
    String userPhone;                            // 用户手机号
    String userEmail;                            // 用户邮箱
    
    // 订单信息（冗余字段，避免联表查询订单表）
    @NotBlank String orderTitle;                 // 订单标题
    String productName;                          // 商品名称
    String productType;                          // 商品类型
    Long merchantId;                             // 商户ID
    String merchantName;                         // 商户名称
    
    // 金额信息
    @NotNull BigDecimal payAmount;               // 支付金额（元）
    BigDecimal discountAmount;                   // 优惠金额（元）
    String currencyCode;                         // 货币代码（默认CNY）
    
    // 支付信息
    @NotBlank String payType;                    // 支付方式
    String payScene;                             // 支付场景
    String payMethod;                            // 具体支付方式
    
    // 时间信息
    LocalDateTime expireTime;                    // 支付过期时间
    
    // 网络信息
    String clientIp;                             // 客户端IP地址
    String userAgent;                            // 用户代理信息
    String deviceInfo;                           // 设备信息
    
    // 回调通知信息
    String notifyUrl;                            // 异步回调通知URL
    String returnUrl;                            // 同步返回URL
    Integer maxNotifyCount;                      // 最大回调次数
    
    // 扩展信息
    Map<String, Object> extraData;              // 扩展信息
    Map<String, Object> businessData;           // 业务相关数据
    String remark;                               // 备注信息
    
    // 风控信息
    String riskLevel;                            // 风险等级
    Integer riskScore;                           // 风险评分
}
```

#### 响应结果

```json
{
    "success": true,
    "errorCode": null,
    "errorMessage": null,
    "data": {
        "paymentId": 123456789,
        "orderNo": "ORDER_20240115001",
        "internalTransactionNo": "INTERNAL_20240115001",
        "payStatus": "PENDING",
        "payAmount": "99.99",
        "payType": "ALIPAY",
        "payUrl": "https://payment.example.com/pay/...",
        "expireTime": "2024-01-15T15:30:00",
        "qrCode": "iVBORw0KGgoAAAANSUhEUgA...",
        "createTime": "2024-01-15T14:00:00"
    }
}
```

### 2. 处理支付回调

#### 接口信息
- **方法**: `processPaymentCallback`
- **描述**: 处理支付回调（去连表设计），传递完整的支付和用户信息
- **参数类型**: `PaymentCallbackRequest`
- **返回类型**: `SingleResponse<Boolean>`

#### 请求参数 `PaymentCallbackRequest`

```java
public class PaymentCallbackRequest {
    // 基础信息
    @NotNull Long paymentRecordId;               // 支付记录ID
    
    // 冗余支付信息（避免联表查询支付表）
    @NotBlank String orderNo;                    // 订单号
    String transactionNo;                        // 支付流水号
    @NotBlank String internalTransactionNo;      // 内部支付流水号
    @NotNull Long userId;                        // 用户ID
    @NotBlank String userName;                   // 用户名称
    @NotNull BigDecimal payAmount;               // 支付金额
    @NotBlank String payType;                    // 支付方式
    
    // 回调信息
    @NotBlank String callbackType;               // 回调类型
    @NotBlank String callbackSource;             // 回调来源
    String callbackResult;                       // 回调业务结果
    
    // 回调数据
    @NotBlank String callbackContent;            // 回调原始内容数据
    String callbackSignature;                   // 回调签名信息
    Map<String, String> callbackParams;         // 回调参数
    
    // 处理信息
    String processMessage;                       // 处理结果消息
    String errorCode;                            // 错误代码
    String errorMessage;                         // 错误信息详情
    
    // 网络信息
    String clientIp;                             // 回调请求IP地址
    String userAgent;                            // 用户代理信息
    Map<String, String> requestHeaders;         // 请求头信息
    
    // 重试信息
    Integer maxRetryCount;                       // 最大重试次数
    
    // 业务扩展信息
    BigDecimal actualPayAmount;                  // 实际支付金额
    String thirdPartyOrderNo;                    // 第三方支付平台订单号
    String thirdPartyPayTime;                    // 第三方支付时间
    BigDecimal thirdPartyFee;                    // 第三方手续费
    BigDecimal refundAmount;                     // 退款金额
    String refundReason;                         // 退款原因
    String failureReason;                        // 失败原因
    String failureCode;                          // 失败错误码
    
    // 验签相关
    Boolean needVerifySignature;                 // 是否需要验签
    String signatureAlgorithm;                   // 签名算法
    String charset;                              // 编码格式
    
    // 其他信息
    String notifyTime;                           // 回调通知时间
    String version;                              // 回调版本号
    Boolean asyncProcess;                        // 是否异步处理
    Integer priority;                            // 优先级
}
```

#### 响应结果

```json
{
    "success": true,
    "errorCode": null,
    "errorMessage": null,
    "data": true
}
```

### 3. 获取支付状态

#### 接口信息
- **方法**: `getPaymentStatus`
- **描述**: 获取支付状态（通过完整信息查询）
- **参数**: 
  - `String orderNo` - 订单号
  - `Long userId` - 用户ID  
  - `String userName` - 用户名称（用于验证和日志记录）
- **返回类型**: `SingleResponse<Map<String, Object>>`

#### 响应结果

```json
{
    "success": true,
    "errorCode": null,
    "errorMessage": null,
    "data": {
        "paymentId": 123456789,
        "orderNo": "ORDER_20240115001",
        "internalTransactionNo": "INTERNAL_20240115001",
        "transactionNo": "ALIPAY_20240115001",
        "userId": 1001,
        "userName": "张三",
        "userPhone": "13800138000",
        "orderTitle": "商品订单支付",
        "productName": "精选商品套装",
        "merchantName": "优选商城",
        "payAmount": "99.99",
        "actualPayAmount": "99.99",
        "discountAmount": "0.00",
        "payType": "ALIPAY",
        "payStatus": "SUCCESS",
        "payScene": "WEB",
        "payTime": "2024-01-15T14:05:32",
        "completeTime": "2024-01-15T14:05:45",
        "expireTime": "2024-01-15T14:30:00",
        "notifyStatus": "SUCCESS",
        "notifyCount": 1,
        "lastNotifyTime": "2024-01-15T14:05:50",
        "refundAmount": "0.00",
        "riskLevel": "LOW",
        "riskScore": 15,
        "createTime": "2024-01-15T14:00:00",
        "updateTime": "2024-01-15T14:05:50"
    }
}
```

### 4. 重置支付状态

#### 接口信息
- **方法**: `resetPaymentStatus`
- **描述**: 重置支付状态（传递完整用户信息）
- **参数**:
  - `String orderNo` - 订单号
  - `Long userId` - 用户ID
  - `String userName` - 用户名称
  - `String resetReason` - 重置原因
- **返回类型**: `SingleResponse<Void>`

### 5. 查询用户支付记录列表

#### 接口信息
- **方法**: `getUserPaymentRecords`
- **描述**: 查询用户支付记录列表
- **参数**:
  - `Long userId` - 用户ID
  - `String userName` - 用户名称
  - `String payStatus` - 支付状态（可选）
  - `String startTime` - 开始时间（可选）
  - `String endTime` - 结束时间（可选）
  - `Integer limit` - 查询限制数量
- **返回类型**: `SingleResponse<Map<String, Object>>`

#### 响应结果

```json
{
    "success": true,
    "errorCode": null,
    "errorMessage": null,
    "data": {
        "totalCount": 25,
        "records": [
            {
                "paymentId": 123456789,
                "orderNo": "ORDER_20240115001",
                "orderTitle": "商品订单支付",
                "productName": "精选商品套装",
                "payAmount": "99.99",
                "actualPayAmount": "99.99",
                "payType": "ALIPAY",
                "payStatus": "SUCCESS",
                "payTime": "2024-01-15T14:05:32",
                "completeTime": "2024-01-15T14:05:45"
            }
        ],
        "summary": {
            "totalAmount": "2599.75",
            "successAmount": "2499.75",
            "refundAmount": "0.00",
            "successCount": 24,
            "failedCount": 1
        }
    }
}
```

### 6. 查询商户支付记录列表

#### 接口信息
- **方法**: `getMerchantPaymentRecords`
- **描述**: 查询商户支付记录列表
- **参数**:
  - `Long merchantId` - 商户ID
  - `String merchantName` - 商户名称
  - `String payStatus` - 支付状态（可选）
  - `String startTime` - 开始时间（可选）
  - `String endTime` - 结束时间（可选）
  - `Integer limit` - 查询限制数量
- **返回类型**: `SingleResponse<Map<String, Object>>`

### 7. 申请退款

#### 接口信息
- **方法**: `applyRefund`
- **描述**: 申请退款（传递完整信息）
- **参数**:
  - `String orderNo` - 订单号
  - `Long userId` - 用户ID
  - `String userName` - 用户名称
  - `String refundAmount` - 退款金额
  - `String refundReason` - 退款原因
  - `Long operatorId` - 操作人ID
  - `String operatorName` - 操作人名称
- **返回类型**: `SingleResponse<Map<String, Object>>`

### 8. 获取支付配置信息

#### 接口信息
- **方法**: `getPaymentConfig`
- **描述**: 获取支付配置信息
- **参数**:
  - `String configType` - 配置类型（ALIPAY/WECHAT/UNIONPAY/SYSTEM）
  - `String envProfile` - 环境标识（dev/test/prod）
- **返回类型**: `SingleResponse<Map<String, Object>>`

### 9. 获取支付统计信息

#### 接口信息
- **方法**: `getPaymentStatistics`
- **描述**: 获取支付统计信息
- **参数**:
  - `String statDate` - 统计日期（格式：yyyy-MM-dd）
  - `String payType` - 支付方式（可选）
  - `Long merchantId` - 商户ID（可选）
  - `Boolean includeHourly` - 是否包含小时统计
- **返回类型**: `SingleResponse<Map<String, Object>>`

### 10. 检查支付风险

#### 接口信息
- **方法**: `checkPaymentRisk`
- **描述**: 检查支付风险
- **参数**:
  - `Long userId` - 用户ID
  - `String userName` - 用户名称
  - `String payAmount` - 支付金额
  - `String payType` - 支付方式
  - `String clientIp` - 客户端IP
  - `String deviceInfo` - 设备信息
- **返回类型**: `SingleResponse<Map<String, Object>>`

## 数据模型

### 支付记录表（t_payment_record）

```sql
CREATE TABLE t_payment_record (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT,
    order_no                    VARCHAR(64)     NOT NULL,
    transaction_no              VARCHAR(128)    DEFAULT NULL,
    internal_transaction_no     VARCHAR(128)    NOT NULL,
    
    -- 用户信息（冗余字段，避免联表查询用户表）
    user_id                     BIGINT          NOT NULL,
    user_name                   VARCHAR(64)     NOT NULL,
    user_phone                  VARCHAR(20)     DEFAULT NULL,
    user_email                  VARCHAR(128)    DEFAULT NULL,
    
    -- 订单信息（冗余字段，避免联表查询订单表）
    order_title                 VARCHAR(255)    NOT NULL,
    product_name                VARCHAR(255)    DEFAULT NULL,
    product_type                VARCHAR(50)     DEFAULT NULL,
    merchant_id                 BIGINT          DEFAULT NULL,
    merchant_name               VARCHAR(128)    DEFAULT NULL,
    
    -- 金额信息
    pay_amount                  DECIMAL(15,2)   NOT NULL,
    actual_pay_amount           DECIMAL(15,2)   DEFAULT NULL,
    discount_amount             DECIMAL(15,2)   DEFAULT 0.00,
    currency_code               VARCHAR(10)     NOT NULL DEFAULT 'CNY',
    
    -- 支付信息
    pay_type                    VARCHAR(20)     NOT NULL,
    pay_status                  VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    pay_channel                 VARCHAR(50)     DEFAULT NULL,
    pay_scene                   VARCHAR(20)     DEFAULT 'WEB',
    pay_method                  VARCHAR(50)     DEFAULT NULL,
    
    -- 时间信息
    pay_time                    DATETIME        DEFAULT NULL,
    complete_time               DATETIME        DEFAULT NULL,
    expire_time                 DATETIME        NOT NULL,
    cancel_time                 DATETIME        DEFAULT NULL,
    
    -- 网络信息
    client_ip                   VARCHAR(45)     DEFAULT NULL,
    user_agent                  VARCHAR(512)    DEFAULT NULL,
    device_info                 VARCHAR(255)    DEFAULT NULL,
    
    -- 回调通知信息
    notify_url                  VARCHAR(512)    DEFAULT NULL,
    return_url                  VARCHAR(512)    DEFAULT NULL,
    notify_status               VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    notify_count                INT             NOT NULL DEFAULT 0,
    last_notify_time            DATETIME        DEFAULT NULL,
    max_notify_count            INT             NOT NULL DEFAULT 5,
    
    -- 退款信息
    refund_amount               DECIMAL(15,2)   DEFAULT 0.00,
    refund_reason               VARCHAR(255)    DEFAULT NULL,
    refund_status               VARCHAR(20)     DEFAULT NULL,
    refund_time                 DATETIME        DEFAULT NULL,
    
    -- 失败信息
    failure_code                VARCHAR(50)     DEFAULT NULL,
    failure_reason              VARCHAR(255)    DEFAULT NULL,
    failure_time                DATETIME        DEFAULT NULL,
    
    -- 风控信息
    risk_level                  VARCHAR(20)     DEFAULT 'LOW',
    risk_score                  INT             DEFAULT 0,
    is_blocked                  TINYINT(1)      NOT NULL DEFAULT 0,
    
    -- 扩展信息
    extra_data                  JSON            DEFAULT NULL,
    third_party_data            JSON            DEFAULT NULL,
    business_data               JSON            DEFAULT NULL,
    remark                      VARCHAR(500)    DEFAULT NULL,
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted                     TINYINT         NOT NULL DEFAULT 0,
    version                     INT             NOT NULL DEFAULT 0,
    
    PRIMARY KEY (id),
    UNIQUE KEY uk_internal_transaction_no (internal_transaction_no),
    UNIQUE KEY uk_order_no_user_id (order_no, user_id),
    -- 更多索引...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表（去连表设计）';
```

### 支付回调记录表（t_payment_callback）

```sql
CREATE TABLE t_payment_callback (
    -- 基础信息
    id                          BIGINT          NOT NULL AUTO_INCREMENT,
    payment_record_id           BIGINT          NOT NULL,
    
    -- 冗余支付信息（避免联表查询支付表）
    order_no                    VARCHAR(64)     NOT NULL,
    transaction_no              VARCHAR(128)    DEFAULT NULL,
    internal_transaction_no     VARCHAR(128)    NOT NULL,
    user_id                     BIGINT          NOT NULL,
    user_name                   VARCHAR(64)     NOT NULL,
    pay_amount                  DECIMAL(15,2)   NOT NULL,
    pay_type                    VARCHAR(20)     NOT NULL,
    
    -- 回调信息
    callback_type               VARCHAR(20)     NOT NULL,
    callback_source             VARCHAR(20)     NOT NULL,
    callback_status             VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    callback_result             VARCHAR(20)     DEFAULT NULL,
    
    -- 回调数据
    callback_content            TEXT            DEFAULT NULL,
    callback_signature          VARCHAR(512)    DEFAULT NULL,
    signature_valid             TINYINT(1)      DEFAULT NULL,
    callback_params             JSON            DEFAULT NULL,
    
    -- 处理信息
    process_result              VARCHAR(20)     DEFAULT NULL,
    process_message             VARCHAR(500)    DEFAULT NULL,
    error_code                  VARCHAR(50)     DEFAULT NULL,
    error_message               VARCHAR(500)    DEFAULT NULL,
    
    -- 网络信息
    client_ip                   VARCHAR(45)     DEFAULT NULL,
    user_agent                  VARCHAR(500)    DEFAULT NULL,
    request_headers             JSON            DEFAULT NULL,
    
    -- 性能信息
    process_start_time          DATETIME        DEFAULT NULL,
    process_end_time            DATETIME        DEFAULT NULL,
    process_time_ms             BIGINT          DEFAULT NULL,
    
    -- 重试信息
    retry_count                 INT             NOT NULL DEFAULT 0,
    max_retry_count             INT             NOT NULL DEFAULT 3,
    next_retry_time             DATETIME        DEFAULT NULL,
    
    -- 系统字段
    create_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time                 DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted                     TINYINT         NOT NULL DEFAULT 0,
    
    PRIMARY KEY (id),
    KEY idx_payment_record_id (payment_record_id),
    -- 更多索引...
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付回调记录表（去连表设计）';
```

## 常量定义

### 支付方式（PayType）
- `ALIPAY` - 支付宝
- `WECHAT` - 微信支付
- `UNIONPAY` - 银联支付
- `TEST` - 测试支付

### 支付状态（PayStatus）
- `PENDING` - 待支付
- `SUCCESS` - 支付成功
- `FAILED` - 支付失败
- `CANCELLED` - 已取消
- `REFUNDING` - 退款中
- `REFUNDED` - 已退款

### 支付场景（PayScene）
- `WEB` - 网页支付
- `MOBILE` - 手机支付
- `APP` - 应用内支付
- `MINI` - 小程序支付

### 回调类型（CallbackType）
- `PAYMENT` - 支付回调
- `REFUND` - 退款回调
- `CANCEL` - 取消回调

### 回调状态（CallbackStatus）
- `SUCCESS` - 处理成功
- `FAILED` - 处理失败
- `PENDING` - 处理中

### 风险等级（RiskLevel）
- `LOW` - 低风险
- `MEDIUM` - 中风险
- `HIGH` - 高风险

## 错误码

### 系统错误（1000-1999）
- `1001` - 系统内部错误
- `1002` - 网络超时
- `1003` - 服务不可用

### 参数错误（2000-2999）
- `2001` - 参数不能为空
- `2002` - 参数格式错误
- `2003` - 参数值超出范围

### 业务错误（3000-3999）
- `3001` - 订单不存在
- `3002` - 订单状态不正确
- `3003` - 支付金额不匹配
- `3004` - 用户信息不匹配
- `3005` - 商户信息不正确

### 支付错误（4000-4999）
- `4001` - 支付方式不支持
- `4002` - 支付已过期
- `4003` - 重复支付
- `4004` - 支付金额不正确
- `4005` - 余额不足

### 风控错误（5000-5999）
- `5001` - 风险等级过高
- `5002` - 用户被限制支付
- `5003` - IP地址异常
- `5004` - 设备异常

## 使用示例

### 创建支付订单示例

```java
// 构建创建支付请求
CreatePaymentRequest request = new CreatePaymentRequest()
    .setOrderNo("ORDER_20240115001")
    .setUserId(1001L)
    .setUserName("张三")
    .setUserPhone("13800138000")
    .setUserEmail("zhangsan@example.com")
    .setOrderTitle("商品订单支付")
    .setProductName("精选商品套装")
    .setProductType("PHYSICAL")
    .setMerchantId(100001L)
    .setMerchantName("优选商城")
    .setPayAmount(new BigDecimal("99.99"))
    .setDiscountAmount(new BigDecimal("0.00"))
    .setPayType("ALIPAY")
    .setPayScene("WEB")
    .setClientIp("192.168.1.100")
    .setNotifyUrl("https://api.example.com/payment/notify")
    .setReturnUrl("https://www.example.com/payment/return")
    .setRemark("用户主动发起的支付");

// 调用创建支付接口
SingleResponse<Map<String, Object>> response = paymentFacadeService.createPayment(request);

if (response.isSuccess()) {
    Map<String, Object> paymentInfo = response.getData();
    String payUrl = (String) paymentInfo.get("payUrl");
    // 引导用户到支付页面
} else {
    // 处理创建失败
    log.error("创建支付失败: {}", response.getErrorMessage());
}
```

### 处理支付回调示例

```java
// 构建回调请求
PaymentCallbackRequest request = new PaymentCallbackRequest()
    .setPaymentRecordId(123456789L)
    .setOrderNo("ORDER_20240115001")
    .setInternalTransactionNo("INTERNAL_20240115001")
    .setUserId(1001L)
    .setUserName("张三")
    .setPayAmount(new BigDecimal("99.99"))
    .setPayType("ALIPAY")
    .setCallbackType("PAYMENT")
    .setCallbackSource("ALIPAY")
    .setCallbackResult("SUCCESS")
    .setCallbackContent(rawCallbackData)
    .setCallbackSignature(signature)
    .setActualPayAmount(new BigDecimal("99.99"))
    .setThirdPartyOrderNo("ALIPAY_20240115001")
    .setClientIp("47.102.103.104");

// 调用回调处理接口
SingleResponse<Boolean> response = paymentFacadeService.processPaymentCallback(request);

if (response.isSuccess() && response.getData()) {
    // 回调处理成功
    return "SUCCESS";
} else {
    // 回调处理失败，第三方会继续重试
    return "FAILED";
}
```

## 升级指南

### 从 v1.0 升级到 v2.0

1. **更新依赖**：确保使用最新版本的 API 依赖
2. **使用新接口**：推荐使用新的去连表化接口
3. **传递完整参数**：调用时传递完整的用户和订单信息
4. **旧接口兼容**：v1.0 接口标记为 `@Deprecated`，但仍可正常使用
5. **数据库升级**：执行 SQL 升级脚本，添加冗余字段

### 兼容性说明

- v2.0 完全兼容 v1.0 接口
- 建议在新项目中使用 v2.0 接口
- v1.0 接口将在未来版本中移除

## 最佳实践

### 1. 幂等性处理
- 支付创建和回调处理都支持幂等性
- 相同参数的重复调用会返回相同结果

### 2. 错误处理
- 检查 `success` 字段判断调用是否成功
- 根据 `errorCode` 进行不同的错误处理逻辑

### 3. 安全建议
- 验证回调签名，确保数据来源可信
- 使用 HTTPS 进行数据传输
- 敏感信息加密存储

### 4. 性能优化
- 合理设置查询限制参数
- 使用异步处理回调
- 定期清理历史数据

### 5. 监控告警
- 监控支付成功率
- 监控回调处理耗时
- 设置异常情况告警

---

**文档版本**: v2.0.0  
**最后更新**: 2024-01-15  
**维护团队**: Collide Payment Team 