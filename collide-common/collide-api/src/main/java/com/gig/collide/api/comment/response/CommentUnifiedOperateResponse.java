package com.gig.collide.api.comment.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 评论统一操作响应
 * 用于评论创建、修改、删除、点赞等操作的响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CommentUnifiedOperateResponse extends BaseResponse {

    /**
     * 操作涉及的评论ID
     */
    private Long commentId;

    /**
     * 操作类型
     * CREATE, UPDATE, DELETE, LIKE, UNLIKE, DISLIKE, UNDISLIKE, REPORT, PIN, UNPIN, AUDIT等
     */
    private String operationType;

    /**
     * 操作结果数据（可选）
     */
    private Object resultData;

    /**
     * 受影响的记录数
     */
    private Integer affectedRows;

    /**
     * 操作时间戳
     */
    private Long timestamp;

    /**
     * 目标对象ID（如内容ID、动态ID等）
     */
    private Long targetId;

    /**
     * 操作用户ID
     */
    private Long operatorId;

    /**
     * 创建成功响应
     */
    public static CommentUnifiedOperateResponse success(String operationType, Long commentId) {
        CommentUnifiedOperateResponse response = new CommentUnifiedOperateResponse();
        response.setSuccess(true);
        response.setOperationType(operationType);
        response.setCommentId(commentId);
        response.setResponseMessage("操作成功");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建成功响应（带结果数据）
     */
    public static CommentUnifiedOperateResponse success(String operationType, Long commentId, Object resultData) {
        CommentUnifiedOperateResponse response = success(operationType, commentId);
        response.setResultData(resultData);
        return response;
    }

    /**
     * 创建成功响应（带影响行数）
     */
    public static CommentUnifiedOperateResponse success(String operationType, Long commentId, 
                                                       Object resultData, Integer affectedRows) {
        CommentUnifiedOperateResponse response = success(operationType, commentId, resultData);
        response.setAffectedRows(affectedRows);
        return response;
    }

    /**
     * 创建成功响应（完整参数）
     */
    public static CommentUnifiedOperateResponse success(String operationType, Long commentId, 
                                                       Long targetId, Long operatorId, Object resultData) {
        CommentUnifiedOperateResponse response = success(operationType, commentId, resultData);
        response.setTargetId(targetId);
        response.setOperatorId(operatorId);
        return response;
    }

    /**
     * 创建失败响应
     */
    public static CommentUnifiedOperateResponse fail(String operationType, String message) {
        CommentUnifiedOperateResponse response = new CommentUnifiedOperateResponse();
        response.setSuccess(false);
        response.setOperationType(operationType);
        response.setResponseMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建失败响应（带评论ID）
     */
    public static CommentUnifiedOperateResponse fail(String operationType, Long commentId, String message) {
        CommentUnifiedOperateResponse response = fail(operationType, message);
        response.setCommentId(commentId);
        return response;
    }

    /**
     * 创建失败响应（带错误码）
     */
    public static CommentUnifiedOperateResponse fail(String operationType, String code, String message) {
        CommentUnifiedOperateResponse response = fail(operationType, message);
        response.setResponseCode(code);
        return response;
    }

    // ===================== 便捷方法 =====================

    /**
     * 创建评论成功响应
     */
    public static CommentUnifiedOperateResponse createSuccess(Long commentId, Long targetId, Long operatorId) {
        return success("CREATE", commentId, targetId, operatorId, null)
                .withMessage("评论创建成功");
    }

    /**
     * 修改评论成功响应
     */
    public static CommentUnifiedOperateResponse updateSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("UPDATE", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("评论修改成功");
    }

    /**
     * 删除评论成功响应
     */
    public static CommentUnifiedOperateResponse deleteSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("DELETE", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("评论删除成功");
    }

    /**
     * 点赞成功响应
     */
    public static CommentUnifiedOperateResponse likeSuccess(Long commentId, Long userId) {
        CommentUnifiedOperateResponse response = success("LIKE", commentId);
        response.setOperatorId(userId);
        return response.withMessage("点赞成功");
    }

    /**
     * 取消点赞成功响应
     */
    public static CommentUnifiedOperateResponse unlikeSuccess(Long commentId, Long userId) {
        CommentUnifiedOperateResponse response = success("UNLIKE", commentId);
        response.setOperatorId(userId);
        return response.withMessage("取消点赞成功");
    }

    /**
     * 点踩成功响应
     */
    public static CommentUnifiedOperateResponse dislikeSuccess(Long commentId, Long userId) {
        CommentUnifiedOperateResponse response = success("DISLIKE", commentId);
        response.setOperatorId(userId);
        return response.withMessage("点踩成功");
    }

    /**
     * 取消点踩成功响应
     */
    public static CommentUnifiedOperateResponse undislikeSuccess(Long commentId, Long userId) {
        CommentUnifiedOperateResponse response = success("UNDISLIKE", commentId);
        response.setOperatorId(userId);
        return response.withMessage("取消点踩成功");
    }

    /**
     * 举报成功响应
     */
    public static CommentUnifiedOperateResponse reportSuccess(Long commentId, Long userId) {
        CommentUnifiedOperateResponse response = success("REPORT", commentId);
        response.setOperatorId(userId);
        return response.withMessage("举报成功");
    }

    /**
     * 置顶成功响应
     */
    public static CommentUnifiedOperateResponse pinSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("PIN", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("置顶成功");
    }

    /**
     * 取消置顶成功响应
     */
    public static CommentUnifiedOperateResponse unpinSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("UNPIN", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("取消置顶成功");
    }

    /**
     * 审核成功响应
     */
    public static CommentUnifiedOperateResponse auditSuccess(Long commentId, Long operatorId, String auditResult) {
        CommentUnifiedOperateResponse response = success("AUDIT", commentId, auditResult);
        response.setOperatorId(operatorId);
        return response.withMessage("审核完成");
    }

    /**
     * 设置精华成功响应
     */
    public static CommentUnifiedOperateResponse essenceSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("ESSENCE", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("设置精华成功");
    }

    /**
     * 设置热门成功响应
     */
    public static CommentUnifiedOperateResponse hotSuccess(Long commentId, Long operatorId) {
        CommentUnifiedOperateResponse response = success("HOT", commentId);
        response.setOperatorId(operatorId);
        return response.withMessage("设置热门成功");
    }

    /**
     * 链式设置消息
     */
    public CommentUnifiedOperateResponse withMessage(String message) {
        this.setResponseMessage(message);
        return this;
    }

    /**
     * 链式设置结果数据
     */
    public CommentUnifiedOperateResponse withResultData(Object resultData) {
        this.setResultData(resultData);
        return this;
    }

    /**
     * 链式设置影响行数
     */
    public CommentUnifiedOperateResponse withAffectedRows(Integer affectedRows) {
        this.setAffectedRows(affectedRows);
        return this;
    }

    /**
     * 链式设置目标ID
     */
    public CommentUnifiedOperateResponse withTargetId(Long targetId) {
        this.setTargetId(targetId);
        return this;
    }

    /**
     * 链式设置操作者ID
     */
    public CommentUnifiedOperateResponse withOperatorId(Long operatorId) {
        this.setOperatorId(operatorId);
        return this;
    }

    /**
     * 判断操作是否成功
     *
     * @return true如果操作成功
     */
    public boolean isOperationSuccess() {
        return Boolean.TRUE.equals(getSuccess());
    }

    /**
     * 判断是否有结果数据
     *
     * @return true如果有结果数据
     */
    public boolean hasResultData() {
        return resultData != null;
    }
} 