package project.board.entity.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.board.entity.Article;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleForm {
	
	private Long articleId;
	
	@NotNull
	private Category category;
	
	@NotNull
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
