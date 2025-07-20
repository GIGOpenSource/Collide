package com.gig.collide.api.tag.response.data;

import com.gig.collide.api.tag.constant.TagLevel;
import com.gig.collide.api.tag.constant.TagStatus;
import com.gig.collide.api.tag.constant.TagType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 标签信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class TagInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签类型
     */
    private TagType tagType;

    /**
     * 标签状态
     */
    private TagStatus status;

    /**
     * 标签层级
     */
    private TagLevel level;

    /**
     * 父标签ID
     */
    private Long parentTagId;

    /**
     * 父标签名称
     */
    private String parentTagName;

    /**
     * 子标签列表
     */
    private List<TagInfo> children;

    /**
     * 标签颜色
     */
    private String color;

    /**
     * 标签图标
     */
    private String icon;

    /**
     * 排序权重
     */
    private Integer sortOrder;

    /**
     * 使用计数
     */
    private Long usageCount;

    /**
     * 热度分数
     */
    private Double hotScore;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 创建者名称
     */
    private String creatorName;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 更新时间
     */
    private Date updatedTime;

    /**
     * 是否用户已打标签
     */
    private Boolean isUserTagged;

    /**
     * 标签路径（从根到当前标签的完整路径）
     */
    private String tagPath;

    /**
     * 扩展属性
     */
    private String extAttributes;
} 