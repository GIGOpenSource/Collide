package com.gig.collide.api.like.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 点赞请求 - 简洁版
 * 
 * @author GIG Team
 * @version 2.0.0
 */
@Data
public class LikeRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Pattern(regexp = "^(CONTENT|COMMENT|DYNAMIC)$", message = "点赞类型只能是CONTENT、COMMENT或DYNAMIC")
    private String likeType;

    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;
} 