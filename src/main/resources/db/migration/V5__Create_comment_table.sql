CREATE TABLE `community`.`comment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(0) NULL,
  `type` varchar(255) NULL,
  `commentator` int(255) NULL,
  `gmt_create` bigint(255) NULL,
  `gmt_modified` bigint(255) NULL,
  `like_count` bigint(255) NULL DEFAULT 0,
  PRIMARY KEY (`id`)
)ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;