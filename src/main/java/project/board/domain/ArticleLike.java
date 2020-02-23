package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("articleLike")
public class ArticleLike{
	private Long id;
	private Long articleId;
	private Long memberId;
	private LocalDateTime registerDate;
}
