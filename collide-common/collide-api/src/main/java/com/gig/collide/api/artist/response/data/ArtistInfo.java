package com.gig.collide.api.artist.response.data;

import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 博主信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ArtistInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 博主ID
     */
    private Long artistId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 博主名称
     */
    private String artistName;

    /**
     * 博主简介
     */
    private String bio;

    /**
     * 博主状态
     */
    private ArtistStatus status;

    /**
     * 博主类型
     */
    private ArtistApplicationType applicationType;

    /**
     * 博主分类
     */
    private List<ArtistCategory> categories;

    /**
     * 博主等级
     */
    private ArtistLevel level;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 个人主页URL
     */
    private String personalUrl;

    /**
     * 粉丝数量
     */
    private Integer followersCount;

    /**
     * 关注数量
     */
    private Integer followingCount;

    /**
     * 作品数量
     */
    private Integer worksCount;

    /**
     * 获赞总数
     */
    private Long totalLikes;

    /**
     * 总浏览量
     */
    private Long totalViews;

    /**
     * 是否认证
     */
    private Boolean verified;

    /**
     * 认证类型
     */
    private String verificationType;

    /**
     * 认证说明
     */
    private String verificationDesc;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 社交媒体账号
     */
    private String socialMediaAccounts;

    /**
     * 擅长领域标签
     */
    private List<String> expertiseTags;

    /**
     * 合作意向
     */
    private String collaborationIntent;

    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 扩展信息
     */
    private String extInfo;
} 