package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.constant.TagRelationType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 用户打标签请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTagRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 标签ID列表
     */
    @NotNull(message = "标签ID不能为空")
    private List<Long> tagIds;

    /**
     * 关联类型
     */
    @NotNull(message = "关联类型不能为空")
    private TagRelationType relationType;

    /**
     * 关联对象ID（内容ID、商品ID等）
     */
    private String relationObjectId;

    /**
     * 标签权重（用户对该标签的偏好程度 0.0-1.0）
     */
    private Double weight = 1.0;

    /**
     * 是否自动标签（系统推荐）
     */
    private Boolean isAutoTag = false;

    /**
     * 标签来源
     */
    private String tagSource;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 是否覆盖已有标签
     */
    private Boolean overrideExisting = false;
} 