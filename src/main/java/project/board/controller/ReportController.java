package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.LoginAuth;
import project.board.service.ReportService;

@Controller
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("/ajax/report/article")
	public ResponseEntity<?> processReportArticle(
			@RequestParam("articleId") Long articleId,
			@RequestParam("checkInfo") Long checkInfo,
			@RequestParam(required = false, value = "reportContent") String reportContent,
			HttpSession session
			)
	{
		Long memberId = (Long) session.getAttribute("memberId");
		if(memberId == null) {
			return ResponseEntity.status(302).body("/login");
		}
		
		reportService.addArticleReport(articleId, memberId, checkInfo, reportContent);
		return ResponseEntity.ok().build();
	}
}
