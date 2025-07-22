-- 商品预订表
-- Create Date: 2024-01-19
-- Description: 用于存储用户对商品的预订信息

CREATE TABLE `goods_book` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `GMT_CREATE` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `GMT_MODIFIED` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `GOODS_ID` bigint(20) NOT NULL COMMENT '商品ID',
    `GOODS_TYPE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '商品类型',
    `BUYER_ID` bigint(20) NOT NULL COMMENT '购买者ID',
    `BUYER_TYPE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '购买者类型',
    `IDENTIFIER` varchar(128) DEFAULT NULL COMMENT '标识符',
    `BOOK_SUCCEED_TIME` datetime DEFAULT NULL COMMENT '预订成功时间',
    `DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标记:0-未删除,1-已删除',
    `LOCK_VERSION` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
    PRIMARY KEY (`ID`),
    KEY `idx_goods_id` (`GOODS_ID`),
    KEY `idx_buyer_id` (`BUYER_ID`),
    KEY `idx_goods_type` (`GOODS_TYPE`),
    KEY `idx_identifier` (`IDENTIFIER`),
    KEY `idx_deleted` (`DELETED`),
    KEY `idx_gmt_create` (`GMT_CREATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品预订表'; 