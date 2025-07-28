package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单取消请求
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
public class OrderCancelRequest extends BaseRequest {

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
     * 取消原因
     */
    @NotBlank(message = "取消原因不能为空")
    private String cancelReason;

    /**
     * 取消类型: USER-用户取消, SYSTEM-系统取消, ADMIN-管理员取消
     */
    private String cancelType = "USER";

    /**
     * 操作人员ID（管理员取消时必填）
     */
    private Long operatorId;

    /**
     * 客户端IP地址
     */
    private String clientIp;
} 