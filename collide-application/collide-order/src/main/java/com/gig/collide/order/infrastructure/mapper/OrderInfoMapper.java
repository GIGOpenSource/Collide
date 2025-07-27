package com.gig.collide.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.order.domain.entity.OrderInfo;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单信息 Mapper 接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 幂等性更新订单状态
     * 
     * @param orderNo 订单号
     * @param expectedStatus 期望的当前状态
     * @param newStatus 新状态
     * @param version 版本号
     * @return 影响行数
     */
    @Update("UPDATE order_info SET status = #{newStatus}, version = version + 1, update_time = NOW() " +
            "WHERE order_no = #{orderNo} AND status = #{expectedStatus} AND version = #{version} AND deleted = 0")
    int updateStatusIdempotent(@Param("orderNo") String orderNo, 
                              @Param("expectedStatus") String expectedStatus,
                              @Param("newStatus") String newStatus,
                              @Param("version") Integer version);

    /**
     * 根据订单号查询（加悲观锁）
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Select("SELECT * FROM order_info WHERE order_no = #{orderNo} AND deleted = 0 FOR UPDATE")
    OrderInfo selectByOrderNoForUpdate(@Param("orderNo") String orderNo);

    /**
     * 根据订单号查询
     * 
     * @param orderNo 订单号
     * @return 订单信息
     */
    @Select("SELECT * FROM order_info WHERE order_no = #{orderNo} AND deleted = 0")
    OrderInfo selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 分页查询订单
     * 
     * @param page 分页参数
     * @param queryParams 查询参数
     * @return 订单分页信息
     */
    @Select("<script>" +
            "SELECT * FROM order_info WHERE deleted = 0 " +
            "<if test='queryParams.userId != null'> AND user_id = #{queryParams.userId} </if>" +
            "<if test='queryParams.status != null and queryParams.status != \"\"'> AND status = #{queryParams.status} </if>" +
            "<if test='queryParams.goodsType != null and queryParams.goodsType != \"\"'> AND goods_type = #{queryParams.goodsType} </if>" +
            "<if test='queryParams.startTime != null'> AND create_time >= #{queryParams.startTime} </if>" +
            "<if test='queryParams.endTime != null'> AND create_time &lt;= #{queryParams.endTime} </if>" +
            "<if test='queryParams.orderNo != null and queryParams.orderNo != \"\"'> AND order_no LIKE CONCAT('%', #{queryParams.orderNo}, '%') </if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    IPage<OrderInfo> pageQuery(Page<OrderInfo> page, @Param("queryParams") Map<String, Object> queryParams);

    /**
     * 根据用户ID和状态查询订单
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @return 订单列表
     */
    @Select("SELECT * FROM order_info WHERE user_id = #{userId} AND status = #{status} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<OrderInfo> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新支付信息
     * 
     * @param orderNo 订单号
     * @param payType 支付方式
     * @param payTime 支付时间
     * @param version 版本号
     * @return 影响行数
     */
    @Update("UPDATE order_info SET pay_type = #{payType}, pay_time = #{payTime}, " +
            "version = version + 1, update_time = NOW() " +
            "WHERE order_no = #{orderNo} AND version = #{version} AND deleted = 0")
    int updatePayInfo(@Param("orderNo") String orderNo,
                     @Param("payType") String payType,
                     @Param("payTime") LocalDateTime payTime,
                     @Param("version") Integer version);

    /**
     * 批量更新订单状态
     * 
     * @param orderNos 订单号列表
     * @param newStatus 新状态
     * @param expectedStatus 期望的当前状态
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE order_info SET status = #{newStatus}, version = version + 1, update_time = NOW() " +
            "WHERE deleted = 0 AND status = #{expectedStatus} AND order_no IN " +
            "<foreach collection='orderNos' item='orderNo' open='(' separator=',' close=')'>" +
            "#{orderNo}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("orderNos") List<String> orderNos,
                         @Param("expectedStatus") String expectedStatus,
                         @Param("newStatus") String newStatus);

    /**
     * 统计订单数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param status 订单状态（可选）
     * @return 统计结果
     */
    @Select("<script>" +
            "SELECT " +
            "COUNT(*) as totalOrders, " +
            "COALESCE(SUM(total_amount), 0) as totalAmount, " +
            "COALESCE(AVG(total_amount), 0) as avgOrderAmount " +
            "FROM order_info " +
            "WHERE deleted = 0 " +
            "<if test='startTime != null'> AND create_time >= #{startTime} </if>" +
            "<if test='endTime != null'> AND create_time &lt;= #{endTime} </if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status} </if>" +
            "</script>")
    Map<String, Object> getOrderStatistics(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime,
                                          @Param("status") String status);

    /**
     * 查询即将过期的订单
     * 
     * @param expireTime 过期时间点
     * @return 即将过期的订单列表
     */
    @Select("SELECT * FROM order_info " +
            "WHERE status IN ('CREATE', 'UNPAID') " +
            "AND expire_time <= #{expireTime} " +
            "AND deleted = 0 " +
            "ORDER BY expire_time ASC")
    List<OrderInfo> selectExpiredOrders(@Param("expireTime") LocalDateTime expireTime);
} 