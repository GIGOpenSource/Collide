package com.gig.collide.api.payment.request;

import lombok.Data;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付创建请求 - 简洁版
 * 基于t_payment表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class PaymentCreateRequest implements Serializable {

    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 订单号（冗余字段）
     */
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 用户昵称（冗余字段）
     */
    private String userNickname;

    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.01", message = "支付金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "^(alipay|wechat|balance)$", message = "支付方式只能是alipay、wechat或balance")
    private String payMethod;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 回调通知地址
     */
    private String notifyUrl;

    /**
     * 支付成功跳转地址
     */
    private String returnUrl;

    /**
     * 商品描述
     */
    private String subject;

    /**
     * 备注信息
     */
    private String remark;
} 