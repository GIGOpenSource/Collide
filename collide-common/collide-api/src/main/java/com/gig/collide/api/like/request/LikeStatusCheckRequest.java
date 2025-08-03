package com.gig.collide.api.like.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 点赞状态检查请求 - 缓存增强版
 * 用于检查用户对单个目标对象的点赞状态
 * 
 * @author GIG Team
 * @version 2.0.0 (缓存增强版)
 * @since 2024-01-16
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LikeStatusCheckRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 点赞类型：CONTENT、COMMENT、DYNAMIC
     */
    @NotBlank(message = "点赞类型不能为空")
    private String likeType;

    /**
     * 目标对象ID
     */
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;
}