package com.gig.collide.goods.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.goods.domain.entity.Goods;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品业务逻辑接口 - 简洁版
 * 基于goods-simple.sql的业务设计，实现核心商品功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface GoodsService {

    /**
     * 创建商品
     * 包含分类和商家信息冗余存储
     * 
     * @param goods 商品实体
     * @return 创建的商品
     */
    Goods createGoods(Goods goods);

    /**
     * 更新商品信息
     * 支持部分字段更新，自动更新冗余字段
     * 
     * @param goods 商品实体
     * @return 更新后的商品
     */
    Goods updateGoods(Goods goods);

    /**
     * 删除商品
     * 逻辑删除，将状态更新为inactive
     * 
     * @param goodsId 商品ID
     * @param deleteReason 删除原因
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean deleteGoods(Long goodsId, String deleteReason, Long operatorId);

    /**
     * 根据ID获取商品
     * 
     * @param goodsId 商品ID
     * @return 商品实体
     */
    Goods getGoodsById(Long goodsId);

    /**
     * 分页查询商品
     * 支持复合条件查询
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param categoryId 分类ID（可选）
     * @param sellerId 商家ID（可选）
     * @param nameKeyword 名称关键词（可选）
     * @param minPrice 最小价格（可选）
     * @param maxPrice 最大价格（可选）
     * @param hasStock 是否有库存（可选）
     * @param status 状态（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页结果
     */
    IPage<Goods> queryGoods(Integer pageNum, Integer pageSize, Long categoryId, Long sellerId,
                           String nameKeyword, BigDecimal minPrice, BigDecimal maxPrice,
                           Boolean hasStock, String status, String orderBy, String orderDirection);

    /**
     * 根据分类ID查询商品
     * 
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页结果
     */
    IPage<Goods> getGoodsByCategory(Long categoryId, Integer pageNum, Integer pageSize);

    /**
     * 根据商家ID查询商品
     * 
     * @param sellerId 商家ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 分页结果
     */
    IPage<Goods> getGoodsBySeller(Long sellerId, Integer pageNum, Integer pageSize);

    /**
     * 搜索商品
     * 根据商品名称进行模糊搜索
     * 
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    IPage<Goods> searchGoods(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 更新商品库存
     * 支持增加或减少库存，自动处理售罄状态
     * 
     * @param goodsId 商品ID
     * @param stockChange 库存变化量（正数为增加，负数为减少）
     * @return 是否成功
     */
    boolean updateStock(Long goodsId, Integer stockChange);

    /**
     * 更新商品销量
     * 通常在订单完成时调用
     * 
     * @param goodsId 商品ID
     * @param salesChange 销量变化量
     * @return 是否成功
     */
    boolean updateSalesCount(Long goodsId, Integer salesChange);

    /**
     * 增加商品浏览量
     * 每次商品被查看时调用
     * 
     * @param goodsId 商品ID
     * @return 是否成功
     */
    boolean increaseViewCount(Long goodsId);

    /**
     * 批量更新商品状态
     * 
     * @param goodsIds 商品ID列表
     * @param status 新状态
     * @return 更新成功的数量
     */
    int batchUpdateStatus(List<Long> goodsIds, String status);

    /**
     * 获取商品统计信息
     * 
     * @param goodsId 商品ID
     * @return 统计信息Map
     */
    Map<String, Object> getGoodsStatistics(Long goodsId);

    /**
     * 查询热销商品
     * 
     * @param minSalesCount 最小销量
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 热销商品列表
     */
    IPage<Goods> getHotGoods(Long minSalesCount, Integer pageNum, Integer pageSize);

    /**
     * 查询最新商品
     * 
     * @param days 天数
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 最新商品列表
     */
    IPage<Goods> getLatestGoods(Integer days, Integer pageNum, Integer pageSize);

    /**
     * 查询库存不足的商品
     * 
     * @param threshold 库存阈值
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 库存不足的商品
     */
    IPage<Goods> getLowStockGoods(Integer threshold, Integer pageNum, Integer pageSize);

    /**
     * 统计商家的商品数量
     * 
     * @param sellerId 商家ID
     * @param status 状态（可选）
     * @return 商品数量
     */
    Long countBySeller(Long sellerId, String status);

    /**
     * 统计分类的商品数量
     * 
     * @param categoryId 分类ID
     * @param status 状态（可选）
     * @return 商品数量
     */
    Long countByCategory(Long categoryId, String status);

    /**
     * 验证商品请求参数
     * 
     * @param goods 商品对象
     * @return 验证结果信息
     */
    String validateGoodsRequest(Goods goods);

    /**
     * 检查商品是否可以删除
     * 
     * @param goodsId 商品ID
     * @return 是否可以删除
     */
    boolean canDelete(Long goodsId);

    /**
     * 检查库存是否充足
     * 
     * @param goodsId 商品ID
     * @param requiredStock 需要的库存数量
     * @return 是否充足
     */
    boolean checkStockAvailable(Long goodsId, Integer requiredStock);

    /**
     * 激活商品
     * 
     * @param goodsId 商品ID
     * @return 是否成功
     */
    boolean activateGoods(Long goodsId);

    /**
     * 停用商品
     * 
     * @param goodsId 商品ID
     * @return 是否成功
     */
    boolean deactivateGoods(Long goodsId);
}