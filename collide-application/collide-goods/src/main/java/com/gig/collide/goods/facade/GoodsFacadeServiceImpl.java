package com.gig.collide.goods.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.service.GoodsService;
import com.gig.collide.web.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品门面服务实现 - 简洁版
 * 基于简洁版SQL设计和API
 * 
 * @author GIG Team  
 * @version 2.0.0 (本地聚合服务)
 */
@Slf4j
@DubboService(version = "1.0.0")
@RequiredArgsConstructor
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    private final GoodsService goodsService;

    @Override
    public Result<GoodsResponse> createGoods(GoodsCreateRequest request) {
        try {
            log.info("RPC创建商品: name={}, sellerId={}", request.getName(), request.getSellerId());

            // 转换请求对象为实体
            Goods goods = convertCreateRequestToEntity(request);
            
            // 调用业务逻辑
            Goods savedGoods = goodsService.createGoods(goods);
            
            // 转换响应对象
            GoodsResponse response = convertToResponse(savedGoods);
            
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
    public Result<GoodsResponse> updateGoods(GoodsUpdateRequest request) {
        try {
            log.info("RPC更新商品: id={}", request.getId());

            // 转换请求对象为实体
            Goods goods = convertUpdateRequestToEntity(request);
            
            // 调用业务逻辑
            Goods updatedGoods = goodsService.updateGoods(goods);
            
            // 转换响应对象
            GoodsResponse response = convertToResponse(updatedGoods);
            
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
    public Result<Void> deleteGoods(GoodsDeleteRequest request) {
        try {
            log.info("RPC删除商品: id={}", request.getId());

            boolean success = goodsService.deleteGoods(
                    request.getId(), 
                    request.getDeleteReason(), 
                    request.getOperatorId()
            );
            
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("GOODS_DELETE_FAILED", "删除商品失败");
            }
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.error("GOODS_DELETE_ERROR", "删除商品失败: " + e.getMessage());
        }
    }

    @Override
    public Result<GoodsResponse> getGoodsById(Long goodsId) {
        try {
            log.info("RPC获取商品详情: goodsId={}", goodsId);

            Goods goods = goodsService.getGoodsById(goodsId);
            if (goods == null) {
                return Result.error("GOODS_NOT_FOUND", "商品不存在");
            }
            
            GoodsResponse response = convertToResponse(goods);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return Result.error("GOODS_GET_ERROR", "获取商品详情失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<GoodsResponse>> queryGoods(GoodsQueryRequest request) {
        try {
            log.info("RPC分页查询商品: pageNum={}, pageSize={}", request.getPageNum(), request.getPageSize());

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

            return Result.success(pageResponse);
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return Result.error("GOODS_QUERY_ERROR", "查询商品失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<GoodsResponse>> getGoodsByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        try {
            IPage<Goods> goodsPage = goodsService.getGoodsByCategory(categoryId, pageNum, pageSize);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("根据分类查询商品失败", e);
            return Result.error("GOODS_CATEGORY_QUERY_ERROR", "查询分类商品失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<GoodsResponse>> getGoodsBySeller(Long sellerId, Integer pageNum, Integer pageSize) {
        try {
            IPage<Goods> goodsPage = goodsService.getGoodsBySeller(sellerId, pageNum, pageSize);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("根据商家查询商品失败", e);
            return Result.error("GOODS_SELLER_QUERY_ERROR", "查询商家商品失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateStock(Long goodsId, Integer stockChange) {
        try {
            log.info("RPC更新商品库存: goodsId={}, stockChange={}", goodsId, stockChange);

            boolean success = goodsService.updateStock(goodsId, stockChange);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("STOCK_UPDATE_FAILED", "更新库存失败");
            }
        } catch (Exception e) {
            log.error("更新库存失败", e);
            return Result.error("STOCK_UPDATE_ERROR", "更新库存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> updateSalesCount(Long goodsId, Integer salesChange) {
        try {
            log.info("RPC更新商品销量: goodsId={}, salesChange={}", goodsId, salesChange);

            boolean success = goodsService.updateSalesCount(goodsId, salesChange);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("SALES_UPDATE_FAILED", "更新销量失败");
            }
        } catch (Exception e) {
            log.error("更新销量失败", e);
            return Result.error("SALES_UPDATE_ERROR", "更新销量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> increaseViewCount(Long goodsId) {
        try {
            boolean success = goodsService.increaseViewCount(goodsId);
            if (success) {
                return Result.success(null);
            } else {
                return Result.error("VIEW_COUNT_UPDATE_FAILED", "更新浏览量失败");
            }
        } catch (Exception e) {
            log.error("增加浏览量失败", e);
            return Result.error("VIEW_COUNT_ERROR", "增加浏览量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchUpdateStatus(List<Long> goodsIds, String status) {
        try {
            log.info("RPC批量更新商品状态: count={}, status={}", 
                    goodsIds != null ? goodsIds.size() : 0, status);

            int updateCount = goodsService.batchUpdateStatus(goodsIds, status);
            if (updateCount > 0) {
                return Result.success(null);
            } else {
                return Result.error("BATCH_UPDATE_FAILED", "批量更新状态失败");
            }
        } catch (Exception e) {
            log.error("批量更新状态失败", e);
            return Result.error("BATCH_UPDATE_ERROR", "批量更新状态失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getGoodsStatistics(Long goodsId) {
        try {
            Map<String, Object> statistics = goodsService.getGoodsStatistics(goodsId);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取商品统计失败", e);
            return Result.error("STATISTICS_ERROR", "获取统计信息失败: " + e.getMessage());
        }
    }

    @Override
    public Result<PageResponse<GoodsResponse>> searchGoods(String keyword, Integer pageNum, Integer pageSize) {
        try {
            IPage<Goods> goodsPage = goodsService.searchGoods(keyword, pageNum, pageSize);
            return buildPageResult(goodsPage);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
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