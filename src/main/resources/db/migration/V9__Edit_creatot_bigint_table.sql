ALTER TABLE `community`.`question`
MODIFY COLUMN `creator` bigint(255) NULL DEFAULT NULL AFTER `gmt_modified`;