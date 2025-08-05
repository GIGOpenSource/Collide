package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.ContentPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 内容付费配置表数据映射接口
 * 专注于C端必需的付费配置查询功能
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentPaymentMapper extends BaseMapper<ContentPayment> {

    // =================== C端必需的核心查询方法 ===================

    /**
     * 根据内容ID查询付费配置
     */
    ContentPayment selectByContentId(@Param("contentId") Long contentId);

    // =================== C端必需的通用查询方法 ===================

    /**
     * 通用条件查询付费配置列表
     * @param paymentType 付费类型（可选：FREE、COIN_PAY、VIP_FREE、VIP_ONLY）
     * @param status 状态（可选）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @param trialEnabled 是否支持试读（可选）
     * @param isPermanent 是否永久有效（可选）
     * @param hasDiscount 是否有折扣（可选）
     * @param orderBy 排序字段（可选：price、salesCount、createTime、revenue）
     * @param orderDirection 排序方向（可选：ASC、DESC）
     * @param currentPage 当前页码（可选，不分页时传null）
     * @param pageSize 页面大小（可选，不分页时传null）
     */
    List<ContentPayment> selectPaymentsByConditions(@Param("paymentType") String paymentType,
                                                   @Param("status") String status,
                                                   @Param("minPrice") Long minPrice,
                                                   @Param("maxPrice") Long maxPrice,
                                                   @Param("trialEnabled") Boolean trialEnabled,
                                                   @Param("isPermanent") Boolean isPermanent,
                                                   @Param("hasDiscount") Boolean hasDiscount,
                                                   @Param("orderBy") String orderBy,
                                                   @Param("orderDirection") String orderDirection,
                                                   @Param("currentPage") Integer currentPage,
                                                   @Param("pageSize") Integer pageSize);

    /**
     * 推荐付费内容查询
     * @param strategy 推荐策略（HOT、HIGH_VALUE、VALUE_FOR_MONEY、NEW）
     * @param paymentType 付费类型筛选（可选）
     * @param excludeContentIds 排除的内容ID列表（可选）
     * @param limit 返回数量限制
     */
    List<ContentPayment> selectRecommendedPayments(@Param("strategy") String strategy,
                                                  @Param("paymentType") String paymentType,
                                                  @Param("excludeContentIds") List<Long> excludeContentIds,
                                                  @Param("limit") Integer limit);

    // =================== C端必需的CRUD操作方法 ===================

    /**
     * 更新付费配置状态
     */
    int updatePaymentStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新付费配置价格信息
     */
    int updatePaymentPrice(@Param("id") Long id,
                          @Param("price") Long price,
                          @Param("discountPrice") Long discountPrice,
                          @Param("discountStartTime") java.time.LocalDateTime discountStartTime,
                          @Param("discountEndTime") java.time.LocalDateTime discountEndTime);

    /**
     * 更新销售统计
     */
    int updateSalesStats(@Param("id") Long id,
                        @Param("salesIncrement") Long salesIncrement,
                        @Param("revenueIncrement") Long revenueIncrement);

    /**
     * 批量更新状态
     */
    int batchUpdatePaymentStatus(@Param("ids") List<Long> ids, @Param("status") String status);

    /**
     * 软删除付费配置
     */
    int softDeletePayment(@Param("id") Long id);

    /**
     * 根据内容ID删除付费配置
     */
    int deleteByContentId(@Param("contentId") Long contentId);
}