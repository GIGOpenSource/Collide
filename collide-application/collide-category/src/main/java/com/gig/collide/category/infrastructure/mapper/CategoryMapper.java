package com.gig.collide.category.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.category.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 分类数据访问层 - 简洁版
 * 基于category-simple.sql的设计，支持层级分类查询
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    // =================== 基础查询 ===================

    /**
     * 根据父分类ID获取子分类列表
     * 
     * @param parentId 父分类ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 子分类列表
     */
    List<Category> selectChildCategories(@Param("parentId") Long parentId,
                                        @Param("status") String status,
                                        @Param("orderBy") String orderBy,
                                        @Param("orderDirection") String orderDirection);

    /**
     * 分页获取分类列表
     * 
     * @param page 分页对象
     * @param parentId 父分类ID
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页分类列表
     */
    IPage<Category> selectCategoriesPage(IPage<Category> page,
                                        @Param("parentId") Long parentId,
                                        @Param("status") String status,
                                        @Param("orderBy") String orderBy,
                                        @Param("orderDirection") String orderDirection);

    /**
     * 根据名称搜索分类
     * 
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @param parentId 父分类ID（可选）
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 搜索结果
     */
    IPage<Category> searchCategories(IPage<Category> page,
                                   @Param("keyword") String keyword,
                                   @Param("parentId") Long parentId,
                                   @Param("status") String status,
                                   @Param("orderBy") String orderBy,
                                   @Param("orderDirection") String orderDirection);

    // =================== 层级查询 ===================

    /**
     * 获取分类路径
     * 从指定分类向上查找到根分类的完整路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    List<Category> selectCategoryPath(@Param("categoryId") Long categoryId);

    /**
     * 获取分类的所有祖先
     * 
     * @param categoryId 分类ID
     * @param includeInactive 是否包含已停用分类
     * @return 祖先分类列表
     */
    List<Category> selectCategoryAncestors(@Param("categoryId") Long categoryId,
                                          @Param("includeInactive") Boolean includeInactive);

    /**
     * 获取分类的所有后代
     * 
     * @param categoryId 分类ID
     * @param maxDepth 最大层级深度
     * @param includeInactive 是否包含已停用分类
     * @return 后代分类列表
     */
    List<Category> selectCategoryDescendants(@Param("categoryId") Long categoryId,
                                           @Param("maxDepth") Integer maxDepth,
                                           @Param("includeInactive") Boolean includeInactive);

    /**
     * 获取分类树形结构数据
     * 
     * @param rootId 根分类ID（null表示获取全部）
     * @param maxDepth 最大层级深度
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 树形分类列表
     */
    List<Category> selectCategoryTree(@Param("rootId") Long rootId,
                                    @Param("maxDepth") Integer maxDepth,
                                    @Param("status") String status,
                                    @Param("orderBy") String orderBy,
                                    @Param("orderDirection") String orderDirection);

    // =================== 统计查询 ===================

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    Long countCategories(@Param("parentId") Long parentId,
                        @Param("status") String status);

    /**
     * 统计子分类数量
     * 
     * @param parentId 父分类ID
     * @param status 状态
     * @return 子分类数量
     */
    Long countChildCategories(@Param("parentId") Long parentId,
                             @Param("status") String status);

    /**
     * 获取分类统计信息
     * 
     * @param categoryId 分类ID
     * @return 统计信息
     */
    Map<String, Object> selectCategoryStatistics(@Param("categoryId") Long categoryId);

    // =================== 更新操作 ===================

    /**
     * 更新分类内容数量
     * 
     * @param categoryId 分类ID
     * @param increment 增量值
     * @return 影响行数
     */
    int updateContentCount(@Param("categoryId") Long categoryId,
                          @Param("increment") Long increment);

    /**
     * 批量更新分类状态
     * 
     * @param categoryIds 分类ID列表
     * @param status 新状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("categoryIds") List<Long> categoryIds,
                         @Param("status") String status);

    /**
     * 批量更新排序值
     * 
     * @param sortMappings 分类ID和排序值的映射
     * @return 影响行数
     */
    int batchUpdateSort(@Param("sortMappings") Map<Long, Integer> sortMappings);

    /**
     * 更新分类的父分类
     * 
     * @param categoryId 分类ID
     * @param newParentId 新父分类ID
     * @return 影响行数
     */
    int updateParentCategory(@Param("categoryId") Long categoryId,
                           @Param("newParentId") Long newParentId);

    // =================== 高级查询 ===================

    /**
     * 检查分类名称是否存在
     * 在同一父分类下检查名称唯一性
     * 
     * @param name 分类名称
     * @param parentId 父分类ID
     * @param excludeId 排除的分类ID（用于更新时检查）
     * @return 是否存在
     */
    boolean existsCategoryName(@Param("name") String name,
                              @Param("parentId") Long parentId,
                              @Param("excludeId") Long excludeId);

    /**
     * 获取热门分类
     * 根据内容数量排序
     * 
     * @param page 分页对象
     * @param parentId 父分类ID（可选）
     * @param status 状态
     * @return 热门分类列表
     */
    IPage<Category> selectPopularCategories(IPage<Category> page,
                                          @Param("parentId") Long parentId,
                                          @Param("status") String status);

    /**
     * 获取叶子分类
     * 没有子分类的分类
     * 
     * @param page 分页对象
     * @param parentId 父分类ID（可选）
     * @param status 状态
     * @return 叶子分类列表
     */
    IPage<Category> selectLeafCategories(IPage<Category> page,
                                       @Param("parentId") Long parentId,
                                       @Param("status") String status);

    /**
     * 获取分类建议
     * 用于输入提示功能
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @param status 状态
     * @return 分类建议列表
     */
    List<Category> selectCategorySuggestions(@Param("keyword") String keyword,
                                           @Param("limit") Integer limit,
                                           @Param("status") String status);

    /**
     * 获取根分类列表
     * 
     * @param page 分页对象
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 根分类列表
     */
    IPage<Category> selectRootCategories(IPage<Category> page,
                                       @Param("status") String status,
                                       @Param("orderBy") String orderBy,
                                       @Param("orderDirection") String orderDirection);

    // =================== 数据同步 ===================

    /**
     * 重新计算分类内容数量
     * 
     * @param categoryId 分类ID（null表示重新计算所有分类）
     * @return 影响行数
     */
    int recalculateContentCount(@Param("categoryId") Long categoryId);

    /**
     * 同步分类层级关系
     * 检查和修复分类层级关系的一致性
     * 
     * @return 修复成功数量
     */
    int syncCategoryHierarchy();

    // =================== 范围查询 ===================

    /**
     * 根据内容数量范围查询分类
     * 
     * @param page 分页对象
     * @param minContentCount 最小内容数量
     * @param maxContentCount 最大内容数量
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分类列表
     */
    IPage<Category> selectCategoriesByContentCountRange(IPage<Category> page,
                                                       @Param("minContentCount") Long minContentCount,
                                                       @Param("maxContentCount") Long maxContentCount,
                                                       @Param("status") String status,
                                                       @Param("orderBy") String orderBy,
                                                       @Param("orderDirection") String orderDirection);

    /**
     * 根据排序值范围查询分类
     * 
     * @param page 分页对象
     * @param minSort 最小排序值
     * @param maxSort 最大排序值
     * @param parentId 父分类ID（可选）
     * @param status 状态
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分类列表
     */
    IPage<Category> selectCategoriesBySortRange(IPage<Category> page,
                                              @Param("minSort") Integer minSort,
                                              @Param("maxSort") Integer maxSort,
                                              @Param("parentId") Long parentId,
                                              @Param("status") String status,
                                              @Param("orderBy") String orderBy,
                                              @Param("orderDirection") String orderDirection);
}