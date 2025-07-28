package com.gig.collide.api.payment.response.data;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 支付方式信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentMethodInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 支付方式
     */
    private PaymentTypeEnum payType;

    /**
     * 支付方式名称
     */
    private String payTypeName;

    /**
     * 支付方式描述
     */
    private String payTypeDescription;

    /**
     * 支付方式图标URL
     */
    private String iconUrl;

    /**
     * 是否可用
     */
    private Boolean isAvailable;

    /**
     * 是否推荐
     */
    private Boolean isRecommended;

    /**
     * 是否为默认支付方式
     */
    private Boolean isDefault;

    /**
     * 支持的支付场景
     */
    private List<PaymentSceneEnum> supportedScenes;

    /**
     * 最小支付金额
     */
    private BigDecimal minAmount;

    /**
     * 最大支付金额
     */
    private BigDecimal maxAmount;

    /**
     * 手续费率
     */
    private BigDecimal feeRate;

    /**
     * 固定手续费
     */
    private BigDecimal fixedFee;

    /**
     * 预计到账时间（分钟）
     */
    private Integer estimatedArrivalMinutes;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 支付方式状态
     */
    private String status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 特殊限制说明
     */
    private String restrictions;
} 