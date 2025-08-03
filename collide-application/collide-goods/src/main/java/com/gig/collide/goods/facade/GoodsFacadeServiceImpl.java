package com.gig.collide.goods.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.goods.GoodsFacadeService;
import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.request.GoodsQueryRequest;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.service.GoodsService;
import com.gig.collide.api.user.UserFacadeService;
import com.gig.collide.api.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品模块对外服务实现类 - 缓存增强版
 * 基于JetCache的高性能商品Facade服务
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@DubboService(version = "2.0.0", timeout = 5000)
@RequiredArgsConstructor
public class GoodsFacadeServiceImpl implements GoodsFacadeService {

    private final GoodsService goodsService;
    
    @DubboReference(version = "1.0.0", timeout = 10000, check = false)
    private UserFacadeService userFacadeService;

    // =================== 基础CRUD操作 ===================

    @Override
    public Result<Void> createGoods(GoodsCreateRequest request) {
        try {
            log.info("REST创建商品: name={}, type={}, sellerId={}", 
                    request.getName(), request.getGoodsType(), request.getSellerId());
            
            // 验证请求参数
            request.validateTypeSpecificFields();
            
            // 验证商家用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(request.getSellerId());
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("商家用户不存在，无法创建商品: sellerId={}", request.getSellerId());
                return Result.failure("SELLER_NOT_FOUND", "商家用户不存在");
            }
            
            // 转换为实体对象
            Goods goods = convertToEntity(request);
            
            // 创建商品
            Long goodsId = goodsService.createGoods(goods);
            
            log.info("商品创建成功: id={}, name={}, 商家={}({})", 
                    goodsId, request.getName(), request.getSellerId(), userResult.getData().getNickname());
            return Result.success();
            
        } catch (IllegalArgumentException e) {
            log.warn("商品创建参数错误: {}", e.getMessage());
            return Result.failure("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("商品创建失败: name={}, sellerId={}", request.getName(), request.getSellerId(), e);
            return Result.failure("商品创建失败: " + e.getMessage());
        }
    }

    @Override
    public Result<GoodsResponse> getGoodsById(Long id) {
        try {
            log.debug("REST查询商品详情: id={}", id);
            
            if (id == null || id <= 0) {
                return Result.failure("商品ID无效");
            }
            
            Goods goods = goodsService.getGoodsById(id);
            if (goods == null) {
                return Result.failure("商品不存在");
            }
            
            // 增加浏览量
            goodsService.increaseViews(id, 1L);
            
            GoodsResponse response = convertToResponse(goods);
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("查询商品详情失败: id={}", id, e);
            return Result.failure("查询失败: " + e.getMessage());
        }
    }

