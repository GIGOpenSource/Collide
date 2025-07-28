package com.gig.collide.api.category.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分类操作类型枚举
 * 定义分类的各种操作类型
 *
 * @author Collide Team
 * @version 1.0
 * @since 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum CategoryOperationTypeEnum {

    /**
     * 创建分类
     */
    CREATE("CREATE", "创建分类", "新建一个分类"),

    /**
     * 更新分类
     */
    UPDATE("UPDATE", "更新分类", "修改分类信息"),

    /**
     * 删除分类
     */
    DELETE("DELETE", "删除分类", "删除一个分类"),

    /**
     * 移动分类
     */
    MOVE("MOVE", "移动分类", "将分类移动到其他父分类下"),

    /**
     * 排序分类
     */
    SORT("SORT", "排序分类", "调整分类的排序顺序"),

    /**
     * 启用分类
     */
    ENABLE("ENABLE", "启用分类", "将分类设置为激活状态"),

    /**
     * 禁用分类
     */
    DISABLE("DISABLE", "禁用分类", "将分类设置为禁用状态"),

    /**
     * 查询分类
     */
    QUERY("QUERY", "查询分类", "查询分类信息"),

    /**
     * 批量操作
     */
    BATCH("BATCH", "批量操作", "批量处理多个分类"),

    /**
     * 导入分类
     */
    IMPORT("IMPORT", "导入分类", "导入分类数据"),

    /**
     * 导出分类
     */
    EXPORT("EXPORT", "导出分类", "导出分类数据"),

    /**
     * 同步分类
     */
    SYNC("SYNC", "同步分类", "同步分类数据");

    /**
     * 操作代码
     */
    private final String code;

    /**
     * 操作名称
     */
    private final String name;

    /**
     * 操作描述
     */
    private final String description;

    /**
     * 根据代码获取枚举值
     *
     * @param code 操作代码
     * @return 对应的枚举值，未找到返回null
     */
    public static CategoryOperationTypeEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (CategoryOperationTypeEnum operation : values()) {
            if (operation.getCode().equals(code)) {
                return operation;
            }
        }
        return null;
    }

    /**
     * 判断是否为写操作
     *
     * @return true如果为写操作
     */
    public boolean isWriteOperation() {
        return this == CREATE || this == UPDATE || this == DELETE || 
               this == MOVE || this == SORT || this == ENABLE || 
               this == DISABLE || this == BATCH || this == IMPORT || this == SYNC;
    }

    /**
     * 判断是否为读操作
     *
     * @return true如果为读操作
     */
    public boolean isReadOperation() {
        return this == QUERY || this == EXPORT;
    }

    /**
     * 判断是否为危险操作（需要特殊权限）
     *
     * @return true如果为危险操作
     */
    public boolean isDangerousOperation() {
        return this == DELETE || this == BATCH || this == IMPORT;
    }

    /**
     * 判断是否为状态改变操作
     *
     * @return true如果为状态改变操作
     */
    public boolean isStatusChangeOperation() {
        return this == ENABLE || this == DISABLE;
    }

    /**
     * 判断是否为结构改变操作
     *
     * @return true如果为结构改变操作
     */
    public boolean isStructureChangeOperation() {
        return this == CREATE || this == DELETE || this == MOVE || this == SORT;
    }
} 