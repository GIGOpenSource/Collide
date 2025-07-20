package com.gig.collide.api.content.response.data;

import com.gig.collide.api.content.constant.ContentType;
import com.gig.collide.api.content.constant.ContentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容信息
 */
@Data
public class ContentInfo {

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 内容类型
     */
    private ContentType contentType;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 内容封面图
     */
    private String coverImage;

    /**
     * 内容URL（视频地址、小说章节地址等）
     */
    private String contentUrl;

    /**
     * 内容时长（视频专用，单位：秒）
     */
    private Integer duration;

    /**
     * 内容大小（字节）
     */
    private Long fileSize;

    /**
     * 内容状态
     */
    private ContentStatus status;

    /**
     * 创建者用户ID
     */
    private Long creatorUserId;

    /**
     * 创建者用户名
     */
    private String creatorUsername;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 分类
     */
    private String category;

    /**
     * 是否原创
     */
    private Boolean isOriginal;

    /**
     * 年龄分级
     */
    private String ageRating;

    /**
     * 语言
     */
    private String language;

    /**
     * 查看次数
     */
    private Long viewCount;

    /**
     * 点赞次数
     */
    private Long likeCount;

    /**
     * 收藏次数
     */
    private Long collectCount;

    /**
     * 分享次数
     */
    private Long shareCount;

    /**
     * 评论次数
     */
    private Long commentCount;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 评分人数
     */
    private Long ratingCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 扩展信息（JSON格式，用于存储各类型特有的属性）
     */
    private String extendInfo;
} 