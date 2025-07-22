package com.gig.collide.api.content.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 内容通用响应类
 * 用于内容创建、更新、删除、发布等操作的响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentResponse extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 操作结果数据
     */
    private Object data;

    public ContentResponse(Long contentId) {
        super();
        this.contentId = contentId;
        this.setSuccess(true);
    }

    public ContentResponse(Long contentId, Object data) {
        super();
        this.contentId = contentId;
        this.data = data;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param contentId 内容ID
     * @return 响应对象
     */
    public static ContentResponse success(Long contentId) {
        return new ContentResponse(contentId);
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param contentId 内容ID
     * @param data 响应数据
     * @return 响应对象
     */
    public static ContentResponse success(Long contentId, Object data) {
        return new ContentResponse(contentId, data);
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return 响应对象
     */
    public static ContentResponse error(String errorCode, String errorMessage) {
        ContentResponse response = new ContentResponse();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 