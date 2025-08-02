
DROP TABLE IF EXISTS `t_task_definition`;
CREATE TABLE `t_task_definition`
(
    `id`            BIGINT                                      NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_name`     VARCHAR(100)                                NOT NULL COMMENT '任务名称',
    `task_desc`     VARCHAR(500)                                NOT NULL COMMENT '任务描述',
    `task_type`     ENUM ('daily','weekly','achievement')       NOT NULL COMMENT '任务类型',
    `task_category` ENUM ('login','content','social','consume') NOT NULL COMMENT '任务分类',
    `task_action`   VARCHAR(50)                                 NOT NULL COMMENT '任务动作',
    `target_count`  INT                                         NOT NULL DEFAULT 1 COMMENT '目标完成次数',

    -- 奖励配置（JSON格式存储所有奖励）
    `rewards`       JSON                                        NOT NULL COMMENT '奖励配置[{"type":"coin","name":"金币","amount":100}]',

    `sort_order`    INT                                         NOT NULL DEFAULT 0 COMMENT '排序值',
    `is_active`     TINYINT(1)                                  NOT NULL DEFAULT 1 COMMENT '是否启用',
    `start_date`    DATE                                        NULL COMMENT '任务开始日期',
    `end_date`      DATE                                        NULL COMMENT '任务结束日期',
    `create_time`   TIMESTAMP                                   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP                                   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_type_category` (`task_type`, `task_category`),
    KEY `idx_active_sort` (`is_active`, `sort_order`),
    KEY `idx_action` (`task_action`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='任务定义表';


DROP TABLE IF EXISTS `t_user_task`;
CREATE TABLE `t_user_task`
(
    `id`            BIGINT     NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`       BIGINT     NOT NULL COMMENT '用户ID',
    `task_id`       BIGINT     NOT NULL COMMENT '任务ID',
    `task_date`     DATE       NOT NULL COMMENT '任务日期（用于每日/周任务）',

    -- 任务进度
    `current_count` INT        NOT NULL DEFAULT 0 COMMENT '当前完成次数',
    `is_completed`  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已完成',
    `is_rewarded`   TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已领取奖励',

    -- 奖励发放状态（JSON格式存储）
    `reward_status` JSON COMMENT '奖励发放状态[{"type":"coin","status":"success"}]',

    `complete_time` TIMESTAMP  NULL COMMENT '完成时间',
    `reward_time`   TIMESTAMP  NULL COMMENT '奖励领取时间',
    `create_time`   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_task_date` (`user_id`, `task_id`, `task_date`),
    KEY `idx_user_date` (`user_id`, `task_date`),
    KEY `idx_task_complete` (`task_id`, `is_completed`),
    KEY `idx_task_rewarded` (`task_id`, `is_rewarded`),
    KEY `idx_user_complete` (`user_id`, `is_completed`),
    KEY `idx_user_rewarded` (`user_id`, `is_rewarded`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户任务记录表';