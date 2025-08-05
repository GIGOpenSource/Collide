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
 * 订单门面服务接口 - 规范版
 * 基于order-simple.sql的去连表化设计，实现完整的订单管理功能
 * 支持商品订单、内容订单、订阅订单等多种业务场景
 * 
 * @author GIG Team
 * @version 2.0.0 (规范版)
 * @since 2024-01-31
 */
public interface OrderFacadeService {

    // =================== 订单创建 ===================

    /**
     * 创建订单
     * 支持商品订单、内容订单、订阅订单等多种类型
     * 包含库存检查、价格计算、用户验证等完整业务逻辑
     * 
     * @param request 订单创建请求
     * @return 创建的订单信息
     */
    Result<OrderResponse> createOrder(OrderCreateRequest request);

    // =================== 订单查询 ===================

    /**
     * 根据ID获取订单详情
     * 包含权限验证，确保用户只能查看自己的订单
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    Result<OrderResponse> getOrderById(Long orderId, Long userId);

    /**
     * 根据订单号获取订单详情
     * 支持用户查询自己的订单，管理员查询所有订单
     * 
     * @param orderNo 订单号
     * @param userId 用户ID（用于权限验证）
     * @return 订单详情
     */
    Result<OrderResponse> getOrderByOrderNo(String orderNo, Long userId);

    /**
     * 分页查询订单
     * 支持多维度条件查询和筛选
     * 
     * @param request 查询请求
     * @return 订单列表分页
     */
    Result<PageResponse<OrderResponse>> queryOrders(OrderQueryRequest request);

    /**
     * 查询用户订单
     * 支持按状态筛选，权限验证确保用户只能查看自己的订单
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 用户订单分页
     */
    Result<PageResponse<OrderResponse>> getUserOrders(Long userId, String status, 
                                                     Integer currentPage, Integer pageSize);

    /**
     * 根据商品类型查询订单
     * 支持查看特定类型商品的订单统计和明细
     * 
     * @param goodsType 商品类型（coin/content/subscription）
     * @param status 订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 商品类型订单分页
     */
    Result<PageResponse<OrderResponse>> getOrdersByGoodsType(String goodsType, String status,
                                                            Integer currentPage, Integer pageSize);

    /**
     * 查询商家订单
     * 商家查看自己的订单明细，包含销售统计
     * 
     * @param sellerId 商家ID
     * @param status 订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 商家订单分页
     */
    Result<PageResponse<OrderResponse>> getSellerOrders(Long sellerId, String status,
                                                       Integer currentPage, Integer pageSize);

    /**
     * 搜索订单
     * 支持订单号、商品名称、用户昵称等关键词搜索
     * 
     * @param keyword 搜索关键词
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 搜索结果分页
     */
    Result<PageResponse<OrderResponse>> searchOrders(String keyword, Integer currentPage, Integer pageSize);

    /**
     * 根据时间范围查询订单
     * 支持查看特定时间段的订单数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（可选）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 时间范围订单分页
     */
    Result<PageResponse<OrderResponse>> getOrdersByTimeRange(LocalDateTime startTime, LocalDateTime endTime,
                                                            String status, Integer currentPage, Integer pageSize);

    // =================== 支付处理 ===================

    /**
     * 处理订单支付
     * 支持现金支付、金币支付等多种支付方式
     * 包含支付前验证、支付渠道选择、支付结果处理
     * 
     * @param orderId 订单ID
     * @param payMethod 支付方式（cash/coin）
     * @param userId 用户ID（用于权限验证）
     * @return 支付处理结果
     */
    Result<Map<String, Object>> processPayment(Long orderId, String payMethod, Long userId);

    /**
     * 确认支付成功
     * 用于外部支付完成后的确认处理
     * 
     * @param orderId 订单ID
     * @param payMethod 支付方式
     * @param userId 用户ID（用于权限验证）
     * @return 确认结果
     */
    Result<Void> confirmPayment(Long orderId, String payMethod, Long userId);

