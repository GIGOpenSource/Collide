package com.gig.collide.api.content;

import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 内容付费配置门面服务接口 - 简化版
 * 与ContentPaymentService保持一致，专注核心功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPaymentFacadeService {

    // =================== 基础CRUD ===================

    /**
     * 根据ID获取付费配置
     */
    Result<ContentPaymentConfigResponse> getPaymentConfigById(Long id);

    /**
     * 根据内容ID获取付费配置
     */
    Result<ContentPaymentConfigResponse> getPaymentConfigByContentId(Long contentId);

    /**
     * 删除付费配置
     */
    Result<Boolean> deletePaymentConfig(Long id, Long operatorId);

    /**
     * 删除内容的付费配置
     */
    Result<Boolean> deleteByContentId(Long contentId, Long operatorId);

    // =================== 查询功能 ===================

    /**
     * 根据付费类型查询配置列表
     */
    Result<List<ContentPaymentConfigResponse>> getConfigsByPaymentType(String paymentType);

    /**
     * 查询免费内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getFreeContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询金币付费内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getCoinPayContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询VIP免费内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getVipFreeContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询VIP专享内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getVipOnlyContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 根据价格范围查询配置
     */
    Result<List<ContentPaymentConfigResponse>> getConfigsByPriceRange(Long minPrice, Long maxPrice);

    /**
     * 查询支持试读的内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getTrialEnabledConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询永久有效的内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getPermanentContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询限时内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getTimeLimitedConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询有折扣的内容配置
     */
    Result<PageResponse<ContentPaymentConfigResponse>> getDiscountedConfigs(Integer currentPage, Integer pageSize);

    /**
     * 根据状态查询配置列表
     */
    Result<List<ContentPaymentConfigResponse>> getConfigsByStatus(String status);

    // =================== 销售统计管理 ===================

    /**
     * 更新销售统计
     */
    Result<Boolean> updateSalesStats(Long contentId, Long salesIncrement, Long revenueIncrement);

    /**
     * 重置销售统计
     */
    Result<Boolean> resetSalesStats(Long contentId);

    // =================== 状态管理 ===================

    /**
     * 批量更新状态
     */
    Result<Boolean> batchUpdateStatus(List<Long> contentIds, String status);

    /**
     * 启用付费配置
     */
    Result<Boolean> enablePaymentConfig(Long contentId, Long operatorId);

    /**
     * 禁用付费配置
     */
    Result<Boolean> disablePaymentConfig(Long contentId, Long operatorId);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否有购买权限
     */
    Result<Boolean> checkPurchasePermission(Long userId, Long contentId);

    /**
     * 检查用户是否可以免费访问
     */
    Result<Boolean> checkFreeAccess(Long userId, Long contentId);

    /**
     * 获取用户对内容的访问策略
     */
    Result<Map<String, Object>> getAccessPolicy(Long userId, Long contentId);

    /**
     * 计算用户实际需要支付的价格
     */
    Result<Long> calculateActualPrice(Long userId, Long contentId);

    /**
     * 获取内容的价格信息
     */
    Result<Map<String, Object>> getContentPriceInfo(Long contentId);

    // =================== 推荐功能 ===================

    /**
     * 获取热门付费内容（按销量排序）
     */
    Result<List<ContentPaymentConfigResponse>> getHotPaidContent(Integer limit);

    /**
     * 获取高价值内容（按单价排序）
     */
    Result<List<ContentPaymentConfigResponse>> getHighValueContent(Integer limit);

    /**
     * 获取性价比内容（按销量/价格比排序）
     */
    Result<List<ContentPaymentConfigResponse>> getValueForMoneyContent(Integer limit);

    /**
     * 获取新上线的付费内容
     */
    Result<List<ContentPaymentConfigResponse>> getNewPaidContent(Integer limit);

    /**
     * 获取销售排行榜
     */
    Result<List<ContentPaymentConfigResponse>> getSalesRanking(Integer limit);

    /**
     * 获取收入排行榜
     */
    Result<List<ContentPaymentConfigResponse>> getRevenueRanking(Integer limit);

    // =================== 统计分析 ===================

    /**
     * 统计各付费类型的数量
     */
    Result<Map<String, Long>> countByPaymentType();

    /**
     * 统计活跃配置数量
     */
    Result<Long> countActiveConfigs();

    /**
     * 获取价格统计信息
     */
    Result<Map<String, Object>> getPriceStats();

    /**
     * 获取总销售统计
     */
    Result<Map<String, Object>> getTotalSalesStats();

    /**
     * 获取月度销售统计
     */
    Result<List<Map<String, Object>>> getMonthlySalesStats(Integer months);

    /**
     * 获取付费转化率统计
     */
    Result<Map<String, Object>> getConversionStats();

    // =================== 业务逻辑 ===================

    /**
     * 同步内容状态
     */
    Result<Boolean> syncContentStatus(Long contentId, String contentStatus);

    /**
     * 批量同步内容状态
     */
    Result<Boolean> batchSyncContentStatus(Map<Long, String> contentStatusMap);

    /**
     * 获取内容收益分析
     */
    Result<Map<String, Object>> getContentRevenueAnalysis(Long contentId);

    /**
     * 获取价格优化建议
     */
    Result<Map<String, Object>> getPriceOptimizationSuggestion(Long contentId);
}