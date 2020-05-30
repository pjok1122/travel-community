package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.BookmarkRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;

@SpringBootTest
@Transactional
public class BookmarkServiceTest {

	@Autowired private BookmarkServiceJpa bookmarkService;
	@Autowired private ArticleRepositoryJpa articleRepository;
	@Autowired private MemberRepositoryJpa memberRepository;
	@Autowired private BookmarkRepositoryJpa bookmarkRepository;
	@Autowired private EntityManager em;
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private Article createArticle(Member member, Category category, String title, String content, Nation nation) {
		return articleRepository.save(new Article(member, category, title, content, nation));
	}
	
	private Bookmark createBookmark(Member member, Article article) {
		return bookmarkRepository.save(Bookmark.createBookmark(member, article));
	}
	
	/**
	 * modifyLikeStatus() Test
	 * 북마크 반영이 정상적으로 이루어지는 지를 확인한다.
	 */
	@Test
	public void 북마크() {
		//given
		Member member = createMember("email@abc.kr", "1234");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		
		em.flush();
		em.clear();
		
		//when
		boolean status = bookmarkService.modifyBookmarkStatus(member.getId(), article.getId());
		
		//then
		boolean status2 = bookmarkRepository.existsByMemberAndArticle(member, article);
		
		assertThat(status).isEqualTo(true);
		assertThat(status2).isEqualTo(true);
	}
	
	/**
	 * modifyLikeStatus() Test
	 * 북마크 취소 반영이 정상적으로 이루어지는 지를 확인한다.
	 */
	@Test
	public void 북마크_취소() {
		//given
		Member member = createMember("email@abc.kr", "1234");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		bookmarkService.modifyBookmarkStatus(member.getId(), article.getId());

		//when
		boolean status = bookmarkService.modifyBookmarkStatus(member.getId(), article.getId());
		
		//then
		boolean status2 = bookmarkRepository.existsByMemberAndArticle(member, article);
		
		assertThat(status).isEqualTo(false);
		assertThat(status2).isEqualTo(false);
	}
	
	/**
	 * getMyBookmarks() Test
	 * 북마크 목록을 정상적으로 불러오는지 확인한다.
	 */
	@Test
	public void 북마크_목록_조회() {
		//given
		Member member = createMember("email@abc.kr", "1234");
		Article article1 = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		Article article2 = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		Article article3 = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		createBookmark(member, article1);
		createBookmark(member, article2);
		
		//when
		Page<Bookmark> bookmarks = bookmarkService.getMyBookmarks(member.getId(), 0);
		
		//then
		assertThat(bookmarks.getTotalElements()).isEqualTo(2);
		assertThat(bookmarks.isFirst()).isTrue();
		assertThat(bookmarks.isLast()).isTrue();
		assertThat(bookmarks.getTotalPages()).isEqualTo(1);
		
		

	}
}
