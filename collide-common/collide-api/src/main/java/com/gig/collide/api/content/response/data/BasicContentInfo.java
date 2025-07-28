package com.gig.collide.api.content.response.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础内容信息
 * 包含最基本的内容标识信息
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class BasicContentInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long id;

    /**
     * 内容标题
     */
    private String title;

    /**
     * 内容描述
     */
    private String description;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者昵称
     */
    private String authorNickname;

    /**
     * 作者头像
     */
    private String authorAvatar;
} 