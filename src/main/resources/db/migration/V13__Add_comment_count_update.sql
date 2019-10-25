ALTER TABLE `community`.`comment`
CHANGE COLUMN `content_count` `comment_count` int(100) NULL DEFAULT 0 AFTER `content`;