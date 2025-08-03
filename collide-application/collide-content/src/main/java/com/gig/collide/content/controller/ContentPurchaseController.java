package com.gig.collide.content.controller;

import com.gig.collide.api.content.ContentPurchaseFacadeService;
import com.gig.collide.api.content.request.ContentPurchaseRequest;
import com.gig.collide.api.content.request.ContentPurchaseQueryRequest;
import com.gig.collide.api.content.response.ContentPurchaseResponse;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.api.order.OrderFacadeService;
import com.gig.collide.api.order.request.OrderCreateRequest;
import com.gig.collide.api.order.response.OrderResponse;
import com.gig.collide.api.user.WalletFacadeService;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 内容购买控制器
 * 处理用户内容购买、权限验证、购买记录查询等功能
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
@Tag(name = "内容购买管理", description = "内容购买相关接口")
public class ContentPurchaseController {

    private final ContentPurchaseFacadeService contentPurchaseFacadeService;
    
    // =================== 外部服务依赖 ===================
    
    @DubboReference(version = "2.0.0", timeout = 5000, check = false)
    private OrderFacadeService orderFacadeService;
    
    @DubboReference(version = "2.0.0", timeout = 5000, check = false)
    private GoodsFacadeService goodsFacadeService;
    
    @DubboReference(version = "2.0.0", timeout = 5000, check = false)
    private WalletFacadeService walletFacadeService;

    // =================== 购买功能 ===================

