package com.gig.collide.api.task.constant;

/**
 * 任务类型常量 - 签到专用版
 * 对应 t_task_template 表的 task_type 字段
 * 
 * @author GIG Team
 * @version 1.0.0
 * @since 2024-01-16
 */
public final class TaskTypeConstant {

    private TaskTypeConstant() {
        // 私有构造函数，防止实例化
    }

    /** 每日签到任务 */
    public static final String DAILY_CHECKIN = "DAILY_CHECKIN";

    /**
     * 获取任务类型描述
     */
    public static String getTaskTypeDesc(String taskType) {
        if (DAILY_CHECKIN.equals(taskType)) {
            return "每日签到";
        }
        return "未知任务类型";
    }

    /**
     * 验证任务类型是否有效
     */
    public static boolean isValidTaskType(String taskType) {
        return DAILY_CHECKIN.equals(taskType);
    }
}