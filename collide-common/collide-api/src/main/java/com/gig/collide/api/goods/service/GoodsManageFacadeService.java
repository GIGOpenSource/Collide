package com.gig.collide.api.goods.service;

import com.gig.collide.api.goods.request.*;
import com.gig.collide.api.goods.response.*;
import com.gig.collide.api.goods.response.data.GoodsInfo;
import com.gig.collide.api.goods.constant.GoodsStatus;
import com.gig.collide.api.goods.constant.GoodsType;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 商品管理门面服务接口
 * 提供商品管理业务功能（管理员权限）
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface GoodsManageFacadeService {

    // ===================== 商品审核管理 =====================

    /**
     * 审核商品（通过/拒绝）
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param approved 是否通过审核
     * @param auditComment 审核意见
     * @param auditorId 审核员ID
     * @return 更新响应
     */
    GoodsUpdateResponse auditGoods(Long goodsId, Integer version, Boolean approved, String auditComment, Long auditorId);

    /**
     * 批量审核商品
     * 
     * @param goodsIds 商品ID列表
     * @param approved 是否通过审核
     * @param auditComment 审核意见
     * @param auditorId 审核员ID
     * @return 批量操作响应
     */
    GoodsBatchOperationResponse batchAuditGoods(List<Long> goodsIds, Boolean approved, String auditComment, Long auditorId);

    // ===================== 商品状态管理 =====================

    /**
     * 强制更新商品状态
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param status 目标状态
     * @param reason 操作原因
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    GoodsUpdateResponse forceUpdateGoodsStatus(Long goodsId, Integer version, GoodsStatus status, String reason, Long operatorId);

    /**
     * 禁用商品
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param reason 禁用原因
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    GoodsUpdateResponse disableGoods(Long goodsId, Integer version, String reason, Long operatorId);

    /**
     * 启用商品
     * 
     * @param goodsId 商品ID
     * @param version 版本号
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    GoodsUpdateResponse enableGoods(Long goodsId, Integer version, Long operatorId);

    // ===================== 商品数据管理 =====================

    /**
     * 物理删除商品（永久删除）
     * 
     * @param goodsId 商品ID
     * @param operatorId 操作员ID
     * @param deleteReason 删除原因
     * @return 删除响应
     */
    GoodsDeleteResponse physicalDeleteGoods(Long goodsId, Long operatorId, String deleteReason);

    /**
     * 恢复已删除的商品
     * 
     * @param goodsId 商品ID
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    GoodsUpdateResponse restoreGoods(Long goodsId, Long operatorId);

    // ===================== 商品统计与报表 =====================

    /**
     * 获取全平台商品统计
     * 
     * @param statisticsRequest 统计请求
     * @return 统计响应
     */
    GoodsStatisticsResponse getPlatformGoodsStatistics(GoodsStatisticsRequest statisticsRequest);

    /**
     * 获取按创建者分组的商品统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计响应
     */
    GoodsStatisticsResponse getGoodsStatisticsByCreator(String startDate, String endDate);

    /**
     * 获取商品销售趋势报告
     * 
     * @param type 商品类型（可选）
     * @param days 统计天数
     * @return 统计响应
     */
    GoodsStatisticsResponse getGoodsSalesTrend(GoodsType type, Integer days);

    // ===================== 商品查询（管理视角） =====================

    /**
     * 查询所有状态的商品（包括已删除）
     * 
     * @param goodsQueryRequest 查询请求
     * @return 查询响应
     */
    GoodsQueryResponse<GoodsInfo> queryAllStatusGoods(GoodsQueryRequest goodsQueryRequest);

    /**
     * 分页查询待审核商品
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页响应
     */
    PageResponse<GoodsInfo> queryPendingAuditGoods(Integer pageNum, Integer pageSize);

    /**
     * 查询异常商品（状态不一致、数据异常等）
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页响应
     */
    PageResponse<GoodsInfo> queryAbnormalGoods(Integer pageNum, Integer pageSize);

    // ===================== 数据维护功能 =====================

    /**
     * 同步商品库存（修复库存不一致问题）
     * 
     * @param goodsId 商品ID（为空则同步所有商品）
     * @return 批量操作响应
     */
    GoodsBatchOperationResponse syncGoodsStock(Long goodsId);

    /**
     * 重建商品索引
     * 
     * @param goodsIds 商品ID列表（为空则重建所有商品索引）
     * @return 批量操作响应
     */
    GoodsBatchOperationResponse rebuildGoodsIndex(List<Long> goodsIds);

    /**
     * 清理无效商品数据
     * 
     * @param dryRun 是否为试运行（不实际删除）
     * @return 批量操作响应
     */
    GoodsBatchOperationResponse cleanupInvalidGoods(Boolean dryRun);
} 