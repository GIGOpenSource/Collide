package com.gig.collide.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.order.domain.entity.OrderContentAssociation;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单内容关联 Mapper 接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Mapper
public interface OrderContentAssociationMapper extends BaseMapper<OrderContentAssociation> {

    /**
     * 根据订单号查询内容关联
     * 
     * @param orderNo 订单号
     * @return 内容关联列表
     */
    @Select("SELECT * FROM order_content_association " +
            "WHERE order_no = #{orderNo} AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<OrderContentAssociation> selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 根据用户ID和内容ID查询关联
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 内容关联
     */
    @Select("SELECT * FROM order_content_association " +
            "WHERE user_id = #{userId} AND content_id = #{contentId} " +
            "AND status = 'ACTIVE' AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT 1")
    OrderContentAssociation selectByUserIdAndContentId(@Param("userId") Long userId, 
                                                       @Param("contentId") Long contentId);

    /**
     * 根据用户ID查询有权限的内容
     * 
     * @param userId 用户ID
     * @param contentType 内容类型（可选）
     * @return 内容关联列表
     */
    @Select("<script>" +
            "SELECT * FROM order_content_association " +
            "WHERE user_id = #{userId} AND status = 'ACTIVE' AND deleted = 0 " +
            "<if test='contentType != null and contentType != \"\"'> AND content_type = #{contentType} </if>" +
            "AND (access_end_time IS NULL OR access_end_time > NOW()) " +
            "ORDER BY create_time DESC" +
            "</script>")
    List<OrderContentAssociation> selectUserAccessibleContents(@Param("userId") Long userId,
                                                               @Param("contentType") String contentType);

    /**
     * 检查用户是否有内容访问权限
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 权限数量（>0表示有权限）
     */
    @Select("SELECT COUNT(*) FROM order_content_association " +
            "WHERE user_id = #{userId} AND content_id = #{contentId} " +
            "AND status = 'ACTIVE' AND deleted = 0 " +
            "AND access_start_time <= NOW() " +
            "AND (access_end_time IS NULL OR access_end_time > NOW())")
    int checkUserContentAccess(@Param("userId") Long userId, @Param("contentId") Long contentId);

    /**
     * 批量创建订单内容关联
     * 
     * @param associations 关联数据列表
     * @return 影响行数
     */
    @Insert("<script>" +
            "INSERT INTO order_content_association " +
            "(order_id, order_no, user_id, content_id, content_type, content_title, " +
            "access_type, access_start_time, access_end_time, status, goods_id, goods_type, " +
            "consumed_coins, remark, create_time, update_time, version) VALUES " +
            "<foreach collection='associations' item='item' separator=','>" +
            "(#{item.orderId}, #{item.orderNo}, #{item.userId}, #{item.contentId}, " +
            "#{item.contentType}, #{item.contentTitle}, #{item.accessType}, " +
            "#{item.accessStartTime}, #{item.accessEndTime}, #{item.status}, " +
            "#{item.goodsId}, #{item.goodsType}, #{item.consumedCoins}, #{item.remark}, " +
            "NOW(), NOW(), 1)" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("associations") List<OrderContentAssociation> associations);

    /**
     * 更新权限状态
     * 
     * @param id 关联ID
     * @param status 新状态
     * @param version 版本号
     * @return 影响行数
     */
    @Update("UPDATE order_content_association " +
            "SET status = #{status}, version = version + 1, update_time = NOW() " +
            "WHERE id = #{id} AND version = #{version} AND deleted = 0")
    int updateStatus(@Param("id") Long id, 
                    @Param("status") String status,
                    @Param("version") Integer version);

    /**
     * 批量更新权限状态
     * 
     * @param orderNo 订单号
     * @param newStatus 新状态
     * @param reason 原因
     * @return 影响行数
     */
    @Update("UPDATE order_content_association " +
            "SET status = #{newStatus}, remark = #{reason}, version = version + 1, update_time = NOW() " +
            "WHERE order_no = #{orderNo} AND deleted = 0")
    int batchUpdateStatusByOrderNo(@Param("orderNo") String orderNo,
                                  @Param("newStatus") String newStatus,
                                  @Param("reason") String reason);

    /**
     * 查询即将过期的权限
     * 
     * @param expireTime 过期时间点
     * @return 即将过期的权限列表
     */
    @Select("SELECT * FROM order_content_association " +
            "WHERE status = 'ACTIVE' " +
            "AND access_end_time IS NOT NULL " +
            "AND access_end_time <= #{expireTime} " +
            "AND deleted = 0 " +
            "ORDER BY access_end_time ASC")
    List<OrderContentAssociation> selectExpiringAccess(@Param("expireTime") LocalDateTime expireTime);

    /**
     * 统计用户内容访问权限
     * 
     * @param userId 用户ID
     * @return 统计结果
     */
    @Select("SELECT " +
            "COUNT(*) as totalAccess, " +
            "COUNT(CASE WHEN access_end_time IS NULL THEN 1 END) as permanentAccess, " +
            "COUNT(CASE WHEN access_end_time IS NOT NULL AND access_end_time > NOW() THEN 1 END) as temporaryAccess, " +
            "COUNT(CASE WHEN content_type = 'VIDEO' THEN 1 END) as videoAccess, " +
            "COUNT(CASE WHEN content_type = 'ARTICLE' THEN 1 END) as articleAccess " +
            "FROM order_content_association " +
            "WHERE user_id = #{userId} AND status = 'ACTIVE' AND deleted = 0")
    Map<String, Object> getUserAccessStatistics(@Param("userId") Long userId);

    /**
     * 根据内容ID统计购买用户数
     * 
     * @param contentId 内容ID
     * @return 购买用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM order_content_association " +
            "WHERE content_id = #{contentId} AND status = 'ACTIVE' AND deleted = 0")
    int countUsersByContentId(@Param("contentId") Long contentId);
} 