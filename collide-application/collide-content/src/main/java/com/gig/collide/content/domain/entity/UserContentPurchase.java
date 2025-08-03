package com.gig.collide.content.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户内容购买记录表实体类
 * 记录用户购买付费内容的完整信息，支持权限验证和统计分析
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_content_purchase")
public class UserContentPurchase {

    /**
     * 购买记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容ID
     */
    @TableField("content_id")
    private Long contentId;

    // =================== 内容信息冗余（避免连表查询） ===================

    /**
     * 内容标题（冗余）
     */
    @TableField("content_title")
    private String contentTitle;

    /**
     * 内容类型（冗余）
     */
    @TableField("content_type")
    private String contentType;

    /**
     * 内容封面（冗余）
     */
    @TableField("content_cover_url")
    private String contentCoverUrl;

    // =================== 作者信息冗余 ===================

    /**
     * 作者ID（冗余）
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 作者昵称（冗余）
     */
    @TableField("author_nickname")
    private String authorNickname;

    // =================== 购买相关信息 ===================

    /**
     * 关联订单ID
     */
    @TableField("order_id")
    private Long orderId;

    /**
     * 订单号（冗余）
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 支付金币数量
     */
    @TableField("coin_amount")
    private Long coinAmount;

    /**
     * 原价金币
     */
    @TableField("original_price")
    private Long originalPrice;

    /**
     * 优惠金币数量
     */
    @TableField("discount_amount")
    private Long discountAmount;

    // =================== 购买状态 ===================

    /**
     * 状态：ACTIVE、EXPIRED、REFUNDED
     */
    @TableField("status")
    private String status;

    /**
     * 购买时间
     */
    @TableField("purchase_time")
    private LocalDateTime purchaseTime;

    /**
     * 过期时间（为空表示永久有效）
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    // =================== 访问统计 ===================

    /**
     * 访问次数
     */
    @TableField("access_count")
    private Integer accessCount;

    /**
     * 最后访问时间
     */
    @TableField("last_access_time")
    private LocalDateTime lastAccessTime;

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
     * 判断是否活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * 判断是否已过期
     */
    public boolean isExpired() {
        if ("EXPIRED".equals(status)) {
            return true;
        }
        if (expireTime == null) {
            return false; // 永久有效
        }
        return LocalDateTime.now().isAfter(expireTime);
    }

    /**
     * 判断是否已退款
     */
    public boolean isRefunded() {
        return "REFUNDED".equals(status);
    }

    /**
     * 判断当前是否有权限访问
     */
    public boolean hasAccessPermission() {
        return isActive() && !isExpired();
    }

    /**
     * 计算实际支付金币数量
     */
    public Long getActualPaidAmount() {
        if (coinAmount == null) {
            return 0L;
        }
        if (discountAmount == null) {
            return coinAmount;
        }
        return coinAmount - discountAmount;
    }

    /**
     * 计算优惠率（百分比）
     */
    public Double getDiscountRate() {
        if (originalPrice == null || originalPrice == 0 || discountAmount == null) {
            return 0.0;
        }
        return (double) discountAmount / originalPrice * 100;
    }

    /**
     * 获取剩余有效天数
     */
    public Long getRemainingDays() {
        if (expireTime == null) {
            return -1L; // 永久有效
        }
        if (isExpired()) {
            return 0L;
        }
        return java.time.Duration.between(LocalDateTime.now(), expireTime).toDays();
    }
}