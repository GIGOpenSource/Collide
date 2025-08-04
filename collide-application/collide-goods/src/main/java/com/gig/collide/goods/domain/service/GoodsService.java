package com.gig.collide.goods.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gig.collide.goods.domain.entity.Goods;

import java.util.List;
import java.util.Map;

/**
 * 商品业务服务接口
 * 提供完整的商品管理功能
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
public interface GoodsService {

    // =================== 基础CRUD操作 ===================

    /**
     * 创建商品
     *
     * @param goods 商品信息
     * @return 商品ID
     */
    Long createGoods(Goods goods);

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Goods getGoodsById(Long id);

    /**
     * 更新商品信息
     *
     * @param goods 商品信息
     * @return 是否成功
     */
    boolean updateGoods(Goods goods);

    /**
     * 删除商品（软删除）
     *
     * @param id 商品ID
     * @return 是否成功
     */
    boolean deleteGoods(Long id);

    /**
     * 批量删除商品
     *
     * @param ids 商品ID列表
     * @return 是否成功
     */
    boolean batchDeleteGoods(List<Long> ids);

    // =================== 查询操作 ===================

    /**
     * 分页查询商品
     *
     * @param page      分页参数
     * @param goodsType 商品类型
     * @param status    商品状态
     * @return 分页结果
     */
    IPage<Goods> queryGoods(Page<Goods> page, String goodsType, String status);

    /**
     * 根据分类查询商品
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @param status     商品状态
     * @return 分页结果
     */
    IPage<Goods> getGoodsByCategory(Page<Goods> page, Long categoryId, String status);

    /**
     * 根据商家查询商品
     *
     * @param page     分页参数
     * @param sellerId 商家ID
     * @param status   商品状态
     * @return 分页结果
     */
    IPage<Goods> getGoodsBySeller(Page<Goods> page, Long sellerId, String status);

    /**
     * 根据内容ID获取商品信息
     * 用于内容购买流程中获取对应的商品记录
     *
     * @param contentId 内容ID
     * @param goodsType 商品类型
     * @return 商品信息
     */
    Goods getGoodsByContentId(Long contentId, String goodsType);

    /**
     * 获取热门商品
     *
     * @param page      分页参数
     * @param goodsType 商品类型（可为空）
     * @return 分页结果
     */
    IPage<Goods> getHotGoods(Page<Goods> page, String goodsType);

    /**
     * 搜索商品
     *
     * @param page    分页参数
     * @param keyword 搜索关键词
     * @param status  商品状态
     * @return 分页结果
     */
    IPage<Goods> searchGoods(Page<Goods> page, String keyword, String status);

    /**
     * 按价格区间查询商品
     *
     * @param page      分页参数
     * @param minPrice  最低价格
     * @param maxPrice  最高价格
     * @param goodsType 商品类型
     * @return 分页结果
     */
    IPage<Goods> getGoodsByPriceRange(Page<Goods> page, Object minPrice, Object maxPrice, String goodsType);

    // =================== 库存管理 ===================

    /**
     * 检查库存是否充足
     *
     * @param goodsId  商品ID
     * @param quantity 需要数量
     * @return 是否充足
     */
    boolean checkStock(Long goodsId, Integer quantity);

    /**
     * 扣减库存
     *
     * @param goodsId  商品ID
     * @param quantity 扣减数量
     * @return 是否成功
     */
    boolean reduceStock(Long goodsId, Integer quantity);

    /**
     * 批量扣减库存
     *
     * @param stockMap 商品ID和扣减数量的映射
     * @return 是否成功
     */
    boolean batchReduceStock(Map<Long, Integer> stockMap);

    /**
     * 查询低库存商品
     *
     * @param threshold 库存阈值
     * @return 商品列表
     */
    List<Goods> getLowStockGoods(Integer threshold);

    // =================== 统计操作 ===================

    /**
     * 增加商品销量
     * 对应Mapper方法：increaseSalesCount
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 是否成功
     */
    boolean increaseSalesCount(Long goodsId, Long count);

    /**
     * 增加商品浏览量
     * 对应Mapper方法：increaseViewCount
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 是否成功
     */
    boolean increaseViewCount(Long goodsId, Long count);

    /**
     * 批量增加浏览量
     * 基于increaseViewCount方法的批量版本
     *
     * @param viewMap 商品ID和浏览量的映射
     * @return 是否成功
     */
    boolean batchIncreaseViewCount(Map<Long, Long> viewMap);

    /**
     * 按类型和状态统计商品
     * 对应Mapper方法：countByTypeAndStatus
     *
     * @return 统计结果
     */
    List<Map<String, Object>> countByTypeAndStatus();

    /**
     * 获取商品统计信息
     * 业务层方法，提供更丰富的统计数据
     *
     * @return 统计结果
     */
    List<Map<String, Object>> getGoodsStatistics();

    /**
     * 根据分类统计商品数量
     * 对应Mapper方法：countByCategory
     *
     * @param categoryId 分类ID
     * @param status     商品状态（可为空）
     * @return 商品数量
     */
    long countByCategory(Long categoryId, String status);

    /**
     * 根据商家统计商品数量
     * 对应Mapper方法：countBySeller
     *
     * @param sellerId 商家ID
     * @param status   商品状态（可为空）
     * @return 商品数量
     */
    long countBySeller(Long sellerId, String status);

    /**
     * 复合条件查询商品
     * 对应Mapper方法：findWithConditions
     * 支持多种查询条件和排序方式的组合
     *
     * @param page           分页参数
     * @param categoryId     分类ID（可为空）
     * @param sellerId       商家ID（可为空）
     * @param goodsType      商品类型（可为空）
     * @param nameKeyword    名称关键词（可为空）
     * @param minPrice       最低现金价格（可为空）
     * @param maxPrice       最高现金价格（可为空）
     * @param minCoinPrice   最低金币价格（可为空）
     * @param maxCoinPrice   最高金币价格（可为空）
     * @param hasStock       是否有库存（可为空）
     * @param status         商品状态（可为空）
     * @param orderBy        排序字段（可为空）
     * @param orderDirection 排序方向（可为空）
     * @return 商品列表
     */
    IPage<Goods> findWithConditions(Page<Goods> page, Long categoryId, Long sellerId, String goodsType,
                                   String nameKeyword, Object minPrice, Object maxPrice,
                                   Object minCoinPrice, Object maxCoinPrice, Boolean hasStock,
                                   String status, String orderBy, String orderDirection);

    // =================== 状态管理 ===================

    /**
     * 上架商品
     *
     * @param goodsId 商品ID
     * @return 是否成功
     */
    boolean publishGoods(Long goodsId);

    /**
     * 下架商品
     *
     * @param goodsId 商品ID
     * @return 是否成功
     */
    boolean unpublishGoods(Long goodsId);

    /**
     * 批量上架商品
     *
     * @param goodsIds 商品ID列表
     * @return 是否成功
     */
    boolean batchPublishGoods(List<Long> goodsIds);

    /**
     * 批量下架商品
     *
     * @param goodsIds 商品ID列表
     * @return 是否成功
     */
    boolean batchUnpublishGoods(List<Long> goodsIds);

    /**
     * 批量更新商品状态
     * 对应Mapper方法：batchUpdateStatus
     * 用于批量上架、下架等操作
     *
     * @param goodsIds 商品ID列表
     * @param status   新状态
     * @return 影响行数
     */
    int batchUpdateStatus(List<Long> goodsIds, String status);

    // =================== 业务验证 ===================

    /**
     * 验证商品是否可购买
     *
     * @param goodsId  商品ID
     * @param quantity 购买数量
     * @return 验证结果和错误信息
     */
    Map<String, Object> validatePurchase(Long goodsId, Integer quantity);

    /**
     * 验证商品数据完整性
     *
     * @param goods 商品信息
     * @return 验证结果和错误信息
     */
    Map<String, Object> validateGoodsData(Goods goods);
}