package com.gig.collide.api.like.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 获取用户点赞数量请求 - 缓存增强版
 * 用于获取用户的点赞数量
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
public class UserLikeCountRequest implements Serializable {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 点赞类型：CONTENT、COMMENT、DYNAMIC（可选，为空则查询所有类型）
     */
    private String likeType;
}