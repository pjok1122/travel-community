package project.board.entity.dto;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import project.board.entity.Article;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@AllArgsConstructor
@Builder
public class ArticleForm {
	
	private Long articleId;
	
	@NotBlank
	private Category category;
	
	@NotBlank
	private Nation nation;
	
	@NotBlank
	@Length(max = 50)
	private String title;
	
	@NotBlank
	@Length(max = 1000000)
	private String content;
	
	private String images;
	
	public ArticleForm(Article article){
		this.articleId = article.getId();
		this.category = article.getCategory();
		this.nation = article.getNation();
		this.title = article.getTitle();
		this.content = article.getContent();
	}
}
