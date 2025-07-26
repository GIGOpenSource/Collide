package com.gig.collide.goods.controller;

import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.goods.domain.service.GoodsDomainService;
import com.gig.collide.goods.domain.convertor.GoodsConvertor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端商品控制器
 * 提供商品浏览和购买相关接口（用户只能浏览和购买，不能管理商品）
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/goods")
@RequiredArgsConstructor
@Tag(name = "用户端商品管理", description = "用户商品浏览和购买接口")
public class GoodsController {
    
    private final GoodsDomainService goodsDomainService;
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{goodsId}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    public Result<GoodsInfo> getGoodsDetail(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId) {
        
        try {
            log.info("获取商品详情，商品ID：{}", goodsId);
            
            com.gig.collide.goods.domain.entity.Goods goods = goodsDomainService.getGoodsById(goodsId);
            if (goods == null) {
                return Result.error("GOODS_NOT_FOUND", "商品不存在");
            }
            
            // 只返回上架的商品给用户
            if (!"ON_SALE".equals(goods.getStatus())) {
                return Result.error("GOODS_NOT_AVAILABLE", "商品当前不可购买");
            }
            
            GoodsInfo goodsInfo = GoodsConvertor.INSTANCE.mapToVo(goods);
            return Result.success(goodsInfo);
            
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID：{}", goodsId, e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }
    
    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询商品列表", description = "获取可购买的商品列表")
    public Result<PageResponse<GoodsInfo>> getGoodsList(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer pageNo,
            
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") Integer pageSize,
            
            @Parameter(description = "商品类型") 
            @RequestParam(required = false) String type,
            
            @Parameter(description = "是否推荐", example = "true") 
            @RequestParam(required = false) Boolean recommended,
            
            @Parameter(description = "是否热门", example = "true") 
            @RequestParam(required = false) Boolean hot,
            
            @Parameter(description = "关键词搜索") 
            @RequestParam(required = false) String keyword) {
        
        try {
            log.info("分页查询商品列表，页码：{}，每页大小：{}，类型：{}，推荐：{}，热门：{}，关键词：{}", 
                pageNo, pageSize, type, recommended, hot, keyword);
            
            // 构建查询请求（只查询上架商品）
            com.gig.collide.api.goods.request.GoodsPageQueryRequest queryRequest = 
                new com.gig.collide.api.goods.request.GoodsPageQueryRequest();
            queryRequest.setPageNo(pageNo);
            queryRequest.setPageSize(pageSize);
            queryRequest.setType(type);
            queryRequest.setStatus("ON_SALE"); // 只查询上架商品
            queryRequest.setRecommended(recommended);
            queryRequest.setHot(hot);
            queryRequest.setKeyword(keyword);
            
            PageResponse<GoodsInfo> pageResponse = goodsDomainService.pageQueryGoodsForUser(queryRequest);
            
            log.info("商品列表查询完成，总记录数：{}，当前页记录数：{}", 
                pageResponse.getTotal(), pageResponse.getRecords().size());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("分页查询商品列表失败", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }
    
    /**
     * 获取推荐商品
     */
    @GetMapping("/recommended")
    @Operation(summary = "获取推荐商品", description = "获取推荐商品列表")
    public Result<java.util.List<GoodsInfo>> getRecommendedGoods(
            @Parameter(description = "最大返回数量", example = "10") 
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            log.info("获取推荐商品，限制数量：{}", limit);
            
            java.util.List<GoodsInfo> recommendedGoods = goodsDomainService.getRecommendedGoods(limit);
            
            log.info("推荐商品查询完成，返回数量：{}", recommendedGoods.size());
            return Result.success(recommendedGoods);
            
        } catch (Exception e) {
            log.error("获取推荐商品失败", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }
    
    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    @Operation(summary = "获取热门商品", description = "获取热门商品列表")
    public Result<java.util.List<GoodsInfo>> getHotGoods(
            @Parameter(description = "最大返回数量", example = "10") 
            @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            log.info("获取热门商品，限制数量：{}", limit);
            
            java.util.List<GoodsInfo> hotGoods = goodsDomainService.getHotGoods(limit);
            
            log.info("热门商品查询完成，返回数量：{}", hotGoods.size());
            return Result.success(hotGoods);
            
        } catch (Exception e) {
            log.error("获取热门商品失败", e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }
    
    /**
     * 检查商品是否可购买
     */
    @GetMapping("/{goodsId}/purchasable")
    @Operation(summary = "检查商品是否可购买", description = "检查商品当前状态是否支持购买")
    public Result<Boolean> checkPurchasable(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId,
            
            @Parameter(description = "购买数量", example = "1") 
            @RequestParam(defaultValue = "1") Integer quantity) {
        
        try {
            log.info("检查商品是否可购买，商品ID：{}，数量：{}", goodsId, quantity);
            
            boolean purchasable = goodsDomainService.checkPurchasable(goodsId, quantity);
            
            log.info("商品购买检查完成，商品ID：{}，是否可购买：{}", goodsId, purchasable);
            return Result.success(purchasable);
            
        } catch (Exception e) {
            log.error("检查商品购买状态失败，商品ID：{}", goodsId, e);
            return Result.error("SYSTEM_ERROR", "系统异常，请稍后重试");
        }
    }
}