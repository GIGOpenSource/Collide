package com.gig.collide.api.tag.request.condition;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户兴趣标签查询条件
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
public class UserInterestTagQueryCondition implements TagQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户ID列表
     */
    private List<Long> userIds;

    /**
     * 兴趣来源
     */
    private String source;

    /**
     * 兴趣来源列表
     */
    private List<String> sources;

    /**
     * 最小兴趣分数
     */
    private BigDecimal minInterestScore;

    /**
     * 最大兴趣分数
     */
    private BigDecimal maxInterestScore;

    /**
     * 状态
     */
    private String status;
} 