    @PostMapping("/buy")
    @Operation(summary = "购买内容", description = "用户购买付费内容，验证权限、价格等")
    public Result<Map<String, Object>> purchaseContent(@Valid @RequestBody ContentPurchaseRequest request) {
        log.info("REST购买内容: userId={}, contentId={}, price={}", 
                request.getUserId(), request.getContentId(), request.getConfirmedPrice());
        
        try {
            // =================== 第一步：业务验证 ===================
            
            // 1.1 验证购买权限和价格（Content模块负责）
            Result<Map<String, Object>> validationResult = contentPurchaseFacadeService.validateContentPurchase(request);
            if (!validationResult.getSuccess()) {
                return validationResult;
            }
            
            Map<String, Object> validationData = validationResult.getData();
            Long actualPrice = (Long) validationData.get("actualPrice");
            
            // =================== 第二步：钱包余额检查 ===================
            
            // 2.1 检查用户金币余额是否充足
            Result<Boolean> balanceCheckResult = walletFacadeService.checkCoinBalance(request.getUserId(), actualPrice);
            if (!balanceCheckResult.getSuccess()) {
                return Result.failure("余额检查失败: " + balanceCheckResult.getMessage());
            }
            
            if (!balanceCheckResult.getData()) {
                return Result.failure("金币余额不足，当前需要 " + actualPrice + " 金币");
            }
            
            // =================== 第三步：查询商品信息 ===================
            
            // 3.1 查询对应的商品记录
            Result<GoodsResponse> goodsResult = goodsFacadeService.getGoodsByContentId(request.getContentId());
            if (!goodsResult.getSuccess()) {
                log.error("查询商品信息失败: contentId={}, error={}", request.getContentId(), goodsResult.getMessage());
                return Result.failure("商品信息查询失败");
            }
            
            GoodsResponse goods = goodsResult.getData();
            if (goods == null) {
                return Result.failure("商品不存在或已下架");
            }
            
            // 3.2 验证商品价格一致性
            if (!actualPrice.equals(goods.getCoinPrice())) {
                log.warn("价格不一致: content计算价格={}, goods价格={}", actualPrice, goods.getCoinPrice());
                return Result.failure("价格信息不一致，请刷新后重试");
            }
            
            // =================== 第四步：创建订单 ===================
            
            // 4.1 构建订单创建请求
            OrderCreateRequest orderRequest = new OrderCreateRequest();
            orderRequest.setUserId(request.getUserId());
            orderRequest.setGoodsId(goods.getId());
            orderRequest.setGoodsType("content");
            orderRequest.setPaymentMode("coin");
            orderRequest.setCoinCost(actualPrice);
            orderRequest.setQuantity(1);
            orderRequest.setContentId(request.getContentId());
            
            // 4.2 创建订单
            Result<OrderResponse> orderResult = orderFacadeService.createOrder(orderRequest);
            if (!orderResult.getSuccess()) {
                log.error("订单创建失败: userId={}, contentId={}, error={}", 
                    request.getUserId(), request.getContentId(), orderResult.getMessage());
                return Result.failure("订单创建失败: " + orderResult.getMessage());
            }
            
            OrderResponse order = orderResult.getData();
            log.info("订单创建成功: orderId={}, orderNo={}", order.getId(), order.getOrderNo());
            
            // =================== 第五步：处理金币支付 ===================
            
            // 5.1 立即处理金币支付（虚拟商品无需等待）
            Result<Map<String, Object>> paymentResult = orderFacadeService.processPayment(order.getId(), "coin");
            if (!paymentResult.getSuccess()) {
                log.error("金币支付失败: orderId={}, error={}", order.getId(), paymentResult.getMessage());
                
                // 支付失败，尝试取消订单
                try {
                    orderFacadeService.cancelOrder(order.getId(), "支付失败自动取消");
                } catch (Exception e) {
                    log.error("取消订单失败: orderId={}", order.getId(), e);
                }
                
                return Result.failure("支付处理失败: " + paymentResult.getMessage());
            }
            
            // =================== 第六步：返回购买结果 ===================
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("orderNo", order.getOrderNo());
            result.put("contentId", request.getContentId());
            result.put("coinCost", actualPrice);
            result.put("status", "completed");
            result.put("message", "内容购买成功");
            result.put("purchaseTime", System.currentTimeMillis());
            
            log.info("内容购买成功: userId={}, contentId={}, orderId={}, coinCost={}", 
                request.getUserId(), request.getContentId(), order.getId(), actualPrice);
            
            return Result.success("内容购买成功", result);
            
        } catch (Exception e) {
            log.error("内容购买处理异常: userId={}, contentId={}", 
                request.getUserId(), request.getContentId(), e);
            return Result.failure("购买处理失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取购买信息", description = "获取内容的购买信息，包括价格、折扣、权限等")
    public Result<Map<String, Object>> getContentPurchaseInfo(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        log.debug("REST获取购买信息: userId={}, contentId={}", userId, contentId);
        return contentPurchaseFacadeService.getContentPurchaseInfo(userId, contentId);
    }

    @PostMapping("/trial")
    @Operation(summary = "申请试读", description = "获取内容的试读部分")
    public Result<Map<String, Object>> requestTrial(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        log.info("REST申请试读: userId={}, contentId={}", userId, contentId);
        return contentPurchaseFacadeService.requestTrial(userId, contentId);
    }

    // =================== 权限验证 ===================

    @GetMapping("/access/check")
    @Operation(summary = "检查访问权限", description = "验证用户是否可以访问指定内容")
    public Result<Map<String, Object>> checkAccessPermission(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        log.debug("REST检查访问权限: userId={}, contentId={}", userId, contentId);
        return contentPurchaseFacadeService.checkAccessPermission(userId, contentId);
    }

    @PostMapping("/access/batch-check")
    @Operation(summary = "批量检查访问权限", description = "一次性检查用户对多个内容的访问权限")
    public Result<Map<Long, Boolean>> batchCheckAccessPermission(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID列表") @RequestBody List<Long> contentIds) {
        log.debug("REST批量检查访问权限: userId={}, contentCount={}", userId, contentIds.size());
        return contentPurchaseFacadeService.batchCheckAccessPermission(userId, contentIds);
    }

    @PostMapping("/access/record")
    @Operation(summary = "记录内容访问", description = "用户访问内容时调用，更新访问统计")
    public Result<Void> recordContentAccess(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "内容ID") @RequestParam Long contentId) {
        log.debug("REST记录内容访问: userId={}, contentId={}", userId, contentId);
        return contentPurchaseFacadeService.recordContentAccess(userId, contentId);
    }

    // =================== 购买记录查询 ===================

    @PostMapping("/user/purchases")
    @Operation(summary = "查询用户购买记录", description = "分页查询用户的购买历史")
    public Result<PageResponse<ContentPurchaseResponse>> getUserPurchases(
            @Valid @RequestBody ContentPurchaseQueryRequest request) {
        log.debug("REST查询用户购买记录: userId={}, page={}", 
                request.getUserId(), request.getPage());
        return contentPurchaseFacadeService.getUserPurchases(request);
    }

    @PostMapping("/content/purchases")
    @Operation(summary = "查询内容购买记录", description = "查询指定内容的购买历史（用于作者和管理员）")
    public Result<PageResponse<ContentPurchaseResponse>> getContentPurchases(
            @Valid @RequestBody ContentPurchaseQueryRequest request) {
        log.debug("REST查询内容购买记录: contentId={}, page={}", 
                request.getContentId(), request.getPage());
        return contentPurchaseFacadeService.getContentPurchases(request);
    }

    @GetMapping("/{purchaseId}")
    @Operation(summary = "获取购买记录详情", description = "根据ID获取购买记录的详细信息")
    public Result<ContentPurchaseResponse> getPurchaseDetail(
            @PathVariable("purchaseId") Long purchaseId,
            @Parameter(description = "用户ID（权限校验）") @RequestParam Long userId) {
        log.debug("REST获取购买记录详情: purchaseId={}, userId={}", purchaseId, userId);
        return contentPurchaseFacadeService.getPurchaseDetail(purchaseId, userId);
    }

    @GetMapping("/user/{userId}/valid")
    @Operation(summary = "查询用户有效购买", description = "获取用户当前有效的购买记录")
    public Result<List<ContentPurchaseResponse>> getUserValidPurchases(
            @PathVariable("userId") Long userId) {
        log.debug("REST查询用户有效购买: userId={}", userId);
        return contentPurchaseFacadeService.getUserValidPurchases(userId);
    }

    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "查询用户未读购买", description = "获取用户已购买但未访问的内容")
    public Result<List<ContentPurchaseResponse>> getUserUnreadPurchases(
            @PathVariable("userId") Long userId) {
        log.debug("REST查询用户未读购买: userId={}", userId);
        return contentPurchaseFacadeService.getUserUnreadPurchases(userId);
    }

