package com.gig.collide.api.tag.response;

import com.gig.collide.api.tag.response.data.TagUnifiedInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签管理响应
 *
 * @author Collide Team
 * @version 2.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
public class TagManageResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 标签信息
     */
    private TagUnifiedInfo tagInfo;

    /**
     * 操作结果
     */
    private String operation;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 操作说明
     */
    private String message;
} 