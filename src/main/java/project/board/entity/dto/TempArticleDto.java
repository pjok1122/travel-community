package project.board.entity.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.board.entity.TempArticle;
import project.board.enums.Category;
import project.board.enums.Nation;

@AllArgsConstructor
@Data
public class TempArticleDto{
	private Long articleId;
	private String title;
	private Category category;
	private Nation nation;
	private String nationKr;
	private String content;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
	
	public TempArticleDto(TempArticle article) {
		this.articleId = article.getId();
		this.title = article.getTitle();
		this.content = article.getContent();
		this.category = article.getCategory();
		this.nationKr = article.getNation().getKrValue();
		this.nation = article.getNation();
		this.createdDate = article.getCreatedDate();
		this.lastModifiedDate = article.getLastModifiedDate();
	}
}
