package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentPurchaseFacadeService;
import com.gig.collide.api.content.response.ContentPurchaseResponse;
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
import java.util.List;
import java.util.Map;

/**
 * 内容购买管理控制器
 * 提供购买记录管理、权限验证、统计分析等REST API接口
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/purchase")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容购买管理", description = "用户内容购买记录的管理、查询和统计接口")
public class ContentPurchaseController {

    private final ContentPurchaseFacadeService contentPurchaseFacadeService;

    // =================== 基础CRUD ===================

    @GetMapping("/{id}")
    @Operation(summary = "获取购买记录", description = "根据购买记录ID获取详情")
    public Result<ContentPurchaseResponse> getPurchaseById(
            @Parameter(description = "购买记录ID", required = true)
            @PathVariable Long id) {
        try {
            return contentPurchaseFacadeService.getPurchaseById(id);
        } catch (Exception e) {
            log.error("获取购买记录API调用失败: id={}", id, e);
            return Result.error("GET_PURCHASE_API_FAILED", "获取购买记录API调用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除购买记录", description = "逻辑删除指定的购买记录")
    public Result<Boolean> deletePurchase(
            @Parameter(description = "购买记录ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPurchaseFacadeService.deletePurchase(id, operatorId);
        } catch (Exception e) {
            log.error("删除购买记录API调用失败: id={}", id, e);
            return Result.error("DELETE_PURCHASE_API_FAILED", "删除购买记录API调用失败: " + e.getMessage());
        }
    }

    // =================== 权限验证 ===================

    @GetMapping("/user/{userId}/content/{contentId}")
    @Operation(summary = "获取用户内容购买记录", description = "获取用户对指定内容的购买记录")
    public Result<ContentPurchaseResponse> getUserContentPurchase(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPurchaseFacadeService.getUserContentPurchase(userId, contentId);
        } catch (Exception e) {
            log.error("获取用户内容购买记录API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_USER_CONTENT_PURCHASE_API_FAILED", "获取用户内容购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-access")
    @Operation(summary = "检查访问权限", description = "检查用户是否有权限访问指定内容")
    public Result<Boolean> hasAccessPermission(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.hasAccessPermission(userId, contentId);
        } catch (Exception e) {
            log.error("检查访问权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_ACCESS_PERMISSION_API_FAILED", "检查访问权限API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/valid-purchase")
    @Operation(summary = "获取有效购买记录", description = "获取用户对指定内容的有效购买记录")
    public Result<ContentPurchaseResponse> getValidPurchase(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.getValidPurchase(userId, contentId);
        } catch (Exception e) {
            log.error("获取有效购买记录API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_VALID_PURCHASE_API_FAILED", "获取有效购买记录API调用失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch-check-access")
    @Operation(summary = "批量检查访问权限", description = "批量检查用户对多个内容的访问权限")
    public Result<Map<Long, Boolean>> batchCheckAccessPermission(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID列表", required = true)
            @RequestBody List<Long> contentIds) {
        try {
            return contentPurchaseFacadeService.batchCheckAccessPermission(userId, contentIds);
        } catch (Exception e) {
            log.error("批量检查访问权限API调用失败: userId={}", userId, e);
            return Result.error("BATCH_CHECK_ACCESS_PERMISSION_API_FAILED", "批量检查访问权限API调用失败: " + e.getMessage());
        }
    }

    // =================== 查询功能 ===================

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户购买记录", description = "分页查询用户的购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPurchaseFacadeService.getUserPurchases(userId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询用户购买记录API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_PURCHASES_API_FAILED", "查询用户购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/valid")
    @Operation(summary = "查询用户有效购买记录", description = "查询用户的有效购买记录")
    public Result<List<ContentPurchaseResponse>> getUserValidPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.getUserValidPurchases(userId);
        } catch (Exception e) {
            log.error("查询用户有效购买记录API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_VALID_PURCHASES_API_FAILED", "查询用户有效购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}")
    @Operation(summary = "查询内容购买记录", description = "分页查询指定内容的购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId,
            @Parameter(description = "当前页码", required = true)
            @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true)
            @RequestParam Integer pageSize) {
        try {
            return contentPurchaseFacadeService.getContentPurchases(contentId, currentPage, pageSize);
        } catch (Exception e) {
            log.error("查询内容购买记录API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PURCHASES_API_FAILED", "查询内容购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "根据订单ID查询", description = "根据订单ID查询购买记录")
    public Result<ContentPurchaseResponse> getPurchaseByOrderId(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long orderId) {
        try {
            return contentPurchaseFacadeService.getPurchaseByOrderId(orderId);
        } catch (Exception e) {
            log.error("根据订单ID查询购买记录API调用失败: orderId={}", orderId, e);
            return Result.error("GET_PURCHASE_BY_ORDER_ID_API_FAILED", "根据订单ID查询购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/order-no/{orderNo}")
    @Operation(summary = "根据订单号查询", description = "根据订单号查询购买记录")
    public Result<ContentPurchaseResponse> getPurchaseByOrderNo(
            @Parameter(description = "订单号", required = true)
            @PathVariable String orderNo) {
        try {
            return contentPurchaseFacadeService.getPurchaseByOrderNo(orderNo);
        } catch (Exception e) {
            log.error("根据订单号查询购买记录API调用失败: orderNo={}", orderNo, e);
            return Result.error("GET_PURCHASE_BY_ORDER_NO_API_FAILED", "根据订单号查询购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/content-type/{contentType}")
    @Operation(summary = "查询用户指定类型购买", description = "查询用户购买的指定类型内容")
    public Result<List<ContentPurchaseResponse>> getUserPurchasesByContentType(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "内容类型", required = true)
            @PathVariable String contentType) {
        try {
            return contentPurchaseFacadeService.getUserPurchasesByContentType(userId, contentType);
        } catch (Exception e) {
            log.error("查询用户指定类型购买API调用失败: userId={}, contentType={}", userId, contentType, e);
            return Result.error("GET_USER_PURCHASES_BY_CONTENT_TYPE_API_FAILED", "查询用户指定类型购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/author/{authorId}")
    @Operation(summary = "查询用户指定作者购买", description = "查询用户购买的指定作者内容")
    public Result<List<ContentPurchaseResponse>> getUserPurchasesByAuthor(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "作者ID", required = true)
            @PathVariable Long authorId) {
        try {
            return contentPurchaseFacadeService.getUserPurchasesByAuthor(userId, authorId);
        } catch (Exception e) {
            log.error("查询用户指定作者购买API调用失败: userId={}, authorId={}", userId, authorId, e);
            return Result.error("GET_USER_PURCHASES_BY_AUTHOR_API_FAILED", "查询用户指定作者购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/recent")
    @Operation(summary = "查询用户最近购买", description = "查询用户最近的购买记录")
    public Result<List<ContentPurchaseResponse>> getUserRecentPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getUserRecentPurchases(userId, limit);
        } catch (Exception e) {
            log.error("查询用户最近购买API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_RECENT_PURCHASES_API_FAILED", "查询用户最近购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "查询用户未读购买", description = "查询用户购买但未阅读的内容")
    public Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.getUserUnreadPurchases(userId);
        } catch (Exception e) {
            log.error("查询用户未读购买API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_UNREAD_PURCHASES_API_FAILED", "查询用户未读购买API调用失败: " + e.getMessage());
        }
    }

    // =================== C端必需的购买记录查询方法 ===================

    @GetMapping("/high-value")
    @Operation(summary = "查询高价值购买记录", description = "查询高消费金额的购买记录")
    public Result<List<ContentPurchaseResponse>> getHighValuePurchases(
            @Parameter(description = "最低金额")
            @RequestParam Long minAmount,
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getHighValuePurchases(minAmount, limit);
        } catch (Exception e) {
            log.error("查询高价值购买记录API调用失败: minAmount={}", minAmount, e);
            return Result.error("GET_HIGH_VALUE_PURCHASES_API_FAILED", "查询高价值购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/high-value")
    @Operation(summary = "查询用户高价值购买", description = "查询用户的高价值购买记录")
    public Result<List<ContentPurchaseResponse>> getUserHighValuePurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "最低金额", required = true)
            @RequestParam Long minAmount) {
        try {
            return contentPurchaseFacadeService.getUserHighValuePurchases(userId, minAmount);
        } catch (Exception e) {
            log.error("查询用户高价值购买API调用失败: userId={}, minAmount={}", userId, minAmount, e);
            return Result.error("GET_USER_HIGH_VALUE_PURCHASES_API_FAILED", "查询用户高价值购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/most-accessed")
    @Operation(summary = "查询最受欢迎购买", description = "查询访问次数最多的购买记录")
    public Result<List<ContentPurchaseResponse>> getMostAccessedPurchases(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getMostAccessedPurchases(limit);
        } catch (Exception e) {
            log.error("查询最受欢迎购买API调用失败", e);
            return Result.error("GET_MOST_ACCESSED_PURCHASES_API_FAILED", "查询最受欢迎购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/recent-accessed")
    @Operation(summary = "查询用户最近访问", description = "查询用户最近访问的购买记录")
    public Result<List<ContentPurchaseResponse>> getUserRecentAccessedPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getUserRecentAccessedPurchases(userId, limit);
        } catch (Exception e) {
            log.error("查询用户最近访问API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_RECENT_ACCESSED_PURCHASES_API_FAILED", "查询用户最近访问API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/discount-stats")
    @Operation(summary = "获取用户优惠统计", description = "获取用户的优惠统计信息")
    public Result<Map<String, Object>> getDiscountStats(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.getDiscountStats(userId);
        } catch (Exception e) {
            log.error("获取用户优惠统计API调用失败: userId={}", userId, e);
            return Result.error("GET_DISCOUNT_STATS_API_FAILED", "获取用户优惠统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 访问记录管理 ===================

    @PostMapping("/record-access")
    @Operation(summary = "记录内容访问", description = "记录用户访问内容")
    public Result<Boolean> recordContentAccess(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.recordContentAccess(userId, contentId);
        } catch (Exception e) {
            log.error("记录内容访问API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("RECORD_CONTENT_ACCESS_API_FAILED", "记录内容访问API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch-update-access-stats")
    @Operation(summary = "批量更新访问统计", description = "批量更新购买记录的访问统计")
    public Result<Boolean> batchUpdateAccessStats(
            @Parameter(description = "购买记录ID列表", required = true)
            @RequestBody List<Long> purchaseIds) {
        try {
            return contentPurchaseFacadeService.batchUpdateAccessStats(purchaseIds);
        } catch (Exception e) {
            log.error("批量更新访问统计API调用失败", e);
            return Result.error("BATCH_UPDATE_ACCESS_STATS_API_FAILED", "批量更新访问统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 状态管理 ===================

    @PostMapping("/process-expired")
    @Operation(summary = "处理过期购买记录", description = "处理过期的购买记录")
    public Result<Integer> processExpiredPurchases() {
        try {
            return contentPurchaseFacadeService.processExpiredPurchases();
        } catch (Exception e) {
            log.error("处理过期购买记录API调用失败", e);
            return Result.error("PROCESS_EXPIRED_PURCHASES_API_FAILED", "处理过期购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/expiring-soon")
    @Operation(summary = "查询即将过期购买", description = "查询即将过期的购买记录")
    public Result<List<ContentPurchaseResponse>> getExpiringSoonPurchases(
            @Parameter(description = "过期时间点", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime) {
        try {
            return contentPurchaseFacadeService.getExpiringSoonPurchases(beforeTime);
        } catch (Exception e) {
            log.error("查询即将过期购买API调用失败", e);
            return Result.error("GET_EXPIRING_SOON_PURCHASES_API_FAILED", "查询即将过期购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/expired")
    @Operation(summary = "查询已过期购买", description = "查询已过期的购买记录")
    public Result<List<ContentPurchaseResponse>> getExpiredPurchases() {
        try {
            return contentPurchaseFacadeService.getExpiredPurchases();
        } catch (Exception e) {
            log.error("查询已过期购买API调用失败", e);
            return Result.error("GET_EXPIRED_PURCHASES_API_FAILED", "查询已过期购买API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新状态", description = "批量更新购买记录状态")
    public Result<Boolean> batchUpdateStatus(
            @Parameter(description = "购买记录ID列表", required = true)
            @RequestParam List<Long> ids,
            @Parameter(description = "目标状态", required = true)
            @RequestParam String status) {
        try {
            return contentPurchaseFacadeService.batchUpdateStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新状态API调用失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/{purchaseId}/refund")
    @Operation(summary = "退款处理", description = "处理购买记录的退款申请")
    public Result<Boolean> refundPurchase(
            @Parameter(description = "购买记录ID", required = true)
            @PathVariable Long purchaseId,
            @Parameter(description = "退款原因", required = true)
            @RequestParam String reason,
            @Parameter(description = "操作人ID", required = true)
            @RequestParam Long operatorId) {
        try {
            return contentPurchaseFacadeService.refundPurchase(purchaseId, reason, operatorId);
        } catch (Exception e) {
            log.error("退款处理API调用失败: purchaseId={}", purchaseId, e);
            return Result.error("REFUND_PURCHASE_API_FAILED", "退款处理API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计分析 ===================

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "统计用户购买总数", description = "统计用户的购买总数")
    public Result<Long> countUserPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.countUserPurchases(userId);
        } catch (Exception e) {
            log.error("统计用户购买总数API调用失败: userId={}", userId, e);
            return Result.error("COUNT_USER_PURCHASES_API_FAILED", "统计用户购买总数API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/valid-count")
    @Operation(summary = "统计用户有效购买数", description = "统计用户的有效购买数量")
    public Result<Long> countUserValidPurchases(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.countUserValidPurchases(userId);
        } catch (Exception e) {
            log.error("统计用户有效购买数API调用失败: userId={}", userId, e);
            return Result.error("COUNT_USER_VALID_PURCHASES_API_FAILED", "统计用户有效购买数API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/count")
    @Operation(summary = "统计内容购买总数", description = "统计内容的购买总数")
    public Result<Long> countContentPurchases(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPurchaseFacadeService.countContentPurchases(contentId);
        } catch (Exception e) {
            log.error("统计内容购买总数API调用失败: contentId={}", contentId, e);
            return Result.error("COUNT_CONTENT_PURCHASES_API_FAILED", "统计内容购买总数API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/revenue")
    @Operation(summary = "统计内容收入", description = "统计内容的收入总额")
    public Result<Long> sumContentRevenue(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPurchaseFacadeService.sumContentRevenue(contentId);
        } catch (Exception e) {
            log.error("统计内容收入API调用失败: contentId={}", contentId, e);
            return Result.error("SUM_CONTENT_REVENUE_API_FAILED", "统计内容收入API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/expense")
    @Operation(summary = "统计用户消费", description = "统计用户的消费总额")
    public Result<Long> sumUserExpense(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.sumUserExpense(userId);
        } catch (Exception e) {
            log.error("统计用户消费API调用失败: userId={}", userId, e);
            return Result.error("SUM_USER_EXPENSE_API_FAILED", "统计用户消费API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/popular-ranking")
    @Operation(summary = "获取热门内容排行", description = "获取热门购买内容排行榜")
    public Result<List<Map<String, Object>>> getPopularContentRanking(
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getPopularContentRanking(limit);
        } catch (Exception e) {
            log.error("获取热门内容排行API调用失败", e);
            return Result.error("GET_POPULAR_CONTENT_RANKING_API_FAILED", "获取热门内容排行API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "获取用户购买统计", description = "获取用户的购买统计信息")
    public Result<Map<String, Object>> getUserPurchaseStats(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        try {
            return contentPurchaseFacadeService.getUserPurchaseStats(userId);
        } catch (Exception e) {
            log.error("获取用户购买统计API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_PURCHASE_STATS_API_FAILED", "获取用户购买统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}/sales-stats")
    @Operation(summary = "获取内容销售统计", description = "获取内容的销售统计信息")
    public Result<Map<String, Object>> getContentSalesStats(
            @Parameter(description = "内容ID", required = true)
            @PathVariable Long contentId) {
        try {
            return contentPurchaseFacadeService.getContentSalesStats(contentId);
        } catch (Exception e) {
            log.error("获取内容销售统计API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_SALES_STATS_API_FAILED", "获取内容销售统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/author/{authorId}/revenue-stats")
    @Operation(summary = "获取作者收入统计", description = "获取作者的收入统计信息")
    public Result<Map<String, Object>> getAuthorRevenueStats(
            @Parameter(description = "作者ID", required = true)
            @PathVariable Long authorId) {
        try {
            return contentPurchaseFacadeService.getAuthorRevenueStats(authorId);
        } catch (Exception e) {
            log.error("获取作者收入统计API调用失败: authorId={}", authorId, e);
            return Result.error("GET_AUTHOR_REVENUE_STATS_API_FAILED", "获取作者收入统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/stats/date-range")
    @Operation(summary = "获取日期范围统计", description = "获取指定日期范围内的购买统计")
    public Result<List<Map<String, Object>>> getPurchaseStatsByDateRange(
            @Parameter(description = "开始时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "结束时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            return contentPurchaseFacadeService.getPurchaseStatsByDateRange(startDate, endDate);
        } catch (Exception e) {
            log.error("获取日期范围统计API调用失败", e);
            return Result.error("GET_PURCHASE_STATS_BY_DATE_RANGE_API_FAILED", "获取日期范围统计API调用失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑 ===================

    @PostMapping("/handle-payment-success/{orderId}")
    @Operation(summary = "处理订单支付成功", description = "处理订单支付成功后的购买记录创建")
    public Result<ContentPurchaseResponse> handleOrderPaymentSuccess(
            @Parameter(description = "订单ID", required = true)
            @PathVariable Long orderId) {
        try {
            return contentPurchaseFacadeService.handleOrderPaymentSuccess(orderId);
        } catch (Exception e) {
            log.error("处理订单支付成功API调用失败: orderId={}", orderId, e);
            return Result.error("HANDLE_ORDER_PAYMENT_SUCCESS_API_FAILED", "处理订单支付成功API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/validate-purchase-permission")
    @Operation(summary = "验证购买权限", description = "验证用户是否有权限购买指定内容")
    public Result<Boolean> validatePurchasePermission(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.validatePurchasePermission(userId, contentId);
        } catch (Exception e) {
            log.error("验证购买权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("VALIDATE_PURCHASE_PERMISSION_API_FAILED", "验证购买权限API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/calculate-content-access")
    @Operation(summary = "计算内容访问权限", description = "计算用户对内容的访问权限详情")
    public Result<Map<String, Object>> calculateContentAccess(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true)
            @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.calculateContentAccess(userId, contentId);
        } catch (Exception e) {
            log.error("计算内容访问权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_CONTENT_ACCESS_API_FAILED", "计算内容访问权限API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/recommendations")
    @Operation(summary = "获取用户内容推荐", description = "获取基于购买历史的内容推荐")
    public Result<List<Long>> getUserContentRecommendations(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "返回数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return contentPurchaseFacadeService.getUserContentRecommendations(userId, limit);
        } catch (Exception e) {
            log.error("获取用户内容推荐API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_CONTENT_RECOMMENDATIONS_API_FAILED", "获取用户内容推荐API调用失败: " + e.getMessage());
        }
    }
}