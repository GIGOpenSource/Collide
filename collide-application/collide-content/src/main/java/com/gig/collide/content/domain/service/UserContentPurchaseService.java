package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.UserContentPurchase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户内容购买记录业务服务接口
 * 极简版 - 12个核心方法，大量使用通用查询
 * 管理用户购买记录、访问权限验证和消费统计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface UserContentPurchaseService {

    // =================== 核心CRUD功能（2个方法）===================

    /**
     * 创建购买记录
     * 验证购买权限、价格信息等
     * 
     * @param purchase 购买记录对象
     * @return 创建的购买记录
     */
    UserContentPurchase createPurchase(UserContentPurchase purchase);

    /**
     * 根据ID获取购买记录
     * 
     * @param id 购买记录ID
     * @return 购买记录
     */
    UserContentPurchase getPurchaseById(Long id);

    // =================== 万能查询功能（3个方法）===================

    /**
     * 万能条件查询购买记录列表
     * 可以替代所有具体查询方法：checkUserPurchase, getUserPurchases, getContentPurchases等
     * 
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
     * @return 购买记录列表
     */
    List<UserContentPurchase> getPurchasesByConditions(Long userId, Long contentId, String contentType,
                                                      Long authorId, String status, Boolean isValid,
                                                      Long minAmount, Long maxAmount, Integer minAccessCount, Boolean isUnread,
                                                      String orderBy, String orderDirection,
                                                      Integer currentPage, Integer pageSize);

    /**
     * 推荐购买记录查询
     * 
     * @param strategy 推荐策略（RECENT、HIGH_VALUE、MOST_ACCESSED、POPULAR）
     * @param userId 用户ID（可选）
     * @param contentType 内容类型筛选（可选）
     * @param excludeContentIds 排除的内容ID列表（可选）
     * @param limit 返回数量限制
     * @return 推荐购买记录列表
     */
    List<UserContentPurchase> getRecommendedPurchases(String strategy, Long userId, String contentType,
                                                     List<Long> excludeContentIds, Integer limit);

    /**
     * 过期相关查询
     * 
     * @param type 查询类型（EXPIRING_SOON、EXPIRED）
     * @param beforeTime 时间界限（EXPIRING_SOON时使用）
     * @param userId 用户ID（可选）
     * @param limit 返回数量限制（可选）
     * @return 购买记录列表
     */
    List<UserContentPurchase> getPurchasesByExpireStatus(String type, LocalDateTime beforeTime,
                                                        Long userId, Integer limit);

    // =================== 权限验证功能（1个方法）===================

    /**
     * 检查用户是否有权限访问内容（已购买且未过期）
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 是否有权限
     */
    boolean checkAccessPermission(Long userId, Long contentId);

    // =================== 状态管理功能（4个方法）===================

    /**
     * 更新购买记录状态（单个）
     * 可处理退款：updatePurchaseStatus(purchaseId, "REFUNDED")
     * 
     * @param purchaseId 购买记录ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updatePurchaseStatus(Long purchaseId, String status);

    /**
     * 批量更新购买记录状态
     * 
     * @param ids 购买记录ID列表
     * @param status 新状态
     * @return 是否成功
     */
    boolean batchUpdateStatus(List<Long> ids, String status);

    /**
     * 处理过期记录
     * 将过期的活跃记录标记为过期状态
     * 
     * @param beforeTime 过期时间界限
     * @return 处理的记录数
     */
    int processExpiredPurchases(LocalDateTime beforeTime);

    /**
     * 软删除购买记录
     * 
     * @param purchaseId 购买记录ID
     * @return 是否成功
     */
    boolean deletePurchase(Long purchaseId);

    // =================== 访问统计功能（1个方法）===================

    /**
     * 记录内容访问
     * 自动增加访问次数并更新最后访问时间
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 是否成功
     */
    boolean recordContentAccess(Long userId, Long contentId);

    // =================== 业务逻辑功能（1个方法）===================

    /**
     * 处理内容购买完成
     * 创建购买记录，更新相关统计
     * 
     * @param userId 用户ID
     * @param contentId 内容ID
     * @param orderId 订单ID
     * @param orderNo 订单号
     * @param purchaseAmount 购买金额
     * @param originalPrice 原价
     * @param expireTime 过期时间（可选）
     * @return 购买记录
     */
    UserContentPurchase completePurchase(Long userId, Long contentId, Long orderId, String orderNo,
                                       Long purchaseAmount, Long originalPrice, LocalDateTime expireTime);
}