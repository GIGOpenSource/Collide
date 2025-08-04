package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.UserContentPurchase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户内容购买服务接口
 * 管理用户的内容购买、权限验证和购买历史
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface UserContentPurchaseService {

    // =================== 基础CRUD ===================

    /**
     * 创建购买记录
     * 验证用户权限、内容存在性、重复购买等
     */
    UserContentPurchase createPurchase(UserContentPurchase purchase);

    /**
     * 根据ID获取购买记录
     */
    UserContentPurchase getPurchaseById(Long id);

    /**
     * 更新购买记录
     */
    UserContentPurchase updatePurchase(UserContentPurchase purchase);

    /**
     * 删除购买记录（逻辑删除）
     */
    boolean deletePurchase(Long id, Long operatorId);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否已购买指定内容
     */
    UserContentPurchase getUserContentPurchase(Long userId, Long contentId);

    /**
     * 检查用户是否有权限访问内容（已购买且未过期）
     */
    boolean hasAccessPermission(Long userId, Long contentId);

    /**
     * 获取用户对内容的访问权限详情
     */
    UserContentPurchase getValidPurchase(Long userId, Long contentId);

    /**
     * 批量检查用户对多个内容的访问权限
     */
    Map<Long, Boolean> batchCheckAccessPermission(Long userId, List<Long> contentIds);

    // =================== 查询功能 ===================

    /**
     * 查询用户的购买记录列表（分页）
     */
    List<UserContentPurchase> getUserPurchases(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询用户的有效购买记录
     */
    List<UserContentPurchase> getUserValidPurchases(Long userId);

    /**
     * 查询内容的购买记录列表（分页）
     */
    List<UserContentPurchase> getContentPurchases(Long contentId, Integer currentPage, Integer pageSize);

    /**
     * 根据订单ID查询购买记录
     */
    UserContentPurchase getPurchaseByOrderId(Long orderId);

    /**
     * 根据订单号查询购买记录
     */
    UserContentPurchase getPurchaseByOrderNo(String orderNo);

    /**
     * 查询用户指定内容类型的购买记录
     */
    List<UserContentPurchase> getUserPurchasesByContentType(Long userId, String contentType);

    /**
     * 查询用户指定作者的购买记录
     */
    List<UserContentPurchase> getUserPurchasesByAuthor(Long userId, Long authorId);

    /**
     * 查询用户最近购买的内容
     */
    List<UserContentPurchase> getUserRecentPurchases(Long userId, Integer limit);

    /**
     * 查询用户购买但未访问的内容
     */
    List<UserContentPurchase> getUserUnreadPurchases(Long userId);

    // =================== C端必需的购买记录查询方法 ===================

    /**
     * 查询高消费金额的购买记录
     * 
     * @param minAmount 最小金额
     * @param limit 数量限制
     * @return 高价值购买记录列表
     */
    List<UserContentPurchase> getHighValuePurchases(Long minAmount, Integer limit);

    /**
     * 查询用户的高价值购买记录
     * 
     * @param userId 用户ID
     * @param minAmount 最小金额
     * @return 用户高价值购买记录列表
     */
    List<UserContentPurchase> getUserHighValuePurchases(Long userId, Long minAmount);

    /**
     * 查询访问次数最多的购买记录
     * 
     * @param limit 数量限制
     * @return 访问次数最多的购买记录列表
     */
    List<UserContentPurchase> getMostAccessedPurchases(Integer limit);

    /**
     * 查询用户最近访问的购买记录
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 用户最近访问的购买记录列表
     */
    List<UserContentPurchase> getUserRecentAccessedPurchases(Long userId, Integer limit);

    /**
     * 获取折扣统计信息
     * 
     * @param userId 用户ID
     * @return 折扣统计信息
     */
    Map<String, Object> getDiscountStats(Long userId);

    // =================== 访问记录管理 ===================

    /**
     * 记录用户访问内容
     * 更新访问次数和最后访问时间
     */
    boolean recordContentAccess(Long userId, Long contentId);

    /**
     * 批量更新访问统计
     */
    boolean batchUpdateAccessStats(List<Long> purchaseIds);

    // =================== 状态管理 ===================

    /**
     * 处理过期的购买记录
     */
    int processExpiredPurchases();

    /**
     * 查询即将过期的购买记录
     */
    List<UserContentPurchase> getExpiringSoonPurchases(LocalDateTime beforeTime);

    /**
     * 查询已过期的购买记录
     */
    List<UserContentPurchase> getExpiredPurchases();

    /**
     * 批量更新购买记录状态
     */
    boolean batchUpdateStatus(List<Long> ids, String status);

    /**
     * 退款处理
     */
    boolean refundPurchase(Long purchaseId, String reason, Long operatorId);

    // =================== 统计分析 ===================

    /**
     * 统计用户的购买总数
     */
    Long countUserPurchases(Long userId);

    /**
     * 统计用户有效购买数
     */
    Long countUserValidPurchases(Long userId);

    /**
     * 统计内容的购买总数
     */
    Long countContentPurchases(Long contentId);

    /**
     * 统计内容的收入总额
     */
    Long sumContentRevenue(Long contentId);

    /**
     * 统计用户的消费总额
     */
    Long sumUserExpense(Long userId);

    /**
     * 获取热门购买内容排行
     */
    List<Map<String, Object>> getPopularContentRanking(Integer limit);

    /**
     * 获取用户购买统计
     */
    Map<String, Object> getUserPurchaseStats(Long userId);

    /**
     * 获取内容销售统计
     */
    Map<String, Object> getContentSalesStats(Long contentId);

    /**
     * 获取作者收入统计
     */
    Map<String, Object> getAuthorRevenueStats(Long authorId);

    /**
     * 获取日期范围内的购买统计
     */
    List<Map<String, Object>> getPurchaseStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // =================== 业务逻辑 ===================

    /**
     * 处理订单支付成功后的购买记录创建
     */
    UserContentPurchase handleOrderPaymentSuccess(Long orderId);

    /**
     * 验证购买权限
     * 检查用户是否为VIP、内容是否VIP专享等
     */
    boolean validatePurchasePermission(Long userId, Long contentId);

    /**
     * 计算内容访问权限
     * 综合考虑购买状态、VIP权限、试读权限等
     */
    Map<String, Object> calculateContentAccess(Long userId, Long contentId);

    /**
     * 获取用户的内容推荐
     * 基于购买历史推荐相似内容
     */
    List<Long> getUserContentRecommendations(Long userId, Integer limit);
}