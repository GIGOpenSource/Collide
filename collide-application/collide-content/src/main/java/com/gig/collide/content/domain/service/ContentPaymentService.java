package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.ContentPayment;

import java.util.List;
import java.util.Map;

/**
 * 内容付费配置业务逻辑接口
 * 管理内容的付费策略、价格配置和销售统计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPaymentService {

    // =================== 基础CRUD ===================

    /**
     * 创建付费配置
     * 验证内容存在性、价格合理性等
     */
    ContentPayment createPaymentConfig(ContentPayment config);

    /**
     * 根据ID获取付费配置
     */
    ContentPayment getPaymentConfigById(Long id);

    /**
     * 根据内容ID获取付费配置
     */
    ContentPayment getPaymentConfigByContentId(Long contentId);

    /**
     * 更新付费配置
     */
    ContentPayment updatePaymentConfig(ContentPayment config);

    /**
     * 删除付费配置
     */
    boolean deletePaymentConfig(Long id, Long operatorId);

    /**
     * 删除内容的付费配置
     */
    boolean deleteByContentId(Long contentId, Long operatorId);

    // =================== 查询功能 ===================

    /**
     * 根据付费类型查询配置列表
     */
    List<ContentPayment> getConfigsByPaymentType(String paymentType);

    /**
     * 查询免费内容配置
     */
    List<ContentPayment> getFreeContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询金币付费内容配置
     */
    List<ContentPayment> getCoinPayContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询VIP免费内容配置
     */
    List<ContentPayment> getVipFreeContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询VIP专享内容配置
     */
    List<ContentPayment> getVipOnlyContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 根据价格范围查询配置
     */
    List<ContentPayment> getConfigsByPriceRange(Long minPrice, Long maxPrice);

    /**
     * 查询支持试读的内容配置
     */
    List<ContentPayment> getTrialEnabledConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询永久有效的内容配置
     */
    List<ContentPayment> getPermanentContentConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询限时内容配置
     */
    List<ContentPayment> getTimeLimitedConfigs(Integer currentPage, Integer pageSize);

    /**
     * 查询有折扣的内容配置
     */
    List<ContentPayment> getDiscountedConfigs(Integer currentPage, Integer pageSize);

    /**
     * 根据状态查询配置列表
     */
    List<ContentPayment> getConfigsByStatus(String status);

    // =================== 销售统计管理 ===================

    /**
     * 更新销售统计
     * 增加销量和收入
     */
    boolean updateSalesStats(Long contentId, Long salesIncrement, Long revenueIncrement);

    /**
     * 批量更新销售统计
     */
    boolean batchUpdateSalesStats(Map<Long, Map<String, Long>> statsMap);

    /**
     * 重置销售统计
     */
    boolean resetSalesStats(Long contentId);

    // =================== 状态管理 ===================

    /**
     * 批量更新状态
     */
    boolean batchUpdateStatus(List<Long> contentIds, String status);

    /**
     * 启用付费配置
     */
    boolean enablePaymentConfig(Long contentId, Long operatorId);

    /**
     * 禁用付费配置
     */
    boolean disablePaymentConfig(Long contentId, Long operatorId);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否有购买权限
     * 考虑VIP专享等限制
     */
    boolean checkPurchasePermission(Long userId, Long contentId);

    /**
     * 检查用户是否可以免费访问
     * 考虑免费内容、VIP免费等情况
     */
    boolean checkFreeAccess(Long userId, Long contentId);

    /**
     * 获取用户对内容的访问策略
     * 返回可访问性、价格、权限等信息
     */
    Map<String, Object> getAccessPolicy(Long userId, Long contentId);

    /**
     * 批量获取用户对多个内容的访问策略
     */
    Map<Long, Map<String, Object>> batchGetAccessPolicy(Long userId, List<Long> contentIds);

    // =================== 价格策略 ===================

    /**
     * 计算用户实际需要支付的价格
     * 考虑折扣、VIP等因素
     */
    Long calculateActualPrice(Long userId, Long contentId);

    /**
     * 获取内容的价格信息
     * 包括原价、现价、折扣等
     */
    Map<String, Object> getContentPriceInfo(Long contentId);

    /**
     * 批量获取内容价格信息
     */
    Map<Long, Map<String, Object>> batchGetContentPriceInfo(List<Long> contentIds);

    // =================== 推荐功能 ===================

    /**
     * 获取热门付费内容（按销量排序）
     */
    List<ContentPayment> getHotPaidContent(Integer limit);

    /**
     * 获取高价值内容（按单价排序）
     */
    List<ContentPayment> getHighValueContent(Integer limit);

    /**
     * 获取性价比内容（按销量/价格比排序）
     */
    List<ContentPayment> getValueForMoneyContent(Integer limit);

    /**
     * 获取新上线的付费内容
     */
    List<ContentPayment> getNewPaidContent(Integer limit);

    /**
     * 获取销售排行榜
     */
    List<ContentPayment> getSalesRanking(Integer limit);

    /**
     * 获取收入排行榜
     */
    List<ContentPayment> getRevenueRanking(Integer limit);

    // =================== 统计分析 ===================

    /**
     * 统计各付费类型的数量
     */
    Map<String, Long> countByPaymentType();

    /**
     * 统计活跃配置数量
     */
    Long countActiveConfigs();

    /**
     * 获取价格统计信息
     */
    Map<String, Object> getPriceStats();

    /**
     * 获取总销售统计
     */
    Map<String, Object> getTotalSalesStats();

    /**
     * 获取月度销售统计
     */
    List<Map<String, Object>> getMonthlySalesStats(Integer months);

    /**
     * 获取付费转化率统计
     */
    Map<String, Object> getConversionStats();

    // =================== 业务逻辑 ===================

    /**
     * 处理内容购买
     * 创建订单前的价格确认和权限验证
     */
    Map<String, Object> handleContentPurchase(Long userId, Long contentId);

    /**
     * 处理试读申请
     */
    Map<String, Object> handleTrialRequest(Long userId, Long contentId);

    /**
     * 同步内容状态
     * 当内容被删除或下架时，同步更新付费配置状态
     */
    boolean syncContentStatus(Long contentId, String contentStatus);

    /**
     * 批量同步内容状态
     */
    boolean batchSyncContentStatus(Map<Long, String> contentStatusMap);

    /**
     * 获取内容收益分析
     * 分析内容的盈利能力和趋势
     */
    Map<String, Object> getContentRevenueAnalysis(Long contentId);

    /**
     * 获取价格优化建议
     * 基于销售数据给出价格调整建议
     */
    Map<String, Object> getPriceOptimizationSuggestion(Long contentId);
}