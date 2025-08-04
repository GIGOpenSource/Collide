package com.gig.collide.api.category;

import com.gig.collide.api.category.request.CategoryCreateRequest;
import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.request.CategoryUpdateRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * 分类门面服务接口 - 规范版
 * 参考Content模块设计，所有方法返回Result包装
 * 支持完整的分类功能：查询、层级、统计、管理
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
public interface CategoryFacadeService {

    // =================== 基础查询 ===================

    /**
     * 获取分类详情
     * 
     * @param categoryId 分类ID
     * @return 分类详情
     */
    Result<CategoryResponse> getCategoryById(Long categoryId);

    /**
     * 分页查询分类
     * 
     * @param request 查询请求（包含parentId, status, currentPage, pageSize, orderBy, orderDirection）
     * @return 分页分类列表
     */
    Result<PageResponse<CategoryResponse>> queryCategories(CategoryQueryRequest request);

    /**
     * 搜索分类
     * 
     * @param keyword 搜索关键词
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页搜索结果
     */
    Result<PageResponse<CategoryResponse>> searchCategories(String keyword, Long parentId, String status,
                                                           Integer currentPage, Integer pageSize,
                                                           String orderBy, String orderDirection);

    // =================== 层级查询 ===================

    /**
     * 获取根分类列表
     * 
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页根分类列表
     */
    Result<PageResponse<CategoryResponse>> getRootCategories(String status, Integer currentPage, Integer pageSize,
                                                            String orderBy, String orderDirection);

    /**
     * 获取子分类列表
     * 
     * @param parentId 父分类ID
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页子分类列表
     */
    Result<PageResponse<CategoryResponse>> getChildCategories(Long parentId, String status,
                                                             Integer currentPage, Integer pageSize,
                                                             String orderBy, String orderDirection);

    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID，null表示获取全部分类树
     * @param maxDepth 最大层级深度
     * @param status 状态（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分类树列表
     */
    Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth, String status,
                                                  String orderBy, String orderDirection);

    /**
     * 获取分类路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表（从根到指定分类）
     */
    Result<List<CategoryResponse>> getCategoryPath(Long categoryId);

    /**
     * 获取分类的所有祖先
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 祖先分类列表
     */
    Result<List<CategoryResponse>> getCategoryAncestors(Long categoryId, Boolean includeInactive);

    /**
     * 获取分类的所有后代
     * 
     * @param categoryId 分类ID
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 后代分类列表
     */
    Result<List<CategoryResponse>> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive);

    // =================== 高级查询 ===================

    /**
     * 获取热门分类
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 分页热门分类列表
     */
    Result<PageResponse<CategoryResponse>> getPopularCategories(Long parentId, String status,
                                                               Integer currentPage, Integer pageSize);

    /**
     * 获取叶子分类
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 分页叶子分类列表
     */
    Result<PageResponse<CategoryResponse>> getLeafCategories(Long parentId, String status,
                                                            Integer currentPage, Integer pageSize);

    /**
     * 获取分类建议
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @param status 状态（可选）
     * @return 分类建议列表
     */
    Result<List<CategoryResponse>> getCategorySuggestions(String keyword, Integer limit, String status);

    // =================== 统计功能 ===================

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    Result<Long> countCategories(Long parentId, String status);

    /**
     * 统计子分类数量
     * 
     * @param parentId 父分类ID
     * @param status 状态（可选）
     * @return 子分类数量
     */
    Result<Long> countChildCategories(Long parentId, String status);

    /**
     * 获取分类统计信息
     * 
     * @param categoryId 分类ID
     * @return 统计信息映射
     */
    Result<Map<String, Object>> getCategoryStatistics(Long categoryId);

    // =================== 验证功能 ===================

    /**
     * 检查分类名称是否存在
     * 
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 是否存在
     */
    Result<Boolean> existsCategoryName(String name, Long parentId, Long excludeId);

    // =================== 管理功能（可选） ===================

    /**
     * 创建分类
     * 
     * @param request 分类创建请求
     * @return 创建的分类
     */
    Result<CategoryResponse> createCategory(CategoryCreateRequest request);

    /**
     * 更新分类
     * 
     * @param categoryId 分类ID
     * @param request 分类更新请求
     * @return 更新的分类
     */
    Result<CategoryResponse> updateCategory(Long categoryId, CategoryUpdateRequest request);

    /**
     * 删除分类
     * 
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    Result<Boolean> deleteCategory(Long categoryId);

    /**
     * 批量更新分类状态
     * 
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @return 影响行数
     */
    Result<Integer> batchUpdateStatus(List<Long> categoryIds, String status);

    /**
     * 更新分类内容数量
     * 
     * @param categoryId 分类ID
     * @param increment 增量值
     * @return 是否更新成功
     */
    Result<Boolean> updateContentCount(Long categoryId, Long increment);

    /**
     * 重新计算分类内容数量
     * 
     * @param categoryId 分类ID（null表示重新计算所有分类）
     * @return 影响行数
     */
    Result<Integer> recalculateContentCount(Long categoryId);
}