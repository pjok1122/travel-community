package project.board.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import project.board.annotation.AjaxLoginAuth;
import project.board.enums.ReportTargetType;
import project.board.service.ReportServiceV2;
import project.board.util.SessionManager;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
public class ReportRestControllerV2 {

    private final ReportServiceV2 reportServiceV2;
    private final SessionManager sessionManager;

    @PostMapping(value = "/ajax/report/{target}")
    @AjaxLoginAuth
    public ResponseEntity<?> report(@PathVariable("target") ReportTargetType target,
                                    @Validated @RequestBody ReportRequest request,
									HttpSession session) {

        return ResponseEntity.ok().body(reportServiceV2.insert(target,
                                            request.getTargetId(),
                                            request.getCheckInfo(),
                                            request.getContent(),
                                            sessionManager.getMemberId(session)));
    }

    @Data
    @AllArgsConstructor
    public static class ReportRequest {
        @NotNull
        private Long targetId;
        @NotNull
        private Long checkInfo;
        private String content;
    }
}
