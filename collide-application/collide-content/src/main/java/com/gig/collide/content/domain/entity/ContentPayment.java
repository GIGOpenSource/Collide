package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 内容付费配置表实体类
 * 记录内容的付费模式、价格配置和销售统计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_content_payment")
public class ContentPayment {

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    // =================== 付费类型配置 ===================

    /**
     * 付费类型：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
     */
    @TableField("payment_type")
    private String paymentType;

    /**
     * 金币价格
     */
    @TableField("coin_price")
    private Long coinPrice;

    /**
     * 原价（用于折扣显示）
     */
    @TableField("original_price")
    private Long originalPrice;

    // =================== 权限配置 ===================

    /**
     * 会员免费：0否，1是
     */
    @TableField("vip_free")
    private Boolean vipFree;

    /**
     * 是否只有VIP才可以购买：0否，1是
     */
    @TableField("vip_only")
    private Boolean vipOnly;

    /**
     * 是否支持试读：0否，1是
     */
    @TableField("trial_enabled")
    private Boolean trialEnabled;

    /**
     * 试读内容
     */
    @TableField("trial_content")
    private String trialContent;

    /**
     * 试读字数
     */
    @TableField("trial_word_count")
    private Integer trialWordCount;

    // =================== 时效配置 ===================

    /**
     * 是否永久有效：0否，1是
     */
    @TableField("is_permanent")
    private Boolean isPermanent;

    /**
     * 有效天数（非永久时使用）
     */
    @TableField("valid_days")
    private Integer validDays;

    // =================== 销售统计 ===================

    /**
     * 总销量
     */
    @TableField("total_sales")
    private Long totalSales;

    /**
     * 总收入（金币）
     */
    @TableField("total_revenue")
    private Long totalRevenue;

    // =================== 状态配置 ===================

    /**
     * 状态：ACTIVE、INACTIVE
     */
    @TableField("status")
    private String status;

    // =================== 时间字段 ===================

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 判断是否免费内容
     */
    public boolean isFree() {
        return "FREE".equals(paymentType);
    }

    /**
     * 判断是否金币付费
     */
    public boolean isCoinPay() {
        return "COIN_PAY".equals(paymentType);
    }

    /**
     * 判断是否VIP免费
     */
    public boolean isVipFree() {
        return "VIP_FREE".equals(paymentType);
    }

    /**
     * 判断是否限时内容
     */
    public boolean isTimeLimited() {
        return "TIME_LIMITED".equals(paymentType);
    }

    /**
     * 判断是否活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * 判断是否支持试读
     */
    public boolean supportsTrial() {
        return Boolean.TRUE.equals(trialEnabled);
    }

    /**
     * 判断是否永久有效
     */
    public boolean isPermanentAccess() {
        return Boolean.TRUE.equals(isPermanent);
    }

    /**
     * 判断是否只有VIP才能购买
     */
    public boolean isVipOnlyPurchase() {
        return Boolean.TRUE.equals(vipOnly);
    }

    /**
     * 判断用户是否有购买权限
     * @param isVip 用户是否为VIP
     * @return true-可以购买，false-无权限购买
     */
    public boolean canPurchase(boolean isVip) {
        // 如果是VIP专享内容，必须是VIP才能购买
        if (isVipOnlyPurchase()) {
            return isVip;
        }
        // 其他情况都可以购买
        return true;
    }

    /**
     * 判断用户是否可以免费访问
     * @param isVip 用户是否为VIP
     * @return true-可以免费访问，false-需要付费
     */
    public boolean canAccessFree(boolean isVip) {
        // 免费内容
        if (isFree()) {
            return true;
        }
        // VIP免费内容且用户是VIP
        if (Boolean.TRUE.equals(vipFree) && isVip) {
            return true;
        }
        return false;
    }

    /**
     * 计算折扣率（百分比）
     */
    public Double getDiscountRate() {
        if (originalPrice == null || originalPrice == 0 || coinPrice == null) {
            return 0.0;
        }
        if (coinPrice >= originalPrice) {
            return 0.0;
        }
        return (double) (originalPrice - coinPrice) / originalPrice * 100;
    }

    /**
     * 计算平均收入（每次销售的平均收入）
     */
    public Double getAverageRevenue() {
        if (totalSales == null || totalSales == 0 || totalRevenue == null) {
            return 0.0;
        }
        return (double) totalRevenue / totalSales;
    }

    /**
     * 获取有效价格（考虑折扣后的实际价格）
     */
    public Long getEffectivePrice() {
        return coinPrice != null ? coinPrice : 0L;
    }

    /**
     * 判断是否有折扣
     */
    public boolean hasDiscount() {
        return originalPrice != null && coinPrice != null && coinPrice < originalPrice;
    }

    /**
     * 获取销售转化率（假设有浏览量统计）
     */
    public Double getConversionRate(Long viewCount) {
        if (viewCount == null || viewCount == 0 || totalSales == null) {
            return 0.0;
        }
        return (double) totalSales / viewCount * 100;
    }
}