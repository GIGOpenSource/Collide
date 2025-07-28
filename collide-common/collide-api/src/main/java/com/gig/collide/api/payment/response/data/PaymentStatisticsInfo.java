package com.gig.collide.api.payment.response.data;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 支付统计信息响应对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class PaymentStatisticsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计记录ID
     */
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计小时（0-23，NULL表示日统计）
     */
    private Integer statHour;

    /**
     * 支付方式
     */
    private PaymentTypeEnum payType;

    /**
     * 支付场景
     */
    private PaymentSceneEnum payScene;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 商品类型
     */
    private String productType;

    /**
     * 总支付笔数
     */
    private Integer totalCount;

    /**
     * 成功支付笔数
     */
    private Integer successCount;

    /**
     * 失败支付笔数
     */
    private Integer failedCount;

    /**
     * 取消支付笔数
     */
    private Integer cancelledCount;

    /**
     * 待支付笔数
     */
    private Integer pendingCount;

    /**
     * 退款笔数
     */
    private Integer refundCount;

    /**
     * 总支付金额（元）
     */
    private BigDecimal totalAmount;

    /**
     * 成功支付金额（元）
     */
    private BigDecimal successAmount;

    /**
     * 失败支付金额（元）
     */
    private BigDecimal failedAmount;

    /**
     * 取消支付金额（元）
     */
    private BigDecimal cancelledAmount;

    /**
     * 待支付金额（元）
     */
    private BigDecimal pendingAmount;

    /**
     * 退款金额（元）
     */
    private BigDecimal refundAmount;

    /**
     * 优惠金额（元）
     */
    private BigDecimal discountAmount;

    /**
     * 平均支付金额（元）
     */
    private BigDecimal avgPayAmount;

    /**
     * 平均处理时长（毫秒）
     */
    private Long avgProcessTimeMs;

    /**
     * 支付成功率（0-1）
     */
    private BigDecimal successRate;

    /**
     * 支付失败率（0-1）
     */
    private BigDecimal failureRate;

    /**
     * 独立用户数
     */
    private Integer uniqueUserCount;

    /**
     * 新用户数
     */
    private Integer newUserCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 统计维度描述
     */
    private String dimensionDescription;

    /**
     * 环比增长率（相比前一期）
     */
    private BigDecimal growthRate;
} 