    // =================== 购买统计 ===================

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "获取用户购买统计", description = "统计用户的购买数量、消费金额等")
    public Result<Map<String, Object>> getUserPurchaseStats(
            @PathVariable("userId") Long userId) {
        log.debug("REST获取用户购买统计: userId={}", userId);
        return contentPurchaseFacadeService.getUserPurchaseStats(userId);
    }

    @GetMapping("/stats/content/{contentId}")
    @Operation(summary = "获取内容销售统计", description = "统计内容的销量、收入等")
    public Result<Map<String, Object>> getContentSalesStats(
            @PathVariable("contentId") Long contentId) {
        log.debug("REST获取内容销售统计: contentId={}", contentId);
        return contentPurchaseFacadeService.getContentSalesStats(contentId);
    }

    @GetMapping("/stats/author/{authorId}")
    @Operation(summary = "获取作者收入统计", description = "统计作者的收入情况")
    public Result<Map<String, Object>> getAuthorRevenueStats(
            @PathVariable("authorId") Long authorId) {
        log.debug("REST获取作者收入统计: authorId={}", authorId);
        return contentPurchaseFacadeService.getAuthorRevenueStats(authorId);
    }

    @GetMapping("/stats/popular")
    @Operation(summary = "获取热门购买内容排行", description = "获取购买量最高的内容排行榜")
    public Result<List<Map<String, Object>>> getPopularContentRanking(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取热门购买内容排行: limit={}", limit);
        return contentPurchaseFacadeService.getPopularContentRanking(limit);
    }

    // =================== 订单处理 ===================

    @PostMapping("/order/{orderId}/success")
    @Operation(summary = "处理订单支付成功", description = "订单支付成功后的回调处理")
    public Result<ContentPurchaseResponse> handleOrderPaymentSuccess(
            @PathVariable("orderId") Long orderId) {
        log.info("REST处理订单支付成功: orderId={}", orderId);
        return contentPurchaseFacadeService.handleOrderPaymentSuccess(orderId);
    }

    @PostMapping("/{purchaseId}/refund")
    @Operation(summary = "处理退款申请", description = "处理用户的退款请求")
    public Result<Void> handleRefundRequest(
            @PathVariable("purchaseId") Long purchaseId,
            @Parameter(description = "退款原因") @RequestParam String reason,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        log.info("REST处理退款申请: purchaseId={}, reason={}, operatorId={}", 
                purchaseId, reason, operatorId);
        return contentPurchaseFacadeService.handleRefundRequest(purchaseId, reason, operatorId);
    }

    // =================== 推荐功能 ===================

    @GetMapping("/user/{userId}/recommendations")
    @Operation(summary = "获取用户内容推荐", description = "基于购买历史为用户推荐内容")
    public Result<List<Long>> getUserContentRecommendations(
            @PathVariable("userId") Long userId,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("REST获取用户内容推荐: userId={}, limit={}", userId, limit);
        return contentPurchaseFacadeService.getUserContentRecommendations(userId, limit);
    }

    @GetMapping("/suggestion/{userId}")
    @Operation(summary = "获取购买建议", description = "为用户提供个性化的购买建议")
    public Result<Map<String, Object>> getPurchaseSuggestion(
            @PathVariable("userId") Long userId,
            @Parameter(description = "内容ID") @RequestParam(required = false) Long contentId) {
        log.debug("REST获取购买建议: userId={}, contentId={}", userId, contentId);
        return contentPurchaseFacadeService.getPurchaseSuggestion(userId, contentId);
    }
}