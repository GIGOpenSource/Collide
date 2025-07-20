package com.gig.collide.api.pay.service;

import com.gig.collide.api.pay.model.PayOrderVO;
import com.gig.collide.api.pay.request.PayCreateRequest;
import com.gig.collide.api.pay.request.PayQueryRequest;
import com.gig.collide.api.pay.response.PayCreateResponse;
import com.gig.collide.api.pay.response.PayQueryResponse;
import com.gig.collide.base.response.MultiResponse;
import com.gig.collide.base.response.SingleResponse;

/**
 * @author GIG
 */
public interface PayFacadeService {

    /**
     * 生成支付链接
     *
     * @param payCreateRequest
     * @return
     */
    public PayCreateResponse generatePayUrl(PayCreateRequest payCreateRequest);

    /**
     * 查询支付订单
     *
     * @param payQueryRequest
     * @return
     */
    public MultiResponse<PayOrderVO> queryPayOrders(PayQueryRequest payQueryRequest);

    /**
     * 查询支付订单
     *
     * @param payOrderId
     * @return
     */
    public SingleResponse<PayOrderVO> queryPayOrder(String payOrderId);

    /**
     * 查询支付订单
     *
     * @param payOrderId
     * @param payerId
     * @return
     */
    public SingleResponse<PayOrderVO> queryPayOrder(String payOrderId, String payerId);


}
