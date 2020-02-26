package project.board.domain;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("category")
public class Category extends CommonDomain{
//	private Long id;
	private String title;
	private Integer order;
}
