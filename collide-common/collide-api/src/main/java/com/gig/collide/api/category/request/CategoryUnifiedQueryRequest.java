package com.gig.collide.api.category.request;

import com.gig.collide.api.category.enums.CategoryStatusEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类统一查询请求
 * 支持多种查询条件和排序方式
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUnifiedQueryRequest extends BaseRequest {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类ID列表（批量查询）
     */
    private List<Long> categoryIds;

    /**
     * 分类名称关键词
     */
    private String nameKeyword;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 父分类ID列表
     */
    private List<Long> parentIds;

    /**
     * 分类状态
     */
    private CategoryStatusEnum status;

    /**
     * 分类状态列表
     */
    private List<CategoryStatusEnum> statuses;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类层级列表
     */
    private List<Integer> levels;

    /**
     * 最小层级
     */
    private Integer minLevel;

    /**
     * 最大层级
     */
    private Integer maxLevel;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者ID列表
     */
    private List<Long> creatorIds;

    /**
     * 最小内容数量
     */
    private Long minContentCount;

    /**
     * 最大内容数量
     */
    private Long maxContentCount;

    /**
     * 分类路径关键词
     */
    private String pathKeyword;

    /**
     * 创建时间范围 - 开始
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间范围 - 结束
     */
    private LocalDateTime createTimeEnd;

    /**
     * 更新时间范围 - 开始
     */
    private LocalDateTime updateTimeStart;

    /**
     * 更新时间范围 - 结束
     */
    private LocalDateTime updateTimeEnd;

    /**
     * 排序字段
     * 可选值：sort_order, create_time, update_time, name, content_count, level
     */
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String sortDirection;

    /**
     * 是否包含子分类信息
     */
    private Boolean includeChildren;

    /**
     * 是否包含父分类信息
     */
    private Boolean includeParent;

    /**
     * 是否包含祖先路径信息
     */
    private Boolean includeAncestors;

    /**
     * 是否包含统计信息
     */
    private Boolean includeStatistics;

    /**
     * 子分类层级深度（用于递归查询子分类）
     */
    private Integer childrenDepth;

    /**
     * 仅查询根分类
     */
    private Boolean rootOnly;

    /**
     * 仅查询叶子分类
     */
    private Boolean leafOnly;

    /**
     * 是否查询热门分类（基于内容数量）
     */
    private Boolean popularOnly;

    /**
     * 热门分类阈值
     */
    private Long popularThreshold;

    // ===================== 便捷构造器 =====================

    /**
     * 根据分类ID查询
     */
    public CategoryUnifiedQueryRequest(Long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * 根据父分类ID查询
     */
    public static CategoryUnifiedQueryRequest byParent(Long parentId) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setParentId(parentId);
        return request;
    }

    /**
     * 根据分类层级查询
     */
    public static CategoryUnifiedQueryRequest byLevel(Integer level) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setLevel(level);
        return request;
    }

    /**
     * 查询根分类
     */
    public static CategoryUnifiedQueryRequest rootCategories() {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setRootOnly(true);
        request.setLevel(1);
        request.setStatus(CategoryStatusEnum.ACTIVE);
        return request;
    }

    /**
     * 查询叶子分类
     */
    public static CategoryUnifiedQueryRequest leafCategories() {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setLeafOnly(true);
        request.setStatus(CategoryStatusEnum.ACTIVE);
        return request;
    }

    /**
     * 查询热门分类
     */
    public static CategoryUnifiedQueryRequest popularCategories() {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setPopularOnly(true);
        request.setStatus(CategoryStatusEnum.ACTIVE);
        request.setSortBy("content_count");
        request.setSortDirection("DESC");
        return request;
    }

    /**
     * 查询分类树（包含子分类）
     */
    public static CategoryUnifiedQueryRequest categoryTree(Long rootId) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setParentId(rootId);
        request.setIncludeChildren(true);
        request.setChildrenDepth(5); // 最多5层
        request.setStatus(CategoryStatusEnum.ACTIVE);
        return request;
    }

    /**
     * 根据创建者查询
     */
    public static CategoryUnifiedQueryRequest byCreator(Long creatorId) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setCreatorId(creatorId);
        return request;
    }

    /**
     * 搜索分类
     */
    public static CategoryUnifiedQueryRequest search(String keyword) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setNameKeyword(keyword);
        request.setStatus(CategoryStatusEnum.ACTIVE);
        return request;
    }

    /**
     * 获取完整分类信息（包含所有关联数据）
     */
    public static CategoryUnifiedQueryRequest fullInfo(Long categoryId) {
        CategoryUnifiedQueryRequest request = new CategoryUnifiedQueryRequest();
        request.setCategoryId(categoryId);
        request.setIncludeChildren(true);
        request.setIncludeParent(true);
        request.setIncludeAncestors(true);
        request.setIncludeStatistics(true);
        return request;
    }

    // ===================== 链式设置方法 =====================

    /**
     * 设置包含子分类
     */
    public CategoryUnifiedQueryRequest withChildren() {
        this.includeChildren = true;
        return this;
    }

    /**
     * 设置包含父分类
     */
    public CategoryUnifiedQueryRequest withParent() {
        this.includeParent = true;
        return this;
    }

    /**
     * 设置包含祖先路径
     */
    public CategoryUnifiedQueryRequest withAncestors() {
        this.includeAncestors = true;
        return this;
    }

    /**
     * 设置包含统计信息
     */
    public CategoryUnifiedQueryRequest withStatistics() {
        this.includeStatistics = true;
        return this;
    }

    /**
     * 设置排序
     */
    public CategoryUnifiedQueryRequest orderBy(String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
        return this;
    }

    /**
     * 设置状态过滤
     */
    public CategoryUnifiedQueryRequest withStatus(CategoryStatusEnum status) {
        this.status = status;
        return this;
    }
} 