package com.gig.collide.goods.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商品数据访问层 - 简洁版
 * 基于MyBatis-Plus，实现简洁的数据访问
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 根据分类ID查询商品
     * 
     * @param page 分页参数
     * @param categoryId 分类ID
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findByCategory(Page<Goods> page, 
                               @Param("categoryId") Long categoryId,
                               @Param("status") String status);

    /**
     * 根据商家ID查询商品
     * 
     * @param page 分页参数
     * @param sellerId 商家ID
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findBySeller(Page<Goods> page,
                             @Param("sellerId") Long sellerId,
                             @Param("status") String status);

    /**
     * 根据名称关键词搜索商品
     * 
     * @param page 分页参数
     * @param keyword 关键词
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> searchByName(Page<Goods> page,
                             @Param("keyword") String keyword,
                             @Param("status") String status);

    /**
     * 根据价格区间查询商品
     * 
     * @param page 分页参数
     * @param minPrice 最小价格
     * @param maxPrice 最大价格
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findByPriceRange(Page<Goods> page,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 @Param("status") String status);

    /**
     * 查询有库存的商品
     * 
     * @param page 分页参数
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findInStock(Page<Goods> page,
                           @Param("status") String status);

    /**
     * 查询库存不足的商品
     * 
     * @param page 分页参数
     * @param threshold 库存阈值
     * @return 商品分页数据
     */
    IPage<Goods> findLowStock(Page<Goods> page,
                            @Param("threshold") Integer threshold);

    /**
     * 更新商品库存
     * 
     * @param goodsId 商品ID
     * @param stockChange 库存变化量
     * @return 更新行数
     */
    int updateStock(@Param("goodsId") Long goodsId,
                   @Param("stockChange") Integer stockChange);

    /**
     * 更新商品销量
     * 
     * @param goodsId 商品ID
     * @param salesChange 销量变化量
     * @return 更新行数
     */
    int updateSalesCount(@Param("goodsId") Long goodsId,
                        @Param("salesChange") Integer salesChange);

    /**
     * 增加商品浏览量
     * 
     * @param goodsId 商品ID
     * @return 更新行数
     */
    int increaseViewCount(@Param("goodsId") Long goodsId);

    /**
     * 批量更新商品状态
     * 
     * @param goodsIds 商品ID列表
     * @param status 新状态
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("goodsIds") List<Long> goodsIds,
                         @Param("status") String status);

    /**
     * 获取商品统计信息
     * 
     * @param goodsId 商品ID
     * @return 统计信息
     */
    Map<String, Object> getGoodsStatistics(@Param("goodsId") Long goodsId);

    /**
     * 统计商家的商品数量
     * 
     * @param sellerId 商家ID
     * @param status 状态（可选）
     * @return 商品数量
     */
    Long countBySeller(@Param("sellerId") Long sellerId,
                      @Param("status") String status);

    /**
     * 统计分类的商品数量
     * 
     * @param categoryId 分类ID
     * @param status 状态（可选）
     * @return 商品数量
     */
    Long countByCategory(@Param("categoryId") Long categoryId,
                        @Param("status") String status);

    /**
     * 查询热销商品
     * 
     * @param page 分页参数
     * @param minSalesCount 最小销量
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findHotGoods(Page<Goods> page,
                            @Param("minSalesCount") Long minSalesCount,
                            @Param("status") String status);

    /**
     * 查询最新商品
     * 
     * @param page 分页参数
     * @param days 天数
     * @param status 状态（可选）
     * @return 商品分页数据
     */
    IPage<Goods> findLatestGoods(Page<Goods> page,
                               @Param("days") Integer days,
                               @Param("status") String status);

    /**
     * 复合条件查询商品
     * 
     * @param page 分页参数
     * @param categoryId 分类ID（可选）
     * @param sellerId 商家ID（可选）
     * @param nameKeyword 名称关键词（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @param hasStock 是否有库存（可选）
     * @param status 状态（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 商品分页数据
     */
    IPage<Goods> findWithConditions(Page<Goods> page,
                                   @Param("categoryId") Long categoryId,
                                   @Param("sellerId") Long sellerId,
                                   @Param("nameKeyword") String nameKeyword,
                                   @Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("hasStock") Boolean hasStock,
                                   @Param("status") String status,
                                   @Param("orderBy") String orderBy,
                                   @Param("orderDirection") String orderDirection);
}