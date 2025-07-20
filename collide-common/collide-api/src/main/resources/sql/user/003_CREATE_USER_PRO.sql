-- 用户付费信息表
CREATE TABLE `user_pro` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '付费记录ID（自增主键）',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '最后更新时间',
  `user_id` bigint unsigned NOT NULL COMMENT '用户ID',
  `user_type` varchar(32) DEFAULT 'CUSTOMER' COMMENT '用户类型（CUSTOMER普通用户，PRO付费用户）',
  `package_type` varchar(32) DEFAULT NULL COMMENT '套餐类型（MONTHLY月费，QUARTERLY季费，YEARLY年费，LIFETIME终身）',
  `pro_status` varchar(32) DEFAULT 'NORMAL' COMMENT '付费状态（NORMAL普通，ACTIVE有效，EXPIRED过期，SUSPENDED暂停，CANCELLED取消）',
  `start_time` datetime DEFAULT NULL COMMENT '付费开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '付费结束时间',
  `duration` int DEFAULT 0 COMMENT '付费时长（月）',
  `amount` bigint DEFAULT 0 COMMENT '付费金额（分）',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `payment_method` varchar(32) DEFAULT NULL COMMENT '支付方式',
  `auto_renewal` tinyint(1) DEFAULT 0 COMMENT '是否自动续费（0否，1是）',
  `total_duration` int DEFAULT 0 COMMENT '累计付费时长（月）',
  `total_amount` bigint DEFAULT 0 COMMENT '累计付费金额（分）',
  `last_payment_time` datetime DEFAULT NULL COMMENT '最后一次付费时间',
  `extend_info` text COMMENT '扩展字段',
  `deleted` int DEFAULT 0 COMMENT '是否逻辑删除，0为未删除，非0为已删除',
  `lock_version` int DEFAULT 0 COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_pro_status` (`pro_status`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_package_type` (`package_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户付费信息表'
; 