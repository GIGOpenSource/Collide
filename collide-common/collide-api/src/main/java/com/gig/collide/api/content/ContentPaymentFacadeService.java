package com.gig.collide.api.content;

import com.gig.collide.api.content.request.ContentPaymentConfigRequest;
import com.gig.collide.api.content.request.PaidContentQueryRequest;
import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.api.content.response.PaidContentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 内容付费配置门面服务接口
 * 管理内容的付费策略、价格配置和销售分析
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPaymentFacadeService {

    // =================== 付费配置管理 ===================

    /**
     * 创建付费配置
     * 为内容设置付费策略和价格
     *
     * @param request 配置请求
     * @return 创建结果
     */
    Result<ContentPaymentConfigResponse> createPaymentConfig(ContentPaymentConfigRequest request);

    /**
     * 更新付费配置
     * 修改内容的付费策略
     *
     * @param configId 配置ID
     * @param request 更新请求
     * @return 更新结果
     */
    Result<ContentPaymentConfigResponse> updatePaymentConfig(Long configId, ContentPaymentConfigRequest request);

    /**
     * 删除付费配置
     * 删除内容的付费配置
     *
     * @param configId 配置ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    Result<Void> deletePaymentConfig(Long configId, Long operatorId);

    /**
     * 获取付费配置详情
     * 根据内容ID获取付费配置
     *
     * @param contentId 内容ID
     * @return 配置详情
     */
    Result<ContentPaymentConfigResponse> getPaymentConfigByContentId(Long contentId);

    /**
     * 批量设置付费配置
     * 批量为多个内容设置相同的付费策略
     *
     * @param contentIds 内容ID列表
     * @param request 配置请求
     * @return 设置结果
     */
    Result<Map<String, Object>> batchSetPaymentConfig(List<Long> contentIds, ContentPaymentConfigRequest request);

    // =================== C端必需的价格和权限验证方法 ===================

    /**
     * 获取内容价格信息
     * 获取指定内容的价格详情
     *
     * @param contentId 内容ID
     * @param userId 用户ID（用于个性化价格）
     * @return 价格信息
     */
    Result<Map<String, Object>> getContentPriceInfo(Long contentId, Long userId);

    /**
     * 批量获取价格信息
     * 批量获取多个内容的价格信息
     *
     * @param contentIds 内容ID列表
     * @param userId 用户ID
     * @return 价格信息映射
     */
    Result<Map<Long, Map<String, Object>>> batchGetContentPriceInfo(List<Long> contentIds, Long userId);

    /**
     * 计算用户实际支付价格
     * 考虑VIP折扣、活动优惠等因素
     *
     * @param userId 用户ID
     * @param contentId 内容ID
     * @return 实际价格
     */
    Result<Long> calculateActualPrice(Long userId, Long contentId);

    // =================== 付费内容查询 ===================

    /**
     * 查询付费内容列表
     * 根据付费类型、价格等条件查询内容
     *
     * @param request 查询请求
     * @return 内容列表
     */
    Result<PageResponse<PaidContentResponse>> queryPaidContent(PaidContentQueryRequest request);

    /**
     * 获取免费内容列表
     * 查询所有免费可访问的内容
     *
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID（可选，用于个性化）
     * @return 免费内容列表
     */
    Result<PageResponse<PaidContentResponse>> getFreeContentList(Integer page, Integer size, Long userId);

    /**
     * 获取VIP内容列表
     * 查询VIP用户可免费访问的内容
     *
     * @param page 页码
     * @param size 每页大小
     * @param userId 用户ID
     * @return VIP内容列表
     */
    Result<PageResponse<PaidContentResponse>> getVipContentList(Integer page, Integer size, Long userId);

    /**
     * 获取限时内容列表
     * 查询有时效限制的内容
     *
     * @param page 页码
     * @param size 每页大小
     * @return 限时内容列表
     */
    Result<PageResponse<PaidContentResponse>> getTimeLimitedContentList(Integer page, Integer size);

    /**
     * 获取折扣内容列表
     * 查询当前有折扣的内容
     *
     * @param page 页码
     * @param size 每页大小
     * @return 折扣内容列表
     */
    Result<PageResponse<PaidContentResponse>> getDiscountedContentList(Integer page, Integer size);

    // =================== 推荐功能 ===================

    /**
     * 获取热门付费内容
     * 按销量排序的热门付费内容
     *
     * @param limit 返回数量
     * @return 热门内容列表
     */
    Result<List<PaidContentResponse>> getHotPaidContent(Integer limit);

    /**
     * 获取高价值内容
     * 按价格排序的高价值内容
     *
     * @param limit 返回数量
     * @return 高价值内容列表
     */
    Result<List<PaidContentResponse>> getHighValueContent(Integer limit);

    /**
     * 获取性价比内容
     * 按性价比排序的内容
     *
     * @param limit 返回数量
     * @return 性价比内容列表
     */
    Result<List<PaidContentResponse>> getValueForMoneyContent(Integer limit);

    /**
     * 获取新上线付费内容
     * 最近上线的付费内容
     *
     * @param limit 返回数量
     * @return 新内容列表
     */
    Result<List<PaidContentResponse>> getNewPaidContent(Integer limit);

    // =================== 销售统计 ===================

    /**
     * 获取销售排行榜
     * 按销量排序的内容排行榜
     *
     * @param limit 返回数量
     * @return 销售排行榜
     */
    Result<List<ContentPaymentConfigResponse>> getSalesRanking(Integer limit);

    /**
     * 获取收入排行榜
     * 按收入排序的内容排行榜
     *
     * @param limit 返回数量
     * @return 收入排行榜
     */
    Result<List<ContentPaymentConfigResponse>> getRevenueRanking(Integer limit);

    /**
     * 获取付费统计概览
     * 整体的付费内容统计信息
     *
     * @return 统计概览
     */
    Result<Map<String, Object>> getPaymentStatsOverview();

    /**
     * 获取月度销售统计
     * 按月统计的销售数据
     *
     * @param months 统计月数
     * @return 月度统计
     */
    Result<List<Map<String, Object>>> getMonthlySalesStats(Integer months);

    /**
     * 获取转化率统计
     * 各付费类型的转化率分析
     *
     * @return 转化率统计
     */
    Result<Map<String, Object>> getConversionStats();

    // =================== 配置管理 ===================

    /**
     * 启用付费配置
     * 启用指定内容的付费配置
     *
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> enablePaymentConfig(Long contentId, Long operatorId);

    /**
     * 禁用付费配置
     * 禁用指定内容的付费配置
     *
     * @param contentId 内容ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> disablePaymentConfig(Long contentId, Long operatorId);

    /**
     * 批量更新配置状态
     * 批量启用或禁用付费配置
     *
     * @param contentIds 内容ID列表
     * @param status 目标状态
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Map<String, Object>> batchUpdateConfigStatus(List<Long> contentIds, String status, Long operatorId);

    /**
     * 同步内容状态
     * 当内容状态变更时，同步更新付费配置状态
     *
     * @param contentId 内容ID
     * @param contentStatus 内容状态
     * @return 同步结果
     */
    Result<Void> syncContentStatus(Long contentId, String contentStatus);

    // =================== 分析功能 ===================

    /**
     * 获取内容收益分析
     * 分析指定内容的收益情况和趋势
     *
     * @param contentId 内容ID
     * @return 收益分析
     */
    Result<Map<String, Object>> getContentRevenueAnalysis(Long contentId);

    /**
     * 获取价格优化建议
     * 基于销售数据给出价格调整建议
     *
     * @param contentId 内容ID
     * @return 优化建议
     */
    Result<Map<String, Object>> getPriceOptimizationSuggestion(Long contentId);

    /**
     * 获取市场趋势分析
     * 分析付费内容的市场趋势
     *
     * @return 趋势分析
     */
    Result<Map<String, Object>> getMarketTrendAnalysis();
}