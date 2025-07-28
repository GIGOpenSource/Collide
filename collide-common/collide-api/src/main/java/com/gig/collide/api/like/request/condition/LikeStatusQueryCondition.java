package com.gig.collide.api.like.request.condition;

import com.gig.collide.api.like.constant.LikeStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 状态查询条件
 * 
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LikeStatusQueryCondition implements LikeQueryCondition {

    /**
     * 状态
     */
    private LikeStatusEnum status;

    /**
     * 状态列表（多状态查询）
     */
    private List<LikeStatusEnum> statuses;

    /**
     * 是否包含已删除记录
     */
    private Boolean includeDeleted;

    public LikeStatusQueryCondition(LikeStatusEnum status) {
        this.status = status;
    }

    public LikeStatusQueryCondition(List<LikeStatusEnum> statuses) {
        this.statuses = statuses;
    }

    @Override
    public boolean isValid() {
        return status != null || (statuses != null && !statuses.isEmpty());
    }
} 