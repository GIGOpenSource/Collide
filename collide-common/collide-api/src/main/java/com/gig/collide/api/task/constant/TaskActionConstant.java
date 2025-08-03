package com.gig.collide.api.task.constant;

/**
 * 任务动作常量
 * 与优化后的 task-simple.sql 保持一致
 * 
 * @author GIG Team
 * @version 2.0.0 (优化版)
 * @since 2024-01-16
 */
public class TaskActionConstant {

    /**
     * 登录动作
     */
    public static final Integer LOGIN = 1;

    /**
     * 发布内容动作
     */
    public static final Integer PUBLISH_CONTENT = 2;

    /**
     * 点赞动作
     */
    public static final Integer LIKE = 3;

    /**
     * 评论动作
     */
    public static final Integer COMMENT = 4;

    /**
     * 分享动作
     */
    public static final Integer SHARE = 5;

    /**
     * 购买动作
     */
    public static final Integer PURCHASE = 6;

    /**
     * 邀请用户动作
     */
    public static final Integer INVITE_USER = 7;

    /**
     * 获取任务动作名称
     */
    public static String getActionName(Integer action) {
        if (action == null) return "未知";
        switch (action) {
            case 1: return "登录";
            case 2: return "发布内容";
            case 3: return "点赞";
            case 4: return "评论";
            case 5: return "分享";
            case 6: return "购买";
            case 7: return "邀请用户";
            default: return "未知";
        }
    }

    /**
     * 获取任务动作英文名称
     */
    public static String getActionKey(Integer action) {
        if (action == null) return "unknown";
        switch (action) {
            case 1: return "login";
            case 2: return "publish_content";
            case 3: return "like";
            case 4: return "comment";
            case 5: return "share";
            case 6: return "purchase";
            case 7: return "invite_user";
            default: return "unknown";
        }
    }

    /**
     * 验证任务动作是否有效
     */
    public static boolean isValidAction(Integer action) {
        return action != null && action >= 1 && action <= 7;
    }

    /**
     * 判断是否为登录动作
     */
    public static boolean isLoginAction(Integer action) {
        return LOGIN.equals(action);
    }

    /**
     * 判断是否为发布内容动作
     */
    public static boolean isPublishContentAction(Integer action) {
        return PUBLISH_CONTENT.equals(action);
    }

    /**
     * 判断是否为点赞动作
     */
    public static boolean isLikeAction(Integer action) {
        return LIKE.equals(action);
    }

    /**
     * 判断是否为评论动作
     */
    public static boolean isCommentAction(Integer action) {
        return COMMENT.equals(action);
    }

    /**
     * 判断是否为分享动作
     */
    public static boolean isShareAction(Integer action) {
        return SHARE.equals(action);
    }

    /**
     * 判断是否为购买动作
     */
    public static boolean isPurchaseAction(Integer action) {
        return PURCHASE.equals(action);
    }

    /**
     * 判断是否为邀请用户动作
     */
    public static boolean isInviteUserAction(Integer action) {
        return INVITE_USER.equals(action);
    }
}