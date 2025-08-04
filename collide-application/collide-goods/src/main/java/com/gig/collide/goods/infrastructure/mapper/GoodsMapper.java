package com.gig.collide.goods.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品数据访问层
 * 基于MyBatis Plus的增强Mapper，所有SQL统一在XML中定义
 * 支持MySQL 8.0/8.4优化索引的高性能查询
 *
 * @author GIG Team
 * @version 2.0.0 (索引优化版)
 * @since 2024-01-31
 */
@Repository
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 根据商品类型和状态分页查询
     * 使用索引：idx_type_status_time
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    商品状态
     * @return 分页结果
     */
    IPage<Goods> selectByTypeAndStatus(Page<Goods> page, 
                                      @Param("goodsType") String goodsType, 
                                      @Param("status") String status);

    /**
     * 根据分类ID查询商品
     * 使用索引：idx_category_status_sales_time
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @param status     商品状态
     * @return 分页结果
     */
    IPage<Goods> selectByCategoryAndStatus(Page<Goods> page, 
                                          @Param("categoryId") Long categoryId, 
                                          @Param("status") String status);

    /**
     * 根据商家ID查询商品
     * 使用索引：idx_seller_status_time
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   商品状态
     * @return 分页结果
     */
    IPage<Goods> selectBySellerAndStatus(Page<Goods> page, 
                                        @Param("sellerId") Long sellerId, 
                                        @Param("status") String status);

    /**
     * 根据内容ID查询商品
     * 使用索引：idx_content_type
     * 用于内容购买流程中获取对应的商品记录
     *
     * @param contentId 内容ID
     * @param goodsType 商品类型
     * @return 商品信息
     */
    Goods selectByContentId(@Param("contentId") Long contentId, 
                           @Param("goodsType") String goodsType);

    /**
     * 热门商品查询（按销量排序）
     * 使用索引：idx_status_sales_views
     *
     * @param page      分页参数
     * @param goodsType 商品类型（可为空）
     * @return 分页结果
     */
    IPage<Goods> selectHotGoods(Page<Goods> page, @Param("goodsType") String goodsType);

    /**
     * 搜索商品（按名称和描述）
     * 使用索引：idx_name_desc_fulltext
     *
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @param status  商品状态
     * @return 分页结果
     */
    IPage<Goods> searchGoods(Page<Goods> page, 
                            @Param("keyword") String keyword, 
                            @Param("status") String status);

    /**
     * 增加销量
     * 使用主键索引快速更新
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 影响行数
     */
    int increaseSalesCount(@Param("goodsId") Long goodsId, @Param("count") Long count);

    /**
     * 增加浏览量
     * 使用主键索引快速更新
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 影响行数
     */
    int increaseViewCount(@Param("goodsId") Long goodsId, @Param("count") Long count);

    /**
     * 扣减库存
     * 使用主键索引快速更新，支持无限库存
     *
     * @param goodsId 商品ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    int reduceStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);

    /**
     * 查询库存不足的商品
     * 使用索引：idx_stock_status
     *
     * @param threshold 库存阈值
     * @return 商品列表
     */
    List<Goods> selectLowStockGoods(@Param("threshold") Integer threshold);

    /**
     * 按价格区间查询
     * 使用索引：idx_status_price / idx_status_coin_price
     *
     * @param page     分页参数
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param goodsType 商品类型
     * @return 分页结果
     */
    IPage<Goods> selectByPriceRange(Page<Goods> page, 
                                   @Param("minPrice") Object minPrice, 
                                   @Param("maxPrice") Object maxPrice,
                                   @Param("goodsType") String goodsType);

    /**
     * 统计各类型商品数量
     * 使用索引：idx_type_status_stats
     *
     * @return 统计结果
     */
    @MapKey("goods_type")
    List<java.util.Map<String, Object>> countByTypeAndStatus();

    /**
     * 批量更新商品状态
     * 用于批量上架、下架等操作
     *
     * @param goodsIds 商品ID列表
     * @param status   新状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("goodsIds") List<Long> goodsIds, @Param("status") String status);

    /**
     * 根据分类统计商品数量
     * 使用索引：idx_category_status_sales_time
     *
     * @param categoryId 分类ID
     * @param status     商品状态（可为空）
     * @return 商品数量
     */
    long countByCategory(@Param("categoryId") Long categoryId, @Param("status") String status);

    /**
     * 根据商家统计商品数量
     * 使用索引：idx_seller_status_time
     *
     * @param sellerId 商家ID
     * @param status   商品状态（可为空）
     * @return 商品数量
     */
    long countBySeller(@Param("sellerId") Long sellerId, @Param("status") String status);

    /**
     * 复合条件查询商品
     * 支持多种查询条件和排序方式的组合
     * 使用多种索引优化查询性能
     *
     * @param categoryId    分类ID（可为空）
     * @param sellerId      商家ID（可为空）
     * @param goodsType     商品类型（可为空）
     * @param nameKeyword   名称关键词（可为空）
     * @param minPrice      最低现金价格（可为空）
     * @param maxPrice      最高现金价格（可为空）
     * @param minCoinPrice  最低金币价格（可为空）
     * @param maxCoinPrice  最高金币价格（可为空）
     * @param hasStock      是否有库存（可为空）
     * @param status        商品状态（可为空）
     * @param orderBy       排序字段（可为空）
     * @param orderDirection 排序方向（可为空）
     * @return 商品列表
     */
    IPage<Goods> findWithConditions(Page<Goods> page,
                                   @Param("categoryId") Long categoryId,
                                   @Param("sellerId") Long sellerId,
                                   @Param("goodsType") String goodsType,
                                   @Param("nameKeyword") String nameKeyword,
                                   @Param("minPrice") Object minPrice,
                                   @Param("maxPrice") Object maxPrice,
                                   @Param("minCoinPrice") Object minCoinPrice,
                                   @Param("maxCoinPrice") Object maxCoinPrice,
                                   @Param("hasStock") Boolean hasStock,
                                   @Param("status") String status,
                                   @Param("orderBy") String orderBy,
                                   @Param("orderDirection") String orderDirection);
}