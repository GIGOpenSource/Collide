package com.gig.collide.api.order.request;

import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 激活内容访问权限请求
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
public class OrderContentActivateRequest extends BaseRequest {

    /**
     * 订单内容关联ID
     */
    @NotNull(message = "订单内容关联ID不能为空")
    private Long associationId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 激活原因
     */
    private String activateReason;

    /**
     * 操作人员ID
     */
    private Long operatorId;

    /**
     * 客户端IP地址
     */
    private String clientIp;
} 