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
 * 用户取消标签请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserUntagRequest extends BaseRequest {

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
    private TagRelationType relationType;

    /**
     * 关联对象ID（内容ID、商品ID等）
     */
    private String relationObjectId;

    /**
     * 取消原因
     */
    private String reason;

    /**
     * 是否批量取消（如果为true，忽略relationObjectId，取消用户所有该标签）
     */
    private Boolean batchUntag = false;
} 