-- 盲盒物品表
-- Create Date: 2024-01-19
-- Description: 用于存储盲盒中的物品信息

CREATE TABLE `blind_box_item` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `GMT_CREATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `OPENED_TIME` datetime DEFAULT NULL COMMENT '开启时间',
    `ASSIGN_TIME` datetime DEFAULT NULL COMMENT '分配时间',
    `BLIND_BOX_ID` bigint(20) NOT NULL COMMENT '盲盒ID',
    `NAME` varchar(255) NOT NULL COMMENT '物品名称',
    `COVER` varchar(512) DEFAULT NULL COMMENT '封面图片URL',
    `DETAIL` text COMMENT '详细描述',
    `COLLECTION_NAME` varchar(255) DEFAULT NULL COMMENT '收藏品名称',
    `COLLECTION_COVER` varchar(512) DEFAULT NULL COMMENT '收藏品封面',
    `COLLECTION_DETAIL` text COMMENT '收藏品详情',
    `COLLECTION_SERIAL_NO` varchar(128) DEFAULT NULL COMMENT '收藏品序列号',
    `STATE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
    `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户ID',
    `PURCHASE_PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '购买价格',
    `REFERENCE_PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '参考价格',
    `rarity` varchar(32) DEFAULT NULL COMMENT '稀有度',
    `ORDER_ID` varchar(64) DEFAULT NULL COMMENT '订单ID',
    `DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记:0-未删除,1-已删除',
    `LOCK_VERSION` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
    PRIMARY KEY (`ID`),
    KEY `idx_blind_box_id` (`BLIND_BOX_ID`),
    KEY `idx_user_id` (`USER_ID`),
    KEY `idx_state` (`STATE`),
    KEY `idx_order_id` (`ORDER_ID`),
    KEY `idx_collection_serial_no` (`COLLECTION_SERIAL_NO`),
    KEY `idx_deleted` (`DELETED`),
    KEY `idx_gmt_create` (`GMT_CREATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盲盒物品表'; 