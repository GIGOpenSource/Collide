package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.PaymentSceneEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 获取支付方式请求
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodRequest extends BaseRequest {

    /**
     * 用户ID（可选）
     */
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 支付场景（可选）
     */
    private PaymentSceneEnum payScene;

    /**
     * 支付金额（可选，用于过滤支付方式）
     */
    @DecimalMin(value = "0.01", message = "支付金额必须大于0.01元")
    private BigDecimal payAmount;

    /**
     * 商户ID（可选）
     */
    private Long merchantId;

    /**
     * 是否仅返回可用的支付方式（可选，默认true）
     */
    private Boolean onlyAvailable = true;

    /**
     * 是否包含测试支付方式（可选，默认false）
     */
    private Boolean includeTest = false;
} 