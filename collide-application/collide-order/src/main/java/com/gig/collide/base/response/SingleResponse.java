package com.gig.collide.base.response;

/**
 * 单一响应结果封装类
 * 
 * 注意：这是临时创建的简化版本，实际应该在 collide-base 组件中提供
 * 
 * @param <T> 响应数据类型
 * @author Collide Team
 */
public class SingleResponse<T> {
    
    private Integer code;
    private String message;
    private T data;
    private String traceId;
    private Long timestamp;
    
    // 构造方法
    public SingleResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public SingleResponse(Integer code, String message, T data) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    // 成功响应构建方法
    public static <T> SingleResponse<T> buildSuccess() {
        return new SingleResponse<>(200, "操作成功", null);
    }
    
    public static <T> SingleResponse<T> buildSuccess(T data) {
        return new SingleResponse<>(200, "操作成功", data);
    }
    
    // 失败响应构建方法
    public static <T> SingleResponse<T> buildFailure(String code, String message) {
        return new SingleResponse<>(500, message, null);
    }
    
    // Getter和Setter方法
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setResponseMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getTraceId() {
        return traceId;
    }
    
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
} 