ALTER TABLE `community`.`comment`
MODIFY COLUMN `type` int(255) NULL DEFAULT NULL AFTER `parent_id`;