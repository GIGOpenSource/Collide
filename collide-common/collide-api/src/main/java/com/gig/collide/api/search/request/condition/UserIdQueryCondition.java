package com.gig.collide.api.search.request.condition;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用户ID查询条件
 * 
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserIdQueryCondition extends SearchQueryCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Positive(message = "用户ID必须为正数")
    private Long userId;

    /**
     * 是否包含用户相关内容
     */
    private Boolean includeUserContent = true;

    /**
     * 是否包含用户评论
     */
    private Boolean includeUserComments = true;

    public UserIdQueryCondition(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getConditionType() {
        return "USER_ID";
    }

    @Override
    public boolean isValid() {
        return userId != null && userId > 0;
    }
} 