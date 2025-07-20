package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.constant.TagStatus;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签更新请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TagUpdateRequest extends BaseRequest {

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
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
     * 标签状态
     */
    private TagStatus status;

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
     * 扩展属性
     */
    private String extAttributes;

    /**
     * 更新者ID
     */
    private Long updaterId;

    /**
     * 更新理由
     */
    private String updateReason;
} 