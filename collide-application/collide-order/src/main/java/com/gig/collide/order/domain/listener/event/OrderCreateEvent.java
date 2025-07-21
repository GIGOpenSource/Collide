package com.gig.collide.order.domain.listener.event;

import com.gig.collide.order.domain.entity.TradeOrder;
import org.springframework.context.ApplicationEvent;

/**
 * @author Hollis
 */
public class OrderCreateEvent extends ApplicationEvent {

    public OrderCreateEvent(TradeOrder tradeOrder) {
        super(tradeOrder);
    }
}
