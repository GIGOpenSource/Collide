package com.gig.collide.api.payment.response.data;

import com.gig.collide.api.payment.constant.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentRecordInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付记录ID
     */
    private Long id;

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
     * 用户手机号
     */
    private String userPhone;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 订单标题
     */
    private String orderTitle;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品类型
     */
    private String productType;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 实际支付金额
     */
    private BigDecimal actualPayAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 支付方式
     */
    private PaymentTypeEnum payType;

    /**
     * 支付状态
     */
    private PaymentStatusEnum payStatus;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 支付场景
     */
    private PaymentSceneEnum payScene;

    /**
     * 支付方式详情
     */
    private String payMethod;

    /**
     * 支付发起时间
     */
    private LocalDateTime payTime;

    /**
     * 支付完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 支付过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 客户端IP地址
     */
    private String clientIp;

    /**
     * 用户代理信息
     */
    private String userAgent;

    /**
     * 设备信息
     */
    private String deviceInfo;

    /**
     * 异步回调通知URL
     */
    private String notifyUrl;

    /**
     * 同步返回URL
     */
    private String returnUrl;

    /**
     * 回调状态
     */
    private CallbackStatusEnum notifyStatus;

    /**
     * 回调重试次数
     */
    private Integer notifyCount;

    /**
     * 最后回调时间
     */
    private LocalDateTime lastNotifyTime;

    /**
     * 最大回调次数
     */
    private Integer maxNotifyCount;

    /**
     * 已退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 失败错误码
     */
    private String failureCode;

    /**
     * 支付失败原因
     */
    private String failureReason;

    /**
     * 失败时间
     */
    private LocalDateTime failureTime;

    /**
     * 风险等级
     */
    private RiskLevelEnum riskLevel;

    /**
     * 风险评分
     */
    private Integer riskScore;

    /**
     * 是否被风控拦截
     */
    private Boolean isBlocked;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 版本号
     */
    private Integer version;
} 