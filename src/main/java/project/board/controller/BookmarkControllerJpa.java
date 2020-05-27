package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.service.BookmarkServiceJpa;
import project.board.util.MySessionUtils;

@RestController
@RequiredArgsConstructor
public class BookmarkControllerJpa {
	
	private final BookmarkServiceJpa bookmarkService;
	private final MySessionUtils sUtils;
	
	/**
	 * 북마크를 반영한다.
	 * @param articleId
	 * @param session
	 * @return
	 */
	@PostMapping("/ajax/v1/article/{articleId}/bookmark")
	@AjaxLoginAuth
	public boolean processBookmark(
			@PathVariable("articleId") Long articleId,
			HttpSession session
			)
	{
		return bookmarkService.modifyBookmarkStatus(sUtils.getMemberId(session), articleId);
	}
}
