package com.gig.collide.api.category.response;

import com.gig.collide.base.response.BaseResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 分类统一操作响应
 * 用于分类创建、修改、删除等操作的响应
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoryUnifiedOperateResponse extends BaseResponse {

    /**
     * 操作涉及的分类ID
     */
    private Long categoryId;

    /**
     * 操作类型
     * CREATE, UPDATE, DELETE, MOVE, SORT, ENABLE, DISABLE等
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
    public static CategoryUnifiedOperateResponse success(String operationType, Long categoryId) {
        CategoryUnifiedOperateResponse response = new CategoryUnifiedOperateResponse();
        response.setSuccess(true);
        response.setOperationType(operationType);
        response.setCategoryId(categoryId);
        response.setResponseMessage("操作成功");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建成功响应（带结果数据）
     */
    public static CategoryUnifiedOperateResponse success(String operationType, Long categoryId, Object resultData) {
        CategoryUnifiedOperateResponse response = success(operationType, categoryId);
        response.setResultData(resultData);
        return response;
    }

    /**
     * 创建成功响应（带影响行数）
     */
    public static CategoryUnifiedOperateResponse success(String operationType, Long categoryId, 
                                                       Object resultData, Integer affectedRows) {
        CategoryUnifiedOperateResponse response = success(operationType, categoryId, resultData);
        response.setAffectedRows(affectedRows);
        return response;
    }

    /**
     * 创建失败响应
     */
    public static CategoryUnifiedOperateResponse fail(String operationType, String message) {
        CategoryUnifiedOperateResponse response = new CategoryUnifiedOperateResponse();
        response.setSuccess(false);
        response.setOperationType(operationType);
        response.setResponseMessage(message);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    /**
     * 创建失败响应（带分类ID）
     */
    public static CategoryUnifiedOperateResponse fail(String operationType, Long categoryId, String message) {
        CategoryUnifiedOperateResponse response = fail(operationType, message);
        response.setCategoryId(categoryId);
        return response;
    }

    /**
     * 创建失败响应（带错误码）
     */
    public static CategoryUnifiedOperateResponse fail(String operationType, Long categoryId, 
                                                     String responseCode, String message) {
        CategoryUnifiedOperateResponse response = fail(operationType, categoryId, message);
        response.setResponseCode(responseCode);
        return response;
    }

    // ===================== 便捷方法 =====================

    /**
     * 创建分类成功响应
     */
    public static CategoryUnifiedOperateResponse createSuccess(Long categoryId) {
        return success("CREATE", categoryId).withMessage("分类创建成功");
    }

    /**
     * 创建分类成功响应（带结果数据）
     */
    public static CategoryUnifiedOperateResponse createSuccess(Long categoryId, Object categoryData) {
        return success("CREATE", categoryId, categoryData).withMessage("分类创建成功");
    }

    /**
     * 修改分类成功响应
     */
    public static CategoryUnifiedOperateResponse updateSuccess(Long categoryId) {
        return success("UPDATE", categoryId).withMessage("分类修改成功");
    }

    /**
     * 修改分类成功响应（带结果数据）
     */
    public static CategoryUnifiedOperateResponse updateSuccess(Long categoryId, Object categoryData) {
        return success("UPDATE", categoryId, categoryData).withMessage("分类修改成功");
    }

    /**
     * 删除分类成功响应
     */
    public static CategoryUnifiedOperateResponse deleteSuccess(Long categoryId) {
        return success("DELETE", categoryId).withMessage("分类删除成功");
    }

    /**
     * 移动分类成功响应
     */
    public static CategoryUnifiedOperateResponse moveSuccess(Long categoryId) {
        return success("MOVE", categoryId).withMessage("分类移动成功");
    }

    /**
     * 移动分类成功响应（带移动信息）
     */
    public static CategoryUnifiedOperateResponse moveSuccess(Long categoryId, Long oldParentId, Long newParentId) {
        Object moveInfo = java.util.Map.of(
            "oldParentId", oldParentId,
            "newParentId", newParentId
        );
        return success("MOVE", categoryId, moveInfo).withMessage("分类移动成功");
    }

    /**
     * 排序分类成功响应
     */
    public static CategoryUnifiedOperateResponse sortSuccess(Long categoryId) {
        return success("SORT", categoryId).withMessage("分类排序成功");
    }

    /**
     * 排序分类成功响应（带排序信息）
     */
    public static CategoryUnifiedOperateResponse sortSuccess(Long categoryId, Integer oldOrder, Integer newOrder) {
        Object sortInfo = java.util.Map.of(
            "oldSortOrder", oldOrder,
            "newSortOrder", newOrder
        );
        return success("SORT", categoryId, sortInfo).withMessage("分类排序成功");
    }

    /**
     * 启用分类成功响应
     */
    public static CategoryUnifiedOperateResponse enableSuccess(Long categoryId) {
        return success("ENABLE", categoryId).withMessage("分类启用成功");
    }

    /**
     * 禁用分类成功响应
     */
    public static CategoryUnifiedOperateResponse disableSuccess(Long categoryId) {
        return success("DISABLE", categoryId).withMessage("分类禁用成功");
    }

    /**
     * 批量操作成功响应
     */
    public static CategoryUnifiedOperateResponse batchSuccess(String operationType, Integer affectedRows) {
        CategoryUnifiedOperateResponse response = success(operationType, null);
        response.setAffectedRows(affectedRows);
        response.setResponseMessage(String.format("批量%s成功，影响%d条记录", getOperationName(operationType), affectedRows));
        return response;
    }

    /**
     * 同步操作成功响应
     */
    public static CategoryUnifiedOperateResponse syncSuccess(Long categoryId) {
        return success("SYNC", categoryId).withMessage("分类数据同步成功");
    }

    /**
     * 同步操作成功响应（批量）
     */
    public static CategoryUnifiedOperateResponse syncSuccess(Integer syncCount) {
        CategoryUnifiedOperateResponse response = success("SYNC", null);
        response.setAffectedRows(syncCount);
        response.setResponseMessage(String.format("分类数据同步成功，同步%d条记录", syncCount));
        return response;
    }

    // ===================== 失败响应便捷方法 =====================

    /**
     * 分类不存在错误响应
     */
    public static CategoryUnifiedOperateResponse categoryNotFound(Long categoryId) {
        return fail("QUERY", categoryId, "CATEGORY_NOT_FOUND", "分类不存在");
    }

    /**
     * 分类名称重复错误响应
     */
    public static CategoryUnifiedOperateResponse nameConflict(String operationType, Long categoryId) {
        return fail(operationType, categoryId, "NAME_CONFLICT", "同级分类名称不能重复");
    }

    /**
     * 分类层级超限错误响应
     */
    public static CategoryUnifiedOperateResponse levelExceeded(String operationType, Long categoryId) {
        return fail(operationType, categoryId, "LEVEL_EXCEEDED", "分类层级不能超过限制");
    }

    /**
     * 分类包含子分类错误响应
     */
    public static CategoryUnifiedOperateResponse hasChildren(Long categoryId) {
        return fail("DELETE", categoryId, "HAS_CHILDREN", "分类包含子分类，无法删除");
    }

    /**
     * 分类包含内容错误响应
     */
    public static CategoryUnifiedOperateResponse hasContent(Long categoryId) {
        return fail("DELETE", categoryId, "HAS_CONTENT", "分类包含内容，无法删除");
    }

    /**
     * 权限不足错误响应
     */
    public static CategoryUnifiedOperateResponse noPermission(String operationType, Long categoryId) {
        return fail(operationType, categoryId, "NO_PERMISSION", "无权限执行此操作");
    }

    // ===================== 链式方法 =====================

    /**
     * 链式设置消息
     */
    public CategoryUnifiedOperateResponse withMessage(String message) {
        this.setResponseMessage(message);
        return this;
    }

    /**
     * 链式设置结果数据
     */
    public CategoryUnifiedOperateResponse withResultData(Object resultData) {
        this.setResultData(resultData);
        return this;
    }

    /**
     * 链式设置影响行数
     */
    public CategoryUnifiedOperateResponse withAffectedRows(Integer affectedRows) {
        this.setAffectedRows(affectedRows);
        return this;
    }

    /**
     * 链式设置错误码
     */
    public CategoryUnifiedOperateResponse withResponseCode(String responseCode) {
        this.setResponseCode(responseCode);
        return this;
    }

    // ===================== 辅助方法 =====================

    /**
     * 获取操作类型的中文名称
     */
    private static String getOperationName(String operationType) {
        switch (operationType) {
            case "CREATE": return "创建";
            case "UPDATE": return "修改";
            case "DELETE": return "删除";
            case "MOVE": return "移动";
            case "SORT": return "排序";
            case "ENABLE": return "启用";
            case "DISABLE": return "禁用";
            case "SYNC": return "同步";
            default: return "操作";
        }
    }

    /**
     * 判断操作是否成功
     */
    public boolean isOperationSuccess() {
        return Boolean.TRUE.equals(getSuccess());
    }

    /**
     * 判断是否为创建操作
     */
    public boolean isCreateOperation() {
        return "CREATE".equals(operationType);
    }

    /**
     * 判断是否为更新操作
     */
    public boolean isUpdateOperation() {
        return "UPDATE".equals(operationType);
    }

    /**
     * 判断是否为删除操作
     */
    public boolean isDeleteOperation() {
        return "DELETE".equals(operationType);
    }

    /**
     * 判断是否为批量操作
     */
    public boolean isBatchOperation() {
        return affectedRows != null && affectedRows > 1;
    }
} 