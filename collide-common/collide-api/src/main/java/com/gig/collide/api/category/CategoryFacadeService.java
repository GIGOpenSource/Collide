package com.gig.collide.api.category;

import com.gig.collide.api.category.request.CategoryQueryRequest;
import com.gig.collide.api.category.response.CategoryResponse;
import com.gig.collide.base.response.PageResponse;
import com.gig.collide.web.vo.Result;

import java.util.List;

/**
 * 分类门面服务接口 - C端简化版
 * 专注于客户端使用的简单查询功能，移除复杂的管理功能
 * 支持分类状态：active、inactive
 * 
 * @author Collide
 * @version 2.0.0 (C端简化版)
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
     * 支持按多种条件查询
     * 
     * @param request 查询请求
     * @return 分类列表
     */
    Result<PageResponse<CategoryResponse>> queryCategories(CategoryQueryRequest request);

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

    // =================== 层级查询 ===================

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
     * @return 分类树
     */
    Result<List<CategoryResponse>> getCategoryTree(Long rootId, Integer maxDepth);

    /**
     * 获取分类路径
     * 获取从根分类到指定分类的完整路径
     * 
     * @param categoryId 分类ID
     * @return 分类路径列表
     */
    Result<List<CategoryResponse>> getCategoryPath(Long categoryId);

    // =================== 统计功能 ===================

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

    /**
     * 获取分类建议
     * 用于输入提示功能
     * 
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 分类建议列表
     */
    Result<List<CategoryResponse>> getCategorySuggestions(String keyword, Integer limit);

    /**
     * 统计分类数量
     * 
     * @param parentId 父分类ID（可选）
     * @param status 状态（可选）
     * @return 分类数量
     */
    Result<Long> countCategories(Long parentId, String status);
}