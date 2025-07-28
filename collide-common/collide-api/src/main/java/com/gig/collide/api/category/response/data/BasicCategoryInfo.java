package com.gig.collide.api.category.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础分类信息
 * 包含最基本的分类标识信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class BasicCategoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 分类图标URL
     */
    private String iconUrl;

    /**
     * 分类封面URL
     */
    private String coverUrl;

    /**
     * 父分类ID
     */
    private Long parentId;

    /**
     * 分类层级
     */
    private Integer level;

    /**
     * 分类路径
     */
    private String path;

    /**
     * 排序顺序
     */
    private Integer sortOrder;
} 