package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 支付回调标记请求
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
public class PaymentCallbackMarkRequest extends BaseRequest {

    /**
     * 回调记录ID
     */
    @NotNull(message = "回调记录ID不能为空")
    private Long callbackId;

    /**
     * 内部交易流水号
     */
    @NotBlank(message = "内部交易流水号不能为空")
    private String internalTransactionNo;

    /**
     * 处理结果
     */
    @NotBlank(message = "处理结果不能为空")
    private String processResult;

    /**
     * 处理备注
     */
    private String processRemark;

    /**
     * 处理人员ID
     */
    private Long processUserId;
} 