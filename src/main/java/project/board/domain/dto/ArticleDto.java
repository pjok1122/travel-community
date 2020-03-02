package project.board.domain.dto;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.domain.Article;
import project.board.domain.Comment;
import project.board.domain.Member;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleDto extends Article{
	private int commentCnt;

	private String email;
	private Long memberId;
	private String category;
//	private List<Comment> comments = new ArrayList<Comment>();
}
