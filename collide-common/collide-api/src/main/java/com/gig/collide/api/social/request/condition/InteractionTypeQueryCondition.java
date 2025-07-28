package com.gig.collide.api.social.request.condition;

import com.gig.collide.api.social.constant.InteractionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于互动类型的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractionTypeQueryCondition implements SocialQueryCondition {
    
    /**
     * 互动类型
     */
    private InteractionTypeEnum interactionType;
    
    /**
     * 互动类型列表（多类型查询）
     */
    private List<InteractionTypeEnum> interactionTypes;
    
    /**
     * 是否只查询主动互动
     */
    private Boolean activeOnly;
    
    /**
     * 是否只查询被动互动
     */
    private Boolean passiveOnly;
    
    /**
     * 是否只查询可撤销的互动
     */
    private Boolean revocableOnly;
    
    /**
     * 构造单个类型查询条件
     */
    public InteractionTypeQueryCondition(InteractionTypeEnum interactionType) {
        this.interactionType = interactionType;
    }
    
    /**
     * 构造多类型查询条件
     */
    public static InteractionTypeQueryCondition ofTypes(List<InteractionTypeEnum> interactionTypes) {
        return new InteractionTypeQueryCondition(null, interactionTypes, null, null, null);
    }
    
    /**
     * 构造主动互动查询条件
     */
    public static InteractionTypeQueryCondition ofActiveOnly() {
        return new InteractionTypeQueryCondition(null, null, true, null, null);
    }
    
    /**
     * 构造被动互动查询条件
     */
    public static InteractionTypeQueryCondition ofPassiveOnly() {
        return new InteractionTypeQueryCondition(null, null, null, true, null);
    }
    
    /**
     * 构造可撤销互动查询条件
     */
    public static InteractionTypeQueryCondition ofRevocableOnly() {
        return new InteractionTypeQueryCondition(null, null, null, null, true);
    }
} 