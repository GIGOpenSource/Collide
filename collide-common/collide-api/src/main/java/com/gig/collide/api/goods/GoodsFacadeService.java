package com.gig.collide.api.goods;

import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.request.GoodsUpdateRequest;
import com.gig.collide.api.goods.request.GoodsQueryRequest;
import com.gig.collide.api.goods.request.GoodsDeleteRequest;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.base.response.PageResponse;

/**
 * 商品门面服务接口 - 简洁版
 * 基于goods-simple.sql的单表设计，实现核心商品管理功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface GoodsFacadeService {
    
    /**
     * 创建商品
     * 支持分类和商家信息冗余存储
     * 
     * @param request 商品创建请求
     * @return 创建结果
     */
    Result<GoodsResponse> createGoods(GoodsCreateRequest request);
    
    /**
     * 更新商品信息
     * 支持部分字段更新，自动更新冗余字段
     * 
     * @param request 商品更新请求
     * @return 更新结果
     */
    Result<GoodsResponse> updateGoods(GoodsUpdateRequest request);
    
    /**
     * 删除商品
     * 逻辑删除，将状态更新为inactive
     * 
     * @param request 删除请求
     * @return 删除结果
     */
    Result<Void> deleteGoods(GoodsDeleteRequest request);
    
    /**
     * 根据ID获取商品详情
     * 
     * @param goodsId 商品ID
     * @return 商品详情
     */
    Result<GoodsResponse> getGoodsById(Long goodsId);
    
    /**
     * 分页查询商品
     * 支持按分类、商家、状态、价格等条件查询
     * 
     * @param request 查询请求
     * @return 商品分页数据
     */
    Result<PageResponse<GoodsResponse>> queryGoods(GoodsQueryRequest request);
    
    /**
     * 根据分类ID查询商品
     * 
     * @param categoryId 分类ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 商品分页数据
     */
    Result<PageResponse<GoodsResponse>> getGoodsByCategory(Long categoryId, Integer pageNum, Integer pageSize);
    
    /**
     * 根据商家ID查询商品
     * 
     * @param sellerId 商家ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 商品分页数据
     */
    Result<PageResponse<GoodsResponse>> getGoodsBySeller(Long sellerId, Integer pageNum, Integer pageSize);
    
    /**
     * 更新商品库存
     * 支持增加或减少库存
     * 
     * @param goodsId 商品ID
     * @param stockChange 库存变化量（正数为增加，负数为减少）
     * @return 更新结果
     */
    Result<Void> updateStock(Long goodsId, Integer stockChange);
    
    /**
     * 更新商品销量
     * 通常在订单完成时调用
     * 
     * @param goodsId 商品ID
     * @param salesChange 销量变化量
     * @return 更新结果
     */
    Result<Void> updateSalesCount(Long goodsId, Integer salesChange);
    
    /**
     * 增加商品浏览量
     * 每次商品被查看时调用
     * 
     * @param goodsId 商品ID
     * @return 更新结果
     */
    Result<Void> increaseViewCount(Long goodsId);
    
    /**
     * 批量更新商品状态
     * 
     * @param goodsIds 商品ID列表
     * @param status 新状态
     * @return 更新结果
     */
    Result<Void> batchUpdateStatus(java.util.List<Long> goodsIds, String status);
    
    /**
     * 获取商品基本统计信息
     * 
     * @param goodsId 商品ID
     * @return 统计信息Map（sales_count、view_count、stock等）
     */
    Result<java.util.Map<String, Object>> getGoodsStatistics(Long goodsId);
    
    /**
     * 搜索商品
     * 根据商品名称进行模糊搜索
     * 
     * @param keyword 搜索关键词
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<GoodsResponse>> searchGoods(String keyword, Integer pageNum, Integer pageSize);
}