package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.entity.Article;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepository;
import project.board.repository.ArticleRepositoryJpa;
import project.board.service.ArticleLikeServiceJpa;
import project.board.service.ArticleServiceJpa;
import project.board.util.MySessionUtils;

@Controller
@RequiredArgsConstructor
public class ArticleLikeControllerJpa {
	
	private final ArticleLikeServiceJpa articleLikeService;
	private final ArticleServiceJpa articleService;
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
	
	@PostMapping("/ajax/v1/article/{articleId}/like")
	@AjaxLoginAuth
	public boolean processLikeArticle(
			@PathVariable("articleId") Long articleId,
			HttpSession session)
	{
		return articleLikeService.modifyLikeStatus(sessionUtils.getMemberId(session), articleId);
		
	}
	
	
}
