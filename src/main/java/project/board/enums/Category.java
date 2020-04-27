package project.board.enums;

import lombok.Getter;

@Getter
public enum Category {
	ALL("전체"),
	ATTRACTIONS("관광지"),
	RESTAURANT("맛집"),
	ACCOMODATION("숙박"),
	FESTIVAL("축제");
	
	private String krValue;
	
	private Category(String krValue) {
		this.krValue = krValue;
	}
}
