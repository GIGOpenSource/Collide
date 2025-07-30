package com.gig.collide.api.payment.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付回调请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class PaymentCallbackRequest implements Serializable {

    @NotBlank(message = "支付单号不能为空")
    private String paymentNo;

    /**
     * 第三方支付单号
     */
    private String thirdPartyNo;

    @NotBlank(message = "支付状态不能为空")
    private String status;

    /**
     * 支付完成时间
     */
    private LocalDateTime payTime;

    /**
     * 回调通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 原始回调数据
     */
    private Map<String, Object> rawData;

    /**
     * 签名
     */
    private String sign;

    /**
     * 备注信息
     */
    private String remark;
} 