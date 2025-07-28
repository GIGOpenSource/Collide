package com.gig.collide.api.tag.response;

import com.gig.collide.api.tag.response.data.ContentTagInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 内容标签响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class ContentTagResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 内容标签信息
     */
    private ContentTagInfo contentTagInfo;

    /**
     * 内容标签列表（批量操作时使用）
     */
    private List<ContentTagInfo> contentTagList;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 推荐标签（AI推荐时使用）
     */
    private List<ContentTagInfo> recommendedTags;

    /**
     * 操作提示信息
     */
    private String message;
} 