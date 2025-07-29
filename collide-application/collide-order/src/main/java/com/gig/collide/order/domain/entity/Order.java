package com.gig.collide.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 简洁版
 * 基于order-simple.sql的t_order表结构
 * 采用无连表设计，包含商品信息冗余字段
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
@TableName("t_order")
public class Order {

    /**
     * 订单ID - 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号 - 唯一索引
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户昵称（冗余字段，避免连表查询）
     */
    @TableField("user_nickname")
    private String userNickname;

    // =================== 商品信息（冗余字段，避免连表） ===================
    
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
     * 商品单价（冗余）
     */
    @TableField("goods_price")
    private BigDecimal goodsPrice;

    /**
     * 商品封面（冗余）
     */
    @TableField("goods_cover")
    private String goodsCover;

    // =================== 订单金额信息 ===================
    
    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    @TableField("final_amount")
    private BigDecimal finalAmount;

    // =================== 订单状态信息 ===================
    
    /**
     * 订单状态：pending、paid、shipped、completed、cancelled
     */
    @TableField("status")
    private String status;

    /**
     * 支付状态：unpaid、paid、refunded
     */
    @TableField("pay_status")
    private String payStatus;

    /**
     * 支付方式：alipay、wechat、balance
     */
    @TableField("pay_method")
    private String payMethod;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    // =================== 时间字段 ===================
    
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
     * 判断订单是否可以支付
     */
    public boolean canPay() {
        return "pending".equals(status) && "unpaid".equals(payStatus);
    }

    /**
     * 判断订单是否可以取消
     */
    public boolean canCancel() {
        return "pending".equals(status) || ("paid".equals(status) && "shipped".equals(status));
    }

    /**
     * 判断订单是否已完成
     */
    public boolean isCompleted() {
        return "completed".equals(status);
    }

    /**
     * 判断订单是否已取消
     */
    public boolean isCancelled() {
        return "cancelled".equals(status);
    }
}