package com.gig.collide.api.order;

import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.request.OrderQueryRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单模块对外服务接口
 * 提供统一的订单管理功能入口
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
public interface OrderFacadeService {

    // =================== 订单创建和管理 ===================

    /**
     * 创建订单
     *
     * @param request 订单创建请求
     * @return 创建的订单信息
     */
    Result<OrderResponse> createOrder(OrderCreateRequest request);

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单信息
     */
    Result<OrderResponse> getOrderById(Long id);

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    Result<OrderResponse> getOrderByOrderNo(String orderNo);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     * @return 取消结果（仅返回状态）
     */
    Result<Void> cancelOrder(Long orderId, String reason);

    /**
     * 批量取消订单
     *
     * @param orderIds 订单ID列表
     * @param reason   取消原因
     * @return 取消结果（仅返回状态）
     */
    Result<Void> batchCancelOrders(List<Long> orderIds, String reason);

    // =================== 订单查询 ===================

    /**
     * 分页查询订单
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<OrderResponse> queryOrders(OrderQueryRequest request);

    /**
     * 根据用户ID查询订单
     *
     * @param userId      用户ID
     * @param status      订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getUserOrders(Long userId, String status, Integer currentPage, Integer pageSize);

    /**
     * 根据商品类型查询订单
     *
     * @param goodsType   商品类型
     * @param status      订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getOrdersByGoodsType(String goodsType, String status, Integer currentPage, Integer pageSize);

    /**
     * 根据商家ID查询订单
     *
     * @param sellerId    商家ID
     * @param status      订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getSellerOrders(Long sellerId, String status, Integer currentPage, Integer pageSize);

    /**
     * 搜索订单
     *
     * @param keyword     搜索关键词
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> searchOrders(String keyword, Integer currentPage, Integer pageSize);

    /**
     * 根据时间范围查询订单
     *
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @param status      订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime, 
                                                    String status, Integer currentPage, Integer pageSize);

    // =================== 支付相关 ===================

    /**
     * 处理订单支付
     *
     * @param orderId   订单ID
     * @param payMethod 支付方式
     * @return 支付结果
     */
    Result<Map<String, Object>> processPayment(Long orderId, String payMethod);

    /**
     * 确认支付成功
     *
     * @param orderId   订单ID
     * @param payMethod 支付方式
     * @return 确认结果（仅返回状态）
     */
    Result<Void> confirmPayment(Long orderId, String payMethod);

    /**
     * 处理支付回调
     *
     * @param orderNo    订单号
     * @param payStatus  支付状态
     * @param payMethod  支付方式
     * @param extraInfo  额外信息
     * @return 处理结果（仅返回状态）
     */
    Result<Void> handlePaymentCallback(String orderNo, String payStatus, String payMethod, Map<String, Object> extraInfo);

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @param reason  退款原因
     * @return 退款结果
     */
    Result<Map<String, Object>> requestRefund(Long orderId, String reason);

    // =================== 订单状态管理 ===================

    /**
     * 发货
     *
     * @param orderId      订单ID
     * @param shippingInfo 物流信息
     * @return 发货结果（仅返回状态）
     */
    Result<Void> shipOrder(Long orderId, Map<String, Object> shippingInfo);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return 确认结果（仅返回状态）
     */
    Result<Void> confirmReceipt(Long orderId);

    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 完成结果（仅返回状态）
     */
    Result<Void> completeOrder(Long orderId);

    // =================== 统计分析 ===================

    /**
     * 获取用户订单统计
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    Result<Map<String, Object>> getUserOrderStatistics(Long userId);

    /**
     * 获取商品销售统计
     *
     * @param goodsId 商品ID
     * @return 统计结果
     */
    Result<Map<String, Object>> getGoodsSalesStatistics(Long goodsId);

    /**
     * 按商品类型统计订单
     *
     * @return 统计结果
     */
    Result<List<Map<String, Object>>> getOrderStatisticsByType();

    /**
     * 获取热门商品
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    Result<List<Map<String, Object>>> getHotGoods(Integer limit);

    /**
     * 获取日营收统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 营收统计
     */
    Result<List<Map<String, Object>>> getDailyRevenue(String startDate, String endDate);

    /**
     * 获取用户最近购买记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 购买记录
     */
    Result<List<OrderResponse>> getUserRecentOrders(Long userId, Integer limit);

    // =================== 专用查询 ===================

    /**
     * 查询用户的金币消费订单
     *
     * @param userId      用户ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getUserCoinOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询用户的充值订单
     *
     * @param userId      用户ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getUserRechargeOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询内容购买订单
     *
     * @param contentId   内容ID（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getContentOrders(Long contentId, Integer currentPage, Integer pageSize);

    /**
     * 查询订阅订单
     *
     * @param subscriptionType 订阅类型（可选）
     * @param currentPage      当前页码
     * @param pageSize         页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getSubscriptionOrders(String subscriptionType, Integer currentPage, Integer pageSize);

    // =================== 业务验证 ===================

    /**
     * 验证订单是否可以支付
     *
     * @param orderId 订单ID
     * @return 验证结果
     */
    Result<Map<String, Object>> validatePayment(Long orderId);

    /**
     * 验证订单是否可以取消
     *
     * @param orderId 订单ID
     * @return 验证结果
     */
    Result<Map<String, Object>> validateCancel(Long orderId);

    /**
     * 验证订单是否可以退款
     *
     * @param orderId 订单ID
     * @return 验证结果
     */
    Result<Map<String, Object>> validateRefund(Long orderId);

    // =================== 快捷查询 ===================

    /**
     * 获取待支付订单
     *
     * @param userId      用户ID（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getPendingOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取已完成订单
     *
     * @param userId      用户ID（可选）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getCompletedOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取今日订单
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<OrderResponse> getTodayOrders(Integer currentPage, Integer pageSize);

    /**
     * 获取超时订单
     *
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    Result<List<OrderResponse>> getTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动取消超时订单
     *
     * @param timeoutMinutes 超时分钟数
     * @return 取消数量
     */
    Result<Integer> autoCancelTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动完成已发货订单
     *
     * @param days 天数
     * @return 完成数量
     */
    Result<Integer> autoCompleteShippedOrders(Integer days);
}