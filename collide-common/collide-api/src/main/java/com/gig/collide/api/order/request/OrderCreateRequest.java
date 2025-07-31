package com.gig.collide.api.order.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建订单请求 - 简洁版
 * 基于order-simple.sql的无连表设计，包含商品信息冗余
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
public class OrderCreateRequest implements Serializable {

    /**
     * 用户ID - 必填
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户昵称 - 冗余字段，避免连表查询
     */
    private String userNickname;

    // =================== 商品信息（冗余字段） ===================
    
    /**
     * 商品ID - 必填
     */
    @NotNull(message = "商品ID不能为空")
    private Long goodsId;

    /**
     * 商品名称 - 冗余字段
     */
    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    /**
     * 商品单价 - 冗余字段
     */
    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.01", message = "商品单价必须大于0")
    private BigDecimal goodsPrice;

    /**
     * 商品封面 - 冗余字段
     */
    private String goodsCover;

    /**
     * 商品分类名称 - 冗余字段（用于金币类商品判断）
     */
    private String goodsCategoryName;

    /**
     * 商品金币数量 - 冗余字段（仅金币类商品有效）
     */
    private BigDecimal goodsCoinAmount;

    // =================== 订单金额信息 ===================
    
    /**
     * 购买数量
     */
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;

    /**
     * 订单总金额 = 商品单价 * 数量
     */
    @NotNull(message = "订单总金额不能为空")
    @DecimalMin(value = "0.01", message = "订单总金额必须大于0")
    private BigDecimal totalAmount;

    /**
     * 优惠金额 - 可选，默认0
     */
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * 实付金额 = 总金额 - 优惠金额
     */
    @NotNull(message = "实付金额不能为空")
    @DecimalMin(value = "0.01", message = "实付金额必须大于0")
    private BigDecimal finalAmount;

    // =================== 可选字段 ===================
    
    /**
     * 支付方式：alipay、wechat、balance
     * 创建时可预设，支付时确认
     */
    private String payMethod;
} 