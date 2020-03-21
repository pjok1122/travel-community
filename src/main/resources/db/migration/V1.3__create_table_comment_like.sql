CREATE TABLE `comment_like`(
	`id`			BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`member_id`		BIGINT			NOT NULL					COMMENT '좋아요를 누른 사람 시퀀스',
	`comment_id`	BIGINT			NOT NULL					COMMENT '좋아요를 누른  댓글 시퀀스',
	`register_date` TIMESTAMP		NOT NULL					COMMENT '좋아요를 누른 시간',
	PRIMARY KEY(`id`)
);


ALTER TABLE `comment_like` ADD CONSTRAINT `FK_comment_TO_comment_like`
FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ;

ALTER TABLE `comment_like` ADD CONSTRAINT `FK_member_TO_comment_like`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ;

ALTER TABLE `comment_like` ADD CONSTRAINT `UNIQUE_COMPOSIT_KEY` UNIQUE (`member_id`, `comment_id`);