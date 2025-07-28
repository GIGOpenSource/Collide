package com.gig.collide.api.content.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 内容统一操作响应
 * 用于内容创建、修改、删除等操作的响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class ContentUnifiedOperateResponse extends BaseResponse {

    /**
     * 操作涉及的内容ID
     */
    private Long contentId;

    /**
     * 操作类型
     * CREATE, UPDATE, DELETE, LIKE, UNLIKE, FAVORITE, UNFAVORITE, SHARE, PUBLISH, OFFLINE等
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
     * 创建成功响应
     */
    public static ContentUnifiedOperateResponse success(String operationType, Long contentId) {
        ContentUnifiedOperateResponse response = new ContentUnifiedOperateResponse();
        response.setSuccess(true);
        response.setOperationType(operationType);
        response.setContentId(contentId);
        response.setResponseMessage("操作成功");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建成功响应（带结果数据）
     */
    public static ContentUnifiedOperateResponse success(String operationType, Long contentId, Object resultData) {
        ContentUnifiedOperateResponse response = success(operationType, contentId);
        response.setResultData(resultData);
        return response;
    }

    /**
     * 创建成功响应（带影响行数）
     */
    public static ContentUnifiedOperateResponse success(String operationType, Long contentId, 
                                                       Object resultData, Integer affectedRows) {
        ContentUnifiedOperateResponse response = success(operationType, contentId, resultData);
        response.setAffectedRows(affectedRows);
        return response;
    }

    /**
     * 创建失败响应
     */
    public static ContentUnifiedOperateResponse fail(String operationType, String message) {
        ContentUnifiedOperateResponse response = new ContentUnifiedOperateResponse();
        response.setSuccess(false);
        response.setOperationType(operationType);
        response.setResponseMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建失败响应（带内容ID）
     */
    public static ContentUnifiedOperateResponse fail(String operationType, Long contentId, String message) {
        ContentUnifiedOperateResponse response = fail(operationType, message);
        response.setContentId(contentId);
        return response;
    }

    // ===================== 便捷方法 =====================

    /**
     * 创建内容成功响应
     */
    public static ContentUnifiedOperateResponse createSuccess(Long contentId) {
        return success("CREATE", contentId).withMessage("内容创建成功");
    }

    /**
     * 修改内容成功响应
     */
    public static ContentUnifiedOperateResponse updateSuccess(Long contentId) {
        return success("UPDATE", contentId).withMessage("内容修改成功");
    }

    /**
     * 删除内容成功响应
     */
    public static ContentUnifiedOperateResponse deleteSuccess(Long contentId) {
        return success("DELETE", contentId).withMessage("内容删除成功");
    }

    /**
     * 点赞成功响应
     */
    public static ContentUnifiedOperateResponse likeSuccess(Long contentId) {
        return success("LIKE", contentId).withMessage("点赞成功");
    }

    /**
     * 取消点赞成功响应
     */
    public static ContentUnifiedOperateResponse unlikeSuccess(Long contentId) {
        return success("UNLIKE", contentId).withMessage("取消点赞成功");
    }

    /**
     * 收藏成功响应
     */
    public static ContentUnifiedOperateResponse favoriteSuccess(Long contentId) {
        return success("FAVORITE", contentId).withMessage("收藏成功");
    }

    /**
     * 取消收藏成功响应
     */
    public static ContentUnifiedOperateResponse unfavoriteSuccess(Long contentId) {
        return success("UNFAVORITE", contentId).withMessage("取消收藏成功");
    }

    /**
     * 分享成功响应
     */
    public static ContentUnifiedOperateResponse shareSuccess(Long contentId) {
        return success("SHARE", contentId).withMessage("分享成功");
    }

    /**
     * 发布成功响应
     */
    public static ContentUnifiedOperateResponse publishSuccess(Long contentId) {
        return success("PUBLISH", contentId).withMessage("内容发布成功");
    }

    /**
     * 下线成功响应
     */
    public static ContentUnifiedOperateResponse offlineSuccess(Long contentId) {
        return success("OFFLINE", contentId).withMessage("内容下线成功");
    }

    /**
     * 链式设置消息
     */
    public ContentUnifiedOperateResponse withMessage(String message) {
        this.setResponseMessage(message);
        return this;
    }

    /**
     * 链式设置结果数据
     */
    public ContentUnifiedOperateResponse withResultData(Object resultData) {
        this.setResultData(resultData);
        return this;
    }

    /**
     * 链式设置影响行数
     */
    public ContentUnifiedOperateResponse withAffectedRows(Integer affectedRows) {
        this.setAffectedRows(affectedRows);
        return this;
    }
} 