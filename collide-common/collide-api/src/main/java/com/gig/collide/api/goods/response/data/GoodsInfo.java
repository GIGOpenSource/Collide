package com.gig.collide.api.goods.response.data;

import com.gig.collide.api.goods.constant.GoodsStatus;
import com.gig.collide.api.goods.constant.GoodsType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品完整信息传输对象
 * 对应数据库goods表的所有字段
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GoodsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 商品类型
     */
    private GoodsType type;

    /**
     * 商品状态
     */
    private GoodsStatus status;

    /**
     * 商品价格（元）
     */
    private BigDecimal price;

    /**
     * 商品图片URL
     */
    private String imageUrl;

    /**
     * 库存数量（-1表示无限库存）
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 订阅周期天数（订阅类商品使用）
     */
    private Integer subscriptionDays;

    /**
     * 金币数量（金币类商品购买后获得的金币数）
     */
    private Integer coinAmount;

    /**
     * 是否推荐
     */
    private Boolean recommended;

    /**
     * 是否热门
     */
    private Boolean hot;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 逻辑删除标识
     */
    private Boolean deleted;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;

    // ===================== 计算属性 =====================

    /**
     * 是否为金币类商品
     */
    public Boolean isCoinType() {
        return type != null && type.isCoinType();
    }

    /**
     * 是否为订阅类商品
     */
    public Boolean isSubscriptionType() {
        return type != null && type.isSubscriptionType();
    }

    /**
     * 是否可购买
     */
    public Boolean isPurchasable() {
        return status != null && status.isPurchasable() && !isOutOfStock();
    }

    /**
     * 是否缺货
     */
    public Boolean isOutOfStock() {
        return stock != null && stock != -1 && stock <= 0;
    }

    /**
     * 是否无限库存
     */
    public Boolean isUnlimitedStock() {
        return stock != null && stock == -1;
    }

    /**
     * 获取库存状态描述
     */
    public String getStockDescription() {
        if (isUnlimitedStock()) {
            return "无限库存";
        } else if (isOutOfStock()) {
            return "库存不足";
        } else {
            return "库存充足（" + stock + "件）";
        }
    }

    /**
     * 获取商品特有属性描述
     */
    public String getSpecialAttributeDescription() {
        if (isCoinType() && coinAmount != null) {
            return "获得" + coinAmount + "金币";
        } else if (isSubscriptionType() && subscriptionDays != null) {
            return subscriptionDays + "天会员";
        }
        return "";
    }
} 