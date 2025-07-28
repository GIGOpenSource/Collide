package com.gig.collide.api.like.request.condition;

import com.gig.collide.api.like.constant.ActionTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 操作类型查询条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeActionTypeQueryCondition implements LikeQueryCondition {

    /**
     * 操作类型
     */
    private ActionTypeEnum actionType;

    /**
     * 操作类型列表（多操作类型查询）
     */
    private List<ActionTypeEnum> actionTypes;

    public LikeActionTypeQueryCondition(ActionTypeEnum actionType) {
        this.actionType = actionType;
    }

    public LikeActionTypeQueryCondition(List<ActionTypeEnum> actionTypes) {
        this.actionTypes = actionTypes;
    }

    @Override
    public boolean isValid() {
        return actionType != null || (actionTypes != null && !actionTypes.isEmpty());
    }
} 