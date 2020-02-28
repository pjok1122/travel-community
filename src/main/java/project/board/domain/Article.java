package project.board.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.ibatis.type.Alias;
import org.hibernate.validator.constraints.Length;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Alias("article")
public class Article extends CommonDomain{
//	private Long id;
	private Long memberId;
	private Long categoryId;
	
	@NotBlank
	@Length(max = 50)
	private String title;
	
	@NotBlank
	@Length(max = 1000000)
	private String content;
	
	private int good;
	private int hit;
//	private LocalDateTime registerDate;
//	private LocalDateTime updateDate;
	
	@NotBlank
	private String nation;
	private String status;
	
}

