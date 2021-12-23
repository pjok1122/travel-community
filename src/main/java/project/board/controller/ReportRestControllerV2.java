package project.board.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.board.annotation.AjaxLoginAuth;
import project.board.enums.ReportTargetType;
import project.board.service.ReportService;
import project.board.service.ReportServiceV2;
import project.board.util.SessionManager;

import javax.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class ReportRestControllerV2 {

    private final ReportServiceV2 reportServiceV2;
    private final SessionManager sessionManager;

    @PostMapping("/ajax/report/{target}")
    @AjaxLoginAuth
    public ResponseEntity<?> report(@PathVariable("target") ReportTargetType target,
                                    @ModelAttribute ReportRequest request,
									HttpSession session) {

        return ResponseEntity.ok().body(reportServiceV2.insert(target, request, sessionManager.getMemberId(session)));
    }

    @Data
    @AllArgsConstructor
    public class ReportRequest {
        private Long targetId;
        private Long checkInfo;
        private String content;
    }
}
