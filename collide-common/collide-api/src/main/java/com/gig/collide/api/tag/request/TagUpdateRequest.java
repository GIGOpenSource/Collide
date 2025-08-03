package com.gig.collide.api.tag.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * 标签更新请求
 * 
 * @author GIG Team
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    @Min(value = 1, message = "标签ID无效")
    private Long id;

    /**
     * 标签名称
     */
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String tagName;

    /**
     * 标签描述
     */
    @Size(max = 200, message = "标签描述长度不能超过200个字符")
    private String tagDescription;

    /**
     * 标签图标URL
     */
    @Size(max = 255, message = "标签图标URL长度不能超过255个字符")
    private String tagIcon;

    /**
     * 权重（1-100）
     */
    @Min(value = 1, message = "权重最小值为1")
    @Max(value = 100, message = "权重最大值为100")
    private Integer weight;

    /**
     * 状态：1-启用 0-禁用
     */
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}