package com.gig.collide.api.artist.request;

import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 博主信息更新请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArtistUpdateRequest extends BaseRequest {

    /**
     * 博主ID
     */
    @NotNull(message = "博主ID不能为空")
    private Long artistId;

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
     * 扩展信息
     */
    private String extInfo;

    /**
     * 更新原因
     */
    private String updateReason;
} 