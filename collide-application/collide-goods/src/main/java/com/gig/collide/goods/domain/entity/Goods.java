package com.gig.collide.goods.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 - 简洁版
 * 基于goods-simple.sql的t_goods表结构
 * 采用无连表设计，包含分类和商家信息冗余字段
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
@TableName("t_goods")
public class Goods {

    /**
     * 商品ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 分类名称（冗余）
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 商品价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 商品封面图
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 商品图片，JSON数组格式
     */
    @TableField("images")
    private String images;

    // =================== 商家信息（冗余字段，避免连表） ===================

    /**
     * 商家ID
     */
    @TableField("seller_id")
    private Long sellerId;

    /**
     * 商家名称（冗余）
     */
    @TableField("seller_name")
    private String sellerName;

    // =================== 状态和统计 ===================

    /**
     * 状态：active、inactive、sold_out
     */
    @TableField("status")
    private String status;

    /**
     * 销量（冗余统计）
     */
    @TableField("sales_count")
    private Long salesCount;

    /**
     * 浏览量（冗余统计）
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 创建时间 - 自动填充
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间 - 自动填充
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 判断是否为活跃状态
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 判断是否为非活跃状态
     */
    public boolean isInactive() {
        return "inactive".equals(status);
    }

    /**
     * 判断是否售罄
     */
    public boolean isSoldOut() {
        return "sold_out".equals(status) || (stock != null && stock <= 0);
    }

    /**
     * 判断是否有库存
     */
    public boolean hasStock() {
        return stock != null && stock > 0;
    }

    /**
     * 判断是否有折扣
     */
    public boolean hasDiscount() {
        return originalPrice != null && price != null && 
               originalPrice.compareTo(price) > 0;
    }

    /**
     * 获取折扣金额
     */
    public BigDecimal getDiscountAmount() {
        if (hasDiscount()) {
            return originalPrice.subtract(price);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 激活商品
     */
    public void activate() {
        this.status = "active";
    }

    /**
     * 停用商品
     */
    public void deactivate() {
        this.status = "inactive";
    }

    /**
     * 设置为售罄状态
     */
    public void setSoldOut() {
        this.status = "sold_out";
    }

    /**
     * 增加库存
     */
    public void addStock(Integer amount) {
        if (amount > 0) {
            this.stock = (this.stock == null ? 0 : this.stock) + amount;
            // 如果之前是售罄状态且现在有库存，恢复为活跃状态
            if ("sold_out".equals(this.status) && this.stock > 0) {
                this.status = "active";
            }
        }
    }

    /**
     * 减少库存
     */
    public boolean reduceStock(Integer amount) {
        if (amount <= 0 || this.stock == null || this.stock < amount) {
            return false;
        }
        this.stock -= amount;
        // 如果库存为0，设置为售罄状态
        if (this.stock <= 0) {
            this.status = "sold_out";
        }
        return true;
    }

    /**
     * 增加销量
     */
    public void addSalesCount(Integer amount) {
        if (amount > 0) {
            this.salesCount = (this.salesCount == null ? 0L : this.salesCount) + amount;
        }
    }

    /**
     * 增加浏览量
     */
    public void increaseViewCount() {
        this.viewCount = (this.viewCount == null ? 0L : this.viewCount) + 1;
    }
}