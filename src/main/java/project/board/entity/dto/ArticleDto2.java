package project.board.entity.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import project.board.entity.Article;
import project.board.entity.BaseTimeEntity;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleDto2{
	private Long id;
	private String title;
	private String content;
	private int good;
	private int hit;
	private int commentCount;
	private Nation nation;
	private Category category;
	private LocalDateTime createdDate;
	private LocalDateTime lastModifiedDate;
	
	public ArticleDto2(Article article){
		id = article.getId();
		title = article.getTitle();
		content = article.getContent();
		nation = article.getNation();
		category = article.getCategory();
		commentCount = article.getCommentCount();
		createdDate = article.getCreatedDate();
		lastModifiedDate = article.getLastModifiedDate();
	}
}
