package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 标签分类查询条件
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagCategoryQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类ID列表（支持多分类查询）
     */
    private List<Long> categoryIds;
} 