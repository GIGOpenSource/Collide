package com.gig.collide.goods.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品数据访问层
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 分页查询商品（基础查询）
     * 
     * @param page 分页对象
     * @param type 商品类型
     * @param status 商品状态
     * @param keyword 关键词
     * @return 分页结果
     */
    IPage<Goods> pageQuery(Page<Goods> page, 
                          @Param("type") String type,
                          @Param("status") String status, 
                          @Param("keyword") String keyword);
    
    /**
     * 分页查询推荐商品
     * 
     * @param page 分页对象
     * @param status 商品状态
     * @return 分页结果
     */
    IPage<Goods> pageQueryRecommended(Page<Goods> page, @Param("status") String status);
    
    /**
     * 分页查询热门商品
     * 
     * @param page 分页对象
     * @param status 商品状态
     * @return 分页结果
     */
    IPage<Goods> pageQueryHot(Page<Goods> page, @Param("status") String status);
    
    /**
     * 获取推荐商品列表
     * 
     * @param status 商品状态
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<Goods> selectRecommendedGoods(@Param("status") String status, @Param("limit") Integer limit);
    
    /**
     * 获取热门商品列表
     * 
     * @param status 商品状态
     * @param limit 限制数量
     * @return 热门商品列表
     */
    List<Goods> selectHotGoods(@Param("status") String status, @Param("limit") Integer limit);
    
    /**
     * 更新商品库存（乐观锁）
     * 
     * @param goodsId 商品ID
     * @param newStock 新库存数量
     * @param oldVersion 旧版本号
     * @return 更新行数
     */
    @Update("UPDATE goods SET stock = #{newStock}, version = version + 1, " +
            "update_time = NOW() WHERE id = #{goodsId} AND version = #{oldVersion}")
    int updateStockWithVersion(@Param("goodsId") Long goodsId, 
                              @Param("newStock") Integer newStock,
                              @Param("oldVersion") Integer oldVersion);
    
    /**
     * 增加库存（乐观锁）
     * 
     * @param goodsId 商品ID
     * @param deltaStock 库存变化量
     * @param oldVersion 旧版本号
     * @return 更新行数
     */
    @Update("UPDATE goods SET stock = stock + #{deltaStock}, version = version + 1, " +
            "update_time = NOW() WHERE id = #{goodsId} AND version = #{oldVersion}")
    int increaseStockWithVersion(@Param("goodsId") Long goodsId, 
                                @Param("deltaStock") Integer deltaStock,
                                @Param("oldVersion") Integer oldVersion);
    
    /**
     * 减少库存（乐观锁）
     * 
     * @param goodsId 商品ID
     * @param deltaStock 库存变化量（正数）
     * @param oldVersion 旧版本号
     * @return 更新行数
     */
    @Update("UPDATE goods SET stock = stock - #{deltaStock}, version = version + 1, " +
            "update_time = NOW() WHERE id = #{goodsId} AND version = #{oldVersion} AND stock >= #{deltaStock}")
    int decreaseStockWithVersion(@Param("goodsId") Long goodsId, 
                                @Param("deltaStock") Integer deltaStock,
                                @Param("oldVersion") Integer oldVersion);
    
    /**
     * 增加销售数量
     * 
     * @param goodsId 商品ID
     * @param deltaSold 销售增加量
     * @return 更新行数
     */
    @Update("UPDATE goods SET sold_count = sold_count + #{deltaSold}, " +
            "update_time = NOW() WHERE id = #{goodsId}")
    int increaseSoldCount(@Param("goodsId") Long goodsId, @Param("deltaSold") Integer deltaSold);
    
    /**
     * 批量更新商品状态
     * 
     * @param goodsIds 商品ID列表
     * @param status 新状态
     * @return 更新行数
     */
    int batchUpdateStatus(@Param("goodsIds") List<Long> goodsIds, @Param("status") String status);
    
    /**
     * 根据创建者ID查询商品
     * 
     * @param creatorId 创建者ID
     * @param status 商品状态
     * @return 商品列表
     */
    List<Goods> selectByCreatorId(@Param("creatorId") Long creatorId, @Param("status") String status);
} 