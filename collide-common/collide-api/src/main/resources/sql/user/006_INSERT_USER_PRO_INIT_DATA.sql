-- 付费用户功能初始化数据

-- 为现有用户初始化付费记录（默认为普通用户）
INSERT INTO `user_pro` (`gmt_create`, `gmt_modified`, `user_id`, `user_type`, `pro_status`, `duration`, `amount`, `total_duration`, `total_amount`, `auto_renewal`, `deleted`, `lock_version`)
SELECT 
    NOW() as `gmt_create`,
    NOW() as `gmt_modified`,
    `id` as `user_id`,
    'CUSTOMER' as `user_type`,
    'NORMAL' as `pro_status`,
    0 as `duration`,
    0 as `amount`,
    0 as `total_duration`,
    0 as `total_amount`,
    0 as `auto_renewal`,
    0 as `deleted`,
    0 as `lock_version`
FROM `users` 
WHERE `id` NOT IN (SELECT `user_id` FROM `user_pro`)
AND `deleted` = 0;

-- 更新现有用户的用户类型为普通用户（如果为空）
UPDATE `users` 
SET `user_type` = 'CUSTOMER', 
    `user_permission` = 'BASIC',
    `gmt_modified` = NOW()
WHERE `user_type` IS NULL 
   OR `user_permission` IS NULL; 