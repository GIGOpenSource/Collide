-- 用户Pro权限表
CREATE TABLE `user_pro_permission` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '权限记录ID（自增主键）',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `permission_type` varchar(64) NOT NULL COMMENT '权限类型',
  `permission_name` varchar(128) NOT NULL COMMENT '权限名称',
  `permission_desc` text COMMENT '权限描述',
  `is_active` tinyint(1) DEFAULT 1 COMMENT '是否激活（0否，1是）',
  `activated_time` datetime DEFAULT NULL COMMENT '激活时间',
  `expired_time` datetime DEFAULT NULL COMMENT '过期时间',
  `usage_limit` int DEFAULT -1 COMMENT '使用次数限制（-1无限制）',
  `used_count` int DEFAULT 0 COMMENT '已使用次数',
  `config_version` varchar(32) DEFAULT NULL COMMENT '权限配置版本',
  `auto_granted` tinyint(1) DEFAULT 0 COMMENT '是否自动开通（0否，1是）',
  `granted_by` bigint unsigned DEFAULT NULL COMMENT '开通人ID',
  `grant_reason` varchar(255) DEFAULT NULL COMMENT '开通原因',
  `ext_config` text COMMENT '扩展配置（JSON格式）',
  `deleted` int DEFAULT 0 COMMENT '是否逻辑删除，0为未删除，非0为已删除',
  `lock_version` int DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_permission` (`user_id`, `permission_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_expired_time` (`expired_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户Pro权限表'
; 