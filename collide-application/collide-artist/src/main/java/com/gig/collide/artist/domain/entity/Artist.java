package com.gig.collide.artist.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gig.collide.api.artist.constant.ArtistApplicationType;
import com.gig.collide.api.artist.constant.ArtistCategory;
import com.gig.collide.api.artist.constant.ArtistLevel;
import com.gig.collide.api.artist.constant.ArtistStatus;
import com.gig.collide.datasource.domain.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 博主实体
 * @author GIG
 */
@Setter
@Getter
@TableName("artist")
public class Artist extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

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
     * 申请类型
     */
    private ArtistApplicationType applicationType;

    /**
     * 博主分类（JSON格式存储）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String categories;

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
     * 社交媒体账号（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String socialMediaAccounts;

    /**
     * 擅长领域标签（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String expertiseTags;

    /**
     * 合作意向
     */
    private String collaborationIntent;

    /**
     * 热度分数
     */
    private BigDecimal hotScore;

    /**
     * 影响力指数
     */
    private BigDecimal influenceIndex;

    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;

    /**
     * 扩展信息（JSON格式）
     */
    @TableField(typeHandler = org.apache.ibatis.type.StringTypeHandler.class)
    private String extInfo;

    // ================== 业务方法 ==================

    /**
     * 激活博主
     */
    public void activate() {
        this.status = ArtistStatus.ACTIVE;
        this.lastActiveTime = new Date();
    }

    /**
     * 暂停博主
     */
    public void suspend() {
        this.status = ArtistStatus.SUSPENDED;
    }

    /**
     * 禁用博主
     */
    public void disable() {
        this.status = ArtistStatus.DISABLED;
    }

    /**
     * 恢复博主
     */
    public void restore() {
        this.status = ArtistStatus.ACTIVE;
        this.lastActiveTime = new Date();
    }

    /**
     * 注销博主
     */
    public void cancel() {
        this.status = ArtistStatus.CANCELLED;
    }

    /**
     * 更新统计数据
     */
    public void updateStatistics(Integer followersCount, Integer followingCount, Integer worksCount, 
                                Long totalLikes, Long totalViews) {
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.worksCount = worksCount;
        this.totalLikes = totalLikes;
        this.totalViews = totalViews;
        this.lastActiveTime = new Date();
        
        // 根据粉丝数自动计算等级
        this.level = ArtistLevel.calculateLevel(followersCount);
    }

    /**
     * 设置认证信息
     */
    public void setVerification(String verificationType, String verificationDesc) {
        this.verified = true;
        this.verificationType = verificationType;
        this.verificationDesc = verificationDesc;
    }

    /**
     * 取消认证
     */
    public void revokeVerification() {
        this.verified = false;
        this.verificationType = null;
        this.verificationDesc = null;
    }

    /**
     * 是否可以进行博主活动
     */
    public boolean canOperate() {
        return this.status != null && this.status.canOperate();
    }

    /**
     * 是否为活跃状态
     */
    public boolean isActive() {
        return this.status != null && this.status.isActive();
    }

    /**
     * 更新活跃时间
     */
    public void updateActiveTime() {
        this.lastActiveTime = new Date();
    }
} 