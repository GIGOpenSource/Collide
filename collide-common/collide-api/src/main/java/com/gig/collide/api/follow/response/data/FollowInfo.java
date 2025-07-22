package com.gig.collide.api.follow.response.data;

import com.gig.collide.api.follow.constant.FollowStatus;
import com.gig.collide.api.follow.constant.FollowType;
import com.gig.collide.api.user.response.data.BasicUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 关注信息响应对象
 * 对应 t_follow 表结构
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    private Long id;

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;

    /**
     * 关注类型
     */
    private FollowType followType;

    /**
     * 关注状态
     */
    private FollowStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 关注者用户信息（用于列表展示）
     */
    private BasicUserInfo followerUser;

    /**
     * 被关注者用户信息（用于列表展示）
     */
    private BasicUserInfo followedUser;

    /**
     * 是否为相互关注
     */
    private Boolean mutualFollow;
} 