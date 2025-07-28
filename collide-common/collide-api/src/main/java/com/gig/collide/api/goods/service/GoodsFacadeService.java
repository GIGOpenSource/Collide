package com.gig.collide.api.goods.service;

import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.api.goods.response.data.BasicGoodsInfo;
import com.gig.collide.base.response.PageResponse;

/**
 * 商品门面服务接口
 * 提供商品核心业务功能
 * 参考SQL设计，实现去连表化的高性能商品系统
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface GoodsFacadeService {

    // ===================== 商品查询相关 =====================

    /**
     * 商品统一信息查询（完整信息）
     * 
     * @param goodsQueryRequest 查询请求
     * @return 查询响应
     */
    GoodsQueryResponse<GoodsInfo> queryGoods(GoodsQueryRequest goodsQueryRequest);

    /**
     * 基础商品信息查询（不包含敏感信息）
     * 
     * @param goodsQueryRequest 查询请求
     * @return 基础商品信息响应
     */
    GoodsQueryResponse<BasicGoodsInfo> queryBasicGoods(GoodsQueryRequest goodsQueryRequest);

    /**
     * 分页查询商品信息
     * 
     * @param goodsQueryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<GoodsInfo> pageQueryGoods(GoodsQueryRequest goodsQueryRequest);

    /**
     * 分页查询基础商品信息
     * 
     * @param goodsQueryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<BasicGoodsInfo> pageQueryBasicGoods(GoodsQueryRequest goodsQueryRequest);

    // ===================== 商品管理相关 =====================

    /**
     * 创建商品
     * 
     * @param createRequest 创建请求
     * @return 创建响应
     */
    GoodsCreateResponse createGoods(GoodsCreateRequest createRequest);

    /**
     * 更新商品信息
     * 
     * @param updateRequest 更新请求
     * @return 更新响应
     */
    GoodsUpdateResponse updateGoods(GoodsUpdateRequest updateRequest);

    /**
     * 删除商品
     * 
     * @param deleteRequest 删除请求
     * @return 删除响应
     */
    GoodsDeleteResponse deleteGoods(GoodsDeleteRequest deleteRequest);

    // ===================== 批量操作相关 =====================

    /**
     * 批量操作商品
     * 
     * @param batchRequest 批量操作请求
     * @return 批量操作响应
     */
    GoodsBatchOperationResponse batchOperateGoods(GoodsBatchOperationRequest batchRequest);

    // ===================== 统计查询相关 =====================

    /**
     * 获取商品统计信息
     * 
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    GoodsStatisticsResponse getGoodsStatistics(GoodsStatisticsRequest statisticsRequest);

    // ===================== 便捷查询方法 =====================

    /**
     * 根据商品ID获取商品详细信息
     * 
     * @param goodsId 商品ID
     * @return 商品详细信息
     */
    GoodsQueryResponse<GoodsInfo> getGoodsById(Long goodsId);

    /**
     * 根据商品ID获取基础商品信息
     * 
     * @param goodsId 商品ID
     * @return 基础商品信息
     */
    GoodsQueryResponse<BasicGoodsInfo> getBasicGoodsById(Long goodsId);

    /**
     * 获取推荐商品列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 推荐商品列表
     */
    PageResponse<BasicGoodsInfo> getRecommendedGoods(Integer pageNum, Integer pageSize);

    /**
     * 获取热门商品列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 热门商品列表
     */
    PageResponse<BasicGoodsInfo> getHotGoods(Integer pageNum, Integer pageSize);

    /**
     * 获取可购买商品列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 可购买商品列表
     */
    PageResponse<BasicGoodsInfo> getPurchasableGoods(Integer pageNum, Integer pageSize);

    /**
     * 根据商品名称搜索商品
     * 
     * @param keyword 关键词
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 搜索结果
     */
    PageResponse<BasicGoodsInfo> searchGoodsByKeyword(String keyword, Integer pageNum, Integer pageSize);

    // ===================== 状态检查方法 =====================

    /**
     * 检查商品名称是否可用
     * 
     * @param name 商品名称
     * @return 是否可用
     */
    Boolean checkGoodsNameAvailable(String name);

    /**
     * 检查商品是否可购买
     * 
     * @param goodsId 商品ID
     * @return 是否可购买
     */
    Boolean checkGoodsPurchasable(Long goodsId);

    /**
     * 检查商品是否有库存
     * 
     * @param goodsId 商品ID
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    Boolean checkGoodsStock(Long goodsId, Integer quantity);

    // ===================== 商品状态管理 =====================

    /**
     * 上架商品
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @return 更新响应
     */
    GoodsUpdateResponse onSaleGoods(Long goodsId, Integer version);

    /**
     * 下架商品
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @return 更新响应
     */
    GoodsUpdateResponse offSaleGoods(Long goodsId, Integer version);

    /**
     * 设置商品推荐状态
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param recommended 是否推荐
     * @return 更新响应
     */
    GoodsUpdateResponse setGoodsRecommended(Long goodsId, Integer version, Boolean recommended);

    /**
     * 设置商品热门状态
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param hot 是否热门
     * @return 更新响应
     */
    GoodsUpdateResponse setGoodsHot(Long goodsId, Integer version, Boolean hot);
} 