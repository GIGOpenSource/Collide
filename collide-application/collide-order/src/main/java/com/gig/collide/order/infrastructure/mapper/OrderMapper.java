package com.gig.collide.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.Order;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单数据访问层
 * 基于MyBatis Plus的增强Mapper
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
    @Select("""
            <script>
            SELECT * FROM t_order 
            WHERE user_id = #{userId}
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
            ORDER BY create_time DESC
            </script>
            """)
    IPage<Order> selectByUserId(Page<Order> page, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo}")
    Order selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据商品类型分页查询订单
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    订单状态（可选）
     * @return 分页结果
     */
    @Select("""
            <script>
            SELECT * FROM t_order 
            WHERE goods_type = #{goodsType}
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
            ORDER BY create_time DESC
            </script>
            """)
    IPage<Order> selectByGoodsType(Page<Order> page, @Param("goodsType") String goodsType, @Param("status") String status);

    /**
     * 根据支付模式分页查询订单
     *
     * @param page        分页参数
     * @param paymentMode 支付模式
     * @param payStatus   支付状态（可选）
     * @return 分页结果
     */
    @Select("""
            <script>
            SELECT * FROM t_order 
            WHERE payment_mode = #{paymentMode}
            <if test="payStatus != null and payStatus != ''">
                AND pay_status = #{payStatus}
            </if>
            ORDER BY create_time DESC
            </script>
            """)
    IPage<Order> selectByPaymentMode(Page<Order> page, @Param("paymentMode") String paymentMode, @Param("payStatus") String payStatus);

    /**
     * 根据商家ID查询订单（通过商品信息）
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   订单状态（可选）
     * @return 分页结果
     */
    @Select("""
            <script>
            SELECT o.* FROM t_order o
            INNER JOIN t_goods g ON o.goods_id = g.id
            WHERE g.seller_id = #{sellerId}
            <if test="status != null and status != ''">
                AND o.status = #{status}
            </if>
            ORDER BY o.create_time DESC
            </script>
            """)
    IPage<Order> selectBySellerId(Page<Order> page, @Param("sellerId") Long sellerId, @Param("status") String status);

    /**
     * 查询待支付超时订单
     *
     * @param timeoutMinutes 超时分钟数
     * @return 超时订单列表
     */
    @Select("""
            SELECT * FROM t_order 
            WHERE status = 'pending' 
            AND pay_status = 'unpaid'
            AND create_time < #{timeoutTime}
            """)
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
    @Select("""
            <script>
            SELECT * FROM t_order 
            WHERE create_time BETWEEN #{startTime} AND #{endTime}
            <if test="status != null and status != ''">
                AND status = #{status}
            </if>
            ORDER BY create_time DESC
            </script>
            """)
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
    @Select("""
            SELECT * FROM t_order 
            WHERE user_id = #{userId} 
            AND payment_mode = 'coin'
            AND pay_status = 'paid'
            ORDER BY pay_time DESC
            """)
    IPage<Order> selectUserCoinOrders(Page<Order> page, @Param("userId") Long userId);

    /**
     * 查询用户的充值订单
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_order 
            WHERE user_id = #{userId} 
            AND goods_type = 'coin'
            AND pay_status = 'paid'
            ORDER BY pay_time DESC
            """)
    IPage<Order> selectUserRechargeOrders(Page<Order> page, @Param("userId") Long userId);

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 统计结果
     */
    @Select("""
            SELECT 
                COUNT(*) as total_orders,
                COUNT(CASE WHEN status = 'completed' THEN 1 END) as completed_orders,
                COUNT(CASE WHEN status = 'cancelled' THEN 1 END) as cancelled_orders,
                COUNT(CASE WHEN pay_status = 'paid' THEN 1 END) as paid_orders,
                COALESCE(SUM(CASE WHEN payment_mode = 'cash' AND pay_status = 'paid' THEN final_amount END), 0) as total_cash_spent,
                COALESCE(SUM(CASE WHEN payment_mode = 'coin' AND pay_status = 'paid' THEN coin_cost END), 0) as total_coins_spent
            FROM t_order 
            WHERE user_id = #{userId}
            """)
    Map<String, Object> selectUserOrderStatistics(@Param("userId") Long userId);

    /**
     * 统计商品销售情况
     *
     * @param goodsId 商品ID
     * @return 统计结果
     */
    @Select("""
            SELECT 
                COUNT(*) as order_count,
                SUM(quantity) as total_quantity,
                COUNT(CASE WHEN pay_status = 'paid' THEN 1 END) as paid_count,
                SUM(CASE WHEN pay_status = 'paid' THEN quantity END) as paid_quantity,
                COALESCE(SUM(CASE WHEN payment_mode = 'cash' AND pay_status = 'paid' THEN final_amount END), 0) as total_revenue
            FROM t_order 
            WHERE goods_id = #{goodsId}
            """)
    Map<String, Object> selectGoodsSalesStatistics(@Param("goodsId") Long goodsId);

    /**
     * 按商品类型统计订单
     *
     * @return 统计结果
     */
    @Select("""
            SELECT 
                goods_type,
                payment_mode,
                status,
                COUNT(*) as order_count,
                SUM(quantity) as total_quantity,
                COALESCE(SUM(CASE WHEN payment_mode = 'cash' THEN final_amount END), 0) as cash_amount,
                COALESCE(SUM(CASE WHEN payment_mode = 'coin' THEN coin_cost END), 0) as coin_amount
            FROM t_order 
            WHERE pay_status = 'paid'
            GROUP BY goods_type, payment_mode, status
            """)
    List<Map<String, Object>> selectOrderStatisticsByType();

    /**
     * 查询热门商品（按订单数量）
     *
     * @param limit 限制数量
     * @return 热门商品列表
     */
    @Select("""
            SELECT 
                goods_id,
                goods_name,
                goods_type,
                COUNT(*) as order_count,
                SUM(quantity) as total_quantity,
                COALESCE(SUM(CASE WHEN payment_mode = 'cash' AND pay_status = 'paid' THEN final_amount END), 0) as total_revenue
            FROM t_order 
            WHERE pay_status = 'paid'
            GROUP BY goods_id, goods_name, goods_type
            ORDER BY order_count DESC, total_quantity DESC
            LIMIT #{limit}
            """)
    List<Map<String, Object>> selectHotGoods(@Param("limit") Integer limit);

    /**
     * 查询用户最近购买记录
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 购买记录
     */
    @Select("""
            SELECT * FROM t_order 
            WHERE user_id = #{userId} 
            AND pay_status = 'paid'
            ORDER BY pay_time DESC
            LIMIT #{limit}
            """)
    List<Order> selectUserRecentOrders(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 批量更新订单状态
     *
     * @param orderIds  订单ID列表
     * @param newStatus 新状态
     * @return 影响行数
     */
    @Update("""
            <script>
            UPDATE t_order SET 
                status = #{newStatus},
                update_time = NOW()
            WHERE id IN
            <foreach collection="orderIds" item="id" open="(" close=")" separator=",">
                #{id}
            </foreach>
            </script>
            """)
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
    @Update("""
            UPDATE t_order SET 
                pay_status = #{payStatus},
                pay_method = #{payMethod},
                pay_time = #{payTime},
                update_time = NOW()
            WHERE id = #{orderId}
            """)
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
    @Select("""
            SELECT 
                DATE(pay_time) as order_date,
                COUNT(*) as order_count,
                COALESCE(SUM(CASE WHEN payment_mode = 'cash' THEN final_amount END), 0) as cash_revenue,
                COALESCE(SUM(CASE WHEN payment_mode = 'coin' THEN coin_cost END), 0) as coin_revenue
            FROM t_order 
            WHERE pay_status = 'paid'
            AND DATE(pay_time) BETWEEN #{startDate} AND #{endDate}
            GROUP BY DATE(pay_time)
            ORDER BY order_date DESC
            """)
    List<Map<String, Object>> selectDailyRevenue(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 搜索订单
     *
     * @param page    分页参数
     * @param keyword 搜索关键词（订单号、商品名称、用户昵称）
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_order 
            WHERE order_no LIKE CONCAT('%', #{keyword}, '%')
            OR goods_name LIKE CONCAT('%', #{keyword}, '%')
            OR user_nickname LIKE CONCAT('%', #{keyword}, '%')
            ORDER BY create_time DESC
            """)
    IPage<Order> searchOrders(Page<Order> page, @Param("keyword") String keyword);
}