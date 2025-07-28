package com.gig.collide.api.social.request;

import com.gig.collide.api.social.constant.PostStatusEnum;
import com.gig.collide.api.social.constant.VisibilityEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

/**
 * 社交动态更新请求
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostUpdateRequest extends BaseRequest {
    
    /**
     * 动态ID
     */
    @NotNull(message = "动态ID不能为空")
    private Long postId;
    
    /**
     * 操作用户ID
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorUserId;
    
    /**
     * 动态内容（可选更新）
     */
    @Size(max = 5000, message = "动态内容不能超过5000字符")
    private String content;
    
    /**
     * 媒体文件URL列表（可选更新）
     */
    @Size(max = 9, message = "媒体文件不能超过9个")
    private List<String> mediaUrls;
    
    /**
     * 位置信息（可选更新）
     */
    @Size(max = 200, message = "位置信息不能超过200字符")
    private String location;
    
    /**
     * 经度（可选更新）
     */
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    private BigDecimal longitude;
    
    /**
     * 纬度（可选更新）
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    private BigDecimal latitude;
    
    /**
     * 话题标签列表（可选更新）
     */
    @Size(max = 10, message = "话题标签不能超过10个")
    private List<String> topics;
    
    /**
     * 提及的用户ID列表（可选更新）
     */
    @Size(max = 20, message = "提及用户不能超过20个")
    private List<Long> mentionedUserIds;
    
    /**
     * 动态状态（可选更新）
     */
    private PostStatusEnum status;
    
    /**
     * 可见性（可选更新）
     */
    private VisibilityEnum visibility;
    
    /**
     * 是否允许评论（可选更新）
     */
    private Boolean allowComments;
    
    /**
     * 是否允许转发（可选更新）
     */
    private Boolean allowShares;
    
    /**
     * 版本号（乐观锁）
     */
    @NotNull(message = "版本号不能为空")
    private Integer version;
    
    /**
     * 构造内容更新请求
     */
    public static SocialPostUpdateRequest updateContent(Long postId, Long operatorUserId, String content, Integer version) {
        SocialPostUpdateRequest request = new SocialPostUpdateRequest();
        request.setPostId(postId);
        request.setOperatorUserId(operatorUserId);
        request.setContent(content);
        request.setVersion(version);
        return request;
    }
    
    /**
     * 构造状态更新请求
     */
    public static SocialPostUpdateRequest updateStatus(Long postId, Long operatorUserId, PostStatusEnum status, Integer version) {
        SocialPostUpdateRequest request = new SocialPostUpdateRequest();
        request.setPostId(postId);
        request.setOperatorUserId(operatorUserId);
        request.setStatus(status);
        request.setVersion(version);
        return request;
    }
    
    /**
     * 构造可见性更新请求
     */
    public static SocialPostUpdateRequest updateVisibility(Long postId, Long operatorUserId, VisibilityEnum visibility, Integer version) {
        SocialPostUpdateRequest request = new SocialPostUpdateRequest();
        request.setPostId(postId);
        request.setOperatorUserId(operatorUserId);
        request.setVisibility(visibility);
        request.setVersion(version);
        return request;
    }
    
    /**
     * 构造发布请求
     */
    public static SocialPostUpdateRequest publishPost(Long postId, Long operatorUserId, Integer version) {
        return updateStatus(postId, operatorUserId, PostStatusEnum.PUBLISHED, version);
    }
    
    /**
     * 构造删除请求
     */
    public static SocialPostUpdateRequest deletePost(Long postId, Long operatorUserId, Integer version) {
        return updateStatus(postId, operatorUserId, PostStatusEnum.DELETED, version);
    }
} 