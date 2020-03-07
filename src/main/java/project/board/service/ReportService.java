package project.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Report;
import project.board.repository.ReportRepository;

@Service
public class ReportService {

	@Autowired
	ReportRepository reportRepository;
	
	private static final String REPORTED_ARTICLE = "이미 신고가 접수된 게시물입니다.";
	private static final String REPORTED_COMMENT = "이미 신고가 접수된 댓글입니다.";
	private static final String SUCCESS_OF_REPORT = "신고가 접수되었습니다.";
	
	public String addReport(String target, Long targetId, Long memberId, Long checkInfo, String reportContent) {
		if(isReported(target,targetId,memberId)) {
			return REPORTED_ARTICLE;
		}
		reportRepository.insertReport(target, targetId, memberId, checkInfo, reportContent);
		return SUCCESS_OF_REPORT;
	}
	
	private Boolean isReported(String target, Long targetId, Long memberId) {
		Report report = reportRepository.selectReportByTargetIdAndMemberId(target, targetId, memberId);
		if(report!=null) {
			return true;
		} else {
			return false;
		}
	}

}
