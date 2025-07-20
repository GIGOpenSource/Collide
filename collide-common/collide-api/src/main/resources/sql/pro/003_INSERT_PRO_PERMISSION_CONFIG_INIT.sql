-- Pro权限配置初始化数据

-- 月费套餐权限配置
INSERT INTO `pro_permission_config` 
(`gmt_create`, `gmt_modified`, `config_name`, `config_version`, `description`, `package_type`, `permission_type`, `permission_name`, `permission_desc`, `default_enabled`, `usage_limit`, `valid_days`, `priority`) 
VALUES 
-- 月费套餐基础权限
(NOW(), NOW(), '月费套餐权限配置', 'v1.0', '月费套餐用户权限配置', 'MONTHLY', 'ADVANCED_SEARCH', '高级搜索权限', '支持更多搜索条件和过滤器', 1, 1000, -1, 1),
(NOW(), NOW(), '月费套餐权限配置', 'v1.0', '月费套餐用户权限配置', 'MONTHLY', 'DATA_EXPORT', '数据导出权限', '支持导出数据到Excel等格式', 1, 50, -1, 2),
(NOW(), NOW(), '月费套餐权限配置', 'v1.0', '月费套餐用户权限配置', 'MONTHLY', 'ADVANCED_FOLLOW', '高级关注权限', '支持特别关注等高级功能', 1, -1, -1, 3),

-- 季费套餐权限配置
(NOW(), NOW(), '季费套餐权限配置', 'v1.0', '季费套餐用户权限配置', 'QUARTERLY', 'ADVANCED_SEARCH', '高级搜索权限', '支持更多搜索条件和过滤器', 1, 3000, -1, 1),
(NOW(), NOW(), '季费套餐权限配置', 'v1.0', '季费套餐用户权限配置', 'QUARTERLY', 'DATA_EXPORT', '数据导出权限', '支持导出数据到Excel等格式', 1, 200, -1, 2),
(NOW(), NOW(), '季费套餐权限配置', 'v1.0', '季费套餐用户权限配置', 'QUARTERLY', 'ADVANCED_FOLLOW', '高级关注权限', '支持特别关注等高级功能', 1, -1, -1, 3),
(NOW(), NOW(), '季费套餐权限配置', 'v1.0', '季费套餐用户权限配置', 'QUARTERLY', 'BATCH_OPERATION', '批量操作权限', '支持批量处理数据', 1, 100, -1, 4),
(NOW(), NOW(), '季费套餐权限配置', 'v1.0', '季费套餐用户权限配置', 'QUARTERLY', 'ADVANCED_ANALYTICS', '高级分析权限', '支持数据分析和统计图表', 1, -1, -1, 5),

-- 年费套餐权限配置
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'ADVANCED_SEARCH', '高级搜索权限', '支持更多搜索条件和过滤器', 1, -1, -1, 1),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'DATA_EXPORT', '数据导出权限', '支持导出数据到Excel等格式', 1, -1, -1, 2),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'ADVANCED_FOLLOW', '高级关注权限', '支持特别关注等高级功能', 1, -1, -1, 3),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'BATCH_OPERATION', '批量操作权限', '支持批量处理数据', 1, -1, -1, 4),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'ADVANCED_ANALYTICS', '高级分析权限', '支持数据分析和统计图表', 1, -1, -1, 5),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'CUSTOM_CONFIG', '自定义配置权限', '支持个性化配置', 1, -1, -1, 6),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'PRIORITY_SUPPORT', '优先级支持权限', '享受优先技术支持', 1, -1, -1, 7),
(NOW(), NOW(), '年费套餐权限配置', 'v1.0', '年费套餐用户权限配置', 'YEARLY', 'API_ACCESS', 'API调用权限', '支持API接口调用', 1, 10000, -1, 8),

-- 终身套餐权限配置（全部权限）
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'ADVANCED_SEARCH', '高级搜索权限', '支持更多搜索条件和过滤器', 1, -1, -1, 1),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'DATA_EXPORT', '数据导出权限', '支持导出数据到Excel等格式', 1, -1, -1, 2),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'ADVANCED_FOLLOW', '高级关注权限', '支持特别关注等高级功能', 1, -1, -1, 3),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'BATCH_OPERATION', '批量操作权限', '支持批量处理数据', 1, -1, -1, 4),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'ADVANCED_ANALYTICS', '高级分析权限', '支持数据分析和统计图表', 1, -1, -1, 5),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'CUSTOM_CONFIG', '自定义配置权限', '支持个性化配置', 1, -1, -1, 6),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'PRIORITY_SUPPORT', '优先级支持权限', '享受优先技术支持', 1, -1, -1, 7),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'API_ACCESS', 'API调用权限', '支持API接口调用', 1, -1, -1, 8),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'DATA_BACKUP', '数据备份权限', '支持数据备份和恢复', 1, -1, -1, 9),
(NOW(), NOW(), '终身套餐权限配置', 'v1.0', '终身套餐用户权限配置', 'LIFETIME', 'ADVANCED_NOTIFICATION', '高级通知权限', '支持高级通知设置', 1, -1, -1, 10); 