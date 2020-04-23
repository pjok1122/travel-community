package project.board.domain.dto;

import lombok.Getter;
import lombok.Setter;
import project.board.enums.Sort;

@Getter @Setter
public class PageAndSort {
	
	private int page = 1;
	private Sort sort = Sort.NEWEST;
}