    @Override
    public Result<GoodsResponse> updateGoods(Long id, GoodsCreateRequest request) {
        try {
            log.info("REST更新商品: id={}, name={}", id, request.getName());
            
            if (id == null || id <= 0) {
                return Result.failure("商品ID无效");
            }
            
            // 验证请求参数
            request.validateTypeSpecificFields();
            
            // 转换为实体对象
            Goods goods = convertToEntity(request);
            goods.setId(id);
            
            // 更新商品
            boolean success = goodsService.updateGoods(goods);
            if (!success) {
                return Result.failure("商品更新失败");
            }
            
            // 获取更新后的商品信息
            Goods updatedGoods = goodsService.getGoodsById(id);
            GoodsResponse response = convertToResponse(updatedGoods);
            
            log.info("商品更新成功: id={}", id);
            return Result.success(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("商品更新参数错误: {}", e.getMessage());
            return Result.failure("参数错误: " + e.getMessage());
        } catch (Exception e) {
            log.error("商品更新失败: id={}", id, e);
            return Result.failure("商品更新失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteGoods(Long id) {
        try {
            log.info("REST删除商品: id={}", id);
            
            if (id == null || id <= 0) {
                return Result.failure("商品ID无效");
            }
            
            boolean success = goodsService.deleteGoods(id);
            if (!success) {
                return Result.failure("商品删除失败");
            }
            
            log.info("商品删除成功: id={}", id);
            return Result.success();
            
        } catch (Exception e) {
            log.error("商品删除失败: id={}", id, e);
            return Result.failure("商品删除失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchDeleteGoods(List<Long> ids) {
        try {
            log.info("REST批量删除商品: count={}", ids.size());
            
            if (CollectionUtils.isEmpty(ids)) {
                return Result.success();
            }
            
            boolean success = goodsService.batchDeleteGoods(ids);
            if (!success) {
                return Result.failure("批量删除失败");
            }
            
            log.info("批量删除商品成功: count={}", ids.size());
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量删除商品失败: count={}", ids.size(), e);
            return Result.failure("批量删除失败: " + e.getMessage());
        }
    }

    // =================== 查询操作 ===================

    @Override
    public PageResponse<GoodsResponse> queryGoods(GoodsQueryRequest request) {
        try {
            log.debug("REST分页查询商品: type={}, page={}, size={}", 
                    request.getGoodsType(), request.getCurrentPage(), request.getPageSize());
            
            // 验证查询参数
            request.validateParams();
            
            Page<Goods> page = new Page<>(request.getCurrentPage(), request.getPageSize());
            IPage<Goods> result = goodsService.queryGoods(page, request.getGoodsType(), request.getStatus());
            
            return convertToPageResponse(result);
            
        } catch (IllegalArgumentException e) {
            log.warn("商品查询参数错误: {}", e.getMessage());
            return PageResponse.empty();
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return PageResponse.empty();
        }
    }

    @Override
    public PageResponse<GoodsResponse> getGoodsByCategory(Long categoryId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("REST根据分类查询商品: categoryId={}, page={}, size={}", categoryId, currentPage, pageSize);
            
            if (categoryId == null || categoryId <= 0) {
                return PageResponse.empty();
            }
            
            Page<Goods> page = new Page<>(currentPage, pageSize);
            IPage<Goods> result = goodsService.getGoodsByCategory(page, categoryId, "active");
            
            return convertToPageResponse(result);
            
        } catch (Exception e) {
            log.error("根据分类查询商品失败: categoryId={}", categoryId, e);
            return PageResponse.empty();
        }
    }

    @Override
    public PageResponse<GoodsResponse> getGoodsBySeller(Long sellerId, Integer currentPage, Integer pageSize) {
        try {
            log.debug("REST根据商家查询商品: sellerId={}, page={}, size={}", sellerId, currentPage, pageSize);
            
            if (sellerId == null || sellerId <= 0) {
                return PageResponse.empty();
            }
            
            // 验证商家用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(sellerId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("商家用户不存在，无法查询商品: sellerId={}", sellerId);
                return PageResponse.empty();
            }
            
            Page<Goods> page = new Page<>(currentPage, pageSize);
            IPage<Goods> result = goodsService.getGoodsBySeller(page, sellerId, "active");
            
            log.debug("商家商品查询完成: 商家={}({}), 商品数量={}", 
                    sellerId, userResult.getData().getNickname(), result.getTotal());
            return convertToPageResponse(result);
            
        } catch (Exception e) {
            log.error("根据商家查询商品失败: sellerId={}", sellerId, e);
            return PageResponse.empty();
        }
    }

    @Override
    public Result<GoodsResponse> getGoodsByContentId(Long contentId) {
        try {
            log.debug("REST根据内容ID查询商品: contentId={}", contentId);
            
            if (contentId == null || contentId <= 0) {
                return Result.failure("内容ID不能为空");
            }
            
            // 查询内容类型的商品
            Goods goods = goodsService.getGoodsByContentId(contentId);
            if (goods == null) {
                return Result.failure("未找到对应的商品记录");
            }
            
            // 验证商品状态
            if (!"active".equals(goods.getStatus().getCode())) {
                return Result.failure("商品已下架或不可用");
            }
            
            // 转换为响应对象
            GoodsResponse response = convertToResponse(goods);
            
            log.debug("根据内容ID查询商品成功: contentId={}, goodsId={}, price={}", 
                contentId, goods.getId(), goods.getCoinPrice());
            
            return Result.success(response);
            
        } catch (Exception e) {
            log.error("根据内容ID查询商品失败: contentId={}", contentId, e);
            return Result.failure("查询失败: " + e.getMessage());
        }
    }

    @Override
    public PageResponse<GoodsResponse> getHotGoods(String goodsType, Integer currentPage, Integer pageSize) {
        try {
            log.debug("REST查询热门商品: type={}, page={}, size={}", goodsType, currentPage, pageSize);
            
            Page<Goods> page = new Page<>(currentPage, pageSize);
            IPage<Goods> result = goodsService.getHotGoods(page, goodsType);
            
            return convertToPageResponse(result);
            
        } catch (Exception e) {
            log.error("查询热门商品失败: type={}", goodsType, e);
            return PageResponse.empty();
        }
    }

    @Override
    public PageResponse<GoodsResponse> searchGoods(String keyword, Integer currentPage, Integer pageSize) {
        try {
            log.debug("REST搜索商品: keyword={}, page={}, size={}", keyword, currentPage, pageSize);
            
            Page<Goods> page = new Page<>(currentPage, pageSize);
            IPage<Goods> result = goodsService.searchGoods(page, keyword, "active");
            
            return convertToPageResponse(result);
            
        } catch (Exception e) {
            log.error("搜索商品失败: keyword={}", keyword, e);
            return PageResponse.empty();
        }
    }

    @Override
    public PageResponse<GoodsResponse> getGoodsByPriceRange(String goodsType, Object minPrice, Object maxPrice, 
                                                           Integer currentPage, Integer pageSize) {
        try {
            log.debug("REST按价格区间查询商品: type={}, min={}, max={}, page={}, size={}", 
                    goodsType, minPrice, maxPrice, currentPage, pageSize);
            
            Page<Goods> page = new Page<>(currentPage, pageSize);
            IPage<Goods> result = goodsService.getGoodsByPriceRange(page, minPrice, maxPrice, goodsType);
            
            return convertToPageResponse(result);
            
        } catch (Exception e) {
            log.error("按价格区间查询商品失败: type={}, min={}, max={}", goodsType, minPrice, maxPrice, e);
            return PageResponse.empty();
        }
    }

    // =================== 库存管理 ===================

    @Override
    public Result<Boolean> checkStock(Long goodsId, Integer quantity) {
        try {
            log.debug("REST检查库存: goodsId={}, quantity={}", goodsId, quantity);
            
            if (goodsId == null || quantity == null || quantity <= 0) {
                return Result.failure("参数无效");
            }
            
            boolean hasStock = goodsService.checkStock(goodsId, quantity);
            return Result.success(hasStock);
            
        } catch (Exception e) {
            log.error("检查库存失败: goodsId={}, quantity={}", goodsId, quantity, e);
            return Result.failure("检查库存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> reduceStock(Long goodsId, Integer quantity) {
        try {
            log.info("REST扣减库存: goodsId={}, quantity={}", goodsId, quantity);
            
            if (goodsId == null || quantity == null || quantity <= 0) {
                return Result.failure("参数无效");
            }
            
            boolean success = goodsService.reduceStock(goodsId, quantity);
            if (!success) {
                return Result.failure("库存不足或扣减失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("扣减库存失败: goodsId={}, quantity={}", goodsId, quantity, e);
            return Result.failure("扣减库存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchReduceStock(Map<Long, Integer> stockMap) {
        try {
            log.info("REST批量扣减库存: count={}", stockMap.size());
            
            if (CollectionUtils.isEmpty(stockMap)) {
                return Result.success();
            }
            
            boolean success = goodsService.batchReduceStock(stockMap);
            if (!success) {
                return Result.failure("批量扣减库存失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量扣减库存失败: count={}", stockMap.size(), e);
            return Result.failure("批量扣减库存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<GoodsResponse>> getLowStockGoods(Integer threshold) {
        try {
            log.debug("REST查询低库存商品: threshold={}", threshold);
            
            List<Goods> goodsList = goodsService.getLowStockGoods(threshold);
            List<GoodsResponse> responseList = goodsList.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            return Result.success(responseList);
            
        } catch (Exception e) {
            log.error("查询低库存商品失败: threshold={}", threshold, e);
            return Result.failure("查询低库存商品失败: " + e.getMessage());
        }
    }

    // =================== 统计操作 ===================

    @Override
    public Result<Void> increaseSales(Long goodsId, Long count) {
        try {
            log.debug("REST增加销量: goodsId={}, count={}", goodsId, count);
            
            if (goodsId == null || count == null || count <= 0) {
                return Result.failure("参数无效");
            }
            
            boolean success = goodsService.increaseSales(goodsId, count);
            if (!success) {
                return Result.failure("增加销量失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("增加销量失败: goodsId={}, count={}", goodsId, count, e);
            return Result.failure("增加销量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> increaseViews(Long goodsId, Long count) {
        try {
            log.debug("REST增加浏览量: goodsId={}, count={}", goodsId, count);
            
            if (goodsId == null || count == null || count <= 0) {
                return Result.failure("参数无效");
            }
            
            boolean success = goodsService.increaseViews(goodsId, count);
            if (!success) {
                return Result.failure("增加浏览量失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("增加浏览量失败: goodsId={}, count={}", goodsId, count, e);
            return Result.failure("增加浏览量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchIncreaseViews(Map<Long, Long> viewMap) {
        try {
            log.debug("REST批量增加浏览量: count={}", viewMap.size());
            
            if (CollectionUtils.isEmpty(viewMap)) {
                return Result.success();
            }
            
            boolean success = goodsService.batchIncreaseViews(viewMap);
            if (!success) {
                return Result.failure("批量增加浏览量失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量增加浏览量失败: count={}", viewMap.size(), e);
            return Result.failure("批量增加浏览量失败: " + e.getMessage());
        }
    }

    @Override
    public Result<List<Map<String, Object>>> getGoodsStatistics() {
        try {
            log.debug("REST获取商品统计信息");
            
            List<Map<String, Object>> statistics = goodsService.getGoodsStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取商品统计信息失败", e);
            return Result.failure("获取统计信息失败: " + e.getMessage());
        }
    }

    // =================== 状态管理 ===================

    @Override
    public Result<Void> publishGoods(Long goodsId) {
        try {
            log.info("REST上架商品: goodsId={}", goodsId);
            
            if (goodsId == null || goodsId <= 0) {
                return Result.failure("商品ID无效");
            }
            
            boolean success = goodsService.publishGoods(goodsId);
            if (!success) {
                return Result.failure("商品上架失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("商品上架失败: goodsId={}", goodsId, e);
            return Result.failure("商品上架失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> unpublishGoods(Long goodsId) {
        try {
            log.info("REST下架商品: goodsId={}", goodsId);
            
            if (goodsId == null || goodsId <= 0) {
                return Result.failure("商品ID无效");
            }
            
            boolean success = goodsService.unpublishGoods(goodsId);
            if (!success) {
                return Result.failure("商品下架失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("商品下架失败: goodsId={}", goodsId, e);
            return Result.failure("商品下架失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchPublishGoods(List<Long> goodsIds) {
        try {
            log.info("REST批量上架商品: count={}", goodsIds.size());
            
            if (CollectionUtils.isEmpty(goodsIds)) {
                return Result.success();
            }
            
            boolean success = goodsService.batchPublishGoods(goodsIds);
            if (!success) {
                return Result.failure("批量上架失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量上架商品失败: count={}", goodsIds.size(), e);
            return Result.failure("批量上架失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> batchUnpublishGoods(List<Long> goodsIds) {
        try {
            log.info("REST批量下架商品: count={}", goodsIds.size());
            
            if (CollectionUtils.isEmpty(goodsIds)) {
                return Result.success();
            }
            
            boolean success = goodsService.batchUnpublishGoods(goodsIds);
            if (!success) {
                return Result.failure("批量下架失败");
            }
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量下架商品失败: count={}", goodsIds.size(), e);
            return Result.failure("批量下架失败: " + e.getMessage());
        }
    }

    // =================== 业务验证 ===================

    @Override
    public Result<Map<String, Object>> validatePurchase(Long goodsId, Integer quantity) {
        try {
            log.debug("REST验证商品购买: goodsId={}, quantity={}", goodsId, quantity);
            
            if (goodsId == null || quantity == null || quantity <= 0) {
                return Result.failure("参数无效");
            }
            
            Map<String, Object> validation = goodsService.validatePurchase(goodsId, quantity);
            return Result.success(validation);
            
        } catch (Exception e) {
            log.error("验证商品购买失败: goodsId={}, quantity={}", goodsId, quantity, e);
            return Result.failure("验证购买失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> getGoodsPurchaseInfo(Long goodsId, Integer quantity) {
        try {
            log.debug("REST获取商品购买信息: goodsId={}, quantity={}", goodsId, quantity);
            
            // 先验证购买
            Map<String, Object> validation = goodsService.validatePurchase(goodsId, quantity);
            if (!(Boolean) validation.get("valid")) {
                return Result.failure((String) validation.get("message"));
            }
            
            Goods goods = (Goods) validation.get("goods");
            GoodsResponse goodsResponse = convertToResponse(goods);
            
            // 构建购买信息
            Map<String, Object> purchaseInfo = Map.of(
                    "goods", goodsResponse,
                    "quantity", quantity,
                    "totalPrice", calculateTotalPrice(goods, quantity),
                    "paymentMethod", goods.getGoodsType() == Goods.GoodsType.CONTENT ? "coin" : "cash"
            );
            
            return Result.success(purchaseInfo);
            
        } catch (Exception e) {
            log.error("获取商品购买信息失败: goodsId={}, quantity={}", goodsId, quantity, e);
            return Result.failure("获取购买信息失败: " + e.getMessage());
        }
    }

    // =================== 快捷查询 ===================

    @Override
    public PageResponse<GoodsResponse> getCoinPackages(Integer currentPage, Integer pageSize) {
        GoodsQueryRequest request = new GoodsQueryRequest()
                .setGoodsType("coin")
                .setStatus("active")
                .setCurrentPage(currentPage)
                .setPageSize(pageSize)
                .setSortField("price")
                .setSortDirection("ASC");
        return queryGoods(request);
    }

    @Override
    public PageResponse<GoodsResponse> getSubscriptionServices(Integer currentPage, Integer pageSize) {
        GoodsQueryRequest request = new GoodsQueryRequest()
                .setGoodsType("subscription")
                .setStatus("active")
                .setCurrentPage(currentPage)
                .setPageSize(pageSize)
                .setSortField("price")
                .setSortDirection("ASC");
        return queryGoods(request);
    }

    @Override
    public PageResponse<GoodsResponse> getContentGoods(Integer currentPage, Integer pageSize) {
        GoodsQueryRequest request = new GoodsQueryRequest()
                .setGoodsType("content")
                .setStatus("active")
                .setCurrentPage(currentPage)
                .setPageSize(pageSize)
                .setSortField("coin_price")
                .setSortDirection("ASC");
        return queryGoods(request);
    }

    @Override
    public PageResponse<GoodsResponse> getPhysicalGoods(Integer currentPage, Integer pageSize) {
        GoodsQueryRequest request = new GoodsQueryRequest()
                .setGoodsType("goods")
                .setStatus("active")
                .setCurrentPage(currentPage)
                .setPageSize(pageSize)
                .setSortField("sales_count")
                .setSortDirection("DESC");
        return queryGoods(request);
    }

    // =================== 私有方法 ===================

    /**
     * 转换请求为实体对象
     */
    private Goods convertToEntity(GoodsCreateRequest request) {
        Goods goods = new Goods();
        goods.setName(request.getName());
        goods.setDescription(request.getDescription());
        goods.setCategoryId(request.getCategoryId());
        goods.setCategoryName(request.getCategoryName());
        goods.setGoodsType(Goods.GoodsType.valueOf(request.getGoodsType().toUpperCase()));
        goods.setPrice(request.getPrice());
        goods.setOriginalPrice(request.getOriginalPrice());
        goods.setCoinPrice(request.getCoinPrice());
        goods.setCoinAmount(request.getCoinAmount());
        goods.setContentId(request.getContentId());
        goods.setContentTitle(request.getContentTitle());
        goods.setSubscriptionDuration(request.getSubscriptionDuration());
        goods.setSubscriptionType(request.getSubscriptionType());
        goods.setStock(request.getStock());
        goods.setCoverUrl(request.getCoverUrl());
        
        // 处理图片列表
        if (!CollectionUtils.isEmpty(request.getImages())) {
            goods.setImages(String.join(",", request.getImages()));
        }
        
        goods.setSellerId(request.getSellerId());
        goods.setSellerName(request.getSellerName());
        
        if (request.getStatus() != null) {
            goods.setStatus(Goods.GoodsStatus.valueOf(request.getStatus().toUpperCase()));
        }
        
        return goods;
    }

    /**
     * 转换实体对象为响应DTO
     */
    private GoodsResponse convertToResponse(Goods goods) {
        if (goods == null) {
            return null;
        }
        
        GoodsResponse response = new GoodsResponse();
        response.setId(goods.getId());
        response.setName(goods.getName());
        response.setDescription(goods.getDescription());
        response.setCategoryId(goods.getCategoryId());
        response.setCategoryName(goods.getCategoryName());
        response.setGoodsType(goods.getGoodsType().getCode());
        response.setPrice(goods.getPrice());
        response.setOriginalPrice(goods.getOriginalPrice());
        response.setCoinPrice(goods.getCoinPrice());
        response.setCoinAmount(goods.getCoinAmount());
        response.setContentId(goods.getContentId());
        response.setContentTitle(goods.getContentTitle());
        response.setSubscriptionDuration(goods.getSubscriptionDuration());
        response.setSubscriptionType(goods.getSubscriptionType());
        response.setStock(goods.getStock());
        response.setCoverUrl(goods.getCoverUrl());
        
        // 处理图片列表
        if (goods.getImages() != null && !goods.getImages().isEmpty()) {
            response.setImages(Arrays.asList(goods.getImages().split(",")));
        }
        
        response.setSellerId(goods.getSellerId());
        response.setSellerName(goods.getSellerName());
        response.setStatus(goods.getStatus().getCode());
        response.setSalesCount(goods.getSalesCount());
        response.setViewCount(goods.getViewCount());
        response.setCreateTime(goods.getCreateTime());
        response.setUpdateTime(goods.getUpdateTime());
        
        return response;
    }

    /**
     * 转换分页结果
     */
    private PageResponse<GoodsResponse> convertToPageResponse(IPage<Goods> page) {
        List<GoodsResponse> responseList = page.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                responseList,
                (int) page.getTotal(),
                (int) page.getSize(),
                (int) page.getCurrent()
        );
    }

    /**
     * 计算总价格
     */
    private Object calculateTotalPrice(Goods goods, Integer quantity) {
        if (goods.getGoodsType() == Goods.GoodsType.CONTENT) {
            return goods.getCoinPrice() * quantity;
        } else {
            return goods.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
        }
    }

    // =================== 内容同步相关方法实现 ===================

    @Override
    public Result<Void> createGoodsFromContent(Long contentId, String contentTitle, String contentDesc,
                                             Long categoryId, String categoryName, Long authorId,
                                             String authorNickname, String coverUrl, Long coinPrice,
                                             String contentStatus) {
        try {
            log.info("REST根据内容创建商品: contentId={}, title={}, authorId={}, coinPrice={}", 
                contentId, contentTitle, authorId, coinPrice);

            // 验证作者用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(authorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("作者用户不存在，无法创建内容商品: authorId={}", authorId);
                return Result.failure("AUTHOR_NOT_FOUND", "作者用户不存在");
            }

            // 检查是否已存在该内容的商品
            Goods existingGoods = goodsService.getGoodsByContentId(contentId);
            if (existingGoods != null) {
                log.warn("内容商品已存在: contentId={}, goodsId={}", contentId, existingGoods.getId());
                return Result.failure("该内容的商品已存在");
            }

            // 创建商品实体
            Goods goods = new Goods();
            goods.setName(contentTitle);
            goods.setDescription(contentDesc != null ? contentDesc : contentTitle);
            goods.setCategoryId(categoryId);
            goods.setCategoryName(categoryName);
            goods.setGoodsType(Goods.GoodsType.CONTENT);

            // 设置价格信息
            goods.setPrice(java.math.BigDecimal.ZERO); // 内容类商品现金价格为0
            goods.setCoinPrice(coinPrice != null ? coinPrice : 0L);

            // 设置内容相关信息
            goods.setContentId(contentId);

            // 设置商家信息（作者）- 使用验证后的真实用户信息
            goods.setSellerId(authorId);
            goods.setSellerName(userResult.getData().getNickname());

            // 设置图片和库存
            goods.setCoverUrl(coverUrl);
            goods.setStock(-1); // 虚拟商品无限库存

            // 设置状态
            String goodsStatus = convertContentStatusToGoodsStatus(contentStatus);
            goods.setStatus(Goods.GoodsStatus.valueOf(goodsStatus.toUpperCase()));

            // 创建商品
            goodsService.createGoods(goods);

            log.info("内容商品创建成功: contentId={}, goodsId={}", contentId, goods.getId());
            return Result.success();

        } catch (Exception e) {
            log.error("根据内容创建商品失败: contentId={}, title={}", contentId, contentTitle, e);
            return Result.failure("创建商品失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> syncContentToGoods(Long contentId, String contentTitle, String contentDesc,
                                         Long categoryId, String categoryName, Long authorId,
                                         String authorNickname, String coverUrl) {
        try {
            log.info("REST同步内容信息到商品: contentId={}, title={}, authorId={}", contentId, contentTitle, authorId);

            // 验证作者用户是否存在
            Result<UserResponse> userResult = userFacadeService.getUserById(authorId);
            if (userResult == null || !userResult.getSuccess()) {
                log.warn("作者用户不存在，无法同步内容到商品: authorId={}", authorId);
                return Result.failure("AUTHOR_NOT_FOUND", "作者用户不存在");
            }

            // 查找对应的商品
            Goods goods = goodsService.getGoodsByContentId(contentId);
            if (goods == null) {
                log.warn("未找到对应的商品: contentId={}", contentId);
                return Result.failure("未找到对应的商品记录");
            }

            // 更新商品信息 - 使用验证后的真实用户信息
            goods.setName(contentTitle);
            goods.setDescription(contentDesc != null ? contentDesc : contentTitle);
            goods.setCategoryId(categoryId);
            goods.setCategoryName(categoryName);
            goods.setSellerId(authorId);
            goods.setSellerName(userResult.getData().getNickname());
            goods.setCoverUrl(coverUrl);

            // 保存更新
            boolean success = goodsService.updateGoods(goods);
            if (success) {
                log.info("内容信息同步成功: contentId={}, goodsId={}, 作者={}({})", 
                        contentId, goods.getId(), authorId, userResult.getData().getNickname());
                return Result.success();
            } else {
                return Result.failure("同步失败");
            }

        } catch (Exception e) {
            log.error("同步内容信息到商品失败: contentId={}, authorId={}", contentId, authorId, e);
            return Result.failure("同步失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> syncContentStatusToGoods(Long contentId, String contentStatus) {
        try {
            log.info("REST同步内容状态到商品: contentId={}, status={}", contentId, contentStatus);

            // 查找对应的商品
            Goods goods = goodsService.getGoodsByContentId(contentId);
            if (goods == null) {
                log.warn("未找到对应的商品: contentId={}", contentId);
                return Result.failure("未找到对应的商品记录");
            }

            // 转换并设置状态
            String goodsStatus = convertContentStatusToGoodsStatus(contentStatus);
            goods.setStatus(Goods.GoodsStatus.valueOf(goodsStatus.toUpperCase()));

            // 保存更新
            boolean success = goodsService.updateGoods(goods);
            if (success) {
                log.info("内容状态同步成功: contentId={}, goodsId={}, status={}", 
                    contentId, goods.getId(), goodsStatus);
                return Result.success();
            } else {
                return Result.failure("状态同步失败");
            }

        } catch (Exception e) {
            log.error("同步内容状态到商品失败: contentId={}, status={}", contentId, contentStatus, e);
            return Result.failure("状态同步失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> syncContentPriceToGoods(Long contentId, Long coinPrice, Boolean isActive) {
        try {
            log.info("REST同步内容价格到商品: contentId={}, coinPrice={}, isActive={}", 
                contentId, coinPrice, isActive);

            // 查找对应的商品
            Goods goods = goodsService.getGoodsByContentId(contentId);
            if (goods == null) {
                log.warn("未找到对应的商品: contentId={}", contentId);
                return Result.failure("未找到对应的商品记录");
            }

            // 更新价格信息
            goods.setCoinPrice(coinPrice != null ? coinPrice : 0L);

            // 根据付费状态更新商品状态
            if (isActive != null) {
                if (isActive && goods.getStatus() == Goods.GoodsStatus.INACTIVE) {
                    goods.setStatus(Goods.GoodsStatus.ACTIVE);
                } else if (!isActive && goods.getStatus() == Goods.GoodsStatus.ACTIVE) {
                    goods.setStatus(Goods.GoodsStatus.INACTIVE);
                }
            }

            // 保存更新
            boolean success = goodsService.updateGoods(goods);
            if (success) {
                log.info("内容价格同步成功: contentId={}, goodsId={}, coinPrice={}", 
                    contentId, goods.getId(), coinPrice);
                return Result.success();
            } else {
                return Result.failure("价格同步失败");
            }

        } catch (Exception e) {
            log.error("同步内容价格到商品失败: contentId={}, coinPrice={}", contentId, coinPrice, e);
            return Result.failure("价格同步失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Map<String, Object>> batchSyncContentGoods(Integer batchSize) {
        try {
            log.info("REST开始批量同步内容商品: batchSize={}", batchSize);

            Map<String, Object> result = new HashMap<>();
            int processedCount = 0;
            int successCount = 0;
            int failureCount = 0;

            // 这里需要调用内容服务获取所有已发布的内容
            // 由于跨模块调用，这里先记录日志，实际实现可能需要通过消息队列或定时任务
            log.info("批量同步功能需要与内容服务集成，建议通过定时任务或消息队列实现");

            result.put("processedCount", processedCount);
            result.put("successCount", successCount);
            result.put("failureCount", failureCount);
            result.put("message", "批量同步功能需要进一步集成内容服务");

            return Result.success(result);

        } catch (Exception e) {
            log.error("批量同步内容商品失败: batchSize={}", batchSize, e);
            return Result.failure("批量同步失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Void> deleteGoodsByContentId(Long contentId) {
        try {
            log.info("REST删除内容对应的商品: contentId={}", contentId);

            // 查找对应的商品
            Goods goods = goodsService.getGoodsByContentId(contentId);
            if (goods == null) {
                log.warn("未找到对应的商品: contentId={}", contentId);
                return Result.success(); // 已经不存在，认为删除成功
            }

            // 软删除商品
            boolean success = goodsService.deleteGoods(goods.getId());
            if (success) {
                log.info("内容商品删除成功: contentId={}, goodsId={}", contentId, goods.getId());
                return Result.success();
            } else {
                return Result.failure("删除失败");
            }

        } catch (Exception e) {
            log.error("删除内容商品失败: contentId={}", contentId, e);
            return Result.failure("删除失败: " + e.getMessage());
        }
    }

    /**
     * 将内容状态转换为商品状态
     */
    private String convertContentStatusToGoodsStatus(String contentStatus) {
        if (contentStatus == null) {
            return "inactive";
        }
        
        switch (contentStatus.toUpperCase()) {
            case "PUBLISHED":
                return "active";
            case "OFFLINE":
            case "DRAFT":
            case "REVIEW":
            default:
                return "inactive";
        }
    }
}