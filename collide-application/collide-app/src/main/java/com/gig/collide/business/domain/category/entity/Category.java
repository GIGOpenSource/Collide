package com.gig.collide.business.domain.category.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容分类实体
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父分类ID，0表示根分类
     */
    @TableField("parent_id")
    private Long parentId;

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
     * 分类图标URL
     */
    @TableField("icon_url")
    private String iconUrl;

    /**
     * 分类封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 排序顺序
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 排序值（兼容性字段）
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 分类层级
     */
    @TableField("level")
    private Integer level;

    /**
     * 分类路径
     */
    @TableField("path")
    private String path;

    /**
     * 该分类下的内容数量
     */
    @TableField("content_count")
    private Long contentCount;

    /**
     * 分类状态（字符串类型）
     */
    @TableField("status")
    private String status;

    /**
     * 分类状态（整数类型，兼容性字段）
     */
    @TableField(exist = false)
    private Integer statusInt;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 子分类列表（用于构建分类树，不对应数据库字段）
     */
    @TableField(exist = false)
    private List<Category> children;

    /**
     * 获取排序值（优先返回sort，然后是sortOrder）
     */
    public Integer getSort() {
        return sort != null ? sort : sortOrder;
    }

    /**
     * 设置排序值（同时设置sort和sortOrder）
     */
    public void setSort(Integer sort) {
        this.sort = sort;
        this.sortOrder = sort;
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