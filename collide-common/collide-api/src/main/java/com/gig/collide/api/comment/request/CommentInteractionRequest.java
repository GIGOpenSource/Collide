package com.gig.collide.api.comment.request;

import com.gig.collide.api.comment.enums.LikeTypeEnum;
import com.gig.collide.api.comment.enums.ReportTypeEnum;
import com.gig.collide.base.request.BaseRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 评论交互请求
 * 用于处理点赞、点踩、举报等用户交互操作
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
public class CommentInteractionRequest extends BaseRequest {

    /**
     * 评论ID（必填）
     */
    @NotNull(message = "评论ID不能为空")
    private Long commentId;

    /**
     * 用户ID（必填）
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 点赞类型（点赞/点踩操作时必填）
     */
    private LikeTypeEnum likeType;

    /**
     * 举报类型（举报操作时必填）
     */
    private ReportTypeEnum reportType;

    /**
     * 举报原因（举报操作时可选）
     */
    @Size(max = 500, message = "举报原因长度不能超过500个字符")
    private String reportReason;

    /**
     * 举报详情（举报操作时可选）
     */
    @Size(max = 2000, message = "举报详情长度不能超过2000个字符")
    private String reportContent;

    /**
     * 操作来源（可选）
     * 如：web, mobile, app等
     */
    private String source;

    /**
     * 设备信息（可选）
     */
    private String deviceInfo;

    /**
     * IP地址（可选）
     */
    private String ipAddress;

    // ===================== 便捷构造器 =====================

    /**
     * 创建点赞请求
     */
    public static CommentInteractionRequest like(Long commentId, Long userId) {
        CommentInteractionRequest request = new CommentInteractionRequest();
        request.setCommentId(commentId);
        request.setUserId(userId);
        request.setLikeType(LikeTypeEnum.LIKE);
        return request;
    }

    /**
     * 创建点踩请求
     */
    public static CommentInteractionRequest dislike(Long commentId, Long userId) {
        CommentInteractionRequest request = new CommentInteractionRequest();
        request.setCommentId(commentId);
        request.setUserId(userId);
        request.setLikeType(LikeTypeEnum.DISLIKE);
        return request;
    }

    /**
     * 创建举报请求
     */
    public static CommentInteractionRequest report(Long commentId, Long userId, 
                                                  ReportTypeEnum reportType, String reportReason) {
        CommentInteractionRequest request = new CommentInteractionRequest();
        request.setCommentId(commentId);
        request.setUserId(userId);
        request.setReportType(reportType);
        request.setReportReason(reportReason);
        return request;
    }

    /**
     * 创建举报请求（带详情）
     */
    public static CommentInteractionRequest reportWithContent(Long commentId, Long userId, 
                                                             ReportTypeEnum reportType, 
                                                             String reportReason, String reportContent) {
        CommentInteractionRequest request = report(commentId, userId, reportType, reportReason);
        request.setReportContent(reportContent);
        return request;
    }

    /**
     * 链式设置来源
     */
    public CommentInteractionRequest withSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * 链式设置设备信息
     */
    public CommentInteractionRequest withDevice(String deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    /**
     * 链式设置IP地址
     */
    public CommentInteractionRequest withIp(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    /**
     * 判断是否为点赞操作
     */
    public boolean isLikeOperation() {
        return likeType != null && likeType.isLike();
    }

    /**
     * 判断是否为点踩操作
     */
    public boolean isDislikeOperation() {
        return likeType != null && likeType.isDislike();
    }

    /**
     * 判断是否为举报操作
     */
    public boolean isReportOperation() {
        return reportType != null;
    }
} 