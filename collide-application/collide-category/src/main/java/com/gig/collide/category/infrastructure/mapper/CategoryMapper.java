package com.gig.collide.category.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.category.domain.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类 Mapper 接口
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 根据父分类ID查询子分类列表
     */
    List<Category> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据分类名称搜索分类
     */
    List<Category> searchByName(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 获取热门分类（按内容数量排序）
     */
    List<Category> selectHotCategories(@Param("limit") Integer limit);

    /**
     * 更新分类内容数量
     */
    int updateContentCount(@Param("categoryId") Long categoryId, @Param("delta") Long delta);

    /**
     * 批量更新分类状态
     */
    int updateStatusBatch(@Param("categoryIds") List<Long> categoryIds, @Param("status") String status);

    /**
     * 根据层级查询分类
     */
    List<Category> selectByLevel(@Param("level") Integer level);

    /**
     * 查询分类及其所有子分类的路径
     */
    List<Category> selectByPathPrefix(@Param("pathPrefix") String pathPrefix);

    /**
     * 幂等性更新分类信息
     * 使用乐观锁机制，确保更新的幂等性
     */
    int updateCategoryIdempotent(@Param("categoryId") Long categoryId,
                                @Param("name") String name,
                                @Param("description") String description,
                                @Param("iconUrl") String iconUrl,
                                @Param("coverUrl") String coverUrl,
                                @Param("sortOrder") Integer sortOrder,
                                @Param("lastModifierId") Long lastModifierId,
                                @Param("lastModifierName") String lastModifierName,
                                @Param("expectedVersion") Integer expectedVersion);

    /**
     * 幂等性更新分类状态
     */
    int updateStatusIdempotent(@Param("categoryId") Long categoryId,
                              @Param("expectedStatus") String expectedStatus,
                              @Param("newStatus") String newStatus,
                              @Param("expectedVersion") Integer expectedVersion);

    /**
     * 检查分类名称是否存在（同级分类下不能重名）
     */
    int checkNameExists(@Param("parentId") Long parentId, 
                       @Param("name") String name, 
                       @Param("excludeId") Long excludeId);

    /**
     * 获取分类的完整路径
     */
    String selectCategoryPath(@Param("categoryId") Long categoryId);

    /**
     * 获取所有根分类
     */
    List<Category> selectRootCategories();

    /**
     * 查询直接子分类ID（递归逻辑在应用层实现，避免JOIN）
     */
    List<Long> selectAllChildrenIds(@Param("parentId") Long parentId);

    /**
     * 统计分类总数
     */
    Long countCategories(@Param("status") String status);

    /**
     * 根据创建者ID查询分类
     */
    List<Category> selectByCreatorId(@Param("creatorId") Long creatorId, 
                                    @Param("offset") Long offset, 
                                    @Param("limit") Integer limit);
} 