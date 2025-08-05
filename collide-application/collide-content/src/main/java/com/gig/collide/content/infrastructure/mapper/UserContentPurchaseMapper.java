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

    // =================== C端必需的核心查询方法 ===================

    /**
     * 检查用户是否已购买指定内容
     */
    UserContentPurchase selectByUserIdAndContentId(@Param("userId") Long userId,
                                                   @Param("contentId") Long contentId);

    /**
     * 根据订单ID查询购买记录
     */
    UserContentPurchase selectByOrderId(@Param("orderId") Long orderId);

    /**
     * 根据订单号查询购买记录
     */
    UserContentPurchase selectByOrderNo(@Param("orderNo") String orderNo);

    // =================== C端必需的通用查询方法 ===================

    /**
     * 通用条件查询购买记录列表
     * @param userId 用户ID（可选）
     * @param contentId 内容ID（可选）
     * @param contentType 内容类型（可选）
     * @param authorId 作者ID（可选）
     * @param status 状态（可选）
     * @param isValid 是否有效（可选，true=未过期，false=已过期）
     * @param minAmount 最小金额（可选）
     * @param maxAmount 最大金额（可选）
     * @param minAccessCount 最小访问次数（可选）
     * @param isUnread 是否未读（可选，true=未访问过，false=已访问）
     * @param orderBy 排序字段（可选：createTime、purchaseAmount、accessCount、lastAccessTime）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     */
    List<UserContentPurchase> selectPurchasesByConditions(@Param("userId") Long userId,
                                                         @Param("contentId") Long contentId,
                                                         @Param("contentType") String contentType,
                                                         @Param("authorId") Long authorId,
                                                         @Param("status") String status,
                                                         @Param("isValid") Boolean isValid,
                                                         @Param("minAmount") Long minAmount,
                                                         @Param("maxAmount") Long maxAmount,
                                                         @Param("minAccessCount") Integer minAccessCount,
                                                         @Param("isUnread") Boolean isUnread,
                                                         @Param("orderBy") String orderBy,
                                                         @Param("orderDirection") String orderDirection,
                                                         @Param("currentPage") Integer currentPage,
                                                         @Param("pageSize") Integer pageSize);

    /**
     * 推荐购买记录查询
     * @param strategy 推荐策略（RECENT、HIGH_VALUE、MOST_ACCESSED、POPULAR）
     * @param userId 用户ID（可选）
     * @param contentType 内容类型筛选（可选）
     * @param excludeContentIds 排除的内容ID列表（可选）
     * @param limit 返回数量限制
     */
    List<UserContentPurchase> selectRecommendedPurchases(@Param("strategy") String strategy,
                                                        @Param("userId") Long userId,
                                                        @Param("contentType") String contentType,
                                                        @Param("excludeContentIds") List<Long> excludeContentIds,
                                                        @Param("limit") Integer limit);

    /**
     * 过期相关查询
     * @param type 查询类型（EXPIRING_SOON、EXPIRED）
     * @param beforeTime 时间界限（EXPIRING_SOON时使用）
     * @param userId 用户ID（可选）
     * @param limit 返回数量限制（可选）
     */
    List<UserContentPurchase> selectByExpireStatus(@Param("type") String type,
                                                  @Param("beforeTime") LocalDateTime beforeTime,
                                                  @Param("userId") Long userId,
                                                  @Param("limit") Integer limit);

    // =================== C端必需的CRUD操作方法 ===================

    /**
     * 更新购买记录状态
     */
    int updatePurchaseStatus(@Param("id") Long id, @Param("status") String status);

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

    /**
     * 软删除购买记录
     */
    int softDeletePurchase(@Param("id") Long id);

    /**
     * 批量软删除购买记录
     */
    int batchSoftDeletePurchases(@Param("ids") List<Long> ids);
}