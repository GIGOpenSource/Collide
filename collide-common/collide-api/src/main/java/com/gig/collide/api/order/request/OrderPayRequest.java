package com.gig.collide.api.order.request;

import com.gig.collide.api.order.constant.TradeOrderEvent;
import com.gig.collide.api.pay.constant.PayChannel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author GIG
 */
@Getter
@Setter
public class OrderPayRequest extends BaseOrderUpdateRequest {

    /**
     * 支付方式
     */
    private PayChannel payChannel;

    /**
     * 支付流水号
     */
    private String payStreamId;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    @Override
    public TradeOrderEvent getOrderEvent() {
        return TradeOrderEvent.PAY;
    }
}
