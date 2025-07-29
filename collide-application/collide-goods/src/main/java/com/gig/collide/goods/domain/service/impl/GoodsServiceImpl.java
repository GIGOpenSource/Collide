package com.gig.collide.goods.domain.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;
import com.gig.collide.goods.domain.service.GoodsService;
import com.gig.collide.goods.infrastructure.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商品业务逻辑实现类 - 简洁版
 * 基于goods-simple.sql的业务逻辑，实现核心商品功能
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    private final GoodsMapper goodsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Goods createGoods(Goods goods) {
        log.info("创建商品: name={}, sellerId={}", goods.getName(), goods.getSellerId());

        // 验证请求参数
        String validationResult = validateGoodsRequest(goods);
        if (validationResult != null) {
            throw new IllegalArgumentException(validationResult);
        }

        // 设置默认值
        if (goods.getStatus() == null) {
            goods.setStatus("active");
        }
        if (goods.getSalesCount() == null) {
            goods.setSalesCount(0L);
        }
        if (goods.getViewCount() == null) {
            goods.setViewCount(0L);
        }
        
        goods.setCreateTime(LocalDateTime.now());
        goods.setUpdateTime(LocalDateTime.now());

        int result = goodsMapper.insert(goods);
        if (result > 0) {
            log.info("创建商品成功: id={}", goods.getId());
            return goods;
        } else {
            throw new RuntimeException("创建商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Goods updateGoods(Goods goods) {
        log.info("更新商品: id={}", goods.getId());

        if (goods.getId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        // 检查商品是否存在
        Goods existingGoods = goodsMapper.selectById(goods.getId());
        if (existingGoods == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        // 更新字段
        goods.setUpdateTime(LocalDateTime.now());
        
        int result = goodsMapper.updateById(goods);
        if (result > 0) {
            log.info("更新商品成功: id={}", goods.getId());
            return goodsMapper.selectById(goods.getId());
        } else {
            throw new RuntimeException("更新商品失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteGoods(Long goodsId, String deleteReason, Long operatorId) {
        log.info("删除商品: goodsId={}, reason={}", goodsId, deleteReason);

        if (goodsId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        // 检查是否可以删除
        if (!canDelete(goodsId)) {
            throw new IllegalStateException("商品不能删除，可能存在关联数据");
        }

        // 逻辑删除：更新状态为inactive
        Goods goods = new Goods();
        goods.setId(goodsId);
        goods.setStatus("inactive");
        goods.setUpdateTime(LocalDateTime.now());

        int result = goodsMapper.updateById(goods);
        boolean success = result > 0;
        
        if (success) {
            log.info("删除商品成功: goodsId={}", goodsId);
        } else {
            log.error("删除商品失败: goodsId={}", goodsId);
        }
        
        return success;
    }

    @Override
    public Goods getGoodsById(Long goodsId) {
        if (goodsId == null) {
            return null;
        }
        return goodsMapper.selectById(goodsId);
    }

    @Override
    public IPage<Goods> queryGoods(Integer pageNum, Integer pageSize, Long categoryId, Long sellerId,
                                  String nameKeyword, BigDecimal minPrice, BigDecimal maxPrice,
                                  Boolean hasStock, String status, String orderBy, String orderDirection) {
        log.info("分页查询商品: pageNum={}, pageSize={}, categoryId={}, sellerId={}", 
                pageNum, pageSize, categoryId, sellerId);

        // 构建分页对象
        Page<Goods> page = new Page<>(pageNum, pageSize);

        // 调用复合条件查询
        return goodsMapper.findWithConditions(page, categoryId, sellerId, nameKeyword,
                minPrice, maxPrice, hasStock, status, orderBy, orderDirection);
    }

    @Override
    public IPage<Goods> getGoodsByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.findByCategory(page, categoryId, "active");
    }

    @Override
    public IPage<Goods> getGoodsBySeller(Long sellerId, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.findBySeller(page, sellerId, "active");
    }

    @Override
    public IPage<Goods> searchGoods(String keyword, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.searchByName(page, keyword, "active");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStock(Long goodsId, Integer stockChange) {
        log.info("更新商品库存: goodsId={}, stockChange={}", goodsId, stockChange);

        if (goodsId == null || stockChange == null || stockChange == 0) {
            return false;
        }

        // 先获取当前商品信息
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            log.warn("商品不存在: goodsId={}", goodsId);
            return false;
        }

        // 检查库存是否充足（如果是减少库存）
        if (stockChange < 0 && goods.getStock() + stockChange < 0) {
            log.warn("库存不足: goodsId={}, currentStock={}, change={}", 
                    goodsId, goods.getStock(), stockChange);
            return false;
        }

        // 更新库存
        int result = goodsMapper.updateStock(goodsId, stockChange);
        
        if (result > 0) {
            // 如果库存变为0或负数，设置为售罄状态
            int newStock = goods.getStock() + stockChange;
            if (newStock <= 0 && "active".equals(goods.getStatus())) {
                goodsMapper.batchUpdateStatus(List.of(goodsId), "sold_out");
            }
            // 如果之前是售罄状态且现在有库存，恢复为活跃状态
            else if (newStock > 0 && "sold_out".equals(goods.getStatus())) {
                goodsMapper.batchUpdateStatus(List.of(goodsId), "active");
            }
            
            log.info("更新库存成功: goodsId={}, newStock={}", goodsId, newStock);
            return true;
        } else {
            log.error("更新库存失败: goodsId={}", goodsId);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSalesCount(Long goodsId, Integer salesChange) {
        log.info("更新商品销量: goodsId={}, salesChange={}", goodsId, salesChange);

        if (goodsId == null || salesChange == null || salesChange <= 0) {
            return false;
        }

        int result = goodsMapper.updateSalesCount(goodsId, salesChange);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新销量成功: goodsId={}", goodsId);
        } else {
            log.error("更新销量失败: goodsId={}", goodsId);
        }
        
        return success;
    }

    @Override
    public boolean increaseViewCount(Long goodsId) {
        if (goodsId == null) {
            return false;
        }

        int result = goodsMapper.increaseViewCount(goodsId);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(List<Long> goodsIds, String status) {
        log.info("批量更新商品状态: count={}, status={}", 
                goodsIds != null ? goodsIds.size() : 0, status);

        if (goodsIds == null || goodsIds.isEmpty() || !StringUtils.hasText(status)) {
            return 0;
        }

        // 验证状态值
        if (!"active".equals(status) && !"inactive".equals(status) && !"sold_out".equals(status)) {
            throw new IllegalArgumentException("无效的状态值: " + status);
        }

        int result = goodsMapper.batchUpdateStatus(goodsIds, status);
        log.info("批量更新状态完成: 更新{}个商品", result);
        
        return result;
    }

    @Override
    public Map<String, Object> getGoodsStatistics(Long goodsId) {
        if (goodsId == null) {
            return null;
        }
        return goodsMapper.getGoodsStatistics(goodsId);
    }

    @Override
    public IPage<Goods> getHotGoods(Long minSalesCount, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.findHotGoods(page, minSalesCount != null ? minSalesCount : 0L, "active");
    }

    @Override
    public IPage<Goods> getLatestGoods(Integer days, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.findLatestGoods(page, days != null ? days : 7, "active");
    }

    @Override
    public IPage<Goods> getLowStockGoods(Integer threshold, Integer pageNum, Integer pageSize) {
        Page<Goods> page = new Page<>(pageNum, pageSize);
        return goodsMapper.findLowStock(page, threshold != null ? threshold : 10);
    }

    @Override
    public Long countBySeller(Long sellerId, String status) {
        return goodsMapper.countBySeller(sellerId, status);
    }

    @Override
    public Long countByCategory(Long categoryId, String status) {
        return goodsMapper.countByCategory(categoryId, status);
    }

    @Override
    public String validateGoodsRequest(Goods goods) {
        if (goods == null) {
            return "商品对象不能为空";
        }

        if (!StringUtils.hasText(goods.getName())) {
            return "商品名称不能为空";
        }

        if (goods.getName().length() > 200) {
            return "商品名称长度不能超过200个字符";
        }

        if (goods.getPrice() == null) {
            return "商品价格不能为空";
        }

        if (goods.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return "商品价格必须大于0";
        }

        if (goods.getSellerId() == null) {
            return "商家ID不能为空";
        }

        if (goods.getStock() != null && goods.getStock() < 0) {
            return "库存数量不能小于0";
        }

        // 验证状态值
        if (StringUtils.hasText(goods.getStatus()) &&
            !"active".equals(goods.getStatus()) && 
            !"inactive".equals(goods.getStatus()) && 
            !"sold_out".equals(goods.getStatus())) {
            return "状态值只能是active、inactive或sold_out";
        }

        return null; // 验证通过
    }

    @Override
    public boolean canDelete(Long goodsId) {
        if (goodsId == null) {
            return false;
        }

        // 检查商品是否存在
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return false;
        }

        // 可以根据业务需求添加更多删除限制
        // 例如：检查是否有未完成的订单、是否有库存等
        
        return true; // 简化版：所有商品都可以删除
    }

    @Override
    public boolean checkStockAvailable(Long goodsId, Integer requiredStock) {
        if (goodsId == null || requiredStock == null || requiredStock <= 0) {
            return false;
        }

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null || !goods.isActive()) {
            return false;
        }

        return goods.getStock() != null && goods.getStock() >= requiredStock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateGoods(Long goodsId) {
        log.info("激活商品: goodsId={}", goodsId);
        return batchUpdateStatus(List.of(goodsId), "active") > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivateGoods(Long goodsId) {
        log.info("停用商品: goodsId={}", goodsId);
        return batchUpdateStatus(List.of(goodsId), "inactive") > 0;
    }
}