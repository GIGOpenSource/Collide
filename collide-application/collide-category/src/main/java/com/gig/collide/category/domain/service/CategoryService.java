package com.gig.collide.category.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.category.domain.entity.Category;

import java.util.List;
import java.util.Map;

/**
 * 分类业务服务接口 - 简洁版
 * 基于category-simple.sql的设计，提供核心业务逻辑
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
public interface CategoryService {

    // =================== 分类管理 ===================

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
     * @return 更新后的分类
     */
    Category updateCategory(Category category);

    /**
     * 删除分类（逻辑删除）
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 删除结果
     */
    boolean deleteCategory(Long categoryId, Long operatorId);

    /**
     * 根据ID获取分类
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 分类对象
     */
    Category getCategoryById(Long categoryId, Boolean includeInactive);

    /**
     * 根据ID列表批量获取分类
     * 
     * @param categoryIds 分类ID列表
     * @param includeInactive 是否包含已停用分类
     * @return 分类列表
     */
    List<Category> getCategoriesByIds(List<Long> categoryIds, Boolean includeInactive);

    // =================== 查询方法 ===================

    /**
     * 分页查询分类
     * 
     * @param parentId 父分类ID
     * @param status 状态
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页分类列表
     */
    IPage<Category> queryCategories(Long parentId, String status, Integer pageNum, Integer pageSize,
                                   String orderBy, String orderDirection);

    /**
     * 搜索分类
     * 
     * @param keyword 搜索关键词
     * @param parentId 父分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 搜索结果
     */
    IPage<Category> searchCategories(String keyword, Long parentId, Integer pageNum, Integer pageSize,
                                   String orderBy, String orderDirection);

    // =================== 层级分类 ===================

    /**
     * 获取根分类列表
     * 
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 根分类列表
     */
    IPage<Category> getRootCategories(Integer pageNum, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取子分类列表
     * 
     * @param parentId 父分类ID
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 子分类列表
     */
    IPage<Category> getChildCategories(Long parentId, Integer pageNum, Integer pageSize,
                                      String orderBy, String orderDirection);

    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID（null表示获取全部）
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 分类树
     */
    List<Category> getCategoryTree(Long rootId, Integer maxDepth, Boolean includeInactive);

    /**
     * 获取分类路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    List<Category> getCategoryPath(Long categoryId);

    /**
     * 获取分类祖先
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 祖先分类列表
     */
    List<Category> getCategoryAncestors(Long categoryId, Boolean includeInactive);

    /**
     * 获取分类后代
     * 
     * @param categoryId 分类ID
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 后代分类列表
     */
    List<Category> getCategoryDescendants(Long categoryId, Integer maxDepth, Boolean includeInactive);

    /**
     * 构建分类树
     * 
     * @param categories 分类列表
     * @param maxDepth 最大层级深度
     * @return 树形分类列表
     */
    List<Category> buildCategoryTree(List<Category> categories, Integer maxDepth);

    // =================== 状态管理 ===================

    /**
     * 更新分类状态
     * 
     * @param categoryId 分类ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新结果
     */
    boolean updateCategoryStatus(Long categoryId, String status, Long operatorId);

    /**
     * 批量更新分类状态
     * 
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    int batchUpdateCategoryStatus(List<Long> categoryIds, String status, Long operatorId);

    /**
     * 激活分类
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    boolean activateCategory(Long categoryId, Long operatorId);

    /**
     * 停用分类
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    boolean deactivateCategory(Long categoryId, Long operatorId);

    // =================== 统计功能 ===================

    /**
     * 更新分类内容数量
     * 
     * @param categoryId 分类ID
     * @param increment 增量值
     * @return 更新后的内容数量
     */
    long updateContentCount(Long categoryId, Long increment);

    /**
     * 获取分类统计信息
     * 
     * @param categoryId 分类ID
     * @return 统计信息
     */
    Map<String, Object> getCategoryStatistics(Long categoryId);

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    long countCategories(Long parentId, String status);

    /**
     * 获取热门分类
     * 
     * @param parentId 父分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 热门分类列表
     */
    IPage<Category> getPopularCategories(Long parentId, Integer pageNum, Integer pageSize);

    // =================== 排序管理 ===================

    /**
     * 调整分类排序
     * 
     * @param categoryId 分类ID
     * @param newSort 新排序值
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    boolean adjustCategorySort(Long categoryId, Integer newSort, Long operatorId);

    /**
     * 批量调整排序
     * 
     * @param sortMappings 分类ID和排序值的映射
     * @param operatorId 操作人ID
     * @return 更新成功数量
     */
    int batchAdjustSort(Map<Long, Integer> sortMappings, Long operatorId);

    /**
     * 移动分类
     * 
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    boolean moveCategory(Long categoryId, Long newParentId, Long operatorId);

    // =================== 高级功能 ===================

    /**
     * 检查分类名称是否存在
     * 
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    boolean existsCategoryName(String name, Long parentId, Long excludeId);

    /**
     * 获取叶子分类
     * 
     * @param parentId 父分类ID（可选）
     * @param pageNum 页码
     * @param pageSize 页面大小
     * @return 叶子分类列表
     */
    IPage<Category> getLeafCategories(Long parentId, Integer pageNum, Integer pageSize);

    /**
     * 获取分类建议
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 分类建议列表
     */
    List<Category> getCategorySuggestions(String keyword, Integer limit);

    /**
     * 克隆分类
     * 
     * @param sourceId 源分类ID
     * @param newParentId 新父分类ID
     * @param newName 新分类名称
     * @param operatorId 操作人ID
     * @return 新分类
     */
    Category cloneCategory(Long sourceId, Long newParentId, String newName, Long operatorId);

    /**
     * 合并分类
     * 
     * @param sourceId 源分类ID
     * @param targetId 目标分类ID
     * @param operatorId 操作人ID
     * @return 操作结果
     */
    boolean mergeCategories(Long sourceId, Long targetId, Long operatorId);

    // =================== 数据同步 ===================

    /**
     * 重新计算内容数量
     * 
     * @param categoryId 分类ID（null表示重新计算所有分类）
     * @return 更新成功数量
     */
    int recalculateContentCount(Long categoryId);

    /**
     * 同步分类层级关系
     * 
     * @return 修复成功数量
     */
    int syncCategoryHierarchy();

    // =================== 验证方法 ===================

    /**
     * 验证分类权限
     * 
     * @param categoryId 分类ID
     * @param operatorId 操作人ID
     * @param operation 操作类型
     * @return 是否有权限
     */
    boolean validateCategoryPermission(Long categoryId, Long operatorId, String operation);

    /**
     * 验证分类层级循环引用
     * 
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @return 是否会形成循环
     */
    boolean wouldCreateCycle(Long categoryId, Long newParentId);
}