package com.gig.collide.api.content;

import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容购买门面服务接口 - 简化版
 * 与UserContentPurchaseService保持一致，专注核心功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPurchaseFacadeService {

    // =================== 基础CRUD ===================

    /**
     * 根据ID获取购买记录
     */
    Result<ContentPurchaseResponse> getPurchaseById(Long id);

    /**
     * 删除购买记录（逻辑删除）
     */
    Result<Boolean> deletePurchase(Long id, Long operatorId);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否已购买指定内容
     */
    Result<ContentPurchaseResponse> getUserContentPurchase(Long userId, Long contentId);

    /**
     * 检查用户是否有权限访问内容（已购买且未过期）
     */
    Result<Boolean> hasAccessPermission(Long userId, Long contentId);

    /**
     * 获取用户对内容的访问权限详情
     */
    Result<ContentPurchaseResponse> getValidPurchase(Long userId, Long contentId);

    /**
     * 批量检查用户对多个内容的访问权限
     */
    Result<Map<Long, Boolean>> batchCheckAccessPermission(Long userId, List<Long> contentIds);

    // =================== 查询功能 ===================

    /**
     * 查询用户的购买记录列表（分页）
     */
    Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(Long userId, Integer currentPage, Integer pageSize);

    /**
     * 查询用户的有效购买记录
     */
    Result<List<ContentPurchaseResponse>> getUserValidPurchases(Long userId);

    /**
     * 查询内容的购买记录列表（分页）
     */
    Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(Long contentId, Integer currentPage, Integer pageSize);

    /**
     * 根据订单ID查询购买记录
     */
    Result<ContentPurchaseResponse> getPurchaseByOrderId(Long orderId);

    /**
     * 根据订单号查询购买记录
     */
    Result<ContentPurchaseResponse> getPurchaseByOrderNo(String orderNo);

    /**
     * 查询用户指定内容类型的购买记录
     */
    Result<List<ContentPurchaseResponse>> getUserPurchasesByContentType(Long userId, String contentType);

    /**
     * 查询用户指定作者的购买记录
     */
    Result<List<ContentPurchaseResponse>> getUserPurchasesByAuthor(Long userId, Long authorId);

    /**
     * 查询用户最近购买的内容
     */
    Result<List<ContentPurchaseResponse>> getUserRecentPurchases(Long userId, Integer limit);

    /**
     * 查询用户购买但未访问的内容
     */
    Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(Long userId);

    // =================== C端必需的购买记录查询方法 ===================

    /**
     * 查询高消费金额的购买记录
     */
    Result<List<ContentPurchaseResponse>> getHighValuePurchases(Long minAmount, Integer limit);

    /**
     * 查询用户的高价值购买记录
     */
    Result<List<ContentPurchaseResponse>> getUserHighValuePurchases(Long userId, Long minAmount);

    /**
     * 查询访问次数最多的购买记录
     */
    Result<List<ContentPurchaseResponse>> getMostAccessedPurchases(Integer limit);

    /**
     * 查询用户最近访问的购买记录
     */
    Result<List<ContentPurchaseResponse>> getUserRecentAccessedPurchases(Long userId, Integer limit);

    /**
     * 获取折扣统计信息
     */
    Result<Map<String, Object>> getDiscountStats(Long userId);

    // =================== 访问记录管理 ===================

    /**
     * 记录用户访问内容
     */
    Result<Boolean> recordContentAccess(Long userId, Long contentId);

    /**
     * 批量更新访问统计
     */
    Result<Boolean> batchUpdateAccessStats(List<Long> purchaseIds);

    // =================== 状态管理 ===================

    /**
     * 处理过期的购买记录
     */
    Result<Integer> processExpiredPurchases();

    /**
     * 查询即将过期的购买记录
     */
    Result<List<ContentPurchaseResponse>> getExpiringSoonPurchases(LocalDateTime beforeTime);

    /**
     * 查询已过期的购买记录
     */
    Result<List<ContentPurchaseResponse>> getExpiredPurchases();

    /**
     * 批量更新购买记录状态
     */
    Result<Boolean> batchUpdateStatus(List<Long> ids, String status);

    /**
     * 退款处理
     */
    Result<Boolean> refundPurchase(Long purchaseId, String reason, Long operatorId);

    // =================== 统计分析 ===================

    /**
     * 统计用户的购买总数
     */
    Result<Long> countUserPurchases(Long userId);

    /**
     * 统计用户有效购买数
     */
    Result<Long> countUserValidPurchases(Long userId);

    /**
     * 统计内容的购买总数
     */
    Result<Long> countContentPurchases(Long contentId);

    /**
     * 统计内容的收入总额
     */
    Result<Long> sumContentRevenue(Long contentId);

    /**
     * 统计用户的消费总额
     */
    Result<Long> sumUserExpense(Long userId);

    /**
     * 获取热门购买内容排行
     */
    Result<List<Map<String, Object>>> getPopularContentRanking(Integer limit);

    /**
     * 获取用户购买统计
     */
    Result<Map<String, Object>> getUserPurchaseStats(Long userId);

    /**
     * 获取内容销售统计
     */
    Result<Map<String, Object>> getContentSalesStats(Long contentId);

    /**
     * 获取作者收入统计
     */
    Result<Map<String, Object>> getAuthorRevenueStats(Long authorId);

    /**
     * 获取日期范围内的购买统计
     */
    Result<List<Map<String, Object>>> getPurchaseStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // =================== 业务逻辑 ===================

    /**
     * 处理订单支付成功后的购买记录创建
     */
    Result<ContentPurchaseResponse> handleOrderPaymentSuccess(Long orderId);

    /**
     * 验证购买权限
     */
    Result<Boolean> validatePurchasePermission(Long userId, Long contentId);

    /**
     * 计算内容访问权限
     */
    Result<Map<String, Object>> calculateContentAccess(Long userId, Long contentId);

    /**
     * 获取用户的内容推荐
     */
    Result<List<Long>> getUserContentRecommendations(Long userId, Integer limit);
}