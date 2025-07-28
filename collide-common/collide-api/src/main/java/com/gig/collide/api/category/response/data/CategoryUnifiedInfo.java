package com.gig.collide.api.category.response.data;

import com.gig.collide.api.category.enums.CategoryStatusEnum;
import com.gig.collide.api.category.enums.CategoryLevelEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类统一信息传输对象
 * 包含完整的分类信息和统计数据
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryUnifiedInfo extends BasicCategoryInfo {

    private static final long serialVersionUID = 1L;

    /**
     * 分类状态
     */
    private CategoryStatusEnum status;

    /**
     * 内容数量
     */
    private Long contentCount;

    /**
     * 版本号（乐观锁）
     */
    private Integer version;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 最后修改者ID
     */
    private Long lastModifierId;

    /**
     * 最后修改者名称
     */
    private String lastModifierName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 子分类列表
     */
    private List<CategoryUnifiedInfo> children;

    /**
     * 父分类信息
     */
    private BasicCategoryInfo parent;

    /**
     * 祖先分类路径列表（从根到直接父分类）
     */
    private List<BasicCategoryInfo> ancestors;

    /**
     * 是否为叶子节点（没有子分类）
     */
    private Boolean isLeaf;

    /**
     * 是否为根节点（没有父分类）
     */
    private Boolean isRoot;

    /**
     * 子分类数量
     */
    private Integer childrenCount;

    /**
     * 当前分类及其所有子分类的内容总数
     */
    private Long totalContentCount;

    /**
     * 判断分类是否激活
     *
     * @return true如果分类激活
     */
    public boolean isActive() {
        return status != null && status.isActive();
    }

    /**
     * 判断分类是否禁用
     *
     * @return true如果分类禁用
     */
    public boolean isInactive() {
        return status != null && status.isInactive();
    }

    /**
     * 判断分类是否可用
     *
     * @return true如果分类可用
     */
    public boolean isAvailable() {
        return status != null && status.isAvailable();
    }

    /**
     * 获取分类层级枚举
     *
     * @return 分类层级枚举
     */
    public CategoryLevelEnum getLevelEnum() {
        return getLevel() != null ? CategoryLevelEnum.getByLevel(getLevel()) : null;
    }

    /**
     * 判断是否为根分类
     *
     * @return true如果为根分类
     */
    public boolean isRootCategory() {
        return getParentId() != null && getParentId() == 0L;
    }

    /**
     * 判断是否为叶子分类
     *
     * @return true如果为叶子分类
     */
    public boolean isLeafCategory() {
        return isLeaf != null && isLeaf;
    }

    /**
     * 判断是否可以添加子分类
     *
     * @return true如果可以添加子分类
     */
    public boolean canHaveChildren() {
        CategoryLevelEnum levelEnum = getLevelEnum();
        return levelEnum != null && levelEnum.canHaveChildren();
    }

    /**
     * 获取完整的分类路径（包含所有祖先的名称）
     *
     * @return 完整路径字符串
     */
    public String getFullPath() {
        if (getPath() == null) {
            return getName();
        }
        return getPath();
    }

    /**
     * 获取分类深度（从根分类开始计算）
     *
     * @return 分类深度
     */
    public int getDepth() {
        return getLevel() != null ? getLevel() - 1 : 0;
    }

    /**
     * 计算分类活跃度（基于内容数量和更新时间）
     *
     * @return 活跃度分数
     */
    public double calculateActivityScore() {
        if (!isActive()) {
            return 0.0;
        }

        long contentScore = contentCount != null ? contentCount : 0;
        
        // 基于更新时间的新鲜度评分
        double freshnessScore = 1.0;
        if (updateTime != null) {
            long daysSinceUpdate = java.time.Duration.between(updateTime, LocalDateTime.now()).toDays();
            freshnessScore = Math.max(0.1, 1.0 - (daysSinceUpdate * 0.01));
        }

        return contentScore * freshnessScore;
    }

    /**
     * 检查是否为热门分类（内容数量超过阈值）
     *
     * @param threshold 热门阈值
     * @return true如果为热门分类
     */
    public boolean isPopular(long threshold) {
        return contentCount != null && contentCount >= threshold;
    }
} 