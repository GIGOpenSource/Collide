package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentPurchaseFacadeService;
import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.content.domain.entity.UserContentPurchase;
import com.gig.collide.content.domain.service.UserContentPurchaseService;
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
 * 内容购买门面服务实现类 - 极简版
 * 专注于购买记录核心功能，12个核心方法
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class ContentPurchaseFacadeServiceImpl implements ContentPurchaseFacadeService {

    private final UserContentPurchaseService userContentPurchaseService;

    // =================== 核心CRUD功能（2个方法）===================

    @Override
    public Result<ContentPurchaseResponse> getPurchaseById(Long id) {
        try {
            log.debug("获取购买记录: id={}", id);
            
            UserContentPurchase purchase = userContentPurchaseService.getPurchaseById(id);
            
            if (purchase == null) {
                return Result.error("PURCHASE_NOT_FOUND", "购买记录不存在");
            }
            
            ContentPurchaseResponse response = convertToResponse(purchase);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取购买记录失败: id={}", id, e);
            return Result.error("GET_PURCHASE_FAILED", "获取购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> deletePurchase(Long id) {
        try {
            log.info("删除购买记录: id={}", id);
            
            boolean result = userContentPurchaseService.deletePurchase(id);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("DELETE_FAILED", "删除购买记录失败");
            }
        } catch (Exception e) {
            log.error("删除购买记录失败: id={}", id, e);
            return Result.error("DELETE_FAILED", "删除购买记录失败: " + e.getMessage());
        }
    }

    // =================== 万能查询功能（3个方法）===================

    @Override
    public Result<PageResponse<ContentPurchaseResponse>> getPurchasesByConditions(
            Long userId, Long contentId, String contentType, Long orderId, String orderNo,
            String status, Boolean isValid, Long minAmount, Long maxAmount,
            String orderBy, String orderDirection, Integer currentPage, Integer pageSize) {
        try {
            log.debug("万能条件查询购买记录: userId={}, contentId={}, contentType={}", 
                     userId, contentId, contentType);
            
            // 调用Service层的万能查询方法
            List<UserContentPurchase> purchases = userContentPurchaseService.getPurchasesByConditions(
                userId, contentId, contentType, null, status, isValid,
                minAmount, maxAmount, null, null,
                orderBy, orderDirection, currentPage, pageSize
            );
            
            PageResponse<ContentPurchaseResponse> pageResponse = convertToPageResponse(purchases, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("万能条件查询购买记录失败", e);
            return Result.error("QUERY_FAILED", "查询购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getRecommendedPurchases(
            String strategy, Long userId, String contentType,
            List<Long> excludeContentIds, Integer limit) {
        try {
            log.debug("推荐购买记录查询: strategy={}, userId={}, contentType={}", 
                     strategy, userId, contentType);
            
            List<UserContentPurchase> purchases = userContentPurchaseService.getRecommendedPurchases(
                strategy, userId, contentType, excludeContentIds, limit
            );
            
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("推荐购买记录查询失败", e);
            return Result.error("GET_RECOMMENDED_FAILED", "获取推荐购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getPurchasesByExpireStatus(
            String type, LocalDateTime beforeTime, Long userId, Integer limit) {
        try {
            log.debug("过期状态查询购买记录: type={}, beforeTime={}, userId={}", type, beforeTime, userId);
            
            List<UserContentPurchase> purchases = userContentPurchaseService.getPurchasesByExpireStatus(
                type, beforeTime, userId, limit
            );
            
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("过期状态查询购买记录失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    // =================== 权限验证功能（1个方法）===================

    @Override
    public Result<Boolean> checkAccessPermission(Long userId, Long contentId) {
        try {
            log.debug("检查用户访问权限: userId={}, contentId={}", userId, contentId);
            
            boolean hasPermission = userContentPurchaseService.checkAccessPermission(userId, contentId);
            
            return Result.success(hasPermission);
        } catch (Exception e) {
            log.error("检查用户访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_PERMISSION_FAILED", "检查访问权限失败: " + e.getMessage());
        }
    }

    // =================== 状态管理功能（2个方法）===================

    @Override
    public Result<Boolean> updatePurchaseStatus(Long purchaseId, String status) {
        try {
            log.info("更新购买记录状态: purchaseId={}, status={}", purchaseId, status);
            
            boolean result = userContentPurchaseService.updatePurchaseStatus(purchaseId, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("UPDATE_STATUS_FAILED", "更新购买记录状态失败");
            }
        } catch (Exception e) {
            log.error("更新购买记录状态失败", e);
            return Result.error("UPDATE_STATUS_FAILED", "更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchUpdateStatus(List<Long> ids, String status) {
        try {
            log.info("批量更新购买记录状态: ids.size={}, status={}", 
                    ids != null ? ids.size() : 0, status);
            
            boolean result = userContentPurchaseService.batchUpdateStatus(ids, status);
            
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("BATCH_UPDATE_FAILED", "批量更新购买记录状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新购买记录状态失败", e);
            return Result.error("BATCH_UPDATE_FAILED", "批量更新失败: " + e.getMessage());
        }
    }

    // =================== 统计功能（1个方法）===================

    @Override
    public Result<Map<String, Object>> getPurchaseStats(String statsType, Map<String, Object> params) {
        try {
            log.debug("获取购买统计信息: statsType={}", statsType);
            
            Map<String, Object> stats = new HashMap<>();
            
            switch (statsType.toUpperCase()) {
                case "USER_SUMMARY":
                    // 用户购买汇总统计
                    Long userId = (Long) params.get("userId");
                    if (userId != null) {
                        // 使用万能查询获取用户的所有购买记录
                        List<UserContentPurchase> userPurchases = userContentPurchaseService.getPurchasesByConditions(
                            userId, null, null, null, null, null, null, null, null, null,
                            null, null, null, null
                        );
                        stats.put("totalPurchases", userPurchases.size());
                        stats.put("totalAmount", userPurchases.stream().mapToLong(p -> p.getCoinAmount() != null ? p.getCoinAmount() : 0L).sum());
                        stats.put("validPurchases", userPurchases.stream().filter(p -> "ACTIVE".equals(p.getStatus())).count());
                    }
                    break;
                case "CONTENT_SUMMARY":
                    // 内容购买汇总统计
                    Long contentId = (Long) params.get("contentId");
                    if (contentId != null) {
                        List<UserContentPurchase> contentPurchases = userContentPurchaseService.getPurchasesByConditions(
                            null, contentId, null, null, null, null, null, null, null, null,
                            null, null, null, null
                        );
                        stats.put("totalPurchases", contentPurchases.size());
                        stats.put("totalRevenue", contentPurchases.stream().mapToLong(p -> p.getCoinAmount() != null ? p.getCoinAmount() : 0L).sum());
                    }
                    break;
                case "DISCOUNT":
                    // 优惠统计
                    Long discountUserId = (Long) params.get("userId");
                    if (discountUserId != null) {
                        List<UserContentPurchase> discountPurchases = userContentPurchaseService.getPurchasesByConditions(
                            discountUserId, null, null, null, null, null, null, null, null, null,
                            null, null, null, null
                        );
                        long totalSaved = discountPurchases.stream()
                            .mapToLong(p -> (p.getOriginalPrice() != null ? p.getOriginalPrice() : 0L) - (p.getCoinAmount() != null ? p.getCoinAmount() : 0L))
                            .filter(saved -> saved > 0)
                            .sum();
                        stats.put("totalSaved", totalSaved);
                        stats.put("discountCount", discountPurchases.stream().filter(p -> p.getOriginalPrice() != null && p.getCoinAmount() != null && p.getOriginalPrice() > p.getCoinAmount()).count());
                    }
                    break;
                default:
                    return Result.error("UNSUPPORTED_STATS_TYPE", "不支持的统计类型: " + statsType);
            }
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取购买统计信息失败", e);
            return Result.error("GET_STATS_FAILED", "获取统计信息失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑功能（3个方法）===================

    @Override
    public Result<ContentPurchaseResponse> completePurchase(Long userId, Long contentId, Long orderId, String orderNo,
                                                           Long purchaseAmount, Long originalPrice, LocalDateTime expireTime) {
        try {
            log.info("处理内容购买完成: userId={}, contentId={}, orderId={}", userId, contentId, orderId);
            
            UserContentPurchase purchase = userContentPurchaseService.completePurchase(
                userId, contentId, orderId, orderNo, purchaseAmount, originalPrice, expireTime
            );
            
            if (purchase == null) {
                return Result.error("COMPLETE_PURCHASE_FAILED", "处理购买完成失败");
            }
            
            ContentPurchaseResponse response = convertToResponse(purchase);
            return Result.success(response);
        } catch (Exception e) {
            log.error("处理内容购买完成失败", e);
            return Result.error("COMPLETE_PURCHASE_FAILED", "处理购买完成失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> processRefund(Long purchaseId, String refundReason, Long refundAmount) {
        try {
            log.info("处理退款: purchaseId={}, refundReason={}, refundAmount={}", 
                    purchaseId, refundReason, refundAmount);
            
            // 更新购买记录状态为退款
            boolean result = userContentPurchaseService.updatePurchaseStatus(purchaseId, "REFUNDED");
            
            if (result) {
                log.info("退款处理成功: purchaseId={}", purchaseId);
                return Result.success(true);
            } else {
                return Result.error("REFUND_FAILED", "退款处理失败");
            }
        } catch (Exception e) {
            log.error("处理退款失败: purchaseId={}", purchaseId, e);
            return Result.error("REFUND_FAILED", "退款处理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> recordContentAccess(Long userId, Long contentId) {
        try {
            log.debug("记录内容访问: userId={}, contentId={}", userId, contentId);
            
            boolean result = userContentPurchaseService.recordContentAccess(userId, contentId);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("记录内容访问失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("RECORD_ACCESS_FAILED", "记录访问失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    private ContentPurchaseResponse convertToResponse(UserContentPurchase purchase) {
        ContentPurchaseResponse response = new ContentPurchaseResponse();
        BeanUtils.copyProperties(purchase, response);
        return response;
    }

    private PageResponse<ContentPurchaseResponse> convertToPageResponse(List<UserContentPurchase> purchases, Integer currentPage, Integer pageSize) {
        PageResponse<ContentPurchaseResponse> pageResponse = new PageResponse<>();
        
        if (CollectionUtils.isEmpty(purchases)) {
            pageResponse.setDatas(Collections.emptyList());
            pageResponse.setTotal(0L);
        } else {
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            pageResponse.setDatas(responses);
            pageResponse.setTotal((long) purchases.size());
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