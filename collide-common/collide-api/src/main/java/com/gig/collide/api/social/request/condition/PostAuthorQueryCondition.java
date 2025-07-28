package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态作者的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostAuthorQueryCondition implements SocialQueryCondition {
    
    /**
     * 作者用户ID
     */
    private Long authorId;
    
    /**
     * 作者用户名
     */
    private String authorUsername;
    
    /**
     * 作者用户ID列表（批量查询）
     */
    private List<Long> authorIds;
    
    /**
     * 是否只查询认证用户的动态
     */
    private Boolean verifiedOnly;
    
    /**
     * 构造单个作者ID查询条件
     */
    public PostAuthorQueryCondition(Long authorId) {
        this.authorId = authorId;
    }
    
    /**
     * 构造作者用户名查询条件
     */
    public PostAuthorQueryCondition(String authorUsername) {
        this.authorUsername = authorUsername;
    }
    
    /**
     * 构造批量作者ID查询条件
     */
    public static PostAuthorQueryCondition ofAuthorIds(List<Long> authorIds) {
        return new PostAuthorQueryCondition(null, null, authorIds, null);
    }
    
    /**
     * 构造仅认证用户查询条件
     */
    public static PostAuthorQueryCondition ofVerifiedOnly() {
        return new PostAuthorQueryCondition(null, null, null, true);
    }
} 