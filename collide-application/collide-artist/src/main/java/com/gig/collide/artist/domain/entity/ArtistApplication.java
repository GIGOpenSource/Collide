package com.gig.collide.artist.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistReviewResult;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 博主申请实体
 * @author GIG
 */
@Setter
@Getter
@TableName("artist_application")
public class ArtistApplication extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

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
     * 申请分类（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String categories;

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
     * 审核评分（1-10分）
     */
    private Integer reviewScore;

    /**
     * 风险评估
     */
    private String riskAssessment;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 申请提交时间
     */
    private Date submitTime;

    /**
     * 申请轮次
     */
    private Integer applicationRound;

    /**
     * 扩展信息（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String extInfo;

    // ================== 业务方法 ==================

    /**
     * 提交申请
     */
    public void submit() {
        this.status = ArtistStatus.APPLYING;
        this.submitTime = new Date();
        if (this.applicationRound == null) {
            this.applicationRound = 1;
        }
    }

    /**
     * 进入审核状态
     */
    public void startReview(Long reviewerId, String reviewerName) {
        this.status = ArtistStatus.REVIEWING;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
    }

    /**
     * 审核通过
     */
    public void approve(Long reviewerId, String reviewerName, String reviewComment, Integer reviewScore) {
        this.reviewResult = ArtistReviewResult.APPROVED;
        this.status = ArtistStatus.ACTIVE;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewComment = reviewComment;
        this.reviewScore = reviewScore;
        this.reviewTime = new Date();
    }

    /**
     * 审核拒绝
     */
    public void reject(Long reviewerId, String reviewerName, String reviewComment, Integer reviewScore) {
        this.reviewResult = ArtistReviewResult.REJECTED;
        this.status = ArtistStatus.REJECTED;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewComment = reviewComment;
        this.reviewScore = reviewScore;
        this.reviewTime = new Date();
    }

    /**
     * 需要补充材料
     */
    public void needSupplement(Long reviewerId, String reviewerName, String reviewComment) {
        this.reviewResult = ArtistReviewResult.NEED_SUPPLEMENT;
        this.status = ArtistStatus.APPLYING;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewComment = reviewComment;
        this.reviewTime = new Date();
    }

    /**
     * 暂停审核
     */
    public void suspend(Long reviewerId, String reviewerName, String reviewComment) {
        this.reviewResult = ArtistReviewResult.SUSPENDED;
        this.status = ArtistStatus.SUSPENDED;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName;
        this.reviewComment = reviewComment;
        this.reviewTime = new Date();
    }

    /**
     * 撤回申请
     */
    public void withdraw() {
        this.status = ArtistStatus.REJECTED;
    }

    /**
     * 重新申请
     */
    public void reapply() {
        this.status = ArtistStatus.APPLYING;
        this.submitTime = new Date();
        this.applicationRound = (this.applicationRound == null ? 1 : this.applicationRound) + 1;
        this.reviewResult = null;
        this.reviewComment = null;
        this.reviewScore = null;
        this.reviewTime = null;
    }

    /**
     * 补充申请材料
     */
    public void supplement(String supplementInfo) {
        this.status = ArtistStatus.APPLYING;
        if (this.extInfo == null) {
            this.extInfo = supplementInfo;
        } else {
            this.extInfo += "; " + supplementInfo;
        }
    }

    /**
     * 是否可以审核
     */
    public boolean canReview() {
        return this.status == ArtistStatus.APPLYING;
    }

    /**
     * 是否需要审核
     */
    public boolean needsReview() {
        return this.status != null && this.status.needsReview();
    }
} 