package com.gig.collide.users.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户统计实体 - 对应 t_user_stats 表
 * 负责用户统计数据管理
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
@TableName("t_user_stats")
public class UserStats {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（关联t_user表）
     */
    private Long userId;

    /**
     * 粉丝数
     */
    private Integer followerCount;

    /**
     * 关注数
     */
    private Integer followingCount;

    /**
     * 内容数
     */
    private Integer contentCount;

    /**
     * 获得点赞数
     */
    private Integer likeCount;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 活跃度分数
     */
    private java.math.BigDecimal activityScore;

    /**
     * 影响力分数
     */
    private java.math.BigDecimal influenceScore;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.followerCount == null) {
            this.followerCount = 0;
        }
        if (this.followingCount == null) {
            this.followingCount = 0;
        }
        if (this.contentCount == null) {
            this.contentCount = 0;
        }
        if (this.likeCount == null) {
            this.likeCount = 0;
        }
        if (this.loginCount == null) {
            this.loginCount = 0;
        }
        if (this.activityScore == null) {
            this.activityScore = java.math.BigDecimal.ZERO;
        }
        if (this.influenceScore == null) {
            this.influenceScore = java.math.BigDecimal.ZERO;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 计算并更新活跃度分数（基于登录次数和内容数）
     */
    public void calculateAndUpdateActivityScore() {
        double loginScore = Math.min(loginCount * 0.1, 50.0); // 登录分数最多50分
        double contentScore = Math.min(contentCount * 2.0, 100.0); // 内容分数最多100分
        this.activityScore = java.math.BigDecimal.valueOf(loginScore + contentScore);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 计算并更新影响力分数（基于粉丝数和点赞数）
     */
    public void calculateAndUpdateInfluenceScore() {
        double followerScore = Math.min(followerCount * 1.0, 1000.0); // 粉丝分数最多1000分
        double likeScore = Math.min(likeCount * 0.5, 500.0); // 点赞分数最多500分
        this.influenceScore = java.math.BigDecimal.valueOf(followerScore + likeScore);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
        this.loginCount = (this.loginCount == null ? 0 : this.loginCount) + 1;
        this.updateTime = LocalDateTime.now();
        calculateAndUpdateActivityScore(); // 重新计算活跃度分数
    }

    /**
     * 检查是否为活跃用户
     */
    public boolean isActiveUser() {
        return loginCount >= 10 && contentCount >= 5;
    }

    /**
     * 检查是否为知名用户
     */
    public boolean isInfluentialUser() {
        return followerCount >= 1000 || likeCount >= 10000;
    }

    /**
     * 增加统计数据
     */
    public void incrementStats(String statsType, Integer increment) {
        switch (statsType.toLowerCase()) {
            case "follower":
                this.followerCount = Math.max(0, this.followerCount + increment);
                break;
            case "following":
                this.followingCount = Math.max(0, this.followingCount + increment);
                break;
            case "content":
                this.contentCount = Math.max(0, this.contentCount + increment);
                break;
            case "like":
                this.likeCount = Math.max(0, this.likeCount + increment);
                break;
            case "login":
                this.loginCount = Math.max(0, this.loginCount + increment);
                break;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新统计修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }
}