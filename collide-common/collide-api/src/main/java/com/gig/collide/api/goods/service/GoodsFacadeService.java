package com.gig.collide.api.goods.service;

import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.base.response.SingleResponse;

import java.util.List;

/**
 * 商品服务 Facade 接口
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface GoodsFacadeService {

    /**
     * 创建商品
     */
    SingleResponse<Long> createGoods(GoodsCreateRequest createRequest);

    /**
     * 更新商品信息
     */
    SingleResponse<Void> updateGoods(GoodsUpdateRequest updateRequest);

    /**
     * 删除商品
     */
    SingleResponse<Void> deleteGoods(Long goodsId);

    /**
     * 获取商品详情
     */
    SingleResponse<GoodsInfo> getGoodsDetail(Long goodsId);

    /**
     * 分页查询商品
     */
    PageResponse<GoodsInfo> pageQueryGoods(GoodsPageQueryRequest queryRequest);

    /**
     * 商品上架
     */
    SingleResponse<Void> putOnSale(Long goodsId);

    /**
     * 商品下架
     */
    SingleResponse<Void> putOffSale(Long goodsId);

    /**
     * 批量上架
     */
    SingleResponse<Object> batchPutOnSale(List<Long> goodsIds);

    /**
     * 批量下架
     */
    SingleResponse<Object> batchPutOffSale(List<Long> goodsIds);

    /**
     * 更新库存
     */
    SingleResponse<Void> updateStock(GoodsStockRequest stockRequest);
} 