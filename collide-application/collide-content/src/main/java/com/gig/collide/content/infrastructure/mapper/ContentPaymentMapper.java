package com.gig.collide.content.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.content.domain.entity.ContentPayment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容付费配置表数据映射接口
 * 管理内容的付费策略、价格配置和销售统计
 *
 * @author GIG Team
 * @version 2.0.0 (内容付费版)
 * @since 2024-01-31
 */
@Mapper
public interface ContentPaymentMapper extends BaseMapper<ContentPayment> {

    /**
     * 根据内容ID查询付费配置
     */
    ContentPayment selectByContentId(@Param("contentId") Long contentId);

    /**
     * 根据付费类型查询配置列表
     */
    List<ContentPayment> selectByPaymentType(@Param("paymentType") String paymentType);

    /**
     * 查询免费内容配置
     */
    List<ContentPayment> selectFreeContent(@Param("offset") Long offset,
                                          @Param("limit") Integer limit);

    /**
     * 查询金币付费内容配置
     */
    List<ContentPayment> selectCoinPayContent(@Param("offset") Long offset,
                                             @Param("limit") Integer limit);

    /**
     * 查询VIP免费内容配置
     */
    List<ContentPayment> selectVipFreeContent(@Param("offset") Long offset,
                                             @Param("limit") Integer limit);

    /**
     * 查询VIP专享内容配置
     */
    List<ContentPayment> selectVipOnlyContent(@Param("offset") Long offset,
                                             @Param("limit") Integer limit);

    /**
     * 根据价格范围查询配置
     */
    List<ContentPayment> selectByPriceRange(@Param("minPrice") Long minPrice,
                                           @Param("maxPrice") Long maxPrice);

    /**
     * 查询支持试读的内容配置
     */
    List<ContentPayment> selectTrialEnabledContent(@Param("offset") Long offset,
                                                  @Param("limit") Integer limit);

    /**
     * 查询永久有效的内容配置
     */
    List<ContentPayment> selectPermanentContent(@Param("offset") Long offset,
                                               @Param("limit") Integer limit);

    /**
     * 查询限时内容配置
     */
    List<ContentPayment> selectTimeLimitedContent(@Param("offset") Long offset,
                                                 @Param("limit") Integer limit);

    /**
     * 根据状态查询配置列表
     */
    List<ContentPayment> selectByStatus(@Param("status") String status);

    /**
     * 更新销售统计
     */
    int updateSalesStats(@Param("contentId") Long contentId,
                        @Param("salesIncrement") Long salesIncrement,
                        @Param("revenueIncrement") Long revenueIncrement);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("contentIds") List<Long> contentIds,
                         @Param("status") String status);

    /**
     * 删除内容的付费配置
     */
    int deleteByContentId(@Param("contentId") Long contentId);

    /**
     * 统计各付费类型的数量
     */
    List<Object> countByPaymentType();

    /**
     * 统计活跃配置数量
     */
    Long countActiveConfigs();

    /**
     * 获取价格统计信息
     */
    Object getPriceStats();

    /**
     * 获取销售排行榜
     */
    List<ContentPayment> getSalesRanking(@Param("limit") Integer limit);

    /**
     * 获取收入排行榜
     */
    List<ContentPayment> getRevenueRanking(@Param("limit") Integer limit);

    /**
     * 查询热门付费内容（按销量排序）
     */
    List<ContentPayment> selectHotPaidContent(@Param("limit") Integer limit);

    /**
     * 查询高价值内容（按单价排序）
     */
    List<ContentPayment> selectHighValueContent(@Param("limit") Integer limit);

    /**
     * 查询性价比内容（按销量/价格比排序）
     */
    List<ContentPayment> selectValueForMoneyContent(@Param("limit") Integer limit);

    /**
     * 查询新上线的付费内容
     */
    List<ContentPayment> selectNewPaidContent(@Param("limit") Integer limit);

    /**
     * 查询有折扣的内容配置
     */
    List<ContentPayment> selectDiscountedContent(@Param("offset") Long offset,
                                                @Param("limit") Integer limit);

    /**
     * 获取总销售统计
     */
    Object getTotalSalesStats();

    /**
     * 获取月度销售统计
     */
    List<Object> getMonthlySalesStats(@Param("months") Integer months);

    /**
     * 获取付费转化率统计
     */
    Object getConversionStats();
}