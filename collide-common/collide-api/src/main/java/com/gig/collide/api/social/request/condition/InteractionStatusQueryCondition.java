package com.gig.collide.api.social.request.condition;

import com.gig.collide.api.social.constant.InteractionStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于互动状态的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractionStatusQueryCondition implements SocialQueryCondition {
    
    /**
     * 互动状态
     */
    private InteractionStatusEnum interactionStatus;
    
    /**
     * 互动状态列表（多状态查询）
     */
    private List<InteractionStatusEnum> interactionStatuses;
    
    /**
     * 是否只查询有效状态
     */
    private Boolean activeOnly;
    
    /**
     * 是否只查询已取消状态
     */
    private Boolean cancelledOnly;
    
    /**
     * 是否只查询计入统计的互动
     */
    private Boolean statisticsOnly;
    
    /**
     * 构造单个状态查询条件
     */
    public InteractionStatusQueryCondition(InteractionStatusEnum interactionStatus) {
        this.interactionStatus = interactionStatus;
    }
    
    /**
     * 构造多状态查询条件
     */
    public static InteractionStatusQueryCondition ofStatuses(List<InteractionStatusEnum> interactionStatuses) {
        return new InteractionStatusQueryCondition(null, interactionStatuses, null, null, null);
    }
    
    /**
     * 构造有效状态查询条件
     */
    public static InteractionStatusQueryCondition ofActiveOnly() {
        return new InteractionStatusQueryCondition(null, null, true, null, null);
    }
    
    /**
     * 构造已取消状态查询条件
     */
    public static InteractionStatusQueryCondition ofCancelledOnly() {
        return new InteractionStatusQueryCondition(null, null, null, true, null);
    }
    
    /**
     * 构造计入统计查询条件
     */
    public static InteractionStatusQueryCondition ofStatisticsOnly() {
        return new InteractionStatusQueryCondition(null, null, null, null, true);
    }
} 