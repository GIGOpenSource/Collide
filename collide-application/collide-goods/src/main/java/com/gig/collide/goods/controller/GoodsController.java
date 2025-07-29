package com.gig.collide.goods.controller;

import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品管理控制器
 * 提供商品相关的HTTP REST API接口
 * 
 * 主要功能：
 * - 商品CRUD操作（创建、查询、更新、删除）
 * - 商品分类查询
 * - 商品库存管理
 * - 商品搜索与筛选
 * - 商品统计信息
 * 
 * 注意：控制器层主要负责HTTP请求处理和参数验证，
 * 具体的业务逻辑由GoodsFacadeService处理
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@Tag(name = "商品管理", description = "商品相关的API接口")
public class GoodsController {

    @Autowired
    private GoodsFacadeService goodsFacadeService;

    // =================== 商品CRUD操作 ===================

    /**
     * 创建商品
     * 
     * @param request 商品创建请求参数，包含商品基本信息
     * @return 创建结果，包含商品详细信息
     */
    @PostMapping("/create")
    @Operation(summary = "创建商品", description = "创建新商品，支持多种商品类型和状态")
    public Result<GoodsResponse> createGoods(@Validated @RequestBody GoodsCreateRequest request) {
        try {
            log.info("HTTP创建商品: name={}, sellerId={}", 
                    request.getName(), request.getSellerId());
            
            // 委托给facade服务处理
            return goodsFacadeService.createGoods(request);
        } catch (Exception e) {
            log.error("创建商品失败", e);
            return Result.error("CREATE_GOODS_ERROR", "创建商品失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品信息
     * 
     * @param request 商品更新请求参数
     * @return 更新结果，包含更新后的商品信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新商品", description = "更新商品信息，支持部分字段更新")
    public Result<GoodsResponse> updateGoods(@Validated @RequestBody GoodsUpdateRequest request) {
        try {
            log.info("HTTP更新商品: goodsId={}", request.getId());
            
            // 委托给facade服务处理
            return goodsFacadeService.updateGoods(request);
        } catch (Exception e) {
            log.error("更新商品失败", e);
            return Result.error("UPDATE_GOODS_ERROR", "更新商品失败: " + e.getMessage());
        }
    }

    /**
     * 删除商品（逻辑删除）
     * 
     * @param goodsId 商品ID
     * @param operatorId 操作人ID
     * @return 删除操作结果
     */
    @DeleteMapping("/{goodsId}")
    @Operation(summary = "删除商品", description = "逻辑删除商品，设置状态为inactive")
    public Result<Void> deleteGoods(
            @PathVariable Long goodsId,
            @Parameter(description = "操作人ID") @RequestParam Long operatorId) {
        try {
            log.info("HTTP删除商品: goodsId={}, operatorId={}", goodsId, operatorId);
            
            // 创建删除请求
            GoodsDeleteRequest request = new GoodsDeleteRequest();
            request.setId(goodsId);
            request.setOperatorId(operatorId);
            
            return goodsFacadeService.deleteGoods(request);
        } catch (Exception e) {
            log.error("删除商品失败", e);
            return Result.error("DELETE_GOODS_ERROR", "删除商品失败: " + e.getMessage());
        }
    }

    /**
     * 获取商品详情
     * 
     * @param goodsId 商品ID
     * @return 商品详细信息
     */
    @GetMapping("/{goodsId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品的详细信息")
    public Result<GoodsResponse> getGoodsById(@PathVariable Long goodsId) {
        try {
            log.debug("HTTP获取商品详情: goodsId={}", goodsId);
            
            return goodsFacadeService.getGoodsById(goodsId);
        } catch (Exception e) {
            log.error("获取商品详情失败", e);
            return Result.error("GET_GOODS_ERROR", "获取商品详情失败: " + e.getMessage());
        }
    }

    // =================== 商品查询与筛选 ===================

    /**
     * 分页查询商品列表
     * 
     * @param sellerId 卖家ID（可选）
     * @param categoryId 分类ID（可选）
     * @param goodsType 商品类型（可选）
     * @param status 商品状态（可选）
     * @param keyword 关键词搜索（可选）
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 商品分页列表
     */
    @GetMapping("/query")
    @Operation(summary = "分页查询商品", description = "支持多种条件筛选的商品分页查询")
    public Result<PageResponse<GoodsResponse>> queryGoods(
            @Parameter(description = "卖家ID") @RequestParam(required = false) Long sellerId,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "商品类型") @RequestParam(required = false) String goodsType,
            @Parameter(description = "商品状态") @RequestParam(required = false) String status,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP分页查询商品: sellerId={}, categoryId={}, page={}, size={}", 
                    sellerId, categoryId, page, size);
            
            // 创建查询请求
            GoodsQueryRequest request = new GoodsQueryRequest();
            request.setSellerId(sellerId);
            request.setCategoryId(categoryId);
            request.setGoodsType(goodsType);
            request.setStatus(status);
            request.setKeyword(keyword);
            request.setPage(page);
            request.setSize(size);
            
            return goodsFacadeService.queryGoods(request);
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return Result.error("QUERY_GOODS_ERROR", "分页查询商品失败: " + e.getMessage());
        }
    }

