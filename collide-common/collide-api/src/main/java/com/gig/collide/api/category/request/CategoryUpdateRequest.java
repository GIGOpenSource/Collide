package com.gig.collide.api.category.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 更新分类请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

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
     * 分类状态
     */
    private String status;
    
    /**
     * 获取排序值（优先使用sort，然后是sortOrder）
     */
    public Integer getSort() {
        return sort != null ? sort : sortOrder;
    }
} 