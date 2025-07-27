package com.gig.collide.payment.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类（去连表设计）
 * 对应数据库表：t_payment_record
 * 设计原则：通过冗余字段避免联表查询，提高查询性能
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_payment_record")
public class PaymentRecord {

    // =============================================
    // 基础信息
    // =============================================
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 支付流水号（外部第三方）
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 内部支付流水号（系统生成）
     */
    @TableField("internal_transaction_no")
    private String internalTransactionNo;

    // =============================================
    // 用户信息（冗余字段，避免联表查询用户表）
    // =============================================
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名称（冗余字段）
     */
    @TableField("user_name")
    private String userName;

    /**
     * 用户手机号（冗余字段）
     */
    @TableField("user_phone")
    private String userPhone;

    /**
     * 用户邮箱（冗余字段）
     */
    @TableField("user_email")
    private String userEmail;

    // =============================================
    // 订单信息（冗余字段，避免联表查询订单表）
    // =============================================
    
    /**
     * 订单标题（冗余字段）
     */
    @TableField("order_title")
    private String orderTitle;

    /**
     * 商品名称（冗余字段）
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品类型（冗余字段）
     */
    @TableField("product_type")
    private String productType;

    /**
     * 商户ID（冗余字段）
     */
    @TableField("merchant_id")
    private Long merchantId;

    /**
     * 商户名称（冗余字段）
     */
    @TableField("merchant_name")
    private String merchantName;

    // =============================================
    // 金额信息
    // =============================================
    
    /**
     * 支付金额（元）
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 实际支付金额（元）
     */
    @TableField("actual_pay_amount")
    private BigDecimal actualPayAmount;

    /**
     * 优惠金额（元）
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 货币代码
     */
    @TableField("currency_code")
    private String currencyCode;

    // =============================================
    // 支付信息
    // =============================================
    
    /**
     * 支付方式：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试
     */
    @TableField("pay_type")
    private String payType;

    /**
     * 支付状态：PENDING-待支付，SUCCESS-成功，FAILED-失败，CANCELLED-已取消，REFUNDING-退款中，REFUNDED-已退款
     */
    @TableField("pay_status")
    private String payStatus;

    /**
     * 支付渠道详细信息
     */
    @TableField("pay_channel")
    private String payChannel;

    /**
     * 支付场景：WEB-网页，MOBILE-手机，APP-应用内，MINI-小程序
     */
    @TableField("pay_scene")
    private String payScene;

    /**
     * 具体支付方式（如：余额支付、信用卡等）
     */
    @TableField("pay_method")
    private String payMethod;

    // =============================================
    // 时间信息
    // =============================================
    
    /**
     * 支付发起时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 支付完成时间
     */
    @TableField("complete_time")
    private LocalDateTime completeTime;

    /**
     * 支付过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    // =============================================
    // 网络信息
    // =============================================
    
    /**
     * 客户端IP地址
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 用户代理信息
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    // =============================================
    // 回调通知信息
    // =============================================
    
    /**
     * 异步回调通知URL
     */
    @TableField("notify_url")
    private String notifyUrl;

    /**
     * 同步返回URL
     */
    @TableField("return_url")
    private String returnUrl;

    /**
     * 回调状态：PENDING-未回调，SUCCESS-成功，FAILED-失败
     */
    @TableField("notify_status")
    private String notifyStatus;

    /**
     * 回调重试次数
     */
    @TableField("notify_count")
    private Integer notifyCount;

    /**
     * 最后回调时间
     */
    @TableField("last_notify_time")
    private LocalDateTime lastNotifyTime;

    /**
     * 最大回调次数
     */
    @TableField("max_notify_count")
    private Integer maxNotifyCount;

    // =============================================
    // 退款信息
    // =============================================
    
    /**
     * 已退款金额（元）
     */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    @TableField("refund_reason")
    private String refundReason;

    /**
     * 退款状态：PENDING-退款中，SUCCESS-退款成功，FAILED-退款失败
     */
    @TableField("refund_status")
    private String refundStatus;

    /**
     * 退款时间
     */
    @TableField("refund_time")
    private LocalDateTime refundTime;

    // =============================================
    // 失败信息
    // =============================================
    
    /**
     * 失败错误码
     */
    @TableField("failure_code")
    private String failureCode;

    /**
     * 支付失败原因
     */
    @TableField("failure_reason")
    private String failureReason;

    /**
     * 失败时间
     */
    @TableField("failure_time")
    private LocalDateTime failureTime;

    // =============================================
    // 风控信息
    // =============================================
    
    /**
     * 风险等级：LOW-低风险，MEDIUM-中风险，HIGH-高风险
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 风险评分（0-100）
     */
    @TableField("risk_score")
    private Integer riskScore;

    /**
     * 是否被风控拦截：false-否，true-是
     */
    @TableField("is_blocked")
    private Boolean isBlocked;

    // =============================================
    // 扩展信息
    // =============================================
    
    /**
     * 扩展信息（JSON格式）
     */
    @TableField("extra_data")
    private String extraData;

    /**
     * 第三方支付平台返回数据
     */
    @TableField("third_party_data")
    private String thirdPartyData;

    /**
     * 业务相关数据
     */
    @TableField("business_data")
    private String businessData;

    /**
     * 备注信息
     */
    @TableField("remark")
    private String remark;

    // =============================================
    // 系统字段
    // =============================================
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识：false-未删除，true-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    // =============================================
    // 常量定义
    // =============================================
    
    /**
     * 支付方式常量
     */
    public static class PayType {
        public static final String ALIPAY = "ALIPAY";
        public static final String WECHAT = "WECHAT";
        public static final String UNIONPAY = "UNIONPAY";
        public static final String TEST = "TEST";
    }

    /**
     * 支付状态常量
     */
    public static class PayStatus {
        public static final String PENDING = "PENDING";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String CANCELLED = "CANCELLED";
        public static final String REFUNDING = "REFUNDING";
        public static final String REFUNDED = "REFUNDED";
    }

    /**
     * 支付场景常量
     */
    public static class PayScene {
        public static final String WEB = "WEB";
        public static final String MOBILE = "MOBILE";
        public static final String APP = "APP";
        public static final String MINI = "MINI";
    }

    /**
     * 回调状态常量
     */
    public static class NotifyStatus {
        public static final String PENDING = "PENDING";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
    }

    /**
     * 退款状态常量
     */
    public static class RefundStatus {
        public static final String PENDING = "PENDING";
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
    }

    /**
     * 风险等级常量
     */
    public static class RiskLevel {
        public static final String LOW = "LOW";
        public static final String MEDIUM = "MEDIUM";
        public static final String HIGH = "HIGH";
    }

    /**
     * 货币代码常量
     */
    public static class CurrencyCode {
        public static final String CNY = "CNY";
        public static final String USD = "USD";
        public static final String EUR = "EUR";
    }
} 