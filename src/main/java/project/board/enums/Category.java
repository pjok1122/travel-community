package project.board.enums;

import lombok.Getter;

@Getter
public enum Category {
	ALL("전체"),
	ACCOMODATION("숙박 정보"),
	RESTAURANT("맛집"),
	FESTIVAL("축제 정보"),
	ATTRACTIONS("관광지");
	
	private String krValue;
	
	private Category(String krValue) {
		this.krValue =krValue;
	}
}
