package com.gig.collide.business.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gig.collide.business.domain.category.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类 Mapper 接口
 *
 * @author GIG Team
 * @version 1.0.0
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
    List<Category> searchByName(@Param("keyword") String keyword);

    /**
     * 获取热门分类（按内容数量排序）
     */
    List<Category> selectHotCategories(@Param("limit") Integer limit);

    /**
     * 更新分类内容数量
     */
    int updateContentCount(@Param("categoryId") Long categoryId, @Param("delta") Integer delta);

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
} 