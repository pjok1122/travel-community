package project.board.entity.dto;


import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import project.board.entity.BaseTimeEntity;
import project.board.enums.Category;
import project.board.enums.Nation;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleDto2 extends BaseTimeEntity{
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
}
