CREATE TABLE `article_like`(
	`id`			BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`member_id`		BIGINT			NOT NULL					COMMENT '좋아요를 누른 사람 시퀀스',
	`article_id`	BIGINT			NOT NULL					COMMENT '좋아요를 누른 게시물 시퀀스',
	`register_date` TIMESTAMP		NOT NULL					COMMENT '좋아요를 누른 시간',
	PRIMARY KEY(`id`)
);
