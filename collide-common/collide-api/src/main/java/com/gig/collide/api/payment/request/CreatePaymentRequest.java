package com.gig.collide.api.payment.request;

import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 创建支付请求（去连表设计）
 * 包含完整的用户和订单信息，避免联表查询
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
public class CreatePaymentRequest {

    // =============================================
    // 基础信息
    // =============================================
    
    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 内部支付流水号（可选，系统自动生成）
     */
    @Size(max = 128, message = "内部支付流水号长度不能超过128个字符")
    private String internalTransactionNo;

    // =============================================
    // 用户信息（完整信息，避免联表查询）
    // =============================================
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;

    /**
     * 用户名称
     */
    @NotBlank(message = "用户名称不能为空")
    @Size(max = 64, message = "用户名称长度不能超过64个字符")
    private String userName;

    /**
     * 用户手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String userPhone;

    /**
     * 用户邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String userEmail;

    // =============================================
    // 订单信息（完整信息，避免联表查询）
    // =============================================
    
    /**
     * 订单标题
     */
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 255, message = "订单标题长度不能超过255个字符")
    private String orderTitle;

    /**
     * 商品名称
     */
    @Size(max = 255, message = "商品名称长度不能超过255个字符")
    private String productName;

    /**
     * 商品类型
     */
    @Size(max = 50, message = "商品类型长度不能超过50个字符")
    private String productType;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    @Size(max = 128, message = "商户名称长度不能超过128个字符")
    private String merchantName;

    // =============================================
    // 金额信息
    // =============================================
    
    /**
     * 支付金额（元）
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01元")
    @DecimalMax(value = "999999.99", message = "支付金额不能超过999999.99元")
    @Digits(integer = 6, fraction = 2, message = "支付金额格式不正确")
    private BigDecimal payAmount;

    /**
     * 优惠金额（元）
     */
    @DecimalMin(value = "0.00", message = "优惠金额不能小于0")
    @Digits(integer = 6, fraction = 2, message = "优惠金额格式不正确")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 货币代码
     */
    @Pattern(regexp = "^(CNY|USD|EUR)$", message = "不支持的货币类型")
    private String currencyCode = "CNY";

    // =============================================
    // 支付信息
    // =============================================
    
    /**
     * 支付方式：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试
     */
    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(ALIPAY|WECHAT|UNIONPAY|TEST)$", message = "不支持的支付方式")
    private String payType;

    /**
     * 支付场景：WEB-网页，MOBILE-手机，APP-应用内，MINI-小程序
     */
    @Pattern(regexp = "^(WEB|MOBILE|APP|MINI)$", message = "不支持的支付场景")
    private String payScene = "WEB";

    /**
     * 具体支付方式（如：余额支付、信用卡等）
     */
    @Size(max = 50, message = "具体支付方式长度不能超过50个字符")
    private String payMethod;

    // =============================================
    // 时间信息
    // =============================================
    
    /**
     * 支付过期时间（可选，默认30分钟后过期）
     */
    private LocalDateTime expireTime;

    // =============================================
    // 网络信息
    // =============================================
    
    /**
     * 客户端IP地址
     */
    @Pattern(regexp = "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$|^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$", 
             message = "IP地址格式不正确")
    private String clientIp;

    /**
     * 用户代理信息
     */
    @Size(max = 512, message = "用户代理信息长度不能超过512个字符")
    private String userAgent;

    /**
     * 设备信息
     */
    @Size(max = 255, message = "设备信息长度不能超过255个字符")
    private String deviceInfo;

    // =============================================
    // 回调通知信息
    // =============================================
    
    /**
     * 异步回调通知URL
     */
    @Pattern(regexp = "^https?://.*", message = "回调URL格式不正确")
    @Size(max = 512, message = "回调URL长度不能超过512个字符")
    private String notifyUrl;

    /**
     * 同步返回URL
     */
    @Pattern(regexp = "^https?://.*", message = "返回URL格式不正确")
    @Size(max = 512, message = "返回URL长度不能超过512个字符")
    private String returnUrl;

    /**
     * 最大回调次数
     */
    @Min(value = 1, message = "最大回调次数不能小于1")
    @Max(value = 10, message = "最大回调次数不能大于10")
    private Integer maxNotifyCount = 5;

    // =============================================
    // 扩展信息
    // =============================================
    
    /**
     * 扩展信息
     */
    private Map<String, Object> extraData;

    /**
     * 业务相关数据
     */
    private Map<String, Object> businessData;

    /**
     * 备注信息
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;

    // =============================================
    // 风控信息
    // =============================================
    
    /**
     * 风险等级：LOW-低风险，MEDIUM-中风险，HIGH-高风险
     */
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH)$", message = "不支持的风险等级")
    private String riskLevel = "LOW";

    /**
     * 风险评分（0-100）
     */
    @Min(value = 0, message = "风险评分不能小于0")
    @Max(value = 100, message = "风险评分不能大于100")
    private Integer riskScore = 0;
} 