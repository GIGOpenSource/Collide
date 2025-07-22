-- 持有收藏品表
-- Create Date: 2024-01-19
-- Description: 用于存储用户持有的收藏品信息

CREATE TABLE `held_collection` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `GMT_CREATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `COLLECTION_ID` bigint(20) NOT NULL COMMENT '收藏品ID',
    `SERIAL_NO` varchar(128) DEFAULT NULL COMMENT '序列号',
    `NFT_ID` varchar(128) DEFAULT NULL COMMENT 'NFT ID',
    `PRE_ID` varchar(128) DEFAULT NULL COMMENT '前置ID',
    `USER_ID` bigint(20) NOT NULL COMMENT '用户ID',
    `STATE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
    `BIZ_TYPE` varchar(32) DEFAULT NULL COMMENT '业务类型',
    `BIZ_NO` varchar(64) DEFAULT NULL COMMENT '业务编号',
    `PURCHASE_PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '购买价格',
    `REFERENCE_PRICE` decimal(10,2) DEFAULT '0.00' COMMENT '参考价格',
    `rarity` varchar(32) DEFAULT NULL COMMENT '稀有度',
    `TX_HASH` varchar(128) DEFAULT NULL COMMENT '交易哈希',
    `HOLD_TIME` datetime DEFAULT NULL COMMENT '持有时间',
    `SYNC_CHAIN_TIME` datetime DEFAULT NULL COMMENT '同步上链时间',
    `DELETE_TIME` datetime DEFAULT NULL COMMENT '删除时间',
    `DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记:0-未删除,1-已删除',
    `LOCK_VERSION` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
    PRIMARY KEY (`ID`),
    UNIQUE KEY `uk_serial_no` (`SERIAL_NO`),
    KEY `idx_collection_id` (`COLLECTION_ID`),
    KEY `idx_user_id` (`USER_ID`),
    KEY `idx_state` (`STATE`),
    KEY `idx_nft_id` (`NFT_ID`),
    KEY `idx_biz_no` (`BIZ_NO`),
    KEY `idx_deleted` (`DELETED`),
    KEY `idx_gmt_create` (`GMT_CREATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='持有收藏品表'; 