package com.gig.collide.api.social.request.condition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态内容的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostContentQueryCondition implements SocialQueryCondition {
    
    /**
     * 内容关键词
     */
    private String keyword;
    
    /**
     * 内容关键词列表（多关键词查询）
     */
    private List<String> keywords;
    
    /**
     * 话题标签
     */
    private String topic;
    
    /**
     * 话题标签列表（多话题查询）
     */
    private List<String> topics;
    
    /**
     * 提及的用户ID
     */
    private Long mentionedUserId;
    
    /**
     * 是否模糊匹配
     */
    private Boolean fuzzyMatch;
    
    /**
     * 构造关键词查询条件
     */
    public PostContentQueryCondition(String keyword) {
        this.keyword = keyword;
        this.fuzzyMatch = true;
    }
    
    /**
     * 构造话题查询条件
     */
    public static PostContentQueryCondition ofTopic(String topic) {
        return new PostContentQueryCondition(null, null, topic, null, null, null);
    }
    
    /**
     * 构造多话题查询条件
     */
    public static PostContentQueryCondition ofTopics(List<String> topics) {
        return new PostContentQueryCondition(null, null, null, topics, null, null);
    }
    
    /**
     * 构造提及用户查询条件
     */
    public static PostContentQueryCondition ofMentionedUser(Long mentionedUserId) {
        return new PostContentQueryCondition(null, null, null, null, mentionedUserId, null);
    }
    
    /**
     * 构造精确匹配查询条件
     */
    public static PostContentQueryCondition ofExactKeyword(String keyword) {
        return new PostContentQueryCondition(keyword, null, null, null, null, false);
    }
} 