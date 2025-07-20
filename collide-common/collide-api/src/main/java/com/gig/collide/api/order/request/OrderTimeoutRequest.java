package com.gig.collide.api.order.request;

import com.gig.collide.api.order.constant.TradeOrderEvent;
import lombok.Getter;
import lombok.Setter;

/**
 * @author GIG
 */
@Getter
@Setter
public class OrderTimeoutRequest extends BaseOrderUpdateRequest {

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.TIME_OUT;
    }
}
