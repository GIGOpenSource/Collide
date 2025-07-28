package com.gig.collide.api.like.request.condition;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 用户ID查询条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeUserIdQueryCondition implements LikeQueryCondition {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户ID列表（批量查询）
     */
    private List<Long> userIds;

    public LikeUserIdQueryCondition(Long userId) {
        this.userId = userId;
    }

    public LikeUserIdQueryCondition(List<Long> userIds) {
        this.userIds = userIds;
    }

    @Override
    public boolean isValid() {
        return (userId != null && userId > 0) || 
               (userIds != null && !userIds.isEmpty() && userIds.stream().allMatch(id -> id != null && id > 0));
    }
} 