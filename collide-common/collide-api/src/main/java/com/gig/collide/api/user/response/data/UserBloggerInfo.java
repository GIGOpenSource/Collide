package com.gig.collide.api.user.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博主信息传输对象
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class UserBloggerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 博主认证状态
     */
    private String bloggerStatus;

    /**
     * 专业领域
     */
    private String speciality;

    /**
     * 个人作品链接
     */
    private String portfolioUrl;

    /**
     * 社交媒体链接
     */
    private String socialMediaUrl;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    /**
     * 审批时间
     */
    private LocalDateTime approveTime;

    /**
     * 审批意见
     */
    private String approvalComment;
} 