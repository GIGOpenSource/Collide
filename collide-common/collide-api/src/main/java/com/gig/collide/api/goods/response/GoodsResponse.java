package com.gig.collide.api.goods.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品统一响应对象 - 简洁版
 * 基于goods-simple.sql的字段结构，包含所有冗余信息
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
public class GoodsResponse implements Serializable {

    /**
     * 商品ID
     */
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
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
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
    private Long sellerId;

    /**
     * 商家名称（冗余）
     */
    private String sellerName;

    // =================== 状态和统计 ===================

    /**
     * 状态：active、inactive、sold_out
     */
    private String status;

    /**
     * 销量（冗余统计）
     */
    private Long salesCount;

    /**
     * 浏览量（冗余统计）
     */
    private Long viewCount;

    // =================== 时间信息 ===================

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 计算属性 ===================

    /**
     * 是否有折扣
     */
    @JsonIgnore
    public boolean hasDiscount() {
        return originalPrice != null && price != null && 
               originalPrice.compareTo(price) > 0;
    }

    /**
     * 折扣金额
     */
    @JsonIgnore
    public BigDecimal getDiscountAmount() {
        if (hasDiscount()) {
            return originalPrice.subtract(price);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 是否有库存
     */
    @JsonIgnore
    public boolean hasStock() {
        return stock != null && stock > 0;
    }

    /**
     * 是否为活跃状态
     */
    @JsonIgnore
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 是否售罄
     */
    @JsonIgnore
    public boolean isSoldOut() {
        return "sold_out".equals(status) || (stock != null && stock <= 0);
    }
}