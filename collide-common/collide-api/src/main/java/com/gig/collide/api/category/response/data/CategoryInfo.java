package com.gig.collide.api.category.response.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类信息
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类ID（兼容性字段）
     */
    private Long categoryId;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 分类图标URL
     */
    private String iconUrl;

    /**
     * 分类封面URL
     */
    private String coverUrl;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 排序值（兼容性字段）
     */
    private Integer sort;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 内容数量
     */
    private Long contentCount;

    /**
     * 分类状态（字符串类型）
     */
    private String status;

    /**
     * 分类状态（整数类型，兼容性字段）
     */
    private Integer statusInt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 设置分类ID（同时设置id和categoryId）
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        this.id = categoryId;
    }

    /**
     * 获取分类ID（优先返回categoryId，然后是id）
     */
    public Long getCategoryId() {
        return categoryId != null ? categoryId : id;
    }

    /**
     * 设置排序值（同时设置sort和sortOrder）
     */
    public void setSort(Integer sort) {
        this.sort = sort;
        this.sortOrder = sort;
    }

    /**
     * 获取排序值（优先返回sort，然后是sortOrder）
     */
    public Integer getSort() {
        return sort != null ? sort : sortOrder;
    }

    /**
     * 设置状态（同时设置字符串和整数类型）
     */
    public void setStatus(Object status) {
        if (status instanceof String) {
            this.status = (String) status;
        } else if (status instanceof Integer) {
            this.statusInt = (Integer) status;
            this.status = String.valueOf(status);
        }
    }
} 