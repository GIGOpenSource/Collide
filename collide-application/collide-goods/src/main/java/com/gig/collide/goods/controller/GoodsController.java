package com.gig.collide.goods.controller;

import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.request.GoodsQueryRequest;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.goods.facade.GoodsFacadeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 商品管理REST控制器 - 缓存增强版
 * 提供商品的完整REST API接口
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/goods")
@RequiredArgsConstructor
@Validated
@Tag(name = "商品管理", description = "商品的增删改查、库存管理、统计分析等功能")
public class GoodsController {

    private final GoodsFacadeServiceImpl goodsFacadeService;

    // =================== 基础CRUD操作 ===================

    @PostMapping
    @Operation(summary = "创建商品", description = "创建新商品，支持四种商品类型")
    public Result<Void> createGoods(@Valid @RequestBody GoodsCreateRequest request) {
        log.debug("REST创建商品: name={}, type={}", request.getName(), request.getGoodsType());
        return goodsFacadeService.createGoods(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息，自动增加浏览量")
    public Result<GoodsResponse> getGoodsById(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id) {
        log.debug("REST获取商品详情: id={}", id);
        return goodsFacadeService.getGoodsById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品信息", description = "更新指定商品的信息")
    public Result<GoodsResponse> updateGoods(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Valid @RequestBody GoodsCreateRequest request) {
        log.debug("REST更新商品: id={}, name={}", id, request.getName());
        return goodsFacadeService.updateGoods(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "软删除指定商品（设置为下架状态）")
    public Result<Void> deleteGoods(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id) {
        log.debug("REST删除商品: id={}", id);
        return goodsFacadeService.deleteGoods(id);
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除商品", description = "批量软删除多个商品")
    public Result<Void> batchDeleteGoods(
            @Parameter(description = "商品ID列表") @RequestBody @NotNull List<Long> ids) {
        log.debug("REST批量删除商品: count={}", ids.size());
        return goodsFacadeService.batchDeleteGoods(ids);
    }

    // =================== 查询操作 ===================

    @PostMapping("/query")
    @Operation(summary = "分页查询商品", description = "支持多条件分页查询商品列表")
    public PageResponse<GoodsResponse> queryGoods(@Valid @RequestBody GoodsQueryRequest request) {
        log.debug("REST分页查询商品: type={}, page={}, size={}", 
                request.getGoodsType(), request.getCurrentPage(), request.getPageSize());
        return goodsFacadeService.queryGoods(request);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "根据分类查询商品", description = "获取指定分类下的商品列表")
    public PageResponse<GoodsResponse> getGoodsByCategory(
            @Parameter(description = "分类ID") @PathVariable @NotNull @Min(1) Long categoryId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST根据分类查询商品: categoryId={}, page={}, size={}", categoryId, currentPage, pageSize);
        return goodsFacadeService.getGoodsByCategory(categoryId, currentPage, pageSize);
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "根据商家查询商品", description = "获取指定商家的商品列表")
    public PageResponse<GoodsResponse> getGoodsBySeller(
            @Parameter(description = "商家ID") @PathVariable @NotNull @Min(1) Long sellerId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST根据商家查询商品: sellerId={}, page={}, size={}", sellerId, currentPage, pageSize);
        return goodsFacadeService.getGoodsBySeller(sellerId, currentPage, pageSize);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门商品", description = "获取按销量排序的热门商品列表")
    public PageResponse<GoodsResponse> getHotGoods(
            @Parameter(description = "商品类型") @RequestParam(required = false) String goodsType,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST获取热门商品: type={}, page={}, size={}", goodsType, currentPage, pageSize);
        return goodsFacadeService.getHotGoods(goodsType, currentPage, pageSize);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品名称和描述")
    public PageResponse<GoodsResponse> searchGoods(
            @Parameter(description = "搜索关键词") @RequestParam @NotNull String keyword,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST搜索商品: keyword={}, page={}, size={}", keyword, currentPage, pageSize);
        return goodsFacadeService.searchGoods(keyword, currentPage, pageSize);
    }

    @GetMapping("/price-range")
    @Operation(summary = "按价格区间查询", description = "根据价格区间查询商品")
    public PageResponse<GoodsResponse> getGoodsByPriceRange(
            @Parameter(description = "商品类型") @RequestParam @NotNull String goodsType,
            @Parameter(description = "最低价格") @RequestParam @NotNull Object minPrice,
            @Parameter(description = "最高价格") @RequestParam @NotNull Object maxPrice,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST按价格区间查询: type={}, min={}, max={}, page={}, size={}", 
                goodsType, minPrice, maxPrice, currentPage, pageSize);
        return goodsFacadeService.getGoodsByPriceRange(goodsType, minPrice, maxPrice, currentPage, pageSize);
    }

    // =================== 库存管理 ===================

    @GetMapping("/{id}/stock/check")
    @Operation(summary = "检查库存", description = "检查指定商品的库存是否充足")
    public Result<Boolean> checkStock(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "需要数量") @RequestParam @NotNull @Min(1) Integer quantity) {
        log.debug("REST检查库存: goodsId={}, quantity={}", id, quantity);
        return goodsFacadeService.checkStock(id, quantity);
    }

    @PostMapping("/{id}/stock/reduce")
    @Operation(summary = "扣减库存", description = "扣减指定商品的库存数量")
    public Result<Void> reduceStock(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "扣减数量") @RequestParam @NotNull @Min(1) Integer quantity) {
        log.debug("REST扣减库存: goodsId={}, quantity={}", id, quantity);
        return goodsFacadeService.reduceStock(id, quantity);
    }

    @PostMapping("/stock/batch-reduce")
    @Operation(summary = "批量扣减库存", description = "批量扣减多个商品的库存")
    public Result<Void> batchReduceStock(
            @Parameter(description = "商品ID和数量映射") @RequestBody @NotNull Map<Long, Integer> stockMap) {
        log.debug("REST批量扣减库存: count={}", stockMap.size());
        return goodsFacadeService.batchReduceStock(stockMap);
    }

    @GetMapping("/low-stock")
    @Operation(summary = "查询低库存商品", description = "查询库存不足的商品列表")
    public Result<List<GoodsResponse>> getLowStockGoods(
            @Parameter(description = "库存阈值") @RequestParam(defaultValue = "10") @Min(0) Integer threshold) {
        log.debug("REST查询低库存商品: threshold={}", threshold);
        return goodsFacadeService.getLowStockGoods(threshold);
    }

    // =================== 统计操作 ===================

    @PostMapping("/{id}/sales/increase")
    @Operation(summary = "增加销量", description = "增加指定商品的销量统计")
    public Result<Void> increaseSales(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "增加数量") @RequestParam @NotNull @Min(1) Long count) {
        log.debug("REST增加销量: goodsId={}, count={}", id, count);
        return goodsFacadeService.increaseSales(id, count);
    }

    @PostMapping("/{id}/views/increase")
    @Operation(summary = "增加浏览量", description = "增加指定商品的浏览量统计")
    public Result<Void> increaseViews(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "增加数量") @RequestParam(defaultValue = "1") @Min(1) Long count) {
        log.debug("REST增加浏览量: goodsId={}, count={}", id, count);
        return goodsFacadeService.increaseViews(id, count);
    }

    @PostMapping("/views/batch-increase")
    @Operation(summary = "批量增加浏览量", description = "批量增加多个商品的浏览量")
    public Result<Void> batchIncreaseViews(
            @Parameter(description = "商品ID和浏览量映射") @RequestBody @NotNull Map<Long, Long> viewMap) {
        log.debug("REST批量增加浏览量: count={}", viewMap.size());
        return goodsFacadeService.batchIncreaseViews(viewMap);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取商品统计", description = "获取各类型商品的统计信息")
    public Result<List<Map<String, Object>>> getGoodsStatistics() {
        log.debug("REST获取商品统计信息");
        return goodsFacadeService.getGoodsStatistics();
    }

    // =================== 状态管理 ===================

    @PostMapping("/{id}/publish")
    @Operation(summary = "上架商品", description = "将指定商品设置为上架状态")
    public Result<Void> publishGoods(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id) {
        log.debug("REST上架商品: goodsId={}", id);
        return goodsFacadeService.publishGoods(id);
    }

    @PostMapping("/{id}/unpublish")
    @Operation(summary = "下架商品", description = "将指定商品设置为下架状态")
    public Result<Void> unpublishGoods(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id) {
        log.debug("REST下架商品: goodsId={}", id);
        return goodsFacadeService.unpublishGoods(id);
    }

    @PostMapping("/batch-publish")
    @Operation(summary = "批量上架商品", description = "批量设置多个商品为上架状态")
    public Result<Void> batchPublishGoods(
            @Parameter(description = "商品ID列表") @RequestBody @NotNull List<Long> goodsIds) {
        log.debug("REST批量上架商品: count={}", goodsIds.size());
        return goodsFacadeService.batchPublishGoods(goodsIds);
    }

    @PostMapping("/batch-unpublish")
    @Operation(summary = "批量下架商品", description = "批量设置多个商品为下架状态")
    public Result<Void> batchUnpublishGoods(
            @Parameter(description = "商品ID列表") @RequestBody @NotNull List<Long> goodsIds) {
        log.debug("REST批量下架商品: count={}", goodsIds.size());
        return goodsFacadeService.batchUnpublishGoods(goodsIds);
    }

    // =================== 业务验证 ===================

    @GetMapping("/{id}/purchase/validate")
    @Operation(summary = "验证商品购买", description = "验证指定商品是否可以购买")
    public Result<Map<String, Object>> validatePurchase(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "购买数量") @RequestParam @NotNull @Min(1) Integer quantity) {
        log.debug("REST验证商品购买: goodsId={}, quantity={}", id, quantity);
        return goodsFacadeService.validatePurchase(id, quantity);
    }

    @GetMapping("/{id}/purchase/info")
    @Operation(summary = "获取购买信息", description = "获取商品的详细购买信息")
    public Result<Map<String, Object>> getGoodsPurchaseInfo(
            @Parameter(description = "商品ID") @PathVariable @NotNull @Min(1) Long id,
            @Parameter(description = "购买数量") @RequestParam @NotNull @Min(1) Integer quantity) {
        log.debug("REST获取商品购买信息: goodsId={}, quantity={}", id, quantity);
        return goodsFacadeService.getGoodsPurchaseInfo(id, quantity);
    }

    // =================== 快捷查询 ===================

    @GetMapping("/coin-packages")
    @Operation(summary = "获取金币充值包", description = "获取所有可用的金币充值包")
    public PageResponse<GoodsResponse> getCoinPackages(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST获取金币充值包: page={}, size={}", currentPage, pageSize);
        return goodsFacadeService.getCoinPackages(currentPage, pageSize);
    }

    @GetMapping("/subscriptions")
    @Operation(summary = "获取订阅服务", description = "获取所有可用的订阅服务")
    public PageResponse<GoodsResponse> getSubscriptionServices(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST获取订阅服务: page={}, size={}", currentPage, pageSize);
        return goodsFacadeService.getSubscriptionServices(currentPage, pageSize);
    }

    @GetMapping("/contents")
    @Operation(summary = "获取付费内容", description = "获取所有可用的付费内容")
    public PageResponse<GoodsResponse> getContentGoods(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST获取付费内容: page={}, size={}", currentPage, pageSize);
        return goodsFacadeService.getContentGoods(currentPage, pageSize);
    }

    @GetMapping("/physical")
    @Operation(summary = "获取实体商品", description = "获取所有可用的实体商品")
    public PageResponse<GoodsResponse> getPhysicalGoods(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") @Min(1) Integer currentPage,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "20") @Min(1) Integer pageSize) {
        log.debug("REST获取实体商品: page={}, size={}", currentPage, pageSize);
        return goodsFacadeService.getPhysicalGoods(currentPage, pageSize);
    }

    // =================== 内容同步管理接口 ===================

    @PostMapping("/sync/content/create")
    @Operation(summary = "根据内容创建商品", description = "当新内容发布时，自动创建对应的商品记录")
    public Result<Void> createGoodsFromContent(
            @Parameter(description = "内容ID") @RequestParam @NotNull Long contentId,
            @Parameter(description = "内容标题") @RequestParam @NotNull String contentTitle,
            @Parameter(description = "内容描述") @RequestParam(required = false) String contentDesc,
            @Parameter(description = "分类ID") @RequestParam @NotNull Long categoryId,
            @Parameter(description = "分类名称") @RequestParam @NotNull String categoryName,
            @Parameter(description = "作者ID") @RequestParam @NotNull Long authorId,
            @Parameter(description = "作者昵称") @RequestParam @NotNull String authorNickname,
            @Parameter(description = "封面图URL") @RequestParam(required = false) String coverUrl,
            @Parameter(description = "金币价格") @RequestParam(required = false, defaultValue = "0") Long coinPrice,
            @Parameter(description = "内容状态") @RequestParam(required = false, defaultValue = "PUBLISHED") String contentStatus) {
        
        log.info("REST管理-根据内容创建商品: contentId={}, title={}", contentId, contentTitle);
        return goodsFacadeService.createGoodsFromContent(contentId, contentTitle, contentDesc,
                categoryId, categoryName, authorId, authorNickname, coverUrl, coinPrice, contentStatus);
    }

    @PutMapping("/sync/content/{contentId}/info")
    @Operation(summary = "同步内容信息到商品", description = "当内容信息更新时，同步更新商品信息")
    public Result<Void> syncContentToGoods(
            @Parameter(description = "内容ID") @PathVariable @NotNull Long contentId,
            @Parameter(description = "内容标题") @RequestParam @NotNull String contentTitle,
            @Parameter(description = "内容描述") @RequestParam(required = false) String contentDesc,
            @Parameter(description = "分类ID") @RequestParam @NotNull Long categoryId,
            @Parameter(description = "分类名称") @RequestParam @NotNull String categoryName,
            @Parameter(description = "作者ID") @RequestParam @NotNull Long authorId,
            @Parameter(description = "作者昵称") @RequestParam @NotNull String authorNickname,
            @Parameter(description = "封面图URL") @RequestParam(required = false) String coverUrl) {
        
        log.info("REST管理-同步内容信息到商品: contentId={}, title={}", contentId, contentTitle);
        return goodsFacadeService.syncContentToGoods(contentId, contentTitle, contentDesc,
                categoryId, categoryName, authorId, authorNickname, coverUrl);
    }

    @PutMapping("/sync/content/{contentId}/status")
    @Operation(summary = "同步内容状态到商品", description = "当内容状态变更时，同步更新商品状态")
    public Result<Void> syncContentStatusToGoods(
            @Parameter(description = "内容ID") @PathVariable @NotNull Long contentId,
            @Parameter(description = "内容状态") @RequestParam @NotNull String contentStatus) {
        
        log.info("REST管理-同步内容状态到商品: contentId={}, status={}", contentId, contentStatus);
        return goodsFacadeService.syncContentStatusToGoods(contentId, contentStatus);
    }

    @PutMapping("/sync/content/{contentId}/price")
    @Operation(summary = "同步内容价格到商品", description = "当内容付费配置变更时，同步更新商品价格")
    public Result<Void> syncContentPriceToGoods(
            @Parameter(description = "内容ID") @PathVariable @NotNull Long contentId,
            @Parameter(description = "金币价格") @RequestParam @NotNull Long coinPrice,
            @Parameter(description = "是否启用付费") @RequestParam(required = false) Boolean isActive) {
        
        log.info("REST管理-同步内容价格到商品: contentId={}, coinPrice={}, isActive={}", 
                contentId, coinPrice, isActive);
        return goodsFacadeService.syncContentPriceToGoods(contentId, coinPrice, isActive);
    }

    @PostMapping("/sync/content/batch")
    @Operation(summary = "批量同步内容商品", description = "批量检查和同步内容与商品的一致性")
    public Result<Map<String, Object>> batchSyncContentGoods(
            @Parameter(description = "批处理大小") @RequestParam(defaultValue = "100") @Min(1) Integer batchSize) {
        
        log.info("REST管理-批量同步内容商品: batchSize={}", batchSize);
        return goodsFacadeService.batchSyncContentGoods(batchSize);
    }

    @DeleteMapping("/sync/content/{contentId}")
    @Operation(summary = "删除内容对应的商品", description = "当内容被删除时，删除对应的商品记录")
    public Result<Void> deleteGoodsByContentId(
            @Parameter(description = "内容ID") @PathVariable @NotNull Long contentId) {
        
        log.info("REST管理-删除内容商品: contentId={}", contentId);
        return goodsFacadeService.deleteGoodsByContentId(contentId);
    }

    @GetMapping("/sync/content/{contentId}")
    @Operation(summary = "根据内容ID获取商品", description = "查询内容对应的商品信息")
    public Result<GoodsResponse> getGoodsByContentId(
            @Parameter(description = "内容ID") @PathVariable @NotNull Long contentId) {
        
        log.debug("REST查询内容商品: contentId={}", contentId);
        return goodsFacadeService.getGoodsByContentId(contentId);
    }
}