package com.gig.collide.order.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.order.domain.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单业务服务接口 - 简洁版
 * 定义核心订单业务逻辑
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface OrderService {

    /**
     * 创建订单
     * 包含订单号生成、金额校验、冗余信息填充
     * 
     * @param order 订单信息
     * @return 创建的订单
     */
    Order createOrder(Order order);

    /**
     * 支付订单
     * 更新支付状态、支付方式、支付时间
     * 
     * @param orderId 订单ID
     * @param payMethod 支付方式
     * @param payAmount 支付金额（用于校验）
     * @param thirdPartyTradeNo 第三方交易号
     * @return 更新后的订单
     */
    Order payOrder(Long orderId, String payMethod, 
                  java.math.BigDecimal payAmount, String thirdPartyTradeNo);

    /**
     * 取消订单
     * 更新订单状态为cancelled
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（权限校验）
     * @param cancelReason 取消原因
     * @return 是否成功
     */
    boolean cancelOrder(Long orderId, Long userId, String cancelReason);

    /**
     * 根据ID查询订单
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    Order getOrderById(Long orderId);

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order getOrderByOrderNo(String orderNo);

    /**
     * 分页查询订单
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param payStatus 支付状态（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页订单列表
     */
    IPage<Order> queryOrders(Long userId, String status, String payStatus,
                           Integer pageNum, Integer pageSize);

    /**
     * 查询用户订单
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param payStatus 支付状态（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页订单列表
     */
    IPage<Order> getUserOrders(Long userId, String status, String payStatus,
                             Integer pageNum, Integer pageSize);

    /**
     * 根据商品ID查询订单
     * 
     * @param goodsId 商品ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<Order> getOrdersByGoodsId(Long goodsId, String status);

    /**
     * 查询时间范围内的订单
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<Order> getOrdersByTimeRange(LocalDateTime startTime, 
                                   LocalDateTime endTime, String status);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateOrderStatus(Long orderId, String status);

    /**
     * 删除订单（逻辑删除）
     * 
     * @param orderId 订单ID
     * @param userId 用户ID（权限校验）
     * @return 是否成功
     */
    boolean deleteOrder(Long orderId, Long userId);

    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Long countUserOrders(Long userId, String status);

    /**
     * 统计商品销量
     * 
     * @param goodsId 商品ID
     * @return 销量
     */
    Long countGoodsSales(Long goodsId);

    /**
     * 生成订单号
     * 格式：ORDER + 时间戳 + 随机数
     * 
     * @return 唯一订单号
     */
    String generateOrderNo();
}