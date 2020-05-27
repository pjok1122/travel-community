package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.entity.Article;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.service.ArticleLikeServiceJpa;
import project.board.util.MySessionUtils;

@RestController
@RequiredArgsConstructor
public class ArticleLikeControllerJpa {
	
	private final ArticleLikeServiceJpa articleLikeService;
	private final ArticleRepositoryJpa articleRepository;
	private final MySessionUtils sessionUtils;
	
	/**
	 * 게시물의 좋아요 수를 반환한다.
	 * @param articleId
	 * @return 좋아요 개수
	 */
	@GetMapping("/ajax/v1/article/{articleId}/like")
	public int getLikeCount(@PathVariable("articleId") Long articleId) {
		Article article = articleRepository.findById(articleId).orElseThrow(()-> new NoExistException());
		return article.getGood();
	}
	
	/**
	 * 게시물에 좋아요(또는 취소)를 반영한다.
	 * @param articleId
	 * @param session
	 * @return 현재 로그인한 유저가 게시물에 반영한 좋아요 상태 True(좋아요) False(좋아요X)
	 */
	@PostMapping("/ajax/v1/article/{articleId}/like")
	@AjaxLoginAuth
	public boolean processLikeArticle(
			@PathVariable("articleId") Long articleId,
			HttpSession session)
	{
		return articleLikeService.modifyLikeStatus(sessionUtils.getMemberId(session), articleId);
		
	}
	
	
}
