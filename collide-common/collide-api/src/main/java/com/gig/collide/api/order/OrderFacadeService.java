package com.gig.collide.api.order;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.request.OrderPayRequest;
import com.gig.collide.api.order.request.OrderCancelRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 订单门面服务接口 - 简洁版
 * 基于order-simple.sql的单表设计，实现核心订单功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface OrderFacadeService {
    
    /**
     * 创建订单
     * 包含商品信息冗余存储，避免连表查询
     * 
     * @param request 订单创建请求
     * @return 创建结果
     */
    Result<OrderResponse> createOrder(OrderCreateRequest request);
    
    /**
     * 支付订单
     * 更新支付状态和支付时间
     * 
     * @param request 支付请求
     * @return 支付结果
     */
    Result<OrderResponse> payOrder(OrderPayRequest request);
    
    /**
     * 取消订单
     * 更新订单状态为cancelled
     * 
     * @param request 取消请求
     * @return 取消结果
     */
    Result<Void> cancelOrder(OrderCancelRequest request);
    
    /**
     * 根据ID查询订单
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    Result<OrderResponse> getOrderById(Long orderId);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Result<OrderResponse> getOrderByOrderNo(String orderNo);
    
    /**
     * 分页查询订单
     * 支持按用户、状态、支付状态等条件查询
     * 
     * @param request 查询请求
     * @return 订单列表
     */
    Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request);
    
    /**
     * 更新订单状态
     * 用于订单流程状态流转
     * 
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新结果
     */
    Result<Void> updateOrderStatus(Long orderId, String status);
    
    /**
     * 删除订单（逻辑删除）
     * 将状态更新为deleted
     * 
     * @param orderId 订单ID
     * @return 删除结果
     */
    Result<Void> deleteOrder(Long orderId);
}