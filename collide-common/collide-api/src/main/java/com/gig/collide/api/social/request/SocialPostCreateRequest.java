package com.gig.collide.api.social.request;

import com.gig.collide.api.social.constant.PostTypeEnum;
import com.gig.collide.api.social.constant.VisibilityEnum;
import com.gig.collide.base.request.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

/**
 * 社交动态创建请求
 * @author GIG
 * @version 1.0.0
 * @since 2024-01-01
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SocialPostCreateRequest extends BaseRequest {
    
    /**
     * 作者用户ID
     */
    @NotNull(message = "作者用户ID不能为空")
    private Long authorId;
    
    /**
     * 动态类型
     */
    @NotNull(message = "动态类型不能为空")
    private PostTypeEnum postType;
    
    /**
     * 动态内容
     */
    @NotBlank(message = "动态内容不能为空")
    @Size(max = 5000, message = "动态内容不能超过5000字符")
    private String content;
    
    /**
     * 媒体文件URL列表
     */
    @Size(max = 9, message = "媒体文件不能超过9个")
    private List<String> mediaUrls;
    
    /**
     * 位置信息
     */
    @Size(max = 200, message = "位置信息不能超过200字符")
    private String location;
    
    /**
     * 经度
     */
    @DecimalMin(value = "-180.0", message = "经度必须在-180到180之间")
    @DecimalMax(value = "180.0", message = "经度必须在-180到180之间")
    private BigDecimal longitude;
    
    /**
     * 纬度
     */
    @DecimalMin(value = "-90.0", message = "纬度必须在-90到90之间")
    @DecimalMax(value = "90.0", message = "纬度必须在-90到90之间")
    private BigDecimal latitude;
    
    /**
     * 话题标签列表
     */
    @Size(max = 10, message = "话题标签不能超过10个")
    private List<String> topics;
    
    /**
     * 提及的用户ID列表
     */
    @Size(max = 20, message = "提及用户不能超过20个")
    private List<Long> mentionedUserIds;
    
    /**
     * 可见性
     */
    @NotNull(message = "可见性不能为空")
    private VisibilityEnum visibility = VisibilityEnum.PUBLIC;
    
    /**
     * 是否允许评论
     */
    @NotNull(message = "是否允许评论不能为空")
    private Boolean allowComments = true;
    
    /**
     * 是否允许转发
     */
    @NotNull(message = "是否允许转发不能为空")
    private Boolean allowShares = true;
    
    /**
     * 是否立即发布（false为保存草稿）
     */
    @NotNull(message = "是否立即发布不能为空")
    private Boolean publishImmediately = true;
    
    /**
     * 构造简单文本动态创建请求
     */
    public static SocialPostCreateRequest createTextPost(Long authorId, String content) {
        SocialPostCreateRequest request = new SocialPostCreateRequest();
        request.setAuthorId(authorId);
        request.setPostType(PostTypeEnum.TEXT);
        request.setContent(content);
        return request;
    }
    
    /**
     * 构造图片动态创建请求
     */
    public static SocialPostCreateRequest createImagePost(Long authorId, String content, List<String> imageUrls) {
        SocialPostCreateRequest request = new SocialPostCreateRequest();
        request.setAuthorId(authorId);
        request.setPostType(PostTypeEnum.IMAGE);
        request.setContent(content);
        request.setMediaUrls(imageUrls);
        return request;
    }
    
    /**
     * 构造视频动态创建请求
     */
    public static SocialPostCreateRequest createVideoPost(Long authorId, String content, String videoUrl) {
        SocialPostCreateRequest request = new SocialPostCreateRequest();
        request.setAuthorId(authorId);
        request.setPostType(PostTypeEnum.VIDEO);
        request.setContent(content);
        request.setMediaUrls(List.of(videoUrl));
        return request;
    }
    
    /**
     * 构造转发动态创建请求
     */
    public static SocialPostCreateRequest createSharePost(Long authorId, String shareComment) {
        SocialPostCreateRequest request = new SocialPostCreateRequest();
        request.setAuthorId(authorId);
        request.setPostType(PostTypeEnum.SHARE);
        request.setContent(shareComment);
        return request;
    }
    
    /**
     * 构造草稿动态创建请求
     */
    public static SocialPostCreateRequest createDraft(Long authorId, PostTypeEnum postType, String content) {
        SocialPostCreateRequest request = new SocialPostCreateRequest();
        request.setAuthorId(authorId);
        request.setPostType(postType);
        request.setContent(content);
        request.setPublishImmediately(false);
        return request;
    }
} 