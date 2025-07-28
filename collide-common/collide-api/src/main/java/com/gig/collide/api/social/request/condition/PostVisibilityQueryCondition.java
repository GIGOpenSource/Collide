package com.gig.collide.api.social.request.condition;

import com.gig.collide.api.social.constant.VisibilityEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 基于动态可见性的查询条件
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostVisibilityQueryCondition implements SocialQueryCondition {
    
    /**
     * 可见性级别
     */
    private VisibilityEnum visibility;
    
    /**
     * 可见性级别列表（多级别查询）
     */
    private List<VisibilityEnum> visibilities;
    
    /**
     * 是否只查询公开动态
     */
    private Boolean publicOnly;
    
    /**
     * 当前查看用户ID（用于权限判断）
     */
    private Long viewerUserId;
    
    /**
     * 构造单个可见性查询条件
     */
    public PostVisibilityQueryCondition(VisibilityEnum visibility) {
        this.visibility = visibility;
    }
    
    /**
     * 构造多可见性查询条件
     */
    public static PostVisibilityQueryCondition ofVisibilities(List<VisibilityEnum> visibilities) {
        return new PostVisibilityQueryCondition(null, visibilities, null, null);
    }
    
    /**
     * 构造公开动态查询条件
     */
    public static PostVisibilityQueryCondition ofPublicOnly() {
        return new PostVisibilityQueryCondition(null, null, true, null);
    }
    
    /**
     * 构造带查看用户的查询条件
     */
    public static PostVisibilityQueryCondition ofViewer(Long viewerUserId) {
        return new PostVisibilityQueryCondition(null, null, null, viewerUserId);
    }
} 