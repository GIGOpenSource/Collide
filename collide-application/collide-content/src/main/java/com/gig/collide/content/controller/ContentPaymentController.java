package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentPaymentFacadeService;
import com.gig.collide.api.content.request.ContentPaymentConfigRequest;
import com.gig.collide.api.content.request.PaidContentQueryRequest;
import com.gig.collide.api.content.response.ContentPaymentConfigResponse;
import com.gig.collide.api.content.response.PaidContentResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 内容付费配置控制器
 * 处理内容的付费策略、价格配置和销售分析
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
@Tag(name = "内容付费配置管理", description = "内容付费配置相关接口")
public class ContentPaymentController {

    private final ContentPaymentFacadeService contentPaymentFacadeService;

    // =================== 付费配置管理 ===================

    @PostMapping("/config")
    @Operation(summary = "创建付费配置", description = "为内容设置付费策略和价格")
    public Result<ContentPaymentConfigResponse> createPaymentConfig(
            @Valid @RequestBody ContentPaymentConfigRequest request) {
        log.info("REST创建付费配置: contentId={}, paymentType={}, price={}", 
                request.getContentId(), request.getPaymentType(), request.getCoinPrice());
        return contentPaymentFacadeService.createPaymentConfig(request);
    }

    @PutMapping("/config/{configId}")
    @Operation(summary = "更新付费配置", description = "修改内容的付费策略")
    public Result<ContentPaymentConfigResponse> updatePaymentConfig(
            @PathVariable("configId") Long configId,
            @Valid @RequestBody ContentPaymentConfigRequest request) {
        log.info("REST更新付费配置: configId={}, paymentType={}", configId, request.getPaymentType());
        return contentPaymentFacadeService.updatePaymentConfig(configId, request);
    }

    @DeleteMapping("/config/{configId}")
    @Operation(summary = "删除付费配置", description = "删除内容的付费配置")
    public Result<Void> deletePaymentConfig(
            @PathVariable("configId") Long configId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST删除付费配置: configId={}, operatorId={}", configId, operatorId);
        return contentPaymentFacadeService.deletePaymentConfig(configId, operatorId);
    }

    @GetMapping("/config/content/{contentId}")
    @Operation(summary = "获取付费配置详情", description = "根据内容ID获取付费配置")
    public Result<ContentPaymentConfigResponse> getPaymentConfigByContentId(
            @PathVariable("contentId") Long contentId) {
        log.debug("REST获取付费配置详情: contentId={}", contentId);
        return contentPaymentFacadeService.getPaymentConfigByContentId(contentId);
    }

    @PostMapping("/config/batch")
    @Operation(summary = "批量设置付费配置", description = "批量为多个内容设置相同的付费策略")
    public Result<Map<String, Object>> batchSetPaymentConfig(
            @Parameter(description = "内容ID列表") @RequestBody List<Long> contentIds,
            @Valid @RequestBody ContentPaymentConfigRequest request) {
        log.info("REST批量设置付费配置: contentCount={}, paymentType={}", 
                contentIds.size(), request.getPaymentType());
        return contentPaymentFacadeService.batchSetPaymentConfig(contentIds, request);
    }

    // =================== 付费内容查询 ===================

    @PostMapping("/content/query")
    @Operation(summary = "查询付费内容列表", description = "根据付费类型、价格等条件查询内容")
    public Result<PageResponse<PaidContentResponse>> queryPaidContent(
            @Valid @RequestBody PaidContentQueryRequest request) {
        log.debug("REST查询付费内容: paymentType={}, page={}", 
                request.getPaymentType(), request.getPage());
        return contentPaymentFacadeService.queryPaidContent(request);
    }

