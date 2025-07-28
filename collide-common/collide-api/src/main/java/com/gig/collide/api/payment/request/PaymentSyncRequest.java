package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 支付状态同步请求
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
public class PaymentSyncRequest extends BaseRequest {

    /**
     * 订单号（必填）
     */
    @NotBlank(message = "订单号不能为空")
    @Size(max = 64, message = "订单号长度不能超过64个字符")
    private String orderNo;

    /**
     * 内部交易流水号（可选）
     */
    @Size(max = 128, message = "内部交易流水号长度不能超过128个字符")
    private String internalTransactionNo;

    /**
     * 第三方交易流水号（可选）
     */
    @Size(max = 128, message = "第三方交易流水号长度不能超过128个字符")
    private String transactionNo;

    /**
     * 是否强制同步（可选，默认false）
     */
    private Boolean forceSync = false;

    /**
     * 操作员ID（可选）
     */
    private Long operatorId;

    /**
     * 备注信息（可选）
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
} 