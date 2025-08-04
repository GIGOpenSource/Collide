package com.gig.collide.category.domain.service;

import com.gig.collide.category.domain.entity.Category;
import com.gig.collide.base.response.PageResponse;

import java.util.List;
import java.util.Map;

/**
 * 分类业务服务接口 - 规范版
 * 参考Content模块设计，分页查询返回PageResponse，非分页查询返回List
 * 支持完整的分类功能：查询、层级、统计、管理
 * 
 * @author Collide
 * @version 5.0.0 (与Content模块一致版)
 * @since 2024-01-01
 */
public interface CategoryService {

    // =================== 基础查询 ===================

    /**
     * 根据ID获取分类
     * 
     * @param categoryId 分类ID
     * @return 分类对象
     */
    Category getCategoryById(Long categoryId);

    /**
     * 分页查询分类
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（active/inactive，可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页结果
     */
    PageResponse<Category> queryCategories(Long parentId, String status, Integer currentPage, Integer pageSize,
                                          String orderBy, String orderDirection);

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
     * @return 搜索结果
     */
    PageResponse<Category> searchCategories(String keyword, Long parentId, String status, 
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
     * @return 分页根分类结果
     */
    PageResponse<Category> getRootCategories(String status, Integer currentPage, Integer pageSize, 
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
     * @return 分页子分类结果
     */
    PageResponse<Category> getChildCategories(Long parentId, String status, 
                                             Integer currentPage, Integer pageSize,
                                             String orderBy, String orderDirection);

    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID（null表示获取全部）
     * @param maxDepth 最大层级深度
     * @param status 状态（可选）
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分类树
     */
    List<Category> getCategoryTree(Long rootId, Integer maxDepth, String status,
                                  String orderBy, String orderDirection);

    /**
     * 获取分类路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表（从根到指定分类）
     */
    List<Category> getCategoryPath(Long categoryId);

    /**
     * 获取分类的所有祖先
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 祖先分类列表
     */
    List<Category> getCategoryAncestors(Long categoryId, Boolean includeInactive);

    /**
     * 获取分类的所有后代
     * 
     * @param categoryId 分类ID
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 后代分类列表
     */
    List<Category> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive);

    // =================== 高级查询 ===================

    /**
     * 获取热门分类
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 分页热门分类结果
     */
    PageResponse<Category> getPopularCategories(Long parentId, String status, 
                                               Integer currentPage, Integer pageSize);

    /**
     * 获取叶子分类
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 分页叶子分类结果
     */
    PageResponse<Category> getLeafCategories(Long parentId, String status, 
                                            Integer currentPage, Integer pageSize);

    /**
     * 获取分类建议
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @param status 状态（可选）
     * @return 分类建议列表
     */
    List<Category> getCategorySuggestions(String keyword, Integer limit, String status);

    // =================== 统计功能 ===================

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    Long countCategories(Long parentId, String status);

    /**
     * 统计子分类数量
     * 
     * @param parentId 父分类ID
     * @param status 状态（可选）
     * @return 子分类数量
     */
    Long countChildCategories(Long parentId, String status);

    /**
     * 获取分类统计信息
     * 
     * @param categoryId 分类ID
     * @return 统计信息映射
     */
    Map<String, Object> getCategoryStatistics(Long categoryId);

    // =================== 验证功能 ===================

    /**
     * 检查分类名称是否存在
     * 
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsCategoryName(String name, Long parentId, Long excludeId);

    // =================== 管理功能 ===================

    /**
     * 创建分类
     * 
     * @param category 分类对象
     * @return 创建的分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类
     * 
     * @param category 分类对象
     * @return 更新的分类
     */
    Category updateCategory(Category category);

    /**
     * 删除分类
     * 
     * @param categoryId 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long categoryId);

    /**
     * 批量更新分类状态
     * 
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @return 影响行数
     */
    int batchUpdateStatus(List<Long> categoryIds, String status);

    /**
     * 更新分类内容数量
     * 
     * @param categoryId 分类ID
     * @param increment 增量值
     * @return 是否更新成功
     */
    boolean updateContentCount(Long categoryId, Long increment);

    /**
     * 重新计算分类内容数量
     * 
     * @param categoryId 分类ID（null表示重新计算所有分类）
     * @return 影响行数
     */
    int recalculateContentCount(Long categoryId);
}