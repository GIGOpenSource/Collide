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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容购买管理控制器 - 极简版
 * 基于12个核心Facade方法设计的精简API
 * 
 * @author GIG Team
 * @version 2.0.0 (极简版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/content/purchase")
@RequiredArgsConstructor
@Validated
@Tag(name = "内容购买管理", description = "用户内容购买记录的管理、查询和统计接口（极简版）")
public class ContentPurchaseController {

    private final ContentPurchaseFacadeService contentPurchaseFacadeService;

    // =================== 核心CRUD功能（2个API）===================

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
            @PathVariable Long id) {
        try {
            return contentPurchaseFacadeService.deletePurchase(id);
        } catch (Exception e) {
            log.error("删除购买记录API调用失败: id={}", id, e);
            return Result.error("DELETE_PURCHASE_API_FAILED", "删除购买记录API调用失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（3个API）===================

    @GetMapping("/query")
    @Operation(summary = "万能条件查询购买记录", description = "根据多种条件查询购买记录列表，替代所有具体查询API")
    public Result<PageResponse<ContentPurchaseResponse>> getPurchasesByConditions(
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "内容ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "订单ID") @RequestParam(required = false) Long orderId,
            @Parameter(description = "订单号") @RequestParam(required = false) String orderNo,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "是否有效") @RequestParam(required = false) Boolean isValid,
            @Parameter(description = "最小金额") @RequestParam(required = false) Long minAmount,
            @Parameter(description = "最大金额") @RequestParam(required = false) Long maxAmount,
            @Parameter(description = "排序字段") @RequestParam(required = false, defaultValue = "createTime") String orderBy,
            @Parameter(description = "排序方向") @RequestParam(required = false, defaultValue = "DESC") String orderDirection,
            @Parameter(description = "当前页码") @RequestParam(required = false) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false) Integer pageSize) {
        try {
            return contentPurchaseFacadeService.getPurchasesByConditions(
                userId, contentId, contentType, orderId, orderNo,
                status, isValid, minAmount, maxAmount,
                orderBy, orderDirection, currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("万能条件查询购买记录API调用失败", e);
            return Result.error("QUERY_PURCHASES_API_FAILED", "万能条件查询购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/recommended")
    @Operation(summary = "推荐购买记录查询", description = "获取推荐的购买记录")
    public Result<List<ContentPurchaseResponse>> getRecommendedPurchases(
            @Parameter(description = "推荐策略：RECENT、HIGH_VALUE、MOST_ACCESSED、POPULAR", required = true) @RequestParam String strategy,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "内容类型") @RequestParam(required = false) String contentType,
            @Parameter(description = "排除的内容ID列表") @RequestParam(required = false) List<Long> excludeContentIds,
            @Parameter(description = "返回数量限制", required = true) @RequestParam Integer limit) {
        try {
            return contentPurchaseFacadeService.getRecommendedPurchases(strategy, userId, contentType, excludeContentIds, limit);
        } catch (Exception e) {
            log.error("推荐购买记录查询API调用失败", e);
            return Result.error("GET_RECOMMENDED_PURCHASES_API_FAILED", "推荐购买记录查询API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/expire-status")
    @Operation(summary = "过期状态查询", description = "根据过期状态查询购买记录")
    public Result<List<ContentPurchaseResponse>> getPurchasesByExpireStatus(
            @Parameter(description = "查询类型：EXPIRING_SOON、EXPIRED", required = true) @RequestParam String type,
            @Parameter(description = "时间界限") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime beforeTime,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "返回数量限制") @RequestParam(required = false) Integer limit) {
        try {
            return contentPurchaseFacadeService.getPurchasesByExpireStatus(type, beforeTime, userId, limit);
        } catch (Exception e) {
            log.error("过期状态查询API调用失败", e);
            return Result.error("GET_PURCHASES_BY_EXPIRE_STATUS_API_FAILED", "过期状态查询API调用失败: " + e.getMessage());
        }
    }

    // =================== 权限验证功能（1个API）===================

    @GetMapping("/check-access")
    @Operation(summary = "检查访问权限", description = "检查用户是否有权限访问指定内容")
    public Result<Boolean> checkAccessPermission(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.checkAccessPermission(userId, contentId);
        } catch (Exception e) {
            log.error("检查访问权限API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_ACCESS_PERMISSION_API_FAILED", "检查访问权限API调用失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个API）===================

    @PutMapping("/{purchaseId}/status")
    @Operation(summary = "更新购买记录状态", description = "更新购买记录状态")
    public Result<Boolean> updatePurchaseStatus(
            @Parameter(description = "购买记录ID", required = true) @PathVariable Long purchaseId,
            @Parameter(description = "新状态", required = true) @RequestParam String status) {
        try {
            return contentPurchaseFacadeService.updatePurchaseStatus(purchaseId, status);
        } catch (Exception e) {
            log.error("更新购买记录状态API调用失败: purchaseId={}, status={}", purchaseId, status, e);
            return Result.error("UPDATE_PURCHASE_STATUS_API_FAILED", "更新购买记录状态API调用失败: " + e.getMessage());
        }
    }

    @PutMapping("/batch-status")
    @Operation(summary = "批量更新购买记录状态", description = "批量更新购买记录状态")
    public Result<Boolean> batchUpdateStatus(
            @Parameter(description = "购买记录ID列表", required = true) @RequestParam List<Long> ids,
            @Parameter(description = "新状态", required = true) @RequestParam String status) {
        try {
            return contentPurchaseFacadeService.batchUpdateStatus(ids, status);
        } catch (Exception e) {
            log.error("批量更新购买记录状态API调用失败: ids={}, status={}", ids, status, e);
            return Result.error("BATCH_UPDATE_STATUS_API_FAILED", "批量更新购买记录状态API调用失败: " + e.getMessage());
        }
    }

    // =================== 统计功能（1个API）===================

    @GetMapping("/stats")
    @Operation(summary = "获取购买统计信息", description = "获取购买统计信息")
    public Result<Map<String, Object>> getPurchaseStats(
            @Parameter(description = "统计类型：USER_SUMMARY、CONTENT_SUMMARY、DISCOUNT", required = true) @RequestParam String statsType,
            @Parameter(description = "统计参数") @RequestParam Map<String, Object> params) {
        try {
            return contentPurchaseFacadeService.getPurchaseStats(statsType, params);
        } catch (Exception e) {
            log.error("获取购买统计信息API调用失败: statsType={}", statsType, e);
            return Result.error("GET_PURCHASE_STATS_API_FAILED", "获取购买统计信息API调用失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑功能（3个API）===================

    @PostMapping("/complete")
    @Operation(summary = "处理内容购买完成", description = "处理内容购买完成，创建购买记录")
    public Result<ContentPurchaseResponse> completePurchase(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId,
            @Parameter(description = "订单ID", required = true) @RequestParam Long orderId,
            @Parameter(description = "订单号", required = true) @RequestParam String orderNo,
            @Parameter(description = "购买金额", required = true) @RequestParam Long purchaseAmount,
            @Parameter(description = "原价", required = true) @RequestParam Long originalPrice,
            @Parameter(description = "过期时间") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expireTime) {
        try {
            return contentPurchaseFacadeService.completePurchase(userId, contentId, orderId, orderNo, purchaseAmount, originalPrice, expireTime);
        } catch (Exception e) {
            log.error("处理内容购买完成API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("COMPLETE_PURCHASE_API_FAILED", "处理内容购买完成API调用失败: " + e.getMessage());
        }
    }

    @PostMapping("/{purchaseId}/refund")
    @Operation(summary = "处理退款", description = "处理购买记录退款")
    public Result<Boolean> processRefund(
            @Parameter(description = "购买记录ID", required = true) @PathVariable Long purchaseId,
            @Parameter(description = "退款原因", required = true) @RequestParam String refundReason,
            @Parameter(description = "退款金额", required = true) @RequestParam Long refundAmount) {
        try {
            return contentPurchaseFacadeService.processRefund(purchaseId, refundReason, refundAmount);
        } catch (Exception e) {
            log.error("处理退款API调用失败: purchaseId={}", purchaseId, e);
            return Result.error("PROCESS_REFUND_API_FAILED", "处理退款API调用失败: " + e.getMessage());
        }
    }

    @PostMapping("/access")
    @Operation(summary = "记录内容访问", description = "记录用户访问内容，更新访问统计")
    public Result<Boolean> recordContentAccess(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "内容ID", required = true) @RequestParam Long contentId) {
        try {
            return contentPurchaseFacadeService.recordContentAccess(userId, contentId);
        } catch (Exception e) {
            log.error("记录内容访问API调用失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("RECORD_CONTENT_ACCESS_API_FAILED", "记录内容访问API调用失败: " + e.getMessage());
        }
    }

    // =================== 便民快捷API（基于万能查询实现）===================

    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户购买记录", description = "快捷API：分页查询用户的购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                userId, null, null, null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询用户购买记录API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_PURCHASES_API_FAILED", "查询用户购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/valid")
    @Operation(summary = "查询用户有效购买记录", description = "快捷API：查询用户的有效购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getUserValidPurchases(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                userId, null, null, null, null, "ACTIVE", true, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询用户有效购买记录API调用失败: userId={}", userId, e);
            return Result.error("GET_USER_VALID_PURCHASES_API_FAILED", "查询用户有效购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/content/{contentId}")
    @Operation(summary = "查询内容购买记录", description = "快捷API：分页查询指定内容的购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(
            @Parameter(description = "内容ID", required = true) @PathVariable Long contentId,
            @Parameter(description = "当前页码", required = true) @RequestParam Integer currentPage,
            @Parameter(description = "页面大小", required = true) @RequestParam Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                null, contentId, null, null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询内容购买记录API调用失败: contentId={}", contentId, e);
            return Result.error("GET_CONTENT_PURCHASES_API_FAILED", "查询内容购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "根据订单ID查询", description = "快捷API：根据订单ID查询购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getPurchaseByOrderId(
            @Parameter(description = "订单ID", required = true) @PathVariable Long orderId) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                null, null, null, orderId, null, null, null, null, null,
                null, null, null, null
            );
        } catch (Exception e) {
            log.error("根据订单ID查询购买记录API调用失败: orderId={}", orderId, e);
            return Result.error("GET_PURCHASE_BY_ORDER_ID_API_FAILED", "根据订单ID查询购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/order-no/{orderNo}")
    @Operation(summary = "根据订单号查询", description = "快捷API：根据订单号查询购买记录")
    public Result<PageResponse<ContentPurchaseResponse>> getPurchaseByOrderNo(
            @Parameter(description = "订单号", required = true) @PathVariable String orderNo) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                null, null, null, null, orderNo, null, null, null, null,
                null, null, null, null
            );
        } catch (Exception e) {
            log.error("根据订单号查询购买记录API调用失败: orderNo={}", orderNo, e);
            return Result.error("GET_PURCHASE_BY_ORDER_NO_API_FAILED", "根据订单号查询购买记录API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/content-type/{contentType}")
    @Operation(summary = "查询用户指定类型购买", description = "快捷API：查询用户购买的指定类型内容")
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchasesByContentType(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "内容类型", required = true) @PathVariable String contentType,
            @Parameter(description = "当前页码") @RequestParam(required = false, defaultValue = "1") Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        try {
            // 使用万能查询实现
            return contentPurchaseFacadeService.getPurchasesByConditions(
                userId, null, contentType, null, null, null, null, null, null,
                "createTime", "DESC", currentPage, pageSize
            );
        } catch (Exception e) {
            log.error("查询用户指定类型购买API调用失败: userId={}, contentType={}", userId, contentType, e);
            return Result.error("GET_USER_PURCHASES_BY_CONTENT_TYPE_API_FAILED", "查询用户指定类型购买API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/discount-stats")
    @Operation(summary = "获取用户优惠统计", description = "快捷API：获取用户的优惠统计信息")
    public Result<Map<String, Object>> getDiscountStats(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {
        try {
            // 使用统计功能实现
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            return contentPurchaseFacadeService.getPurchaseStats("DISCOUNT", params);
        } catch (Exception e) {
            log.error("获取用户优惠统计API调用失败: userId={}", userId, e);
            return Result.error("GET_DISCOUNT_STATS_API_FAILED", "获取用户优惠统计API调用失败: " + e.getMessage());
        }
    }

    @GetMapping("/expired")
    @Operation(summary = "查询已过期购买", description = "快捷API：查询已过期的购买记录")
    public Result<List<ContentPurchaseResponse>> getExpiredPurchases(
            @Parameter(description = "返回数量限制") @RequestParam(required = false, defaultValue = "100") Integer limit) {
        try {
            // 使用过期状态查询实现
            return contentPurchaseFacadeService.getPurchasesByExpireStatus("EXPIRED", null, null, limit);
        } catch (Exception e) {
            log.error("查询已过期购买API调用失败", e);
            return Result.error("GET_EXPIRED_PURCHASES_API_FAILED", "查询已过期购买API调用失败: " + e.getMessage());
        }
    }
}