ALTER TABLE `article_like` ADD CONSTRAINT `FK_article_TO_article_like`
FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ;

ALTER TABLE `article_like` ADD CONSTRAINT `FK_member_TO_article_like`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ;

ALTER TABLE `article_like` ADD CONSTRAINT `UNIQUE_COMPOSIT_KEY` UNIQUE (`member_id`, `article_id`);