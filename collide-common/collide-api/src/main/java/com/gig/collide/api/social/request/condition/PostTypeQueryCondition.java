package com.gig.collide.api.social.request.condition;

import com.gig.collide.api.social.constant.PostTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态类型的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostTypeQueryCondition implements SocialQueryCondition {
    
    /**
     * 动态类型
     */
    private PostTypeEnum postType;
    
    /**
     * 动态类型列表（多类型查询）
     */
    private List<PostTypeEnum> postTypes;
    
    /**
     * 是否只查询媒体类型（图片、视频）
     */
    private Boolean mediaOnly;
    
    /**
     * 是否只查询原创内容（排除转发）
     */
    private Boolean originalOnly;
    
    /**
     * 构造单个类型查询条件
     */
    public PostTypeQueryCondition(PostTypeEnum postType) {
        this.postType = postType;
    }
    
    /**
     * 构造多类型查询条件
     */
    public static PostTypeQueryCondition ofTypes(List<PostTypeEnum> postTypes) {
        return new PostTypeQueryCondition(null, postTypes, null, null);
    }
    
    /**
     * 构造媒体类型查询条件
     */
    public static PostTypeQueryCondition ofMediaOnly() {
        return new PostTypeQueryCondition(null, null, true, null);
    }
    
    /**
     * 构造原创内容查询条件
     */
    public static PostTypeQueryCondition ofOriginalOnly() {
        return new PostTypeQueryCondition(null, null, null, true);
    }
} 