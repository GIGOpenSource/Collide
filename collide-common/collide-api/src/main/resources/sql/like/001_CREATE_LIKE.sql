-- -----------------------------------
-- 统一点赞表
-- 处理所有类型的点赞，无连表依赖
-- 统计信息通过冗余字段存储在各主表中
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_like` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标对象ID（内容ID、评论ID等）',
    `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型：CONTENT、COMMENT、SOCIAL_POST、USER',
    `action_type` TINYINT NOT NULL DEFAULT 1 COMMENT '操作类型：1-点赞，0-取消，-1-点踩',
    `ip_address` VARCHAR(45) COMMENT '点赞IP地址',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target_type` (`user_id`, `target_id`, `target_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_action_type` (`action_type`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_target_action` (`target_id`, `target_type`, `action_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一点赞表';

-- -----------------------------------
-- 插入基础测试数据
-- -----------------------------------

-- 插入统一点赞数据
INSERT IGNORE INTO `t_like` (`id`, `user_id`, `target_id`, `target_type`, `action_type`) VALUES
-- 内容点赞
(1, 1, 1, 'CONTENT', 1),
(2, 2, 1, 'CONTENT', 1),
(3, 3, 1, 'CONTENT', 1),
(4, 1, 2, 'CONTENT', 1),
(5, 2, 2, 'CONTENT', -1),
-- 评论点赞
(6, 1, 1, 'COMMENT', 1),
(7, 2, 1, 'COMMENT', 1),
(8, 3, 3, 'COMMENT', 1),
(9, 2, 4, 'COMMENT', 1); 