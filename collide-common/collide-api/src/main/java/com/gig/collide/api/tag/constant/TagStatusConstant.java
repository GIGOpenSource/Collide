package com.gig.collide.api.tag.constant;

/**
 * 标签状态常量
 * 
 * @author GIG Team
 * @version 1.0.0
 */
public class TagStatusConstant {

    /**
     * 启用状态
     */
    public static final Integer ACTIVE = 1;

    /**
     * 禁用状态
     */
    public static final Integer INACTIVE = 0;

    /**
     * 状态描述映射
     */
    public static String getStatusDesc(Integer status) {
        if (ACTIVE.equals(status)) {
            return "启用";
        } else if (INACTIVE.equals(status)) {
            return "禁用";
        }
        return "未知";
    }

    /**
     * 验证状态值是否有效
     */
    public static boolean isValidStatus(Integer status) {
        return ACTIVE.equals(status) || INACTIVE.equals(status);
    }
}