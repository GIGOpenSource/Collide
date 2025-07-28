package com.gig.collide.api.payment.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付响应 - 简洁版
 * 基于t_payment表结构
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class PaymentResponse {

    private Long id;

    private String paymentNo;

    private Long orderId;

    private String orderNo;

    private Long userId;

    private String userNickname;

    private BigDecimal amount;

    private String payMethod;

    private String payChannel;

    private String thirdPartyNo;

    private String status;

    private LocalDateTime payTime;

    private LocalDateTime notifyTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 支付二维码或链接（用于前端展示）
     */
    private String payUrl;

    /**
     * 支付状态描述
     */
    private String statusDesc;

    /**
     * 是否可以取消
     */
    private Boolean cancellable;
} 