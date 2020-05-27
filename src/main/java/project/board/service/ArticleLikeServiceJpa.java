package project.board.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.ArticleLike;
import project.board.entity.Member;
import project.board.exception.NoExistException;
import project.board.repository.ArticleLikeRepositoryJpa;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleLikeServiceJpa {
	
	private final ArticleRepositoryJpa articleRepository;
	private final MemberRepositoryJpa memberRepository;
	private final ArticleLikeRepositoryJpa articleLikeRepository;
	
	/**
	 * 좋아요 상태를 변경한다.
	 * @param memberId
	 * @param articleId
	 * @return 변경된 좋아요 상태를 반환한다. True(좋아요 활성 상태) False(좋아요 비활성 상태)
	 */
	public boolean modifyLikeStatus(Long memberId, Long articleId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException());
		Article article = articleRepository.findById(articleId).orElseThrow(()->new NoExistException());
		Optional<ArticleLike> optLike = articleLikeRepository.findByMemberAndArticle(member, article);
		
		if(optLike.isPresent()) {
			unlike(member, article);
			return false;
		} else{
			like(member, article);
			return true;
		}
	}
	
	@Transactional
	private void like(Member member, Article article) {
		articleLikeRepository.save(ArticleLike.createArticleLike(article, member));
		article.goodUp();
	}
	
	@Transactional
	private void unlike(Member member, Article article) {
		articleLikeRepository.deleteByMemberAndArticle(member, article);
		article.goodDown();
	}
}
