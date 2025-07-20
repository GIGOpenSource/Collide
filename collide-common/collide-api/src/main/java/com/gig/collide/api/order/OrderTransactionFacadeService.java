package com.gig.collide.api.order;

import com.gig.collide.api.order.request.OrderConfirmRequest;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderDiscardRequest;
import com.gig.collide.api.order.response.OrderResponse;

/**
 * 订单事务门面服务
 *
 * @author GIG
 */
public interface OrderTransactionFacadeService {

    /**
     * 创建订单
     *
     * @param orderCreateRequest
     * @return
     */
    public OrderResponse tryOrder(OrderCreateRequest orderCreateRequest);

    /**
     * 确认订单
     *
     * @param orderConfirmRequest
     * @return
     */
    public OrderResponse confirmOrder(OrderConfirmRequest orderConfirmRequest);

    /**
     * 撤销订单
     *
     * @param orderDiscardRequest
     * @return
     */
    public OrderResponse cancelOrder(OrderDiscardRequest orderDiscardRequest);
}
