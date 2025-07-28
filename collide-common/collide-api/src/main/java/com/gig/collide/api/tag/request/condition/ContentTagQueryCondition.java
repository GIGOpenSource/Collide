package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 内容标签查询条件
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
public class ContentTagQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容ID列表
     */
    private List<Long> contentIds;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签ID列表
     */
    private List<Long> tagIds;
} 