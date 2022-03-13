CREATE TABLE if not exists `reservation`
(
    `id`          bigint UNSIGNED AUTO_INCREMENT NOT NULL COMMENT 'id',
    `order_num`   VARCHAR(128)                   NULL COMMENT '电子客单号',
    `message`     VARCHAR(256)                   NOT NULL COMMENT '预定信息',
    `pay_status`  VARCHAR(32)                    NOT NULL COMMENT '支付信息',
    `flight_id`   VARCHAR(128)                   NOT NULL COMMENT '航班号',
    `fc_id`       VARCHAR(128)                   NOT NULL COMMENT '协议号',
    `price`       double                         NOT NULL COMMENT '总价',
    `creator`     VARCHAR(32)                    NOT NULL COMMENT '创建人',
    `create_time` DATETIME                       NOT NULL COMMENT '创建时间',
    `modify_time` DATETIME                       NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;