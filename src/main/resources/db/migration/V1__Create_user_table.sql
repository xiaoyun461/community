CREATE TABLE `user`
(
    `id`           int(11)                                                 NOT NULL AUTO_INCREMENT,
    `account_id`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `name`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `token`        char(36) CHARACTER SET utf8 COLLATE utf8_general_ci     NULL DEFAULT NULL,
    `gmt_create`   bigint(255)                                             NULL DEFAULT NULL,
    `gmt_modified` bigint(255)                                             NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;