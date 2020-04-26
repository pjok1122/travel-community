package project.board.domain.dto;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Article;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleDto extends Article{
	private int commentCnt;
	
	private String email;
	private Long memberId;
	private List<GpsDecimal> gpsInfo;
}
