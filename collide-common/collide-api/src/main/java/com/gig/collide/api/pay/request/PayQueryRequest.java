package com.gig.collide.api.pay.request;

import com.gig.collide.api.pay.constant.PayOrderState;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class PayQueryRequest extends BaseRequest {

    @NotNull(message = "payQueryCondition is null")
    private PayQueryCondition payQueryCondition;

    private PayOrderState payOrderState;

    @NotNull(message = "payerId is null")
    private String payerId;

}
