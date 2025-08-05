package com.gig.collide.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层
 * 基于MyBatis Plus的增强Mapper，使用XML配置SQL
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Repository
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据用户ID分页查询订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> selectByUserId(Page<Order> page, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据商品类型分页查询订单
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> selectByGoodsType(Page<Order> page, @Param("goodsType") String goodsType, @Param("status") String status);

    /**
     * 根据支付模式分页查询订单
     *
     * @param page        分页参数
     * @param paymentMode 支付模式
     * @param payStatus   支付状态（可选）
     * @return 分页结果
     */
    IPage<Order> selectByPaymentMode(Page<Order> page, @Param("paymentMode") String paymentMode, @Param("payStatus") String payStatus);

    /**
     * 根据商家ID查询订单（通过商品信息）
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> selectBySellerId(Page<Order> page, @Param("sellerId") Long sellerId, @Param("status") String status);

    /**
     * 查询待支付超时订单
     *
     * @param timeoutTime 超时时间
     * @return 超时订单列表
     */
    List<Order> selectTimeoutOrders(@Param("timeoutTime") LocalDateTime timeoutTime);

    /**
     * 查询指定时间范围内的订单
     *
     * @param page      分页参数
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param status    订单状态（可选）
     * @return 分页结果
     */
    IPage<Order> selectByTimeRange(Page<Order> page, 
                                  @Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("status") String status);

    /**
     * 查询用户的金币消费订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Order> selectUserCoinOrders(Page<Order> page, @Param("userId") Long userId);

    /**
     * 查询用户的充值订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<Order> selectUserRechargeOrders(Page<Order> page, @Param("userId") Long userId);

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    Map<String, Object> selectUserOrderStatistics(@Param("userId") Long userId);

    /**
     * 统计商品销售情况
     *
     * @param goodsId 商品ID
     * @return 统计结果
     */
    Map<String, Object> selectGoodsSalesStatistics(@Param("goodsId") Long goodsId);

    /**
     * 按商品类型统计订单
     *
     * @return 统计结果
     */
    List<Map<String, Object>> selectOrderStatisticsByType();

    /**
     * 查询热门商品（按订单数量）
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<Map<String, Object>> selectHotGoods(@Param("limit") Integer limit);

    /**
     * 查询用户最近购买记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 购买记录
     */
    List<Order> selectUserRecentOrders(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量更新订单状态
     *
     * @param orderIds  订单ID列表
     * @param newStatus 新状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("orderIds") List<Long> orderIds, @Param("newStatus") String newStatus);

    /**
     * 更新订单支付信息
     *
     * @param orderId   订单ID
     * @param payStatus 支付状态
     * @param payMethod 支付方式
     * @param payTime   支付时间
     * @return 影响行数
     */
    int updatePaymentInfo(@Param("orderId") Long orderId, 
                         @Param("payStatus") String payStatus,
                         @Param("payMethod") String payMethod, 
                         @Param("payTime") LocalDateTime payTime);

    /**
     * 查询日营收统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 营收统计
     */
    List<Map<String, Object>> selectDailyRevenue(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 搜索订单
     *
     * @param page    分页参数
     * @param keyword 搜索关键词（订单号、商品名称、用户昵称）
     * @return 分页结果
     */
    IPage<Order> searchOrders(Page<Order> page, @Param("keyword") String keyword);

    /**
     * 根据商品ID统计订单数
     *
     * @param goodsId 商品ID
     * @param status  订单状态（可选）
     * @return 订单数量
     */
    Long countByGoodsId(@Param("goodsId") Long goodsId, @Param("status") String status);

    /**
     * 根据用户ID统计订单数
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Long countByUserId(@Param("userId") Long userId, @Param("status") String status);
}