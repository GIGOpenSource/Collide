package com.gig.collide.api.category.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类统一响应对象 - 简洁版
 * 基于category-simple.sql的字段结构，支持层级分类
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
public class CategoryResponse implements Serializable {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 父分类ID，0表示顶级分类
     */
    private Long parentId;

    /**
     * 分类图标URL
     */
    private String iconUrl;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 内容数量（冗余统计）
     */
    private Long contentCount;

    /**
     * 状态：active、inactive
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // =================== 扩展字段（用于树形结构） ===================

    /**
     * 子分类列表（用于树形展示）
     */
    private List<CategoryResponse> children;

    /**
     * 分类层级深度
     */
    private Integer level;

    /**
     * 分类路径（如：一级分类/二级分类/三级分类）
     */
    private String path;

    /**
     * 父分类名称
     */
    private String parentName;

    // =================== 计算属性 ===================

    /**
     * 是否为激活状态
     */
    @JsonIgnore
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 是否已停用
     */
    @JsonIgnore
    public boolean isInactive() {
        return "inactive".equals(status);
    }

    /**
     * 是否为根分类
     */
    @JsonIgnore
    public boolean isRootCategory() {
        return parentId == null || parentId == 0;
    }

    /**
     * 是否为子分类
     */
    @JsonIgnore
    public boolean isChildCategory() {
        return !isRootCategory();
    }

    /**
     * 是否有内容
     */
    @JsonIgnore
    public boolean hasContent() {
        return contentCount != null && contentCount > 0;
    }

    /**
     * 是否有子分类
     */
    @JsonIgnore
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 是否为叶子分类（没有子分类）
     */
    @JsonIgnore
    public boolean isLeafCategory() {
        return !hasChildren();
    }

    /**
     * 获取子分类数量
     */
    @JsonIgnore
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * 获取内容数量（安全方法）
     */
    @JsonIgnore
    public long getSafeContentCount() {
        return contentCount != null ? contentCount : 0L;
    }

    /**
     * 获取分类热度评分
     * 基于内容数量和子分类数量
     */
    @JsonIgnore
    public double getPopularityScore() {
        double contentScore = getSafeContentCount() * 1.0;
        double childrenScore = getChildrenCount() * 0.5;
        
        return contentScore + childrenScore;
    }

    /**
     * 获取分类创建天数
     */
    @JsonIgnore
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 获取分类名称长度
     */
    @JsonIgnore
    public int getNameLength() {
        return name != null ? name.length() : 0;
    }

    /**
     * 是否为长名称（超过10个字符）
     */
    @JsonIgnore
    public boolean isLongName() {
        return getNameLength() > 10;
    }

    /**
     * 获取描述摘要（前50个字符）
     */
    @JsonIgnore
    public String getDescriptionSummary() {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }
        String trimmed = description.trim();
        return trimmed.length() > 50 ? trimmed.substring(0, 50) + "..." : trimmed;
    }

    /**
     * 是否有描述
     */
    @JsonIgnore
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    /**
     * 是否有图标
     */
    @JsonIgnore
    public boolean hasIcon() {
        return iconUrl != null && !iconUrl.trim().isEmpty();
    }

    // =================== 树形结构方法 ===================

    /**
     * 初始化子分类列表
     */
    public void initChildren() {
        if (this.children == null) {
            this.children = new java.util.ArrayList<>();
        }
    }

    /**
     * 添加子分类
     */
    public void addChild(CategoryResponse child) {
        initChildren();
        if (child != null) {
            child.setLevel((this.level != null ? this.level : 0) + 1);
            child.setParentName(this.name);
            this.children.add(child);
        }
    }

    /**
     * 移除子分类
     */
    public boolean removeChild(Long childId) {
        if (children == null || childId == null) {
            return false;
        }
        return children.removeIf(child -> childId.equals(child.getId()));
    }

    /**
     * 查找子分类
     */
    public CategoryResponse findChild(Long childId) {
        if (children == null || childId == null) {
            return null;
        }
        return children.stream()
                .filter(child -> childId.equals(child.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据名称查找子分类
     */
    public CategoryResponse findChildByName(String childName) {
        if (children == null || childName == null) {
            return null;
        }
        return children.stream()
                .filter(child -> childName.equals(child.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 构建分类路径
     */
    public void buildPath(String separator) {
        if (parentName != null && !parentName.isEmpty()) {
            this.path = parentName + separator + this.name;
        } else {
            this.path = this.name;
        }
    }

    /**
     * 递归查找分类
     */
    @JsonIgnore
    public CategoryResponse findDescendant(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        if (categoryId.equals(this.id)) {
            return this;
        }
        
        if (children != null) {
            for (CategoryResponse child : children) {
                CategoryResponse found = child.findDescendant(categoryId);
                if (found != null) {
                    return found;
                }
            }
        }
        
        return null;
    }

    /**
     * 获取所有叶子分类
     */
    @JsonIgnore
    public List<CategoryResponse> getLeafCategories() {
        List<CategoryResponse> leaves = new java.util.ArrayList<>();
        collectLeafCategories(leaves);
        return leaves;
    }

    /**
     * 收集叶子分类（递归）
     */
    @JsonIgnore
    private void collectLeafCategories(List<CategoryResponse> leaves) {
        if (isLeafCategory()) {
            leaves.add(this);
        } else if (children != null) {
            for (CategoryResponse child : children) {
                child.collectLeafCategories(leaves);
            }
        }
    }

    /**
     * 统计总内容数量（包含子分类）
     */
    @JsonIgnore
    public long getTotalContentCount() {
        long total = getSafeContentCount();
        
        if (children != null) {
            for (CategoryResponse child : children) {
                total += child.getTotalContentCount();
            }
        }
        
        return total;
    }
}