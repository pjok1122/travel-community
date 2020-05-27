package project.board.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import project.board.enums.ReportTarget;

@Data
@AllArgsConstructor
public class ReportRequest {
	private ReportTarget target;
	private Long targetId;
	private Long checkNo;
	private String content;
}
