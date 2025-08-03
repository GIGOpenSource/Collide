package com.gig.collide.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 支持四种商品类型和双支付模式
 * 基于order-simple.sql的单表设计，支持无连表查询
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_order")
public class Order {

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    @TableField("user_nickname")
    private String userNickname;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    private Long goodsId;

    /**
     * 商品名称（冗余）
     */
    @TableField("goods_name")
    private String goodsName;

    /**
     * 商品类型：coin、goods、subscription、content
     */
    @TableField("goods_type")
    private GoodsType goodsType;

    /**
     * 商品封面（冗余）
     */
    @TableField("goods_cover")
    private String goodsCover;

    /**
     * 商品分类名称（冗余）
     */
    @TableField("goods_category_name")
    private String goodsCategoryName;

    /**
     * 金币数量（金币类商品：购买后获得金币数）
     */
    @TableField("coin_amount")
    private Long coinAmount;

    /**
     * 内容ID（内容类商品）
     */
    @TableField("content_id")
    private Long contentId;

    /**
     * 内容标题（内容类商品冗余）
     */
    @TableField("content_title")
    private String contentTitle;

    /**
     * 订阅时长天数（订阅类商品）
     */
    @TableField("subscription_duration")
    private Integer subscriptionDuration;

    /**
     * 订阅类型（订阅类商品）
     */
    @TableField("subscription_type")
    private String subscriptionType;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 支付模式：cash-现金支付、coin-金币支付
     */
    @TableField("payment_mode")
    private PaymentMode paymentMode;

    /**
     * 现金金额（现金支付时使用）
     */
    @TableField("cash_amount")
    private BigDecimal cashAmount;

    /**
     * 消耗金币数（金币支付时使用）
     */
    @TableField("coin_cost")
    private Long coinCost;

    /**
     * 订单总金额（现金）
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额（现金）
     */
    @TableField("final_amount")
    private BigDecimal finalAmount;

    /**
     * 订单状态：pending、paid、shipped、completed、cancelled
     */
    @TableField("status")
    private OrderStatus status;

    /**
     * 支付状态：unpaid、paid、refunded
     */
    @TableField("pay_status")
    private PayStatus payStatus;

    /**
     * 支付方式：alipay、wechat、balance、coin
     */
    @TableField("pay_method")
    private String payMethod;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

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
         * 是否为虚拟商品
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
     * 支付模式枚举
     */
    public enum PaymentMode {
        /**
         * 现金支付
         */
        CASH("cash", "现金支付"),
        /**
         * 金币支付
         */
        COIN("coin", "金币支付");

        private final String code;
        private final String description;

        PaymentMode(String code, String description) {
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
     * 订单状态枚举
     */
    public enum OrderStatus {
        /**
         * 待支付
         */
        PENDING("pending", "待支付"),
        /**
         * 已支付
         */
        PAID("paid", "已支付"),
        /**
         * 已发货
         */
        SHIPPED("shipped", "已发货"),
        /**
         * 已完成
         */
        COMPLETED("completed", "已完成"),
        /**
         * 已取消
         */
        CANCELLED("cancelled", "已取消");

        private final String code;
        private final String description;

        OrderStatus(String code, String description) {
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
         * 是否为最终状态
         */
        public boolean isFinalStatus() {
            return this == COMPLETED || this == CANCELLED;
        }

        /**
         * 是否可以取消
         */
        public boolean canCancel() {
            return this == PENDING || this == PAID;
        }
    }

    /**
     * 支付状态枚举
     */
    public enum PayStatus {
        /**
         * 未支付
         */
        UNPAID("unpaid", "未支付"),
        /**
         * 已支付
         */
        PAID("paid", "已支付"),
        /**
         * 已退款
         */
        REFUNDED("refunded", "已退款");

        private final String code;
        private final String description;

        PayStatus(String code, String description) {
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
         * 是否可以退款
         */
        public boolean canRefund() {
            return this == PAID;
        }
    }

    /**
     * 获取有效金额（根据支付模式）
     */
    public Object getEffectiveAmount() {
        return paymentMode == PaymentMode.COIN ? coinCost : finalAmount;
    }

    /**
     * 是否为虚拟商品订单
     */
    public boolean isVirtualGoods() {
        return goodsType != null && goodsType.isVirtual();
    }

    /**
     * 是否为金币支付
     */
    public boolean isCoinPayment() {
        return paymentMode == PaymentMode.COIN;
    }

    /**
     * 是否为现金支付
     */
    public boolean isCashPayment() {
        return paymentMode == PaymentMode.CASH;
    }

    /**
     * 是否可以支付
     */
    public boolean canPay() {
        return status == OrderStatus.PENDING && payStatus == PayStatus.UNPAID;
    }

    /**
     * 是否可以取消
     */
    public boolean canCancel() {
        return status != null && status.canCancel();
    }

    /**
     * 是否可以退款
     */
    public boolean canRefund() {
        return payStatus != null && payStatus.canRefund();
    }

    /**
     * 计算总价值（统一单位）
     */
    public String getTotalValue() {
        if (paymentMode == PaymentMode.COIN) {
            return coinCost + "金币";
        } else {
            return finalAmount + "元";
        }
    }

    /**
     * 获取订单类型描述
     */
    public String getOrderTypeDesc() {
        if (goodsType == null) {
            return "未知订单";
        }
        
        String typeDesc = goodsType.getDescription();
        String paymentDesc = paymentMode == PaymentMode.COIN ? "金币支付" : "现金支付";
        
        return typeDesc + "(" + paymentDesc + ")";
    }
}