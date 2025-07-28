package com.gig.collide.api.category.service;

import com.gig.collide.api.category.request.*;
import com.gig.collide.api.category.response.*;
import com.gig.collide.api.category.response.data.*;
import com.gig.collide.base.response.PageResponse;

import java.util.List;

/**
 * 分类门面服务接口
 * 提供分类核心业务功能
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
public interface CategoryFacadeService {

    // ===================== 分类查询相关 =====================

    /**
     * 分类统一信息查询
     *
     * @param queryRequest 查询请求
     * @return 查询响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> queryCategory(CategoryUnifiedQueryRequest queryRequest);

    /**
     * 基础分类信息查询（不包含敏感信息）
     *
     * @param queryRequest 查询请求
     * @return 基础分类信息响应
     */
    CategoryUnifiedQueryResponse<BasicCategoryInfo> queryBasicCategory(CategoryUnifiedQueryRequest queryRequest);

    /**
     * 分页查询分类信息
     *
     * @param queryRequest 分页查询请求
     * @return 分页响应
     */
    PageResponse<CategoryUnifiedInfo> pageQueryCategories(CategoryUnifiedQueryRequest queryRequest);

    /**
     * 根据分类ID批量查询
     *
     * @param queryRequest 查询请求（包含分类ID列表）
     * @return 批量查询响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> batchQueryCategories(CategoryUnifiedQueryRequest queryRequest);

    /**
     * 查询分类统计信息
     *
     * @param categoryId 分类ID
     * @return 统计信息响应
     */
    CategoryUnifiedQueryResponse<CategoryStatisticsInfo> queryCategoryStatistics(Long categoryId);

    // ===================== 分类操作相关 =====================

    /**
     * 创建分类
     *
     * @param createRequest 创建请求
     * @return 创建响应
     */
    CategoryUnifiedOperateResponse createCategory(CategoryUnifiedCreateRequest createRequest);

    /**
     * 修改分类
     *
     * @param modifyRequest 修改请求
     * @return 修改响应
     */
    CategoryUnifiedOperateResponse modifyCategory(CategoryUnifiedModifyRequest modifyRequest);

    /**
     * 删除分类（逻辑删除）
     *
     * @param categoryId 分类ID
     * @param operatorId 操作员ID
     * @param reason 删除原因
     * @return 删除响应
     */
    CategoryUnifiedOperateResponse deleteCategory(Long categoryId, Long operatorId, String reason);

    /**
     * 移动分类
     *
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @param operatorId 操作员ID
     * @param reason 移动原因
     * @return 移动响应
     */
    CategoryUnifiedOperateResponse moveCategory(Long categoryId, Long newParentId, Long operatorId, String reason);

    /**
     * 调整分类排序
     *
     * @param categoryId 分类ID
     * @param newSortOrder 新排序位置
     * @param operatorId 操作员ID
     * @return 排序响应
     */
    CategoryUnifiedOperateResponse sortCategory(Long categoryId, Integer newSortOrder, Long operatorId);

    /**
     * 启用分类
     *
     * @param categoryId 分类ID
     * @param operatorId 操作员ID
     * @param reason 启用原因
     * @return 启用响应
     */
    CategoryUnifiedOperateResponse enableCategory(Long categoryId, Long operatorId, String reason);

    /**
     * 禁用分类
     *
     * @param categoryId 分类ID
     * @param operatorId 操作员ID
     * @param reason 禁用原因
     * @return 禁用响应
     */
    CategoryUnifiedOperateResponse disableCategory(Long categoryId, Long operatorId, String reason);

    // ===================== 分类树相关 =====================

    /**
     * 获取完整分类树
     *
     * @param rootId 根分类ID（可选，为null时获取所有根分类）
     * @param maxDepth 最大深度（可选）
     * @param includeInactive 是否包含禁用分类
     * @return 分类树响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getCategoryTree(Long rootId, Integer maxDepth, Boolean includeInactive);

    /**
     * 获取根分类列表
     *
     * @param includeInactive 是否包含禁用分类
     * @return 根分类列表响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getRootCategories(Boolean includeInactive);

    /**
     * 获取子分类列表
     *
     * @param parentId 父分类ID
     * @param includeInactive 是否包含禁用分类
     * @return 子分类列表响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getChildCategories(Long parentId, Boolean includeInactive);

    /**
     * 获取叶子分类列表
     *
     * @param includeInactive 是否包含禁用分类
     * @return 叶子分类列表响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getLeafCategories(Boolean includeInactive);

    /**
     * 获取分类路径（从根到当前分类）
     *
     * @param categoryId 分类ID
     * @return 分类路径响应
     */
    CategoryUnifiedQueryResponse<BasicCategoryInfo> getCategoryPath(Long categoryId);

    // ===================== 分类发现和推荐 =====================

    /**
     * 获取热门分类列表
     *
     * @param topCount 返回数量
     * @param timeRange 时间范围（如：7d, 30d, 90d）
     * @return 热门分类响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getPopularCategories(Integer topCount, String timeRange);

    /**
     * 获取新兴分类列表（最近创建且增长快速）
     *
     * @param topCount 返回数量
     * @param daysSince 最近天数
     * @return 新兴分类响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getEmergingCategories(Integer topCount, Integer daysSince);

    /**
     * 获取推荐分类列表（基于用户行为）
     *
     * @param userId 用户ID（可选）
     * @param topCount 返回数量
     * @return 推荐分类响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getRecommendedCategories(Long userId, Integer topCount);

    /**
     * 根据分类层级获取分类列表
     *
     * @param level 分类层级
     * @param includeInactive 是否包含禁用分类
     * @return 分类列表响应
     */
    CategoryUnifiedQueryResponse<CategoryUnifiedInfo> getCategoriesByLevel(Integer level, Boolean includeInactive);

    /**
     * 搜索分类
     *
     * @param keyword 关键词
     * @param queryRequest 查询请求
     * @return 搜索结果响应
     */
    PageResponse<CategoryUnifiedInfo> searchCategories(String keyword, CategoryUnifiedQueryRequest queryRequest);

    // ===================== 分类关联内容 =====================

    /**
     * 获取分类下的内容数量
     *
     * @param categoryId 分类ID
     * @param includeChildren 是否包含子分类
     * @return 内容数量响应
     */
    CategoryUnifiedQueryResponse<Long> getCategoryContentCount(Long categoryId, Boolean includeChildren);

    /**
     * 获取分类使用情况统计
     *
     * @param categoryId 分类ID
     * @return 使用情况统计响应
     */
    CategoryUnifiedQueryResponse<CategoryStatisticsInfo> getCategoryUsageStats(Long categoryId);

    // ===================== 验证相关 =====================

    /**
     * 验证分类是否存在
     *
     * @param categoryId 分类ID
     * @return true如果存在
     */
    Boolean validateCategoryExists(Long categoryId);

    /**
     * 验证分类名称是否可用（同级不重名）
     *
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeCategoryId 排除的分类ID（修改时用）
     * @return true如果可用
     */
    Boolean validateCategoryNameAvailable(String name, Long parentId, Long excludeCategoryId);

    /**
     * 验证分类是否可以添加子分类
     *
     * @param categoryId 分类ID
     * @return true如果可以添加
     */
    Boolean validateCanHaveChildren(Long categoryId);

    /**
     * 验证分类是否可以删除
     *
     * @param categoryId 分类ID
     * @return true如果可以删除
     */
    Boolean validateCanDelete(Long categoryId);

    /**
     * 验证分类移动是否有效（避免循环引用）
     *
     * @param categoryId 要移动的分类ID
     * @param newParentId 新父分类ID
     * @return true如果移动有效
     */
    Boolean validateCategoryMove(Long categoryId, Long newParentId);

    /**
     * 检查用户是否有分类修改权限
     *
     * @param categoryId 分类ID
     * @param userId 用户ID
     * @return true如果有权限
     */
    Boolean checkCategoryModifyPermission(Long categoryId, Long userId);

    // ===================== 缓存相关 =====================

    /**
     * 刷新分类缓存
     *
     * @param categoryId 分类ID（可选，为null时刷新所有缓存）
     * @return 刷新响应
     */
    CategoryUnifiedOperateResponse refreshCategoryCache(Long categoryId);

    /**
     * 预热分类缓存
     *
     * @return 预热响应
     */
    CategoryUnifiedOperateResponse warmupCategoryCache();
} 