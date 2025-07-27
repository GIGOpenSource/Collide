package com.gig.collide.goods.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.request.GoodsPageQueryRequest;
import com.gig.collide.api.goods.request.GoodsStockRequest;
import com.gig.collide.api.goods.request.GoodsUpdateRequest;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.goods.domain.convertor.GoodsConvertor;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.infrastructure.mapper.GoodsMapper;
import com.gig.collide.goods.infrastructure.service.GoodsCacheService;
import com.gig.collide.goods.infrastructure.exception.GoodsBusinessException;
import com.gig.collide.goods.infrastructure.exception.GoodsErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品领域服务
 * 
 * 提供商品相关的业务逻辑处理
 * 
 * @author Collide Team
 * @since 2024-01-01
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GoodsDomainService {
    
    private final GoodsMapper goodsMapper;
    private final GoodsCacheService goodsCacheService;
    
    /**
     * 根据ID获取商品
     * 
     * @param goodsId 商品ID
     * @return 商品实体
     */
    public Goods getGoodsById(Long goodsId) {
        log.info("获取商品详情，商品ID：{}", goodsId);
        
        if (goodsId == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.GOODS_ID_EMPTY);
        }
        
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            log.warn("商品不存在，商品ID：{}", goodsId);
            return null;
        }
        
        return goods;
    }
    
    /**
     * 获取商品详情（转换为VO）
     * 
     * @param goodsId 商品ID
     * @return 商品信息VO
     */
    public GoodsInfo getGoodsDetail(Long goodsId) {
        // 先尝试从缓存获取
        GoodsInfo cachedGoodsInfo = goodsCacheService.getGoodsDetail(goodsId);
        if (cachedGoodsInfo != null) {
            return cachedGoodsInfo;
        }
        
        // 缓存未命中，从数据库查询
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            return null;
        }
        
        GoodsInfo goodsInfo = GoodsConvertor.INSTANCE.mapToVo(goods);
        
        // 设置缓存
        goodsCacheService.setGoodsDetail(goodsInfo);
        
        return goodsInfo;
    }
    
    /**
     * 分页查询商品列表
     * 
     * @param pageNo 页码
     * @param pageSize 页大小
     * @param type 商品类型
     * @param keyword 关键词
     * @return 商品分页信息
     */
    public PageResponse<GoodsInfo> pageQueryGoods(Integer pageNo, Integer pageSize, 
                                                 String type, String keyword) {
        log.info("分页查询商品，页码：{}，页大小：{}，类型：{}，关键词：{}", 
            pageNo, pageSize, type, keyword);
        
        // 参数校验
        if (pageNo == null || pageNo <= 0) {
            pageNo = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 20;
        }
        if (pageSize > 100) {
            pageSize = 100; // 限制最大页大小
        }
        
        // 先尝试从缓存获取
        PageResponse<GoodsInfo> cachedResult = goodsCacheService.getGoodsList(pageNo, pageSize, type, keyword);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // 缓存未命中，从数据库查询
        // 构建分页对象
        Page<Goods> page = new Page<>(pageNo, pageSize);
        
        // 执行分页查询
        IPage<Goods> goodsPage = goodsMapper.pageQuery(page, type, "ON_SALE", keyword);
        
        // 转换结果
        List<GoodsInfo> records = goodsPage.getRecords().stream()
            .map(GoodsConvertor.INSTANCE::mapToVo)
            .collect(Collectors.toList());
        
        PageResponse<GoodsInfo> result = PageResponse.of(records, goodsPage.getTotal(), pageNo, pageSize);
        
        // 设置缓存
        goodsCacheService.setGoodsList(pageNo, pageSize, type, keyword, result);
        
        return result;
    }
    
    /**
     * 搜索商品
     * 
     * @param keyword 搜索关键词
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 商品分页信息
     */
    public PageResponse<GoodsInfo> searchGoods(String keyword, Integer pageNo, Integer pageSize) {
        log.info("搜索商品，关键词：{}，页码：{}，页大小：{}", keyword, pageNo, pageSize);
        
        if (!StringUtils.hasText(keyword)) {
            return PageResponse.empty();
        }
        
        // 使用数据库模糊查询，后续可优化为ES搜索
        return pageQueryGoods(pageNo, pageSize, null, keyword);
    }
    
    /**
     * 获取热门商品
     * 
     * @param limit 限制数量
     * @return 热门商品列表
     */
    public List<GoodsInfo> getHotGoods(Integer limit) {
        log.info("获取热门商品，限制数量：{}", limit);
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50; // 限制最大查询数量
        }
        
        // 先尝试从缓存获取
        List<GoodsInfo> cachedHotGoods = goodsCacheService.getHotGoods(limit);
        if (cachedHotGoods != null) {
            return cachedHotGoods;
        }
        
        // 缓存未命中，从数据库查询
        List<Goods> hotGoods = goodsMapper.selectHotGoods("ON_SALE", limit);
        
        List<GoodsInfo> result = hotGoods.stream()
            .map(GoodsConvertor.INSTANCE::mapToVo)
            .collect(Collectors.toList());
        
        // 设置缓存
        goodsCacheService.setHotGoods(limit, result);
        
        return result;
    }
    
    /**
     * 获取推荐商品（无用户上下文）
     * 
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    public List<GoodsInfo> getRecommendedGoods(Integer limit) {
        return getRecommendedGoods(null, limit);
    }
    
    /**
     * 获取推荐商品
     * 
     * @param userId 用户ID（可选，用于个性化推荐）
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    public List<GoodsInfo> getRecommendedGoods(Long userId, Integer limit) {
        log.info("获取推荐商品，用户ID：{}，限制数量：{}", userId, limit);
        
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        if (limit > 50) {
            limit = 50; // 限制最大查询数量
        }
        
        // 先尝试从缓存获取
        List<GoodsInfo> cachedRecommendedGoods = goodsCacheService.getRecommendedGoods(userId, limit);
        if (cachedRecommendedGoods != null) {
            return cachedRecommendedGoods;
        }
        
        // 缓存未命中，从数据库查询
        // 获取推荐商品（目前基于系统配置，后续可加入个性化推荐算法）
        List<Goods> recommendedGoods = goodsMapper.selectRecommendedGoods("ON_SALE", limit);
        
        List<GoodsInfo> result = recommendedGoods.stream()
            .map(GoodsConvertor.INSTANCE::mapToVo)
            .collect(Collectors.toList());
        
        // 设置缓存
        goodsCacheService.setRecommendedGoods(userId, limit, result);
        
        return result;
    }
    
    /**
     * 用户端分页查询商品
     * 
     * @param queryRequest 查询请求
     * @return 商品分页信息
     */
    public PageResponse<GoodsInfo> pageQueryGoodsForUser(GoodsPageQueryRequest queryRequest) {
        log.info("用户端分页查询商品：{}", queryRequest);
        
        if (queryRequest == null) {
            return PageResponse.empty();
        }
        
        // 用户端只能看到上架的商品
        return pageQueryGoods(queryRequest.getPageNo(), queryRequest.getPageSize(), 
                             queryRequest.getType(), queryRequest.getKeyword());
    }
    
    /**
     * 检查商品是否可购买
     * 
     * @param goodsId 商品ID
     * @param quantity 购买数量
     * @return 是否可购买
     */
    public boolean checkPurchasable(Long goodsId, Integer quantity) {
        log.info("检查商品可购买性，商品ID：{}，数量：{}", goodsId, quantity);
        
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            log.warn("商品不存在：{}", goodsId);
            return false;
        }
        
        // 检查商品状态
        if (!"ON_SALE".equals(goods.getStatus())) {
            log.warn("商品未上架：{}，状态：{}", goodsId, goods.getStatus());
            return false;
        }
        
        // 检查库存（对于订阅类商品可能不需要检查库存）
        if ("COIN".equals(goods.getType()) && goods.getStock() < quantity) {
            log.warn("商品库存不足：{}，库存：{}，需要：{}", goodsId, goods.getStock(), quantity);
            return false;
        }
        
        return true;
    }
    
    // ==================== 商品管理方法 ====================
    
    /**
     * 创建商品
     * 
     * @param createRequest 创建请求
     * @return 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createGoods(GoodsCreateRequest createRequest) {
        log.info("创建商品，请求：{}", createRequest);
        
        // 参数校验
        validateCreateRequest(createRequest);
        
        // 转换为实体
        Goods goods = GoodsConvertor.INSTANCE.mapFromCreateRequest(createRequest);
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());
        
        // 保存到数据库
        int inserted = goodsMapper.insert(goods);
        if (inserted <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.CREATE_GOODS_FAILED);
        }
        
        // 清除列表缓存（新商品可能影响列表展示）
        goodsCacheService.evictGoodsListCache();
        goodsCacheService.evictHotGoodsCache();
        goodsCacheService.evictRecommendedGoodsCache();
        
        log.info("商品创建成功，商品ID：{}", goods.getId());
        return goods.getId();
    }
    
    /**
     * 更新商品信息
     * 
     * @param updateRequest 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateGoods(GoodsUpdateRequest updateRequest) {
        log.info("更新商品，请求：{}", updateRequest);
        
        // 参数校验
        if (updateRequest == null || updateRequest.getGoodsId() == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "更新请求或商品ID不能为空");
        }
        
        // 检查商品是否存在
        Goods existingGoods = getGoodsById(updateRequest.getGoodsId());
        if (existingGoods == null) {
            throw GoodsBusinessException.goodsNotFound(updateRequest.getGoodsId());
        }
        
        // 构建更新对象
        Goods updateGoods = new Goods();
        updateGoods.setId(updateRequest.getGoodsId());
        if (StringUtils.hasText(updateRequest.getName())) {
            updateGoods.setName(updateRequest.getName());
        }
        if (StringUtils.hasText(updateRequest.getDescription())) {
            updateGoods.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getPrice() != null) {
            updateGoods.setPrice(updateRequest.getPrice());
        }
        if (StringUtils.hasText(updateRequest.getMainImage())) {
            updateGoods.setImageUrl(updateRequest.getMainImage());
        }
        if (updateRequest.getRecommended() != null) {
            updateGoods.setRecommended(updateRequest.getRecommended());
        }
        if (updateRequest.getHot() != null) {
            updateGoods.setHot(updateRequest.getHot());
        }
        updateGoods.setUpdateTime(LocalDateTime.now());
        
        // 执行更新
        int updated = goodsMapper.updateById(updateGoods);
        if (updated <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.UPDATE_GOODS_FAILED);
        }
        
        // 清除相关缓存
        goodsCacheService.evictGoodsAllCache(updateRequest.getGoodsId());
        
        log.info("商品更新成功，商品ID：{}", updateRequest.getGoodsId());
    }
    
    /**
     * 更新库存
     * 
     * @param stockRequest 库存请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(GoodsStockRequest stockRequest) {
        log.info("更新商品库存，请求：{}", stockRequest);
        
        // 参数校验
        if (stockRequest == null || stockRequest.getGoodsId() == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "库存更新请求或商品ID不能为空");
        }
        
        // 获取当前商品信息
        Goods goods = getGoodsById(stockRequest.getGoodsId());
        if (goods == null) {
            throw GoodsBusinessException.goodsNotFound(stockRequest.getGoodsId());
        }
        
        int result = 0;
        String operationType = stockRequest.getOperationType();
        
        if ("SET".equals(operationType)) {
            // 设置库存
            result = goodsMapper.updateStockWithVersion(
                stockRequest.getGoodsId(), 
                stockRequest.getStock(), 
                goods.getVersion()
            );
        } else if ("INCREASE".equals(operationType)) {
            // 增加库存
            result = goodsMapper.increaseStockWithVersion(
                stockRequest.getGoodsId(), 
                stockRequest.getStock(), 
                goods.getVersion()
            );
        } else if ("DECREASE".equals(operationType)) {
            // 减少库存
            if (stockRequest.getStock() > goods.getStock()) {
                throw GoodsBusinessException.stockNotEnough(stockRequest.getGoodsId(), stockRequest.getStock(), goods.getStock());
            }
            result = goodsMapper.decreaseStockWithVersion(
                stockRequest.getGoodsId(), 
                stockRequest.getStock(), 
                goods.getVersion()
            );
        } else {
            throw GoodsBusinessException.invalidOperationType(operationType);
        }
        
        if (result <= 0) {
            throw GoodsBusinessException.stockUpdateConflict(stockRequest.getGoodsId());
        }
        
        log.info("商品库存更新成功，商品ID：{}，操作：{}，数量：{}", 
            stockRequest.getGoodsId(), operationType, stockRequest.getStock());
    }
    
    /**
     * 商品上架
     * 
     * @param goodsId 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void putOnSale(Long goodsId) {
        log.info("商品上架，商品ID：{}", goodsId);
        
        if (goodsId == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.GOODS_ID_EMPTY);
        }
        
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            throw GoodsBusinessException.goodsNotFound(goodsId);
        }
        
        if ("ON_SALE".equals(goods.getStatus())) {
            log.warn("商品已经上架，商品ID：{}", goodsId);
            return;
        }
        
        // 更新状态为上架
        Goods updateGoods = new Goods();
        updateGoods.setId(goodsId);
        updateGoods.setStatus("ON_SALE");
        updateGoods.setUpdateTime(LocalDateTime.now());
        
        int updated = goodsMapper.updateById(updateGoods);
        if (updated <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.UPDATE_GOODS_FAILED, "商品上架失败");
        }
        
        // 清除相关缓存（上架商品会影响列表展示）
        goodsCacheService.evictGoodsAllCache(goodsId);
        
        log.info("商品上架成功，商品ID：{}", goodsId);
    }
    
    /**
     * 商品下架
     * 
     * @param goodsId 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void putOffSale(Long goodsId) {
        log.info("商品下架，商品ID：{}", goodsId);
        
        if (goodsId == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.GOODS_ID_EMPTY);
        }
        
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            throw GoodsBusinessException.goodsNotFound(goodsId);
        }
        
        if ("OFF_SALE".equals(goods.getStatus())) {
            log.warn("商品已经下架，商品ID：{}", goodsId);
            return;
        }
        
        // 更新状态为下架
        Goods updateGoods = new Goods();
        updateGoods.setId(goodsId);
        updateGoods.setStatus("OFF_SALE");
        updateGoods.setUpdateTime(LocalDateTime.now());
        
        int updated = goodsMapper.updateById(updateGoods);
        if (updated <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.UPDATE_GOODS_FAILED, "商品下架失败");
        }
        
        // 清除相关缓存（下架商品不再在列表中展示）
        goodsCacheService.evictGoodsAllCache(goodsId);
        
        log.info("商品下架成功，商品ID：{}", goodsId);
    }
    
    /**
     * 批量上架
     * 
     * @param goodsIds 商品ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchPutOnSale(List<Long> goodsIds) {
        log.info("批量商品上架，商品ID列表：{}", goodsIds);
        
        if (CollectionUtils.isEmpty(goodsIds)) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "商品ID列表不能为空");
        }
        
        int updated = goodsMapper.batchUpdateStatus(goodsIds, "ON_SALE");
        
        // 清除相关缓存
        goodsCacheService.evictGoodsListCache();
        goodsCacheService.evictHotGoodsCache();
        goodsCacheService.evictRecommendedGoodsCache();
        
        log.info("批量上架完成，更新数量：{}", updated);
    }
    
    /**
     * 批量下架
     * 
     * @param goodsIds 商品ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchPutOffSale(List<Long> goodsIds) {
        log.info("批量商品下架，商品ID列表：{}", goodsIds);
        
        if (CollectionUtils.isEmpty(goodsIds)) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "商品ID列表不能为空");
        }
        
        int updated = goodsMapper.batchUpdateStatus(goodsIds, "OFF_SALE");
        
        // 清除相关缓存
        goodsCacheService.evictGoodsListCache();
        goodsCacheService.evictHotGoodsCache();
        goodsCacheService.evictRecommendedGoodsCache();
        
        log.info("批量下架完成，更新数量：{}", updated);
    }
    
    /**
     * 删除商品（逻辑删除）
     * 
     * @param goodsId 商品ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoods(Long goodsId) {
        log.info("删除商品，商品ID：{}", goodsId);
        
        if (goodsId == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.GOODS_ID_EMPTY);
        }
        
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            throw GoodsBusinessException.goodsNotFound(goodsId);
        }
        
        // 执行逻辑删除
        int deleted = goodsMapper.deleteById(goodsId);
        if (deleted <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.DELETE_GOODS_FAILED);
        }
        
        // 清除相关缓存
        goodsCacheService.evictGoodsAllCache(goodsId);
        
        log.info("商品删除成功，商品ID：{}", goodsId);
    }
    
    /**
     * 增加销售数量
     * 
     * @param goodsId 商品ID
     * @param deltaSold 销售增加量
     */
    @Transactional(rollbackFor = Exception.class)
    public void increaseSoldCount(Long goodsId, Integer deltaSold) {
        log.info("增加商品销售数量，商品ID：{}，增加量：{}", goodsId, deltaSold);
        
        if (goodsId == null || deltaSold == null || deltaSold <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID);
        }
        
        int updated = goodsMapper.increaseSoldCount(goodsId, deltaSold);
        if (updated <= 0) {
            log.warn("增加销售数量失败，商品可能不存在，商品ID：{}", goodsId);
        }
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 校验创建请求参数
     */
    private void validateCreateRequest(GoodsCreateRequest request) {
        if (request == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "创建请求不能为空");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "商品名称不能为空");
        }
        if (!StringUtils.hasText(request.getType())) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "商品类型不能为空");
        }
        if (request.getPrice() == null || request.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "商品价格必须大于0");
        }
        if (request.getCreatorId() == null) {
            throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "创建者ID不能为空");
        }
        
        // 根据商品类型校验特定字段
        if ("COIN".equals(request.getType())) {
            if (request.getCoinAmount() == null || request.getCoinAmount() <= 0) {
                throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "金币类商品的金币数量必须大于0");
            }
        } else if ("SUBSCRIPTION".equals(request.getType())) {
            if (request.getSubscriptionDays() == null || request.getSubscriptionDays() <= 0) {
                throw GoodsBusinessException.of(GoodsErrorCode.PARAM_INVALID, "订阅类商品的订阅天数必须大于0");
            }
        }
    }
} 