package project.board.domain;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.ibatis.type.Alias;
import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("article")
//	private Long id;
public class Article extends CommonDomain{
	private Long memberId;
	
	@NotNull
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

