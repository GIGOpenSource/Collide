package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ContentPurchaseRequest;
import com.gig.collide.api.content.request.ContentPurchaseQueryRequest;
import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 内容购买门面服务接口
 * 管理用户的内容购买、权限验证和购买历史
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPurchaseFacadeService {

    // =================== C端必需的权限验证和购买信息方法 ===================

    /**
     * 验证内容购买权限和价格
     * 用于统一购买流程的第一步验证
     *
     * @param request 购买请求
     * @return 验证结果（包含实际价格、权限信息等）
     */
    Result<Map<String, Object>> validateContentPurchase(ContentPurchaseRequest request);

    /**
     * 获取内容购买信息
     * 返回价格、权限、折扣等信息供用户确认
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 购买信息
     */
    Result<Map<String, Object>> getContentPurchaseInfo(Long userId, Long contentId);

    /**
     * 申请试读
     * 获取内容的试读部分
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 试读内容
     */
    Result<Map<String, Object>> requestTrial(Long userId, Long contentId);

    /**
     * 检查用户访问权限
     * 验证用户是否可以访问指定内容
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 权限结果
     */
    Result<Map<String, Object>> checkAccessPermission(Long userId, Long contentId);

    /**
     * 批量检查访问权限
     * 一次性检查用户对多个内容的访问权限
     *
     * @param userId 用户ID
     * @param contentIds 内容ID列表
     * @return 权限结果映射
     */
    Result<Map<Long, Boolean>> batchCheckAccessPermission(Long userId, List<Long> contentIds);

    /**
     * 记录内容访问
     * 用户访问内容时调用，更新访问统计
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 记录结果
     */
    Result<Void> recordContentAccess(Long userId, Long contentId);

    // =================== 购买功能 ===================

    /**
     * 购买内容（已废弃，使用统一下单流程）
     * @deprecated 使用新的统一购买流程，通过Controller直接调用order和wallet服务
     * @param request 购买请求
     * @return 购买结果（包含订单信息或支付链接）
     */
    @Deprecated
    Result<Map<String, Object>> purchaseContent(ContentPurchaseRequest request);

    // =================== 购买记录查询 ===================

    /**
     * 查询用户购买记录
     * 分页查询用户的购买历史
     *
     * @param request 查询请求
     * @return 购买记录列表
     */
    Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(ContentPurchaseQueryRequest request);

    /**
     * 查询内容购买记录
     * 查询指定内容的购买历史（用于作者和管理员）
     *
     * @param request 查询请求
     * @return 购买记录列表
     */
    Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(ContentPurchaseQueryRequest request);

    /**
     * 获取购买记录详情
     * 根据ID获取购买记录的详细信息
     *
     * @param purchaseId 购买记录ID
     * @param userId 用户ID（权限校验）
     * @return 购买记录详情
     */
    Result<ContentPurchaseResponse> getPurchaseDetail(Long purchaseId, Long userId);

    /**
     * 查询用户有效购买
     * 获取用户当前有效的购买记录
     *
     * @param userId 用户ID
     * @return 有效购买列表
     */
    Result<List<ContentPurchaseResponse>> getUserValidPurchases(Long userId);

    /**
     * 查询用户未读购买
     * 获取用户已购买但未访问的内容
     *
     * @param userId 用户ID
     * @return 未读购买列表
     */
    Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(Long userId);

    // =================== 购买统计 ===================

    /**
     * 获取用户购买统计
     * 统计用户的购买数量、消费金额等
     *
     * @param userId 用户ID
     * @return 购买统计信息
     */
    Result<Map<String, Object>> getUserPurchaseStats(Long userId);

    /**
     * 获取内容销售统计
     * 统计内容的销量、收入等
     *
     * @param contentId 内容ID
     * @return 销售统计信息
     */
    Result<Map<String, Object>> getContentSalesStats(Long contentId);

    /**
     * 获取作者收入统计
     * 统计作者的收入情况
     *
     * @param authorId 作者ID
     * @return 收入统计信息
     */
    Result<Map<String, Object>> getAuthorRevenueStats(Long authorId);

    /**
     * 获取热门购买内容排行
     * 获取购买量最高的内容排行榜
     *
     * @param limit 返回数量限制
     * @return 热门内容排行
     */
    Result<List<Map<String, Object>>> getPopularContentRanking(Integer limit);

    // =================== 订单处理 ===================

    /**
     * 处理订单支付成功
     * 订单支付成功后的回调处理
     *
     * @param orderId 订单ID
     * @return 处理结果
     */
    Result<ContentPurchaseResponse> handleOrderPaymentSuccess(Long orderId);

    /**
     * 处理退款申请
     * 处理用户的退款请求
     *
     * @param purchaseId 购买记录ID
     * @param reason 退款原因
     * @param operatorId 操作人ID
     * @return 退款结果
     */
    Result<Void> handleRefundRequest(Long purchaseId, String reason, Long operatorId);

    // =================== 推荐功能 ===================

    /**
     * 获取用户内容推荐
     * 基于购买历史推荐相似内容
     *
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐内容ID列表
     */
    Result<List<Long>> getUserContentRecommendations(Long userId, Integer limit);

    /**
     * 获取购买建议
     * 基于用户行为和偏好给出购买建议
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 购买建议
     */
    Result<Map<String, Object>> getPurchaseSuggestion(Long userId, Long contentId);
}