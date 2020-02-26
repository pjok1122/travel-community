package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("commonDomain")
public class CommonDomain {
	private Long id;
	
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
}
