package com.gig.collide.api.category.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 分类查询请求 - 简洁版
 * 基于category-simple.sql的字段，支持多种查询条件
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryQueryRequest implements Serializable {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类ID列表（批量查询）
     */
    private List<Long> ids;

    /**
     * 分类名称关键词
     */
    private String nameKeyword;

    /**
     * 分类名称（精确匹配）
     */
    private String name;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 父分类ID列表
     */
    private List<Long> parentIds;

    /**
     * 状态：active、inactive
     */
    private String status;

    /**
     * 状态列表
     */
    private List<String> statuses;

    /**
     * 查询类型：root、child、tree、leaf、popular、search
     */
    private String queryType;

    /**
     * 最小内容数量
     */
    private Long minContentCount;

    /**
     * 最大内容数量
     */
    private Long maxContentCount;

    /**
     * 最小排序值
     */
    private Integer minSort;

    /**
     * 最大排序值
     */
    private Integer maxSort;

    /**
     * 最大层级深度（用于树形查询）
     */
    private Integer maxDepth;

    // =================== 分页参数 ===================

    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer currentPage = 1;

    /**
     * 页面大小
     */
    @Min(value = 1, message = "页面大小必须大于0")
    private Integer pageSize = 20;

    /**
     * 排序字段：id、name、sort、content_count、create_time、update_time
     */
    private String orderBy = "sort";

    /**
     * 排序方向：ASC、DESC
     */
    private String orderDirection = "ASC";

    /**
     * 是否包含已停用分类
     */
    private Boolean includeInactive = false;

    /**
     * 是否只查询根分类
     */
    private Boolean rootOnly = false;

    /**
     * 是否只查询叶子分类
     */
    private Boolean leafOnly = false;

    /**
     * 是否包含子分类（用于树形查询）
     */
    private Boolean includeChildren = false;

    // =================== 业务方法 ===================

    /**
     * 是否查询根分类
     */
    public boolean isQueryRootCategories() {
        return "root".equals(queryType) || (rootOnly != null && rootOnly);
    }

    /**
     * 是否查询子分类
     */
    public boolean isQueryChildCategories() {
        return "child".equals(queryType) || parentId != null;
    }

    /**
     * 是否查询叶子分类
     */
    public boolean isQueryLeafCategories() {
        return "leaf".equals(queryType) || (leafOnly != null && leafOnly);
    }

    /**
     * 是否树形查询
     */
    public boolean isTreeQuery() {
        return "tree".equals(queryType) || (includeChildren != null && includeChildren);
    }

    /**
     * 是否热门分类查询
     */
    public boolean isPopularQuery() {
        return "popular".equals(queryType);
    }

    /**
     * 是否搜索查询
     */
    public boolean isSearchQuery() {
        return "search".equals(queryType) || nameKeyword != null;
    }

    /**
     * 是否ID查询
     */
    public boolean isIdQuery() {
        return id != null || (ids != null && !ids.isEmpty());
    }

    /**
     * 是否名称查询
     */
    public boolean isNameQuery() {
        return name != null || nameKeyword != null;
    }

    /**
     * 是否范围查询
     */
    public boolean isRangeQuery() {
        return minContentCount != null || maxContentCount != null 
               || minSort != null || maxSort != null;
    }

    /**
     * 验证排序字段
     */
    public boolean isValidOrderBy() {
        if (orderBy == null) {
            return true;
        }
        return "id".equals(orderBy) || "name".equals(orderBy) || "sort".equals(orderBy)
               || "content_count".equals(orderBy) || "create_time".equals(orderBy) 
               || "update_time".equals(orderBy);
    }

    /**
     * 验证排序方向
     */
    public boolean isValidOrderDirection() {
        if (orderDirection == null) {
            return true;
        }
        return "ASC".equals(orderDirection) || "DESC".equals(orderDirection);
    }

    /**
     * 验证状态值
     */
    public boolean isValidStatus() {
        if (status == null) {
            return true;
        }
        return "active".equals(status) || "inactive".equals(status);
    }

    /**
     * 获取实际的父分类ID
     */
    public Long getActualParentId() {
        if (isQueryRootCategories()) {
            return 0L;
        }
        return parentId;
    }

    /**
     * 获取实际的状态值
     */
    public String getActualStatus() {
        if (includeInactive != null && includeInactive) {
            return null; // 包含所有状态
        }
        return status != null ? status : "active";
    }

    /**
     * 设置根分类查询
     */
    public void setRootQuery() {
        this.queryType = "root";
        this.rootOnly = true;
        this.parentId = 0L;
    }

    /**
     * 设置子分类查询
     */
    public void setChildQuery(Long parentCategoryId) {
        this.queryType = "child";
        this.parentId = parentCategoryId;
        this.rootOnly = false;
    }

    /**
     * 设置叶子分类查询
     */
    public void setLeafQuery() {
        this.queryType = "leaf";
        this.leafOnly = true;
    }

    /**
     * 设置树形查询
     */
    public void setTreeQuery(Long rootId, Integer depth) {
        this.queryType = "tree";
        this.parentId = rootId;
        this.maxDepth = depth;
        this.includeChildren = true;
    }

    /**
     * 设置热门分类查询
     */
    public void setPopularQuery() {
        this.queryType = "popular";
        this.orderBy = "content_count";
        this.orderDirection = "DESC";
    }

    /**
     * 设置搜索查询
     */
    public void setSearchQuery(String keyword) {
        this.queryType = "search";
        this.nameKeyword = keyword;
    }
}