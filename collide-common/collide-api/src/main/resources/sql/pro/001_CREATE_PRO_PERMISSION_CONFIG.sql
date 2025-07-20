-- Pro权限配置表
CREATE TABLE `pro_permission_config` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '配置ID（自增主键）',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
  `config_name` varchar(64) NOT NULL COMMENT '配置名称',
  `config_version` varchar(32) NOT NULL COMMENT '配置版本',
  `description` text COMMENT '配置描述',
  `package_type` varchar(32) NOT NULL COMMENT '套餐类型（MONTHLY，QUARTERLY，YEARLY，LIFETIME）',
  `permission_type` varchar(64) NOT NULL COMMENT '权限类型',
  `permission_name` varchar(128) NOT NULL COMMENT '权限名称',
  `permission_desc` text COMMENT '权限描述',
  `default_enabled` tinyint(1) DEFAULT 0 COMMENT '是否默认开通（0否，1是）',
  `usage_limit` int DEFAULT -1 COMMENT '使用次数限制（-1无限制）',
  `valid_days` int DEFAULT -1 COMMENT '有效期天数（-1跟随套餐）',
  `priority` int DEFAULT 0 COMMENT '权限优先级',
  `ext_config` text COMMENT '扩展配置（JSON格式）',
  `status` varchar(32) DEFAULT 'ACTIVE' COMMENT '配置状态（ACTIVE激活，INACTIVE停用）',
  `deleted` int DEFAULT 0 COMMENT '是否逻辑删除，0为未删除，非0为已删除',
  `lock_version` int DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_package_permission` (`config_version`, `package_type`, `permission_type`),
  KEY `idx_config_version` (`config_version`),
  KEY `idx_package_type` (`package_type`),
  KEY `idx_permission_type` (`permission_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Pro权限配置表'
; 