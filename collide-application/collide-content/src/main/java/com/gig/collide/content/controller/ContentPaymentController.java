package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentPaymentFacadeService;
import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容付费配置控制器 - 极简版
 * 基于12个核心Facade方法设计的精简API
 * 
 * @author GIG Team
 * @version 2.0.0 (极简版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/payment")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容付费管理", description = "内容付费配置的管理、查询和统计接口（极简版）")
public class ContentPaymentController {

    private final ContentPaymentFacadeService contentPaymentFacadeService;

    // =================== 核心CRUD功能（2个API）===================

    @GetMapping("/{id}")
    @Operation(summary = "获取付费配置", description = "根据配置ID获取付费配置详情")
    public Result<ContentPaymentConfigResponse> getPaymentConfigById(
            @Parameter(description = "配置ID", required = true)
            @PathVariable Long id) {
        try {
            return contentPaymentFacadeService.getPaymentConfigById(id);
        } catch (Exception e) {
            log.error("获取付费配置API调用失败: id={}", id, e);
            return Result.error("GET_PAYMENT_CONFIG_API_FAILED", "获取付费配置API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除付费配置", description = "删除指定的付费配置")
    public Result<Boolean> deletePaymentConfig(
            @Parameter(description = "配置ID", required = true) @PathVariable Long id,
            @Parameter(description = "操作人ID", required = true) @RequestParam Long operatorId) {
        try {
            return contentPaymentFacadeService.deletePaymentConfig(id, operatorId);
        } catch (Exception e) {
            log.error("删除付费配置API调用失败: id={}", id, e);
            return Result.error("DELETE_PAYMENT_CONFIG_API_FAILED", "删除付费配置API调用失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（2个API）===================

    @GetMapping("/query")
    @Operation(summary = "万能条件查询付费配置", description = "根据多种条件查询付费配置列表，替代所有具体查询API")
    public Result<PageResponse<ContentPaymentConfigResponse>> getPaymentsByConditions(
            @Parameter(description = "内容ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "付费类型") @RequestParam(required = false) String paymentType,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "最小价格") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "最大价格") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "是否支持试读") @RequestParam(required = false) Boolean trialEnabled,
            @Parameter(description = "是否永久") @RequestParam(required = false) Boolean isPermanent,
            @Parameter(description = "是否有折扣") @RequestParam(required = false) Boolean hasDiscount,
            @Parameter(description = "排序字段") @RequestParam(required = false, defaultValue = "createTime") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false, defaultValue = "DESC") String orderDirection,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            return contentPaymentFacadeService.getPaymentsByConditions(
                contentId, paymentType, status, minPrice, maxPrice,
                trialEnabled, isPermanent, hasDiscount,
                orderBy, orderDirection, currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("万能条件查询付费配置API调用失败", e);
            return Result.error("QUERY_PAYMENTS_API_FAILED", "万能条件查询付费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommended")
    @Operation(summary = "推荐付费内容查询", description = "获取推荐的付费内容")
    public Result<List<ContentPaymentConfigResponse>> getRecommendedPayments(
            @Parameter(description = "推荐策略：HOT、HIGH_VALUE、NEW、SALES_RANKING、REVENUE_RANKING", required = true) @RequestParam String strategy,
            @Parameter(description = "付费类型筛选") @RequestParam(required = false) String paymentType,
            @Parameter(description = "排除的内容ID列表") @RequestParam(required = false) List<Long> excludeContentIds,
            @Parameter(description = "返回数量限制", required = true) @RequestParam Integer limit) {
        try {
            return contentPaymentFacadeService.getRecommendedPayments(strategy, paymentType, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("推荐付费内容查询API调用失败", e);
            return Result.error("GET_RECOMMENDED_PAYMENTS_API_FAILED", "推荐付费内容查询API调用失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个API）===================

    @PutMapping("/{configId}/status")
    @Operation(summary = "更新付费配置状态", description = "更新付费配置状态")
    public Result<Boolean> updatePaymentStatus(
            @Parameter(description = "配置ID", required = true) @PathVariable Long configId,
            @Parameter(description = "新状态", required = true) @RequestParam String status) {
        try {
            return contentPaymentFacadeService.updatePaymentStatus(configId, status);
        } catch (Exception e) {
            log.error("更新付费配置状态API调用失败: configId={}, status={}", configId, status, e);
            return Result.error("UPDATE_PAYMENT_STATUS_API_FAILED", "更新付费配置状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新状态", description = "批量更新付费配置状态")
    public Result<Boolean> batchUpdateStatus(
            @Parameter(description = "配置ID列表", required = true) @RequestParam List<Long> ids,
            @Parameter(description = "目标状态", required = true) @RequestParam String status) {
        try {
            return contentPaymentFacadeService.batchUpdateStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新付费配置状态API调用失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新付费配置状态API调用失败: " + e.getMessage());
        }
    }

    // =================== 价格管理功能（2个API）===================

    @PutMapping("/{configId}/price")
    @Operation(summary = "更新付费配置价格", description = "更新付费配置的价格信息")
    public Result<Boolean> updatePaymentPrice(
            @Parameter(description = "配置ID", required = true) @PathVariable Long configId,
            @Parameter(description = "价格", required = true) @RequestParam Long price,
            @Parameter(description = "原价", required = true) @RequestParam Long originalPrice,
            @Parameter(description = "折扣开始时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime discountStartTime,
            @Parameter(description = "折扣结束时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime discountEndTime) {
        try {
            return contentPaymentFacadeService.updatePaymentPrice(configId, price, originalPrice, discountStartTime, discountEndTime);
        } catch (Exception e) {
            log.error("更新付费配置价格API调用失败: configId={}", configId, e);
            return Result.error("UPDATE_PAYMENT_PRICE_API_FAILED", "更新付费配置价格API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/calculate-price")
    @Operation(summary = "计算实际价格", description = "计算用户购买指定内容的实际价格")
    public Result<Long> calculateActualPrice(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.calculateActualPrice(userId, contentId);
        } catch (Exception e) {
            log.error("计算实际价格API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_ACTUAL_PRICE_API_FAILED", "计算实际价格API调用失败: " + e.getMessage());
        }
    }

    // =================== 权限验证功能（1个API）===================

    @GetMapping("/check-access")
    @Operation(summary = "检查访问权限", description = "检查用户对指定内容的访问权限（包含购买权限和免费访问检查）")
    public Result<Map<String, Object>> checkAccessPermission(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.checkAccessPermission(userId, contentId);
        } catch (Exception e) {
            log.error("检查访问权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_ACCESS_PERMISSION_API_FAILED", "检查访问权限API调用失败: " + e.getMessage());
        }
    }

    // =================== 销售统计功能（1个API）===================

    @PutMapping("/{configId}/sales-stats")
    @Operation(summary = "更新销售统计", description = "更新指定配置的销售统计")
    public Result<Boolean> updateSalesStats(
            @Parameter(description = "配置ID", required = true) @PathVariable Long configId,
            @Parameter(description = "销售增量", required = true) @RequestParam Long salesIncrement,
            @Parameter(description = "收入增量", required = true) @RequestParam Long revenueIncrement) {
        try {
            return contentPaymentFacadeService.updateSalesStats(configId, salesIncrement, revenueIncrement);
        } catch (Exception e) {
            log.error("更新销售统计API调用失败: configId={}", configId, e);
            return Result.error("UPDATE_SALES_STATS_API_FAILED", "更新销售统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计分析功能（1个API）===================

    @GetMapping("/stats")
    @Operation(summary = "获取付费统计信息", description = "获取付费统计信息")
    public Result<Map<String, Object>> getPaymentStats(
            @Parameter(description = "统计类型：PAYMENT_TYPE、PRICE、SALES、CONVERSION、REVENUE_ANALYSIS", required = true) @RequestParam String statsType,
            @Parameter(description = "统计参数") @RequestParam Map<String, Object> params) {
        try {
            return contentPaymentFacadeService.getPaymentStats(statsType, params);
        } catch (Exception e) {
            log.error("获取付费统计信息API调用失败: statsType={}", statsType, e);
            return Result.error("GET_PAYMENT_STATS_API_FAILED", "获取付费统计信息API调用失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑功能（1个API）===================

    @PutMapping("/sync")
    @Operation(summary = "同步内容状态", description = "统一业务逻辑处理，包括同步内容状态、价格优化建议等")
    public Result<Map<String, Object>> syncContentStatus(
            @Parameter(description = "操作类型：SYNC_CONTENT_STATUS、BATCH_SYNC_CONTENT_STATUS、PRICE_OPTIMIZATION", required = true) @RequestParam String operationType,
            @Parameter(description = "操作数据", required = true) @RequestBody Map<String, Object> operationData) {
        try {
            return contentPaymentFacadeService.syncContentStatus(operationType, operationData);
        } catch (Exception e) {
            log.error("同步内容状态API调用失败: operationType={}", operationType, e);
            return Result.error("SYNC_CONTENT_STATUS_API_FAILED", "同步内容状态API调用失败: " + e.getMessage());
        }
    }

    // =================== 便民快捷API（基于万能查询实现）===================

    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取内容付费配置", description = "快捷API：根据内容ID获取付费配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getPaymentConfigByContentId(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                contentId, null, null, null, null, null, null, null,
                null, null, null, null
            );
        } catch (Exception e) {
            log.error("获取内容付费配置API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PAYMENT_CONFIG_API_FAILED", "获取内容付费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/payment-type/{paymentType}")
    @Operation(summary = "按付费类型查询", description = "快捷API：根据付费类型查询配置列表")
    public Result<PageResponse<ContentPaymentConfigResponse>> getConfigsByPaymentType(
            @Parameter(description = "付费类型", required = true) @PathVariable String paymentType,
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                null, paymentType, null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("按付费类型查询API调用失败: paymentType={}", paymentType, e);
            return Result.error("GET_CONFIGS_BY_PAYMENT_TYPE_API_FAILED", "按付费类型查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/free")
    @Operation(summary = "查询免费内容配置", description = "快捷API：分页查询免费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getFreeContentConfigs(
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                null, "FREE", null, 0L, 0L, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询免费内容配置API调用失败", e);
            return Result.error("GET_FREE_CONTENT_CONFIGS_API_FAILED", "查询免费内容配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/coin-pay")
    @Operation(summary = "查询金币付费配置", description = "快捷API：分页查询金币付费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getCoinPayContentConfigs(
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                null, "COIN_PAY", null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询金币付费配置API调用失败", e);
            return Result.error("GET_COIN_PAY_CONFIGS_API_FAILED", "查询金币付费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/vip-free")
    @Operation(summary = "查询VIP免费配置", description = "快捷API：分页查询VIP免费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getVipFreeContentConfigs(
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                null, "VIP_FREE", null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询VIP免费配置API调用失败", e);
            return Result.error("GET_VIP_FREE_CONFIGS_API_FAILED", "查询VIP免费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/price-range")
    @Operation(summary = "按价格范围查询", description = "快捷API：根据价格范围查询配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getConfigsByPriceRange(
            @Parameter(description = "最低价格") @RequestParam(required = false) Long minPrice,
            @Parameter(description = "最高价格") @RequestParam(required = false) Long maxPrice,
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPaymentFacadeService.getPaymentsByConditions(
                null, null, null, minPrice, maxPrice, null, null, null,
                "coinPrice", "ASC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("按价格范围查询API调用失败: minPrice={}, maxPrice={}", minPrice, maxPrice, e);
            return Result.error("GET_CONFIGS_BY_PRICE_RANGE_API_FAILED", "按价格范围查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门付费内容", description = "快捷API：获取热门付费内容排行")
    public Result<List<ContentPaymentConfigResponse>> getHotPaidContent(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            // 使用推荐查询实现
            return contentPaymentFacadeService.getRecommendedPayments("HOT", null, null, limit);
        } catch (Exception e) {
            log.error("获取热门付费内容API调用失败", e);
            return Result.error("GET_HOT_PAID_CONTENT_API_FAILED", "获取热门付费内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/high-value")
    @Operation(summary = "获取高价值内容", description = "快捷API：获取高价值内容排行")
    public Result<List<ContentPaymentConfigResponse>> getHighValueContent(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            // 使用推荐查询实现
            return contentPaymentFacadeService.getRecommendedPayments("HIGH_VALUE", null, null, limit);
        } catch (Exception e) {
            log.error("获取高价值内容API调用失败", e);
            return Result.error("GET_HIGH_VALUE_CONTENT_API_FAILED", "获取高价值内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/sales-ranking")
    @Operation(summary = "获取销售排行榜", description = "快捷API：获取内容销售排行榜")
    public Result<List<ContentPaymentConfigResponse>> getSalesRanking(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            // 使用推荐查询实现
            return contentPaymentFacadeService.getRecommendedPayments("SALES_RANKING", null, null, limit);
        } catch (Exception e) {
            log.error("获取销售排行榜API调用失败", e);
            return Result.error("GET_SALES_RANKING_API_FAILED", "获取销售排行榜API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/price-info")
    @Operation(summary = "获取价格信息", description = "快捷API：获取指定内容的价格信息")
    public Result<Map<String, Object>> getContentPriceInfo(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            // 使用统计功能实现
            Map<String, Object> params = new HashMap<>();
            params.put("contentId", contentId);
            return contentPaymentFacadeService.getPaymentStats("REVENUE_ANALYSIS", params);
        } catch (Exception e) {
            log.error("获取价格信息API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PRICE_INFO_API_FAILED", "获取价格信息API调用失败: " + e.getMessage());
        }
    }

    // =================== 传统业务API（基于同步功能实现）===================

    @PutMapping("/sync/content/{contentId}/status")
    @Operation(summary = "同步内容状态", description = "快捷API：同步内容状态到付费配置")
    public Result<Map<String, Object>> syncSingleContentStatus(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "内容状态", required = true) @RequestParam String contentStatus) {
        try {
            // 使用同步功能实现
            Map<String, Object> operationData = new HashMap<>();
            operationData.put("contentId", contentId);
            operationData.put("contentStatus", contentStatus);
            return contentPaymentFacadeService.syncContentStatus("SYNC_CONTENT_STATUS", operationData);
        } catch (Exception e) {
            log.error("同步内容状态API调用失败: contentId={}", contentId, e);
            return Result.error("SYNC_CONTENT_STATUS_API_FAILED", "同步内容状态API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/price-optimization")
    @Operation(summary = "获取价格优化建议", description = "快捷API：获取指定内容的价格优化建议")
    public Result<Map<String, Object>> getPriceOptimizationSuggestion(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId) {
        try {
            // 使用同步功能实现
            Map<String, Object> operationData = new HashMap<>();
            operationData.put("contentId", contentId);
            return contentPaymentFacadeService.syncContentStatus("PRICE_OPTIMIZATION", operationData);
        } catch (Exception e) {
            log.error("获取价格优化建议API调用失败: contentId={}", contentId, e);
            return Result.error("GET_PRICE_OPTIMIZATION_SUGGESTION_API_FAILED", "获取价格优化建议API调用失败: " + e.getMessage());
        }
    }
}