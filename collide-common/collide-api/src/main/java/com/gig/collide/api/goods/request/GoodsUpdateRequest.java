package com.gig.collide.api.goods.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 商品更新请求 - 简洁版
 * 支持部分字段更新，自动更新冗余字段
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoodsUpdateRequest {

    /**
     * 商品ID
     */
    @NotNull(message = "商品ID不能为空")
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    private String categoryName;

    /**
     * 商品价格
     */
    @DecimalMin(value = "0.0", inclusive = false, message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stock;

    /**
     * 商品封面图
     */
    private String coverUrl;

    /**
     * 商品图片，JSON数组格式
     */
    private String images;

    // =================== 商家信息（冗余字段） ===================

    /**
     * 商家名称（冗余）
     * 注意：商家ID不允许修改
     */
    private String sellerName;

    // =================== 状态和统计 ===================

    /**
     * 状态：active、inactive、sold_out
     */
    private String status;

    /**
     * 更新原因
     */
    private String updateReason;
} 