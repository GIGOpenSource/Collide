package com.gig.collide.admin.controller;

import com.gig.collide.admin.service.AdminGoodsService;
import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 商品管理控制器
 * 
 * 独立的管理端控制器，不与用户端共用
 * 提供完整的商品管理功能，包括：
 * - 商品CRUD操作
 * - 批量操作
 * - 上下架管理
 * - 库存管理
 * - 数据统计
 * - 文件上传
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
@Tag(name = "管理后台-商品管理", description = "管理员专用的商品管理接口")
public class AdminGoodsController {
    
    private final AdminGoodsService adminGoodsService;
    
    /**
     * 创建商品
     */
    @PostMapping
    @Operation(summary = "创建商品", description = "管理员创建新商品")
    public Result<Long> createGoods(
            @Parameter(description = "商品创建请求", required = true)
            @Valid @RequestBody GoodsCreateRequest createRequest) {
        
        try {
            log.info("[管理后台] 创建商品，请求参数：{}", createRequest);
            
            Long goodsId = adminGoodsService.createGoods(createRequest);
            
            log.info("[管理后台] 商品创建成功，商品ID：{}", goodsId);
            return Result.success(goodsId);
            
        } catch (IllegalArgumentException e) {
            log.warn("[管理后台] 创建商品参数错误：{}", e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("[管理后台] 创建商品失败", e);
            return Result.error("CREATE_GOODS_ERROR", "创建商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新商品信息
     */
    @PutMapping("/{goodsId}")
    @Operation(summary = "更新商品信息", description = "管理员更新商品信息")
    public Result<Void> updateGoods(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId,
            
            @Parameter(description = "商品更新请求", required = true)
            @Valid @RequestBody GoodsUpdateRequest updateRequest) {
        
        try {
            log.info("[管理后台] 更新商品，商品ID：{}，请求参数：{}", goodsId, updateRequest);
            
            updateRequest.setGoodsId(goodsId);
            adminGoodsService.updateGoods(updateRequest);
            
            log.info("[管理后台] 商品更新成功，商品ID：{}", goodsId);
            return Result.success(null);
            
        } catch (IllegalArgumentException e) {
            log.warn("[管理后台] 更新商品参数错误，商品ID：{}，错误：{}", goodsId, e.getMessage());
            return Result.error("INVALID_PARAMETER", e.getMessage());
        } catch (Exception e) {
            log.error("[管理后台] 更新商品失败，商品ID：{}", goodsId, e);
            return Result.error("UPDATE_GOODS_ERROR", "更新商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除商品（软删除）
     */
    @DeleteMapping("/{goodsId}")
    @Operation(summary = "删除商品", description = "管理员删除商品（软删除）")
    public Result<Void> deleteGoods(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId) {
        
        try {
            log.info("[管理后台] 删除商品，商品ID：{}", goodsId);
            
            adminGoodsService.deleteGoods(goodsId);
            
            log.info("[管理后台] 商品删除成功，商品ID：{}", goodsId);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 删除商品失败，商品ID：{}", goodsId, e);
            return Result.error("DELETE_GOODS_ERROR", "删除商品失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取商品详情（管理端视图）
     */
    @GetMapping("/{goodsId}")
    @Operation(summary = "获取商品详情", description = "获取商品详细信息（包含所有状态）")
    public Result<GoodsInfo> getGoodsDetail(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId) {
        
        try {
            log.info("[管理后台] 获取商品详情，商品ID：{}", goodsId);
            
            GoodsInfo goodsInfo = adminGoodsService.getGoodsDetail(goodsId);
            
            return Result.success(goodsInfo);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取商品详情失败，商品ID：{}", goodsId, e);
            return Result.error("GET_GOODS_ERROR", "获取商品详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 分页查询商品列表（管理端全部数据）
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询商品", description = "管理员查看所有商品列表（包含所有状态）")
    public Result<PageResponse<GoodsInfo>> getGoodsList(
            @Parameter(description = "页码", example = "1") 
            @RequestParam(defaultValue = "1") Integer pageNo,
            
            @Parameter(description = "每页大小", example = "20") 
            @RequestParam(defaultValue = "20") Integer pageSize,
            
            @Parameter(description = "商品类型") 
            @RequestParam(required = false) String type,
            
            @Parameter(description = "商品状态") 
            @RequestParam(required = false) String status,
            
            @Parameter(description = "是否推荐", example = "true") 
            @RequestParam(required = false) Boolean recommended,
            
            @Parameter(description = "是否热门", example = "true") 
            @RequestParam(required = false) Boolean hot,
            
            @Parameter(description = "关键词搜索") 
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "创建者ID") 
            @RequestParam(required = false) Long creatorId,

            @Parameter(description = "创建时间开始", example = "2024-01-01") 
            @RequestParam(required = false) String createTimeStart,

            @Parameter(description = "创建时间结束", example = "2024-01-31") 
            @RequestParam(required = false) String createTimeEnd) {
        
        try {
            log.info("[管理后台] 分页查询商品，页码：{}，每页大小：{}，类型：{}，状态：{}，关键词：{}", 
                pageNo, pageSize, type, status, keyword);
            
            GoodsPageQueryRequest queryRequest = new GoodsPageQueryRequest();
            queryRequest.setPageNo(pageNo);
            queryRequest.setPageSize(pageSize);
            queryRequest.setType(type);
            queryRequest.setStatus(status);
            queryRequest.setRecommended(recommended);
            queryRequest.setHot(hot);
            queryRequest.setKeyword(keyword);
            queryRequest.setCreatorId(creatorId);
            queryRequest.setCreateTimeStart(createTimeStart);
            queryRequest.setCreateTimeEnd(createTimeEnd);
            
            PageResponse<GoodsInfo> pageResponse = adminGoodsService.pageQueryGoods(queryRequest);
            
            log.info("[管理后台] 商品列表查询完成，总记录数：{}，当前页记录数：{}", 
                pageResponse.getTotal(), pageResponse.getRecords().size());
            
            return Result.success(pageResponse);
            
        } catch (Exception e) {
            log.error("[管理后台] 分页查询商品失败", e);
            return Result.error("QUERY_GOODS_ERROR", "查询商品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品上架
     */
    @PostMapping("/{goodsId}/on-sale")
    @Operation(summary = "商品上架", description = "管理员将商品设置为上架状态")
    public Result<Void> putOnSale(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId) {
        
        try {
            log.info("[管理后台] 上架商品，商品ID：{}", goodsId);
            
            adminGoodsService.putOnSale(goodsId);
            
            log.info("[管理后台] 商品上架成功，商品ID：{}", goodsId);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 商品上架失败，商品ID：{}", goodsId, e);
            return Result.error("PUT_ON_SALE_ERROR", "商品上架失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品下架
     */
    @PostMapping("/{goodsId}/off-sale")
    @Operation(summary = "商品下架", description = "管理员将商品设置为下架状态")
    public Result<Void> putOffSale(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId) {
        
        try {
            log.info("[管理后台] 下架商品，商品ID：{}", goodsId);
            
            adminGoodsService.putOffSale(goodsId);
            
            log.info("[管理后台] 商品下架成功，商品ID：{}", goodsId);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 商品下架失败，商品ID：{}", goodsId, e);
            return Result.error("PUT_OFF_SALE_ERROR", "商品下架失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量上架商品
     */
    @PostMapping("/batch/on-sale")
    @Operation(summary = "批量上架商品", description = "管理员批量上架多个商品")
    public Result<Map<String, Object>> batchPutOnSale(
            @Parameter(description = "商品ID列表", required = true)
            @RequestBody List<Long> goodsIds) {
        
        try {
            log.info("[管理后台] 批量上架商品，商品ID列表：{}", goodsIds);
            
            Map<String, Object> result = adminGoodsService.batchPutOnSale(goodsIds);
            
            log.info("[管理后台] 商品批量上架完成，成功数量：{}", result.get("successCount"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 批量上架商品失败，商品ID列表：{}", goodsIds, e);
            return Result.error("BATCH_ON_SALE_ERROR", "批量上架失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量下架商品
     */
    @PostMapping("/batch/off-sale")
    @Operation(summary = "批量下架商品", description = "管理员批量下架多个商品")
    public Result<Map<String, Object>> batchPutOffSale(
            @Parameter(description = "商品ID列表", required = true)
            @RequestBody List<Long> goodsIds) {
        
        try {
            log.info("[管理后台] 批量下架商品，商品ID列表：{}", goodsIds);
            
            Map<String, Object> result = adminGoodsService.batchPutOffSale(goodsIds);
            
            log.info("[管理后台] 商品批量下架完成，成功数量：{}", result.get("successCount"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 批量下架商品失败，商品ID列表：{}", goodsIds, e);
            return Result.error("BATCH_OFF_SALE_ERROR", "批量下架失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品库存管理
     */
    @PostMapping("/{goodsId}/stock")
    @Operation(summary = "更新商品库存", description = "管理员更新商品库存")
    public Result<Void> updateStock(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId,
            
            @Parameter(description = "库存更新请求", required = true)
            @Valid @RequestBody GoodsStockRequest stockRequest) {
        
        try {
            log.info("[管理后台] 更新商品库存，商品ID：{}，请求参数：{}", goodsId, stockRequest);
            
            stockRequest.setGoodsId(goodsId);
            adminGoodsService.updateStock(stockRequest);
            
            log.info("[管理后台] 商品库存更新成功，商品ID：{}", goodsId);
            return Result.success(null);
            
        } catch (Exception e) {
            log.error("[管理后台] 更新商品库存失败，商品ID：{}", goodsId, e);
            return Result.error("UPDATE_STOCK_ERROR", "更新库存失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品图片上传
     */
    @PostMapping("/{goodsId}/upload-image")
    @Operation(summary = "上传商品图片", description = "管理员上传商品图片")
    public Result<Map<String, String>> uploadImage(
            @Parameter(description = "商品ID", required = true) 
            @PathVariable Long goodsId,
            
            @Parameter(description = "图片文件", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "图片类型")
            @RequestParam(defaultValue = "main") String imageType) {
        
        try {
            log.info("[管理后台] 上传商品图片，商品ID：{}，文件名：{}，图片类型：{}", 
                goodsId, file.getOriginalFilename(), imageType);
            
            Map<String, String> result = adminGoodsService.uploadImage(goodsId, file, imageType);
            
            log.info("[管理后台] 商品图片上传成功，商品ID：{}，图片URL：{}", goodsId, result.get("imageUrl"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 上传商品图片失败，商品ID：{}", goodsId, e);
            return Result.error("UPLOAD_IMAGE_ERROR", "图片上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 商品销售统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "商品销售统计", description = "获取商品销售统计信息")
    public Result<Map<String, Object>> getGoodsStatistics(
            @Parameter(description = "统计开始日期", example = "2024-01-01")
            @RequestParam(required = false) String startDate,
            
            @Parameter(description = "统计结束日期", example = "2024-01-31")
            @RequestParam(required = false) String endDate,
            
            @Parameter(description = "商品类型筛选")
            @RequestParam(required = false) String type) {
        
        try {
            log.info("[管理后台] 获取商品销售统计，开始日期：{}，结束日期：{}，类型：{}", startDate, endDate, type);
            
            Map<String, Object> statistics = adminGoodsService.getGoodsStatistics(startDate, endDate, type);
            
            log.info("[管理后台] 商品销售统计查询完成");
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("[管理后台] 获取商品销售统计失败", e);
            return Result.error("GET_STATISTICS_ERROR", "获取统计数据失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出商品数据
     */
    @GetMapping("/export")
    @Operation(summary = "导出商品数据", description = "导出商品数据到Excel")
    public Result<Map<String, String>> exportGoods(
            @Parameter(description = "商品类型筛选")
            @RequestParam(required = false) String type,
            
            @Parameter(description = "商品状态筛选")
            @RequestParam(required = false) String status,
            
            @Parameter(description = "导出格式")
            @RequestParam(defaultValue = "xlsx") String format) {
        
        try {
            log.info("[管理后台] 导出商品数据，类型：{}，状态：{}，格式：{}", type, status, format);
            
            Map<String, String> result = adminGoodsService.exportGoods(type, status, format);
            
            log.info("[管理后台] 商品数据导出成功，文件路径：{}", result.get("filePath"));
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("[管理后台] 导出商品数据失败", e);
            return Result.error("EXPORT_ERROR", "数据导出失败：" + e.getMessage());
        }
    }
} 