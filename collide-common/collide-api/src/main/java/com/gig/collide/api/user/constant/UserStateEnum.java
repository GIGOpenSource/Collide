package com.gig.collide.api.user.constant;

/**
 * 用户状态枚举
 * 与users模块保持一致
 * 
 * @author Collide Team  
 * @version 1.0
 * @since 2024-01-01
 */
public enum UserStateEnum {
    /**
     * 正常状态
     */
    active,
    
    /**
     * 未激活
     */
    inactive,
    
    /**
     * 已封禁
     */
    banned;
}
