package com.gig.collide.category.domain.repository;

import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.category.domain.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * 分类数据仓库接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
public interface CategoryRepository {

    /**
     * 保存分类
     */
    Category save(Category category);

    /**
     * 根据ID查询分类
     */
    Optional<Category> findById(Long categoryId);

    /**
     * 根据ID删除分类
     */
    boolean deleteById(Long categoryId);

    /**
     * 分页查询分类
     */
    PageResponse<Category> findByPage(CategoryQueryRequest request);

    /**
     * 根据父分类ID查询子分类
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 获取热门分类
     */
    List<Category> findHotCategories(Integer limit);

    /**
     * 根据关键词搜索分类
     */
    List<Category> searchByKeyword(String keyword, Integer limit);

    /**
     * 根据层级查询分类
     */
    List<Category> findByLevel(Integer level);

    /**
     * 获取所有根分类
     */
    List<Category> findRootCategories();

    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(Long parentId, String name, Long excludeId);

    /**
     * 更新分类内容数量
     */
    boolean updateContentCount(Long categoryId, Long delta);

    /**
     * 批量更新分类状态
     */
    boolean batchUpdateStatus(List<Long> categoryIds, String status);

    /**
     * 获取分类的完整路径
     */
    String getCategoryPath(Long categoryId);

    /**
     * 递归获取所有子分类ID
     */
    List<Long> getAllChildrenIds(Long parentId);

    /**
     * 统计分类总数
     */
    Long countByStatus(String status);

    /**
     * 根据创建者ID查询分类
     */
    PageResponse<Category> findByCreatorId(Long creatorId, int pageNo, int pageSize);

    // ================================ 幂等性操作方法 ================================

    /**
     * 幂等性更新分类信息
     * 使用乐观锁机制，确保更新的幂等性
     */
    boolean updateCategoryIdempotent(Long categoryId, 
                                   String name, 
                                   String description,
                                   String iconUrl, 
                                   String coverUrl,
                                   Integer sortOrder,
                                   Long lastModifierId,
                                   String lastModifierName,
                                   Integer expectedVersion);

    /**
     * 幂等性更新分类状态
     */
    boolean updateStatusIdempotent(Long categoryId, 
                                 String expectedStatus, 
                                 String newStatus, 
                                 Integer expectedVersion);
} 