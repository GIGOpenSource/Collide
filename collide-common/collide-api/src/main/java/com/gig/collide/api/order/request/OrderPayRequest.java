package com.gig.collide.api.order.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 订单支付请求 - 简洁版
 * 更新订单支付状态和支付信息
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
public class OrderPayRequest {

    /**
     * 订单ID - 必填
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单号 - 可用于二次校验
     */
    private String orderNo;

    /**
     * 支付方式：alipay、wechat、balance
     */
    @NotBlank(message = "支付方式不能为空")
    private String payMethod;

    /**
     * 实际支付金额 - 用于金额校验
     */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payAmount;

    /**
     * 第三方支付交易号
     * 如支付宝交易号、微信交易号等
     */
    private String thirdPartyTradeNo;

    /**
     * 支付备注
     */
    private String payRemark;
} 