package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("article")
public class Article {
	private Long id;
	private Long memberId;
	private Long categoryId;
	private String title;
	private String content;
	private int good;
	private int hit;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
	private String nation;
	private String status;
	private int commentCnt;
}

