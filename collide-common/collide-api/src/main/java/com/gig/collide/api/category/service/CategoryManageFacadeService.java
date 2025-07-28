package com.gig.collide.api.category.service;

import com.gig.collide.api.category.request.*;
import com.gig.collide.api.category.response.*;
import com.gig.collide.api.category.response.data.*;
import com.gig.collide.api.category.enums.CategoryStatusEnum;
import com.gig.collide.base.response.PageResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 分类管理门面服务接口
 * 提供管理员级别的分类管理功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface CategoryManageFacadeService {

    // ===================== 分类管理操作 =====================

    /**
     * 管理员创建分类（无需额外审核）
     *
     * @param createRequest 创建请求
     * @param operatorId 操作员ID
     * @return 创建响应
     */
    CategoryUnifiedOperateResponse adminCreateCategory(CategoryUnifiedCreateRequest createRequest, Long operatorId);

    /**
     * 强制删除分类（物理删除）
     *
     * @param categoryId 分类ID
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @param force 是否强制删除（忽略子分类和内容检查）
     * @return 删除响应
     */
    CategoryUnifiedOperateResponse forceDeleteCategory(Long categoryId, Long operatorId, String reason, Boolean force);

    /**
     * 恢复已删除分类
     *
     * @param categoryId 分类ID
     * @param operatorId 操作员ID
     * @param reason 恢复原因
     * @return 恢复响应
     */
    CategoryUnifiedOperateResponse restoreDeletedCategory(Long categoryId, Long operatorId, String reason);

    /**
     * 批量修改分类状态
     *
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @param operatorId 操作员ID
     * @param reason 修改原因
     * @return 批量修改响应
     */
    CategoryUnifiedOperateResponse batchModifyCategoryStatus(List<Long> categoryIds, CategoryStatusEnum status, 
                                                           Long operatorId, String reason);

    /**
     * 批量删除分类
     *
     * @param categoryIds 分类ID列表
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @param force 是否强制删除
     * @return 批量删除响应
     */
    CategoryUnifiedOperateResponse batchDeleteCategories(List<Long> categoryIds, Long operatorId, 
                                                        String reason, Boolean force);

    /**
     * 批量移动分类
     *
     * @param categoryIds 分类ID列表
     * @param newParentId 新父分类ID
     * @param operatorId 操作员ID
     * @param reason 移动原因
     * @return 批量移动响应
     */
    CategoryUnifiedOperateResponse batchMoveCategories(List<Long> categoryIds, Long newParentId, 
                                                      Long operatorId, String reason);

    /**
     * 重构分类层级（重新计算level和path）
     *
     * @param rootId 根分类ID（可选，为null时重构所有分类）
     * @param operatorId 操作员ID
     * @return 重构响应
     */
    CategoryUnifiedOperateResponse rebuildCategoryHierarchy(Long rootId, Long operatorId);

    // ===================== 统计分析 =====================

    /**
     * 获取分类管理概览统计
     *
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 概览统计响应
     */
    CategoryUnifiedQueryResponse<Object> getCategoryManageOverview(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取分类使用排行榜
     *
     * @param rankType 排行类型（内容数量、活跃度等）
     * @param timeRange 时间范围
     * @param topCount 排行数量
     * @return 排行榜响应
     */
    CategoryUnifiedQueryResponse<List<CategoryUnifiedInfo>> getCategoryRanking(String rankType, String timeRange, Integer topCount);

    /**
     * 获取分类增长趋势分析
     *
     * @param level 分类层级（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势分析响应
     */
    CategoryUnifiedQueryResponse<Object> getCategoryGrowthTrend(Integer level, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取分类使用情况分析
     *
     * @param categoryId 分类ID（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 使用情况分析响应
     */
    CategoryUnifiedQueryResponse<Object> getCategoryUsageAnalysis(Long categoryId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取分类健康度评估
     *
     * @param categoryId 分类ID（可选，为null时评估所有分类）
     * @return 健康度评估响应
     */
    CategoryUnifiedQueryResponse<Object> getCategoryHealthAssessment(Long categoryId);

    // ===================== 质量监控 =====================

    /**
     * 获取未使用分类列表
     *
     * @param daysSinceLastUsed 多少天未使用
     * @param queryRequest 查询请求
     * @return 未使用分类列表响应
     */
    PageResponse<CategoryUnifiedInfo> getUnusedCategories(Integer daysSinceLastUsed, CategoryUnifiedQueryRequest queryRequest);

    /**
     * 获取重复名称分类列表
     *
     * @param queryRequest 查询请求
     * @return 重复名称分类列表响应
     */
    PageResponse<Object> getDuplicateNameCategories(CategoryUnifiedQueryRequest queryRequest);

    /**
     * 获取异常分类列表（层级错误、路径错误等）
     *
     * @param errorType 错误类型
     * @param queryRequest 查询请求
     * @return 异常分类列表响应
     */
    PageResponse<Object> getAbnormalCategories(String errorType, CategoryUnifiedQueryRequest queryRequest);

    /**
     * 检查分类数据一致性
     *
     * @param categoryId 分类ID（可选）
     * @param operatorId 操作员ID
     * @return 一致性检查响应
     */
    CategoryUnifiedOperateResponse checkCategoryDataConsistency(Long categoryId, Long operatorId);

    /**
     * 修复分类数据异常
     *
     * @param categoryId 分类ID（可选）
     * @param repairType 修复类型
     * @param operatorId 操作员ID
     * @return 修复响应
     */
    CategoryUnifiedOperateResponse repairCategoryDataAbnormal(Long categoryId, String repairType, Long operatorId);

    // ===================== 导入导出 =====================

    /**
     * 导出分类数据
     *
     * @param queryRequest 查询条件
     * @param exportFormat 导出格式（JSON, EXCEL, CSV等）
     * @param operatorId 操作员ID
     * @return 导出响应
     */
    CategoryUnifiedOperateResponse exportCategoryData(CategoryUnifiedQueryRequest queryRequest, 
                                                     String exportFormat, Long operatorId);

    /**
     * 导入分类数据
     *
     * @param importData 导入数据
     * @param importFormat 导入格式
     * @param operatorId 操作员ID
     * @param conflictStrategy 冲突处理策略（SKIP, OVERWRITE, MERGE）
     * @return 导入响应
     */
    CategoryUnifiedOperateResponse importCategoryData(Object importData, String importFormat, 
                                                     Long operatorId, String conflictStrategy);

    /**
     * 获取导入导出历史
     *
     * @param operationType 操作类型（IMPORT, EXPORT）
     * @param queryRequest 查询请求
     * @return 历史记录响应
     */
    PageResponse<Object> getImportExportHistory(String operationType, CategoryUnifiedQueryRequest queryRequest);

    // ===================== 系统维护 =====================

    /**
     * 同步分类统计数据
     *
     * @param categoryId 分类ID（可选）
     * @param operatorId 操作员ID
     * @return 同步响应
     */
    CategoryUnifiedOperateResponse syncCategoryStatistics(Long categoryId, Long operatorId);

    /**
     * 重建分类索引
     *
     * @param categoryId 分类ID（可选）
     * @param operatorId 操作员ID
     * @return 重建响应
     */
    CategoryUnifiedOperateResponse rebuildCategoryIndex(Long categoryId, Long operatorId);

    /**
     * 清理无效分类数据
     *
     * @param cleanType 清理类型（DELETED, ORPHANED, DUPLICATE等）
     * @param beforeTime 清理时间点之前的数据
     * @param operatorId 操作员ID
     * @return 清理响应
     */
    CategoryUnifiedOperateResponse cleanInvalidCategoryData(String cleanType, LocalDateTime beforeTime, Long operatorId);

    /**
     * 压缩分类存储空间
     *
     * @param operatorId 操作员ID
     * @return 压缩响应
     */
    CategoryUnifiedOperateResponse compressCategoryStorage(Long operatorId);

    // ===================== 权限管理 =====================

    /**
     * 设置分类管理权限
     *
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @param permissions 权限列表
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    CategoryUnifiedOperateResponse setCategoryPermissions(Long categoryId, Long userId, List<String> permissions, Long operatorId);

    /**
     * 获取分类权限列表
     *
     * @param categoryId 分类ID
     * @param userId 用户ID（可选）
     * @return 权限列表响应
     */
    CategoryUnifiedQueryResponse<Object> getCategoryPermissions(Long categoryId, Long userId);

    /**
     * 批量设置分类权限
     *
     * @param categoryIds 分类ID列表
     * @param permissions 权限配置
     * @param operatorId 操作员ID
     * @return 批量设置响应
     */
    CategoryUnifiedOperateResponse batchSetCategoryPermissions(List<Long> categoryIds, Map<String, Object> permissions, Long operatorId);

    // ===================== 审计日志 =====================

    /**
     * 获取分类操作日志
     *
     * @param categoryId 分类ID（可选）
     * @param operatorId 操作员ID（可选）
     * @param operationType 操作类型（可选）
     * @param queryRequest 查询请求
     * @return 操作日志响应
     */
    PageResponse<Object> getCategoryOperationLogs(Long categoryId, Long operatorId, String operationType, 
                                                  CategoryUnifiedQueryRequest queryRequest);

    /**
     * 获取分类变更历史
     *
     * @param categoryId 分类ID
     * @param queryRequest 查询请求
     * @return 变更历史响应
     */
    PageResponse<Object> getCategoryChangeHistory(Long categoryId, CategoryUnifiedQueryRequest queryRequest);

    /**
     * 导出操作日志
     *
     * @param queryRequest 查询条件
     * @param exportFormat 导出格式
     * @param operatorId 操作员ID
     * @return 导出响应
     */
    CategoryUnifiedOperateResponse exportOperationLogs(CategoryUnifiedQueryRequest queryRequest, String exportFormat, Long operatorId);

    // ===================== 配置管理 =====================

    /**
     * 获取分类系统配置
     *
     * @param configType 配置类型（可选）
     * @return 配置响应
     */
    CategoryUnifiedQueryResponse<Object> getCategorySystemConfig(String configType);

    /**
     * 更新分类系统配置
     *
     * @param configType 配置类型
     * @param configData 配置数据
     * @param operatorId 操作员ID
     * @return 更新响应
     */
    CategoryUnifiedOperateResponse updateCategorySystemConfig(String configType, Object configData, Long operatorId);

    /**
     * 重置分类系统配置
     *
     * @param configType 配置类型（可选）
     * @param operatorId 操作员ID
     * @return 重置响应
     */
    CategoryUnifiedOperateResponse resetCategorySystemConfig(String configType, Long operatorId);

    // ===================== 监控告警 =====================

    /**
     * 获取分类系统监控指标
     *
     * @param metricType 指标类型
     * @param timeRange 时间范围
     * @return 监控指标响应
     */
    CategoryUnifiedQueryResponse<Object> getCategorySystemMetrics(String metricType, String timeRange);

    /**
     * 设置分类告警规则
     *
     * @param alertType 告警类型
     * @param alertRule 告警规则
     * @param operatorId 操作员ID
     * @return 设置响应
     */
    CategoryUnifiedOperateResponse setCategoryAlertRule(String alertType, Object alertRule, Long operatorId);

    /**
     * 获取分类告警记录
     *
     * @param alertType 告警类型（可选）
     * @param queryRequest 查询请求
     * @return 告警记录响应
     */
    PageResponse<Object> getCategoryAlertRecords(String alertType, CategoryUnifiedQueryRequest queryRequest);
} 