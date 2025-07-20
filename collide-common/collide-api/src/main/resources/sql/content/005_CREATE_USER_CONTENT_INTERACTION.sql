-- 用户内容交互表
CREATE TABLE `user_content_interaction` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '交互ID',
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `content_id` bigint(20) NOT NULL COMMENT '内容ID',
    `interaction_type` varchar(20) NOT NULL COMMENT '交互类型：view-查看，like-点赞，collect-收藏，share-分享，comment-评论',
    `interaction_value` varchar(100) COMMENT '交互值（如评论内容、分享平台等）',
    `duration` int(11) COMMENT '查看时长（秒，仅view类型有效）',
    `completion_rate` decimal(5,4) COMMENT '完成率（仅view类型有效）',
    `device_type` varchar(50) COMMENT '设备类型',
    `ip_address` varchar(50) COMMENT 'IP地址',
    `user_agent` varchar(500) COMMENT '用户代理',
    `extend_info` json COMMENT '扩展信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_content_type` (`user_id`, `content_id`, `interaction_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_interaction_type` (`interaction_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户内容交互表'; 