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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容付费配置门面服务实现类 - 极简版
 * 专注于付费配置核心功能，12个核心方法
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

    // =================== 核心CRUD功能（2个方法）===================

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

    // =================== 万能查询功能（2个方法）===================

    @Override
    public Result<PageResponse<ContentPaymentConfigResponse>> getPaymentsByConditions(
            Long contentId, String paymentType, String status, Long minPrice, Long maxPrice,
            Boolean trialEnabled, Boolean isPermanent, Boolean hasDiscount,
            String orderBy, String orderDirection, Integer currentPage, Integer pageSize) {
        try {
            log.debug("万能条件查询付费配置: contentId={}, paymentType={}, status={}", 
                     contentId, paymentType, status);
            
            // 调用Service层的万能查询方法
            List<ContentPayment> configs = contentPaymentService.getPaymentsByConditions(
                paymentType, status, minPrice, maxPrice,
                trialEnabled, isPermanent, hasDiscount,
                orderBy, orderDirection, currentPage, pageSize
            );
            
            // 如果有contentId筛选，需要额外过滤（因为Service层万能查询可能没有contentId参数）
            if (contentId != null) {
                configs = configs.stream()
                    .filter(config -> contentId.equals(config.getContentId()))
                    .collect(Collectors.toList());
            }
            
            PageResponse<ContentPaymentConfigResponse> pageResponse = convertToPageResponse(configs, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("万能条件查询付费配置失败", e);
            return Result.error("QUERY_FAILED", "查询付费配置失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPaymentConfigResponse>> getRecommendedPayments(
            String strategy, String paymentType, List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("推荐付费内容查询: strategy={}, paymentType={}", strategy, paymentType);
            
            List<ContentPayment> configs = contentPaymentService.getRecommendedPayments(
                strategy, paymentType, excludeContentIds, limit
            );
            
            if (CollectionUtils.isEmpty(configs)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPaymentConfigResponse> responses = configs.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("推荐付费内容查询失败", e);
            return Result.error("GET_RECOMMENDED_FAILED", "获取推荐付费内容失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个方法）===================

    @Override
    public Result<Boolean> updatePaymentStatus(Long configId, String status) {
        try {
            log.info("更新付费配置状态: configId={}, status={}", configId, status);
            
            boolean result = contentPaymentService.updatePaymentStatus(configId, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_STATUS_FAILED", "更新付费配置状态失败");
            }
        } catch (Exception e) {
            log.error("更新付费配置状态失败", e);
            return Result.error("UPDATE_STATUS_FAILED", "更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchUpdateStatus(List<Long> ids, String status) {
        try {
            log.info("批量更新付费配置状态: ids.size={}, status={}", 
                    ids != null ? ids.size() : 0, status);
            
            boolean result = contentPaymentService.batchUpdateStatus(ids, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("BATCH_UPDATE_FAILED", "批量更新付费配置状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新付费配置状态失败", e);
            return Result.error("BATCH_UPDATE_FAILED", "批量更新失败: " + e.getMessage());
        }
    }

    // =================== 价格管理功能（2个方法）===================

    @Override
    public Result<Boolean> updatePaymentPrice(Long configId, Long price, Long originalPrice,
                                             LocalDateTime discountStartTime, LocalDateTime discountEndTime) {
        try {
            log.info("更新付费配置价格: configId={}, price={}, originalPrice={}", configId, price, originalPrice);
            
            boolean result = contentPaymentService.updatePaymentPrice(configId, price, originalPrice, 
                                                                    discountStartTime, discountEndTime);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_PRICE_FAILED", "更新付费配置价格失败");
            }
        } catch (Exception e) {
            log.error("更新付费配置价格失败", e);
            return Result.error("UPDATE_PRICE_FAILED", "更新价格失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> calculateActualPrice(Long userId, Long contentId) {
        try {
            log.debug("计算实际支付价格: userId={}, contentId={}", userId, contentId);
            
            Long actualPrice = contentPaymentService.calculateActualPrice(userId, contentId);
            
            return Result.success(actualPrice);
        } catch (Exception e) {
            log.error("计算实际支付价格失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_PRICE_FAILED", "计算价格失败: " + e.getMessage());
        }
    }

    // =================== 权限验证功能（1个方法）===================

    @Override
    public Result<Map<String, Object>> checkAccessPermission(Long userId, Long contentId) {
        try {
            log.debug("检查访问权限: userId={}, contentId={}", userId, contentId);
            
            Map<String, Object> result = new HashMap<>();
            
            // 检查基本访问权限
            boolean hasAccess = contentPaymentService.checkAccessPermission(userId, contentId);
            result.put("hasAccess", hasAccess);
            
            // 获取价格信息
            Long actualPrice = contentPaymentService.calculateActualPrice(userId, contentId);
            result.put("actualPrice", actualPrice);
            result.put("isFree", actualPrice == 0);
            
            // 如果没有访问权限且需要付费，设置相关信息
            if (!hasAccess && actualPrice > 0) {
                result.put("needPurchase", true);
                result.put("canPurchase", true); // 简化实现，默认可以购买
            } else {
                result.put("needPurchase", false);
                result.put("canPurchase", false);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("检查访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_ACCESS_FAILED", "检查访问权限失败: " + e.getMessage());
        }
    }

    // =================== 销售统计功能（1个方法）===================

    @Override
    public Result<Boolean> updateSalesStats(Long configId, Long salesIncrement, Long revenueIncrement) {
        try {
            log.info("更新销售统计: configId={}, salesIncrement={}, revenueIncrement={}", 
                    configId, salesIncrement, revenueIncrement);
            
            boolean result = contentPaymentService.updateSalesStats(configId, salesIncrement, revenueIncrement);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_SALES_STATS_FAILED", "更新销售统计失败");
            }
        } catch (Exception e) {
            log.error("更新销售统计失败", e);
            return Result.error("UPDATE_SALES_STATS_FAILED", "更新销售统计失败: " + e.getMessage());
        }
    }

    // =================== 统计分析功能（1个方法）===================

    @Override
    public Result<Map<String, Object>> getPaymentStats(String statsType, Map<String, Object> params) {
        try {
            log.debug("获取付费统计信息: statsType={}", statsType);
            
            Map<String, Object> stats = new HashMap<>();
            
            switch (statsType.toUpperCase()) {
                case "PAYMENT_TYPE":
                    // 按付费类型统计
                    List<ContentPayment> allConfigs = contentPaymentService.getPaymentsByConditions(
                        null, null, null, null, null, null, null,
                        null, null, null, null
                    );
                    Map<String, Long> typeStats = allConfigs.stream()
                        .collect(Collectors.groupingBy(
                            config -> config.getPaymentType() != null ? config.getPaymentType() : "UNKNOWN",
                            Collectors.counting()
                        ));
                    stats.put("paymentTypeStats", typeStats);
                    break;
                case "PRICE":
                    // 价格统计
                    List<ContentPayment> priceConfigs = contentPaymentService.getPaymentsByConditions(
                        null, null, null, null, null, null, null,
                        null, null, null, null
                    );
                    if (!priceConfigs.isEmpty()) {
                        long totalConfigs = priceConfigs.size();
                        long freeCount = priceConfigs.stream().filter(c -> c.getCoinPrice() != null && c.getCoinPrice() == 0).count();
                        stats.put("totalConfigs", totalConfigs);
                        stats.put("freeCount", freeCount);
                        stats.put("paidCount", totalConfigs - freeCount);
                    }
                    break;
                case "SALES":
                    // 销售统计（需要根据实际业务逻辑补充）
                    stats.put("totalSales", 0L);
                    stats.put("totalRevenue", 0L);
                    break;
                case "REVENUE_ANALYSIS":
                    // 收益分析（传入contentId参数）
                    Long contentId = (Long) params.get("contentId");
                    if (contentId != null) {
                        // 使用万能查询获取特定内容的付费配置
                        List<ContentPayment> contentConfigs = contentPaymentService.getPaymentsByConditions(
                            null, "ACTIVE", null, null, null, null, null,
                            null, null, null, null
                        ).stream()
                         .filter(config -> contentId.equals(config.getContentId()))
                         .collect(Collectors.toList());
                        
                        if (!contentConfigs.isEmpty()) {
                            ContentPayment config = contentConfigs.get(0);
                            stats.put("currentPrice", config.getCoinPrice());
                            stats.put("paymentType", config.getPaymentType());
                        }
                    }
                    break;
                default:
                    return Result.error("UNSUPPORTED_STATS_TYPE", "不支持的统计类型: " + statsType);
            }
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取付费统计信息失败", e);
            return Result.error("GET_STATS_FAILED", "获取统计信息失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑功能（1个方法）===================

    @Override
    public Result<Map<String, Object>> syncContentStatus(String operationType, Map<String, Object> operationData) {
        try {
            log.info("同步内容状态: operationType={}", operationType);
            
            Map<String, Object> result = new HashMap<>();
            
            switch (operationType.toUpperCase()) {
                case "SYNC_CONTENT_STATUS":
                    // 同步单个内容状态
                    Long contentId = (Long) operationData.get("contentId");
                    String contentStatus = (String) operationData.get("contentStatus");
                    if (contentId != null && contentStatus != null) {
                        // 根据内容状态更新付费配置状态
                        String paymentStatus = "PUBLISHED".equals(contentStatus) ? "ACTIVE" : "DISABLED";
                        // 这里需要先获取配置ID，再更新状态
                        List<ContentPayment> configs = contentPaymentService.getPaymentsByConditions(
                            null, null, null, null, null, null, null,
                            null, null, null, null
                        ).stream()
                         .filter(config -> contentId.equals(config.getContentId()))
                         .collect(Collectors.toList());
                        
                        boolean success = true;
                        for (ContentPayment config : configs) {
                            success &= contentPaymentService.updatePaymentStatus(config.getId(), paymentStatus);
                        }
                        result.put("success", success);
                        result.put("updatedCount", configs.size());
                    }
                    break;
                case "BATCH_SYNC_CONTENT_STATUS":
                    // 批量同步内容状态
                    @SuppressWarnings("unchecked")
                    Map<Long, String> contentStatusMap = (Map<Long, String>) operationData.get("contentStatusMap");
                    if (contentStatusMap != null) {
                        int totalUpdated = 0;
                        for (Map.Entry<Long, String> entry : contentStatusMap.entrySet()) {
                            // 处理每个内容的状态同步
                            String paymentStatus = "PUBLISHED".equals(entry.getValue()) ? "ACTIVE" : "DISABLED";
                            List<ContentPayment> configs = contentPaymentService.getPaymentsByConditions(
                                null, null, null, null, null, null, null,
                                null, null, null, null
                            ).stream()
                             .filter(config -> entry.getKey().equals(config.getContentId()))
                             .collect(Collectors.toList());
                            
                            for (ContentPayment config : configs) {
                                if (contentPaymentService.updatePaymentStatus(config.getId(), paymentStatus)) {
                                    totalUpdated++;
                                }
                            }
                        }
                        result.put("success", true);
                        result.put("totalUpdated", totalUpdated);
                    }
                    break;
                case "PRICE_OPTIMIZATION":
                    // 价格优化建议
                    Long priceContentId = (Long) operationData.get("contentId");
                    if (priceContentId != null) {
                        // 简化的价格优化建议
                        result.put("suggestion", "KEEP_CURRENT");
                        result.put("reason", "当前价格合适");
                        result.put("recommendedPrice", 0L);
                    }
                    break;
                default:
                    return Result.error("UNSUPPORTED_OPERATION", "不支持的操作类型: " + operationType);
            }
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("同步内容状态失败", e);
            return Result.error("SYNC_FAILED", "同步失败: " + e.getMessage());
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
        
        pageResponse.setCurrentPage(currentPage != null ? currentPage : 1);
        pageResponse.setPageSize(pageSize != null ? pageSize : 20);
        if (pageResponse.getPageSize() > 0) {
            pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageResponse.getPageSize()));
        } else {
            pageResponse.setTotalPage(0);
        }
        pageResponse.setSuccess(true);
        
        return pageResponse;
    }
}