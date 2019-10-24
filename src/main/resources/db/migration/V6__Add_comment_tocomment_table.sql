ALTER TABLE `community`.`comment`
ADD COLUMN `content` varchar(1024) NULL AFTER `like_count`;