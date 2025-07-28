package com.gig.collide.api.payment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付验证响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class PaymentVerifyResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 签名验证结果
     */
    private Boolean signatureValid;

    /**
     * 验证失败原因
     */
    private String failureReason;

    /**
     * 验证时间戳
     */
    private String verifyTime;

    /**
     * 数据完整性检查结果
     */
    private Boolean dataIntegrityValid;

    /**
     * 验证详情信息
     */
    private String verifyDetails;
} 