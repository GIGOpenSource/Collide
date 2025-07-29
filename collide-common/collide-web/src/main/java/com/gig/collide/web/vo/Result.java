package com.gig.collide.web.vo;

import com.gig.collide.base.response.SingleResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import static com.gig.collide.base.response.ResponseCode.SUCCESS;

/**
 * 通用响应结果封装类
 * 用于Dubbo微服务间的数据传输，支持序列化
 * 
 * @author GIGTeam
 * @version 1.0.0 (支持Dubbo序列化)
 */
@Getter
@Setter
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private String code;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息描述
     */
    private String message;

    /**
     * 数据，可以是任何类型的VO
     */
    private T data;

    public Result() {
    }

    public Result(Boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public Result(Boolean success, String code, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public Result(SingleResponse<T> singleResponse){
        this.success = singleResponse.getSuccess();
        this.data = singleResponse.getData();
        this.code = singleResponse.getResponseCode();
        this.message = singleResponse.getResponseMessage();
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, SUCCESS.name(), SUCCESS.name(), data);
    }

    public static <T> Result<T> fail(String errorCode, String errorMsg) {
        return new Result<>(false, errorCode, errorMsg, null);
    }

    public static <T> Result<T> error(String errorCode,String errorMsg) {
        return fail(errorCode, errorMsg);
    }
}