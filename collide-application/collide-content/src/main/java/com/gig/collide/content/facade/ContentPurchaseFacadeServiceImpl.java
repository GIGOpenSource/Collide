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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容购买门面服务实现类 - 简化版
 * 与UserContentPurchaseService保持一致，专注核心功能
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

    // =================== 基础CRUD ===================

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
    public Result<Boolean> deletePurchase(Long id, Long operatorId) {
        try {
            log.info("删除购买记录: id={}, operatorId={}", id, operatorId);
            
            boolean result = userContentPurchaseService.deletePurchase(id, operatorId);
            
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

    // =================== 权限验证 ===================

    @Override
    public Result<ContentPurchaseResponse> getUserContentPurchase(Long userId, Long contentId) {
        try {
            log.debug("获取用户内容购买记录: userId={}, contentId={}", userId, contentId);
            
            UserContentPurchase purchase = userContentPurchaseService.getUserContentPurchase(userId, contentId);
            
            if (purchase == null) {
                return Result.error("PURCHASE_NOT_FOUND", "用户未购买该内容");
            }
            
            ContentPurchaseResponse response = convertToResponse(purchase);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户内容购买记录失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_PURCHASE_FAILED", "获取购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> hasAccessPermission(Long userId, Long contentId) {
        try {
            log.debug("检查用户访问权限: userId={}, contentId={}", userId, contentId);
            
            boolean hasPermission = userContentPurchaseService.hasAccessPermission(userId, contentId);
            
            return Result.success(hasPermission);
        } catch (Exception e) {
            log.error("检查用户访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CHECK_PERMISSION_FAILED", "检查访问权限失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentPurchaseResponse> getValidPurchase(Long userId, Long contentId) {
        try {
            log.debug("获取有效购买记录: userId={}, contentId={}", userId, contentId);
            
            UserContentPurchase purchase = userContentPurchaseService.getValidPurchase(userId, contentId);
            
            if (purchase == null) {
                return Result.error("VALID_PURCHASE_NOT_FOUND", "用户没有有效的购买记录");
            }
            
            ContentPurchaseResponse response = convertToResponse(purchase);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取有效购买记录失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("GET_VALID_PURCHASE_FAILED", "获取有效购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckAccessPermission(Long userId, List<Long> contentIds) {
        try {
            log.debug("批量检查用户访问权限: userId={}, contentIds={}", userId, contentIds);
            
            Map<Long, Boolean> permissions = userContentPurchaseService.batchCheckAccessPermission(userId, contentIds);
            
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("批量检查用户访问权限失败: userId={}", userId, e);
            return Result.error("BATCH_CHECK_FAILED", "批量检查访问权限失败: " + e.getMessage());
        }
    }

    // =================== 查询功能 ===================

    @Override
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(Long userId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询用户购买记录: userId={}, currentPage={}, pageSize={}", userId, currentPage, pageSize);
            
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserPurchases(userId, currentPage, pageSize);
            
            PageResponse<ContentPurchaseResponse> pageResponse = convertToPageResponse(purchases, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询用户购买记录失败: userId={}", userId, e);
            return Result.error("QUERY_FAILED", "查询用户购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserValidPurchases(Long userId) {
        try {
            log.debug("查询用户有效购买记录: userId={}", userId);
            
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserValidPurchases(userId);
            
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户有效购买记录失败: userId={}", userId, e);
            return Result.error("QUERY_FAILED", "查询有效购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(Long contentId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("查询内容购买记录: contentId={}, currentPage={}, pageSize={}", contentId, currentPage, pageSize);
            
            List<UserContentPurchase> purchases = userContentPurchaseService.getContentPurchases(contentId, currentPage, pageSize);
            
            PageResponse<ContentPurchaseResponse> pageResponse = convertToPageResponse(purchases, currentPage, pageSize);
            
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询内容购买记录失败: contentId={}", contentId, e);
            return Result.error("QUERY_FAILED", "查询内容购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentPurchaseResponse> getPurchaseByOrderId(Long orderId) {
        try {
            UserContentPurchase purchase = userContentPurchaseService.getPurchaseByOrderId(orderId);
            if (purchase == null) {
                return Result.error("PURCHASE_NOT_FOUND", "根据订单ID未找到购买记录");
            }
            return Result.success(convertToResponse(purchase));
        } catch (Exception e) {
            log.error("根据订单ID查询购买记录失败: orderId={}", orderId, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<ContentPurchaseResponse> getPurchaseByOrderNo(String orderNo) {
        try {
            UserContentPurchase purchase = userContentPurchaseService.getPurchaseByOrderNo(orderNo);
            if (purchase == null) {
                return Result.error("PURCHASE_NOT_FOUND", "根据订单号未找到购买记录");
            }
            return Result.success(convertToResponse(purchase));
        } catch (Exception e) {
            log.error("根据订单号查询购买记录失败: orderNo={}", orderNo, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserPurchasesByContentType(Long userId, String contentType) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserPurchasesByContentType(userId, contentType);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户指定类型购买记录失败: userId={}, contentType={}", userId, contentType, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserPurchasesByAuthor(Long userId, Long authorId) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserPurchasesByAuthor(userId, authorId);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户指定作者购买记录失败: userId={}, authorId={}", userId, authorId, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserRecentPurchases(Long userId, Integer limit) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserRecentPurchases(userId, limit);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户最近购买记录失败: userId={}", userId, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(Long userId) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserUnreadPurchases(userId);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户未读购买记录失败: userId={}", userId, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    // =================== C端必需的购买记录查询方法 ===================

    @Override
    public Result<List<ContentPurchaseResponse>> getHighValuePurchases(Long minAmount, Integer limit) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getHighValuePurchases(minAmount, limit);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询高价值购买记录失败: minAmount={}", minAmount, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserHighValuePurchases(Long userId, Long minAmount) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserHighValuePurchases(userId, minAmount);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户高价值购买记录失败: userId={}, minAmount={}", userId, minAmount, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getMostAccessedPurchases(Integer limit) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getMostAccessedPurchases(limit);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询最受欢迎购买记录失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserRecentAccessedPurchases(Long userId, Integer limit) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserRecentAccessedPurchases(userId, limit);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户最近访问购买记录失败: userId={}", userId, e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getDiscountStats(Long userId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getDiscountStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户优惠统计失败: userId={}", userId, e);
            return Result.error("GET_STATS_FAILED", "获取优惠统计失败: " + e.getMessage());
        }
    }

    // =================== 访问记录管理 ===================

    @Override
    public Result<Boolean> recordContentAccess(Long userId, Long contentId) {
        try {
            log.debug("记录内容访问: userId={}, contentId={}", userId, contentId);
            
            boolean result = userContentPurchaseService.recordContentAccess(userId, contentId);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("记录内容访问失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("RECORD_ACCESS_FAILED", "记录内容访问失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchUpdateAccessStats(List<Long> purchaseIds) {
        try {
            boolean result = userContentPurchaseService.batchUpdateAccessStats(purchaseIds);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新访问统计失败", e);
            return Result.error("UPDATE_STATS_FAILED", "批量更新访问统计失败: " + e.getMessage());
        }
    }

    // =================== 状态管理 ===================

    @Override
    public Result<Integer> processExpiredPurchases() {
        try {
            int count = userContentPurchaseService.processExpiredPurchases();
            return Result.success(count);
        } catch (Exception e) {
            log.error("处理过期购买记录失败", e);
            return Result.error("PROCESS_EXPIRED_FAILED", "处理过期购买记录失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getExpiringSoonPurchases(LocalDateTime beforeTime) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getExpiringSoonPurchases(beforeTime);
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询即将过期购买记录失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getExpiredPurchases() {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getExpiredPurchases();
            if (CollectionUtils.isEmpty(purchases)) {
                return Result.success(Collections.emptyList());
            }
            List<ContentPurchaseResponse> responses = purchases.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询已过期购买记录失败", e);
            return Result.error("QUERY_FAILED", "查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> batchUpdateStatus(List<Long> ids, String status) {
        try {
            boolean result = userContentPurchaseService.batchUpdateStatus(ids, status);
            return Result.success(result);
        } catch (Exception e) {
            log.error("批量更新购买记录状态失败", e);
            return Result.error("UPDATE_STATUS_FAILED", "批量更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> refundPurchase(Long purchaseId, String reason, Long operatorId) {
        try {
            boolean result = userContentPurchaseService.refundPurchase(purchaseId, reason, operatorId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("退款处理失败: purchaseId={}", purchaseId, e);
            return Result.error("REFUND_FAILED", "退款处理失败: " + e.getMessage());
        }
    }

    // =================== 统计分析 ===================

    @Override
    public Result<Long> countUserPurchases(Long userId) {
        try {
            Long count = userContentPurchaseService.countUserPurchases(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户购买总数失败: userId={}", userId, e);
            return Result.error("COUNT_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countUserValidPurchases(Long userId) {
        try {
            Long count = userContentPurchaseService.countUserValidPurchases(userId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计用户有效购买数失败: userId={}", userId, e);
            return Result.error("COUNT_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> countContentPurchases(Long contentId) {
        try {
            Long count = userContentPurchaseService.countContentPurchases(contentId);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计内容购买总数失败: contentId={}", contentId, e);
            return Result.error("COUNT_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> sumContentRevenue(Long contentId) {
        try {
            Long revenue = userContentPurchaseService.sumContentRevenue(contentId);
            return Result.success(revenue);
        } catch (Exception e) {
            log.error("统计内容收入失败: contentId={}", contentId, e);
            return Result.error("SUM_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Long> sumUserExpense(Long userId) {
        try {
            Long expense = userContentPurchaseService.sumUserExpense(userId);
            return Result.success(expense);
        } catch (Exception e) {
            log.error("统计用户消费失败: userId={}", userId, e);
            return Result.error("SUM_FAILED", "统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getPopularContentRanking(Integer limit) {
        try {
            List<Map<String, Object>> ranking = userContentPurchaseService.getPopularContentRanking(limit);
            return Result.success(ranking);
        } catch (Exception e) {
            log.error("获取热门内容排行失败", e);
            return Result.error("GET_RANKING_FAILED", "获取排行失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getUserPurchaseStats(Long userId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getUserPurchaseStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户购买统计失败: userId={}", userId, e);
            return Result.error("GET_STATS_FAILED", "获取统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getContentSalesStats(Long contentId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getContentSalesStats(contentId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容销售统计失败: contentId={}", contentId, e);
            return Result.error("GET_STATS_FAILED", "获取统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getAuthorRevenueStats(Long authorId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getAuthorRevenueStats(authorId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取作者收入统计失败: authorId={}", authorId, e);
            return Result.error("GET_STATS_FAILED", "获取统计失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getPurchaseStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Map<String, Object>> stats = userContentPurchaseService.getPurchaseStatsByDateRange(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取日期范围购买统计失败", e);
            return Result.error("GET_STATS_FAILED", "获取统计失败: " + e.getMessage());
        }
    }

    // =================== 业务逻辑 ===================

    @Override
    public Result<ContentPurchaseResponse> handleOrderPaymentSuccess(Long orderId) {
        try {
            UserContentPurchase purchase = userContentPurchaseService.handleOrderPaymentSuccess(orderId);
            if (purchase == null) {
                return Result.error("HANDLE_PAYMENT_FAILED", "处理订单支付成功失败");
            }
            return Result.success(convertToResponse(purchase));
        } catch (Exception e) {
            log.error("处理订单支付成功失败: orderId={}", orderId, e);
            return Result.error("HANDLE_PAYMENT_FAILED", "处理订单支付成功失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> validatePurchasePermission(Long userId, Long contentId) {
        try {
            boolean result = userContentPurchaseService.validatePurchasePermission(userId, contentId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("验证购买权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("VALIDATE_FAILED", "验证购买权限失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> calculateContentAccess(Long userId, Long contentId) {
        try {
            Map<String, Object> access = userContentPurchaseService.calculateContentAccess(userId, contentId);
            return Result.success(access);
        } catch (Exception e) {
            log.error("计算内容访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return Result.error("CALCULATE_FAILED", "计算内容访问权限失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Long>> getUserContentRecommendations(Long userId, Integer limit) {
        try {
            List<Long> recommendations = userContentPurchaseService.getUserContentRecommendations(userId, limit);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("获取用户内容推荐失败: userId={}", userId, e);
            return Result.error("GET_RECOMMENDATIONS_FAILED", "获取内容推荐失败: " + e.getMessage());
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
        
        pageResponse.setCurrentPage(currentPage);
        pageResponse.setPageSize(pageSize);
        pageResponse.setTotalPage((int) Math.ceil((double) pageResponse.getTotal() / pageSize));
        pageResponse.setSuccess(true);
        
        return pageResponse;
    }
}