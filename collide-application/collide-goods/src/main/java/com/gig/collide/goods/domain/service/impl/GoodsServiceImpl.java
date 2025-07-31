package com.gig.collide.goods.domain.service.impl;

import com.alicp.jetcache.anno.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.service.GoodsService;
import com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant;
import com.gig.collide.goods.infrastructure.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 商品业务服务实现类 - 缓存增强版
 * 基于JetCache的高性能商品服务
 *
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-31
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    // =================== 基础CRUD操作 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_HOT_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE)
    public Long createGoods(Goods goods) {
        log.info("创建商品: name={}, type={}, sellerId={}", 
                goods.getName(), goods.getGoodsType(), goods.getSellerId());
        
        // 验证数据完整性
        Map<String, Object> validation = validateGoodsData(goods);
        if (!(Boolean) validation.get("valid")) {
            throw new IllegalArgumentException((String) validation.get("message"));
        }
        
        // 设置默认值
        setDefaultValues(goods);
        
        // 保存商品
        int result = goodsMapper.insert(goods);
        if (result > 0) {
            log.info("商品创建成功: id={}, name={}", goods.getId(), goods.getName());
            return goods.getId();
        } else {
            throw new RuntimeException("商品创建失败");
        }
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
            key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#id",
            expire = GoodsCacheConstant.DETAIL_EXPIRE, 
            timeUnit = TimeUnit.MINUTES)
    public Goods getGoodsById(Long id) {
        log.debug("查询商品详情: id={}", id);
        
        if (id == null || id <= 0) {
            return null;
        }
        
        Goods goods = goodsMapper.selectById(id);
        if (goods != null) {
            log.debug("商品查询成功: id={}, name={}, type={}", 
                    goods.getId(), goods.getName(), goods.getGoodsType());
        }
        
        return goods;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheUpdate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                 key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goods.id", 
                 value = "#goods")
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_HOT_CACHE)
    public boolean updateGoods(Goods goods) {
        log.info("更新商品: id={}, name={}", goods.getId(), goods.getName());
        
        if (goods.getId() == null || goods.getId() <= 0) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 验证商品是否存在
        Goods existingGoods = goodsMapper.selectById(goods.getId());
        if (existingGoods == null) {
            throw new IllegalArgumentException("商品不存在: id=" + goods.getId());
        }
        
        // 设置更新时间
        goods.setUpdateTime(LocalDateTime.now());
        
        int result = goodsMapper.updateById(goods);
        if (result > 0) {
            log.info("商品更新成功: id={}", goods.getId());
            return true;
        } else {
            log.warn("商品更新失败: id={}", goods.getId());
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#id")
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_HOT_CACHE)
    public boolean deleteGoods(Long id) {
        log.info("删除商品: id={}", id);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 软删除：更新状态为inactive
        LambdaUpdateWrapper<Goods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Goods::getId, id)
                    .set(Goods::getStatus, Goods.GoodsStatus.INACTIVE)
                    .set(Goods::getUpdateTime, LocalDateTime.now());
        
        int result = goodsMapper.update(null, updateWrapper);
        if (result > 0) {
            log.info("商品删除成功: id={}", id);
            return true;
        } else {
            log.warn("商品删除失败: id={}", id);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_HOT_CACHE)
    public boolean batchDeleteGoods(List<Long> ids) {
        log.info("批量删除商品: count={}", ids.size());
        
        if (CollectionUtils.isEmpty(ids)) {
            return true;
        }
        
        LambdaUpdateWrapper<Goods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Goods::getId, ids)
                    .set(Goods::getStatus, Goods.GoodsStatus.INACTIVE)
                    .set(Goods::getUpdateTime, LocalDateTime.now());
        
        int result = goodsMapper.update(null, updateWrapper);
        log.info("批量删除商品完成: 目标={}, 实际={}", ids.size(), result);
        return result > 0;
    }

    // =================== 查询操作 ===================

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_LIST_CACHE,
            key = "T(com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant).buildListKey(#goodsType, #status, #page.current, #page.size)",
            expire = GoodsCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Goods> queryGoods(Page<Goods> page, String goodsType, String status) {
        log.debug("分页查询商品: type={}, status={}, page={}, size={}", 
                goodsType, status, page.getCurrent(), page.getSize());
        
        return goodsMapper.selectByTypeAndStatus(page, goodsType, status);
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_CATEGORY_CACHE,
            key = "T(com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant).buildCategoryKey(#categoryId, #page.current, #page.size)",
            expire = GoodsCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Goods> getGoodsByCategory(Page<Goods> page, Long categoryId, String status) {
        log.debug("根据分类查询商品: categoryId={}, status={}, page={}, size={}", 
                categoryId, status, page.getCurrent(), page.getSize());
        
        return goodsMapper.selectByCategoryAndStatus(page, categoryId, status);
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_SELLER_CACHE,
            key = "T(com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant).buildSellerKey(#sellerId, #page.current, #page.size)",
            expire = GoodsCacheConstant.LIST_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Goods> getGoodsBySeller(Page<Goods> page, Long sellerId, String status) {
        log.debug("根据商家查询商品: sellerId={}, status={}, page={}, size={}", 
                sellerId, status, page.getCurrent(), page.getSize());
        
        return goodsMapper.selectBySellerAndStatus(page, sellerId, status);
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_HOT_CACHE,
            key = "T(com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant).buildHotKey(#goodsType, #page.current, #page.size)",
            expire = GoodsCacheConstant.HOT_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Goods> getHotGoods(Page<Goods> page, String goodsType) {
        log.debug("查询热门商品: type={}, page={}, size={}", 
                goodsType, page.getCurrent(), page.getSize());
        
        return goodsMapper.selectHotGoods(page, goodsType);
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_SEARCH_CACHE,
            key = "T(com.gig.collide.goods.infrastructure.cache.GoodsCacheConstant).buildSearchKey(#keyword, #page.current, #page.size)",
            expire = GoodsCacheConstant.SEARCH_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public IPage<Goods> searchGoods(Page<Goods> page, String keyword, String status) {
        log.debug("搜索商品: keyword={}, status={}, page={}, size={}", 
                keyword, status, page.getCurrent(), page.getSize());
        
        if (!StringUtils.hasText(keyword)) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        
        return goodsMapper.searchGoods(page, keyword.trim(), status);
    }

    @Override
    public IPage<Goods> getGoodsByPriceRange(Page<Goods> page, Object minPrice, Object maxPrice, String goodsType) {
        log.debug("按价格区间查询商品: min={}, max={}, type={}, page={}, size={}", 
                minPrice, maxPrice, goodsType, page.getCurrent(), page.getSize());
        
        return goodsMapper.selectByPriceRange(page, minPrice, maxPrice, goodsType);
    }

    // =================== 库存管理 ===================

    @Override
    public boolean checkStock(Long goodsId, Integer quantity) {
        log.debug("检查库存: goodsId={}, quantity={}", goodsId, quantity);
        
        Goods goods = getGoodsById(goodsId);
        if (goods == null) {
            return false;
        }
        
        return goods.hasStock(quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goodsId")
    public boolean reduceStock(Long goodsId, Integer quantity) {
        log.info("扣减库存: goodsId={}, quantity={}", goodsId, quantity);
        
        if (goodsId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("参数无效");
        }
        
        // 检查库存
        if (!checkStock(goodsId, quantity)) {
            log.warn("库存不足: goodsId={}, 需要={}", goodsId, quantity);
            return false;
        }
        
        int result = goodsMapper.reduceStock(goodsId, quantity);
        if (result > 0) {
            log.info("库存扣减成功: goodsId={}, quantity={}", goodsId, quantity);
            return true;
        } else {
            log.warn("库存扣减失败: goodsId={}, quantity={}", goodsId, quantity);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchReduceStock(Map<Long, Integer> stockMap) {
        log.info("批量扣减库存: count={}", stockMap.size());
        
        if (CollectionUtils.isEmpty(stockMap)) {
            return true;
        }
        
        // 检查所有商品库存
        for (Map.Entry<Long, Integer> entry : stockMap.entrySet()) {
            if (!checkStock(entry.getKey(), entry.getValue())) {
                log.warn("批量库存检查失败: goodsId={}, 需要={}", entry.getKey(), entry.getValue());
                return false;
            }
        }
        
        // 批量扣减
        boolean allSuccess = true;
        for (Map.Entry<Long, Integer> entry : stockMap.entrySet()) {
            if (!reduceStock(entry.getKey(), entry.getValue())) {
                allSuccess = false;
                break;
            }
        }
        
        log.info("批量库存扣减完成: success={}", allSuccess);
        return allSuccess;
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_LOW_STOCK_CACHE,
            expire = GoodsCacheConstant.LOW_STOCK_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Goods> getLowStockGoods(Integer threshold) {
        log.debug("查询低库存商品: threshold={}", threshold);
        
        if (threshold == null || threshold < 0) {
            threshold = 10; // 默认阈值
        }
        
        return goodsMapper.selectLowStockGoods(threshold);
    }

    // =================== 统计操作 ===================

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goodsId")
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_HOT_CACHE)
    public boolean increaseSales(Long goodsId, Long count) {
        log.debug("增加销量: goodsId={}, count={}", goodsId, count);
        
        if (goodsId == null || count == null || count <= 0) {
            return false;
        }
        
        int result = goodsMapper.increaseSalesCount(goodsId, count);
        return result > 0;
    }

    @Override
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goodsId")
    public boolean increaseViews(Long goodsId, Long count) {
        log.debug("增加浏览量: goodsId={}, count={}", goodsId, count);
        
        if (goodsId == null || count == null || count <= 0) {
            return false;
        }
        
        int result = goodsMapper.increaseViewCount(goodsId, count);
        return result > 0;
    }

    @Override
    public boolean batchIncreaseViews(Map<Long, Long> viewMap) {
        log.debug("批量增加浏览量: count={}", viewMap.size());
        
        if (CollectionUtils.isEmpty(viewMap)) {
            return true;
        }
        
        boolean allSuccess = true;
        for (Map.Entry<Long, Long> entry : viewMap.entrySet()) {
            if (!increaseViews(entry.getKey(), entry.getValue())) {
                allSuccess = false;
            }
        }
        
        return allSuccess;
    }

    @Override
    @Cached(name = GoodsCacheConstant.GOODS_STATISTICS_CACHE,
            expire = GoodsCacheConstant.STATISTICS_EXPIRE,
            timeUnit = TimeUnit.MINUTES)
    public List<Map<String, Object>> getGoodsStatistics() {
        log.debug("获取商品统计信息");
        return goodsMapper.countByTypeAndStatus();
    }

    // =================== 状态管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goodsId")
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    public boolean publishGoods(Long goodsId) {
        log.info("上架商品: goodsId={}", goodsId);
        return updateGoodsStatus(goodsId, Goods.GoodsStatus.ACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_DETAIL_CACHE, 
                     key = GoodsCacheConstant.GOODS_DETAIL_KEY + "#goodsId")
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    public boolean unpublishGoods(Long goodsId) {
        log.info("下架商品: goodsId={}", goodsId);
        return updateGoodsStatus(goodsId, Goods.GoodsStatus.INACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    public boolean batchPublishGoods(List<Long> goodsIds) {
        log.info("批量上架商品: count={}", goodsIds.size());
        return batchUpdateGoodsStatus(goodsIds, Goods.GoodsStatus.ACTIVE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheInvalidate(name = GoodsCacheConstant.GOODS_LIST_CACHE)
    public boolean batchUnpublishGoods(List<Long> goodsIds) {
        log.info("批量下架商品: count={}", goodsIds.size());
        return batchUpdateGoodsStatus(goodsIds, Goods.GoodsStatus.INACTIVE);
    }

    // =================== 业务验证 ===================

    @Override
    public Map<String, Object> validatePurchase(Long goodsId, Integer quantity) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取商品信息
            Goods goods = getGoodsById(goodsId);
            if (goods == null) {
                result.put("valid", false);
                result.put("message", "商品不存在");
                return result;
            }
            
            // 检查商品状态
            if (goods.getStatus() != Goods.GoodsStatus.ACTIVE) {
                result.put("valid", false);
                result.put("message", "商品已下架");
                return result;
            }
            
            // 检查库存
            if (!goods.hasStock(quantity)) {
                result.put("valid", false);
                result.put("message", "库存不足");
                return result;
            }
            
            result.put("valid", true);
            result.put("goods", goods);
            
        } catch (Exception e) {
            log.error("购买验证失败: goodsId={}, quantity={}", goodsId, quantity, e);
            result.put("valid", false);
            result.put("message", "验证失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> validateGoodsData(Goods goods) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 基础字段验证
            if (!StringUtils.hasText(goods.getName())) {
                result.put("valid", false);
                result.put("message", "商品名称不能为空");
                return result;
            }
            
            if (goods.getGoodsType() == null) {
                result.put("valid", false);
                result.put("message", "商品类型不能为空");
                return result;
            }
            
            if (goods.getSellerId() == null) {
                result.put("valid", false);
                result.put("message", "商家ID不能为空");
                return result;
            }
            
            // 根据商品类型验证特定字段
            switch (goods.getGoodsType()) {
                case COIN:
                    if (goods.getPrice() == null || goods.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        result.put("valid", false);
                        result.put("message", "金币商品价格必须大于0");
                        return result;
                    }
                    if (goods.getCoinAmount() == null || goods.getCoinAmount() <= 0) {
                        result.put("valid", false);
                        result.put("message", "金币数量必须大于0");
                        return result;
                    }
                    break;
                    
                case CONTENT:
                    if (goods.getCoinPrice() == null || goods.getCoinPrice() <= 0) {
                        result.put("valid", false);
                        result.put("message", "内容商品金币价格必须大于0");
                        return result;
                    }
                    if (goods.getContentId() == null) {
                        result.put("valid", false);
                        result.put("message", "内容ID不能为空");
                        return result;
                    }
                    break;
                    
                case SUBSCRIPTION:
                    if (goods.getPrice() == null || goods.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        result.put("valid", false);
                        result.put("message", "订阅商品价格必须大于0");
                        return result;
                    }
                    if (goods.getSubscriptionDuration() == null || goods.getSubscriptionDuration() <= 0) {
                        result.put("valid", false);
                        result.put("message", "订阅时长必须大于0");
                        return result;
                    }
                    break;
                    
                case GOODS:
                    if (goods.getPrice() == null || goods.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                        result.put("valid", false);
                        result.put("message", "实体商品价格必须大于0");
                        return result;
                    }
                    break;
            }
            
            result.put("valid", true);
            
        } catch (Exception e) {
            log.error("商品数据验证失败: {}", e.getMessage(), e);
            result.put("valid", false);
            result.put("message", "数据验证失败: " + e.getMessage());
        }
        
        return result;
    }

    // =================== 私有方法 ===================

    /**
     * 设置商品默认值
     */
    private void setDefaultValues(Goods goods) {
        if (goods.getStatus() == null) {
            goods.setStatus(Goods.GoodsStatus.ACTIVE);
        }
        
        if (goods.getSalesCount() == null) {
            goods.setSalesCount(0L);
        }
        
        if (goods.getViewCount() == null) {
            goods.setViewCount(0L);
        }
        
        // 虚拟商品设置无限库存
        if (goods.isVirtual() && goods.getStock() == null) {
            goods.setStock(-1);
        }
        
        // 非内容类商品设置金币价格为0
        if (goods.getGoodsType() != Goods.GoodsType.CONTENT && goods.getCoinPrice() == null) {
            goods.setCoinPrice(0L);
        }
        
        // 非金币类商品设置金币数量为null
        if (goods.getGoodsType() != Goods.GoodsType.COIN) {
            goods.setCoinAmount(null);
        }
    }

    /**
     * 更新商品状态
     */
    private boolean updateGoodsStatus(Long goodsId, Goods.GoodsStatus status) {
        LambdaUpdateWrapper<Goods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Goods::getId, goodsId)
                    .set(Goods::getStatus, status)
                    .set(Goods::getUpdateTime, LocalDateTime.now());
        
        return goodsMapper.update(null, updateWrapper) > 0;
    }

    /**
     * 批量更新商品状态
     */
    private boolean batchUpdateGoodsStatus(List<Long> goodsIds, Goods.GoodsStatus status) {
        if (CollectionUtils.isEmpty(goodsIds)) {
            return true;
        }
        
        LambdaUpdateWrapper<Goods> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Goods::getId, goodsIds)
                    .set(Goods::getStatus, status)
                    .set(Goods::getUpdateTime, LocalDateTime.now());
        
        return goodsMapper.update(null, updateWrapper) > 0;
    }
}