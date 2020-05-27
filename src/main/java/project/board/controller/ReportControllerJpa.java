package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.entity.dto.ReportRequest;
import project.board.service.ReportServiceJpa;
import project.board.util.MySessionUtils;

@RestController
@RequiredArgsConstructor
public class ReportControllerJpa {
	
	private final ReportServiceJpa reportService;
	private final MySessionUtils sUtils;
	
	@PostMapping("/ajax/v1/report/{targetId}")
	@AjaxLoginAuth
	public ResponseEntity<?> processReport(
			@PathVariable("targetId") Long targetId,
			@ModelAttribute("reportInfo") ReportRequest request,
			HttpSession session
			)
	{
		boolean status = reportService.save(sUtils.getMemberId(session), request, targetId);
		return ResponseEntity.ok(status);
	}
}
