-- 收藏品空投流水表
-- Create Date: 2024-01-19
-- Description: 用于存储收藏品空投操作的流水记录

CREATE TABLE `collection_airdrop_stream` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `GMT_CREATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `COLLECTION_ID` bigint(20) NOT NULL COMMENT '收藏品ID',
    `RECIPIENT_USER_ID` bigint(20) NOT NULL COMMENT '接收用户ID',
    `QUANTITY` int(11) NOT NULL DEFAULT '0' COMMENT '空投数量',
    `STREAM_TYPE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '流水类型',
    `IDENTIFIER` varchar(128) DEFAULT NULL COMMENT '标识符',
    `DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记:0-未删除,1-已删除',
    `LOCK_VERSION` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
    PRIMARY KEY (`ID`),
    KEY `idx_collection_id` (`COLLECTION_ID`),
    KEY `idx_recipient_user_id` (`RECIPIENT_USER_ID`),
    KEY `idx_stream_type` (`STREAM_TYPE`),
    KEY `idx_identifier` (`IDENTIFIER`),
    KEY `idx_deleted` (`DELETED`),
    KEY `idx_gmt_create` (`GMT_CREATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏品空投流水表'; 