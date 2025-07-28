package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态ID的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostIdQueryCondition implements SocialQueryCondition {
    
    /**
     * 动态ID
     */
    private Long postId;
    
    /**
     * 动态ID列表（批量查询）
     */
    private List<Long> postIds;
    
    /**
     * 构造单个动态ID查询条件
     */
    public PostIdQueryCondition(Long postId) {
        this.postId = postId;
    }
    
    /**
     * 构造批量动态ID查询条件
     */
    public static PostIdQueryCondition ofIds(List<Long> postIds) {
        return new PostIdQueryCondition(null, postIds);
    }
} 