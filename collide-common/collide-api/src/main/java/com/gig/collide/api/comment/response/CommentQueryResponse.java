package com.gig.collide.api.comment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 评论查询响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentQueryResponse<T> extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应数据
     */
    private T data;

    public CommentQueryResponse(T data) {
        super();
        this.data = data;
        this.setSuccess(true);
    }

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> CommentQueryResponse<T> success(T data) {
        return new CommentQueryResponse<>(data);
    }

    /**
     * 创建失败响应
     *
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param <T> 数据类型
     * @return 响应对象
     */
    public static <T> CommentQueryResponse<T> error(String errorCode, String errorMessage) {
        CommentQueryResponse<T> response = new CommentQueryResponse<>();
        response.setSuccess(false);
        response.setResponseCode(errorCode);
        response.setResponseMessage(errorMessage);
        return response;
    }
} 