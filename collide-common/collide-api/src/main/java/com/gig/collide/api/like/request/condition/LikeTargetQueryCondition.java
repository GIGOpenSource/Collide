package com.gig.collide.api.like.request.condition;

import com.gig.collide.api.like.constant.TargetTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 目标对象查询条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeTargetQueryCondition implements LikeQueryCondition {

    /**
     * 目标对象ID
     */
    private Long targetId;

    /**
     * 目标类型
     */
    private TargetTypeEnum targetType;

    /**
     * 目标对象ID列表（批量查询）
     */
    private List<Long> targetIds;

    /**
     * 目标类型列表（多类型查询）
     */
    private List<TargetTypeEnum> targetTypes;

    public LikeTargetQueryCondition(Long targetId, TargetTypeEnum targetType) {
        this.targetId = targetId;
        this.targetType = targetType;
    }

    public LikeTargetQueryCondition(List<Long> targetIds, TargetTypeEnum targetType) {
        this.targetIds = targetIds;
        this.targetType = targetType;
    }

    @Override
    public boolean isValid() {
        // 单个目标查询：需要目标ID和目标类型
        if (targetId != null && targetType != null) {
            return targetId > 0;
        }
        
        // 批量目标查询：需要目标ID列表和目标类型
        if (targetIds != null && targetType != null) {
            return !targetIds.isEmpty() && targetIds.stream().allMatch(id -> id != null && id > 0);
        }
        
        return false;
    }
} 