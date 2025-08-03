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
 * 取消点赞请求 - 简洁版
 * 将点赞状态更新为cancelled
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LikeCancelRequest implements Serializable {

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

    /**
     * 点赞用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 取消原因
     */
    private String cancelReason;
} 