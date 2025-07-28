package com.gig.collide.api.payment.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * 支付取消请求
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
public class PaymentCancelRequest extends BaseRequest {

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
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 取消原因（必填）
     */
    @NotBlank(message = "取消原因不能为空")
    @Size(max = 255, message = "取消原因长度不能超过255个字符")
    private String cancelReason;

    /**
     * 操作员ID（可选）
     */
    private Long operatorId;

    /**
     * 客户端IP地址（可选）
     */
    @Size(max = 45, message = "IP地址长度不能超过45个字符")
    private String clientIp;

    /**
     * 备注信息（可选）
     */
    @Size(max = 500, message = "备注信息长度不能超过500个字符")
    private String remark;
} 