    /**
     * 处理支付回调
     * 处理第三方支付平台的异步回调通知
     * 
     * @param orderNo 订单号
     * @param payStatus 支付状态
     * @param payMethod 支付方式
     * @param extraInfo 回调额外信息
     * @return 处理结果
     */
    Result<Void> handlePaymentCallback(String orderNo, String payStatus, String payMethod, 
                                     Map<String, Object> extraInfo);

    /**
     * 申请退款
     * 支持全额退款和部分退款，包含退款前验证
     * 
     * @param orderId 订单ID
     * @param reason 退款原因
     * @param userId 用户ID（用于权限验证）
     * @return 退款申请结果
     */
    Result<Map<String, Object>> requestRefund(Long orderId, String reason, Long userId);

    // =================== 订单管理 ===================

    /**
     * 取消订单
     * 支持用户主动取消和系统超时取消
     * 包含取消条件验证和库存回滚
     * 
     * @param orderId 订单ID
     * @param reason 取消原因
     * @param userId 操作用户ID
     * @return 取消结果
     */
    Result<Void> cancelOrder(Long orderId, String reason, Long userId);

    /**
     * 发货
     * 商家发货操作，更新订单状态和物流信息
     * 
     * @param orderId 订单ID
     * @param shippingInfo 物流信息
     * @param operatorId 操作者ID（商家ID）
     * @return 发货结果
     */
    Result<Void> shipOrder(Long orderId, Map<String, Object> shippingInfo, Long operatorId);

    /**
     * 确认收货
     * 用户确认收到商品，订单状态变更为已完成
     * 
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 确认结果
     */
    Result<Void> confirmReceipt(Long orderId, Long userId);

    /**
     * 完成订单
     * 系统或管理员完成订单的最终处理
     * 
     * @param orderId 订单ID
     * @param operatorId 操作者ID
     * @return 完成结果
     */
    Result<Void> completeOrder(Long orderId, Long operatorId);

    // =================== 统计分析 ===================

    /**
     * 获取用户订单统计
     * 统计用户的订单总数、金额、状态分布等信息
     * 
     * @param userId 用户ID
     * @return 用户订单统计数据
     */
    Result<Map<String, Object>> getUserOrderStatistics(Long userId);

    /**
     * 获取商品销售统计
     * 统计商品的销量、营收、订单状态分布
     * 
     * @param goodsId 商品ID
     * @return 商品销售统计数据
     */
    Result<Map<String, Object>> getGoodsSalesStatistics(Long goodsId);

    /**
     * 按商品类型统计订单
     * 统计各类型商品的订单分布和营收情况
     * 
     * @return 商品类型统计数据
     */
    Result<List<Map<String, Object>>> getOrderStatisticsByType();

    /**
     * 获取热门商品
     * 根据销量和订单数量统计最受欢迎的商品
     * 
     * @param limit 返回数量限制
     * @return 热门商品统计列表
     */
    Result<List<Map<String, Object>>> getHotGoods(Integer limit);

    /**
     * 获取日营收统计
     * 统计指定时间范围内的每日营收数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日营收统计数据
     */
    Result<List<Map<String, Object>>> getDailyRevenue(String startDate, String endDate);

    /**
     * 获取用户最近购买记录
     * 查看用户最近的购买历史，用于推荐和分析
     * 
     * @param userId 用户ID
     * @param limit 记录数量限制
     * @return 最近购买记录
     */
    Result<List<OrderResponse>> getUserRecentOrders(Long userId, Integer limit);

    // =================== 专用查询 ===================

    /**
     * 查询用户的金币消费订单
     * 用于查看用户使用金币购买内容或服务的订单记录
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 金币消费订单分页
     */
    Result<PageResponse<OrderResponse>> getUserCoinOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询用户的充值订单
     * 用于查看用户购买金币的充值订单记录
     * 
     * @param userId 用户ID
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 充值订单分页
     */
    Result<PageResponse<OrderResponse>> getUserRechargeOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询内容购买订单
     * 查看特定内容的购买订单，用于内容统计分析
     * 
     * @param contentId 内容ID（可选，为空则查询所有内容订单）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 内容购买订单分页
     */
    Result<PageResponse<OrderResponse>> getContentOrders(Long contentId, Integer currentPage, Integer pageSize);

