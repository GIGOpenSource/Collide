package com.gig.collide.content.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gig.collide.content.domain.entity.ContentPayment;
import com.gig.collide.content.domain.service.ContentPaymentService;
import com.gig.collide.content.infrastructure.mapper.ContentPaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 内容付费配置业务逻辑实现类
 * 管理内容的付费策略、价格配置和销售统计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ContentPaymentServiceImpl extends ServiceImpl<ContentPaymentMapper, ContentPayment> implements ContentPaymentService {

    private final ContentPaymentMapper contentPaymentMapper;

    // =================== 基础CRUD ===================

    @Override
    public ContentPayment createPaymentConfig(ContentPayment config) {
        log.info("创建付费配置: contentId={}, paymentType={}", config.getContentId(), config.getPaymentType());
        
        try {
            // 检查是否已存在配置
            ContentPayment existing = contentPaymentMapper.selectByContentId(config.getContentId());
            if (existing != null) {
                log.warn("内容已存在付费配置: contentId={}", config.getContentId());
                throw new RuntimeException("内容已存在付费配置");
            }

            // 设置默认值
            if (config.getStatus() == null) {
                config.setStatus("ACTIVE");
            }
            if (config.getTotalSales() == null) {
                config.setTotalSales(0L);
            }
            if (config.getTotalRevenue() == null) {
                config.setTotalRevenue(0L);
            }
            if (config.getVipFree() == null) {
                config.setVipFree(false);
            }
            if (config.getVipOnly() == null) {
                config.setVipOnly(false);
            }
            if (config.getTrialEnabled() == null) {
                config.setTrialEnabled(false);
            }
            if (config.getIsPermanent() == null) {
                config.setIsPermanent(true);
            }

            contentPaymentMapper.insert(config);
            log.info("付费配置创建成功: id={}", config.getId());
            return config;
        } catch (Exception e) {
            log.error("创建付费配置失败: contentId={}", config.getContentId(), e);
            throw new RuntimeException("创建付费配置失败", e);
        }
    }

    @Override
    public ContentPayment getPaymentConfigById(Long id) {
        return contentPaymentMapper.selectById(id);
    }

    @Override
    public ContentPayment getPaymentConfigByContentId(Long contentId) {
        return contentPaymentMapper.selectByContentId(contentId);
    }

    @Override
    public ContentPayment updatePaymentConfig(ContentPayment config) {
        log.info("更新付费配置: id={}", config.getId());
        
        try {
            contentPaymentMapper.updateById(config);
            return contentPaymentMapper.selectById(config.getId());
        } catch (Exception e) {
            log.error("更新付费配置失败: id={}", config.getId(), e);
            throw new RuntimeException("更新付费配置失败", e);
        }
    }

    @Override
    public boolean deletePaymentConfig(Long id, Long operatorId) {
        log.info("删除付费配置: id={}, operatorId={}", id, operatorId);
        
        try {
            return contentPaymentMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("删除付费配置失败: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean deleteByContentId(Long contentId, Long operatorId) {
        log.info("删除内容的付费配置: contentId={}, operatorId={}", contentId, operatorId);
        
        try {
            return contentPaymentMapper.deleteByContentId(contentId) > 0;
        } catch (Exception e) {
            log.error("删除内容付费配置失败: contentId={}", contentId, e);
            return false;
        }
    }

    // =================== 查询功能 ===================

    @Override
    public List<ContentPayment> getConfigsByPaymentType(String paymentType) {
        return contentPaymentMapper.selectByPaymentType(paymentType);
    }

    @Override
    public List<ContentPayment> getFreeContentConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectFreeContent(offset, size);
    }

    @Override
    public List<ContentPayment> getCoinPayContentConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectCoinPayContent(offset, size);
    }

    @Override
    public List<ContentPayment> getVipFreeContentConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectVipFreeContent(offset, size);
    }

    @Override
    public List<ContentPayment> getVipOnlyContentConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectVipOnlyContent(offset, size);
    }

    @Override
    public List<ContentPayment> getConfigsByPriceRange(Long minPrice, Long maxPrice) {
        return contentPaymentMapper.selectByPriceRange(minPrice, maxPrice);
    }

    @Override
    public List<ContentPayment> getTrialEnabledConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectTrialEnabledContent(offset, size);
    }

    @Override
    public List<ContentPayment> getPermanentContentConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectPermanentContent(offset, size);
    }

    @Override
    public List<ContentPayment> getTimeLimitedConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectTimeLimitedContent(offset, size);
    }

    @Override
    public List<ContentPayment> getDiscountedConfigs(Integer page, Integer size) {
        Long offset = (long) ((page - 1) * size);
        return contentPaymentMapper.selectDiscountedContent(offset, size);
    }

    @Override
    public List<ContentPayment> getConfigsByStatus(String status) {
        return contentPaymentMapper.selectByStatus(status);
    }

    // =================== 销售统计管理 ===================

    @Override
    public boolean updateSalesStats(Long contentId, Long salesIncrement, Long revenueIncrement) {
        log.debug("更新销售统计: contentId={}, sales={}, revenue={}", contentId, salesIncrement, revenueIncrement);
        
        try {
            return contentPaymentMapper.updateSalesStats(contentId, salesIncrement, revenueIncrement) > 0;
        } catch (Exception e) {
            log.error("更新销售统计失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean batchUpdateSalesStats(Map<Long, Map<String, Long>> statsMap) {
        log.info("批量更新销售统计: count={}", statsMap.size());
        
        try {
            for (Map.Entry<Long, Map<String, Long>> entry : statsMap.entrySet()) {
                Long contentId = entry.getKey();
                Map<String, Long> stats = entry.getValue();
                Long salesIncrement = stats.get("sales");
                Long revenueIncrement = stats.get("revenue");
                contentPaymentMapper.updateSalesStats(contentId, salesIncrement, revenueIncrement);
            }
            return true;
        } catch (Exception e) {
            log.error("批量更新销售统计失败", e);
            return false;
        }
    }

    @Override
    public boolean resetSalesStats(Long contentId) {
        log.info("重置销售统计: contentId={}", contentId);
        
        try {
            ContentPayment config = new ContentPayment();
            config.setContentId(contentId);
            config.setTotalSales(0L);
            config.setTotalRevenue(0L);
            return contentPaymentMapper.updateById(config) > 0;
        } catch (Exception e) {
            log.error("重置销售统计失败: contentId={}", contentId, e);
            return false;
        }
    }

    // =================== 状态管理 ===================

    @Override
    public boolean batchUpdateStatus(List<Long> contentIds, String status) {
        log.info("批量更新付费配置状态: count={}, status={}", contentIds.size(), status);
        
        try {
            return contentPaymentMapper.batchUpdateStatus(contentIds, status) > 0;
        } catch (Exception e) {
            log.error("批量更新付费配置状态失败", e);
            return false;
        }
    }

    @Override
    public boolean enablePaymentConfig(Long contentId, Long operatorId) {
        log.info("启用付费配置: contentId={}, operatorId={}", contentId, operatorId);
        return batchUpdateStatus(List.of(contentId), "ACTIVE");
    }

    @Override
    public boolean disablePaymentConfig(Long contentId, Long operatorId) {
        log.info("禁用付费配置: contentId={}, operatorId={}", contentId, operatorId);
        return batchUpdateStatus(List.of(contentId), "INACTIVE");
    }

    // =================== 权限验证 ===================

    @Override
    public boolean checkPurchasePermission(Long userId, Long contentId) {
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null) {
                return false; // 无配置，默认不允许购买
            }

            // 检查是否VIP专享
            if (config.isVipOnlyPurchase()) {
                // 这里应该检查用户是否为VIP
                // 暂时简化实现
                return true;
            }

            return config.isActive();
        } catch (Exception e) {
            log.error("检查购买权限失败: userId={}, contentId={}", userId, contentId, e);
            return false;
        }
    }

    @Override
    public boolean checkFreeAccess(Long userId, Long contentId) {
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null) {
                return false;
            }

            // 检查是否免费内容
            if (config.isFree()) {
                return true;
            }

            // 检查是否VIP免费且用户是VIP
            if (Boolean.TRUE.equals(config.getVipFree())) {
                // 这里应该检查用户是否为VIP
                // 暂时简化实现
                return false;
            }

            return false;
        } catch (Exception e) {
            log.error("检查免费访问权限失败: userId={}, contentId={}", userId, contentId, e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getAccessPolicy(Long userId, Long contentId) {
        Map<String, Object> policy = new HashMap<>();
        
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null) {
                policy.put("canAccess", false);
                policy.put("reason", "NO_CONFIG");
                return policy;
            }

            policy.put("paymentType", config.getPaymentType());
            policy.put("coinPrice", config.getCoinPrice());
            policy.put("originalPrice", config.getOriginalPrice());
            policy.put("vipFree", config.getVipFree());
            policy.put("vipOnly", config.getVipOnly());
            policy.put("trialEnabled", config.getTrialEnabled());
            policy.put("isPermanent", config.getIsPermanent());
            policy.put("validDays", config.getValidDays());

            // 判断用户访问权限
            boolean canAccessFree = checkFreeAccess(userId, contentId);
            boolean canPurchase = checkPurchasePermission(userId, contentId);

            policy.put("canAccessFree", canAccessFree);
            policy.put("canPurchase", canPurchase);
            policy.put("needPayment", !canAccessFree);

            return policy;
        } catch (Exception e) {
            log.error("获取访问策略失败: userId={}, contentId={}", userId, contentId, e);
            policy.put("canAccess", false);
            policy.put("reason", "ERROR");
            return policy;
        }
    }

    @Override
    public Map<Long, Map<String, Object>> batchGetAccessPolicy(Long userId, List<Long> contentIds) {
        return contentIds.stream().collect(Collectors.toMap(
            contentId -> contentId,
            contentId -> getAccessPolicy(userId, contentId)
        ));
    }

    // =================== 价格策略 ===================

    @Override
    public Long calculateActualPrice(Long userId, Long contentId) {
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null) {
                return 0L;
            }

            // 如果可以免费访问，价格为0
            if (checkFreeAccess(userId, contentId)) {
                return 0L;
            }

            return config.getEffectivePrice();
        } catch (Exception e) {
            log.error("计算实际价格失败: userId={}, contentId={}", userId, contentId, e);
            return 0L;
        }
    }

    @Override
    public Map<String, Object> getContentPriceInfo(Long contentId) {
        Map<String, Object> priceInfo = new HashMap<>();
        
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null) {
                priceInfo.put("available", false);
                return priceInfo;
            }

            priceInfo.put("available", true);
            priceInfo.put("paymentType", config.getPaymentType());
            priceInfo.put("coinPrice", config.getCoinPrice());
            priceInfo.put("originalPrice", config.getOriginalPrice());
            priceInfo.put("hasDiscount", config.hasDiscount());
            priceInfo.put("discountRate", config.getDiscountRate());
            priceInfo.put("effectivePrice", config.getEffectivePrice());

            return priceInfo;
        } catch (Exception e) {
            log.error("获取内容价格信息失败: contentId={}", contentId, e);
            priceInfo.put("available", false);
            return priceInfo;
        }
    }

    @Override
    public Map<Long, Map<String, Object>> batchGetContentPriceInfo(List<Long> contentIds) {
        return contentIds.stream().collect(Collectors.toMap(
            contentId -> contentId,
            this::getContentPriceInfo
        ));
    }

    // =================== 推荐功能 ===================

    @Override
    public List<ContentPayment> getHotPaidContent(Integer limit) {
        return contentPaymentMapper.selectHotPaidContent(limit);
    }

    @Override
    public List<ContentPayment> getHighValueContent(Integer limit) {
        return contentPaymentMapper.selectHighValueContent(limit);
    }

    @Override
    public List<ContentPayment> getValueForMoneyContent(Integer limit) {
        return contentPaymentMapper.selectValueForMoneyContent(limit);
    }

    @Override
    public List<ContentPayment> getNewPaidContent(Integer limit) {
        return contentPaymentMapper.selectNewPaidContent(limit);
    }

    @Override
    public List<ContentPayment> getSalesRanking(Integer limit) {
        return contentPaymentMapper.getSalesRanking(limit);
    }

    @Override
    public List<ContentPayment> getRevenueRanking(Integer limit) {
        return contentPaymentMapper.getRevenueRanking(limit);
    }

    // =================== 统计分析 ===================

    @Override
    public Map<String, Long> countByPaymentType() {
        List<Map<String, Object>> results = contentPaymentMapper.countByPaymentType();
        return results.stream().collect(Collectors.toMap(
            result -> (String) result.get("payment_type"),
            result -> (Long) result.get("count")
        ));
    }

    @Override
    public Long countActiveConfigs() {
        return contentPaymentMapper.countActiveConfigs();
    }

    @Override
    public Map<String, Object> getPriceStats() {
        return contentPaymentMapper.getPriceStats();
    }

    @Override
    public Map<String, Object> getTotalSalesStats() {
        return contentPaymentMapper.getTotalSalesStats();
    }

    @Override
    public List<Map<String, Object>> getMonthlySalesStats(Integer months) {
        return contentPaymentMapper.getMonthlySalesStats(months);
    }

    @Override
    public Map<String, Object> getConversionStats() {
        return contentPaymentMapper.getConversionStats();
    }

    // =================== 业务逻辑 ===================

    @Override
    public Map<String, Object> handleContentPurchase(Long userId, Long contentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取访问策略
            Map<String, Object> policy = getAccessPolicy(userId, contentId);
            
            if (!Boolean.TRUE.equals(policy.get("canPurchase"))) {
                result.put("success", false);
                result.put("message", "无购买权限");
                return result;
            }

            // 计算实际价格
            Long actualPrice = calculateActualPrice(userId, contentId);
            
            result.put("success", true);
            result.put("actualPrice", actualPrice);
            result.put("policy", policy);
            
            return result;
        } catch (Exception e) {
            log.error("处理内容购买失败: userId={}, contentId={}", userId, contentId, e);
            result.put("success", false);
            result.put("message", "处理失败");
            return result;
        }
    }

    @Override
    public Map<String, Object> handleTrialRequest(Long userId, Long contentId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            ContentPayment config = contentPaymentMapper.selectByContentId(contentId);
            if (config == null || !config.supportsTrial()) {
                result.put("success", false);
                result.put("message", "不支持试读");
                return result;
            }

            result.put("success", true);
            result.put("trialContent", config.getTrialContent());
            result.put("trialWordCount", config.getTrialWordCount());
            
            return result;
        } catch (Exception e) {
            log.error("处理试读申请失败: userId={}, contentId={}", userId, contentId, e);
            result.put("success", false);
            result.put("message", "处理失败");
            return result;
        }
    }

    @Override
    public boolean syncContentStatus(Long contentId, String contentStatus) {
        log.info("同步内容状态: contentId={}, contentStatus={}", contentId, contentStatus);
        
        try {
            String paymentStatus = "OFFLINE".equals(contentStatus) ? "INACTIVE" : "ACTIVE";
            return batchUpdateStatus(List.of(contentId), paymentStatus);
        } catch (Exception e) {
            log.error("同步内容状态失败: contentId={}", contentId, e);
            return false;
        }
    }

    @Override
    public boolean batchSyncContentStatus(Map<Long, String> contentStatusMap) {
        log.info("批量同步内容状态: count={}", contentStatusMap.size());
        
        try {
            for (Map.Entry<Long, String> entry : contentStatusMap.entrySet()) {
                syncContentStatus(entry.getKey(), entry.getValue());
            }
            return true;
        } catch (Exception e) {
            log.error("批量同步内容状态失败", e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getContentRevenueAnalysis(Long contentId) {
        // 这里可以实现更复杂的收益分析逻辑
        return Map.of(
            "contentId", contentId,
            "analysis", "revenue_analysis_placeholder"
        );
    }

    @Override
    public Map<String, Object> getPriceOptimizationSuggestion(Long contentId) {
        // 这里可以实现价格优化建议算法
        return Map.of(
            "contentId", contentId,
            "suggestion", "price_optimization_placeholder"
        );
    }
}