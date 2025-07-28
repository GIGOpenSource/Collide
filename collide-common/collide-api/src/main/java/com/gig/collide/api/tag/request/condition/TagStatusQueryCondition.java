package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.util.List;

/**
 * 标签状态查询条件
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
public class TagStatusQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 标签状态
     */
    private String status;

    /**
     * 标签状态列表（支持多状态查询）
     */
    private List<String> statuses;
} 