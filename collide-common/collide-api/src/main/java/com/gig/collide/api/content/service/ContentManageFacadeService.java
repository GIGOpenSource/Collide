package com.gig.collide.api.content.service;

import com.gig.collide.api.content.request.*;
import com.gig.collide.api.content.response.*;
import com.gig.collide.api.content.response.data.*;
import com.gig.collide.api.content.enums.ReviewStatusEnum;
import com.gig.collide.base.response.PageResponse;

/**
 * 内容管理门面服务接口
 * 提供管理员级别的内容管理功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface ContentManageFacadeService {

    // ===================== 内容审核相关 =====================

    /**
     * 审核内容
     *
     * @param contentId 内容ID
     * @param reviewStatus 审核状态
     * @param reviewComment 审核意见
     * @param reviewerId 审核员ID
     * @return 审核响应
     */
    ContentUnifiedOperateResponse reviewContent(Long contentId, ReviewStatusEnum reviewStatus, 
                                               String reviewComment, Long reviewerId);

    /**
     * 批量审核内容
     *
     * @param contentIds 内容ID列表
     * @param reviewStatus 审核状态
     * @param reviewComment 审核意见
     * @param reviewerId 审核员ID
     * @return 批量审核响应
     */
    ContentUnifiedOperateResponse batchReviewContents(Long[] contentIds, ReviewStatusEnum reviewStatus,
                                                     String reviewComment, Long reviewerId);

    /**
     * 获取待审核内容列表
     *
     * @param queryRequest 查询请求
     * @return 待审核内容响应
     */
    PageResponse<ContentUnifiedInfo> getPendingReviewContents(ContentUnifiedQueryRequest queryRequest);

    /**
     * 获取审核历史
     *
     * @param contentId 内容ID
     * @return 审核历史响应
     */
    ContentUnifiedQueryResponse<Object> getReviewHistory(Long contentId);

    // ===================== 内容管理相关 =====================

    /**
     * 管理员创建内容
     *
     * @param createRequest 创建请求
     * @return 创建响应
     */
    ContentUnifiedOperateResponse createContentByAdmin(ContentUnifiedCreateRequest createRequest);

    /**
     * 强制删除内容（物理删除）
     *
     * @param contentId 内容ID
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @return 删除响应
     */
    ContentUnifiedOperateResponse forceDeleteContent(Long contentId, Long operatorId, String reason);

    /**
     * 恢复已删除内容
     *
     * @param contentId 内容ID
     * @param operatorId 操作员ID
     * @param reason 恢复原因
     * @return 恢复响应
     */
    ContentUnifiedOperateResponse restoreContent(Long contentId, Long operatorId, String reason);

    /**
     * 设置内容推荐状态
     *
     * @param contentId 内容ID
     * @param recommended 是否推荐
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    ContentUnifiedOperateResponse setContentRecommended(Long contentId, boolean recommended, Long operatorId);

    /**
     * 设置内容置顶状态
     *
     * @param contentId 内容ID
     * @param pinned 是否置顶
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    ContentUnifiedOperateResponse setContentPinned(Long contentId, boolean pinned, Long operatorId);

    /**
     * 设置内容权重分数
     *
     * @param contentId 内容ID
     * @param weightScore 权重分数
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    ContentUnifiedOperateResponse setContentWeight(Long contentId, Double weightScore, Long operatorId);

    /**
     * 批量设置内容状态
     *
     * @param modifyRequest 批量修改请求
     * @return 批量设置响应
     */
    ContentUnifiedOperateResponse batchModifyContents(ContentUnifiedModifyRequest modifyRequest);

    // ===================== 统计分析相关 =====================

    /**
     * 获取内容统计概览
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计概览响应
     */
    ContentUnifiedQueryResponse<Object> getContentStatisticsOverview(String startDate, String endDate);

    /**
     * 获取热门内容排行榜
     *
     * @param rankType 排行榜类型（view, like, share, favorite等）
     * @param timeRange 时间范围（day, week, month等）
     * @param limit 限制数量
     * @return 排行榜响应
     */
    ContentUnifiedQueryResponse<ContentUnifiedInfo> getContentRanking(String rankType, String timeRange, Integer limit);

    /**
     * 获取内容趋势分析
     *
     * @param contentType 内容类型
     * @param timeRange 时间范围
     * @return 趋势分析响应
     */
    ContentUnifiedQueryResponse<Object> getContentTrend(String contentType, String timeRange);

    /**
     * 获取作者内容统计
     *
     * @param authorId 作者ID
     * @param timeRange 时间范围
     * @return 作者统计响应
     */
    ContentUnifiedQueryResponse<Object> getAuthorContentStatistics(Long authorId, String timeRange);

    // ===================== 内容质量监控 =====================

    /**
     * 获取低质量内容列表
     *
     * @param queryRequest 查询请求
     * @return 低质量内容响应
     */
    PageResponse<ContentUnifiedInfo> getLowQualityContents(ContentUnifiedQueryRequest queryRequest);

    /**
     * 获取违规内容列表
     *
     * @param queryRequest 查询请求
     * @return 违规内容响应
     */
    PageResponse<ContentUnifiedInfo> getViolationContents(ContentUnifiedQueryRequest queryRequest);

    /**
     * 内容质量评估
     *
     * @param contentId 内容ID
     * @return 质量评估响应
     */
    ContentUnifiedQueryResponse<Object> assessContentQuality(Long contentId);

    /**
     * 批量内容质量检查
     *
     * @param contentIds 内容ID列表
     * @return 质量检查响应
     */
    ContentUnifiedQueryResponse<Object> batchAssessContentQuality(Long[] contentIds);

    // ===================== 内容分类管理 =====================

    /**
     * 批量移动内容到指定分类
     *
     * @param contentIds 内容ID列表
     * @param targetCategoryId 目标分类ID
     * @param operatorId 操作员ID
     * @return 移动响应
     */
    ContentUnifiedOperateResponse batchMoveContentsToCategory(Long[] contentIds, Long targetCategoryId, Long operatorId);

    /**
     * 批量添加标签
     *
     * @param contentIds 内容ID列表
     * @param tags 标签列表
     * @param operatorId 操作员ID
     * @return 添加标签响应
     */
    ContentUnifiedOperateResponse batchAddTags(Long[] contentIds, String[] tags, Long operatorId);

    /**
     * 批量移除标签
     *
     * @param contentIds 内容ID列表
     * @param tags 标签列表
     * @param operatorId 操作员ID
     * @return 移除标签响应
     */
    ContentUnifiedOperateResponse batchRemoveTags(Long[] contentIds, String[] tags, Long operatorId);

    // ===================== 系统维护相关 =====================

    /**
     * 同步内容统计数据
     *
     * @param contentId 内容ID（可选，为空则同步全部）
     * @return 同步响应
     */
    ContentUnifiedOperateResponse syncContentStatistics(Long contentId);

    /**
     * 重建内容索引
     *
     * @param contentType 内容类型（可选）
     * @return 重建索引响应
     */
    ContentUnifiedOperateResponse rebuildContentIndex(String contentType);

    /**
     * 数据一致性校验
     *
     * @return 校验响应
     */
    ContentUnifiedQueryResponse<Object> verifyDataConsistency();

    /**
     * 清理无效数据
     *
     * @param dryRun 是否为试运行
     * @return 清理响应
     */
    ContentUnifiedOperateResponse cleanInvalidData(boolean dryRun);
} 