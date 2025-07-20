-- 内容操作日志表
CREATE TABLE `content_operation_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `content_id` bigint(20) NOT NULL COMMENT '内容ID',
    `operator_user_id` bigint(20) NOT NULL COMMENT '操作者用户ID',
    `operator_username` varchar(100) COMMENT '操作者用户名',
    `operation_type` varchar(50) NOT NULL COMMENT '操作类型：create-创建，update-编辑，delete-删除，submit_review-提交审核等',
    `operation_desc` varchar(200) COMMENT '操作描述',
    `before_data` text COMMENT '操作前数据（JSON格式）',
    `after_data` text COMMENT '操作后数据（JSON格式）',
    `ip_address` varchar(50) COMMENT '操作者IP地址',
    `user_agent` varchar(500) COMMENT '用户代理',
    `extend_info` json COMMENT '扩展信息',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_content_id` (`content_id`),
    KEY `idx_operator_user_id` (`operator_user_id`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='内容操作日志表'; 