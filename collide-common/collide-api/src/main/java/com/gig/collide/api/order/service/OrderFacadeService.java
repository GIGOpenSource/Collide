package com.gig.collide.api.order.service;

import com.gig.collide.api.order.response.data.OrderInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;

import java.util.List;
import java.util.Map;

/**
 * 订单服务 Facade 接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface OrderFacadeService {

    /**
     * 分页查询订单
     */
    PageResponse<OrderInfo> pageQueryOrders(Map<String, Object> queryParams);

    /**
     * 获取订单详情
     */
    SingleResponse<OrderInfo> getOrderDetail(String orderNo);

    /**
     * 取消订单
     */
    SingleResponse<Void> cancelOrder(String orderNo, String reason);

    /**
     * 订单退款
     */
    SingleResponse<Void> refundOrder(String orderNo, String refundAmount, String reason);

    /**
     * 批量处理订单
     */
    SingleResponse<Object> batchProcessOrders(String action, List<String> orderNos, String reason);

    /**
     * 获取订单统计
     */
    SingleResponse<Object> getOrderStatistics(String startDate, String endDate, String dimension);

    /**
     * 获取订单内容关联
     */
    SingleResponse<List<Map<String, Object>>> getOrderContents(String orderNo);

    /**
     * 管理订单权限
     */
    SingleResponse<Void> manageOrderPermissions(String orderNo, String action, String reason);

    /**
     * 导出订单数据
     */
    SingleResponse<Map<String, String>> exportOrders(String status, String startDate, String endDate, String format);
} 