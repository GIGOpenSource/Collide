package com.gig.collide.api.content;

import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容购买门面服务接口 - 极简版
 * 与UserContentPurchaseService保持一致，12个核心方法
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPurchaseFacadeService {

    // =================== 核心CRUD功能（2个方法）===================

    /**
     * 根据ID获取购买记录
     */
    Result<ContentPurchaseResponse> getPurchaseById(Long id);

    /**
     * 删除购买记录（逻辑删除）
     */
    Result<Boolean> deletePurchase(Long id);

    // =================== 万能查询功能（3个方法）===================

    /**
     * 万能条件查询购买记录列表 - 替代所有具体查询
     * 可实现：getUserPurchases, getContentPurchases, getUserValidPurchases, getUserPurchasesByContentType等
     * 
     * @param userId 用户ID（可选）
     * @param contentId 内容ID（可选）
     * @param contentType 内容类型（可选）
     * @param orderId 订单ID（可选）
     * @param orderNo 订单号（可选）
     * @param status 状态（可选）
     * @param isValid 是否有效（可选，true=未过期，false=已过期）
     * @param minAmount 最小金额（可选）
     * @param maxAmount 最大金额（可选）
     * @param orderBy 排序字段（可选：createTime、purchaseAmount、accessCount）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     * @return 购买记录列表
     */
    Result<PageResponse<ContentPurchaseResponse>> getPurchasesByConditions(
            Long userId, Long contentId, String contentType, Long orderId, String orderNo,
            String status, Boolean isValid, Long minAmount, Long maxAmount,
            String orderBy, String orderDirection, Integer currentPage, Integer pageSize);

    /**
     * 推荐购买记录查询
     */
    Result<List<ContentPurchaseResponse>> getRecommendedPurchases(
            String strategy, Long userId, String contentType,
            List<Long> excludeContentIds, Integer limit);

    /**
     * 过期相关查询 - 替代getExpiredPurchases等
     */
    Result<List<ContentPurchaseResponse>> getPurchasesByExpireStatus(
            String type, LocalDateTime beforeTime, Long userId, Integer limit);

    // =================== 权限验证功能（1个方法）===================

    /**
     * 检查用户是否有权限访问内容（已购买且未过期）
     */
    Result<Boolean> checkAccessPermission(Long userId, Long contentId);

    // =================== 状态管理功能（2个方法）===================

    /**
     * 更新购买记录状态
     */
    Result<Boolean> updatePurchaseStatus(Long purchaseId, String status);

    /**
     * 批量更新购买记录状态
     */
    Result<Boolean> batchUpdateStatus(List<Long> ids, String status);

    // =================== 统计功能（1个方法）===================

    /**
     * 获取购买统计信息 - 替代getDiscountStats、getPurchaseStatsByDateRange等
     */
    Result<Map<String, Object>> getPurchaseStats(String statsType, Map<String, Object> params);

    // =================== 业务逻辑功能（3个方法）===================

    /**
     * 处理内容购买完成
     */
    Result<ContentPurchaseResponse> completePurchase(Long userId, Long contentId, Long orderId, String orderNo,
                                                    Long purchaseAmount, Long originalPrice, LocalDateTime expireTime);

    /**
     * 处理退款 
     */
    Result<Boolean> processRefund(Long purchaseId, String refundReason, Long refundAmount);

    /**
     * 记录内容访问
     */
    Result<Boolean> recordContentAccess(Long userId, Long contentId);
}