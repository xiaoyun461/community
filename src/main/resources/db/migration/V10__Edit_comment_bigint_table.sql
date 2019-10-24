ALTER TABLE `community`.`comment`
MODIFY COLUMN `commentator` bigint(255) NULL DEFAULT NULL AFTER `type`;