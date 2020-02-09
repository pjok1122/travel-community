package project.board.domain;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("category")
public class Category {
	private Long id;
	private String title;
	private Integer order;
}
