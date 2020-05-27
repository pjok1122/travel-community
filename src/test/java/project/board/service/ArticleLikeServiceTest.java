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
import project.board.repository.ArticleLikeRepositoryJpa;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;

@SpringBootTest
@Transactional
public class ArticleLikeServiceTest {

	@Autowired private ArticleLikeServiceJpa articleLikeService;
	@Autowired private ArticleRepositoryJpa articleRepository;
	@Autowired private MemberRepositoryJpa memberRepository;
	@Autowired private ArticleLikeRepositoryJpa articleLikeRepository;
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private Article createArticle(Member member, Category category, String title, String content, Nation nation) {
		return articleRepository.save(new Article(member, category, title, content, nation));
	}
	
	/**
	 * modifyLikeStatus() Test
	 * 좋아요 반영이 정상적으로 이루어지는 지를 확인한다.
	 */
	@Test
	public void 좋아요() {
		//given
		Member member = createMember("email@abc.kr", "1234");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		
		//when
		boolean status = articleLikeService.modifyLikeStatus(member.getId(), article.getId());
		
		//then
		boolean status2 = articleLikeRepository.existsByMemberAndArticle(member, article);
		
		assertThat(status).isEqualTo(true);
		assertThat(status2).isEqualTo(true);
		assertThat(article.getGood()).isEqualTo(1);
	}
	
	/**
	 * modifyLikeStatus() Test
	 * 좋아요 취소 반영이 정상적으로 이루어지는 지를 확인한다.
	 */
	@Test
	public void 좋아요_취소() {
		//given
		Member member = createMember("email@abc.kr", "1234");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		articleLikeService.modifyLikeStatus(member.getId(), article.getId());

		//when
		boolean status = articleLikeService.modifyLikeStatus(member.getId(), article.getId());
		
		//then
		boolean status2 = articleLikeRepository.existsByMemberAndArticle(member, article);
		
		assertThat(status).isEqualTo(false);
		assertThat(status2).isEqualTo(false);
		assertThat(article.getGood()).isEqualTo(0);
	}
	
	/**
	 * getLikeCount() Test
	 * 게시물의 좋아요 개수를 조회한다.
	 */
	public void 게시물_좋아요_개수_조회() {
		//given
		Member member1 = createMember("email1@email.com", "password");
		Member member2 = createMember("email2@email.com", "password");
		Member member3 = createMember("email3@email.com", "password");
		Article article = createArticle(member1, Category.ACCOMODATION, "title", "content", Nation.KR);
		
		//when
		articleLikeService.modifyLikeStatus(member1.getId(), article.getId());
		articleLikeService.modifyLikeStatus(member2.getId(), article.getId());
		articleLikeService.modifyLikeStatus(member3.getId(), article.getId());
		
		//then
		assertThat(article.getGood()).isEqualTo(3);
	}
}
