package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("commentLike")
public class CommentLike{
	private Long id;
	private Long commentId;
	private Long memberId;
	private LocalDateTime registerDate;
}
