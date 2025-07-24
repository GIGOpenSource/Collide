package com.gig.collide.api.tag.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户兴趣标签请求
 *
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestTagRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 单个标签ID（用于关注/取消关注单个标签）
     */
    private Long tagId;

    /**
     * 多个标签ID列表（用于批量操作）
     */
    private List<Long> tagIds;

    /**
     * 来源：manual-手动添加，behavior-行为分析，recommendation-推荐算法
     */
    private String source;

    /**
     * 兴趣标签列表
     */
    private List<UserInterestTagItem> interestTags;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInterestTagItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 标签ID
         */
        private Long tagId;

        /**
         * 兴趣分数
         */
        private Double interestScore;

        /**
         * 来源：manual-手动添加，behavior-行为分析，recommendation-推荐算法
         */
        private String source;
    }
    
    /**
     * 获取单个标签ID
     */
    public Long getTagId() {
        return tagId;
    }
    
    /**
     * 获取多个标签ID列表
     */
    public List<Long> getTagIds() {
        return tagIds;
    }
    
    /**
     * 获取来源
     */
    public String getSource() {
        return source;
    }
    
    /**
     * 获取用户ID
     */
    public Long getUserId() {
        return userId;
    }
    
    /**
     * 获取兴趣标签列表
     */
    public List<UserInterestTagItem> getInterestTags() {
        return interestTags;
    }
} 