package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 标签名称查询条件
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
public class TagNameQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签名称列表（支持多名称查询）
     */
    private List<String> names;

    /**
     * 标签名称模糊匹配
     */
    private String nameLike;
} 