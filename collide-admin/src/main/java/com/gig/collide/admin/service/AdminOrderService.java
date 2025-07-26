package com.gig.collide.admin.service;

import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.base.response.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 订单服务接口
 * 
 * 通过Dubbo调用微服务实现订单管理功能
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface AdminOrderService {
    
    /**
     * 分页查询订单
     */
    PageResponse<OrderInfo> pageQueryOrders(Map<String, Object> queryParams);
    
    /**
     * 获取订单详情
     */
    OrderInfo getOrderDetail(String orderNo);
    
    /**
     * 取消订单
     */
    void cancelOrder(String orderNo, String reason);
    
    /**
     * 订单退款
     */
    void refundOrder(String orderNo, String refundAmount, String reason);
    
    /**
     * 批量处理订单
     */
    Map<String, Object> batchProcessOrders(String action, List<String> orderNos, String reason);
    
    /**
     * 获取订单统计
     */
    Map<String, Object> getOrderStatistics(String startDate, String endDate, String dimension);
    
    /**
     * 获取订单内容关联
     */
    List<Map<String, Object>> getOrderContents(String orderNo);
    
    /**
     * 管理订单权限
     */
    void manageOrderPermissions(String orderNo, String action, String reason);
    
    /**
     * 导出订单数据
     */
    Map<String, String> exportOrders(String status, String startDate, String endDate, String format);
} 