package com.gig.collide.api.order;

import com.gig.collide.api.order.model.TradeOrderVO;
import com.gig.collide.api.order.request.*;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderPayRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;

/**
 * @author GIG
 */
public interface OrderFacadeService {


    public void setPool(int core,int max);

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    public OrderResponse create(OrderCreateRequest request);

    /**
     * 取消订单
     *
     * @param request
     * @return
     */
    public OrderResponse cancel(OrderCancelRequest request);

    /**
     * 订单超时
     *
     * @param request
     * @return
     */
    public OrderResponse timeout(OrderTimeoutRequest request);

    /**
     * 订单确认
     *
     * @param request
     * @return
     */
    public OrderResponse confirm(OrderConfirmRequest request);

    /**
     * 创建并确认订单
     * @param request
     * @return
     */
    public OrderResponse createAndConfirm(OrderCreateAndConfirmRequest request);

    /**
     * 订单支付成功
     *
     * @param request
     * @return
     */
    public OrderResponse paySuccess(OrderPayRequest request);

    /**
     * 订单详情
     *
     * @param orderId
     * @return
     */
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId);

    /**
     * 订单详情
     *
     * @param orderId
     * @param userId
     * @return
     */
    public SingleResponse<TradeOrderVO> getTradeOrder(String orderId, String userId);

    /**
     * 订单分页查询
     *
     * @param request
     * @return
     */
    public PageResponse<TradeOrderVO> pageQuery(OrderPageQueryRequest request);
}
