package com.gig.collide.api.content;

import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 内容付费配置门面服务接口 - 极简版
 * 与ContentPaymentService保持一致，12个核心方法
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPaymentFacadeService {

    // =================== 核心CRUD功能（2个方法）===================

    /**
     * 根据ID获取付费配置
     */
    Result<ContentPaymentConfigResponse> getPaymentConfigById(Long id);

    /**
     * 删除付费配置
     */
    Result<Boolean> deletePaymentConfig(Long id, Long operatorId);

    // =================== 万能查询功能（2个方法）===================

    /**
     * 万能条件查询付费配置列表 - 替代所有具体查询
     * 可实现：getPaymentConfigByContentId, getFreeContentConfigs, getCoinPayContentConfigs等
     * 
     * @param contentId 内容ID（可选）
     * @param paymentType 付费类型（可选：FREE、COIN_PAY、VIP_FREE、VIP_ONLY）
     * @param status 状态（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @param trialEnabled 是否支持试读（可选）
     * @param isPermanent 是否永久（可选）
     * @param hasDiscount 是否有折扣（可选）
     * @param orderBy 排序字段（可选：createTime、price、salesCount、totalRevenue）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     * @return 付费配置列表
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getPaymentsByConditions(
            Long contentId, String paymentType, String status, Long minPrice, Long maxPrice,
            Boolean trialEnabled, Boolean isPermanent, Boolean hasDiscount,
            String orderBy, String orderDirection, Integer currentPage, Integer pageSize);

    /**
     * 推荐付费内容查询 - 替代所有推荐类查询
     * 可实现：getHotPaidContent, getHighValueContent, getValueForMoneyContent, getSalesRanking等
     */
    Result<List<ContentPaymentConfigResponse>> getRecommendedPayments(
            String strategy, String paymentType, List<Long> excludeContentIds, Integer limit);

    // =================== 状态管理功能（2个方法）===================

    /**
     * 更新付费配置状态
     */
    Result<Boolean> updatePaymentStatus(Long configId, String status);

    /**
     * 批量更新状态
     */
    Result<Boolean> batchUpdateStatus(List<Long> ids, String status);

    // =================== 价格管理功能（2个方法）===================

    /**
     * 更新付费配置价格信息
     */
    Result<Boolean> updatePaymentPrice(Long configId, Long price, Long originalPrice,
                                      LocalDateTime discountStartTime, LocalDateTime discountEndTime);

    /**
     * 计算用户实际需要支付的价格
     */
    Result<Long> calculateActualPrice(Long userId, Long contentId);

    // =================== 权限验证功能（1个方法）===================

    /**
     * 检查访问权限（包含购买权限和免费访问检查）
     * 替代：checkPurchasePermission, checkFreeAccess, getAccessPolicy等
     */
    Result<Map<String, Object>> checkAccessPermission(Long userId, Long contentId);

    // =================== 销售统计功能（1个方法）===================

    /**
     * 更新销售统计
     */
    Result<Boolean> updateSalesStats(Long configId, Long salesIncrement, Long revenueIncrement);

    // =================== 统计分析功能（1个方法）===================

    /**
     * 获取付费统计信息 - 替代所有统计分析方法
     * 可实现：countByPaymentType, getPriceStats, getTotalSalesStats, getConversionStats等
     * 
     * @param statsType 统计类型（PAYMENT_TYPE、PRICE、SALES、CONVERSION、REVENUE_ANALYSIS）
     * @param params 统计参数
     * @return 统计结果
     */
    Result<Map<String, Object>> getPaymentStats(String statsType, Map<String, Object> params);

    // =================== 业务逻辑功能（1个方法）===================

    /**
     * 同步内容状态 - 统一业务逻辑处理
     * 可实现：syncContentStatus, batchSyncContentStatus, getPriceOptimizationSuggestion等
     */
    Result<Map<String, Object>> syncContentStatus(String operationType, Map<String, Object> operationData);
}