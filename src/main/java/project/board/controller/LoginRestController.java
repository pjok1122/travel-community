package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.util.SessionManager;

@RestController
@RequiredArgsConstructor
public class LoginRestController {

    private final SessionManager sessionManager;

    @GetMapping("/ajax/login_check")
    public ResponseEntity<?> isLogin(HttpSession session) {
        if (sessionManager.getMemberId(session) == null) {
            return ResponseEntity.status(302).body("/login");
        } else {
            return ResponseEntity.ok().body(session.getAttribute("email"));
        }
    }
}
