package com.gig.collide.api.social.request.condition;

import com.gig.collide.api.social.constant.PostStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态状态的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostStatusQueryCondition implements SocialQueryCondition {
    
    /**
     * 动态状态
     */
    private PostStatusEnum status;
    
    /**
     * 动态状态列表（多状态查询）
     */
    private List<PostStatusEnum> statuses;
    
    /**
     * 是否只查询可见动态
     */
    private Boolean visibleOnly;
    
    /**
     * 是否排除已删除
     */
    private Boolean excludeDeleted;
    
    /**
     * 构造单个状态查询条件
     */
    public PostStatusQueryCondition(PostStatusEnum status) {
        this.status = status;
    }
    
    /**
     * 构造多状态查询条件
     */
    public static PostStatusQueryCondition ofStatuses(List<PostStatusEnum> statuses) {
        return new PostStatusQueryCondition(null, statuses, null, null);
    }
    
    /**
     * 构造可见动态查询条件
     */
    public static PostStatusQueryCondition ofVisibleOnly() {
        return new PostStatusQueryCondition(null, null, true, null);
    }
    
    /**
     * 构造排除已删除查询条件
     */
    public static PostStatusQueryCondition ofExcludeDeleted() {
        return new PostStatusQueryCondition(null, null, null, true);
    }
} 