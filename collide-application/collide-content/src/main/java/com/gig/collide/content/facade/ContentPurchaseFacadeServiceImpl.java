package com.gig.collide.content.facade;

import com.gig.collide.api.content.ContentPurchaseFacadeService;
import com.gig.collide.api.content.request.ContentPurchaseRequest;
import com.gig.collide.api.content.request.ContentPurchaseQueryRequest;
import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.content.domain.entity.UserContentPurchase;
import com.gig.collide.content.domain.service.UserContentPurchaseService;
import com.gig.collide.content.domain.service.ContentPaymentService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容购买门面服务实现类
 * 管理用户的内容购买、权限验证和购买历史
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
    private final ContentPaymentService contentPaymentService;

    // =================== 购买功能 ===================

    @Override
    public Result<Map<String, Object>> purchaseContent(ContentPurchaseRequest request) {
        log.info("处理内容购买请求: userId={}, contentId={}", request.getUserId(), request.getContentId());
        
        try {
            // 验证购买权限
            boolean canPurchase = userContentPurchaseService.validatePurchasePermission(
                request.getUserId(), request.getContentId());
            if (!canPurchase) {
                return Result.failure("无购买权限");
            }

            // 检查是否已购买
            UserContentPurchase existing = userContentPurchaseService.getUserContentPurchase(
                request.getUserId(), request.getContentId());
            if (existing != null && existing.hasAccessPermission()) {
                return Result.failure("已购买该内容");
            }

            // 验证价格
            Long actualPrice = contentPaymentService.calculateActualPrice(
                request.getUserId(), request.getContentId());
            if (!actualPrice.equals(request.getConfirmedPrice())) {
                return Result.failure("价格已变更，请重新确认");
            }

            // 处理购买逻辑（这里应该集成订单服务）
            Map<String, Object> purchaseResult = contentPaymentService.handleContentPurchase(
                request.getUserId(), request.getContentId());

            return Result.success("购买处理成功", purchaseResult);
        } catch (Exception e) {
            log.error("处理内容购买失败", e);
            return Result.failure("购买处理失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getContentPurchaseInfo(Long userId, Long contentId) {
        log.debug("获取内容购买信息: userId={}, contentId={}", userId, contentId);
        
        try {
            Map<String, Object> purchaseInfo = new HashMap<>();

            // 获取访问策略
            Map<String, Object> accessPolicy = contentPaymentService.getAccessPolicy(userId, contentId);
            purchaseInfo.put("accessPolicy", accessPolicy);

            // 获取价格信息
            Map<String, Object> priceInfo = contentPaymentService.getContentPriceInfo(contentId);
            purchaseInfo.put("priceInfo", priceInfo);

            // 计算实际价格
            Long actualPrice = contentPaymentService.calculateActualPrice(userId, contentId);
            purchaseInfo.put("actualPrice", actualPrice);

            // 检查购买状态
            UserContentPurchase purchase = userContentPurchaseService.getUserContentPurchase(userId, contentId);
            purchaseInfo.put("hasPurchased", purchase != null);
            purchaseInfo.put("hasAccess", purchase != null && purchase.hasAccessPermission());

            return Result.success(purchaseInfo);
        } catch (Exception e) {
            log.error("获取内容购买信息失败", e);
            return Result.failure("获取购买信息失败");
        }
    }

    @Override
    public Result<Map<String, Object>> requestTrial(Long userId, Long contentId) {
        log.info("处理试读申请: userId={}, contentId={}", userId, contentId);
        
        try {
            Map<String, Object> trialResult = contentPaymentService.handleTrialRequest(userId, contentId);
            return Result.success(trialResult);
        } catch (Exception e) {
            log.error("处理试读申请失败", e);
            return Result.failure("试读申请失败");
        }
    }

    // =================== 权限验证 ===================

    @Override
    public Result<Map<String, Object>> checkAccessPermission(Long userId, Long contentId) {
        try {
            Map<String, Object> accessInfo = userContentPurchaseService.calculateContentAccess(userId, contentId);
            return Result.success(accessInfo);
        } catch (Exception e) {
            log.error("检查访问权限失败", e);
            return Result.failure("权限检查失败");
        }
    }

    @Override
    public Result<Map<Long, Boolean>> batchCheckAccessPermission(Long userId, List<Long> contentIds) {
        try {
            Map<Long, Boolean> permissions = userContentPurchaseService.batchCheckAccessPermission(userId, contentIds);
            return Result.success(permissions);
        } catch (Exception e) {
            log.error("批量检查访问权限失败", e);
            return Result.failure("批量权限检查失败");
        }
    }

    @Override
    public Result<Void> recordContentAccess(Long userId, Long contentId) {
        try {
            boolean success = userContentPurchaseService.recordContentAccess(userId, contentId);
            if (success) {
                return Result.success();
            } else {
                return Result.failure("记录访问失败");
            }
        } catch (Exception e) {
            log.error("记录内容访问失败", e);
            return Result.failure("记录访问失败");
        }
    }

    // =================== 购买记录查询 ===================

    @Override
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(ContentPurchaseQueryRequest request) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserPurchases(
                request.getUserId(), request.getPage(), request.getSize());
            
            List<ContentPurchaseResponse> responses = purchases.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

            Long total = userContentPurchaseService.countUserPurchases(request.getUserId());
            PageResponse<ContentPurchaseResponse> pageResponse = PageResponse.of(
                responses, total, request.getPage(), request.getSize());

            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询用户购买记录失败", e);
            return Result.failure("查询购买记录失败");
        }
    }

    @Override
    public Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(ContentPurchaseQueryRequest request) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getContentPurchases(
                request.getContentId(), request.getPage(), request.getSize());
            
            List<ContentPurchaseResponse> responses = purchases.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

            Long total = userContentPurchaseService.countContentPurchases(request.getContentId());
            PageResponse<ContentPurchaseResponse> pageResponse = PageResponse.of(
                responses, total, request.getPage(), request.getSize());

            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("查询内容购买记录失败", e);
            return Result.failure("查询购买记录失败");
        }
    }

    @Override
    public Result<ContentPurchaseResponse> getPurchaseDetail(Long purchaseId, Long userId) {
        try {
            UserContentPurchase purchase = userContentPurchaseService.getPurchaseById(purchaseId);
            if (purchase == null) {
                return Result.failure("购买记录不存在");
            }

            // 权限检查（只能查看自己的购买记录）
            if (!purchase.getUserId().equals(userId)) {
                return Result.failure("无权限查看");
            }

            ContentPurchaseResponse response = convertToResponse(purchase);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取购买记录详情失败", e);
            return Result.failure("获取详情失败");
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserValidPurchases(Long userId) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserValidPurchases(userId);
            List<ContentPurchaseResponse> responses = purchases.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户有效购买失败", e);
            return Result.failure("查询有效购买失败");
        }
    }

    @Override
    public Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(Long userId) {
        try {
            List<UserContentPurchase> purchases = userContentPurchaseService.getUserUnreadPurchases(userId);
            List<ContentPurchaseResponse> responses = purchases.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return Result.success(responses);
        } catch (Exception e) {
            log.error("查询用户未读购买失败", e);
            return Result.failure("查询未读购买失败");
        }
    }

    // =================== 购买统计 ===================

    @Override
    public Result<Map<String, Object>> getUserPurchaseStats(Long userId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getUserPurchaseStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户购买统计失败", e);
            return Result.failure("获取购买统计失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getContentSalesStats(Long contentId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getContentSalesStats(contentId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容销售统计失败", e);
            return Result.failure("获取销售统计失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getAuthorRevenueStats(Long authorId) {
        try {
            Map<String, Object> stats = userContentPurchaseService.getAuthorRevenueStats(authorId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取作者收入统计失败", e);
            return Result.failure("获取收入统计失败");
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getPopularContentRanking(Integer limit) {
        try {
            List<Map<String, Object>> ranking = userContentPurchaseService.getPopularContentRanking(limit);
            return Result.success(ranking);
        } catch (Exception e) {
            log.error("获取热门内容排行失败", e);
            return Result.failure("获取排行失败");
        }
    }

    // =================== 订单处理 ===================

    @Override
    public Result<ContentPurchaseResponse> handleOrderPaymentSuccess(Long orderId) {
        try {
            UserContentPurchase purchase = userContentPurchaseService.handleOrderPaymentSuccess(orderId);
            if (purchase != null) {
                ContentPurchaseResponse response = convertToResponse(purchase);
                return Result.success(response);
            } else {
                return Result.failure("订单处理失败");
            }
        } catch (Exception e) {
            log.error("处理订单支付成功失败", e);
            return Result.failure("订单处理失败");
        }
    }

    @Override
    public Result<Void> handleRefundRequest(Long purchaseId, String reason, Long operatorId) {
        try {
            boolean success = userContentPurchaseService.refundPurchase(purchaseId, reason, operatorId);
            if (success) {
                return Result.success();
            } else {
                return Result.failure("退款处理失败");
            }
        } catch (Exception e) {
            log.error("处理退款申请失败", e);
            return Result.failure("退款处理失败");
        }
    }

    // =================== 推荐功能 ===================

    @Override
    public Result<List<Long>> getUserContentRecommendations(Long userId, Integer limit) {
        try {
            List<Long> recommendations = userContentPurchaseService.getUserContentRecommendations(userId, limit);
            return Result.success(recommendations);
        } catch (Exception e) {
            log.error("获取用户内容推荐失败", e);
            return Result.failure("获取推荐失败");
        }
    }

    @Override
    public Result<Map<String, Object>> getPurchaseSuggestion(Long userId, Long contentId) {
        try {
            // 这里可以实现更复杂的购买建议逻辑
            Map<String, Object> suggestion = new HashMap<>();
            suggestion.put("recommended", true);
            suggestion.put("reason", "基于您的购买历史推荐");
            return Result.success(suggestion);
        } catch (Exception e) {
            log.error("获取购买建议失败", e);
            return Result.failure("获取建议失败");
        }
    }

    // =================== 私有方法 ===================

    /**
     * 转换为响应对象
     */
    private ContentPurchaseResponse convertToResponse(UserContentPurchase purchase) {
        ContentPurchaseResponse response = new ContentPurchaseResponse();
        BeanUtils.copyProperties(purchase, response);
        
        // 设置计算属性
        response.setActualPaidAmount(purchase.getActualPaidAmount());
        response.setDiscountRate(purchase.getDiscountRate());
        response.setIsExpired(purchase.isExpired());
        response.setRemainingDays(purchase.getRemainingDays());
        response.setHasAccessPermission(purchase.hasAccessPermission());
        response.setHasAccessed(purchase.getAccessCount() != null && purchase.getAccessCount() > 0);

        return response;
    }
}