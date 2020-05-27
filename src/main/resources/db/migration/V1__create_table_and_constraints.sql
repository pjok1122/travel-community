CREATE TABLE `member` (
	`id`			BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`email`			VARCHAR(255)	NOT NULL					COMMENT '사용자 아이디',
	`password`		VARCHAR(255)	NOT NULL					COMMENT '비밀번호 해쉬화',
	`salt`			VARCHAR(255)	NOT NULL					COMMENT '패스워드에 사용될 랜덤 변수.',
	`register_date`	TIMESTAMP		NOT NULL					COMMENT '가입일자',
	`update_date`	TIMESTAMP		NULL						COMMENT '수정일자',
	`login_date`	TIMESTAMP		NULL						COMMENT '최근 로그인 일자',
	`role`			VARCHAR(32)		NOT NULL	DEFAULT 'USER'	COMMENT 'enum{1 : USER, 2 : ADMIN}',
	PRIMARY KEY(`id`)
);

CREATE TABLE `category` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`title`				VARCHAR(32)		NULL						COMMENT '카테고리 명  ex) 맛집, 숙소, 관광지',
	`order`				INT				NULL		DEFAULT 0		COMMENT '카테고리 순서',
	PRIMARY KEY(`id`)
);

CREATE TABLE `article` (
	`id`			BIGINT			NOT NULL	AUTO_INCREMENT		COMMENT '시퀀스',
	`member_id`		BIGINT			NOT NULL						COMMENT '유저 시퀀스 (FK)',
	`category_id`	BIGINT			NULL							COMMENT '카테고리 시퀀스 (FK) ex)맛집, 숙소',
	`title`			VARCHAR(50)		NOT NULL						COMMENT '글 제목',
	`content`		TEXT			NULL							COMMENT '글 내용',
	`good`			INT				NOT NULL	DEFAULT 0			COMMENT '좋아요 수',
	`hit`			INT				NOT NULL	DEFAULT 0			COMMENT '조회 수',
	`register_date`	TIMESTAMP		NOT NULL						COMMENT '등록 일자',
	`update_date`	TIMESTAMP		NULL							COMMENT '마지막 수정 일자',
	`nation`		VARCHAR(20)		NOT NULL						COMMENT '국가'
	PRIMARY KEY(`id`)
);

CREATE TABLE `temp_article` (
	`id`			BIGINT			NOT NULL	AUTO_INCREMENT		COMMENT '시퀀스',
	`member_id`		BIGINT			NOT NULL						COMMENT '유저 시퀀스 (FK)',
	`category_id`	BIGINT			NULL							COMMENT '카테고리 시퀀스 (FK) ex)맛집, 숙소',
	`title`			VARCHAR(50)		NOT NULL						COMMENT '글 제목',
	`content`		TEXT			NULL							COMMENT '글 내용',
	`register_date`	TIMESTAMP		NOT NULL						COMMENT '등록 일자',
	`update_date`	TIMESTAMP		NULL							COMMENT '마지막 수정 일자',
	`nation`		VARCHAR(20)		NOT NULL						COMMENT '국가',
	`status`		VARCHAR(20)		NOT NULL	DEFAULT 'PERMANENT'	COMMENT 'enum{TEMPORARY,  PERMANENT}',
	PRIMARY KEY(`id`)
);

CREATE TABLE `comment` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`article_id`		BIGINT			NOT NULL					COMMENT '글 시퀀스 (FK)',
	`member_id`			BIGINT			NULL						COMMENT '유저 시퀀스 (FK)',
	`content`			TEXT			NOT NULL					COMMENT '댓글 내용',
	`register_date`		TIMESTAMP		NULL						COMMENT '댓글 등록 일자',
	`update_date`		TIMESTAMP		NULL						COMMENT '댓글 수정 일자',
	`parent_comment_id`	BIGINT			NULL						COMMENT '대댓글 유무',
	`good`				INT				NOT NULL	DEFAULT 0		COMMENT '좋아요 수',
	PRIMARY KEY(id)
);


