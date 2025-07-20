package com.gig.collide.api.follow.response.data;

import com.gig.collide.api.follow.constant.FollowTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 关注信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class FollowInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关注ID
     */
    private Long followId;

    /**
     * 关注者用户ID
     */
    private Long followerUserId;

    /**
     * 被关注者用户ID
     */
    private Long followedUserId;

    /**
     * 被关注者用户名
     */
    private String followedUserName;

    /**
     * 被关注者头像
     */
    private String followedUserAvatar;

    /**
     * 被关注者简介
     */
    private String followedUserBio;

    /**
     * 关注类型
     */
    private FollowTypeEnum followType;

    /**
     * 是否互相关注
     */
    private Boolean isMutualFollow;

    /**
     * 关注时间
     */
    private Date followTime;

    /**
     * 被关注者粉丝数
     */
    private Integer followerCount;

    /**
     * 被关注者关注数
     */
    private Integer followingCount;
} 