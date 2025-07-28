package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 订单退款请求
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
public class OrderRefundRequest extends BaseRequest {

    /**
     * 订单号
     */
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空")
    @DecimalMin(value = "0.01", message = "退款金额不能小于0.01")
    private BigDecimal refundAmount;

    /**
     * 退款原因
     */
    @NotBlank(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 退款类型: USER-用户申请, SYSTEM-系统退款, ADMIN-管理员退款
     */
    private String refundType = "USER";

    /**
     * 操作人员ID（管理员退款时必填）
     */
    private Long operatorId;

    /**
     * 退款凭证（图片URL等）
     */
    private String refundCertificate;

    /**
     * 客户端IP地址
     */
    private String clientIp;
} 