package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付回调验证响应
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
public class PaymentCallbackVerifyResponse extends BaseResponse {

    /**
     * 验证是否通过
     */
    private Boolean verified;

    /**
     * 验证结果描述
     */
    private String verifyMessage;

    /**
     * 计算的签名值
     */
    private String calculatedSignature;

    /**
     * 接收的签名值
     */
    private String receivedSignature;

    /**
     * 验证时间戳
     */
    private Long verifyTimestamp;
} 