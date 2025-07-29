package com.gig.collide.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问层 - 简洁版
 * 基于MyBatis-Plus，实现简洁的数据访问
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 分页查询用户订单
     * 
     * @param page 分页参数
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @param payStatus 支付状态（可选）
     * @return 订单分页数据
     */
    IPage<Order> findUserOrders(Page<Order> page, 
                               @Param("userId") Long userId,
                               @Param("status") String status, 
                               @Param("payStatus") String payStatus);

    /**
     * 根据商品ID查询订单列表
     * 
     * @param goodsId 商品ID
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<Order> findByGoodsId(@Param("goodsId") Long goodsId, 
                             @Param("status") String status);

    /**
     * 查询指定时间范围内的订单
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（可选）
     * @return 订单列表
     */
    List<Order> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime,
                               @Param("status") String status);

    /**
     * 更新订单状态
     * 
     * @param orderId 订单ID
     * @param status 新状态
     * @return 更新行数
     */
    int updateOrderStatus(@Param("orderId") Long orderId, 
                         @Param("status") String status);

    /**
     * 更新订单支付信息
     * 
     * @param orderId 订单ID
     * @param payStatus 支付状态
     * @param payMethod 支付方式
     * @param payTime 支付时间
     * @return 更新行数
     */
    int updatePayInfo(@Param("orderId") Long orderId,
                     @Param("payStatus") String payStatus,
                     @Param("payMethod") String payMethod,
                     @Param("payTime") LocalDateTime payTime);

    /**
     * 统计用户订单数量
     * 
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Long countUserOrders(@Param("userId") Long userId, 
                        @Param("status") String status);

    /**
     * 统计商品销量
     * 
     * @param goodsId 商品ID
     * @param status 订单状态（只统计已支付的订单）
     * @return 销量
     */
    Long countGoodsSales(@Param("goodsId") Long goodsId, 
                        @Param("status") String status);
}