    @GetMapping("/content/free")
    @Operation(summary = "获取免费内容列表", description = "查询所有免费可访问的内容")
    public Result<PageResponse<PaidContentResponse>> getFreeContentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        log.debug("REST获取免费内容列表: page={}, size={}, userId={}", page, size, userId);
        return contentPaymentFacadeService.getFreeContentList(page, size, userId);
    }

    @GetMapping("/content/vip")
    @Operation(summary = "获取VIP内容列表", description = "查询VIP用户可免费访问的内容")
    public Result<PageResponse<PaidContentResponse>> getVipContentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        log.debug("REST获取VIP内容列表: page={}, size={}, userId={}", page, size, userId);
        return contentPaymentFacadeService.getVipContentList(page, size, userId);
    }

    @GetMapping("/content/time-limited")
    @Operation(summary = "获取限时内容列表", description = "查询有时效限制的内容")
    public Result<PageResponse<PaidContentResponse>> getTimeLimitedContentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.debug("REST获取限时内容列表: page={}, size={}", page, size);
        return contentPaymentFacadeService.getTimeLimitedContentList(page, size);
    }

    @GetMapping("/content/discounted")
    @Operation(summary = "获取折扣内容列表", description = "查询当前有折扣的内容")
    public Result<PageResponse<PaidContentResponse>> getDiscountedContentList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.debug("REST获取折扣内容列表: page={}, size={}", page, size);
        return contentPaymentFacadeService.getDiscountedContentList(page, size);
    }

    // =================== 价格策略 ===================

    @GetMapping("/price/content/{contentId}")
    @Operation(summary = "获取内容价格信息", description = "获取指定内容的价格详情")
    public Result<Map<String, Object>> getContentPriceInfo(
            @PathVariable("contentId") Long contentId,
            @Parameter(description = "用户ID（用于个性化价格）") @RequestParam(required = false) Long userId) {
        log.debug("REST获取内容价格信息: contentId={}, userId={}", contentId, userId);
        return contentPaymentFacadeService.getContentPriceInfo(contentId, userId);
    }

    @PostMapping("/price/batch")
    @Operation(summary = "批量获取价格信息", description = "批量获取多个内容的价格信息")
    public Result<Map<Long, Map<String, Object>>> batchGetContentPriceInfo(
            @Parameter(description = "内容ID列表") @RequestBody List<Long> contentIds,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId) {
        log.debug("REST批量获取价格信息: contentCount={}, userId={}", contentIds.size(), userId);
        return contentPaymentFacadeService.batchGetContentPriceInfo(contentIds, userId);
    }

    @GetMapping("/price/calculate")
    @Operation(summary = "计算用户实际支付价格", description = "考虑VIP折扣、活动优惠等因素")
    public Result<Long> calculateActualPrice(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        log.debug("REST计算实际支付价格: userId={}, contentId={}", userId, contentId);
        return contentPaymentFacadeService.calculateActualPrice(userId, contentId);
    }

    // =================== 推荐功能 ===================

    @GetMapping("/recommend/hot")
    @Operation(summary = "获取热门付费内容", description = "按销量排序的热门付费内容")
    public Result<List<PaidContentResponse>> getHotPaidContent(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取热门付费内容: limit={}", limit);
        return contentPaymentFacadeService.getHotPaidContent(limit);
    }

    @GetMapping("/recommend/high-value")
    @Operation(summary = "获取高价值内容", description = "按价格排序的高价值内容")
    public Result<List<PaidContentResponse>> getHighValueContent(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取高价值内容: limit={}", limit);
        return contentPaymentFacadeService.getHighValueContent(limit);
    }

    @GetMapping("/recommend/value-for-money")
    @Operation(summary = "获取性价比内容", description = "按性价比排序的内容")
    public Result<List<PaidContentResponse>> getValueForMoneyContent(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取性价比内容: limit={}", limit);
        return contentPaymentFacadeService.getValueForMoneyContent(limit);
    }

    @GetMapping("/recommend/new")
    @Operation(summary = "获取新上线付费内容", description = "最近上线的付费内容")
    public Result<List<PaidContentResponse>> getNewPaidContent(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取新上线付费内容: limit={}", limit);
        return contentPaymentFacadeService.getNewPaidContent(limit);
    }

    // =================== 销售统计 ===================

    @GetMapping("/stats/sales-ranking")
    @Operation(summary = "获取销售排行榜", description = "按销量排序的内容排行榜")
    public Result<List<ContentPaymentConfigResponse>> getSalesRanking(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取销售排行榜: limit={}", limit);
        return contentPaymentFacadeService.getSalesRanking(limit);
    }

    @GetMapping("/stats/revenue-ranking")
    @Operation(summary = "获取收入排行榜", description = "按收入排序的内容排行榜")
    public Result<List<ContentPaymentConfigResponse>> getRevenueRanking(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取收入排行榜: limit={}", limit);
        return contentPaymentFacadeService.getRevenueRanking(limit);
    }

    @GetMapping("/stats/overview")
    @Operation(summary = "获取付费统计概览", description = "整体的付费内容统计信息")
    public Result<Map<String, Object>> getPaymentStatsOverview() {
        log.debug("REST获取付费统计概览");
        return contentPaymentFacadeService.getPaymentStatsOverview();
    }

    @GetMapping("/stats/monthly")
    @Operation(summary = "获取月度销售统计", description = "按月统计的销售数据")
    public Result<List<Map<String, Object>>> getMonthlySalesStats(
            @Parameter(description = "统计月数") @RequestParam(defaultValue = "12") Integer months) {
        log.debug("REST获取月度销售统计: months={}", months);
        return contentPaymentFacadeService.getMonthlySalesStats(months);
    }

    @GetMapping("/stats/conversion")
    @Operation(summary = "获取转化率统计", description = "各付费类型的转化率分析")
    public Result<Map<String, Object>> getConversionStats() {
        log.debug("REST获取转化率统计");
        return contentPaymentFacadeService.getConversionStats();
    }

    // =================== 配置管理 ===================

    @PutMapping("/config/content/{contentId}/enable")
    @Operation(summary = "启用付费配置", description = "启用指定内容的付费配置")
    public Result<Void> enablePaymentConfig(
            @PathVariable("contentId") Long contentId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST启用付费配置: contentId={}, operatorId={}", contentId, operatorId);
        return contentPaymentFacadeService.enablePaymentConfig(contentId, operatorId);
    }

    @PutMapping("/config/content/{contentId}/disable")
    @Operation(summary = "禁用付费配置", description = "禁用指定内容的付费配置")
    public Result<Void> disablePaymentConfig(
            @PathVariable("contentId") Long contentId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST禁用付费配置: contentId={}, operatorId={}", contentId, operatorId);
        return contentPaymentFacadeService.disablePaymentConfig(contentId, operatorId);
    }

    @PutMapping("/config/batch/status")
    @Operation(summary = "批量更新配置状态", description = "批量启用或禁用付费配置")
    public Result<Map<String, Object>> batchUpdateConfigStatus(
            @Parameter(description = "内容ID列表") @RequestBody List<Long> contentIds,
            @Parameter(description = "目标状态") @RequestParam String status,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST批量更新配置状态: contentCount={}, status={}, operatorId={}", 
                contentIds.size(), status, operatorId);
        return contentPaymentFacadeService.batchUpdateConfigStatus(contentIds, status, operatorId);
    }

    @PutMapping("/config/sync/content/{contentId}")
    @Operation(summary = "同步内容状态", description = "当内容状态变更时，同步更新付费配置状态")
    public Result<Void> syncContentStatus(
            @PathVariable("contentId") Long contentId,
            @Parameter(description = "内容状态") @RequestParam String contentStatus) {
        log.info("REST同步内容状态: contentId={}, contentStatus={}", contentId, contentStatus);
        return contentPaymentFacadeService.syncContentStatus(contentId, contentStatus);
    }

    // =================== 分析功能 ===================

    @GetMapping("/analysis/revenue/content/{contentId}")
    @Operation(summary = "获取内容收益分析", description = "分析指定内容的收益情况和趋势")
    public Result<Map<String, Object>> getContentRevenueAnalysis(
            @PathVariable("contentId") Long contentId) {
        log.debug("REST获取内容收益分析: contentId={}", contentId);
        return contentPaymentFacadeService.getContentRevenueAnalysis(contentId);
    }

    @GetMapping("/analysis/price-optimization/content/{contentId}")
    @Operation(summary = "获取价格优化建议", description = "基于销售数据给出价格调整建议")
    public Result<Map<String, Object>> getPriceOptimizationSuggestion(
            @PathVariable("contentId") Long contentId) {
        log.debug("REST获取价格优化建议: contentId={}", contentId);
        return contentPaymentFacadeService.getPriceOptimizationSuggestion(contentId);
    }

    @GetMapping("/analysis/market-trend")
    @Operation(summary = "获取市场趋势分析", description = "分析付费内容的市场趋势")
    public Result<Map<String, Object>> getMarketTrendAnalysis() {
        log.debug("REST获取市场趋势分析");
        return contentPaymentFacadeService.getMarketTrendAnalysis();
    }
}