-- =============================================
-- 任务模块简洁版 SQL  
-- 基于简单签到得金币需求设计
-- =============================================

USE collide;

-- 任务模板表（预设任务配置）
DROP TABLE IF EXISTS `t_task_template`;
CREATE TABLE `t_task_template`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `task_name`       VARCHAR(100) NOT NULL COMMENT '任务名称',
    `task_type`       VARCHAR(50)  NOT NULL DEFAULT 'DAILY_CHECKIN' COMMENT '任务类型：DAILY_CHECKIN-每日签到',
    `task_desc`       VARCHAR(500) COMMENT '任务描述',
    `reward_coins`    INT          NOT NULL DEFAULT 10 COMMENT '基础奖励金币数量',
    `bonus_rule`      VARCHAR(200) COMMENT '奖励规则说明：如连续7天翻倍',
    `is_repeatable`   TINYINT      NOT NULL DEFAULT 1 COMMENT '是否可重复：1-是，0-否',
    `sort_order`      INT          NOT NULL DEFAULT 0 COMMENT '排序权重',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_task_type` (`task_type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='任务模板表';

-- 用户签到记录表
DROP TABLE IF EXISTS `t_user_checkin_record`;
CREATE TABLE `t_user_checkin_record`
(
    `id`                BIGINT    NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id`           BIGINT    NOT NULL COMMENT '用户ID',
    `task_template_id`  BIGINT    NOT NULL COMMENT '任务模板ID',
    `checkin_date`      DATE      NOT NULL COMMENT '签到日期',
    `reward_coins`      INT       NOT NULL DEFAULT 0 COMMENT '获得金币数量',
    `continuous_days`   INT       NOT NULL DEFAULT 1 COMMENT '连续签到天数',
    `is_bonus`          TINYINT   NOT NULL DEFAULT 0 COMMENT '是否获得连续奖励：1-是，0-否',
    `checkin_ip`        VARCHAR(50) COMMENT '签到IP地址',
    `checkin_time`      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
    `create_time`       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_date` (`user_id`, `checkin_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_checkin_date` (`checkin_date`),
    KEY `idx_task_template_id` (`task_template_id`),
    KEY `idx_continuous_days` (`continuous_days`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户签到记录表';

-- =============================================
-- 初始化基础数据
-- =============================================

-- 插入每日签到任务模板
INSERT INTO `t_task_template` (`task_name`, `task_type`, `task_desc`, `reward_coins`, `bonus_rule`, `is_repeatable`, `sort_order`, `status`)
VALUES ('每日签到', 'DAILY_CHECKIN', '每天签到获得金币奖励，连续签到有额外奖励', 10, '连续签到7天可获得双倍奖励(20金币)', 1, 1, 1);

-- =============================================
-- 索引优化说明
-- =============================================
-- uk_user_date: 防止用户重复签到
-- idx_user_id: 查询用户签到历史
-- idx_checkin_date: 按日期统计签到数据
-- idx_continuous_days: 查询连续签到排行榜