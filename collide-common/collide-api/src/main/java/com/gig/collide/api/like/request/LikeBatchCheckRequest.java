package com.gig.collide.api.like.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 批量检查点赞状态请求 - 缓存增强版
 * 用于批量检查用户对多个目标对象的点赞状态
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
public class LikeBatchCheckRequest implements Serializable {

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
     * 目标对象ID列表
     */
    @NotNull(message = "目标对象ID列表不能为空")
    @Size(min = 1, max = 500, message = "目标对象ID列表大小必须在1-500之间")
    private List<Long> targetIds;
}