package com.gig.collide.mq.constant;

/**
 * MQ常量类
 * 统一管理 Collide 项目的消息主题和标签
 *
 * @author Collide Team
 */
public class MqConstant {

    // ========== 评论模块相关 ==========
    /**
     * 评论创建主题
     */
    public static final String COMMENT_CREATED_TOPIC = "COMMENT_CREATED";
    
    /**
     * 评论删除主题
     */
    public static final String COMMENT_DELETED_TOPIC = "COMMENT_DELETED";
    
    /**
     * 评论点赞主题
     */
    public static final String COMMENT_LIKED_TOPIC = "COMMENT_LIKED";
    
    /**
     * 评论统计变更主题
     */
    public static final String COMMENT_STATISTICS_CHANGED_TOPIC = "COMMENT_STATISTICS_CHANGED";

    // ========== 内容模块相关 ==========
    /**
     * 内容创建主题
     */
    public static final String CONTENT_CREATED_TOPIC = "CONTENT_CREATED";
    
    /**
     * 内容更新主题
     */
    public static final String CONTENT_UPDATED_TOPIC = "CONTENT_UPDATED";
    
    /**
     * 内容删除主题
     */
    public static final String CONTENT_DELETED_TOPIC = "CONTENT_DELETED";

    // ========== 用户模块相关 ==========
    /**
     * 用户注册主题
     */
    public static final String USER_REGISTERED_TOPIC = "USER_REGISTERED";
    
    /**
     * 用户信息更新主题
     */
    public static final String USER_UPDATED_TOPIC = "USER_UPDATED";

    // ========== 关注模块相关 ==========
    /**
     * 关注主题
     */
    public static final String FOLLOW_TOPIC = "FOLLOW";
    
    /**
     * 取消关注主题
     */
    public static final String UNFOLLOW_TOPIC = "UNFOLLOW";

    // ========== 消息TAG常量 ==========
    /**
     * 创建标签
     */
    public static final String CREATE_TAG = "CREATE";
    
    /**
     * 更新标签
     */
    public static final String UPDATE_TAG = "UPDATE";
    
    /**
     * 删除标签
     */
    public static final String DELETE_TAG = "DELETE";
    
    /**
     * 统计标签
     */
    public static final String STATISTICS_TAG = "STATISTICS";
} 