    /**
     * 查询订阅订单
     * 查看用户的会员订阅、VIP等订阅类服务订单
     * 
     * @param subscriptionType 订阅类型（可选，如VIP/PREMIUM）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 订阅订单分页
     */
    Result<PageResponse<OrderResponse>> getSubscriptionOrders(String subscriptionType, Integer currentPage, Integer pageSize);

    // =================== 业务验证 ===================

    /**
     * 验证订单是否可以支付
     * 检查订单状态、库存、用户余额等支付前置条件
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 验证结果和可支付信息
     */
    Result<Map<String, Object>> validatePayment(Long orderId, Long userId);

    /**
     * 验证订单是否可以取消
     * 检查订单状态和取消规则，确定是否允许取消
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 验证结果和取消规则信息
     */
    Result<Map<String, Object>> validateCancel(Long orderId, Long userId);

    /**
     * 验证订单是否可以退款
     * 检查退款政策、订单状态和退款条件
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（用于权限验证）
     * @return 验证结果和退款规则信息
     */
    Result<Map<String, Object>> validateRefund(Long orderId, Long userId);

    // =================== 快捷查询 ===================

    /**
     * 获取待支付订单
     * 查询用户或系统中待支付的订单
     * 
     * @param userId 用户ID（可选，为空则查询系统所有待支付订单）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 待支付订单分页
     */
    Result<PageResponse<OrderResponse>> getPendingOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取已完成订单
     * 查询用户或系统中已完成的订单
     * 
     * @param userId 用户ID（可选，为空则查询系统所有已完成订单）
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 已完成订单分页
     */
    Result<PageResponse<OrderResponse>> getCompletedOrders(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 获取今日订单
     * 查询今天创建的所有订单
     * 
     * @param currentPage 当前页码
     * @param pageSize 页面大小
     * @return 今日订单分页
     */
    Result<PageResponse<OrderResponse>> getTodayOrders(Integer currentPage, Integer pageSize);

    // =================== 批量操作 ===================

    /**
     * 批量取消订单
     * 支持用户批量取消自己的订单，或管理员批量取消订单
     * 
     * @param orderIds 订单ID列表
     * @param reason 取消原因
     * @param operatorId 操作者ID
     * @return 批量取消结果
     */
    Result<Void> batchCancelOrders(List<Long> orderIds, String reason, Long operatorId);

    // =================== 系统管理 ===================

    /**
     * 获取超时订单
     * 查询超过指定时间仍未支付的订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    Result<List<OrderResponse>> getTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动取消超时订单
     * 系统定时任务，自动取消超时未支付的订单
     * 
     * @param timeoutMinutes 超时分钟数
     * @return 取消的订单数量
     */
    Result<Integer> autoCancelTimeoutOrders(Integer timeoutMinutes);

    /**
     * 自动完成已发货订单
     * 系统定时任务，自动完成长时间未确认收货的订单
     * 
     * @param days 发货后天数
     * @return 自动完成的订单数量
     */
    Result<Integer> autoCompleteShippedOrders(Integer days);

    /**
     * 根据商品ID统计订单数
     * 
     * @param goodsId 商品ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Result<Long> countOrdersByGoodsId(Long goodsId, String status);

    /**
     * 根据用户ID统计订单数
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Result<Long> countOrdersByUserId(Long userId, String status);

    /**
     * 更新订单支付信息
     * 内部系统调用，更新订单的支付状态和时间
     * 
     * @param orderId 订单ID
     * @param payStatus 支付状态
     * @param payMethod 支付方式
     * @param payTime 支付时间
     * @return 更新结果
     */
    Result<Void> updatePaymentInfo(Long orderId, String payStatus, String payMethod, LocalDateTime payTime);

    /**
     * 订单系统健康检查
     * 检查订单系统的运行状态
     * 
     * @return 系统健康状态
     */
    Result<String> healthCheck();
}