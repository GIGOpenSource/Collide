package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.enums.AuditStatusEnum;
import com.gig.collide.api.comment.enums.CommentStatusEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * 评论修改请求
 * 用于修改已存在的评论
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
public class CommentUnifiedModifyRequest extends BaseRequest {

    /**
     * 评论ID（必填）
     */
    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    /**
     * 操作用户ID（必填）
     */
    @NotNull(message = "操作用户ID不能为空")
    private Long operatorId;

    /**
     * 评论内容（可选）
     */
    @Size(max = 2000, message = "评论内容长度不能超过2000个字符")
    private String content;

    /**
     * 评论状态（可选）
     */
    private CommentStatusEnum status;

    /**
     * 审核状态（可选）
     */
    private AuditStatusEnum auditStatus;

    /**
     * 审核原因（可选）
     */
    @Size(max = 500, message = "审核原因长度不能超过500个字符")
    private String auditReason;

    /**
     * 是否置顶（可选）
     */
    private Boolean isPinned;

    /**
     * 是否热门（可选）
     */
    private Boolean isHot;

    /**
     * 是否精华（可选）
     */
    private Boolean isEssence;

    /**
     * 质量分数（可选）
     */
    private Double qualityScore;

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
     * 修改原因/备注（可选）
     */
    @Size(max = 500, message = "修改原因长度不能超过500个字符")
    private String modifyReason;

    // ===================== 便捷构造器 =====================

    /**
     * 修改评论内容
     */
    public static CommentUnifiedModifyRequest content(Long commentId, Long operatorId, String content) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setContent(content);
        return request;
    }

    /**
     * 修改评论状态
     */
    public static CommentUnifiedModifyRequest status(Long commentId, Long operatorId, 
                                                    CommentStatusEnum status, String reason) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setStatus(status);
        request.setModifyReason(reason);
        return request;
    }

    /**
     * 审核评论
     */
    public static CommentUnifiedModifyRequest audit(Long commentId, Long operatorId, 
                                                   AuditStatusEnum auditStatus, String auditReason) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setAuditStatus(auditStatus);
        request.setAuditReason(auditReason);
        return request;
    }

    /**
     * 设置置顶
     */
    public static CommentUnifiedModifyRequest pin(Long commentId, Long operatorId, boolean pin) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setIsPinned(pin);
        return request;
    }

    /**
     * 设置热门
     */
    public static CommentUnifiedModifyRequest hot(Long commentId, Long operatorId, boolean hot) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setIsHot(hot);
        return request;
    }

    /**
     * 设置精华
     */
    public static CommentUnifiedModifyRequest essence(Long commentId, Long operatorId, boolean essence) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setIsEssence(essence);
        return request;
    }

    /**
     * 设置质量分数
     */
    public static CommentUnifiedModifyRequest quality(Long commentId, Long operatorId, Double qualityScore) {
        CommentUnifiedModifyRequest request = new CommentUnifiedModifyRequest();
        request.setCommentId(commentId);
        request.setOperatorId(operatorId);
        request.setQualityScore(qualityScore);
        return request;
    }

    /**
     * 链式设置提及用户
     */
    public CommentUnifiedModifyRequest withMentions(Long... userIds) {
        this.mentionUserIds = List.of(userIds);
        return this;
    }

    /**
     * 链式设置图片
     */
    public CommentUnifiedModifyRequest withImages(String... imageUrls) {
        this.images = List.of(imageUrls);
        return this;
    }

    /**
     * 链式设置扩展数据
     */
    public CommentUnifiedModifyRequest withExtraData(String extraData) {
        this.extraData = extraData;
        return this;
    }

    /**
     * 链式设置修改原因
     */
    public CommentUnifiedModifyRequest withReason(String reason) {
        this.modifyReason = reason;
        return this;
    }
} 