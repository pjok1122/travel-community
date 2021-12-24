package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.service.BookmarkService;
import project.board.service.BookmarkServiceV2;
import project.board.util.SessionManager;

@RestController
@RequiredArgsConstructor
public class BookmarkRestController {

    private final BookmarkServiceV2 bookmarkService;
	private final SessionManager sessionManager;

    @PostMapping("/ajax/bookmark")
    @AjaxLoginAuth
	public ResponseEntity<?> add(@RequestParam("articleId") Long articleId, HttpSession session) {
		int status = bookmarkService.toggle(sessionManager.getMemberId(session), articleId);
        return ResponseEntity.ok().body(status);
    }
}
