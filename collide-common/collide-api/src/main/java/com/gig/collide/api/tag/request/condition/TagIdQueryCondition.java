package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 标签ID查询条件
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
public class TagIdQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签ID列表（支持多标签查询）
     */
    private List<Long> tagIds;
}