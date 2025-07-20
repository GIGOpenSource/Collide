package com.gig.collide.api.tag.constant;

/**
 * 标签操作类型枚举
 * @author GIG
 */
public enum TagOperateTypeEnum {

    /**
     * 创建标签
     */
    CREATE_TAG,

    /**
     * 修改标签
     */
    UPDATE_TAG,

    /**
     * 删除标签
     */
    DELETE_TAG,

    /**
     * 启用标签
     */
    ENABLE_TAG,

    /**
     * 禁用标签
     */
    DISABLE_TAG,

    /**
     * 用户打标签
     */
    USER_TAG,

    /**
     * 用户取消标签
     */
    USER_UNTAG,

    /**
     * 批量打标签
     */
    BATCH_TAG,

    /**
     * 标签合并
     */
    MERGE_TAG,

    /**
     * 标签分类调整
     */
    RECLASSIFY_TAG,

    /**
     * 标签审核通过
     */
    APPROVE_TAG,

    /**
     * 标签审核拒绝
     */
    REJECT_TAG;
} 