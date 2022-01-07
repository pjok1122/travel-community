package project.board.domain.dto;

import lombok.Getter;
import lombok.Setter;
import project.board.enums.ArticleSortType;

@Getter @Setter
public class PageAndSort {
	
	private int page = 1;
	private int size = 10;
	private ArticleSortType sort = ArticleSortType.NEWEST;
}
