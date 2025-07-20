package com.gig.collide.api.content.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 内容更新请求
 */
@Data
public class ContentUpdateRequest {

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 内容标题
     */
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 内容描述
     */
    @Size(max = 2000, message = "描述长度不能超过2000字符")
    private String description;

    /**
     * 内容封面图
     */
    private String coverImage;

    /**
     * 内容URL
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
     * 标签列表
     */
    @Size(max = 10, message = "标签数量不能超过10个")
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
     * 扩展信息（JSON格式）
     */
    private String extendInfo;
} 