package com.gig.collide.api.order.service;

import com.gig.collide.api.order.request.*;
import com.gig.collide.api.order.response.*;
import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.base.response.BaseResponse;
import com.gig.collide.base.response.PageResponse;

/**
 * 订单门面服务接口
 * 提供订单核心业务功能
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
public interface OrderFacadeService {

    /**
     * 创建订单
     * 
     * @param createRequest 创建订单请求
     * @return 创建订单响应
     */
    OrderCreateResponse createOrder(OrderCreateRequest createRequest);

    /**
     * 查询订单
     * 
     * @param queryRequest 查询订单请求
     * @return 查询订单响应
     */
    OrderQueryResponse queryOrder(OrderQueryRequest queryRequest);

    /**
     * 分页查询订单
     * 
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<OrderInfo> pageQueryOrders(OrderQueryRequest queryRequest);

    /**
     * 取消订单
     * 
     * @param cancelRequest 取消订单请求
     * @return 取消订单响应
     */
    OrderCancelResponse cancelOrder(OrderCancelRequest cancelRequest);

    /**
     * 支付订单
     * 
     * @param payRequest 支付订单请求
     * @return 支付订单响应
     */
    OrderPayResponse payOrder(OrderPayRequest payRequest);

    /**
     * 申请退款
     * 
     * @param refundRequest 退款申请请求
     * @return 退款申请响应
     */
    OrderRefundResponse refundOrder(OrderRefundRequest refundRequest);

    /**
     * 订单状态同步
     * 
     * @param syncRequest 订单同步请求
     * @return 订单同步响应
     */
    OrderSyncResponse syncOrderStatus(OrderSyncRequest syncRequest);

    /**
     * 获取订单详情
     * 
     * @param orderNo 订单号
     * @return 订单详情响应
     */
    OrderDetailResponse getOrderDetail(String orderNo);

    /**
     * 检查订单是否存在
     * 
     * @param orderNo 订单号
     * @return 是否存在
     */
    Boolean checkOrderExists(String orderNo);

    /**
     * 生成订单号
     * 
     * @param userId 用户ID
     * @param orderType 订单类型
     * @return 订单号
     */
    String generateOrderNo(Long userId, String orderType);

    /**
     * 获取用户订单统计
     * 
     * @param userId 用户ID
     * @return 订单统计响应
     */
    OrderStatisticsResponse getUserOrderStatistics(Long userId);
} 