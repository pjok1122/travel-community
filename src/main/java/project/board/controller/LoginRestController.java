package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.common.SessionContext;
import project.board.util.SessionManager;

@RestController
@RequiredArgsConstructor
public class LoginRestController {

    @GetMapping("/ajax/login-check")
    public ResponseEntity<?> isLogin(@RequestAttribute SessionContext session) {
        if (!session.isLoggedIn()) {
            return ResponseEntity.status(302).body("/login");
        } else {
            return ResponseEntity.ok().body(session.getEmail());
        }
    }
}
