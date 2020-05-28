package project.board.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.BookmarkRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkServiceJpa {
	
	private final BookmarkRepositoryJpa bookmarkRepository;
	private final MemberRepositoryJpa memberRepository;
	private final ArticleRepositoryJpa articleRepository;
	
	
	/**
	 * 	/**
	 * 북마크 상태를 변경한다.
	 * @param memberId
	 * @param articleId
	 * @return 변경된 북마크 상태를 반환한다. True(북마크 활성 상태) False(북마크 비활성 상태)
	 */
	@Transactional
	public boolean modifyBookmarkStatus(Long memberId, Long articleId) {
		Member member = memberRepository.findById(memberId).orElseThrow(()-> new NoExistException());
		Article article = articleRepository.findById(articleId).orElseThrow(()-> new NoExistException());
		Optional<Bookmark> optBookmark = bookmarkRepository.findByMemberAndArticle(member, article);
		
		if(optBookmark.isPresent()) {
			destroy(optBookmark.get());
			return false;
		}
		else{
			create(member, article);
			return true;
		}
	}
	
	@Transactional
	private void create(Member member, Article article) {
		bookmarkRepository.save(Bookmark.createBookmark(member, article));
	}
	
	@Transactional
	private void destroy(Bookmark bookmark) {
		bookmarkRepository.delete(bookmark);
	}
	

}
