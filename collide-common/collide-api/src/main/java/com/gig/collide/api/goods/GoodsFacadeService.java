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
    Result<PageResponse<GoodsResponse>> queryGoods(GoodsQueryRequest request);

    /**
     * 根据分类查询商品
     *
     * @param categoryId  分类ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getGoodsByCategory(Long categoryId, Integer currentPage, Integer pageSize);

    /**
     * 根据商家查询商品
     *
     * @param sellerId    商家ID
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getGoodsBySeller(Long sellerId, Integer currentPage, Integer pageSize);

    /**
     * 根据内容ID获取商品信息
     * 用于内容购买流程中获取对应的商品信息
     *
     * @param contentId 内容ID
     * @param goodsType 商品类型
     * @return 商品信息
     */
    Result<GoodsResponse> getGoodsByContentId(Long contentId, String goodsType);

    /**
     * 获取热门商品
     *
     * @param goodsType   商品类型（可为空）
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getHotGoods(String goodsType, Integer currentPage, Integer pageSize);

    /**
     * 搜索商品
     *
     * @param keyword     搜索关键词
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> searchGoods(String keyword, Integer currentPage, Integer pageSize);

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
    Result<PageResponse<GoodsResponse>> getGoodsByPriceRange(String goodsType, Object minPrice, Object maxPrice, 
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
     * 对应Service方法：increaseSalesCount
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 操作结果（仅返回状态）
     */
    Result<Void> increaseSalesCount(Long goodsId, Long count);

    /**
     * 增加商品浏览量
     * 对应Service方法：increaseViewCount
     *
     * @param goodsId 商品ID
     * @param count   增加数量
     * @return 操作结果（仅返回状态）
     */
    Result<Void> increaseViewCount(Long goodsId, Long count);

    /**
     * 批量增加浏览量
     * 对应Service方法：batchIncreaseViewCount
     *
     * @param viewMap 商品ID和浏览量的映射
     * @return 操作结果（仅返回状态）
     */
    Result<Void> batchIncreaseViewCount(Map<Long, Long> viewMap);

    /**
     * 按类型和状态统计商品
     * 对应Service方法：countByTypeAndStatus
     *
     * @return 统计结果
     */
    Result<List<Map<String, Object>>> countByTypeAndStatus();

    /**
     * 获取商品统计信息
     * 业务层方法，提供更丰富的统计数据
     *
     * @return 统计结果
     */
    Result<List<Map<String, Object>>> getGoodsStatistics();

    /**
     * 根据分类统计商品数量
     * 对应Service方法：countByCategory
     *
     * @param categoryId 分类ID
     * @param status     商品状态（可为空）
     * @return 商品数量
     */
    Result<Long> countByCategory(Long categoryId, String status);

    /**
     * 根据商家统计商品数量
     * 对应Service方法：countBySeller
     *
     * @param sellerId 商家ID
     * @param status   商品状态（可为空）
     * @return 商品数量
     */
    Result<Long> countBySeller(Long sellerId, String status);

    /**
     * 复合条件查询商品
     * 对应Service方法：findWithConditions
     * 支持多种查询条件和排序方式的组合
     *
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
     * @param currentPage    当前页码
     * @param pageSize       页面大小
     * @return 商品列表
     */
    Result<PageResponse<GoodsResponse>> findWithConditions(Long categoryId, Long sellerId, String goodsType,
                                                          String nameKeyword, Object minPrice, Object maxPrice,
                                                          Object minCoinPrice, Object maxCoinPrice, Boolean hasStock,
                                                          String status, String orderBy, String orderDirection,
                                                          Integer currentPage, Integer pageSize);

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

    /**
     * 批量更新商品状态
     * 对应Service方法：batchUpdateStatus
     * 用于批量上架、下架等操作
     *
     * @param goodsIds 商品ID列表
     * @param status   新状态
     * @return 操作结果（返回影响行数）
     */
    Result<Integer> batchUpdateStatus(List<Long> goodsIds, String status);

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
    Result<PageResponse<GoodsResponse>> getCoinPackages(Integer currentPage, Integer pageSize);

    /**
     * 获取订阅服务列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getSubscriptionServices(Integer currentPage, Integer pageSize);

    /**
     * 获取付费内容列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getContentGoods(Integer currentPage, Integer pageSize);

    /**
     * 获取实体商品列表
     *
     * @param currentPage 当前页码
     * @param pageSize    页面大小
     * @return 分页结果
     */
    Result<PageResponse<GoodsResponse>> getPhysicalGoods(Integer currentPage, Integer pageSize);

    // =================== 内容同步相关方法 ===================

    /**
     * 根据内容信息自动创建商品
     * 当新内容发布时调用，自动为内容创建对应的商品记录
     *
     * @param contentId       内容ID
     * @param contentTitle    内容标题
     * @param contentDesc     内容描述
     * @param categoryId      分类ID
     * @param categoryName    分类名称
     * @param authorId        作者ID
     * @param authorNickname  作者昵称
     * @param coverUrl        封面图URL
     * @param coinPrice       金币价格
     * @param contentStatus   内容状态
     * @return 创建结果
     */
    Result<Void> createGoodsFromContent(Long contentId, String contentTitle, String contentDesc,
                                       Long categoryId, String categoryName, Long authorId, 
                                       String authorNickname, String coverUrl, Long coinPrice,
                                       String contentStatus);

    /**
     * 同步内容信息到商品
     * 当内容信息更新时调用，同步更新对应的商品信息
     *
     * @param contentId       内容ID
     * @param contentTitle    内容标题
     * @param contentDesc     内容描述
     * @param categoryId      分类ID
     * @param categoryName    分类名称
     * @param authorId        作者ID
     * @param authorNickname  作者昵称
     * @param coverUrl        封面图URL
     * @return 同步结果
     */
    Result<Void> syncContentToGoods(Long contentId, String contentTitle, String contentDesc,
                                   Long categoryId, String categoryName, Long authorId,
                                   String authorNickname, String coverUrl);

    /**
     * 同步内容状态到商品
     * 当内容状态变更时调用，同步更新商品状态
     *
     * @param contentId     内容ID
     * @param contentStatus 内容状态 (PUBLISHED, OFFLINE, DRAFT, etc.)
     * @return 同步结果
     */
    Result<Void> syncContentStatusToGoods(Long contentId, String contentStatus);

    /**
     * 同步内容价格到商品
     * 当内容付费配置变更时调用，同步更新商品价格
     *
     * @param contentId 内容ID
     * @param coinPrice 金币价格
     * @param isActive  是否启用付费
     * @return 同步结果
     */
    Result<Void> syncContentPriceToGoods(Long contentId, Long coinPrice, Boolean isActive);

    /**
     * 批量同步内容商品
     * 用于系统维护，批量检查和同步内容与商品的一致性
     *
     * @param batchSize 批处理大小
     * @return 同步结果（包含处理统计信息）
     */
    Result<Map<String, Object>> batchSyncContentGoods(Integer batchSize);

    /**
     * 删除内容对应的商品
     * 当内容被删除时调用，删除对应的商品记录
     *
     * @param contentId 内容ID
     * @return 删除结果
     */
    Result<Void> deleteGoodsByContentId(Long contentId);
}