package project.board.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import project.board.enums.ReportTarget;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(
			name = "MEMBER_TARGET_TAGET_ID_UNIQUE", 
			columnNames = {"target_id", "member_id", "target"}
		))
public class Report extends BaseTimeEntity{
	@Id @GeneratedValue
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Enumerated(EnumType.STRING)
	private ReportTarget target;
	
	@Column(name = "target_id", nullable = false)
	private Long targetId;
	
	private String content;
	private Boolean process;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_info_id")
	private ReportInfo reportInfo;
	
	//======== 생성 메서드 ========
	public static Report createReport(Member member, ReportTarget target, Long targetId, String content, ReportInfo reportInfo)
	{
		Report report = new Report();
		report.member = member;
		report.target = target;
		report.targetId = targetId;
		report.content = content;
		report.process = false;
		report.reportInfo = reportInfo;
		return report;
	}
}
