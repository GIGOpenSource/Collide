-- 用户标签关联表
CREATE TABLE `user_tag_relation` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '关联ID（自增主键）',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `tag_id` bigint unsigned NOT NULL COMMENT '标签ID',
  `relation_type` varchar(32) NOT NULL COMMENT '关联类型（USER用户，CONTENT内容，GOODS商品，COLLECTION藏品，ACTIVITY活动，TOPIC话题，CHANNEL频道，SPECIAL专题）',
  `relation_object_id` varchar(128) DEFAULT NULL COMMENT '关联对象ID（内容ID、商品ID等）',
  `weight` decimal(5,4) DEFAULT 1.0000 COMMENT '标签权重（用户对该标签的偏好程度 0.0-1.0）',
  `usage_count` int DEFAULT 1 COMMENT '标签使用次数',
  `last_used_time` datetime DEFAULT NULL COMMENT '最后使用时间',
  `is_auto_tag` tinyint(1) DEFAULT 0 COMMENT '是否自动标签（0否，1是）',
  `tag_source` varchar(64) DEFAULT NULL COMMENT '标签来源',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注信息',
  `deleted` int DEFAULT 0 COMMENT '是否逻辑删除，0为未删除，非0为已删除',
  `lock_version` int DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_tag_relation` (`user_id`, `tag_id`, `relation_type`, `relation_object_id`, `deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_relation_type` (`relation_type`),
  KEY `idx_relation_object_id` (`relation_object_id`),
  KEY `idx_weight` (`weight`),
  KEY `idx_last_used_time` (`last_used_time`),
  KEY `idx_is_auto_tag` (`is_auto_tag`),
  KEY `idx_gmt_create` (`gmt_create`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户标签关联表'
; 