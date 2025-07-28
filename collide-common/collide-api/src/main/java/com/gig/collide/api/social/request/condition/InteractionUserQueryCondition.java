package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于互动用户的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InteractionUserQueryCondition implements SocialQueryCondition {
    
    /**
     * 互动用户ID
     */
    private Long userId;
    
    /**
     * 互动用户ID列表（批量查询）
     */
    private List<Long> userIds;
    
    /**
     * 动态作者ID（查询谁的动态被互动）
     */
    private Long postAuthorId;
    
    /**
     * 动态作者ID列表（批量查询）
     */
    private List<Long> postAuthorIds;
    
    /**
     * 构造单个用户ID查询条件
     */
    public InteractionUserQueryCondition(Long userId) {
        this.userId = userId;
    }
    
    /**
     * 构造批量用户ID查询条件
     */
    public static InteractionUserQueryCondition ofUserIds(List<Long> userIds) {
        return new InteractionUserQueryCondition(null, userIds, null, null);
    }
    
    /**
     * 构造动态作者查询条件
     */
    public static InteractionUserQueryCondition ofPostAuthor(Long postAuthorId) {
        return new InteractionUserQueryCondition(null, null, postAuthorId, null);
    }
    
    /**
     * 构造批量动态作者查询条件
     */
    public static InteractionUserQueryCondition ofPostAuthors(List<Long> postAuthorIds) {
        return new InteractionUserQueryCondition(null, null, null, postAuthorIds);
    }
} 