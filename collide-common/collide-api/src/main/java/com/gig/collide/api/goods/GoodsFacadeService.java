package com.gig.collide.api.goods;

import com.gig.collide.api.goods.request.GoodsCreateRequest;
import com.gig.collide.api.goods.request.GoodsQueryRequest;
import com.gig.collide.api.goods.response.GoodsResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 商品模块对外服务接口
 * 提供统一的商品管理功能入口
 *
 * @author GIG Team
 * @version 2.0.0 (扩展版)
 * @since 2024-01-31
 */
public interface GoodsFacadeService {

    // =================== 基础CRUD操作 ===================

    /**
     * 创建商品
     *
     * @param request 商品创建请求
     * @return 创建结果（仅返回状态）
     */
    Result<Void> createGoods(GoodsCreateRequest request);

    /**
     * 根据ID获取商品详情
     *
     * @param id 商品ID
     * @return 商品信息
     */
    Result<GoodsResponse> getGoodsById(Long id);

    /**
     * 更新商品信息
     *
     * @param id      商品ID
     * @param request 更新请求
     * @return 更新后的商品信息
     */
    Result<GoodsResponse> updateGoods(Long id, GoodsCreateRequest request);

    /**
     * 删除商品（软删除）
     *
     * @param id 商品ID
     * @return 删除结果（仅返回状态）
     */
    Result<Void> deleteGoods(Long id);

    /**
     * 批量删除商品
     *
     * @param ids 商品ID列表
     * @return 删除结果（仅返回状态）
     */
    Result<Void> batchDeleteGoods(List<Long> ids);

    // =================== 查询操作 ===================

    /**
     * 分页查询商品
     *
     * @param request 查询请求
     * @return 分页结果
     */
    PageResponse<GoodsResponse> queryGoods(GoodsQueryRequest request);

    /**
     * 根据分类查询商品
     *
     * @param categoryId  分类ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getGoodsByCategory(Long categoryId, Integer currentPage, Integer pageSize);

    /**
     * 根据商家查询商品
     *
     * @param sellerId    商家ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getGoodsBySeller(Long sellerId, Integer currentPage, Integer pageSize);

    /**
     * 获取热门商品
     *
     * @param goodsType   商品类型（可为空）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getHotGoods(String goodsType, Integer currentPage, Integer pageSize);

    /**
     * 搜索商品
     *
     * @param keyword     搜索关键词
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> searchGoods(String keyword, Integer currentPage, Integer pageSize);

    /**
     * 按价格区间查询商品
     *
     * @param goodsType   商品类型
     * @param minPrice    最低价格
     * @param maxPrice    最高价格
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getGoodsByPriceRange(String goodsType, Object minPrice, Object maxPrice, 
                                                    Integer currentPage, Integer pageSize);

    // =================== 库存管理 ===================

    /**
     * 检查库存是否充足
     *
     * @param goodsId  商品ID
     * @param quantity 需要数量
     * @return 检查结果
     */
    Result<Boolean> checkStock(Long goodsId, Integer quantity);

    /**
     * 扣减库存
     *
     * @param goodsId  商品ID
     * @param quantity 扣减数量
     * @return 扣减结果（仅返回状态）
     */
    Result<Void> reduceStock(Long goodsId, Integer quantity);

    /**
     * 批量扣减库存
     *
     * @param stockMap 商品ID和扣减数量的映射
     * @return 扣减结果（仅返回状态）
     */
    Result<Void> batchReduceStock(Map<Long, Integer> stockMap);

    /**
     * 查询低库存商品
     *
     * @param threshold 库存阈值
     * @return 商品列表
     */
    Result<List<GoodsResponse>> getLowStockGoods(Integer threshold);

    // =================== 统计操作 ===================

    /**
     * 增加商品销量
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 操作结果（仅返回状态）
     */
    Result<Void> increaseSales(Long goodsId, Long count);

    /**
     * 增加商品浏览量
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 操作结果（仅返回状态）
     */
    Result<Void> increaseViews(Long goodsId, Long count);

    /**
     * 批量增加浏览量
     *
     * @param viewMap 商品ID和浏览量的映射
     * @return 操作结果（仅返回状态）
     */
    Result<Void> batchIncreaseViews(Map<Long, Long> viewMap);

    /**
     * 获取商品统计信息
     *
     * @return 统计结果
     */
    Result<List<Map<String, Object>>> getGoodsStatistics();

    // =================== 状态管理 ===================

    /**
     * 上架商品
     *
     * @param goodsId 商品ID
     * @return 操作结果（仅返回状态）
     */
    Result<Void> publishGoods(Long goodsId);

    /**
     * 下架商品
     *
     * @param goodsId 商品ID
     * @return 操作结果（仅返回状态）
     */
    Result<Void> unpublishGoods(Long goodsId);

    /**
     * 批量上架商品
     *
     * @param goodsIds 商品ID列表
     * @return 操作结果（仅返回状态）
     */
    Result<Void> batchPublishGoods(List<Long> goodsIds);

    /**
     * 批量下架商品
     *
     * @param goodsIds 商品ID列表
     * @return 操作结果（仅返回状态）
     */
    Result<Void> batchUnpublishGoods(List<Long> goodsIds);

    // =================== 业务验证 ===================

    /**
     * 验证商品是否可购买
     *
     * @param goodsId  商品ID
     * @param quantity 购买数量
     * @return 验证结果
     */
    Result<Map<String, Object>> validatePurchase(Long goodsId, Integer quantity);

    /**
     * 获取商品购买信息（用于订单创建）
     *
     * @param goodsId  商品ID
     * @param quantity 购买数量
     * @return 购买信息
     */
    Result<Map<String, Object>> getGoodsPurchaseInfo(Long goodsId, Integer quantity);

    // =================== 快捷查询 ===================

    /**
     * 获取金币充值包列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getCoinPackages(Integer currentPage, Integer pageSize);

    /**
     * 获取订阅服务列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getSubscriptionServices(Integer currentPage, Integer pageSize);

    /**
     * 获取付费内容列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getContentGoods(Integer currentPage, Integer pageSize);

    /**
     * 获取实体商品列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    PageResponse<GoodsResponse> getPhysicalGoods(Integer currentPage, Integer pageSize);
}