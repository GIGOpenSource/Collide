-- -----------------------------------
-- 统一收藏表
-- 处理所有类型的收藏，无连表依赖
-- 统计信息通过冗余字段存储在各主表中
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `target_id` BIGINT NOT NULL COMMENT '目标对象ID（内容ID、动态ID等）',
    `target_type` VARCHAR(50) NOT NULL COMMENT '目标类型：CONTENT、SOCIAL_POST、USER、COMMENT',
    `favorite_type` TINYINT NOT NULL DEFAULT 1 COMMENT '收藏类型：1-收藏，0-取消收藏',
    `folder_id` BIGINT NULL COMMENT '收藏夹ID（可选分类）',
    `folder_name` VARCHAR(100) NULL COMMENT '收藏夹名称',
    `tags` JSON NULL COMMENT '收藏标签（JSON数组）',
    `notes` TEXT NULL COMMENT '收藏备注',
    `ip_address` VARCHAR(45) COMMENT '收藏IP地址',
    `device_info` VARCHAR(200) COMMENT '设备信息',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_target_type` (`user_id`, `target_id`, `target_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_target_id` (`target_id`),
    KEY `idx_target_type` (`target_type`),
    KEY `idx_favorite_type` (`favorite_type`),
    KEY `idx_folder_id` (`folder_id`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_target_favorite` (`target_id`, `target_type`, `favorite_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统一收藏表';

-- -----------------------------------
-- 收藏夹表（可选功能）
-- -----------------------------------

CREATE TABLE IF NOT EXISTS `t_favorite_folder` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `folder_name` VARCHAR(100) NOT NULL COMMENT '收藏夹名称',
    `description` TEXT NULL COMMENT '收藏夹描述',
    `cover_url` VARCHAR(500) NULL COMMENT '封面图片URL',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认收藏夹：1-是，0-否',
    `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开：1-公开，0-私密',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `item_count` BIGINT NOT NULL DEFAULT 0 COMMENT '收藏项目数量',
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_folder_name` (`folder_name`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_is_public` (`is_public`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_created_time` (`created_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏夹表';

-- -----------------------------------
-- 插入基础测试数据
-- -----------------------------------

-- 插入默认收藏夹数据
INSERT IGNORE INTO `t_favorite_folder` (`id`, `user_id`, `folder_name`, `description`, `is_default`, `is_public`, `sort_order`) VALUES
(1, 1, '默认收藏夹', '系统默认创建的收藏夹', 1, 0, 0),
(2, 1, '精选文章', '收藏的优质文章', 0, 1, 1),
(3, 1, '学习资料', '学习相关的收藏内容', 0, 0, 2),
(4, 2, '默认收藏夹', '系统默认创建的收藏夹', 1, 0, 0),
(5, 3, '默认收藏夹', '系统默认创建的收藏夹', 1, 0, 0);

-- 插入收藏数据
INSERT IGNORE INTO `t_favorite` (`id`, `user_id`, `target_id`, `target_type`, `favorite_type`, `folder_id`, `folder_name`, `notes`) VALUES
-- 内容收藏
(1, 1, 1, 'CONTENT', 1, 1, '默认收藏夹', '很有用的文章'),
(2, 1, 2, 'CONTENT', 1, 2, '精选文章', '值得收藏'),
(3, 2, 1, 'CONTENT', 1, 4, '默认收藏夹', ''),
(4, 3, 1, 'CONTENT', 1, 5, '默认收藏夹', '不错的内容'),
(5, 1, 3, 'CONTENT', 1, 3, '学习资料', '学习必备'),
-- 社交动态收藏
(6, 1, 1, 'SOCIAL_POST', 1, 1, '默认收藏夹', '有趣的动态'),
(7, 2, 2, 'SOCIAL_POST', 1, 4, '默认收藏夹', ''),
(8, 3, 1, 'SOCIAL_POST', 1, 5, '默认收藏夹', '值得保存'),
-- 用户收藏（关注作者）
(9, 1, 2, 'USER', 1, 1, '默认收藏夹', '优秀作者'),
(10, 2, 3, 'USER', 1, 4, '默认收藏夹', '');

-- -----------------------------------
-- 更新收藏夹项目数量
-- -----------------------------------

UPDATE `t_favorite_folder` f SET `item_count` = (
    SELECT COUNT(*) FROM `t_favorite` fav 
    WHERE fav.`folder_id` = f.`id` 
    AND fav.`favorite_type` = 1 
    AND fav.`deleted` = 0
); 