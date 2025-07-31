package com.gig.collide.content.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gig.collide.content.domain.entity.UserContentPurchase;
import com.gig.collide.content.domain.service.UserContentPurchaseService;
import com.gig.collide.content.infrastructure.mapper.UserContentPurchaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户内容购买记录业务逻辑实现类
 * 管理用户的内容购买、权限验证和统计分析
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserContentPurchaseServiceImpl extends ServiceImpl<UserContentPurchaseMapper, UserContentPurchase> implements UserContentPurchaseService {

    private final UserContentPurchaseMapper userContentPurchaseMapper;

    // =================== 基础CRUD ===================

    @Override
    public UserContentPurchase createPurchase(UserContentPurchase purchase) {
        log.info("创建购买记录: userId={}, contentId={}", purchase.getUserId(), purchase.getContentId());
        
        try {
            // 检查是否已存在购买记录
            UserContentPurchase existing = userContentPurchaseMapper.selectByUserIdAndContentId(
                purchase.getUserId(), purchase.getContentId());
            if (existing != null) {
                log.warn("用户已购买该内容: userId={}, contentId={}", purchase.getUserId(), purchase.getContentId());
                return existing;
            }

            // 设置默认值
            if (purchase.getStatus() == null) {
                purchase.setStatus("ACTIVE");
            }
            if (purchase.getAccessCount() == null) {
                purchase.setAccessCount(0);
            }
            if (purchase.getPurchaseTime() == null) {
                purchase.setPurchaseTime(LocalDateTime.now());
            }

            userContentPurchaseMapper.insert(purchase);
            log.info("购买记录创建成功: id={}", purchase.getId());
            return purchase;
        } catch (Exception e) {
            log.error("创建购买记录失败: userId={}, contentId={}", purchase.getUserId(), purchase.getContentId(), e);
            throw new RuntimeException("创建购买记录失败", e);
        }
    }

    @Override
    public UserContentPurchase getPurchaseById(Long id) {
        return userContentPurchaseMapper.selectById(id);
    }

    @Override
    public UserContentPurchase updatePurchase(UserContentPurchase purchase) {
        log.info("更新购买记录: id={}", purchase.getId());
        
        try {
            userContentPurchaseMapper.updateById(purchase);
            return userContentPurchaseMapper.selectById(purchase.getId());
        } catch (Exception e) {
            log.error("更新购买记录失败: id={}", purchase.getId(), e);
            throw new RuntimeException("更新购买记录失败", e);
        }
    }

    @Override
    public boolean deletePurchase(Long id, Long operatorId) {
        log.info("删除购买记录: id={}, operatorId={}", id, operatorId);
        
        try {
            // 这里实现逻辑删除，将状态设置为REFUNDED
            UserContentPurchase purchase = new UserContentPurchase();
            purchase.setId(id);
            purchase.setStatus("REFUNDED");
            return userContentPurchaseMapper.updateById(purchase) > 0;
        } catch (Exception e) {
            log.error("删除购买记录失败: id={}", id, e);
            return false;
        }
    }

    // =================== 权限验证 ===================

    @Override
    public UserContentPurchase getUserContentPurchase(Long userId, Long contentId) {
        return userContentPurchaseMapper.selectByUserIdAndContentId(userId, contentId);
    }

    @Override
    public boolean hasAccessPermission(Long userId, Long contentId) {
        UserContentPurchase purchase = userContentPurchaseMapper.selectValidPurchase(userId, contentId);
        return purchase != null && purchase.hasAccessPermission();
    }

    @Override
    public UserContentPurchase getValidPurchase(Long userId, Long contentId) {
        return userContentPurchaseMapper.selectValidPurchase(userId, contentId);
    }

    @Override
    public Map<Long, Boolean> batchCheckAccessPermission(Long userId, List<Long> contentIds) {
        return contentIds.stream().collect(Collectors.toMap(
            contentId -> contentId,
            contentId -> hasAccessPermission(userId, contentId)
        ));
    }

    // =================== 查询功能 ===================

    @Override
    public List<UserContentPurchase> getUserPurchases(Long userId, Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return userContentPurchaseMapper.selectByUserId(userId, offset, size);
    }

    @Override
    public List<UserContentPurchase> getUserValidPurchases(Long userId) {
        return userContentPurchaseMapper.selectValidPurchasesByUserId(userId);
    }

    @Override
    public List<UserContentPurchase> getContentPurchases(Long contentId, Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return userContentPurchaseMapper.selectByContentId(contentId, offset, size);
    }

    @Override
    public UserContentPurchase getPurchaseByOrderId(Long orderId) {
        return userContentPurchaseMapper.selectByOrderId(orderId);
    }

    @Override
    public UserContentPurchase getPurchaseByOrderNo(String orderNo) {
        return userContentPurchaseMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<UserContentPurchase> getUserPurchasesByContentType(Long userId, String contentType) {
        return userContentPurchaseMapper.selectByUserIdAndContentType(userId, contentType);
    }

    @Override
    public List<UserContentPurchase> getUserPurchasesByAuthor(Long userId, Long authorId) {
        return userContentPurchaseMapper.selectByUserIdAndAuthorId(userId, authorId);
    }

    @Override
    public List<UserContentPurchase> getUserRecentPurchases(Long userId, Integer limit) {
        return userContentPurchaseMapper.selectRecentPurchases(userId, limit);
    }

    @Override
    public List<UserContentPurchase> getUserUnreadPurchases(Long userId) {
        return userContentPurchaseMapper.selectUnreadPurchases(userId);
    }

    // =================== 访问记录管理 ===================

    @Override
    public boolean recordContentAccess(Long userId, Long contentId) {
        log.debug("记录内容访问: userId={}, contentId={}", userId, contentId);
        
        try {
            UserContentPurchase purchase = userContentPurchaseMapper.selectValidPurchase(userId, contentId);
            if (purchase == null) {
                log.warn("用户未购买该内容，无法记录访问: userId={}, contentId={}", userId, contentId);
                return false;
            }

            int newAccessCount = (purchase.getAccessCount() != null ? purchase.getAccessCount() : 0) + 1;
            return userContentPurchaseMapper.updateAccessStats(
                purchase.getId(), newAccessCount, LocalDateTime.now()) > 0;
        } catch (Exception e) {
            log.error("记录内容访问失败: userId={}, contentId={}", userId, contentId, e);
            return false;
        }
    }

    @Override
    public boolean batchUpdateAccessStats(List<Long> purchaseIds) {
        // 这里可以实现批量更新逻辑
        return true;
    }

    // =================== 状态管理 ===================

    @Override
    public int processExpiredPurchases() {
        log.info("处理过期的购买记录");
        
        try {
            return userContentPurchaseMapper.batchExpirePurchases(LocalDateTime.now());
        } catch (Exception e) {
            log.error("处理过期购买记录失败", e);
            return 0;
        }
    }

    @Override
    public List<UserContentPurchase> getExpiringSoonPurchases(LocalDateTime beforeTime) {
        return userContentPurchaseMapper.selectExpiringSoon(beforeTime);
    }

    @Override
    public List<UserContentPurchase> getExpiredPurchases() {
        return userContentPurchaseMapper.selectExpiredPurchases();
    }

    @Override
    public boolean batchUpdateStatus(List<Long> ids, String status) {
        log.info("批量更新购买记录状态: count={}, status={}", ids.size(), status);
        
        try {
            return userContentPurchaseMapper.batchUpdateStatus(ids, status) > 0;
        } catch (Exception e) {
            log.error("批量更新购买记录状态失败", e);
            return false;
        }
    }

    @Override
    public boolean refundPurchase(Long purchaseId, String reason, Long operatorId) {
        log.info("退款处理: purchaseId={}, reason={}, operatorId={}", purchaseId, reason, operatorId);
        
        try {
            UserContentPurchase purchase = new UserContentPurchase();
            purchase.setId(purchaseId);
            purchase.setStatus("REFUNDED");
            return userContentPurchaseMapper.updateById(purchase) > 0;
        } catch (Exception e) {
            log.error("退款处理失败: purchaseId={}", purchaseId, e);
            return false;
        }
    }

    // =================== 统计分析 ===================

    @Override
    public Long countUserPurchases(Long userId) {
        return userContentPurchaseMapper.countByUserId(userId);
    }

    @Override
    public Long countUserValidPurchases(Long userId) {
        return userContentPurchaseMapper.countValidByUserId(userId);
    }

    @Override
    public Long countContentPurchases(Long contentId) {
        return userContentPurchaseMapper.countByContentId(contentId);
    }

    @Override
    public Long sumContentRevenue(Long contentId) {
        return userContentPurchaseMapper.sumRevenueByContentId(contentId);
    }

    @Override
    public Long sumUserExpense(Long userId) {
        return userContentPurchaseMapper.sumExpenseByUserId(userId);
    }

    @Override
    public List<Map<String, Object>> getPopularContentRanking(Integer limit) {
        return userContentPurchaseMapper.getPopularContentRanking(limit);
    }

    @Override
    public Map<String, Object> getUserPurchaseStats(Long userId) {
        return userContentPurchaseMapper.getUserPurchaseStats(userId);
    }

    @Override
    public Map<String, Object> getContentSalesStats(Long contentId) {
        return userContentPurchaseMapper.getContentSalesStats(contentId);
    }

    @Override
    public Map<String, Object> getAuthorRevenueStats(Long authorId) {
        return userContentPurchaseMapper.getAuthorRevenueStats(authorId);
    }

    @Override
    public List<Map<String, Object>> getPurchaseStatsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return userContentPurchaseMapper.getPurchaseStatsByDateRange(startDate, endDate);
    }

    // =================== 业务逻辑 ===================

    @Override
    public UserContentPurchase handleOrderPaymentSuccess(Long orderId) {
        log.info("处理订单支付成功: orderId={}", orderId);
        
        try {
            // 这里应该根据订单信息创建购买记录
            // 需要集成订单模块的接口来获取订单详情
            UserContentPurchase purchase = userContentPurchaseMapper.selectByOrderId(orderId);
            if (purchase != null) {
                purchase.setStatus("ACTIVE");
                purchase.setPurchaseTime(LocalDateTime.now());
                userContentPurchaseMapper.updateById(purchase);
            }
            return purchase;
        } catch (Exception e) {
            log.error("处理订单支付成功失败: orderId={}", orderId, e);
            return null;
        }
    }

    @Override
    public boolean validatePurchasePermission(Long userId, Long contentId) {
        // 这里应该集成用户模块和内容模块的接口
        // 检查用户VIP状态、内容VIP权限要求等
        return true; // 简化实现
    }

    @Override
    public Map<String, Object> calculateContentAccess(Long userId, Long contentId) {
        // 这里应该综合考虑多种访问权限
        // 包括购买状态、VIP权限、试读权限等
        return Map.of(
            "hasAccess", hasAccessPermission(userId, contentId),
            "accessType", "PURCHASED",
            "purchase", getValidPurchase(userId, contentId)
        );
    }

    @Override
    public List<Long> getUserContentRecommendations(Long userId, Integer limit) {
        // 这里应该实现基于购买历史的推荐算法
        // 暂时返回空列表
        return List.of();
    }
}