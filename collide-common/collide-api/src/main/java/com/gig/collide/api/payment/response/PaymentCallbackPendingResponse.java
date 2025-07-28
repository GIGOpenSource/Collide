package com.gig.collide.api.payment.response;

import com.gig.collide.api.payment.response.data.PaymentCallbackInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 支付回调待处理响应
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
public class PaymentCallbackPendingResponse extends BaseResponse {

    /**
     * 待处理的回调记录列表
     */
    private List<PaymentCallbackInfo> pendingCallbacks;

    /**
     * 待处理回调数量
     */
    private Integer totalCount;

    /**
     * 最早待处理时间
     */
    private Long earliestPendingTime;

    /**
     * 最长等待时间（毫秒）
     */
    private Long maxWaitingTime;
} 