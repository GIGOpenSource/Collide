package com.gig.collide.content.domain.service;

import com.gig.collide.content.domain.entity.ContentPayment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容付费配置业务服务接口
 * 极简版 - 12个核心方法，使用通用查询
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
public interface ContentPaymentService {

    // =================== 核心CRUD功能（4个方法）===================

    /**
     * 创建付费配置
     */
    ContentPayment createPaymentConfig(ContentPayment config);

    /**
     * 更新付费配置
     */
    ContentPayment updatePaymentConfig(ContentPayment config);

    /**
     * 根据ID获取付费配置
     */
    ContentPayment getPaymentConfigById(Long id);

    /**
     * 删除付费配置
     */
    boolean deletePaymentConfig(Long id, Long operatorId);

    // =================== 万能查询功能（2个方法）===================

    /**
     * 万能条件查询付费配置列表 - 替代所有具体查询
     * 可实现：getPaymentConfigByContentId, getFreeContentConfigs, getCoinPayContentConfigs等
     */
    List<ContentPayment> getPaymentsByConditions(String paymentType, String status,
                                                Long minPrice, Long maxPrice,
                                                Boolean trialEnabled, Boolean isPermanent, Boolean hasDiscount,
                                                String orderBy, String orderDirection,
                                                Integer currentPage, Integer pageSize);

    /**
     * 推荐付费内容查询
     */
    List<ContentPayment> getRecommendedPayments(String strategy, String paymentType,
                                               List<Long> excludeContentIds, Integer limit);

    // =================== 状态管理功能（2个方法）===================

    /**
     * 更新付费配置状态
     */
    boolean updatePaymentStatus(Long configId, String status);

    /**
     * 批量更新状态
     */
    boolean batchUpdateStatus(List<Long> ids, String status);

    // =================== 价格管理功能（2个方法）===================

    /**
     * 更新付费配置价格信息
     */
    boolean updatePaymentPrice(Long configId, Long price, Long originalPrice,
                              LocalDateTime discountStartTime, LocalDateTime discountEndTime);

    /**
     * 计算用户实际需要支付的价格
     */
    Long calculateActualPrice(Long userId, Long contentId);

    // =================== 权限验证功能（1个方法）===================

    /**
     * 检查访问权限（包含购买权限和免费访问检查）
     */
    boolean checkAccessPermission(Long userId, Long contentId);

    // =================== 销售统计功能（1个方法）===================

    /**
     * 更新销售统计
     */
    boolean updateSalesStats(Long configId, Long salesIncrement, Long revenueIncrement);
}