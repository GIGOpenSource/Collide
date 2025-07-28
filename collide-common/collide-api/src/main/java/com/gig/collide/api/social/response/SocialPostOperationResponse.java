package com.gig.collide.api.social.response;

import com.gig.collide.api.social.response.data.SocialPostInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 社交动态操作响应
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostOperationResponse extends BaseResponse {
    
    /**
     * 动态信息
     */
    private SocialPostInfo postInfo;
    
    /**
     * 操作类型
     */
    private String operationType;

    private String Message;
    
    /**
     * 构造创建成功响应
     */
    public static SocialPostOperationResponse createSuccess(SocialPostInfo postInfo) {
        SocialPostOperationResponse response = new SocialPostOperationResponse();
        response.setSuccess(true);
        response.setResponseMessage("动态创建成功");
        response.setPostInfo(postInfo);
        response.setOperationType("CREATE");
        return response;
    }
    
    /**
     * 构造更新成功响应
     */
    public static SocialPostOperationResponse updateSuccess(SocialPostInfo postInfo) {
        SocialPostOperationResponse response = new SocialPostOperationResponse();
        response.setSuccess(true);
        response.setResponseMessage("动态更新成功");
        response.setPostInfo(postInfo);
        response.setOperationType("UPDATE");
        return response;
    }
    
    /**
     * 构造删除成功响应
     */
    public static SocialPostOperationResponse deleteSuccess(Long postId) {
        SocialPostOperationResponse response = new SocialPostOperationResponse();
        response.setSuccess(true);
        response.setResponseMessage("动态删除成功");
        response.setOperationType("DELETE");
        // 删除操作只返回基本信息
        SocialPostInfo postInfo = SocialPostInfo.builder().id(postId).build();
        response.setPostInfo(postInfo);
        return response;
    }
    
    /**
     * 构造发布成功响应
     */
    public static SocialPostOperationResponse publishSuccess(SocialPostInfo postInfo) {
        SocialPostOperationResponse response = new SocialPostOperationResponse();
        response.setSuccess(true);
        response.setResponseMessage("动态发布成功");
        response.setPostInfo(postInfo);
        response.setOperationType("PUBLISH");
        return response;
    }
    
    /**
     * 构造操作失败响应
     */
    public static SocialPostOperationResponse error(String operationType, String message) {
        SocialPostOperationResponse response = new SocialPostOperationResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        response.setOperationType(operationType);
        return response;
    }
} 