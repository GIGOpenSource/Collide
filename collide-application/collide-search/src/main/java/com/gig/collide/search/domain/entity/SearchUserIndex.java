package com.gig.collide.search.domain.entity;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 搜索用户索引表实体
 * 
 * @author Collide Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_search_user_index")
public class SearchUserIndex implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 索引ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 用户角色：user/vip/blogger/admin
     */
    @TableField("role")
    private String role;

    /**
     * 用户状态：active/inactive/suspended/banned
     */
    @TableField("status")
    private String status;

    /**
     * 是否认证：0-未认证，1-已认证
     */
    @TableField("is_verified")
    private Integer isVerified;

    /**
     * 博主认证状态：none/applying/approved/rejected
     */
    @TableField("blogger_status")
    private String bloggerStatus;

    /**
     * VIP等级：0-普通用户，1-VIP1，2-VIP2
     */
    @TableField("vip_level")
    private Integer vipLevel;

    /**
     * VIP过期时间
     */
    @TableField("vip_expire_time")
    private LocalDateTime vipExpireTime;

    /**
     * 粉丝数
     */
    @TableField("follower_count")
    private Long followerCount;

    /**
     * 关注数
     */
    @TableField("following_count")
    private Long followingCount;

    /**
     * 内容数
     */
    @TableField("content_count")
    private Long contentCount;

    /**
     * 获得点赞数
     */
    @TableField("like_count")
    private Long likeCount;

    /**
     * 被查看数
     */
    @TableField("view_count")
    private Long viewCount;

    /**
     * 活跃度评分
     */
    @TableField("activity_score")
    private BigDecimal activityScore;

    /**
     * 影响力评分
     */
    @TableField("influence_score")
    private BigDecimal influenceScore;

    /**
     * 内容质量评分
     */
    @TableField("quality_score")
    private BigDecimal qualityScore;

    /**
     * 所在地
     */
    @TableField("location")
    private String location;

    /**
     * 地区编码
     */
    @TableField("location_code")
    private String locationCode;

    /**
     * 搜索权重
     */
    @TableField("search_weight")
    private Double searchWeight;

    /**
     * 搜索标签，JSON数组格式
     */
    @TableField("search_tags")
    private String searchTags;

    /**
     * 关键词（用于全文搜索）
     */
    @TableField("keywords")
    private String keywords;

    /**
     * 最后活跃时间
     */
    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;

    /**
     * 注册时间（冗余字段）
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

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

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 获取搜索标签数组
     */
    public String[] getSearchTagsArray() {
        if (searchTags == null) {
            return new String[0];
        }
        return JSON.parseObject(searchTags, String[].class);
    }

    /**
     * 设置搜索标签数组
     */
    public void setSearchTagsArray(String[] searchTagsArray) {
        this.searchTags = searchTagsArray == null ? null : JSON.toJSONString(searchTagsArray);
    }
} 