CREATE TABLE `upload_file` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`origin_file_name`	VARCHAR(255)	NOT NULL					COMMENT '원본 파일명',
	`dir_path`			VARCHAR(255)	NOT NULL					COMMENT '폴더 경로',
	`file_name`			VARCHAR(255)	NOT NULL					COMMENT '생성된 파일 이름',
	`content_type`		VARCHAR(6)		NULL						COMMENT '확장자',
	`size`				BIGINT			NULL						COMMENT '파일 크기 (byte)',
	`register_date`		TIMESTAMP		NULL						COMMENT '등록 일자',
	`update_date`		TIMESTAMP		NULL						COMMENT '수정 일자',
	PRIMARY KEY(id)
);

CREATE TABLE `post_file` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`origin_file_name`	VARCHAR(255)	NOT NULL					COMMENT '원본 파일명',
	`dir_path`			VARCHAR(255)	NOT NULL					COMMENT '폴더 경로',
	`file_name`			VARCHAR(255)	NOT NULL					COMMENT '생성된 파일 이름',
	`content_type`		VARCHAR(6)		NULL						COMMENT '확장자',
	`size`				BIGINT			NULL						COMMENT '파일 크기 (byte)',
	`register_date`		TIMESTAMP		NULL						COMMENT '등록 일자',
	`update_date`		TIMESTAMP		NULL						COMMENT '수정 일자',
	`article_id`		BIGINT			NOT NULL					COMMENT '게시물 시퀀스',
	PRIMARY KEY(id)
);

CREATE TABLE `report` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`member_id`			BIGINT			NULL						COMMENT '신고자 시퀀스(FK)',
	`target`			VARCHAR(32)		NULL						COMMENT 'enum : Article, Comment',
	`target_id`			BIGINT			NOT NULL					COMMENT '피신고자',
	`register_date`		TIMESTAMP		NOT NULL					COMMENT '신고 일자',
	`update_date`		TIMESTAMP		NULL						COMMENT '신고 수정 일자',
	`content`			VARCHAR(200)	NULL						COMMENT '신고 내용',
	`process`			BOOLEAN			NULL						COMMENT '처리 결과',
	PRIMARY KEY(id)
);

CREATE TABLE `bookmark` (
	`id`				BIGINT			NOT NULL	AUTO_INCREMENT	COMMENT '시퀀스',
	`member_id`			BIGINT			NOT NULL					COMMENT '유저 시퀀스(FK)',
	`article_id`		BIGINT			NOT NULL					COMMENT '즐겨찾기 글(FK)',
	`register_date`		TIMESTAMP		NULL						COMMENT '즐겨찾기 등록 일자',
	`update_date`		TIMESTAMP		NULL						COMMENT '즐겨찾기 수정 일자',
	PRIMARY KEY(id)
);


ALTER TABLE `article` ADD CONSTRAINT `FK_member_TO_article`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE ;

ALTER TABLE `article` ADD CONSTRAINT `FK_category_TO_article`
FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ;

ALTER TABLE `comment` ADD CONSTRAINT `FK_article_TO_comment` 
FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ;

ALTER TABLE `comment` ADD CONSTRAINT `FK_member_TO_comment`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE SET NULL ;

ALTER TABLE `report` ADD CONSTRAINT `FK_member_TO_report`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE SET NULL ;

ALTER TABLE `bookmark` ADD CONSTRAINT `FK_member_TO_bookmark`
FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE;

ALTER TABLE `bookmark` ADD CONSTRAINT `FK_article_TO_bookmark`
FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE;

ALTER TABLE `post_file` ADD CONSTRAINT `FK_article_TO_post_file`
FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE;

INSERT INTO `category`(title, `order`)
VALUES ('관광지', 1);
INSERT INTO `category`(title, `order`)
VALUES ('맛집', 2);
INSERT INTO `category`(title, `order`)
VALUES ('숙소', 3);
INSERT INTO `category`(title, `order`)
VALUES ('축제', 4);