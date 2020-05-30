package project.board.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
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
		System.out.println(optBookmark.isPresent());
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

	/**
	 * 회원의 북마크 목록을 불러온다.
	 * Bookmark는 Article을 참조하고 있으므로 사용하는 쪽에서 가공해서 사용한다.
	 * @param memberId
	 * @param pageNo
	 * @return Page<Bookmark>
	 */
	public Page<Bookmark> getMyBookmarks(Long memberId, int pageNo) {
		Member member = memberRepository.findById(memberId).orElseThrow(()->new NoExistException());
		PageRequest pageable = PageRequest.of(pageNo, 10, Direction.DESC, "createdDate");
		return bookmarkRepository.findByMember(member, pageable);
	}
	

}
