package com.gig.collide.artist.param;

import com.gig.collide.api.artist.constant.ArtistCategory;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 博主信息更新参数
 * @author GIG
 */
@Getter
@Setter
public class ArtistUpdateParam {

    /**
     * 博主名称
     */
    private String artistName;

    /**
     * 博主简介
     */
    private String bio;

    /**
     * 博主分类
     */
    private List<ArtistCategory> categories;

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
     * 联系邮箱
     */
    @Email(message = "邮箱格式不正确")
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
     * 更新原因
     */
    private String updateReason;
} 