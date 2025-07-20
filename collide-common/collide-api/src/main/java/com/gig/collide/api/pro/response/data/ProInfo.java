package com.gig.collide.api.pro.response.data;

import com.gig.collide.api.pro.constant.ProPackageType;
import com.gig.collide.api.pro.constant.ProStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 付费用户信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ProInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否为付费用户
     */
    private Boolean isPro;

    /**
     * 付费套餐类型
     */
    private ProPackageType packageType;

    /**
     * 付费状态
     */
    private ProStatus proStatus;

    /**
     * 付费开始时间
     */
    private Date proStartTime;

    /**
     * 付费结束时间
     */
    private Date proEndTime;

    /**
     * 是否已过期
     */
    private Boolean isExpired;

    /**
     * 剩余天数
     */
    private Integer remainingDays;

    /**
     * 付费总时长（月）
     */
    private Integer totalDuration;

    /**
     * 付费总金额
     */
    private Long totalAmount;

    /**
     * 最后一次付费时间
     */
    private Date lastPaymentTime;

    /**
     * 自动续费状态
     */
    private Boolean autoRenewal;
} 