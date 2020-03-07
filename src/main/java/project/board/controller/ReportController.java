package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.LoginAuth;
import project.board.service.ReportService;

@Controller
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("/ajax/report/{target}")
	public ResponseEntity<?> processReportArticle(
			@PathVariable("target") String target,
			@RequestParam("targetId") Long targetId,
			@RequestParam("checkInfo") Long checkInfo,
			@RequestParam(required = false, value = "reportContent") String reportContent,
			HttpSession session
			)
	{
		Long memberId = (Long) session.getAttribute("memberId");
		if(memberId == null) {
			return ResponseEntity.status(302).body("/login");
		}
		
		String message = reportService.addReport(target.toUpperCase(), targetId, memberId, checkInfo, reportContent);
		return ResponseEntity.ok().body(message);
	}

}
