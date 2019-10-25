ALTER TABLE `community`.`comment` 
MODIFY COLUMN `content_count` int(100) NULL DEFAULT 0 AFTER `content`;