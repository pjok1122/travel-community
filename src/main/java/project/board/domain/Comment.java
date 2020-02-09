package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("comment")
public class Comment {
	private Long id;
	private Long articleId;
	private Long memberId;
	private String content;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
	private Long parentCommentId;
	private int good;
}

