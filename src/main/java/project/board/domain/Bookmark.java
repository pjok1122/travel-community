package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("bookmark")
public class Bookmark extends CommonDomain{
//	private Long id;
	private Long memberId;
	private Long articleId;
//	private LocalDateTime registerDate;
//	private LocalDateTime updateDate;
}
