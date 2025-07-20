package com.gig.collide.api.artist.response.data;

import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 博主申请信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class ArtistApplicationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID
     */
    private Long applicationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 申请类型
     */
    private ArtistApplicationType applicationType;

    /**
     * 申请状态
     */
    private ArtistStatus status;

    /**
     * 申请的博主名称
     */
    private String artistName;

    /**
     * 申请简介
     */
    private String bio;

    /**
     * 申请分类
     */
    private List<ArtistCategory> categories;

    /**
     * 申请理由
     */
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
    private String contactEmail;

    /**
     * 联系电话
     */
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
     * 审核员ID
     */
    private Long reviewerId;

    /**
     * 审核员姓名
     */
    private String reviewerName;

    /**
     * 审核结果
     */
    private ArtistReviewResult reviewResult;

    /**
     * 审核意见
     */
    private String reviewComment;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 申请提交时间
     */
    private Date submitTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 申请轮次（支持重新申请）
     */
    private Integer applicationRound;

    /**
     * 扩展信息
     */
    private String extInfo;
} 