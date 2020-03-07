ALTER TABLE `comment` ADD CONSTRAINT `FK_comment_TO_comment`
FOREIGN KEY (`parent_comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ;