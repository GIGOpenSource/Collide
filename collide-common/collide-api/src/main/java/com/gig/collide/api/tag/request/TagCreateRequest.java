package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.constant.TagLevel;
import com.gig.collide.api.tag.constant.TagType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签创建请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagCreateRequest extends BaseRequest {

    /**
     * 标签名称
     */
    @NotBlank(message = "标签名称不能为空")
    private String tagName;

    /**
     * 标签描述
     */
    private String description;

    /**
     * 标签类型
     */
    @NotNull(message = "标签类型不能为空")
    private TagType tagType;

    /**
     * 标签层级
     */
    @NotNull(message = "标签层级不能为空")
    private TagLevel level;

    /**
     * 父标签ID（一级标签可为空）
     */
    private Long parentTagId;

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
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 扩展属性
     */
    private String extAttributes;

    /**
     * 是否需要审核
     */
    private Boolean needReview = false;

    /**
     * 申请理由
     */
    private String applyReason;
} 