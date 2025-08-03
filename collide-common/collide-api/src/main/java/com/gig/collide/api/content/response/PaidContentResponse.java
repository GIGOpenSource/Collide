package com.gig.collide.api.content.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 付费内容响应
 * 返回内容基本信息和付费策略的组合数据
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
public class PaidContentResponse implements Serializable {

    // =================== 内容基本信息 ===================

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    private String contentType;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 标签
     */
    private String tags;

    // =================== 作者信息 ===================

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称
     */
    private String authorNickname;

    /**
     * 作者头像
     */
    private String authorAvatar;

    // =================== 分类信息 ===================

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    // =================== 付费信息 ===================

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
     * 是否有折扣
     */
    private Boolean hasDiscount;

    /**
     * 折扣率（百分比）
     */
    private Double discountRate;

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
     * 试读字数
     */
    private Integer trialWordCount;

    /**
     * 是否永久有效
     */
    private Boolean isPermanent;

    /**
     * 有效天数
     */
    private Integer validDays;

    // =================== 销售统计 ===================

    /**
     * 总销量
     */
    private Long totalSales;

    /**
     * 总收入
     */
    private Long totalRevenue;

    /**
     * 销售排名
     */
    private Integer salesRank;

    // =================== 内容统计 ===================

    /**
     * 查看数
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 评论数
     */
    private Long commentCount;

    /**
     * 收藏数
     */
    private Long favoriteCount;

    /**
     * 评分数
     */
    private Long scoreCount;

    /**
     * 总评分
     */
    private Long scoreTotal;

    /**
     * 平均评分
     */
    private Double averageScore;

    // =================== 用户相关信息（当提供userId时） ===================

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

    /**
     * 用户购买时间
     */
    private LocalDateTime userPurchaseTime;

    /**
     * 用户访问次数
     */
    private Integer userAccessCount;

    // =================== 状态信息 ===================

    /**
     * 内容状态：DRAFT、PUBLISHED、OFFLINE
     */
    private String contentStatus;

    /**
     * 审核状态：PENDING、APPROVED、REJECTED
     */
    private String reviewStatus;

    /**
     * 付费配置状态：ACTIVE、INACTIVE
     */
    private String paymentStatus;

    // =================== 时间信息 ===================

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 扩展信息 ===================

    /**
     * 热度值（综合计算）
     */
    private Double hotScore;

    /**
     * 推荐理由
     */
    private String recommendReason;

    /**
     * 特殊标签：HOT、NEW、DISCOUNT、VIP_FREE、BESTSELLER
     */
    private String specialTag;

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
     * 计算平均评分
     */
    public Double getAverageScore() {
        if (averageScore != null) {
            return averageScore;
        }
        
        if (scoreCount == null || scoreCount == 0 || scoreTotal == null) {
            return 0.0;
        }
        return (double) scoreTotal / scoreCount;
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
     * 获取特殊标签
     */
    public String getSpecialTag() {
        if (specialTag != null) {
            return specialTag;
        }
        
        // 根据业务逻辑自动判断特殊标签
        if (getHasDiscount()) {
            return "DISCOUNT";
        }
        if (Boolean.TRUE.equals(vipFree)) {
            return "VIP_FREE";
        }
        if (totalSales != null && totalSales > 1000) {
            return "BESTSELLER";
        }
        if (createTime != null && createTime.isAfter(LocalDateTime.now().minusDays(7))) {
            return "NEW";
        }
        if (hotScore != null && hotScore > 80) {
            return "HOT";
        }
        
        return null;
    }

    /**
     * 判断是否可以查看
     */
    public boolean isViewable() {
        return "PUBLISHED".equals(contentStatus) && "APPROVED".equals(reviewStatus);
    }

    /**
     * 判断付费配置是否有效
     */
    public boolean isPaymentActive() {
        return "ACTIVE".equals(paymentStatus);
    }
}