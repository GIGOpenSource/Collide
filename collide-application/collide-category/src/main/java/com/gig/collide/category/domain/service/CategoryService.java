package com.gig.collide.category.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gig.collide.category.domain.entity.Category;

import java.util.List;

/**
 * 分类业务服务接口 - C端简化版
 * 专注于客户端使用的简单查询功能，移除复杂的管理功能
 * 
 * @author Collide
 * @version 2.0.0 (C端简化版)
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
     * @param parentId 父分类ID
     * @param status 状态
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 分页分类列表
     */
    IPage<Category> queryCategories(Long parentId, String status, Integer currentPage, Integer pageSize,
                                   String orderBy, String orderDirection);

    /**
     * 搜索分类
     * 
     * @param keyword 搜索关键词
     * @param parentId 父分类ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 搜索结果
     */
    IPage<Category> searchCategories(String keyword, Long parentId, Integer currentPage, Integer pageSize,
                                   String orderBy, String orderDirection);

    // =================== 层级查询 ===================

    /**
     * 获取根分类列表
     * 
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 根分类列表
     */
    IPage<Category> getRootCategories(Integer currentPage, Integer pageSize, String orderBy, String orderDirection);

    /**
     * 获取子分类列表
     * 
     * @param parentId 父分类ID
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @param orderBy 排序字段
     * @param orderDirection 排序方向
     * @return 子分类列表
     */
    IPage<Category> getChildCategories(Long parentId, Integer currentPage, Integer pageSize,
                                      String orderBy, String orderDirection);

    /**
     * 获取分类树
     * 
     * @param rootId 根分类ID（null表示获取全部）
     * @param maxDepth 最大层级深度
     * @return 分类树
     */
    List<Category> getCategoryTree(Long rootId, Integer maxDepth);

    /**
     * 获取分类路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    List<Category> getCategoryPath(Long categoryId);

    // =================== 统计功能 ===================

    /**
     * 获取热门分类
     * 
     * @param parentId 父分类ID（可选）
     * @param currentPage 页码
     * @param pageSize 页面大小
     * @return 热门分类列表
     */
    IPage<Category> getPopularCategories(Long parentId, Integer currentPage, Integer pageSize);

    /**
     * 获取分类建议
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 分类建议列表
     */
    List<Category> getCategorySuggestions(String keyword, Integer limit);

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    long countCategories(Long parentId, String status);
}