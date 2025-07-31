package com.gig.collide.api.content.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 付费内容查询请求
 * 用于查询各种付费策略的内容列表
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
public class PaidContentQueryRequest implements Serializable {

    /**
     * 用户ID（用于个性化推荐和权限判断）
     */
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;

    /**
     * 付费类型：FREE、COIN_PAY、VIP_FREE、TIME_LIMITED
     */
    private String paymentType;

    /**
     * 内容类型：NOVEL、COMIC、VIDEO、ARTICLE、AUDIO
     */
    private String contentType;

    /**
     * 分类ID
     */
    @Min(value = 1, message = "分类ID必须大于0")
    private Long categoryId;

    /**
     * 最小价格（金币）
     */
    @Min(value = 0, message = "最小价格不能为负数")
    private Long minPrice;

    /**
     * 最大价格（金币）
     */
    @Min(value = 0, message = "最大价格不能为负数")
    private Long maxPrice;

    /**
     * 是否只显示VIP免费内容
     */
    private Boolean vipFreeOnly = false;

    /**
     * 是否只显示VIP专享内容
     */
    private Boolean vipOnlyContent = false;

    /**
     * 是否只显示支持试读的内容
     */
    private Boolean trialEnabledOnly = false;

    /**
     * 是否只显示永久有效的内容
     */
    private Boolean permanentOnly = false;

    /**
     * 是否只显示有折扣的内容
     */
    private Boolean discountedOnly = false;

    /**
     * 关键词搜索
     */
    private String keyword;

    /**
     * 作者ID
     */
    @Min(value = 1, message = "作者ID必须大于0")
    private Long authorId;

    /**
     * 排序方式：
     * price_asc - 价格升序
     * price_desc - 价格降序  
     * sales_desc - 销量降序
     * revenue_desc - 收入降序
     * create_time_desc - 创建时间降序
     * hot - 热度排序（综合销量、评分等）
     */
    private String sortBy = "create_time_desc";

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

    /**
     * 是否包含用户购买状态信息
     */
    private Boolean includePurchaseStatus = false;
}