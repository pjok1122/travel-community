package project.board.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;

@SpringBootTest
@Transactional
public class ArticleTest {
	
	@Autowired private MemberRepositoryJpa memberRepository;
	@Autowired private ArticleRepositoryJpa articleRepository;
	@Autowired private EntityManager em;
	/**
	 * goodUp(), hitUp() Test
	 * 변경감지를 통해 조회수가 증가하는지를 테스트한다.
	 */
	@Test
	public void 조회수_좋아요_증가_테스트() {
		
		//given
		Member member = memberRepository.save(Member.createMember("email@email.com", "password"));
		Article article = articleRepository.save(new Article(member, Category.ACCOMODATION, "title", "content", Nation.KR));
		
		//when
		article.goodUp();
		article.hitUp();		
		em.flush();
		em.clear();
		Article findArticle = articleRepository.findById(article.getId()).orElseThrow(()->new NoExistException());
		
		//then
		assertThat(findArticle.getGood()).isEqualTo(1);
		assertThat(findArticle.getHit()).isEqualTo(1);

	}
}
