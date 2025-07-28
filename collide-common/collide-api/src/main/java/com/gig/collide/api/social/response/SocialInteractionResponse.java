package com.gig.collide.api.social.response;

import com.gig.collide.api.social.response.data.SocialInteractionInfo;
import com.gig.collide.base.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 社交互动响应
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialInteractionResponse extends BaseResponse {
    
    /**
     * 互动信息
     */
    private SocialInteractionInfo interactionInfo;
    
    /**
     * 动态当前统计信息
     */
    private PostStatistics postStatistics;

    private String Message;
    
    /**
     * 动态统计信息内部类
     */
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostStatistics {
        /**
         * 动态ID
         */
        private Long postId;
        
        /**
         * 点赞数
         */
        private Long likeCount;
        
        /**
         * 评论数
         */
        private Long commentCount;
        
        /**
         * 转发数
         */
        private Long shareCount;
        
        /**
         * 浏览数
         */
        private Long viewCount;
        
        /**
         * 收藏数
         */
        private Long favoriteCount;
    }
    
    /**
     * 构造互动成功响应
     */
    public static SocialInteractionResponse success(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = new SocialInteractionResponse();
        response.setSuccess(true);
        response.setResponseMessage("互动成功");
        response.setInteractionInfo(interactionInfo);
        response.setPostStatistics(postStatistics);
        return response;
    }
    
    /**
     * 构造点赞成功响应
     */
    public static SocialInteractionResponse likeSuccess(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = success(interactionInfo, postStatistics);
        response.setResponseMessage("点赞成功");
        return response;
    }
    
    /**
     * 构造取消点赞成功响应
     */
    public static SocialInteractionResponse unlikeSuccess(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = success(interactionInfo, postStatistics);
        response.setResponseMessage("取消点赞成功");
        return response;
    }
    
    /**
     * 构造收藏成功响应
     */
    public static SocialInteractionResponse favoriteSuccess(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = success(interactionInfo, postStatistics);
        response.setResponseMessage("收藏成功");
        return response;
    }
    
    /**
     * 构造取消收藏成功响应
     */
    public static SocialInteractionResponse unfavoriteSuccess(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = success(interactionInfo, postStatistics);
        response.setResponseMessage("取消收藏成功");
        return response;
    }
    
    /**
     * 构造转发成功响应
     */
    public static SocialInteractionResponse shareSuccess(SocialInteractionInfo interactionInfo, PostStatistics postStatistics) {
        SocialInteractionResponse response = success(interactionInfo, postStatistics);
        response.setResponseMessage("转发成功");
        return response;
    }
    
    /**
     * 构造浏览成功响应
     */
    public static SocialInteractionResponse viewSuccess(PostStatistics postStatistics) {
        SocialInteractionResponse response = new SocialInteractionResponse();
        response.setSuccess(true);
        response.setResponseMessage("浏览记录成功");
        response.setPostStatistics(postStatistics);
        return response;
    }
    
    /**
     * 构造互动失败响应
     */
    public static SocialInteractionResponse failure(String message) {
        SocialInteractionResponse response = new SocialInteractionResponse();
        response.setSuccess(false);
        response.setResponseMessage(message);
        return response;
    }
} 