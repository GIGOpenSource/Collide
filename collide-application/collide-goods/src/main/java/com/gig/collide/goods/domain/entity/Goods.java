package com.gig.collide.goods.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 支持金币类和订阅类商品
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("goods")
public class Goods {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 商品类型：COIN-金币类，SUBSCRIPTION-订阅类
     */
    @TableField("type")
    private String type;

    /**
     * 商品状态：DRAFT-草稿，ON_SALE-销售中，OFF_SALE-下架，SOLD_OUT-售罄，DISABLED-禁用
     */
    @TableField("status")
    private String status;

    /**
     * 商品价格（元）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 库存数量（金币类商品使用，-1表示无限库存）
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 已售数量
     */
    @TableField("sold_count")
    private Integer soldCount;

    /**
     * 订阅周期天数（订阅类商品使用）
     */
    @TableField("subscription_days")
    private Integer subscriptionDays;

    /**
     * 金币数量（金币类商品购买后获得的金币数）
     */
    @TableField("coin_amount")
    private Integer coinAmount;

    /**
     * 是否推荐
     */
    @TableField("recommended")
    private Boolean recommended;

    /**
     * 是否热门
     */
    @TableField("hot")
    private Boolean hot;

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
     * 创建者ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 是否为金币类商品
     */
    public boolean isCoinType() {
        return "COIN".equals(this.type);
    }

    /**
     * 是否为订阅类商品
     */
    public boolean isSubscriptionType() {
        return "SUBSCRIPTION".equals(this.type);
    }

    /**
     * 是否可购买
     */
    public boolean canPurchase() {
        return "ON_SALE".equals(this.status) && hasStock();
    }

    /**
     * 是否有库存
     */
    public boolean hasStock() {
        return this.stock == null || this.stock == -1 || this.stock > 0;
    }
} 