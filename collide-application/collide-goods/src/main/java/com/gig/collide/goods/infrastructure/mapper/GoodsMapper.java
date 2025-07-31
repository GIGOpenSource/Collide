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
 * 基于MyBatis Plus的增强Mapper
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
@Repository
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 根据商品类型分页查询
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    商品状态
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_goods 
            WHERE goods_type = #{goodsType} 
            AND status = #{status}
            ORDER BY create_time DESC
            """)
    IPage<Goods> selectByTypeAndStatus(Page<Goods> page, 
                                      @Param("goodsType") String goodsType, 
                                      @Param("status") String status);

    /**
     * 根据分类ID查询商品
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @param status     商品状态
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_goods 
            WHERE category_id = #{categoryId} 
            AND status = #{status}
            ORDER BY sales_count DESC, create_time DESC
            """)
    IPage<Goods> selectByCategoryAndStatus(Page<Goods> page, 
                                          @Param("categoryId") Long categoryId, 
                                          @Param("status") String status);

    /**
     * 根据商家ID查询商品
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   商品状态
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_goods 
            WHERE seller_id = #{sellerId} 
            AND status = #{status}
            ORDER BY create_time DESC
            """)
    IPage<Goods> selectBySellerAndStatus(Page<Goods> page, 
                                        @Param("sellerId") Long sellerId, 
                                        @Param("status") String status);

    /**
     * 热门商品查询（按销量排序）
     *
     * @param page      分页参数
     * @param goodsType 商品类型（可为空）
     * @return 分页结果
     */
    @Select("""
            <script>
            SELECT * FROM t_goods 
            WHERE status = 'active'
            <if test="goodsType != null and goodsType != ''">
                AND goods_type = #{goodsType}
            </if>
            ORDER BY sales_count DESC, view_count DESC
            </script>
            """)
    IPage<Goods> selectHotGoods(Page<Goods> page, @Param("goodsType") String goodsType);

    /**
     * 搜索商品（按名称和描述）
     *
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @param status  商品状态
     * @return 分页结果
     */
    @Select("""
            SELECT * FROM t_goods 
            WHERE (name LIKE CONCAT('%', #{keyword}, '%') 
                   OR description LIKE CONCAT('%', #{keyword}, '%'))
            AND status = #{status}
            ORDER BY sales_count DESC, create_time DESC
            """)
    IPage<Goods> searchGoods(Page<Goods> page, 
                            @Param("keyword") String keyword, 
                            @Param("status") String status);

    /**
     * 批量更新销量
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 影响行数
     */
    @Update("UPDATE t_goods SET sales_count = sales_count + #{count} WHERE id = #{goodsId}")
    int increaseSalesCount(@Param("goodsId") Long goodsId, @Param("count") Long count);

    /**
     * 批量更新浏览量
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 影响行数
     */
    @Update("UPDATE t_goods SET view_count = view_count + #{count} WHERE id = #{goodsId}")
    int increaseViewCount(@Param("goodsId") Long goodsId, @Param("count") Long count);

    /**
     * 批量更新库存
     *
     * @param goodsId 商品ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    @Update("""
            UPDATE t_goods 
            SET stock = CASE 
                WHEN stock = -1 THEN -1 
                ELSE GREATEST(0, stock - #{quantity}) 
            END 
            WHERE id = #{goodsId}
            """)
    int reduceStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);

    /**
     * 查询库存不足的商品
     *
     * @param threshold 库存阈值
     * @return 商品列表
     */
    @Select("""
            SELECT * FROM t_goods 
            WHERE stock >= 0 AND stock <= #{threshold} 
            AND status = 'active' 
            ORDER BY stock ASC
            """)
    List<Goods> selectLowStockGoods(@Param("threshold") Integer threshold);

    /**
     * 按价格区间查询
     *
     * @param page     分页参数
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param goodsType 商品类型
     * @return 分页结果
     */
    @Select("""
            <script>
            SELECT * FROM t_goods 
            WHERE status = 'active'
            <if test="goodsType == 'content'">
                AND coin_price BETWEEN #{minPrice} AND #{maxPrice}
            </if>
            <if test="goodsType != 'content'">
                AND price BETWEEN #{minPrice} AND #{maxPrice}
            </if>
            <if test="goodsType != null and goodsType != ''">
                AND goods_type = #{goodsType}
            </if>
            ORDER BY 
            <choose>
                <when test="goodsType == 'content'">coin_price ASC</when>
                <otherwise>price ASC</otherwise>
            </choose>
            </script>
            """)
    IPage<Goods> selectByPriceRange(Page<Goods> page, 
                                   @Param("minPrice") Object minPrice, 
                                   @Param("maxPrice") Object maxPrice,
                                   @Param("goodsType") String goodsType);

    /**
     * 统计各类型商品数量
     *
     * @return 统计结果
     */
    @Select("""
            SELECT goods_type, status, COUNT(*) as count
            FROM t_goods 
            GROUP BY goods_type, status
            """)
    @MapKey("goods_type")
    List<java.util.Map<String, Object>> countByTypeAndStatus();
}