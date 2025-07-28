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
 * 基础商品信息传输对象
 * 供前端展示使用，不包含敏感信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class BasicGoodsInfo implements Serializable {

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
     * 是否有库存
     */
    private Boolean inStock;

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

    // ===================== 便捷方法 =====================

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
        return status != null && status.isPurchasable() && Boolean.TRUE.equals(inStock);
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

    /**
     * 获取价格显示文本
     */
    public String getPriceText() {
        if (price == null) {
            return "价格未设置";
        }
        return "￥" + price;
    }
} 