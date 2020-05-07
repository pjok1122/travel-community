package project.board.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;

@Entity
@Getter
public class ReportInfo {
	@Id
	private Long id;
	private String content;
}
