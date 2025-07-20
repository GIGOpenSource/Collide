package com.gig.collide.api.tag.response.data;

import com.gig.collide.api.tag.constant.TagRelationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户标签信息
 * @author GIG
 */
@Getter
@Setter
@NoArgsConstructor
public class UserTagInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户标签关联ID
     */
    private Long userTagId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标签ID
     */
    private Long tagId;

    /**
     * 标签信息
     */
    private TagInfo tagInfo;

    /**
     * 关联类型
     */
    private TagRelationType relationType;

    /**
     * 关联对象ID（内容ID、商品ID等）
     */
    private String relationObjectId;

    /**
     * 标签权重（用户对该标签的偏好程度）
     */
    private Double weight;

    /**
     * 标签使用次数
     */
    private Integer usageCount;

    /**
     * 最后使用时间
     */
    private Date lastUsedTime;

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * 是否自动标签（系统推荐）
     */
    private Boolean isAutoTag;

    /**
     * 标签来源
     */
    private String tagSource;

    /**
     * 备注信息
     */
    private String remark;
} 