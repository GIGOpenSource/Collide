-- 收藏品库存流水表
-- Create Date: 2024-01-19
-- Description: 用于存储收藏品库存变动的流水记录

CREATE TABLE `collection_inventory_stream` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `GMT_CREATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `collection_id` bigint(20) NOT NULL COMMENT '收藏品ID',
    `stream_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '流水类型',
    `PRICE` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
    `QUANTITY` int(11) NOT NULL DEFAULT '0' COMMENT '数量',
    `SALEABLE_INVENTORY` int(11) NOT NULL DEFAULT '0' COMMENT '可售库存',
    `OCCUPIED_INVENTORY` int(11) NOT NULL DEFAULT '0' COMMENT '占用库存',
    `CHANGED_QUANTITY` int(11) NOT NULL DEFAULT '0' COMMENT '变更数量',
    `STATE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
    `IDENTIFIER` varchar(128) DEFAULT NULL COMMENT '标识符',
    `DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记:0-未删除,1-已删除',
    `LOCK_VERSION` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
    PRIMARY KEY (`ID`),
    KEY `idx_collection_id` (`collection_id`),
    KEY `idx_stream_type` (`stream_type`),
    KEY `idx_identifier` (`IDENTIFIER`),
    KEY `idx_state` (`STATE`),
    KEY `idx_deleted` (`DELETED`),
    KEY `idx_gmt_create` (`GMT_CREATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏品库存流水表'; 