package com.gig.collide.api.comment.service;

import com.gig.collide.api.comment.request.*;
import com.gig.collide.api.comment.response.*;
import com.gig.collide.api.comment.response.data.*;
import com.gig.collide.api.comment.enums.*;
import com.gig.collide.base.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论管理门面服务接口
 * 提供管理员级别的评论管理功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface CommentManageFacadeService {

    // ===================== 评论审核管理 =====================

    /**
     * 审核评论
     *
     * @param commentId 评论ID
     * @param auditStatus 审核状态
     * @param auditReason 审核原因
     * @param auditorId 审核员ID
     * @return 审核响应
     */
    CommentUnifiedOperateResponse auditComment(Long commentId, AuditStatusEnum auditStatus, 
                                              String auditReason, Long auditorId);

    /**
     * 批量审核评论
     *
     * @param commentIds 评论ID列表
     * @param auditStatus 审核状态
     * @param auditReason 审核原因
     * @param auditorId 审核员ID
     * @return 批量审核响应
     */
    CommentUnifiedOperateResponse batchAuditComments(List<Long> commentIds, AuditStatusEnum auditStatus, 
                                                    String auditReason, Long auditorId);

    /**
     * 获取待审核评论列表
     *
     * @param queryRequest 查询请求
     * @return 待审核评论列表响应
     */
    PageResponse<CommentUnifiedInfo> getPendingAuditComments(CommentUnifiedQueryRequest queryRequest);

    /**
     * 获取审核历史
     *
     * @param commentId 评论ID（可选）
     * @param auditorId 审核员ID（可选）
     * @param queryRequest 查询请求
     * @return 审核历史响应
     */
    PageResponse<Object> getAuditHistory(Long commentId, Long auditorId, CommentUnifiedQueryRequest queryRequest);

    // ===================== 评论管理操作 =====================

    /**
     * 管理员创建评论（无需审核）
     *
     * @param createRequest 创建请求
     * @param operatorId 操作员ID
     * @return 创建响应
     */
    CommentUnifiedOperateResponse adminCreateComment(CommentUnifiedCreateRequest createRequest, Long operatorId);

    /**
     * 强制删除评论（物理删除）
     *
     * @param commentId 评论ID
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @return 删除响应
     */
    CommentUnifiedOperateResponse forceDeleteComment(Long commentId, Long operatorId, String reason);

    /**
     * 恢复已删除评论
     *
     * @param commentId 评论ID
     * @param operatorId 操作员ID
     * @param reason 恢复原因
     * @return 恢复响应
     */
    CommentUnifiedOperateResponse restoreDeletedComment(Long commentId, Long operatorId, String reason);

    /**
     * 设置评论置顶
     *
     * @param commentId 评论ID
     * @param isPinned 是否置顶
     * @param operatorId 操作员ID
     * @return 置顶响应
     */
    CommentUnifiedOperateResponse setCommentPinned(Long commentId, Boolean isPinned, Long operatorId);

    /**
     * 设置评论热门
     *
     * @param commentId 评论ID
     * @param isHot 是否热门
     * @param operatorId 操作员ID
     * @return 热门响应
     */
    CommentUnifiedOperateResponse setCommentHot(Long commentId, Boolean isHot, Long operatorId);

    /**
     * 设置评论精华
     *
     * @param commentId 评论ID
     * @param isEssence 是否精华
     * @param operatorId 操作员ID
     * @return 精华响应
     */
    CommentUnifiedOperateResponse setCommentEssence(Long commentId, Boolean isEssence, Long operatorId);

    /**
     * 设置评论质量分数
     *
     * @param commentId 评论ID
     * @param qualityScore 质量分数
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    CommentUnifiedOperateResponse setCommentQualityScore(Long commentId, Double qualityScore, Long operatorId);

    // ===================== 批量管理操作 =====================

    /**
     * 批量修改评论状态
     *
     * @param commentIds 评论ID列表
     * @param status 新状态
     * @param operatorId 操作员ID
     * @param reason 修改原因
     * @return 批量修改响应
     */
    CommentUnifiedOperateResponse batchModifyCommentStatus(List<Long> commentIds, CommentStatusEnum status, 
                                                          Long operatorId, String reason);

    /**
     * 批量删除评论
     *
     * @param commentIds 评论ID列表
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @return 批量删除响应
     */
    CommentUnifiedOperateResponse batchDeleteComments(List<Long> commentIds, Long operatorId, String reason);

    /**
     * 批量设置评论属性
     *
     * @param commentIds 评论ID列表
     * @param modifyRequest 修改请求
     * @return 批量设置响应
     */
    CommentUnifiedOperateResponse batchModifyComments(List<Long> commentIds, CommentUnifiedModifyRequest modifyRequest);

    // ===================== 举报处理 =====================

    /**
     * 处理评论举报
     *
     * @param reportId 举报ID
     * @param handleResult 处理结果
     * @param handlerId 处理人ID
     * @return 处理响应
     */
    CommentUnifiedOperateResponse handleCommentReport(Long reportId, String handleResult, Long handlerId);

    /**
     * 获取举报列表
     *
     * @param status 举报状态（可选）
     * @param reportType 举报类型（可选）
     * @param queryRequest 查询请求
     * @return 举报列表响应
     */
    PageResponse<Object> getCommentReports(String status, ReportTypeEnum reportType, CommentUnifiedQueryRequest queryRequest);

    /**
     * 批量处理举报
     *
     * @param reportIds 举报ID列表
     * @param handleResult 处理结果
     * @param handlerId 处理人ID
     * @return 批量处理响应
     */
    CommentUnifiedOperateResponse batchHandleReports(List<Long> reportIds, String handleResult, Long handlerId);

    // ===================== 统计分析 =====================

    /**
     * 获取评论管理概览统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 概览统计响应
     */
    CommentUnifiedQueryResponse<Object> getCommentManageOverview(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取评论排行榜
     *
     * @param rankType 排行类型（最多点赞、最多回复等）
     * @param timeRange 时间范围
     * @param topCount 排行数量
     * @return 排行榜响应
     */
    CommentUnifiedQueryResponse<List<CommentUnifiedInfo>> getCommentRanking(String rankType, String timeRange, Integer topCount);

    /**
     * 获取评论趋势分析
     *
     * @param targetId 目标对象ID（可选）
     * @param commentType 评论类型（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势分析响应
     */
    CommentUnifiedQueryResponse<Object> getCommentTrend(Long targetId, CommentTypeEnum commentType, 
                                                       LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户评论统计
     *
     * @param userId 用户ID
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 用户统计响应
     */
    CommentUnifiedQueryResponse<Object> getUserCommentStatistics(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    // ===================== 质量监控 =====================

    /**
     * 获取低质量评论列表
     *
     * @param maxQualityScore 最大质量分数
     * @param queryRequest 查询请求
     * @return 低质量评论列表响应
     */
    PageResponse<CommentUnifiedInfo> getLowQualityComments(Double maxQualityScore, CommentUnifiedQueryRequest queryRequest);

    /**
     * 获取违规评论列表
     *
     * @param violationType 违规类型
     * @param queryRequest 查询请求
     * @return 违规评论列表响应
     */
    PageResponse<CommentUnifiedInfo> getViolationComments(String violationType, CommentUnifiedQueryRequest queryRequest);

    /**
     * 评估评论质量
     *
     * @param commentId 评论ID
     * @param operatorId 操作员ID
     * @return 质量评估响应
     */
    CommentUnifiedOperateResponse assessCommentQuality(Long commentId, Long operatorId);

    /**
     * 批量评估评论质量
     *
     * @param commentIds 评论ID列表
     * @param operatorId 操作员ID
     * @return 批量评估响应
     */
    CommentUnifiedOperateResponse batchAssessCommentQuality(List<Long> commentIds, Long operatorId);

    // ===================== 敏感词管理 =====================

    /**
     * 添加敏感词
     *
     * @param word 敏感词
     * @param wordType 敏感词类型
     * @param severity 严重程度
     * @param action 处理动作
     * @param operatorId 操作员ID
     * @return 添加响应
     */
    CommentUnifiedOperateResponse addSensitiveWord(String word, String wordType, Integer severity, 
                                                  String action, Long operatorId);

    /**
     * 删除敏感词
     *
     * @param wordId 敏感词ID
     * @param operatorId 操作员ID
     * @return 删除响应
     */
    CommentUnifiedOperateResponse removeSensitiveWord(Long wordId, Long operatorId);

    /**
     * 获取敏感词列表
     *
     * @param wordType 敏感词类型（可选）
     * @param queryRequest 查询请求
     * @return 敏感词列表响应
     */
    PageResponse<Object> getSensitiveWords(String wordType, CommentUnifiedQueryRequest queryRequest);

    // ===================== 系统维护 =====================

    /**
     * 同步评论统计数据
     *
     * @param targetId 目标对象ID（可选）
     * @param commentType 评论类型（可选）
     * @return 同步响应
     */
    CommentUnifiedOperateResponse syncCommentStatistics(Long targetId, CommentTypeEnum commentType);

    /**
     * 重建评论索引
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 重建响应
     */
    CommentUnifiedOperateResponse rebuildCommentIndex(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 验证数据一致性
     *
     * @param targetId 目标对象ID（可选）
     * @return 验证响应
     */
    CommentUnifiedOperateResponse verifyDataConsistency(Long targetId);

    /**
     * 清理无效数据
     *
     * @param cleanType 清理类型（已删除、过期等）
     * @param beforeTime 清理时间点之前的数据
     * @param operatorId 操作员ID
     * @return 清理响应
     */
    CommentUnifiedOperateResponse cleanInvalidData(String cleanType, LocalDateTime beforeTime, Long operatorId);

    // ===================== 配置管理 =====================

    /**
     * 更新系统配置
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    CommentUnifiedOperateResponse updateSystemConfig(String configKey, String configValue, Long operatorId);

    /**
     * 获取系统配置
     *
     * @param configKey 配置键（可选）
     * @return 配置响应
     */
    CommentUnifiedQueryResponse<Object> getSystemConfig(String configKey);
} 