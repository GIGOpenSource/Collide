package com.gig.collide.api.payment.request;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付回调请求（去连表设计）
 * 包含完整的支付和用户信息，避免联表查询
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
public class PaymentCallbackRequest {

    // =============================================
    // 基础信息
    // =============================================
    
    /**
     * 支付记录ID（不使用外键约束）
     */
    @NotNull(message = "支付记录ID不能为空")
    @Min(value = 1, message = "支付记录ID必须大于0")
    private Long paymentRecordId;

    // =============================================
    // 冗余支付信息（避免联表查询支付表）
    // =============================================
    
    /**
     * 订单号（冗余字段）
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 支付流水号（冗余字段）
     */
    @Size(max = 128, message = "支付流水号长度不能超过128个字符")
    private String transactionNo;

    /**
     * 内部支付流水号（冗余字段）
     */
    @NotBlank(message = "内部支付流水号不能为空")
    @Size(max = 128, message = "内部支付流水号长度不能超过128个字符")
    private String internalTransactionNo;

    /**
     * 用户ID（冗余字段）
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;

    /**
     * 用户名称（冗余字段）
     */
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 64, message = "用户名称长度不能超过64个字符")
    private String userName;

    /**
     * 支付金额（冗余字段）
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01元")
    private BigDecimal payAmount;

    /**
     * 支付方式（冗余字段）
     */
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(ALIPAY|WECHAT|UNIONPAY|TEST)$", message = "不支持的支付方式")
    private String payType;

    // =============================================
    // 回调信息
    // =============================================
    
    /**
     * 回调类型：PAYMENT-支付回调，REFUND-退款回调，CANCEL-取消回调
     */
    @NotBlank(message = "回调类型不能为空")
    @Pattern(regexp = "^(PAYMENT|REFUND|CANCEL)$", message = "不支持的回调类型")
    private String callbackType;

    /**
     * 回调来源：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试
     */
    @NotBlank(message = "回调来源不能为空")
    @Pattern(regexp = "^(ALIPAY|WECHAT|UNIONPAY|TEST)$", message = "不支持的回调来源")
    private String callbackSource;

    /**
     * 回调业务结果：SUCCESS-业务成功，FAILED-业务失败
     */
    @Pattern(regexp = "^(SUCCESS|FAILED)$", message = "不支持的回调结果")
    private String callbackResult;

    // =============================================
    // 回调数据
    // =============================================
    
    /**
     * 回调原始内容数据
     */
    @NotBlank(message = "回调内容不能为空")
    @Size(max = 65535, message = "回调内容长度不能超过65535个字符")
    private String callbackContent;

    /**
     * 回调签名信息
     */
    @Size(max = 512, message = "回调签名长度不能超过512个字符")
    private String callbackSignature;

    /**
     * 回调参数
     */
    private Map<String, String> callbackParams;

    // =============================================
    // 处理信息
    // =============================================
    
    /**
     * 处理结果消息
     */
    @Size(max = 500, message = "处理结果消息长度不能超过500个字符")
    private String processMessage;

    /**
     * 错误代码
     */
    @Size(max = 50, message = "错误代码长度不能超过50个字符")
    private String errorCode;

    /**
     * 错误信息详情
     */
    @Size(max = 500, message = "错误信息长度不能超过500个字符")
    private String errorMessage;

    // =============================================
    // 网络信息
    // =============================================
    
    /**
     * 回调请求IP地址
     */
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$", 
             message = "IP地址格式不正确")
    private String clientIp;

    /**
     * 用户代理信息
     */
    @Size(max = 500, message = "用户代理信息长度不能超过500个字符")
    private String userAgent;

    /**
     * 请求头信息
     */
    private Map<String, String> requestHeaders;

    // =============================================
    // 重试信息
    // =============================================
    
    /**
     * 最大重试次数
     */
    @Min(value = 1, message = "最大重试次数不能小于1")
    @Max(value = 10, message = "最大重试次数不能大于10")
    private Integer maxRetryCount = 3;

    // =============================================
    // 业务扩展信息
    // =============================================
    
    /**
     * 实际支付金额（用于成功回调）
     */
    @DecimalMin(value = "0.00", message = "实际支付金额不能小于0")
    private BigDecimal actualPayAmount;

    /**
     * 第三方支付平台订单号
     */
    @Size(max = 128, message = "第三方支付平台订单号长度不能超过128个字符")
    private String thirdPartyOrderNo;

    /**
     * 第三方支付时间
     */
    private String thirdPartyPayTime;

    /**
     * 第三方手续费
     */
    @DecimalMin(value = "0.00", message = "第三方手续费不能小于0")
    private BigDecimal thirdPartyFee;

    /**
     * 退款金额（用于退款回调）
     */
    @DecimalMin(value = "0.00", message = "退款金额不能小于0")
    private BigDecimal refundAmount;

    /**
     * 退款原因（用于退款回调）
     */
    @Size(max = 255, message = "退款原因长度不能超过255个字符")
    private String refundReason;

    /**
     * 失败原因（用于失败回调）
     */
    @Size(max = 255, message = "失败原因长度不能超过255个字符")
    private String failureReason;

    /**
     * 失败错误码（用于失败回调）
     */
    @Size(max = 50, message = "失败错误码长度不能超过50个字符")
    private String failureCode;

    // =============================================
    // 验签相关
    // =============================================
    
    /**
     * 是否需要验签
     */
    private Boolean needVerifySignature = true;

    /**
     * 签名算法
     */
    @Size(max = 20, message = "签名算法长度不能超过20个字符")
    private String signatureAlgorithm = "RSA2";

    /**
     * 编码格式
     */
    @Size(max = 10, message = "编码格式长度不能超过10个字符")
    private String charset = "UTF-8";

    // =============================================
    // 其他信息
    // =============================================
    
    /**
     * 回调通知时间（第三方平台时间）
     */
    private String notifyTime;

    /**
     * 回调版本号
     */
    @Size(max = 10, message = "回调版本号长度不能超过10个字符")
    private String version = "1.0";

    /**
     * 是否异步处理
     */
    private Boolean asyncProcess = false;

    /**
     * 优先级（数值越大优先级越高）
     */
    @Min(value = 0, message = "优先级不能小于0")
    @Max(value = 10, message = "优先级不能大于10")
    private Integer priority = 5;
} 