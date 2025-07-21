package com.gig.collide.order.domain.listener.event;

import com.gig.collide.api.order.request.BaseOrderRequest;
import org.springframework.context.ApplicationEvent;

/**
 * @author Hollis
 */
public class OrderTimeoutEvent extends ApplicationEvent {

    public OrderTimeoutEvent(BaseOrderRequest baseOrderRequest) {
        super(baseOrderRequest);
    }
}
