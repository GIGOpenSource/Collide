-- 标签操作日志表
CREATE TABLE `tag_operation_log` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '日志ID（自增主键）',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `user_id` bigint unsigned DEFAULT NULL COMMENT '操作用户ID',
  `tag_id` bigint unsigned DEFAULT NULL COMMENT '标签ID',
  `operation_type` varchar(32) NOT NULL COMMENT '操作类型（CREATE_TAG创建，UPDATE_TAG修改，DELETE_TAG删除，USER_TAG打标签，USER_UNTAG取消标签等）',
  `operation_desc` varchar(512) DEFAULT NULL COMMENT '操作描述',
  `before_data` text COMMENT '操作前数据（JSON格式）',
  `after_data` text COMMENT '操作后数据（JSON格式）',
  `operation_ip` varchar(64) DEFAULT NULL COMMENT '操作IP',
  `user_agent` varchar(512) DEFAULT NULL COMMENT '用户代理',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注信息',
  `relation_type` varchar(32) DEFAULT NULL COMMENT '关联类型',
  `relation_object_id` varchar(128) DEFAULT NULL COMMENT '关联对象ID',
  `operation_result` varchar(32) DEFAULT 'SUCCESS' COMMENT '操作结果（SUCCESS成功，FAILED失败）',
  `error_message` varchar(1024) DEFAULT NULL COMMENT '错误信息',
  `deleted` int DEFAULT 0 COMMENT '是否逻辑删除，0为未删除，非0为已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_operation_result` (`operation_result`),
  KEY `idx_relation_type` (`relation_type`),
  KEY `idx_relation_object_id` (`relation_object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签操作日志表'
; 