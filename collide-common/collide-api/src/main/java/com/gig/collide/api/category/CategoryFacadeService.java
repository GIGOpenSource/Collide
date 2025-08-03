package com.gig.collide.api.category;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 分类门面服务接口 - 简洁版
 * 基于category-simple.sql的设计，支持层级分类和冗余统计
 * 支持分类状态：active、inactive
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface CategoryFacadeService {

    // =================== 分类管理 ===================

    /**
     * 创建分类
     * 支持层级结构，包含内容数量冗余统计
     * 
     * @param request 创建请求
     * @return 创建的分类
     */
    Result<CategoryResponse> createCategory(CategoryCreateRequest request);

    /**
     * 更新分类
     * 支持分类信息和状态更新
     * 
     * @param request 更新请求
     * @return 更新后的分类
     */
    Result<CategoryResponse> updateCategory(CategoryUpdateRequest request);

    /**
     * 删除分类
     * 逻辑删除，将状态更新为inactive
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    Result<Void> deleteCategory(Long categoryId, Long operatorId);

    /**
     * 根据ID获取分类详情
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 分类详情
     */
    Result<CategoryResponse> getCategoryById(Long categoryId, Boolean includeInactive);

    /**
     * 分页查询分类
     * 支持按多种条件查询
     * 
     * @param request 查询请求
     * @return 分类列表
     */
    Result<PageResponse<CategoryResponse>> queryCategories(CategoryQueryRequest request);

    // =================== 层级分类 ===================

    /**
     * 获取根分类列表
     * 获取所有顶级分类（parent_id = 0）
     * 
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 根分类列表
     */
    Result<PageResponse<CategoryResponse>> getRootCategories(Integer currentPage, Integer pageSize,
                                                           String orderBy, String orderDirection);

    /**
     * 获取子分类列表
     * 获取指定分类的直接子分类
     * 
     * @param parentId 父分类ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 子分类列表
     */
    Result<PageResponse<CategoryResponse>> getChildCategories(Long parentId, Integer currentPage, Integer pageSize,
                                                            String orderBy, String orderDirection);

    /**
     * 获取分类树
     * 构建指定分类的树形结构
     * 
     * @param rootId 根分类ID，null表示获取全部分类树
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 分类树
     */
    Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth, Boolean includeInactive);

    /**
     * 获取分类路径
     * 获取从根分类到指定分类的完整路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    Result<List<CategoryResponse>> getCategoryPath(Long categoryId);

    /**
     * 获取分类祖先
     * 获取指定分类的所有祖先分类
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 祖先分类列表
     */
    Result<List<CategoryResponse>> getCategoryAncestors(Long categoryId, Boolean includeInactive);

    /**
     * 获取分类后代
     * 获取指定分类的所有后代分类
     * 
     * @param categoryId 分类ID
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 后代分类列表
     */
    Result<List<CategoryResponse>> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive);

    // =================== 状态管理 ===================

    /**
     * 更新分类状态
     * 支持active、inactive状态
     * 
     * @param categoryId 分类ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新结果
     */
    Result<Void> updateCategoryStatus(Long categoryId, String status, Long operatorId);

    /**
     * 批量更新分类状态
     * 
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    Result<Integer> batchUpdateCategoryStatus(List<Long> categoryIds, String status, Long operatorId);

    /**
     * 激活分类
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> activateCategory(Long categoryId, Long operatorId);

    /**
     * 停用分类
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> deactivateCategory(Long categoryId, Long operatorId);

    // =================== 统计功能 ===================

    /**
     * 更新分类内容数量
     * 同步更新content_count字段
     * 
     * @param categoryId 分类ID
     * @param increment 增量值（可为负数）
     * @return 更新后的内容数量
     */
    Result<Long> updateContentCount(Long categoryId, Long increment);

    /**
     * 获取分类统计信息
     * 
     * @param categoryId 分类ID
     * @return 统计信息
     */
    Result<Map<String, Object>> getCategoryStatistics(Long categoryId);

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    Result<Long> countCategories(Long parentId, String status);

    /**
     * 获取热门分类
     * 根据内容数量排序
     * 
     * @param parentId 父分类ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 热门分类列表
     */
    Result<PageResponse<CategoryResponse>> getPopularCategories(Long parentId, Integer currentPage, Integer pageSize);

    // =================== 搜索功能 ===================

    /**
     * 搜索分类
     * 根据分类名称搜索
     * 
     * @param keyword 搜索关键词
     * @param parentId 父分类ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 搜索结果
     */
    Result<PageResponse<CategoryResponse>> searchCategories(String keyword, Long parentId,
                                                          Integer currentPage, Integer pageSize);

    /**
     * 获取分类建议
     * 用于输入提示功能
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 分类建议列表
     */
    Result<List<CategoryResponse>> getCategorySuggestions(String keyword, Integer limit);

    // =================== 排序管理 ===================

    /**
     * 调整分类排序
     * 
     * @param categoryId 分类ID
     * @param newSort 新排序值
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> adjustCategorySort(Long categoryId, Integer newSort, Long operatorId);

    /**
     * 批量调整排序
     * 
     * @param sortMappings 分类ID和排序值的映射
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    Result<Integer> batchAdjustSort(Map<Long, Integer> sortMappings, Long operatorId);

    /**
     * 移动分类
     * 将分类移动到新的父分类下
     * 
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> moveCategory(Long categoryId, Long newParentId, Long operatorId);

    // =================== 数据同步 ===================

    /**
     * 重新计算内容数量
     * 重新统计所有分类的内容数量
     * 
     * @param categoryId 分类ID（可选，null表示重新计算所有分类）
     * @return 更新成功数量
     */
    Result<Integer> recalculateContentCount(Long categoryId);

    /**
     * 同步分类层级关系
     * 检查和修复分类层级关系的一致性
     * 
     * @return 修复成功数量
     */
    Result<Integer> syncCategoryHierarchy();

    // =================== 高级功能 ===================

    /**
     * 检查分类名称是否存在
     * 在同一父分类下检查名称唯一性
     * 
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 是否存在
     */
    Result<Boolean> existsCategoryName(String name, Long parentId, Long excludeId);

    /**
     * 获取叶子分类
     * 获取没有子分类的分类
     * 
     * @param parentId 父分类ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 叶子分类列表
     */
    Result<PageResponse<CategoryResponse>> getLeafCategories(Long parentId, Integer currentPage, Integer pageSize);

    /**
     * 克隆分类
     * 复制分类及其子分类结构
     * 
     * @param sourceId 源分类ID
     * @param newParentId 新父分类ID
     * @param newName 新分类名称
     * @param operatorId 操作人ID
     * @return 新分类
     */
    Result<CategoryResponse> cloneCategory(Long sourceId, Long newParentId, String newName, Long operatorId);

    /**
     * 合并分类
     * 将源分类的内容合并到目标分类，并删除源分类
     * 
     * @param sourceId 源分类ID
     * @param targetId 目标分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    Result<Void> mergeCategories(Long sourceId, Long targetId, Long operatorId);
}