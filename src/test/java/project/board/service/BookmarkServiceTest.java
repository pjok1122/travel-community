package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.entity.Article;
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
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private Article createArticle(Member member, Category category, String title, String content, Nation nation) {
		return articleRepository.save(new Article(member, category, title, content, nation));
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
}
