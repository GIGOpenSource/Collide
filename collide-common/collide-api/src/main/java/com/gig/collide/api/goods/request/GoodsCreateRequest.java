package com.gig.collide.api.goods.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品创建请求 - 简洁版
 * 基于goods-simple.sql的无连表设计，包含分类和商家信息冗余
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
public class GoodsCreateRequest implements Serializable {

    /**
     * 商品名称
     */
    @NotBlank(message = "商品名称不能为空")
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
    @NotNull(message = "商品价格不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "商品价格必须大于0")
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
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
     * 商家ID
     */
    @NotNull(message = "商家ID不能为空")
    private Long sellerId;

    /**
     * 商家名称（冗余）
     */
    private String sellerName;

    // =================== 状态和统计 ===================

    /**
     * 状态：active、inactive、sold_out
     */
    private String status = "active";

    /**
     * 销量（初始为0）
     */
    private Long salesCount = 0L;

    /**
     * 浏览量（初始为0）
     */
    private Long viewCount = 0L;
} 