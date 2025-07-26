package com.gig.collide.admin.service;

import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 商品服务接口
 * 
 * 通过Dubbo调用微服务实现商品管理功能
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
public interface AdminGoodsService {
    
    /**
     * 创建商品
     */
    Long createGoods(GoodsCreateRequest createRequest);
    
    /**
     * 更新商品信息
     */
    void updateGoods(GoodsUpdateRequest updateRequest);
    
    /**
     * 删除商品
     */
    void deleteGoods(Long goodsId);
    
    /**
     * 获取商品详情
     */
    GoodsInfo getGoodsDetail(Long goodsId);
    
    /**
     * 分页查询商品
     */
    PageResponse<GoodsInfo> pageQueryGoods(GoodsPageQueryRequest queryRequest);
    
    /**
     * 商品上架
     */
    void putOnSale(Long goodsId);
    
    /**
     * 商品下架
     */
    void putOffSale(Long goodsId);
    
    /**
     * 批量上架
     */
    Map<String, Object> batchPutOnSale(List<Long> goodsIds);
    
    /**
     * 批量下架
     */
    Map<String, Object> batchPutOffSale(List<Long> goodsIds);
    
    /**
     * 更新库存
     */
    void updateStock(GoodsStockRequest stockRequest);
    
    /**
     * 上传商品图片
     */
    Map<String, String> uploadImage(Long goodsId, MultipartFile file, String imageType);
    
    /**
     * 获取商品统计
     */
    Map<String, Object> getGoodsStatistics(String startDate, String endDate, String type);
    
    /**
     * 导出商品数据
     */
    Map<String, String> exportGoods(String type, String status, String format);
} 