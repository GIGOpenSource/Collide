package com.gig.collide.api.order.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单统一响应对象 - 简洁版
 * 基于order-simple.sql的字段结构，包含所有冗余信息
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
public class OrderResponse {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称（冗余）
     */
    private String userNickname;

    // =================== 商品信息（冗余字段） ===================
    
    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称（冗余）
     */
    private String goodsName;

    /**
     * 商品单价（冗余）
     */
    private BigDecimal goodsPrice;

    /**
     * 商品封面（冗余）
     */
    private String goodsCover;

    // =================== 订单金额信息 ===================
    
    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal finalAmount;

    // =================== 订单状态信息 ===================
    
    /**
     * 订单状态：pending、paid、shipped、completed、cancelled
     */
    private String status;

    /**
     * 支付状态：unpaid、paid、refunded
     */
    private String payStatus;

    /**
     * 支付方式：alipay、wechat、balance
     */
    private String payMethod;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    // =================== 时间信息 ===================
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}