package com.gig.collide.tag.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 标签实体 - 对应 t_tag 表
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签描述
     */
    private String tagDescription;

    /**
     * 标签图标URL
     */
    private String tagIcon;

    /**
     * 权重（1-100，管理员配置）
     */
    private Integer weight;

    /**
     * 热度值（定时计算）
     */
    private Long hotness;

    /**
     * 关注人数
     */
    private Long followCount;

    /**
     * 内容数量
     */
    private Long contentCount;

    /**
     * 状态：1-启用 0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 初始化默认值
     */
    public void initDefaults() {
        if (this.weight == null) {
            this.weight = 50;
        }
        if (this.hotness == null) {
            this.hotness = 0L;
        }
        if (this.followCount == null) {
            this.followCount = 0L;
        }
        if (this.contentCount == null) {
            this.contentCount = 0L;
        }
        if (this.status == null) {
            this.status = 1;
        }
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 更新修改时间
     */
    public void updateModifyTime() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 检查标签是否启用
     */
    public boolean isActive() {
        return Integer.valueOf(1).equals(this.status);
    }

    /**
     * 检查标签是否禁用
     */
    public boolean isInactive() {
        return Integer.valueOf(0).equals(this.status);
    }

    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (this.status == null) {
            return "未知";
        }
        return this.status == 1 ? "启用" : "禁用";
    }

    /**
     * 检查权重是否有效
     */
    public boolean isValidWeight() {
        return this.weight != null && this.weight >= 1 && this.weight <= 100;
    }

    /**
     * 增加关注数
     */
    public void incrementFollowCount() {
        if (this.followCount == null) {
            this.followCount = 0L;
        }
        this.followCount++;
        updateModifyTime();
    }

    /**
     * 减少关注数
     */
    public void decrementFollowCount() {
        if (this.followCount == null || this.followCount <= 0) {
            this.followCount = 0L;
        } else {
            this.followCount--;
        }
        updateModifyTime();
    }

    /**
     * 增加内容数
     */
    public void incrementContentCount() {
        if (this.contentCount == null) {
            this.contentCount = 0L;
        }
        this.contentCount++;
        updateModifyTime();
    }

    /**
     * 减少内容数
     */
    public void decrementContentCount() {
        if (this.contentCount == null || this.contentCount <= 0) {
            this.contentCount = 0L;
        } else {
            this.contentCount--;
        }
        updateModifyTime();
    }

    /**
     * 计算推荐分数
     */
    public double calculateRecommendScore() {
        double baseScore = 0.0;
        
        // 权重影响（30%）
        if (this.weight != null) {
            baseScore += (this.weight / 100.0) * 30;
        }
        
        // 热度影响（40%）
        if (this.hotness != null && this.hotness > 0) {
            baseScore += Math.log(this.hotness + 1) * 4;
        }
        
        // 关注数影响（20%）
        if (this.followCount != null && this.followCount > 0) {
            baseScore += Math.log(this.followCount + 1) * 2;
        }
        
        // 内容数影响（10%）
        if (this.contentCount != null && this.contentCount > 0) {
            baseScore += Math.log(this.contentCount + 1) * 1;
        }
        
        return baseScore;
    }

    /**
     * 检查标签是否热门
     */
    public boolean isHot() {
        return this.hotness != null && this.hotness > 1000 && this.followCount != null && this.followCount > 100;
    }

    /**
     * 检查标签是否为新标签
     */
    public boolean isNew() {
        if (this.createTime == null) {
            return false;
        }
        return this.createTime.isAfter(LocalDateTime.now().minusDays(7));
    }
}