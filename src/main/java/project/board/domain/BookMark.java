package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("bookMark")
public class BookMark {
	private Long id;
	private Long memberId;
	private Long articleId;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
}
