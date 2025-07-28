package com.gig.collide.api.order.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 订单取消响应
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
public class OrderCancelResponse extends BaseResponse {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 取消状态: CANCELLED-已取消, FAILED-取消失败
     */
    private String cancelStatus;

    /**
     * 取消时间戳
     */
    private Long cancelTimestamp;

    /**
     * 取消原因
     */
    private String cancelReason;
} 