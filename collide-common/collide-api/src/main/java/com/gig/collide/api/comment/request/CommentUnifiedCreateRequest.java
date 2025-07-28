package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.enums.CommentTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 评论创建请求
 * 用于创建新的评论
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentUnifiedCreateRequest extends BaseRequest {

    /**
     * 评论类型（必填）
     */
    @NotNull(message = "评论类型不能为空")
    private CommentTypeEnum commentType;

    /**
     * 目标对象ID（必填）
     */
    @NotNull(message = "目标对象ID不能为空")
    private Long targetId;

    /**
     * 父评论ID（可选，0或null表示根评论）
     */
    private Long parentCommentId;

    /**
     * 根评论ID（可选，系统自动计算）
     */
    private Long rootCommentId;

    /**
     * 评论内容（必填）
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容长度不能超过2000个字符")
    private String content;

    /**
     * 评论用户ID（必填）
     */
    @NotNull(message = "评论用户ID不能为空")
    private Long userId;

    /**
     * 回复目标用户ID（可选，当为回复评论时需要）
     */
    private Long replyToUserId;

    /**
     * 提及的用户ID列表（可选）
     */
    @Size(max = 10, message = "提及用户数量不能超过10个")
    private List<Long> mentionUserIds;

    /**
     * 评论图片列表（可选）
     */
    @Size(max = 9, message = "评论图片数量不能超过9张")
    private List<String> images;

    /**
     * 扩展数据（可选）
     */
    private String extraData;

    /**
     * IP地址（可选）
     */
    private String ipAddress;

    /**
     * 设备信息（可选）
     */
    private String deviceInfo;

    /**
     * 地理位置（可选）
     */
    private String location;

    /**
     * 是否需要审核（可选，默认根据系统设置）
     */
    private Boolean needAudit;

    // ===================== 便捷构造器 =====================

    /**
     * 创建内容评论
     */
    public static CommentUnifiedCreateRequest contentComment(Long targetId, String content, Long userId) {
        CommentUnifiedCreateRequest request = new CommentUnifiedCreateRequest();
        request.setCommentType(CommentTypeEnum.CONTENT);
        request.setTargetId(targetId);
        request.setContent(content);
        request.setUserId(userId);
        return request;
    }

    /**
     * 创建动态评论
     */
    public static CommentUnifiedCreateRequest dynamicComment(Long targetId, String content, Long userId) {
        CommentUnifiedCreateRequest request = new CommentUnifiedCreateRequest();
        request.setCommentType(CommentTypeEnum.DYNAMIC);
        request.setTargetId(targetId);
        request.setContent(content);
        request.setUserId(userId);
        return request;
    }

    /**
     * 创建回复评论
     */
    public static CommentUnifiedCreateRequest replyComment(Long targetId, CommentTypeEnum commentType, 
                                                          Long parentCommentId, Long rootCommentId,
                                                          String content, Long userId, Long replyToUserId) {
        CommentUnifiedCreateRequest request = new CommentUnifiedCreateRequest();
        request.setCommentType(commentType);
        request.setTargetId(targetId);
        request.setParentCommentId(parentCommentId);
        request.setRootCommentId(rootCommentId);
        request.setContent(content);
        request.setUserId(userId);
        request.setReplyToUserId(replyToUserId);
        return request;
    }

    /**
     * 链式设置提及用户
     */
    public CommentUnifiedCreateRequest withMentions(Long... userIds) {
        this.mentionUserIds = List.of(userIds);
        return this;
    }

    /**
     * 链式设置图片
     */
    public CommentUnifiedCreateRequest withImages(String... imageUrls) {
        this.images = List.of(imageUrls);
        return this;
    }

    /**
     * 链式设置扩展数据
     */
    public CommentUnifiedCreateRequest withExtraData(String extraData) {
        this.extraData = extraData;
        return this;
    }

    /**
     * 链式设置位置信息
     */
    public CommentUnifiedCreateRequest withLocation(String ipAddress, String deviceInfo, String location) {
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.location = location;
        return this;
    }

    /**
     * 判断是否为根评论
     */
    public boolean isRootComment() {
        return parentCommentId == null || parentCommentId == 0L;
    }

    /**
     * 判断是否为回复评论
     */
    public boolean isReplyComment() {
        return parentCommentId != null && parentCommentId > 0L;
    }
} 