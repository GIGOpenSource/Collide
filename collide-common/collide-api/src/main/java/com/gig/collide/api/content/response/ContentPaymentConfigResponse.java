package com.gig.collide.api.content.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容付费配置响应
 * 返回内容的付费策略和销售统计信息
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContentPaymentConfigResponse implements Serializable {

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 内容ID
     */
    private Long contentId;

    // =================== 付费策略 ===================

    /**
     * 付费类型：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
     */
    private String paymentType;

    /**
     * 付费类型描述
     */
    private String paymentTypeDesc;

    /**
     * 金币价格
     */
    private Long coinPrice;

    /**
     * 原价金币
     */
    private Long originalPrice;

    /**
     * 有效价格（考虑折扣后）
     */
    private Long effectivePrice;

    /**
     * 是否有折扣
     */
    private Boolean hasDiscount;

    /**
     * 折扣率（百分比）
     */
    private Double discountRate;

    // =================== 权限配置 ===================

    /**
     * 会员免费
     */
    private Boolean vipFree;

    /**
     * 是否只有VIP才可以购买
     */
    private Boolean vipOnly;

    /**
     * 是否支持试读
     */
    private Boolean trialEnabled;

    /**
     * 试读内容
     */
    private String trialContent;

    /**
     * 试读字数
     */
    private Integer trialWordCount;

    // =================== 时效配置 ===================

    /**
     * 是否永久有效
     */
    private Boolean isPermanent;

    /**
     * 有效天数（非永久时使用）
     */
    private Integer validDays;

    /**
     * 时效描述
     */
    private String validityDesc;

    // =================== 销售统计 ===================

    /**
     * 总销量
     */
    private Long totalSales;

    /**
     * 总收入（金币）
     */
    private Long totalRevenue;

    /**
     * 平均收入（每次销售的平均收入）
     */
    private Double averageRevenue;

    /**
     * 销售排名
     */
    private Integer salesRank;

    /**
     * 收入排名
     */
    private Integer revenueRank;

    // =================== 状态信息 ===================

    /**
     * 配置状态：ACTIVE、INACTIVE
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDesc;

    // =================== 时间信息 ===================

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 用户相关信息（可选） ===================

    /**
     * 用户是否可以免费访问
     */
    private Boolean userCanAccessFree;

    /**
     * 用户是否有购买权限
     */
    private Boolean userCanPurchase;

    /**
     * 用户实际需要支付的价格
     */
    private Long userActualPrice;

    /**
     * 用户是否已购买
     */
    private Boolean userHasPurchased;

    // =================== 计算方法 ===================

    /**
     * 获取付费类型描述
     */
    public String getPaymentTypeDesc() {
        if (paymentTypeDesc != null) {
            return paymentTypeDesc;
        }
        
        switch (paymentType) {
            case "FREE":
                return "免费";
            case "COIN_PAY":
                return "金币付费";
            case "VIP_FREE":
                return "VIP免费";
            case "TIME_LIMITED":
                return "限时内容";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (statusDesc != null) {
            return statusDesc;
        }
        
        switch (status) {
            case "ACTIVE":
                return "启用";
            case "INACTIVE":
                return "禁用";
            default:
                return "未知";
        }
    }

    /**
     * 获取时效描述
     */
    public String getValidityDesc() {
        if (validityDesc != null) {
            return validityDesc;
        }
        
        if (isPermanent != null && isPermanent) {
            return "永久有效";
        } else if (validDays != null) {
            return validDays + "天有效";
        } else {
            return "未设置";
        }
    }

    /**
     * 判断是否有折扣
     */
    public Boolean getHasDiscount() {
        if (hasDiscount != null) {
            return hasDiscount;
        }
        return originalPrice != null && coinPrice != null && coinPrice < originalPrice;
    }

    /**
     * 计算折扣率
     */
    public Double getDiscountRate() {
        if (discountRate != null) {
            return discountRate;
        }
        
        if (originalPrice == null || originalPrice == 0 || coinPrice == null) {
            return 0.0;
        }
        if (coinPrice >= originalPrice) {
            return 0.0;
        }
        return (double) (originalPrice - coinPrice) / originalPrice * 100;
    }

    /**
     * 获取有效价格
     */
    public Long getEffectivePrice() {
        if (effectivePrice != null) {
            return effectivePrice;
        }
        return coinPrice != null ? coinPrice : 0L;
    }

    /**
     * 计算平均收入
     */
    public Double getAverageRevenue() {
        if (averageRevenue != null) {
            return averageRevenue;
        }
        
        if (totalSales == null || totalSales == 0 || totalRevenue == null) {
            return 0.0;
        }
        return (double) totalRevenue / totalSales;
    }
}