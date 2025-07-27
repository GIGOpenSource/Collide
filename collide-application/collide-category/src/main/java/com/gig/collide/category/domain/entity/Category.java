package com.gig.collide.category.domain.entity;

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
 * @author Collide Team
 * @version 1.0
 * @since 2024-12-19
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
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
     * 分类状态 (ACTIVE-激活, INACTIVE-禁用)
     */
    @TableField("status")
    private String status;

    /**
     * 版本号（用于乐观锁和幂等性控制）
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 创建者ID
     */
    @TableField("creator_id")
    private Long creatorId;

    /**
     * 创建者名称（冗余存储，避免连表查询）
     */
    @TableField("creator_name")
    private String creatorName;

    /**
     * 最后修改者ID
     */
    @TableField("last_modifier_id")
    private Long lastModifierId;

    /**
     * 最后修改者名称（冗余存储，避免连表查询）
     */
    @TableField("last_modifier_name")
    private String lastModifierName;

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
     * 分类状态枚举
     */
    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";
    }
} 