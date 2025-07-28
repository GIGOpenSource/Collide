package com.gig.collide.api.payment.response.data;

import com.gig.collide.api.payment.constant.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付回调信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentCallbackInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 回调记录ID
     */
    private Long id;

    /**
     * 支付记录ID
     */
    private Long paymentRecordId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 第三方交易流水号
     */
    private String transactionNo;

    /**
     * 内部交易流水号
     */
    private String internalTransactionNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付方式
     */
    private PaymentTypeEnum payType;

    /**
     * 回调类型
     */
    private CallbackTypeEnum callbackType;

    /**
     * 回调来源
     */
    private PaymentTypeEnum callbackSource;

    /**
     * 回调处理状态
     */
    private CallbackStatusEnum callbackStatus;

    /**
     * 回调业务结果
     */
    private String callbackResult;

    /**
     * 回调原始内容数据
     */
    private String callbackContent;

    /**
     * 回调签名信息
     */
    private String callbackSignature;

    /**
     * 签名验证结果
     */
    private Boolean signatureValid;

    /**
     * 处理结果状态
     */
    private String processResult;

    /**
     * 处理结果消息
     */
    private String processMessage;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 错误信息详情
     */
    private String errorMessage;

    /**
     * 回调请求IP地址
     */
    private String clientIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 处理开始时间
     */
    private LocalDateTime processStartTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime processEndTime;

    /**
     * 处理耗时（毫秒）
     */
    private Long processTimeMs;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 