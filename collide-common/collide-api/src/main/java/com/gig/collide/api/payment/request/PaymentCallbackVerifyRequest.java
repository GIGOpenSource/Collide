package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * 支付回调验证请求
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class PaymentCallbackVerifyRequest extends BaseRequest {

    /**
     * 回调数据内容
     */
    @NotBlank(message = "回调数据内容不能为空")
    private String callbackData;

    /**
     * 接收的签名
     */
    @NotBlank(message = "签名不能为空")
    private String signature;

    /**
     * 签名算法
     */
    @NotBlank(message = "签名算法不能为空")
    private String signatureAlgorithm;

    /**
     * 支付渠道
     */
    @NotBlank(message = "支付渠道不能为空")
    private String paymentChannel;

    /**
     * 回调参数
     */
    private Map<String, String> callbackParams;
} 