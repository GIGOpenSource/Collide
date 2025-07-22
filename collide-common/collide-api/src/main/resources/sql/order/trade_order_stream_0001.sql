-- 交易订单流水分片表 trade_order_stream_0001
-- Create Date: 2024-01-19 10:48:24
-- Description: 用于存储交易订单流水信息（分片表0001）

CREATE TABLE `trade_order_stream_0001` (
  `id` bigint NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `order_id` varchar(32) NOT NULL COMMENT '订单号',
  `buyer_id` varchar(32) NOT NULL COMMENT '买家ID',
  `buyer_type` varchar(32) NOT NULL COMMENT '买家类型',
  `seller_id` varchar(32) NOT NULL COMMENT '卖家ID',
  `seller_type` varchar(32) NOT NULL COMMENT '卖家类型',
  `identifier` varchar(128) NOT NULL COMMENT '幂等号',
  `goods_id` varchar(32) NOT NULL COMMENT '商品ID',
  `goods_type` varchar(32) NOT NULL COMMENT '商品类型',
  `goods_name` varchar(1024) DEFAULT NULL COMMENT '商品名称',
  `goods_pic_url` varchar(1024) DEFAULT NULL COMMENT '商品主图',
  `order_amount` decimal(18,6) NOT NULL COMMENT '订单金额',
  `order_state` varchar(32) NOT NULL COMMENT '订单状态',
  `paid_amount` decimal(18,6) NOT NULL COMMENT '已支付金额',
  `item_price` decimal(18,6) NOT NULL COMMENT '商品单价',
  `item_count` int NOT NULL COMMENT '商品数量',
  `pay_succeed_time` datetime DEFAULT NULL COMMENT '支付成功时间',
  `order_confirmed_time` datetime DEFAULT NULL COMMENT '订单确认时间',
  `order_finished_time` datetime DEFAULT NULL COMMENT '完结时间',
  `order_closed_time` datetime DEFAULT NULL COMMENT '关单时间',
  `pay_channel` varchar(64) DEFAULT NULL COMMENT '支付方式',
  `pay_stream_id` varchar(256) DEFAULT NULL COMMENT '支付流水号',
  `close_type` varchar(32) DEFAULT NULL COMMENT '关闭类型',
  `deleted` tinyint DEFAULT NULL COMMENT '逻辑删除标识',
  `lock_version` int DEFAULT NULL COMMENT '乐观锁版本号',
  `snapshot_version` int DEFAULT NULL COMMENT '商品快照版本号',
  `stream_identifier` varchar(128) NOT NULL COMMENT '幂等号',
  `stream_type` varchar(128) NOT NULL COMMENT '流水类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`stream_type`,`stream_identifier`,`order_id`),
  KEY `idx_order_id_buyer` (`order_id`,`buyer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易订单流水分片表0001'; 