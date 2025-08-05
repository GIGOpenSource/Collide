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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 内容付费配置控制器
 * 提供付费配置管理、查询、统计等REST API接口
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/payment")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容付费管理", description = "内容付费配置的管理、查询和统计接口")
public class ContentPaymentController {

    private final ContentPaymentFacadeService contentPaymentFacadeService;

    // =================== 基础CRUD ===================

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

    @GetMapping("/content/{contentId}")
    @Operation(summary = "获取内容付费配置", description = "根据内容ID获取付费配置")
    public Result<ContentPaymentConfigResponse> getPaymentConfigByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPaymentFacadeService.getPaymentConfigByContentId(contentId);
        } catch (Exception e) {
            log.error("获取内容付费配置API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PAYMENT_CONFIG_API_FAILED", "获取内容付费配置API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除付费配置", description = "删除指定的付费配置")
    public Result<Boolean> deletePaymentConfig(
            @Parameter(description = "配置ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPaymentFacadeService.deletePaymentConfig(id, operatorId);
        } catch (Exception e) {
            log.error("删除付费配置API调用失败: id={}", id, e);
            return Result.error("DELETE_PAYMENT_CONFIG_API_FAILED", "删除付费配置API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/content/{contentId}")
    @Operation(summary = "删除内容付费配置", description = "删除指定内容的付费配置")
    public Result<Boolean> deleteByContentId(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPaymentFacadeService.deleteByContentId(contentId, operatorId);
        } catch (Exception e) {
            log.error("删除内容付费配置API调用失败: contentId={}", contentId, e);
            return Result.error("DELETE_CONTENT_PAYMENT_CONFIG_API_FAILED", "删除内容付费配置API调用失败: " + e.getMessage());
        }
    }

    // =================== 查询功能 ===================

    @GetMapping("/payment-type/{paymentType}")
    @Operation(summary = "按付费类型查询", description = "根据付费类型查询配置列表")
    public Result<List<ContentPaymentConfigResponse>> getConfigsByPaymentType(
            @Parameter(description = "付费类型", required = true)
            @PathVariable String paymentType) {
        try {
            return contentPaymentFacadeService.getConfigsByPaymentType(paymentType);
        } catch (Exception e) {
            log.error("按付费类型查询API调用失败: paymentType={}", paymentType, e);
            return Result.error("GET_CONFIGS_BY_PAYMENT_TYPE_API_FAILED", "按付费类型查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/free")
    @Operation(summary = "查询免费内容配置", description = "分页查询免费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getFreeContentConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getFreeContentConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询免费内容配置API调用失败", e);
            return Result.error("GET_FREE_CONTENT_CONFIGS_API_FAILED", "查询免费内容配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/coin-pay")
    @Operation(summary = "查询金币付费配置", description = "分页查询金币付费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getCoinPayContentConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getCoinPayContentConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询金币付费配置API调用失败", e);
            return Result.error("GET_COIN_PAY_CONFIGS_API_FAILED", "查询金币付费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/vip-free")
    @Operation(summary = "查询VIP免费配置", description = "分页查询VIP免费内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getVipFreeContentConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getVipFreeContentConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询VIP免费配置API调用失败", e);
            return Result.error("GET_VIP_FREE_CONFIGS_API_FAILED", "查询VIP免费配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/vip-only")
    @Operation(summary = "查询VIP专享配置", description = "分页查询VIP专享内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getVipOnlyContentConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getVipOnlyContentConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询VIP专享配置API调用失败", e);
            return Result.error("GET_VIP_ONLY_CONFIGS_API_FAILED", "查询VIP专享配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/price-range")
    @Operation(summary = "按价格范围查询", description = "根据价格范围查询配置")
    public Result<List<ContentPaymentConfigResponse>> getConfigsByPriceRange(
            @Parameter(description = "最低价格")
            @RequestParam(required = false) Long minPrice,
            @Parameter(description = "最高价格")
            @RequestParam(required = false) Long maxPrice) {
        try {
            return contentPaymentFacadeService.getConfigsByPriceRange(minPrice, maxPrice);
        } catch (Exception e) {
            log.error("按价格范围查询API调用失败: minPrice={}, maxPrice={}", minPrice, maxPrice, e);
            return Result.error("GET_CONFIGS_BY_PRICE_RANGE_API_FAILED", "按价格范围查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/trial-enabled")
    @Operation(summary = "查询试读配置", description = "分页查询支持试读的内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getTrialEnabledConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getTrialEnabledConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询试读配置API调用失败", e);
            return Result.error("GET_TRIAL_ENABLED_CONFIGS_API_FAILED", "查询试读配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/permanent")
    @Operation(summary = "查询永久配置", description = "分页查询永久有效的内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getPermanentContentConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getPermanentContentConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询永久配置API调用失败", e);
            return Result.error("GET_PERMANENT_CONFIGS_API_FAILED", "查询永久配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/time-limited")
    @Operation(summary = "查询限时配置", description = "分页查询限时内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getTimeLimitedConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getTimeLimitedConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询限时配置API调用失败", e);
            return Result.error("GET_TIME_LIMITED_CONFIGS_API_FAILED", "查询限时配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/discounted")
    @Operation(summary = "查询折扣配置", description = "分页查询有折扣的内容配置")
    public Result<PageResponse<ContentPaymentConfigResponse>> getDiscountedConfigs(
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPaymentFacadeService.getDiscountedConfigs(currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询折扣配置API调用失败", e);
            return Result.error("GET_DISCOUNTED_CONFIGS_API_FAILED", "查询折扣配置API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "按状态查询配置", description = "根据状态查询配置列表")
    public Result<List<ContentPaymentConfigResponse>> getConfigsByStatus(
            @Parameter(description = "配置状态", required = true)
            @PathVariable String status) {
        try {
            return contentPaymentFacadeService.getConfigsByStatus(status);
        } catch (Exception e) {
            log.error("按状态查询配置API调用失败: status={}", status, e);
            return Result.error("GET_CONFIGS_BY_STATUS_API_FAILED", "按状态查询配置API调用失败: " + e.getMessage());
        }
    }

    // =================== 销售统计管理 ===================

    @PutMapping("/content/{contentId}/sales-stats")
    @Operation(summary = "更新销售统计", description = "更新指定内容的销售统计")
    public Result<Boolean> updateSalesStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "销售增量", required = true)
            @RequestParam Long salesIncrement,
            @Parameter(description = "收入增量", required = true)
            @RequestParam Long revenueIncrement) {
        try {
            return contentPaymentFacadeService.updateSalesStats(contentId, salesIncrement, revenueIncrement);
        } catch (Exception e) {
            log.error("更新销售统计API调用失败: contentId={}", contentId, e);
            return Result.error("UPDATE_SALES_STATS_API_FAILED", "更新销售统计API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/content/{contentId}/reset-sales-stats")
    @Operation(summary = "重置销售统计", description = "重置指定内容的销售统计")
    public Result<Boolean> resetSalesStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPaymentFacadeService.resetSalesStats(contentId);
        } catch (Exception e) {
            log.error("重置销售统计API调用失败: contentId={}", contentId, e);
            return Result.error("RESET_SALES_STATS_API_FAILED", "重置销售统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 状态管理 ===================

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新状态", description = "批量更新付费配置状态")
    public Result<Boolean> batchUpdateStatus(
            @Parameter(description = "内容ID列表", required = true)
            @RequestParam List<Long> contentIds,
            @Parameter(description = "目标状态", required = true)
            @RequestParam String status) {
        try {
            return contentPaymentFacadeService.batchUpdateStatus(contentIds, status);
        } catch (Exception e) {
            log.error("批量更新状态API调用失败: contentIds={}, status={}", contentIds, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/content/{contentId}/enable")
    @Operation(summary = "启用付费配置", description = "启用指定内容的付费配置")
    public Result<Boolean> enablePaymentConfig(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPaymentFacadeService.enablePaymentConfig(contentId, operatorId);
        } catch (Exception e) {
            log.error("启用付费配置API调用失败: contentId={}", contentId, e);
            return Result.error("ENABLE_PAYMENT_CONFIG_API_FAILED", "启用付费配置API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/content/{contentId}/disable")
    @Operation(summary = "禁用付费配置", description = "禁用指定内容的付费配置")
    public Result<Boolean> disablePaymentConfig(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPaymentFacadeService.disablePaymentConfig(contentId, operatorId);
        } catch (Exception e) {
            log.error("禁用付费配置API调用失败: contentId={}", contentId, e);
            return Result.error("DISABLE_PAYMENT_CONFIG_API_FAILED", "禁用付费配置API调用失败: " + e.getMessage());
        }
    }

    // =================== 权限验证 ===================

    @GetMapping("/check-purchase-permission")
    @Operation(summary = "检查购买权限", description = "检查用户是否有权限购买指定内容")
    public Result<Boolean> checkPurchasePermission(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.checkPurchasePermission(userId, contentId);
        } catch (Exception e) {
            log.error("检查购买权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_PURCHASE_PERMISSION_API_FAILED", "检查购买权限API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-free-access")
    @Operation(summary = "检查免费访问权限", description = "检查用户是否可以免费访问指定内容")
    public Result<Boolean> checkFreeAccess(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.checkFreeAccess(userId, contentId);
        } catch (Exception e) {
            log.error("检查免费访问权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_FREE_ACCESS_API_FAILED", "检查免费访问权限API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/access-policy")
    @Operation(summary = "获取访问策略", description = "获取用户对指定内容的访问策略")
    public Result<Map<String, Object>> getAccessPolicy(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.getAccessPolicy(userId, contentId);
        } catch (Exception e) {
            log.error("获取访问策略API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_ACCESS_POLICY_API_FAILED", "获取访问策略API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/calculate-price")
    @Operation(summary = "计算实际价格", description = "计算用户购买指定内容的实际价格")
    public Result<Long> calculateActualPrice(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPaymentFacadeService.calculateActualPrice(userId, contentId);
        } catch (Exception e) {
            log.error("计算实际价格API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_ACTUAL_PRICE_API_FAILED", "计算实际价格API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/price-info")
    @Operation(summary = "获取价格信息", description = "获取指定内容的价格信息")
    public Result<Map<String, Object>> getContentPriceInfo(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPaymentFacadeService.getContentPriceInfo(contentId);
        } catch (Exception e) {
            log.error("获取价格信息API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PRICE_INFO_API_FAILED", "获取价格信息API调用失败: " + e.getMessage());
        }
    }

    // =================== 推荐功能 ===================

    @GetMapping("/hot")
    @Operation(summary = "获取热门付费内容", description = "获取热门付费内容排行")
    public Result<List<ContentPaymentConfigResponse>> getHotPaidContent(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getHotPaidContent(limit);
        } catch (Exception e) {
            log.error("获取热门付费内容API调用失败", e);
            return Result.error("GET_HOT_PAID_CONTENT_API_FAILED", "获取热门付费内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/high-value")
    @Operation(summary = "获取高价值内容", description = "获取高价值内容排行")
    public Result<List<ContentPaymentConfigResponse>> getHighValueContent(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getHighValueContent(limit);
        } catch (Exception e) {
            log.error("获取高价值内容API调用失败", e);
            return Result.error("GET_HIGH_VALUE_CONTENT_API_FAILED", "获取高价值内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/value-for-money")
    @Operation(summary = "获取性价比内容", description = "获取性价比内容排行")
    public Result<List<ContentPaymentConfigResponse>> getValueForMoneyContent(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getValueForMoneyContent(limit);
        } catch (Exception e) {
            log.error("获取性价比内容API调用失败", e);
            return Result.error("GET_VALUE_FOR_MONEY_CONTENT_API_FAILED", "获取性价比内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/new")
    @Operation(summary = "获取新付费内容", description = "获取新上线的付费内容")
    public Result<List<ContentPaymentConfigResponse>> getNewPaidContent(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getNewPaidContent(limit);
        } catch (Exception e) {
            log.error("获取新付费内容API调用失败", e);
            return Result.error("GET_NEW_PAID_CONTENT_API_FAILED", "获取新付费内容API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/sales-ranking")
    @Operation(summary = "获取销售排行榜", description = "获取内容销售排行榜")
    public Result<List<ContentPaymentConfigResponse>> getSalesRanking(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getSalesRanking(limit);
        } catch (Exception e) {
            log.error("获取销售排行榜API调用失败", e);
            return Result.error("GET_SALES_RANKING_API_FAILED", "获取销售排行榜API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/revenue-ranking")
    @Operation(summary = "获取收入排行榜", description = "获取内容收入排行榜")
    public Result<List<ContentPaymentConfigResponse>> getRevenueRanking(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPaymentFacadeService.getRevenueRanking(limit);
        } catch (Exception e) {
            log.error("获取收入排行榜API调用失败", e);
            return Result.error("GET_REVENUE_RANKING_API_FAILED", "获取收入排行榜API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计分析 ===================

    @GetMapping("/stats/payment-type")
    @Operation(summary = "统计付费类型", description = "统计各付费类型的数量")
    public Result<Map<String, Long>> countByPaymentType() {
        try {
            return contentPaymentFacadeService.countByPaymentType();
        } catch (Exception e) {
            log.error("统计付费类型API调用失败", e);
            return Result.error("COUNT_BY_PAYMENT_TYPE_API_FAILED", "统计付费类型API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/active-count")
    @Operation(summary = "统计活跃配置数", description = "统计活跃的付费配置数量")
    public Result<Long> countActiveConfigs() {
        try {
            return contentPaymentFacadeService.countActiveConfigs();
        } catch (Exception e) {
            log.error("统计活跃配置数API调用失败", e);
            return Result.error("COUNT_ACTIVE_CONFIGS_API_FAILED", "统计活跃配置数API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/price")
    @Operation(summary = "获取价格统计", description = "获取价格统计信息")
    public Result<Map<String, Object>> getPriceStats() {
        try {
            return contentPaymentFacadeService.getPriceStats();
        } catch (Exception e) {
            log.error("获取价格统计API调用失败", e);
            return Result.error("GET_PRICE_STATS_API_FAILED", "获取价格统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/total-sales")
    @Operation(summary = "获取总销售统计", description = "获取总销售统计信息")
    public Result<Map<String, Object>> getTotalSalesStats() {
        try {
            return contentPaymentFacadeService.getTotalSalesStats();
        } catch (Exception e) {
            log.error("获取总销售统计API调用失败", e);
            return Result.error("GET_TOTAL_SALES_STATS_API_FAILED", "获取总销售统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/monthly-sales")
    @Operation(summary = "获取月度销售统计", description = "获取近期月度销售统计")
    public Result<List<Map<String, Object>>> getMonthlySalesStats(
            @Parameter(description = "月份数")
            @RequestParam(defaultValue = "12") Integer months) {
        try {
            return contentPaymentFacadeService.getMonthlySalesStats(months);
        } catch (Exception e) {
            log.error("获取月度销售统计API调用失败", e);
            return Result.error("GET_MONTHLY_SALES_STATS_API_FAILED", "获取月度销售统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/conversion")
    @Operation(summary = "获取转化率统计", description = "获取付费转化率统计")
    public Result<Map<String, Object>> getConversionStats() {
        try {
            return contentPaymentFacadeService.getConversionStats();
        } catch (Exception e) {
            log.error("获取转化率统计API调用失败", e);
            return Result.error("GET_CONVERSION_STATS_API_FAILED", "获取转化率统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑 ===================

    @PutMapping("/sync/content/{contentId}/status")
    @Operation(summary = "同步内容状态", description = "同步内容状态到付费配置")
    public Result<Boolean> syncContentStatus(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "内容状态", required = true)
            @RequestParam String contentStatus) {
        try {
            return contentPaymentFacadeService.syncContentStatus(contentId, contentStatus);
        } catch (Exception e) {
            log.error("同步内容状态API调用失败: contentId={}", contentId, e);
            return Result.error("SYNC_CONTENT_STATUS_API_FAILED", "同步内容状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/sync/batch-content-status")
    @Operation(summary = "批量同步内容状态", description = "批量同步内容状态到付费配置")
    public Result<Boolean> batchSyncContentStatus(
            @Parameter(description = "内容状态映射", required = true)
            @RequestBody Map<Long, String> contentStatusMap) {
        try {
            return contentPaymentFacadeService.batchSyncContentStatus(contentStatusMap);
        } catch (Exception e) {
            log.error("批量同步内容状态API调用失败", e);
            return Result.error("BATCH_SYNC_CONTENT_STATUS_API_FAILED", "批量同步内容状态API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/revenue-analysis")
    @Operation(summary = "获取收益分析", description = "获取指定内容的收益分析")
    public Result<Map<String, Object>> getContentRevenueAnalysis(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPaymentFacadeService.getContentRevenueAnalysis(contentId);
        } catch (Exception e) {
            log.error("获取收益分析API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_REVENUE_ANALYSIS_API_FAILED", "获取收益分析API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/price-optimization")
    @Operation(summary = "获取价格优化建议", description = "获取指定内容的价格优化建议")
    public Result<Map<String, Object>> getPriceOptimizationSuggestion(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPaymentFacadeService.getPriceOptimizationSuggestion(contentId);
        } catch (Exception e) {
            log.error("获取价格优化建议API调用失败: contentId={}", contentId, e);
            return Result.error("GET_PRICE_OPTIMIZATION_SUGGESTION_API_FAILED", "获取价格优化建议API调用失败: " + e.getMessage());
        }
    }
}