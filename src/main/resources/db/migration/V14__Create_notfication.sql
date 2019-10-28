CREATE TABLE `community`.`notification`  (
  `id` bigint(0) NOT NULL,
  `notifier` bigint(255) NULL,
  `receiver` bigint(255) NULL,
  `outer_id` bigint(0) NULL,
  `type` int(255) NULL,
  `gmt_create` bigint(255) NULL,
  `status` int(255) NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;