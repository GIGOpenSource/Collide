package com.gig.collide.payment.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付回调记录实体类（去连表设计）
 * 对应数据库表：t_payment_callback
 * 设计原则：通过冗余字段避免联表查询，提高查询性能
 * 
 * @author Collide
 * @since 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_payment_callback")
public class PaymentCallback {

    // =============================================
    // 基础信息
    // =============================================
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 支付记录ID（不使用外键约束）
     */
    @TableField("payment_record_id")
    private Long paymentRecordId;

    // =============================================
    // 冗余支付信息（避免联表查询支付表）
    // =============================================
    
    /**
     * 订单号（冗余字段）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 支付流水号（冗余字段）
     */
    @TableField("transaction_no")
    private String transactionNo;

    /**
     * 内部支付流水号（冗余字段）
     */
    @TableField("internal_transaction_no")
    private String internalTransactionNo;

    /**
     * 用户ID（冗余字段）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名称（冗余字段）
     */
    @TableField("user_name")
    private String userName;

    /**
     * 支付金额（冗余字段）
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 支付方式（冗余字段）
     */
    @TableField("pay_type")
    private String payType;

    // =============================================
    // 回调信息
    // =============================================
    
    /**
     * 回调类型：PAYMENT-支付回调，REFUND-退款回调，CANCEL-取消回调
     */
    @TableField("callback_type")
    private String callbackType;

    /**
     * 回调来源：ALIPAY-支付宝，WECHAT-微信，UNIONPAY-银联，TEST-测试
     */
    @TableField("callback_source")
    private String callbackSource;

    /**
     * 回调处理状态：SUCCESS-成功，FAILED-失败，PENDING-处理中
     */
    @TableField("callback_status")
    private String callbackStatus;

    /**
     * 回调业务结果：SUCCESS-业务成功，FAILED-业务失败
     */
    @TableField("callback_result")
    private String callbackResult;

    // =============================================
    // 回调数据
    // =============================================
    
    /**
     * 回调原始内容数据
     */
    @TableField("callback_content")
    private String callbackContent;

    /**
     * 回调签名信息
     */
    @TableField("callback_signature")
    private String callbackSignature;

    /**
     * 签名验证结果：false-失败，true-成功，null-未验证
     */
    @TableField("signature_valid")
    private Boolean signatureValid;

    /**
     * 回调参数（JSON格式）
     */
    @TableField("callback_params")
    private String callbackParams;

    // =============================================
    // 处理信息
    // =============================================
    
    /**
     * 处理结果状态
     */
    @TableField("process_result")
    private String processResult;

    /**
     * 处理结果消息
     */
    @TableField("process_message")
    private String processMessage;

    /**
     * 错误代码
     */
    @TableField("error_code")
    private String errorCode;

    /**
     * 错误信息详情
     */
    @TableField("error_message")
    private String errorMessage;

    // =============================================
    // 网络信息
    // =============================================
    
    /**
     * 回调请求IP地址
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 用户代理信息
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 请求头信息（JSON格式）
     */
    @TableField("request_headers")
    private String requestHeaders;

    // =============================================
    // 性能信息
    // =============================================
    
    /**
     * 处理开始时间
     */
    @TableField("process_start_time")
    private LocalDateTime processStartTime;

    /**
     * 处理结束时间
     */
    @TableField("process_end_time")
    private LocalDateTime processEndTime;

    /**
     * 处理耗时（毫秒）
     */
    @TableField("process_time_ms")
    private Long processTimeMs;

    // =============================================
    // 重试信息
    // =============================================
    
    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    @TableField("max_retry_count")
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    @TableField("next_retry_time")
    private LocalDateTime nextRetryTime;

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

    // =============================================
    // 常量定义
    // =============================================
    
    /**
     * 回调类型常量
     */
    public static class CallbackType {
        public static final String PAYMENT = "PAYMENT";
        public static final String REFUND = "REFUND";
        public static final String CANCEL = "CANCEL";
    }

    /**
     * 回调来源常量
     */
    public static class CallbackSource {
        public static final String ALIPAY = "ALIPAY";
        public static final String WECHAT = "WECHAT";
        public static final String UNIONPAY = "UNIONPAY";
        public static final String TEST = "TEST";
    }

    /**
     * 回调状态常量
     */
    public static class CallbackStatus {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String PENDING = "PENDING";
    }

    /**
     * 回调结果常量
     */
    public static class CallbackResult {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
    }

    /**
     * 处理结果常量
     */
    public static class ProcessResult {
        public static final String SUCCESS = "SUCCESS";
        public static final String FAILED = "FAILED";
        public static final String PROCESSING = "PROCESSING";
    }
} 