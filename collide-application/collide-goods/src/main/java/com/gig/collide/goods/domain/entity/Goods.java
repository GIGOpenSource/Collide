package com.gig.collide.goods.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 - 支持四种商品类型
 * 基于goods-simple.sql的单表设计，支持无连表查询
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_goods")
public class Goods {

    /**
     * 商品ID
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
     * 商品类型：coin-金币、goods-商品、subscription-订阅、content-内容
     */
    @TableField("goods_type")
    private GoodsType goodsType;

    /**
     * 现金价格（内容类型为0）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 金币价格（内容类型专用，其他类型为0）
     */
    @TableField("coin_price")
    private Long coinPrice;

    /**
     * 金币数量（仅金币类商品：购买后获得的金币数）
     */
    @TableField("coin_amount")
    private Long coinAmount;

    /**
     * 关联内容ID（仅内容类型有效）
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容标题（冗余，仅内容类型）
     */
    @TableField("content_title")
    private String contentTitle;

    /**
     * 订阅时长（天数，仅订阅类型有效）
     */
    @TableField("subscription_duration")
    private Integer subscriptionDuration;

    /**
     * 订阅类型（VIP、PREMIUM等，仅订阅类型有效）
     */
    @TableField("subscription_type")
    private String subscriptionType;

    /**
     * 库存数量（-1表示无限库存，适用于虚拟商品）
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

    /**
     * 状态：active、inactive、sold_out
     */
    @TableField("status")
    private GoodsStatus status;

    /**
     * 销量（冗余统计）
     */
    @TableField("sales_count")
    private Long salesCount;

    /**
     * 查看数（冗余统计）
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 商品类型枚举
     */
    public enum GoodsType {
        /**
         * 金币充值包
         */
        COIN("coin", "金币充值包"),
        /**
         * 实体商品
         */
        GOODS("goods", "实体商品"),
        /**
         * 订阅服务
         */
        SUBSCRIPTION("subscription", "订阅服务"),
        /**
         * 付费内容
         */
        CONTENT("content", "付费内容");

        private final String code;
        private final String description;

        GoodsType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 是否为虚拟商品（无限库存）
         */
        public boolean isVirtual() {
            return this == COIN || this == SUBSCRIPTION || this == CONTENT;
        }

        /**
         * 是否只能现金支付
         */
        public boolean isCashOnly() {
            return this != CONTENT;
        }

        /**
         * 是否只能金币支付
         */
        public boolean isCoinOnly() {
            return this == CONTENT;
        }
    }

    /**
     * 商品状态枚举
     */
    public enum GoodsStatus {
        /**
         * 正常销售
         */
        ACTIVE("active", "正常销售"),
        /**
         * 下架
         */
        INACTIVE("inactive", "下架"),
        /**
         * 售罄
         */
        SOLD_OUT("sold_out", "售罄");

        private final String code;
        private final String description;

        GoodsStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 获取有效价格（根据商品类型返回对应价格）
     */
    public Object getEffectivePrice() {
        return goodsType == GoodsType.CONTENT ? coinPrice : price;
    }

    /**
     * 是否为虚拟商品
     */
    public boolean isVirtual() {
        return goodsType != null && goodsType.isVirtual();
    }

    /**
     * 是否库存充足
     */
    public boolean hasStock(int quantity) {
        return stock == -1 || stock >= quantity;
    }

    /**
     * 扣减库存
     */
    public void reduceStock(int quantity) {
        if (stock > 0) {
            this.stock = Math.max(0, this.stock - quantity);
        }
    }

    /**
     * 增加销量
     */
    public void increaseSales(long count) {
        this.salesCount = (this.salesCount == null ? 0 : this.salesCount) + count;
    }

    /**
     * 增加浏览量
     */
    public void increaseViews(long count) {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + count;
    }
}