package com.gig.collide.api.artist.request;

import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 博主申请请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArtistApplicationRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 申请类型
     */
    @NotNull(message = "申请类型不能为空")
    private ArtistApplicationType applicationType;

    /**
     * 申请的博主名称
     */
    @NotBlank(message = "博主名称不能为空")
    private String artistName;

    /**
     * 申请简介
     */
    @NotBlank(message = "申请简介不能为空")
    private String bio;

    /**
     * 申请分类
     */
    @NotNull(message = "申请分类不能为空")
    private List<ArtistCategory> categories;

    /**
     * 申请理由
     */
    @NotBlank(message = "申请理由不能为空")
    private String applyReason;

    /**
     * 个人简历/机构介绍
     */
    private String resume;

    /**
     * 作品集链接
     */
    private String portfolioUrl;

    /**
     * 社交媒体证明
     */
    private String socialProof;

    /**
     * 身份证明文件
     */
    private String identityProof;

    /**
     * 资质证明文件
     */
    private String qualificationProof;

    /**
     * 联系邮箱
     */
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "联系邮箱不能为空")
    private String contactEmail;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 期望合作类型
     */
    private String expectedCollaboration;

    /**
     * 粉丝数量证明
     */
    private String followersProof;

    /**
     * 是否同意协议
     */
    @NotNull(message = "必须同意用户协议")
    private Boolean agreeTerms;

    /**
     * 扩展信息
     */
    private String extInfo;
} 