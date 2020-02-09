package project.board.domain;

import java.time.LocalDateTime;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Data
@Alias("report")
public class Report {
	private Long id;
	private Long memberId;
	private String target;
	private Long targetId;
	private LocalDateTime registerDate;
	private LocalDateTime updateDate;
	private String content;
	private Boolean process;
}
