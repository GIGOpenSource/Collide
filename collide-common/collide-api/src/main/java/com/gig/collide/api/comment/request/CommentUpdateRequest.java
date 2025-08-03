package com.gig.collide.api.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 评论更新请求 - 简洁版
 * 支持评论内容、状态和统计数据更新
 * 
 * @author Collide
 * @version 2.0.0 (简洁版)
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentUpdateRequest implements Serializable {

    /**
     * 评论ID
     */
    @NotNull(message = "评论ID不能为空")
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 状态：NORMAL、HIDDEN、DELETED
     */
    private String status;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 回复数
     */
    private Integer replyCount;

    /**
     * 操作人ID（用于权限验证）
     */
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;

    /**
     * 更新类型：CONTENT、STATUS、STATS
     */
    private String updateType;

    // =================== 业务方法 ===================

    /**
     * 是否更新内容
     */
    public boolean isUpdateContent() {
        return "CONTENT".equals(updateType) || content != null;
    }

    /**
     * 是否更新状态
     */
    public boolean isUpdateStatus() {
        return "STATUS".equals(updateType) || status != null;
    }

    /**
     * 是否更新统计
     */
    public boolean isUpdateStats() {
        return "STATS".equals(updateType) || likeCount != null || replyCount != null;
    }

    /**
     * 验证状态值
     */
    public boolean isValidStatus() {
        if (status == null) {
            return true;
        }
        return "NORMAL".equals(status) || "HIDDEN".equals(status) || "DELETED".equals(status);
    }

    /**
     * 获取评论内容长度
     */
    public int getContentLength() {
        return content != null ? content.trim().length() : 0;
    }

    /**
     * 是否有效的更新请求
     */
    public boolean isValidUpdate() {
        return content != null || status != null || likeCount != null || replyCount != null;
    }
}