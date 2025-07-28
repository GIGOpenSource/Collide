package com.gig.collide.api.payment.request;

import com.gig.collide.api.payment.constant.PaymentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Map;

/**
 * 支付验证请求
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
public class PaymentVerifyRequest extends BaseRequest {

    /**
     * 支付方式（必填）
     */
    @NotNull(message = "支付方式不能为空")
    private PaymentTypeEnum payType;

    /**
     * 签名数据（必填）
     */
    @NotBlank(message = "签名数据不能为空")
    private String signature;

    /**
     * 原始数据（必填）
     */
    @NotNull(message = "原始数据不能为空")
    private Map<String, String> rawData;

    /**
     * 签名算法（可选）
     */
    @Size(max = 50, message = "签名算法长度不能超过50个字符")
    private String signType;

    /**
     * 字符编码（可选，默认UTF-8）
     */
    @Size(max = 20, message = "字符编码长度不能超过20个字符")
    private String charset = "UTF-8";
} 