package com.gig.collide.category.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类实体 - 简洁版
 * 基于category-simple.sql的t_category表设计
 * 支持层级分类结构和冗余统计字段
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
@TableName("t_category")
public class Category {

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 分类描述
     */
    @TableField("description")
    private String description;

    /**
     * 父分类ID，0表示顶级分类
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 分类图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 排序值
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 内容数量（冗余统计）
     */
    @TableField("content_count")
    private Long contentCount;

    /**
     * 状态：active、inactive
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    // =================== 扩展字段（用于树形结构） ===================

    /**
     * 子分类列表（用于树形展示，不映射到数据库）
     */
    @TableField(exist = false)
    private List<Category> children;

    /**
     * 分类层级深度（不映射到数据库）
     */
    @TableField(exist = false)
    private Integer level;

    /**
     * 父分类名称（不映射到数据库）
     */
    @TableField(exist = false)
    private String parentName;

    // =================== 业务方法 ===================

    /**
     * 是否为激活状态
     */
    public boolean isActive() {
        return "active".equals(status);
    }

    /**
     * 是否已停用
     */
    public boolean isInactive() {
        return "inactive".equals(status);
    }

    /**
     * 是否为根分类
     */
    public boolean isRootCategory() {
        return parentId == null || parentId == 0;
    }

    /**
     * 是否为子分类
     */
    public boolean isChildCategory() {
        return !isRootCategory();
    }

    /**
     * 激活分类
     */
    public void activate() {
        this.status = "active";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 停用分类
     */
    public void deactivate() {
        this.status = "inactive";
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新内容数量
     */
    public void updateContentCount(long increment) {
        if (this.contentCount == null) {
            this.contentCount = 0L;
        }
        this.contentCount = Math.max(0, this.contentCount + increment);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加内容数量
     */
    public void increaseContentCount(long increment) {
        updateContentCount(Math.max(0, increment));
    }

    /**
     * 减少内容数量
     */
    public void decreaseContentCount(long decrement) {
        updateContentCount(-Math.max(0, decrement));
    }

    /**
     * 更新基本信息
     */
    public void updateInfo(String newName, String newDescription, String newIconUrl) {
        if (newName != null && !newName.trim().isEmpty()) {
            this.name = newName.trim();
        }
        if (newDescription != null) {
            this.description = newDescription.trim();
        }
        if (newIconUrl != null) {
            this.iconUrl = newIconUrl.trim();
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新排序值
     */
    public void updateSort(Integer newSort) {
        if (newSort != null && newSort >= 0) {
            this.sort = newSort;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 移动到新父分类
     */
    public void moveToParent(Long newParentId) {
        if (newParentId != null && newParentId >= 0) {
            this.parentId = newParentId;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 是否有内容
     */
    public boolean hasContent() {
        return contentCount != null && contentCount > 0;
    }

    /**
     * 是否有子分类
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * 是否为叶子分类（没有子分类）
     */
    public boolean isLeafCategory() {
        return !hasChildren();
    }

    /**
     * 获取子分类数量
     */
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }

    /**
     * 获取内容数量（安全方法）
     */
    public long getSafeContentCount() {
        return contentCount != null ? contentCount : 0L;
    }

    /**
     * 获取分类名称长度
     */
    public int getNameLength() {
        return name != null ? name.length() : 0;
    }

    /**
     * 是否为长名称（超过10个字符）
     */
    public boolean isLongName() {
        return getNameLength() > 10;
    }

    /**
     * 获取描述摘要（前50个字符）
     */
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
    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }

    /**
     * 是否有图标
     */
    public boolean hasIcon() {
        return iconUrl != null && !iconUrl.trim().isEmpty();
    }

    /**
     * 获取分类热度评分
     * 基于内容数量和子分类数量
     */
    public double getPopularityScore() {
        double contentScore = getSafeContentCount() * 1.0;
        double childrenScore = getChildrenCount() * 0.5;
        
        return contentScore + childrenScore;
    }

    /**
     * 获取分类创建天数
     */
    public long getCreateDays() {
        if (createTime == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(createTime, LocalDateTime.now());
    }

    /**
     * 是否可以被删除
     */
    public boolean canBeDeleted() {
        return isActive() && !hasContent() && !hasChildren();
    }

    /**
     * 是否可以被停用
     */
    public boolean canBeDeactivated() {
        return isActive();
    }

    /**
     * 是否可以被激活
     */
    public boolean canBeActivated() {
        return isInactive();
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
    public void addChild(Category child) {
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
    public Category findChild(Long childId) {
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
    public Category findChildByName(String childName) {
        if (children == null || childName == null) {
            return null;
        }
        return children.stream()
                .filter(child -> childName.equals(child.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 递归查找分类
     */
    public Category findDescendant(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        
        if (categoryId.equals(this.id)) {
            return this;
        }
        
        if (children != null) {
            for (Category child : children) {
                Category found = child.findDescendant(categoryId);
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
    public List<Category> getLeafCategories() {
        List<Category> leaves = new java.util.ArrayList<>();
        collectLeafCategories(leaves);
        return leaves;
    }

    /**
     * 收集叶子分类（递归）
     */
    private void collectLeafCategories(List<Category> leaves) {
        if (isLeafCategory()) {
            leaves.add(this);
        } else if (children != null) {
            for (Category child : children) {
                child.collectLeafCategories(leaves);
            }
        }
    }

    /**
     * 统计总内容数量（包含子分类）
     */
    public long getTotalContentCount() {
        long total = getSafeContentCount();
        
        if (children != null) {
            for (Category child : children) {
                total += child.getTotalContentCount();
            }
        }
        
        return total;
    }

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.parentId == null) {
            this.parentId = 0L;
        }
        if (this.sort == null) {
            this.sort = 0;
        }
        if (this.contentCount == null) {
            this.contentCount = 0L;
        }
        if (this.status == null) {
            this.status = "active";
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 验证分类层级循环引用
     */
    public boolean wouldCreateCycle(Long newParentId) {
        if (newParentId == null || newParentId == 0 || newParentId.equals(this.id)) {
            return false;
        }
        
        // 递归检查是否会形成循环引用
        return isDescendantOf(newParentId);
    }
    
    /**
     * 检查是否为指定分类的后代
     */
    private boolean isDescendantOf(Long ancestorId) {
        Category current = this;
        while (current != null && current.getParentId() != null && current.getParentId() != 0) {
            if (ancestorId.equals(current.getParentId())) {
                return true;
            }
            // 这里需要从数据库查询父分类，但为了简化，暂时返回false
            // 实际使用时应该通过Service层来检查
            break;
        }
        return false;
    }
}