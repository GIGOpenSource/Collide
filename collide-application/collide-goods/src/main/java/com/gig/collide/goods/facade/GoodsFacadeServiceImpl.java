package com.gig.collide.goods.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.service.GoodsService;
import com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant;
import com.gig.collide.web.vo.Result;
import com.alicp.jetcache.anno.Cached;
import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品门面服务实现 - 缓存增强版
 * 对齐like模块设计风格，提供完整的商品服务
 * 包含缓存功能、跨模块集成、错误处理、数据转换
 * 
 * @author GIG Team  
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    private final GoodsService goodsService;

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_CATEGORY_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.SELLER_GOODS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<GoodsResponse> createGoods(GoodsCreateRequest request) {
        try {
            log.info("RPC创建商品: name={}, sellerId={}", request.getName(), request.getSellerId());
            long startTime = System.currentTimeMillis();

            // 转换请求对象为实体
            Goods goods = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Goods savedGoods = goodsService.createGoods(goods);
            
            // 转换响应对象
            GoodsResponse response = convertToResponse(savedGoods);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("商品创建成功: ID={}, 耗时={}ms", savedGoods.getId(), duration);
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("商品创建参数验证失败: {}", e.getMessage());
            return Result.error("GOODS_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return Result.error("GOODS_CREATE_ERROR", "创建商品失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, key = GoodsCacheConstant.GOODS_DETAIL_BY_ID_KEY)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_CATEGORY_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.SELLER_GOODS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<GoodsResponse> updateGoods(GoodsUpdateRequest request) {
        try {
            log.info("RPC更新商品: id={}", request.getId());
            long startTime = System.currentTimeMillis();

            // 转换请求对象为实体
            Goods goods = convertUpdateRequestToEntity(request);
            
            // 调用业务逻辑
            Goods updatedGoods = goodsService.updateGoods(goods);
            
            // 转换响应对象
            GoodsResponse response = convertToResponse(updatedGoods);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("商品更新成功: ID={}, 耗时={}ms", updatedGoods.getId(), duration);
            return Result.success(response);
        } catch (IllegalArgumentException e) {
            log.warn("商品更新参数验证失败: {}", e.getMessage());
            return Result.error("GOODS_UPDATE_PARAM_ERROR", e.getMessage());
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.error("GOODS_UPDATE_ERROR", "更新商品失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_CATEGORY_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.SELLER_GOODS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<Void> deleteGoods(GoodsDeleteRequest request) {
        try {
            log.info("RPC删除商品: id={}", request.getId());
            long startTime = System.currentTimeMillis();

            boolean success = goodsService.deleteGoods(
                    request.getId(), 
                    request.getDeleteReason(), 
                    request.getOperatorId()
            );
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("商品删除成功: ID={}, 耗时={}ms", request.getId(), duration);
                return Result.success(null);
            } else {
                log.warn("商品删除失败: ID={}", request.getId());
                return Result.error("GOODS_DELETE_FAILED", "删除商品失败");
            }
        } catch (Exception e) {
            log.error("删除商品失败: ID={}", request.getId(), e);
            return Result.error("GOODS_DELETE_ERROR", "删除商品失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, key = GoodsCacheConstant.GOODS_DETAIL_BY_ID_KEY,
            expire = GoodsCacheConstant.GOODS_DETAIL_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<GoodsResponse> getGoodsById(Long goodsId) {
        try {
            log.debug("RPC获取商品详情: goodsId={}", goodsId);

            Goods goods = goodsService.getGoodsById(goodsId);
            if (goods == null) {
                log.warn("商品不存在: goodsId={}", goodsId);
                return Result.error("GOODS_NOT_FOUND", "商品不存在");
            }
            
            GoodsResponse response = convertToResponse(goods);
            log.debug("商品详情查询成功: goodsId={}, name={}", goodsId, goods.getName());
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取商品详情失败: goodsId={}", goodsId, e);
            return Result.error("GOODS_GET_ERROR", "获取商品详情失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_LIST_CACHE, key = GoodsCacheConstant.GOODS_LIST_KEY,
            expire = GoodsCacheConstant.GOODS_LIST_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<GoodsResponse>> queryGoods(GoodsQueryRequest request) {
        try {
            log.info("RPC分页查询商品: pageNum={}, pageSize={}, sellerId={}, categoryId={}", 
                    request.getPageNum(), request.getPageSize(), request.getSellerId(), request.getCategoryId());
            long startTime = System.currentTimeMillis();

            // 调用业务逻辑进行分页查询
            IPage<Goods> goodsPage = goodsService.queryGoods(
                    request.getPageNum(),
                    request.getPageSize(),
                    request.getCategoryId(),
                    request.getSellerId(),
                    request.getNameKeyword(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getHasStock(),
                    request.getStatus(),
                    request.getOrderBy(),
                    request.getOrderDirection()
            );

            // 转换分页响应
            PageResponse<GoodsResponse> pageResponse = new PageResponse<>();
            List<GoodsResponse> responseList = goodsPage.getRecords().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());

            pageResponse.setDatas(responseList);
            pageResponse.setTotal(goodsPage.getTotal());
            pageResponse.setCurrentPage((int) goodsPage.getCurrent());
            pageResponse.setPageSize((int) goodsPage.getSize());
            pageResponse.setTotalPage((int) goodsPage.getPages());

            long duration = System.currentTimeMillis() - startTime;
            log.info("商品分页查询成功: 总数={}, 当前页={}, 耗时={}ms", 
                    pageResponse.getTotal(), pageResponse.getCurrentPage(), duration);
            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询商品失败: 页码={}, 页大小={}", request.getPageNum(), request.getPageSize(), e);
            return Result.error("GOODS_QUERY_ERROR", "查询商品失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_CATEGORY_CACHE, key = GoodsCacheConstant.CATEGORY_GOODS_KEY,
            expire = GoodsCacheConstant.GOODS_CATEGORY_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<GoodsResponse>> getGoodsByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        try {
            log.debug("根据分类查询商品: categoryId={}, pageNum={}, pageSize={}", categoryId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Goods> goodsPage = goodsService.getGoodsByCategory(categoryId, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("分类商品查询成功: categoryId={}, 总数={}, 耗时={}ms", 
                    categoryId, goodsPage.getTotal(), duration);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("根据分类查询商品失败: categoryId={}", categoryId, e);
            return Result.error("GOODS_CATEGORY_QUERY_ERROR", "查询分类商品失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.SELLER_GOODS_CACHE, key = GoodsCacheConstant.SELLER_GOODS_LIST_KEY,
            expire = GoodsCacheConstant.SELLER_GOODS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<GoodsResponse>> getGoodsBySeller(Long sellerId, Integer pageNum, Integer pageSize) {
        try {
            log.debug("根据商家查询商品: sellerId={}, pageNum={}, pageSize={}", sellerId, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Goods> goodsPage = goodsService.getGoodsBySeller(sellerId, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("商家商品查询成功: sellerId={}, 总数={}, 耗时={}ms", 
                    sellerId, goodsPage.getTotal(), duration);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("根据商家查询商品失败: sellerId={}", sellerId, e);
            return Result.error("GOODS_SELLER_QUERY_ERROR", "查询商家商品失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_INVENTORY_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<Void> updateStock(Long goodsId, Integer stockChange) {
        try {
            log.info("RPC更新商品库存: goodsId={}, stockChange={}", goodsId, stockChange);
            long startTime = System.currentTimeMillis();

            boolean success = goodsService.updateStock(goodsId, stockChange);
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("库存更新成功: goodsId={}, stockChange={}, 耗时={}ms", goodsId, stockChange, duration);
                return Result.success(null);
            } else {
                log.warn("库存更新失败: goodsId={}, stockChange={}", goodsId, stockChange);
                return Result.error("STOCK_UPDATE_FAILED", "更新库存失败");
            }
        } catch (Exception e) {
            log.error("更新库存失败: goodsId={}, stockChange={}", goodsId, stockChange, e);
            return Result.error("STOCK_UPDATE_ERROR", "更新库存失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.HOT_GOODS_CACHE)
    public Result<Void> updateSalesCount(Long goodsId, Integer salesChange) {
        try {
            log.info("RPC更新商品销量: goodsId={}, salesChange={}", goodsId, salesChange);
            long startTime = System.currentTimeMillis();

            boolean success = goodsService.updateSalesCount(goodsId, salesChange);
            
            long duration = System.currentTimeMillis() - startTime;
            if (success) {
                log.info("销量更新成功: goodsId={}, salesChange={}, 耗时={}ms", goodsId, salesChange, duration);
                return Result.success(null);
            } else {
                log.warn("销量更新失败: goodsId={}, salesChange={}", goodsId, salesChange);
                return Result.error("SALES_UPDATE_FAILED", "更新销量失败");
            }
        } catch (Exception e) {
            log.error("更新销量失败: goodsId={}, salesChange={}", goodsId, salesChange, e);
            return Result.error("SALES_UPDATE_ERROR", "更新销量失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<Void> increaseViewCount(Long goodsId) {
        try {
            log.debug("增加商品浏览量: goodsId={}", goodsId);
            
            boolean success = goodsService.increaseViewCount(goodsId);
            if (success) {
                log.debug("浏览量更新成功: goodsId={}", goodsId);
                return Result.success(null);
            } else {
                log.warn("浏览量更新失败: goodsId={}", goodsId);
                return Result.error("VIEW_COUNT_UPDATE_FAILED", "更新浏览量失败");
            }
        } catch (Exception e) {
            log.error("增加浏览量失败: goodsId={}", goodsId, e);
            return Result.error("VIEW_COUNT_ERROR", "增加浏览量失败: " + e.getMessage());
        }
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATUS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.SELLER_GOODS_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Result<Void> batchUpdateStatus(List<Long> goodsIds, String status) {
        try {
            log.info("RPC批量更新商品状态: count={}, status={}", 
                    goodsIds != null ? goodsIds.size() : 0, status);
            long startTime = System.currentTimeMillis();

            int updateCount = goodsService.batchUpdateStatus(goodsIds, status);
            
            long duration = System.currentTimeMillis() - startTime;
            if (updateCount > 0) {
                log.info("批量状态更新成功: count={}, status={}, updateCount={}, 耗时={}ms", 
                        goodsIds != null ? goodsIds.size() : 0, status, updateCount, duration);
                return Result.success(null);
            } else {
                log.warn("批量状态更新失败: count={}, status={}", 
                        goodsIds != null ? goodsIds.size() : 0, status);
                return Result.error("BATCH_UPDATE_FAILED", "批量更新状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新状态失败: count={}, status={}", 
                    goodsIds != null ? goodsIds.size() : 0, status, e);
            return Result.error("BATCH_UPDATE_ERROR", "批量更新状态失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE, key = GoodsCacheConstant.SELLER_GOODS_STATS_KEY,
            expire = GoodsCacheConstant.GOODS_STATISTICS_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<Map<String, Object>> getGoodsStatistics(Long goodsId) {
        try {
            log.debug("获取商品统计: goodsId={}", goodsId);
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> statistics = goodsService.getGoodsStatistics(goodsId);
            
            long duration = System.currentTimeMillis() - startTime;
            log.debug("商品统计查询成功: goodsId={}, 耗时={}ms", goodsId, duration);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取商品统计失败: goodsId={}", goodsId, e);
            return Result.error("STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_SEARCH_CACHE, key = GoodsCacheConstant.GOODS_SEARCH_KEY,
            expire = GoodsCacheConstant.GOODS_SEARCH_EXPIRE, timeUnit = TimeUnit.MINUTES, cacheType = CacheType.BOTH)
    public Result<PageResponse<GoodsResponse>> searchGoods(String keyword, Integer pageNum, Integer pageSize) {
        try {
            log.info("搜索商品: keyword={}, pageNum={}, pageSize={}", keyword, pageNum, pageSize);
            long startTime = System.currentTimeMillis();
            
            IPage<Goods> goodsPage = goodsService.searchGoods(keyword, pageNum, pageSize);
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("商品搜索成功: keyword={}, 总数={}, 耗时={}ms", 
                    keyword, goodsPage.getTotal(), duration);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("搜索商品失败: keyword={}", keyword, e);
            return Result.error("GOODS_SEARCH_ERROR", "搜索商品失败: " + e.getMessage());
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 将GoodsCreateRequest转换为Goods实体
     */
    private Goods convertCreateRequestToEntity(GoodsCreateRequest request) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(request, goods);
        return goods;
    }

    /**
     * 将GoodsUpdateRequest转换为Goods实体
     */
    private Goods convertUpdateRequestToEntity(GoodsUpdateRequest request) {
        Goods goods = new Goods();
        BeanUtils.copyProperties(request, goods);
        return goods;
    }

    /**
     * 将Goods实体转换为GoodsResponse
     */
    private GoodsResponse convertToResponse(Goods goods) {
        GoodsResponse response = new GoodsResponse();
        BeanUtils.copyProperties(goods, response);
        return response;
    }

    /**
     * 构建分页结果
     */
    private Result<PageResponse<GoodsResponse>> buildPageResult(IPage<Goods> goodsPage) {
        PageResponse<GoodsResponse> pageResponse = new PageResponse<>();
        List<GoodsResponse> responseList = goodsPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        pageResponse.setDatas(responseList);
        pageResponse.setTotal(goodsPage.getTotal());
        pageResponse.setCurrentPage((int) goodsPage.getCurrent());
        pageResponse.setPageSize((int) goodsPage.getSize());
        pageResponse.setTotalPage((int) goodsPage.getPages());

        return Result.success(pageResponse);
    }
}