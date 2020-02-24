package project.board.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.service.ArticleService;
import project.board.service.BookmarkService;

@Controller
public class BookmarkController {
	
	@Autowired
	BookmarkService bookmarkService;
	
	@Autowired
	ArticleService articleService;
	
	@PostMapping("/ajax/bookmark")
	public ResponseEntity<?> processAddBookmark(
			@RequestParam("articleId") Long articleId,
			HttpServletRequest request,
			HttpSession session,
			Model model)
	{
		if(session.getAttribute("memberId") == null) {
			session.setAttribute("prevPage", "/article/" + articleId);
			ResponseEntity.status(302).body("/login");
		}
		
		int bookmarkStatus = bookmarkService.modifyBookmarkStatus((Long)session.getAttribute("memberId"), articleId);
		return ResponseEntity.ok().body(bookmarkStatus);
	}
}
