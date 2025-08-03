package com.gig.collide.order.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单业务服务接口
 * 提供完整的订单管理功能
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
public interface OrderService {

    // =================== 订单创建和管理 ===================

    /**
     * 创建订单
     *
     * @param order 订单信息
     * @return 订单ID
     */
    Long createOrder(Order order);

    /**
     * 根据ID获取订单详情
     *
     * @param id 订单ID
     * @return 订单信息
     */
    Order getOrderById(Long id);

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 更新订单信息
     *
     * @param order 订单信息
     * @return 是否成功
     */
    boolean updateOrder(Order order);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param reason  取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, String reason);

    /**
     * 批量取消订单
     *
     * @param orderIds 订单ID列表
     * @param reason   取消原因
     * @return 是否成功
     */
    boolean batchCancelOrders(List<Long> orderIds, String reason);

    // =================== 订单查询 ===================

    /**
     * 根据用户ID分页查询订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> getOrdersByUserId(Page<Order> page, Long userId, String status);

    /**
     * 根据商品类型分页查询订单
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> getOrdersByGoodsType(Page<Order> page, String goodsType, String status);

    /**
     * 根据支付模式分页查询订单
     *
     * @param page        分页参数
     * @param paymentMode 支付模式
     * @param payStatus   支付状态（可选）
     * @return 分页结果
     */
    IPage<Order> getOrdersByPaymentMode(Page<Order> page, String paymentMode, String payStatus);

    /**
     * 根据商家ID查询订单
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> getOrdersBySellerId(Page<Order> page, Long sellerId, String status);

    /**
     * 查询指定时间范围内的订单
     *
     * @param page      分页参数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> getOrdersByTimeRange(Page<Order> page, LocalDateTime startTime, LocalDateTime endTime, String status);

    /**
     * 搜索订单
     *
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<Order> searchOrders(Page<Order> page, String keyword);

    // =================== 支付相关 ===================

    /**
     * 处理订单支付
     *
     * @param orderId   订单ID
     * @param payMethod 支付方式
     * @return 支付结果和相关信息
     */
    Map<String, Object> processPayment(Long orderId, String payMethod);

    /**
     * 确认支付成功
     *
     * @param orderId   订单ID
     * @param payMethod 支付方式
     * @return 是否成功
     */
    boolean confirmPayment(Long orderId, String payMethod);

    /**
     * 更新订单支付状态
     * 用于金币支付等需要直接更新状态的场景
     *
     * @param orderId   订单ID
     * @param payStatus 支付状态 (paid, failed, pending)
     * @param payMethod 支付方式
     * @return 是否成功
     */
    boolean updatePaymentStatus(Long orderId, String payStatus, String payMethod);

    /**
     * 处理支付回调
     *
     * @param orderNo    订单号
     * @param payStatus  支付状态
     * @param payMethod  支付方式
     * @param payTime    支付时间
     * @param extraInfo  额外信息
     * @return 是否成功
     */
    boolean handlePaymentCallback(String orderNo, String payStatus, String payMethod, 
                                LocalDateTime payTime, Map<String, Object> extraInfo);

    /**
     * 申请退款
     *
     * @param orderId 订单ID
     * @param reason  退款原因
     * @return 退款结果
     */
    Map<String, Object> requestRefund(Long orderId, String reason);

    // =================== 订单状态管理 ===================

    /**
     * 更新订单状态
     *
     * @param orderId   订单ID
     * @param newStatus 新状态
     * @return 是否成功
     */
    boolean updateOrderStatus(Long orderId, String newStatus);

    /**
     * 批量更新订单状态
     *
     * @param orderIds  订单ID列表
     * @param newStatus 新状态
     * @return 是否成功
     */
    boolean batchUpdateOrderStatus(List<Long> orderIds, String newStatus);

    /**
     * 发货
     *
     * @param orderId      订单ID
     * @param shippingInfo 物流信息
     * @return 是否成功
     */
    boolean shipOrder(Long orderId, Map<String, Object> shippingInfo);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean confirmReceipt(Long orderId);

    /**
     * 完成订单
     *
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean completeOrder(Long orderId);

    // =================== 定时任务相关 ===================

    /**
     * 查询待支付超时订单
     *
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    List<Order> getTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动取消超时订单
     *
     * @param timeoutMinutes 超时分钟数
     * @return 取消的订单数量
     */
    int autoCancelTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动完成确认收货的订单
     *
     * @param days 天数
     * @return 完成的订单数量
     */
    int autoCompleteShippedOrders(Integer days);

    // =================== 统计分析 ===================

    /**
     * 统计用户订单数据
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    Map<String, Object> getUserOrderStatistics(Long userId);

    /**
     * 统计商品销售数据
     *
     * @param goodsId 商品ID
     * @return 统计结果
     */
    Map<String, Object> getGoodsSalesStatistics(Long goodsId);

    /**
     * 按商品类型统计订单
     *
     * @return 统计结果
     */
    List<Map<String, Object>> getOrderStatisticsByType();

    /**
     * 查询热门商品
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<Map<String, Object>> getHotGoods(Integer limit);

    /**
     * 查询日营收统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 营收统计
     */
    List<Map<String, Object>> getDailyRevenue(String startDate, String endDate);

    /**
     * 获取用户最近购买记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 购买记录
     */
    List<Order> getUserRecentOrders(Long userId, Integer limit);

    // =================== 专用查询 ===================

    /**
     * 查询用户的金币消费订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Order> getUserCoinOrders(Page<Order> page, Long userId);

    /**
     * 查询用户的充值订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Order> getUserRechargeOrders(Page<Order> page, Long userId);

    /**
     * 查询内容购买订单
     *
     * @param page      分页参数
     * @param contentId 内容ID（可选）
     * @return 分页结果
     */
    IPage<Order> getContentOrders(Page<Order> page, Long contentId);

    /**
     * 查询订阅订单
     *
     * @param page             分页参数
     * @param subscriptionType 订阅类型（可选）
     * @return 分页结果
     */
    IPage<Order> getSubscriptionOrders(Page<Order> page, String subscriptionType);

    // =================== 业务验证 ===================

    /**
     * 验证订单是否可以支付
     *
     * @param orderId 订单ID
     * @return 验证结果和相关信息
     */
    Map<String, Object> validatePayment(Long orderId);

    /**
     * 验证订单是否可以取消
     *
     * @param orderId 订单ID
     * @return 验证结果和相关信息
     */
    Map<String, Object> validateCancel(Long orderId);

    /**
     * 验证订单是否可以退款
     *
     * @param orderId 订单ID
     * @return 验证结果和相关信息
     */
    Map<String, Object> validateRefund(Long orderId);

    /**
     * 生成订单号
     *
     * @param userId 用户ID
     * @return 订单号
     */
    String generateOrderNo(Long userId);
}