package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 标签类型查询条件
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
public class TagTypeQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 标签类型
     */
    private String tagType;

    /**
     * 标签类型列表（支持多类型查询）
     */
    private List<String> tagTypes;
} 