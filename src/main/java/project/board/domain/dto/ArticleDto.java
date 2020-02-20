package project.board.domain.dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Article;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleDto extends Article{
	private int commentCnt; 
}
