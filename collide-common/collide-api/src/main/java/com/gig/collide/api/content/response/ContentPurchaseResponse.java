package com.gig.collide.api.content.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容购买记录响应
 * 返回用户的内容购买详情和访问权限信息
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
public class ContentPurchaseResponse implements Serializable {

    /**
     * 购买记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容ID
     */
    private Long contentId;

    // =================== 内容信息（冗余） ===================

    /**
     * 内容标题
     */
    private String contentTitle;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 内容封面
     */
    private String contentCoverUrl;

    // =================== 作者信息（冗余） ===================

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称
     */
    private String authorNickname;

    // =================== 购买信息 ===================

    /**
     * 关联订单ID
     */
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金币数量
     */
    private Long coinAmount;

    /**
     * 原价金币
     */
    private Long originalPrice;

    /**
     * 优惠金币数量
     */
    private Long discountAmount;

    /**
     * 实际支付金币数量
     */
    private Long actualPaidAmount;

    /**
     * 优惠率（百分比）
     */
    private Double discountRate;

    // =================== 状态信息 ===================

    /**
     * 状态：ACTIVE、EXPIRED、REFUNDED
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;

    /**
     * 过期时间（为空表示永久有效）
     */
    private LocalDateTime expireTime;

    /**
     * 是否已过期
     */
    private Boolean isExpired;

    /**
     * 剩余有效天数（-1表示永久有效）
     */
    private Long remainingDays;

    // =================== 访问统计 ===================

    /**
     * 访问次数
     */
    private Integer accessCount;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastAccessTime;

    /**
     * 是否已访问过
     */
    private Boolean hasAccessed;

    // =================== 权限信息 ===================

    /**
     * 是否有访问权限
     */
    private Boolean hasAccessPermission;

    /**
     * 权限描述
     */
    private String permissionDesc;

    // =================== 时间信息 ===================

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
     * 购买来源：WEB、MOBILE、APP
     */
    private String source;

    /**
     * 备注信息
     */
    private String remark;

    // =================== 计算方法 ===================

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (statusDesc != null) {
            return statusDesc;
        }
        
        switch (status) {
            case "ACTIVE":
                return isExpired != null && isExpired ? "已过期" : "有效";
            case "EXPIRED":
                return "已过期";
            case "REFUNDED":
                return "已退款";
            default:
                return "未知";
        }
    }

    /**
     * 获取权限描述
     */
    public String getPermissionDesc() {
        if (permissionDesc != null) {
            return permissionDesc;
        }
        
        if (hasAccessPermission != null && hasAccessPermission) {
            return "有权限访问";
        } else {
            return "无权限访问";
        }
    }

    /**
     * 判断是否已访问
     */
    public Boolean getHasAccessed() {
        if (hasAccessed != null) {
            return hasAccessed;
        }
        return accessCount != null && accessCount > 0;
    }
}