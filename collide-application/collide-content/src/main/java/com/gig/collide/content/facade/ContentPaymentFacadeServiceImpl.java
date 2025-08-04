package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentPaymentFacadeService;
import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.content.domain.entity.ContentPayment;
import com.gig.collide.content.domain.service.ContentPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容付费配置门面服务实现类 - 简化版
 * 与ContentPaymentService保持一致，专注核心功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class ContentPaymentFacadeServiceImpl implements ContentPaymentFacadeService {

    private final ContentPaymentService contentPaymentService;

    // =================== 基础CRUD ===================

    @Override
    public Result<ContentPaymentConfigResponse> getPaymentConfigById(Long id) {
        try {
            log.debug("获取付费配置: id={}", id);
            
            ContentPayment config = contentPaymentService.getPaymentConfigById(id);
            
            if (config == null) {
                return Result.error("CONFIG_NOT_FOUND", "付费配置不存在");
            }
            
            ContentPaymentConfigResponse response = convertToResponse(config);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取付费配置失败: id={}", id, e);
            return Result.error("GET_CONFIG_FAILED", "获取付费配置失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentPaymentConfigResponse> getPaymentConfigByContentId(Long contentId) {
        try {
            log.debug("根据内容ID获取付费配置: contentId={}", contentId);
            
            ContentPayment config = contentPaymentService.getPaymentConfigByContentId(contentId);
            
            if (config == null) {
                return Result.error("CONFIG_NOT_FOUND", "内容付费配置不存在");
            }
            
            ContentPaymentConfigResponse response = convertToResponse(config);
            return Result.success(response);
        } catch (Exception e) {
            log.error("根据内容ID获取付费配置失败: contentId={}", contentId, e);
            return Result.error("GET_CONFIG_FAILED", "获取付费配置失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deletePaymentConfig(Long id, Long operatorId) {
        try {
            log.info("删除付费配置: id={}, operatorId={}", id, operatorId);
            
            boolean result = contentPaymentService.deletePaymentConfig(id, operatorId);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("DELETE_FAILED", "删除付费配置失败");
            }
        } catch (Exception e) {
            log.error("删除付费配置失败: id={}", id, e);
            return Result.error("DELETE_FAILED", "删除付费配置失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deleteByContentId(Long contentId, Long operatorId) {
        try {
            log.info("删除内容的付费配置: contentId={}, operatorId={}", contentId, operatorId);
            
            boolean result = contentPaymentService.deleteByContentId(contentId, operatorId);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("DELETE_FAILED", "删除内容付费配置失败");
            }
        } catch (Exception e) {
            log.error("删除内容付费配置失败: contentId={}", contentId, e);
            return Result.error("DELETE_FAILED", "删除内容付费配置失败: " + e.getMessage());
        }
    }

    // =================== 查询功能 ===================

    @Override
    public Result<List<ContentPaymentConfigResponse>> getConfigsByPaymentType(String paymentType) {
        try {
            log.debug("根据付费类型查询配置: paymentType={}", paymentType);
            
            List<ContentPayment> configs = contentPaymentService.getConfigsByPaymentType(paymentType);
            
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据付费类型查询配置失败: paymentType={}", paymentType, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getFreeContentConfigs(Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询免费内容配置: currentPage={}, pageSize={}", currentPage, pageSize);
            
            List<ContentPayment> configs = contentPaymentService.getFreeContentConfigs(currentPage, pageSize);
            
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询免费内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getCoinPayContentConfigs(Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询金币付费内容配置: currentPage={}, pageSize={}", currentPage, pageSize);
            
            List<ContentPayment> configs = contentPaymentService.getCoinPayContentConfigs(currentPage, pageSize);
            
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询金币付费内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getVipFreeContentConfigs(Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询VIP免费内容配置: currentPage={}, pageSize={}", currentPage, pageSize);
            
            List<ContentPayment> configs = contentPaymentService.getVipFreeContentConfigs(currentPage, pageSize);
            
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询VIP免费内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getVipOnlyContentConfigs(Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询VIP专享内容配置: currentPage={}, pageSize={}", currentPage, pageSize);
            
            List<ContentPayment> configs = contentPaymentService.getVipOnlyContentConfigs(currentPage, pageSize);
            
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询VIP专享内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getConfigsByPriceRange(Long minPrice, Long maxPrice) {
        try {
            log.debug("根据价格范围查询配置: minPrice={}, maxPrice={}", minPrice, maxPrice);
            
            List<ContentPayment> configs = contentPaymentService.getConfigsByPriceRange(minPrice, maxPrice);
            
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据价格范围查询配置失败: minPrice={}, maxPrice={}", minPrice, maxPrice, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getTrialEnabledConfigs(Integer currentPage, Integer pageSize) {
        try {
            List<ContentPayment> configs = contentPaymentService.getTrialEnabledConfigs(currentPage, pageSize);
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询支持试读的内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getPermanentContentConfigs(Integer currentPage, Integer pageSize) {
        try {
            List<ContentPayment> configs = contentPaymentService.getPermanentContentConfigs(currentPage, pageSize);
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询永久有效的内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getTimeLimitedConfigs(Integer currentPage, Integer pageSize) {
        try {
            List<ContentPayment> configs = contentPaymentService.getTimeLimitedConfigs(currentPage, pageSize);
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询限时内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getDiscountedConfigs(Integer currentPage, Integer pageSize) {
        try {
            List<ContentPayment> configs = contentPaymentService.getDiscountedConfigs(currentPage, pageSize);
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询有折扣的内容配置失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getConfigsByStatus(String status) {
        try {
            List<ContentPayment> configs = contentPaymentService.getConfigsByStatus(status);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("根据状态查询配置失败: status={}", status, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    // =================== 销售统计管理 ===================

    @Override
    public Result<Boolean> updateSalesStats(Long contentId, Long salesIncrement, Long revenueIncrement) {
        try {
            boolean result = contentPaymentService.updateSalesStats(contentId, salesIncrement, revenueIncrement);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新销售统计失败: contentId={}", contentId, e);
            return Result.error("UPDATE_FAILED", "更新销售统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> resetSalesStats(Long contentId) {
        try {
            boolean result = contentPaymentService.resetSalesStats(contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("重置销售统计失败: contentId={}", contentId, e);
            return Result.error("RESET_FAILED", "重置销售统计失败: " + e.getMessage());
        }
    }

    // =================== 状态管理 ===================

    @Override
    public Result<Boolean> batchUpdateStatus(List<Long> contentIds, String status) {
        try {
            boolean result = contentPaymentService.batchUpdateStatus(contentIds, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新状态失败: contentIds={}, status={}", contentIds, status, e);
            return Result.error("UPDATE_FAILED", "批量更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> enablePaymentConfig(Long contentId, Long operatorId) {
        try {
            boolean result = contentPaymentService.enablePaymentConfig(contentId, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("启用付费配置失败: contentId={}", contentId, e);
            return Result.error("ENABLE_FAILED", "启用付费配置失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> disablePaymentConfig(Long contentId, Long operatorId) {
        try {
            boolean result = contentPaymentService.disablePaymentConfig(contentId, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("禁用付费配置失败: contentId={}", contentId, e);
            return Result.error("DISABLE_FAILED", "禁用付费配置失败: " + e.getMessage());
        }
    }

    // =================== 权限验证 ===================

    @Override
    public Result<Boolean> checkPurchasePermission(Long userId, Long contentId) {
        try {
            boolean result = contentPaymentService.checkPurchasePermission(userId, contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查购买权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_FAILED", "检查购买权限失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> checkFreeAccess(Long userId, Long contentId) {
        try {
            boolean result = contentPaymentService.checkFreeAccess(userId, contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查免费访问失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_FAILED", "检查免费访问失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getAccessPolicy(Long userId, Long contentId) {
        try {
            Map<String, Object> policy = contentPaymentService.getAccessPolicy(userId, contentId);
            return Result.success(policy);
        } catch (Exception e) {
            log.error("获取访问策略失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_POLICY_FAILED", "获取访问策略失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> calculateActualPrice(Long userId, Long contentId) {
        try {
            Long price = contentPaymentService.calculateActualPrice(userId, contentId);
            return Result.success(price);
        } catch (Exception e) {
            log.error("计算实际价格失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_FAILED", "计算实际价格失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getContentPriceInfo(Long contentId) {
        try {
            Map<String, Object> priceInfo = contentPaymentService.getContentPriceInfo(contentId);
            return Result.success(priceInfo);
        } catch (Exception e) {
            log.error("获取内容价格信息失败: contentId={}", contentId, e);
            return Result.error("GET_PRICE_FAILED", "获取内容价格信息失败: " + e.getMessage());
        }
    }

    // =================== 推荐功能 ===================

    @Override
    public Result<List<ContentPaymentConfigResponse>> getHotPaidContent(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getHotPaidContent(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取热门付费内容失败", e);
            return Result.error("GET_HOT_FAILED", "获取热门付费内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getHighValueContent(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getHighValueContent(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取高价值内容失败", e);
            return Result.error("GET_HIGH_VALUE_FAILED", "获取高价值内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getValueForMoneyContent(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getValueForMoneyContent(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取性价比内容失败", e);
            return Result.error("GET_VALUE_FAILED", "获取性价比内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getNewPaidContent(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getNewPaidContent(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取新上线的付费内容失败", e);
            return Result.error("GET_NEW_FAILED", "获取新上线的付费内容失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getSalesRanking(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getSalesRanking(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取销售排行榜失败", e);
            return Result.error("GET_RANKING_FAILED", "获取销售排行榜失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getRevenueRanking(Integer limit) {
        try {
            List<ContentPayment> configs = contentPaymentService.getRevenueRanking(limit);
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("获取收入排行榜失败", e);
            return Result.error("GET_RANKING_FAILED", "获取收入排行榜失败: " + e.getMessage());
        }
    }

    // =================== 统计分析 ===================

    @Override
    public Result<Map<String, Long>> countByPaymentType() {
        try {
            Map<String, Long> stats = contentPaymentService.countByPaymentType();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("统计各付费类型数量失败", e);
            return Result.error("COUNT_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countActiveConfigs() {
        try {
            Long count = contentPaymentService.countActiveConfigs();
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计活跃配置数量失败", e);
            return Result.error("COUNT_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getPriceStats() {
        try {
            Map<String, Object> stats = contentPaymentService.getPriceStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取价格统计失败", e);
            return Result.error("GET_STATS_FAILED", "获取价格统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getTotalSalesStats() {
        try {
            Map<String, Object> stats = contentPaymentService.getTotalSalesStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取总销售统计失败", e);
            return Result.error("GET_STATS_FAILED", "获取总销售统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getMonthlySalesStats(Integer months) {
        try {
            List<Map<String, Object>> stats = contentPaymentService.getMonthlySalesStats(months);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取月度销售统计失败", e);
            return Result.error("GET_STATS_FAILED", "获取月度销售统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getConversionStats() {
        try {
            Map<String, Object> stats = contentPaymentService.getConversionStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取付费转化率统计失败", e);
            return Result.error("GET_STATS_FAILED", "获取付费转化率统计失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑 ===================

    @Override
    public Result<Boolean> syncContentStatus(Long contentId, String contentStatus) {
        try {
            boolean result = contentPaymentService.syncContentStatus(contentId, contentStatus);
            return Result.success(result);
        } catch (Exception e) {
            log.error("同步内容状态失败: contentId={}", contentId, e);
            return Result.error("SYNC_FAILED", "同步内容状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchSyncContentStatus(Map<Long, String> contentStatusMap) {
        try {
            boolean result = contentPaymentService.batchSyncContentStatus(contentStatusMap);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量同步内容状态失败", e);
            return Result.error("SYNC_FAILED", "批量同步内容状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getContentRevenueAnalysis(Long contentId) {
        try {
            Map<String, Object> analysis = contentPaymentService.getContentRevenueAnalysis(contentId);
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取内容收益分析失败: contentId={}", contentId, e);
            return Result.error("ANALYSIS_FAILED", "获取内容收益分析失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getPriceOptimizationSuggestion(Long contentId) {
        try {
            Map<String, Object> suggestion = contentPaymentService.getPriceOptimizationSuggestion(contentId);
            return Result.success(suggestion);
        } catch (Exception e) {
            log.error("获取价格优化建议失败: contentId={}", contentId, e);
            return Result.error("SUGGESTION_FAILED", "获取价格优化建议失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    private ContentPaymentConfigResponse convertToResponse(ContentPayment config) {
        ContentPaymentConfigResponse response = new ContentPaymentConfigResponse();
        BeanUtils.copyProperties(config, response);
        return response;
    }

    private PageResponse<ContentPaymentConfigResponse> convertToPageResponse(List<ContentPayment> configs, Integer currentPage, Integer pageSize) {
        PageResponse<ContentPaymentConfigResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(configs)) {
            pageResponse.setDatas(Collections.emptyList());
            pageResponse.setTotal(0L);
        } else {
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responses);
            pageResponse.setTotal((long) configs.size());
        }
        
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageSize));
        pageResponse.setSuccess(true);
        
        return pageResponse;
    }
}