package project.board.domain.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ReplyDto {
	private String content;
	private int good;
	private LocalDateTime registerDate;
	
	private String writer;
}
