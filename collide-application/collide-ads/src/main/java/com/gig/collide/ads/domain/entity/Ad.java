package com.gig.collide.ads.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 广告实体类 - 极简版
 * 对应表：t_ad
 * 
 * @author GIG Team
 * @version 3.0.0 (极简版)
 * @since 2024-01-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
@TableName("t_ad")
public class Ad {

    /**
     * 广告ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 广告名称
     */
    @TableField("ad_name")
    private String adName;

    /**
     * 广告标题
     */
    @TableField("ad_title")
    private String adTitle;

    /**
     * 广告描述
     */
    @TableField("ad_description")
    private String adDescription;

    /**
     * 广告类型（核心字段）
     * banner: 横幅广告
     * sidebar: 侧边栏广告 
     * popup: 弹窗广告
     * feed: 信息流广告
     */
    @TableField("ad_type")
    private String adType;

    /**
     * 广告图片URL（核心字段）
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 点击跳转链接（核心字段）
     */
    @TableField("click_url")
    private String clickUrl;

    /**
     * 图片替代文本
     */
    @TableField("alt_text")
    private String altText;

    /**
     * 链接打开方式
     * _blank: 新窗口打开
     * _self: 当前窗口打开
     */
    @TableField("target_type")
    private String targetType;

    /**
     * 是否启用
     * 1: 启用
     * 0: 禁用
     */
    @TableField("is_active")
    private Integer isActive;

    /**
     * 排序权重（数值越大优先级越高）
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // =================== 业务方法 ===================

    /**
     * 是否为启用状态
     */
    public boolean isEnabled() {
        return this.isActive != null && this.isActive == 1;
    }

    /**
     * 启用广告
     */
    public Ad enable() {
        this.isActive = 1;
        return this;
    }

    /**
     * 禁用广告
     */
    public Ad disable() {
        this.isActive = 0;
        return this;
    }

    /**
     * 设置权重
     */
    public Ad setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return this.adTitle != null && !this.adTitle.trim().isEmpty() 
            ? this.adTitle 
            : this.adName;
    }
}