package com.gig.collide.api.content.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 内容购买记录查询请求
 * 用于查询用户的购买历史和内容的销售记录
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
public class ContentPurchaseQueryRequest implements Serializable {

    /**
     * 用户ID
     */
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;

    /**
     * 内容ID
     */
    @Min(value = 1, message = "内容ID必须大于0")
    private Long contentId;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    private String contentType;

    /**
     * 作者ID
     */
    @Min(value = 1, message = "作者ID必须大于0")
    private Long authorId;

    /**
     * 购买状态：ACTIVE、EXPIRED、REFUNDED
     */
    private String status;

    /**
     * 订单ID
     */
    @Min(value = 1, message = "订单ID必须大于0")
    private Long orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 购买开始时间
     */
    private LocalDateTime purchaseStartTime;

    /**
     * 购买结束时间
     */
    private LocalDateTime purchaseEndTime;

    /**
     * 最小金币金额
     */
    @Min(value = 0, message = "最小金币金额不能为负数")
    private Long minCoinAmount;

    /**
     * 最大金币金额
     */
    @Min(value = 0, message = "最大金币金额不能为负数")
    private Long maxCoinAmount;

    /**
     * 是否只查询有效购买（未过期）
     */
    private Boolean onlyValid = false;

    /**
     * 是否只查询未访问的购买
     */
    private Boolean onlyUnread = false;

    /**
     * 排序字段：purchase_time、coin_amount、access_count
     */
    private String sortBy = "purchase_time";

    /**
     * 排序方向：ASC、DESC
     */
    private String sortOrder = "DESC";

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 20;
}