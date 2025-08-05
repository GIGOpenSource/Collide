package com.gig.collide.content.domain.service.impl;

import com.gig.collide.content.domain.entity.UserContentPurchase;
import com.gig.collide.content.domain.service.UserContentPurchaseService;
import com.gig.collide.content.infrastructure.mapper.UserContentPurchaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户内容购买记录业务服务实现
 * 极简版 - 严格12个方法，大量使用通用查询
 * 
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserContentPurchaseServiceImpl implements UserContentPurchaseService {

    private final UserContentPurchaseMapper userContentPurchaseMapper;

    // =================== 核心CRUD功能（2个方法）===================

    @Override
    public UserContentPurchase createPurchase(UserContentPurchase purchase) {
        log.info("创建购买记录: userId={}, contentId={}", 
                purchase.getUserId(), purchase.getContentId());
        
        // 基础验证
        if (purchase.getUserId() == null || purchase.getContentId() == null) {
            throw new IllegalArgumentException("用户ID和内容ID不能为空");
        }
        
        // 检查是否已经购买过 - 使用通用查询
        List<UserContentPurchase> existing = getPurchasesByConditions(
            purchase.getUserId(), purchase.getContentId(), null, null, null, null,
            null, null, null, null, null, null, null, 1
        );
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("该用户已购买过此内容");
        }
        
        // 设置默认值
        if (purchase.getCreateTime() == null) {
            purchase.setCreateTime(LocalDateTime.now());
        }
        if (purchase.getUpdateTime() == null) {
            purchase.setUpdateTime(LocalDateTime.now());
        }
        if (!StringUtils.hasText(purchase.getStatus())) {
            purchase.setStatus("ACTIVE");
        }
        if (purchase.getAccessCount() == null) {
            purchase.setAccessCount(0);
        }
        
        userContentPurchaseMapper.insert(purchase);
        log.info("购买记录创建成功: id={}", purchase.getId());
        return purchase;
    }

    @Override
    public UserContentPurchase getPurchaseById(Long id) {
        log.debug("获取购买记录详情: id={}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("购买记录ID不能为空");
        }
        
        return userContentPurchaseMapper.selectById(id);
    }

    // =================== 万能查询功能（3个方法）===================

    @Override
    public List<UserContentPurchase> getPurchasesByConditions(Long userId, Long contentId, String contentType,
                                                             Long authorId, String status, Boolean isValid,
                                                             Long minAmount, Long maxAmount, Integer minAccessCount, Boolean isUnread,
                                                             String orderBy, String orderDirection,
                                                             Integer currentPage, Integer pageSize) {
        log.debug("万能条件查询购买记录: userId={}, contentId={}, contentType={}", 
                 userId, contentId, contentType);
        
        return userContentPurchaseMapper.selectPurchasesByConditions(
            userId, contentId, contentType, authorId, status, isValid,
            minAmount, maxAmount, minAccessCount, isUnread,
            orderBy, orderDirection, currentPage, pageSize
        );
    }

    @Override
    public List<UserContentPurchase> getRecommendedPurchases(String strategy, Long userId, String contentType,
                                                            List<Long> excludeContentIds, Integer limit) {
        log.debug("推荐购买记录查询: strategy={}, userId={}, contentType={}", 
                 strategy, userId, contentType);
        
        if (!StringUtils.hasText(strategy)) {
            throw new IllegalArgumentException("推荐策略不能为空");
        }
        
        return userContentPurchaseMapper.selectRecommendedPurchases(strategy, userId, contentType, 
                                                                   excludeContentIds, limit);
    }

    @Override
    public List<UserContentPurchase> getPurchasesByExpireStatus(String type, LocalDateTime beforeTime,
                                                               Long userId, Integer limit) {
        log.debug("过期状态查询购买记录: type={}, beforeTime={}, userId={}", type, beforeTime, userId);
        
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("查询类型不能为空");
        }
        
        return userContentPurchaseMapper.selectByExpireStatus(type, beforeTime, userId, limit);
    }

    // =================== 权限验证功能（1个方法）===================

    @Override
    public boolean checkAccessPermission(Long userId, Long contentId) {
        log.debug("检查访问权限: userId={}, contentId={}", userId, contentId);
        
        // 使用万能查询检查购买记录
        List<UserContentPurchase> purchases = getPurchasesByConditions(
            userId, contentId, null, null, null, null,
            null, null, null, null, null, null, null, 1
        );
        
        if (purchases.isEmpty()) {
            return false;
        }
        
        UserContentPurchase purchase = purchases.get(0);
        
        // 检查状态
        if (!"ACTIVE".equals(purchase.getStatus())) {
            return false;
        }
        
        // 检查是否过期
        if (purchase.getExpireTime() != null && purchase.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        return true;
    }

    // =================== 状态管理功能（4个方法）===================

    @Override
    public boolean updatePurchaseStatus(Long purchaseId, String status) {
        log.info("更新购买记录状态: purchaseId={}, status={}", purchaseId, status);
        
        if (purchaseId == null || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("购买记录ID和状态不能为空");
        }
        
        try {
            int result = userContentPurchaseMapper.updatePurchaseStatus(purchaseId, status);
            boolean success = result > 0;
            if (success) {
                log.info("购买记录状态更新成功: purchaseId={}", purchaseId);
            }
            return success;
        } catch (Exception e) {
            log.error("购买记录状态更新失败: purchaseId={}", purchaseId, e);
            return false;
        }
    }

    @Override
    public boolean batchUpdateStatus(List<Long> ids, String status) {
        log.info("批量更新购买记录状态: ids.size={}, status={}", 
                ids != null ? ids.size() : 0, status);
        
        if (ids == null || ids.isEmpty() || !StringUtils.hasText(status)) {
            throw new IllegalArgumentException("购买记录ID列表和状态不能为空");
        }
        
        try {
            int result = userContentPurchaseMapper.batchUpdateStatus(ids, status);
            boolean success = result > 0;
            if (success) {
                log.info("批量更新购买记录状态成功: 影响行数={}", result);
            }
            return success;
        } catch (Exception e) {
            log.error("批量更新购买记录状态失败", e);
            return false;
        }
    }

    @Override
    public int processExpiredPurchases(LocalDateTime beforeTime) {
        log.info("处理过期购买记录: beforeTime={}", beforeTime);
        
        if (beforeTime == null) {
            beforeTime = LocalDateTime.now();
        }
        
        try {
            int result = userContentPurchaseMapper.batchExpirePurchases(beforeTime);
            log.info("处理过期购买记录成功: 影响行数={}", result);
            return result;
        } catch (Exception e) {
            log.error("处理过期购买记录失败", e);
            return 0;
        }
    }

    @Override
    public boolean deletePurchase(Long purchaseId) {
        log.info("软删除购买记录: purchaseId={}", purchaseId);
        
        if (purchaseId == null) {
            throw new IllegalArgumentException("购买记录ID不能为空");
        }
        
        try {
            int result = userContentPurchaseMapper.softDeletePurchase(purchaseId);
            boolean success = result > 0;
            if (success) {
                log.info("购买记录软删除成功: purchaseId={}", purchaseId);
            }
            return success;
        } catch (Exception e) {
            log.error("购买记录软删除失败: purchaseId={}", purchaseId, e);
            return false;
        }
    }

    // =================== 访问统计功能（1个方法）===================

    @Override
    public boolean recordContentAccess(Long userId, Long contentId) {
        log.debug("记录内容访问: userId={}, contentId={}", userId, contentId);
        
        // 检查访问权限
        if (!checkAccessPermission(userId, contentId)) {
            log.warn("用户无权访问内容: userId={}, contentId={}", userId, contentId);
            return false;
        }
        
        // 使用万能查询获取购买记录
        List<UserContentPurchase> purchases = getPurchasesByConditions(
            userId, contentId, null, null, null, null,
            null, null, null, null, null, null, null, 1
        );
        
        if (purchases.isEmpty()) {
            return false;
        }
        
        UserContentPurchase purchase = purchases.get(0);
        
        // 增加访问次数并更新访问时间
        Integer newAccessCount = purchase.getAccessCount() + 1;
        LocalDateTime now = LocalDateTime.now();
        
        try {
            int result = userContentPurchaseMapper.updateAccessStats(purchase.getId(), newAccessCount, now);
            return result > 0;
        } catch (Exception e) {
            log.error("更新访问统计失败: purchaseId={}", purchase.getId(), e);
            return false;
        }
    }

    // =================== 业务逻辑功能（1个方法）===================

    @Override
    public UserContentPurchase completePurchase(Long userId, Long contentId, Long orderId, String orderNo,
                                               Long purchaseAmount, Long originalPrice, LocalDateTime expireTime) {
        log.info("处理内容购买完成: userId={}, contentId={}, orderId={}", userId, contentId, orderId);
        
        // 创建购买记录
        UserContentPurchase purchase = new UserContentPurchase();
        purchase.setUserId(userId);
        purchase.setContentId(contentId);
        purchase.setOrderId(orderId);
        purchase.setOrderNo(orderNo);
        purchase.setCoinAmount(purchaseAmount);
        purchase.setOriginalPrice(originalPrice);
        purchase.setExpireTime(expireTime);
        purchase.setPurchaseTime(LocalDateTime.now());
        
        return createPurchase(purchase);
    }
}