    /**
     * 根据分类查询商品
     * 
     * @param categoryId 分类ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 分类下的商品分页列表
     */
    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询商品", description = "获取指定分类下的商品列表")
    public Result<PageResponse<GoodsResponse>> getGoodsByCategory(
            @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP根据分类查询商品: categoryId={}, page={}, size={}", categoryId, page, size);
            
            return goodsFacadeService.getGoodsByCategory(categoryId, page, size);
        } catch (Exception e) {
            log.error("根据分类查询商品失败", e);
            return Result.error("GET_GOODS_BY_CATEGORY_ERROR", "根据分类查询商品失败: " + e.getMessage());
        }
    }

    /**
     * 根据卖家查询商品
     * 
     * @param sellerId 卖家ID
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 卖家的商品分页列表
     */
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "根据卖家查询商品", description = "获取指定卖家的商品列表")
    public Result<PageResponse<GoodsResponse>> getGoodsBySeller(
            @PathVariable Long sellerId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP根据卖家查询商品: sellerId={}, page={}, size={}", sellerId, page, size);
            
            return goodsFacadeService.getGoodsBySeller(sellerId, page, size);
        } catch (Exception e) {
            log.error("根据卖家查询商品失败", e);
            return Result.error("GET_GOODS_BY_SELLER_ERROR", "根据卖家查询商品失败: " + e.getMessage());
        }
    }

    /**
     * 搜索商品
     * 
     * @param keyword 搜索关键词
     * @param page 页码，从1开始
     * @param size 每页大小
     * @return 搜索结果分页列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public Result<PageResponse<GoodsResponse>> searchGoods(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "20") Integer size) {
        try {
            log.debug("HTTP搜索商品: keyword={}, page={}, size={}", keyword, page, size);
            
            return goodsFacadeService.searchGoods(keyword, page, size);
        } catch (Exception e) {
            log.error("搜索商品失败", e);
            return Result.error("SEARCH_GOODS_ERROR", "搜索商品失败: " + e.getMessage());
        }
    }

    // =================== 库存管理 ===================

    /**
     * 更新商品库存
     * 
     * @param goodsId 商品ID
     * @param stockChange 库存变化量（正数为增加，负数为减少）
     * @return 库存更新结果
     */
    @PostMapping("/{goodsId}/stock")
    @Operation(summary = "更新商品库存", description = "更新商品的库存数量（支持增加或减少）")
    public Result<Void> updateStock(
            @PathVariable Long goodsId,
            @Parameter(description = "库存变化量（正数增加，负数减少）") @RequestParam Integer stockChange) {
        try {
            log.info("HTTP更新商品库存: goodsId={}, stockChange={}", goodsId, stockChange);
            
            return goodsFacadeService.updateStock(goodsId, stockChange);
        } catch (Exception e) {
            log.error("更新商品库存失败", e);
            return Result.error("UPDATE_STOCK_ERROR", "更新商品库存失败: " + e.getMessage());
        }
    }

    /**
     * 更新商品销量
     * 
     * @param goodsId 商品ID
     * @param salesChange 销量变化量
     * @return 销量更新结果
     */
    @PostMapping("/{goodsId}/sales")
    @Operation(summary = "更新商品销量", description = "更新商品的销量统计")
    public Result<Void> updateSalesCount(
            @PathVariable Long goodsId,
            @Parameter(description = "销量变化量") @RequestParam Integer salesChange) {
        try {
            log.info("HTTP更新商品销量: goodsId={}, salesChange={}", goodsId, salesChange);
            
            return goodsFacadeService.updateSalesCount(goodsId, salesChange);
        } catch (Exception e) {
            log.error("更新商品销量失败", e);
            return Result.error("UPDATE_SALES_ERROR", "更新商品销量失败: " + e.getMessage());
        }
    }

    /**
     * 增加商品浏览量
     * 
     * @param goodsId 商品ID
     * @return 浏览量更新结果
     */
    @PostMapping("/{goodsId}/view")
    @Operation(summary = "增加商品浏览量", description = "增加商品的浏览次数统计")
    public Result<Void> increaseViewCount(@PathVariable Long goodsId) {
        try {
            log.debug("HTTP增加商品浏览量: goodsId={}", goodsId);
            
            return goodsFacadeService.increaseViewCount(goodsId);
        } catch (Exception e) {
            log.error("增加商品浏览量失败", e);
            return Result.error("INCREASE_VIEW_ERROR", "增加商品浏览量失败: " + e.getMessage());
        }
    }

    // =================== 批量操作 ===================

    /**
     * 批量更新商品状态
     * 
     * @param goodsIds 商品ID列表
     * @param status 新状态
     * @return 批量更新结果
     */
    @PostMapping("/batch/status")
    @Operation(summary = "批量更新商品状态", description = "批量更新多个商品的状态")
    public Result<Void> batchUpdateStatus(
            @RequestBody List<Long> goodsIds,
            @Parameter(description = "新状态") @RequestParam String status) {
        try {
            log.info("HTTP批量更新商品状态: goodsIds={}, status={}", goodsIds, status);
            
            return goodsFacadeService.batchUpdateStatus(goodsIds, status);
        } catch (Exception e) {
            log.error("批量更新商品状态失败", e);
            return Result.error("BATCH_UPDATE_STATUS_ERROR", "批量更新商品状态失败: " + e.getMessage());
        }
    }

    // =================== 统计信息 ===================

    /**
     * 获取商品统计信息
     * 
     * @param goodsId 商品ID
     * @return 商品统计信息（包含销量、浏览量、库存等）
     */
    @GetMapping("/{goodsId}/statistics")
    @Operation(summary = "获取商品统计", description = "获取商品的统计信息，包括销量、浏览量、库存等")
    public Result<Map<String, Object>> getGoodsStatistics(@PathVariable Long goodsId) {
        try {
            log.debug("HTTP获取商品统计: goodsId={}", goodsId);
            
            return goodsFacadeService.getGoodsStatistics(goodsId);
        } catch (Exception e) {
            log.error("获取商品统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取商品统计失败: " + e.getMessage());
        }
    }
}