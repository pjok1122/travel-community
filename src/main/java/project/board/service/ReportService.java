package project.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.repository.ReportRepository;

@Service
public class ReportService {

	@Autowired
	ReportRepository reportRepository;
	
	public void addArticleReport(Long articleId, Long memberId, Long checkInfo, String reportContent) {
		String target = "ARTICLE";
		reportRepository.insertReport(target, articleId, memberId, checkInfo, reportContent);
	}

}
