CREATE TABLE `report_info` (
	`id`				BIGINT			NOT NULL					COMMENT '시퀀스',
	`content`			VARCHAR(200)	NOT	NULL					COMMENT '신고 내용',
	`order`				BIGINT			NOT NULL					COMMENT	'신고 순서',
	PRIMARY KEY(id)
);

INSERT INTO `report_info`
VALUES (1, '부적절한 홍보 게시글', 1);

INSERT INTO `report_info`
VALUES (2, '음란성 또는 청소년에게 부적합한 내용', 2);

INSERT INTO `report_info`
VALUES (3, '명예훼손/사생활 침해 등 저작권 침해 행위', 3);

INSERT INTO `report_info`
VALUES (4, '기타', 4);
