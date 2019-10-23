CREATE TABLE `question`
(
    `id`            int(0)                                                  NOT NULL AUTO_INCREMENT,
    `title`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL,
    `description`   text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL,
    `gmt_create`    bigint(255)                                             NULL,
    `gmt_modified`  bigint(255)                                             NULL,
    `creator`       int(255)                                                NULL,
    `comment_count` int(255)                                                NULL DEFAULT 0,
    `view_count`    int(255)                                                NULL DEFAULT 0,
    `like_count`    int(255)                                                NULL DEFAULT 0,
    `tag`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 6
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;