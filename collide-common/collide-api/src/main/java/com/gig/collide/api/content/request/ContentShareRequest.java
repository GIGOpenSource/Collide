package com.gig.collide.api.content.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 内容分享请求
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentShareRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 分享平台（WECHAT、WEIBO、QQ等）
     */
    private String platform;

    /**
     * 分享文案
     */
    private String shareText;
} 