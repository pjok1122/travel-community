package project.board.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.AjaxLoginAuth;
import project.board.service.ArticleService;
import project.board.service.BookmarkService;
import project.board.util.MySessionUtils;

@Controller
public class BookmarkController {
	
	@Autowired
	BookmarkService bookmarkService;
	
	@Autowired
	ArticleService articleService;
	
	@Autowired
	MySessionUtils sUtils;
	
	@PostMapping("/ajax/bookmark")
	@AjaxLoginAuth
	public ResponseEntity<?> processAddBookmark(
			@RequestParam("articleId") Long articleId,
			HttpSession session
			)
	{
		int bookmarkStatus = bookmarkService.modifyBookmarkStatus(sUtils.getMemberId(session), articleId);
		return ResponseEntity.ok().body(bookmarkStatus);
	}
}
