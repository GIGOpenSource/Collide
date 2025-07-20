package com.gig.collide.api.tag.request;

import com.gig.collide.api.tag.constant.TagRelationType;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 用户标签查询请求
 * @author GIG
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserTagQueryRequest extends BaseRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 关联类型
     */
    private TagRelationType relationType;

    /**
     * 关联对象ID
     */
    private String relationObjectId;

    /**
     * 是否只查询自动标签
     */
    private Boolean onlyAutoTag;

    /**
     * 最小权重
     */
    private Double minWeight;

    /**
     * 排序字段
     */
    private String sortBy = "weight";

    /**
     * 排序方向
     */
    private String sortDirection = "DESC";

    /**
     * 限制数量
     */
    private Integer limit;

    public UserTagQueryRequest(Long userId) {
        this.userId = userId;
    }
} 