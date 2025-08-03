CREATE TABLE `t_task_template`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_name`    VARCHAR(100) NOT NULL COMMENT '任务名称',
    `task_type`    VARCHAR(50)  NOT NULL DEFAULT 'DAILY_CHECKIN' COMMENT '任务类型',
    `reward_coins` INT          NOT NULL DEFAULT 10 COMMENT '奖励金币数量',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用 0-禁用',
    `create_time`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='任务模板表';

CREATE TABLE `t_user_checkin_record`
(
    `id`              BIGINT    NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`         BIGINT    NOT NULL COMMENT '用户ID',
    `checkin_date`    DATE      NOT NULL COMMENT '签到日期',
    `reward_coins`    INT       NOT NULL DEFAULT 0 COMMENT '获得金币',
    `continuous_days` INT       NOT NULL DEFAULT 1 COMMENT '连续签到天数',
    `checkin_time`    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_checkin_date` (`checkin_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户签到记录表';