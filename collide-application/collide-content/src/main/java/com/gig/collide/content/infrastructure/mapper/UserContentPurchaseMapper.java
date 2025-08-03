package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.UserContentPurchase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户内容购买记录表数据映射接口
 * 专注于C端必需的购买记录查询功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface UserContentPurchaseMapper extends BaseMapper<UserContentPurchase> {

    // =================== C端必需的基础查询方法 ===================

    /**
     * 检查用户是否已购买指定内容
     */
    UserContentPurchase selectByUserIdAndContentId(@Param("userId") Long userId,
                                                   @Param("contentId") Long contentId);

    /**
     * 检查用户是否有权限访问内容（已购买且未过期）
     */
    UserContentPurchase selectValidPurchase(@Param("userId") Long userId,
                                           @Param("contentId") Long contentId);

    /**
     * 查询用户的购买记录列表
     */
    List<UserContentPurchase> selectByUserId(@Param("userId") Long userId,
                                            @Param("offset") Long offset,
                                            @Param("limit") Integer limit);

    /**
     * 查询用户的有效购买记录
     */
    List<UserContentPurchase> selectValidPurchasesByUserId(@Param("userId") Long userId);

    /**
     * 查询内容的购买记录列表
     */
    List<UserContentPurchase> selectByContentId(@Param("contentId") Long contentId,
                                               @Param("offset") Long offset,
                                               @Param("limit") Integer limit);

    /**
     * 根据订单ID查询购买记录
     */
    UserContentPurchase selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单号查询购买记录
     */
    UserContentPurchase selectByOrderNo(@Param("orderNo") String orderNo);

    /**
     * 查询用户指定内容类型的购买记录
     */
    List<UserContentPurchase> selectByUserIdAndContentType(@Param("userId") Long userId,
                                                          @Param("contentType") String contentType);

    /**
     * 查询用户指定作者的购买记录
     */
    List<UserContentPurchase> selectByUserIdAndAuthorId(@Param("userId") Long userId,
                                                        @Param("authorId") Long authorId);

    /**
     * 查询即将过期的购买记录
     */
    List<UserContentPurchase> selectExpiringSoon(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 查询已过期的购买记录
     */
    List<UserContentPurchase> selectExpiredPurchases();

    /**
     * 查询高消费金额的购买记录
     */
    List<UserContentPurchase> selectHighValuePurchases(@Param("minAmount") Long minAmount,
                                                      @Param("limit") Integer limit);

    /**
     * 查询用户的高价值购买记录
     */
    List<UserContentPurchase> selectUserHighValuePurchases(@Param("userId") Long userId,
                                                          @Param("minAmount") Long minAmount);

    /**
     * 查询访问次数最多的购买记录
     */
    List<UserContentPurchase> selectMostAccessedPurchases(@Param("limit") Integer limit);

    /**
     * 查询用户最近访问的购买记录
     */
    List<UserContentPurchase> selectUserRecentAccessedPurchases(@Param("userId") Long userId,
                                                               @Param("limit") Integer limit);

    /**
     * 查询用户最近购买的内容
     */
    List<UserContentPurchase> selectRecentPurchases(@Param("userId") Long userId,
                                                   @Param("limit") Integer limit);

    /**
     * 查询用户购买但未访问的内容
     */
    List<UserContentPurchase> selectUnreadPurchases(@Param("userId") Long userId);

    // =================== C端必需的统计方法 ===================

    /**
     * 统计用户的购买总数
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户有效购买数
     */
    Long countValidByUserId(@Param("userId") Long userId);

    /**
     * 统计内容的购买总数
     */
    Long countByContentId(@Param("contentId") Long contentId);

    /**
     * 统计内容的收入总额
     */
    Long sumRevenueByContentId(@Param("contentId") Long contentId);

    /**
     * 统计用户的消费总额
     */
    Long sumExpenseByUserId(@Param("userId") Long userId);

    /**
     * 获取热门购买内容排行
     */
    List<Map<String, Object>> getPopularContentRanking(@Param("limit") Integer limit);

    /**
     * 获取用户购买统计
     */
    Map<String, Object> getUserPurchaseStats(@Param("userId") Long userId);

    /**
     * 获取内容销售统计
     */
    Map<String, Object> getContentSalesStats(@Param("contentId") Long contentId);

    /**
     * 获取作者收入统计
     */
    Map<String, Object> getAuthorRevenueStats(@Param("authorId") Long authorId);

    /**
     * 获取日期范围内的购买统计
     */
    List<Map<String, Object>> getPurchaseStatsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);

    /**
     * 获取折扣统计信息
     */
    Map<String, Object> getDiscountStats(@Param("userId") Long userId);

    // =================== C端必需的管理方法 ===================

    /**
     * 更新访问统计
     */
    int updateAccessStats(@Param("id") Long id,
                         @Param("accessCount") Integer accessCount,
                         @Param("lastAccessTime") LocalDateTime lastAccessTime);

    /**
     * 批量更新购买记录状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 批量处理过期记录
     */
    int batchExpirePurchases(@Param("beforeTime") LocalDateTime beforeTime);
}