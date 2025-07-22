CREATE TABLE `collection`
(
    `id`                 bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID（自增主键）',
    `gmt_create`         datetime        NOT NULL COMMENT '创建时间',
    `gmt_modified`       datetime        NOT NULL COMMENT '最后更新时间',
    `name`               varchar(512)   DEFAULT NULL COMMENT '藏品名称',
    `cover`              varchar(512)   DEFAULT NULL COMMENT '藏品封面',
    `class_id`           varchar(128)   DEFAULT NULL COMMENT '藏品类目ID',
    `price`              decimal(18, 6) DEFAULT NULL COMMENT '价格',
    `quantity`           bigint         DEFAULT NULL COMMENT '藏品数量',
    `detail`             text COMMENT '详情',
    `saleable_inventory` bigint         DEFAULT NULL COMMENT '可销售库存',
    `identifier`         varchar(128)   DEFAULT NULL COMMENT '幂等号',
    `occupied_inventory` bigint         DEFAULT NULL COMMENT '已占用库存',
    `frozen_inventory`   bigint         DEFAULT 0 COMMENT '冻结库存',
    `state`              varchar(128)   DEFAULT NULL COMMENT '状态',
    `create_time`        datetime       DEFAULT NULL COMMENT '藏品创建时间',
    `sale_time`          datetime       DEFAULT NULL COMMENT '藏品发售时间',
    `sync_chain_time`    datetime       DEFAULT NULL COMMENT '藏品上链时间',
    `book_start_time`    datetime       DEFAULT NULL COMMENT '预约开始时间',
    `book_end_time`      datetime       DEFAULT NULL COMMENT '预约结束时间',
    `can_book`           int            DEFAULT NULL COMMENT '是否可以预约',
    `deleted`            int            DEFAULT NULL COMMENT '是否逻辑删除，0为未删除，非0为已删除',
    `lock_version`       int            DEFAULT NULL COMMENT '乐观锁版本号',
    `creator_id`         varchar(128)   DEFAULT NULL COMMENT '创建者',
    `version`            int            DEFAULT NULL COMMENT '修改版本',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10023
  DEFAULT CHARSET = utf8mb4 COMMENT ='藏品表'
;