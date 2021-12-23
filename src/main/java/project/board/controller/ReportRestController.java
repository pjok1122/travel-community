package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.service.ReportService;
import project.board.util.SessionManager;

@RestController
@RequiredArgsConstructor
public class ReportRestController {

    private final ReportService reportService;
    private final SessionManager sessionManager;

//    @PostMapping("/ajax/report/{target}")
//    @AjaxLoginAuth
    public ResponseEntity<?> report(@PathVariable("target") String target,
									@RequestParam("targetId") Long targetId,
									@RequestParam("checkInfo") Long checkInfo,
									@RequestParam(required = false, value = "reportContent")
                                                   String reportContent,
									HttpSession session) {
		String message = reportService.addReport(target.toUpperCase(), targetId,
												 sessionManager.getMemberId(session), checkInfo, reportContent);
        return ResponseEntity.ok().body(message);
    }

}
