package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.domain.dto.GpsDecimal;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.entity.TempArticle;
import project.board.entity.UploadFile;
import project.board.entity.dto.ArticleForm;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.exception.EntityOwnerMismatchException;
import project.board.exception.ExceedTempArticleLimitException;
import project.board.exception.NoExistException;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.PostFileRepositoryJpa;
import project.board.repository.TempArticleRepositoryJpa;

@SpringBootTest
@Transactional
public class TempArticleServiceTest {
	
	@Autowired private EntityManager em;
	@Autowired private TempArticleServiceJpa tempArticleService;
	@Autowired private TempArticleRepositoryJpa tempArticleRepository;
	@Autowired private PostFileRepositoryJpa postFileRepository;
	@Autowired private MemberRepositoryJpa memberRepository;
	private final int TEMP_ARTICLE_MAX_SIZE = 10;
	
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private TempArticle createArticle(Member member, Category category, String title, String content, Nation nation) {
		return tempArticleRepository.save(new TempArticle(member, category, title, content, nation));
	}
	
	private UploadFile createPostFile(TempArticle article, Double latitude, Double longitude) {
		UploadFile image = new UploadFile();
		image.setGps(new GpsDecimal(latitude, longitude));
		article.addImage(image);
		
		return image;
	}
	
	private ArticleForm createArticleForm(Long articleId, Category category, String title, String content, Nation nation) {
		return ArticleForm.builder()
				.articleId(articleId)
				.category(category)
				.title(title)
				.content(content)
				.nation(nation)
				.build();
	}
	
	/**
	 * save() Test
	 * 저장이 정상적으로 동작하는지 테스트한다.
	 * Image는 없는 상태로 테스트한다.
	 */
	@Test
	public void 게시물_저장() {
		//given
		Member member = createMember("email@email", "password");
		ArticleForm articleForm = createArticleForm(null, Category.ACCOMODATION, "title", "content", Nation.KR);
		
		//when
		Long id = tempArticleService.save(member.getId(), articleForm);				
		TempArticle article = tempArticleRepository.findById(id).orElseThrow(()->new NoExistException());
		
		//then
		assertThat(article.getTitle()).isEqualTo("title");
		assertThat(article.getContent()).isEqualTo("content");
		assertThat(article.getCategory()).isEqualTo(Category.ACCOMODATION);
		assertThat(article.getNation()).isEqualTo(Nation.KR);
	}
	
	
	/**
	 * save() Test
	 * 임시저장글의 최대 개수를 초과하는 경우 
	 */
	@Test
	public void 게시물_저장_개수_초과() {
		//given
		Member member = createMember("email@email", "password");
		for(int i=0;i<TEMP_ARTICLE_MAX_SIZE;i++) {
			tempArticleRepository.save(new TempArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR));
		}
		em.flush();
		em.clear();
		
		//when & then
		ArticleForm articleForm = createArticleForm(null, Category.ACCOMODATION, "title", "content", Nation.KR);
		assertThrows(ExceedTempArticleLimitException.class, ()->tempArticleService.save(member.getId(), articleForm));
	}
	
	
	
	/**
	 * getMyArticles() Test
	 * 회원이 작성한 임시저장글 목록이 정상적으로 조회되는지 확인한다.
	 */
	@Test
	public void 임시저장글_목록_조회() {
		//given
		Member member = createMember("email@email", "password");
		Member member2 = createMember("email2@email", "password");
		
		createArticle(member, Category.ACCOMODATION, "title1", "content1", Nation.JP);
		createArticle(member, Category.ATTRACTIONS, "title2", "content2", Nation.KR);
		createArticle(member, Category.FESTIVAL, "title3", "content3", Nation.CN);
		createArticle(member, Category.ACCOMODATION, "title4", "content4", Nation.MY);
		createArticle(member2, Category.ACCOMODATION, "title4", "content4", Nation.MY);
		
		em.flush();
		em.clear();		
		//when
		List<TempArticle> myArticles = tempArticleService.getMyArticles(member.getId());
		
		//then
		assertThat(myArticles.size()).isEqualTo(4);
		assertThat(myArticles.get(0).getCategory()).isEqualTo(Category.ACCOMODATION);
		assertThat(myArticles.get(0).getTitle()).isEqualTo("title1");
		assertThat(myArticles.get(1).getCategory()).isEqualTo(Category.ATTRACTIONS);
		assertThat(myArticles.get(1).getTitle()).isEqualTo("title2");
		assertThat(myArticles.get(2).getCategory()).isEqualTo(Category.FESTIVAL);
		assertThat(myArticles.get(2).getContent()).isEqualTo("content3");
		assertThat(myArticles.get(3).getNation()).isEqualTo(Nation.MY);
	}
	
	/**
	 * remove() Test
	 * 게시물이 정상적으로 삭제되는지 확인한다.
	 */
	@Test
	public void 게시물_삭제() {
		//given
		Member member = createMember("email1@email", "password");
		
		TempArticle article1 = createArticle(member, Category.ACCOMODATION, "title1", "content1", Nation.JP);
		TempArticle article2 = createArticle(member, Category.ATTRACTIONS, "title2", "content2", Nation.KR);
		em.flush();
		em.clear();		
		
		//when
		tempArticleService.remove(member.getId(), article1.getId());
		
		//then
		List<TempArticle> articles = tempArticleRepository.findAll();
		
		assertThat(articles.size()).isEqualTo(1);
		assertThat(articles.get(0).getCategory()).isEqualTo(Category.ATTRACTIONS);
		assertThat(articles.get(0).getTitle()).isEqualTo("title2");
		assertThat(articles.get(0).getContent()).isEqualTo("content2");
		assertThat(articles.get(0).getNation()).isEqualTo(Nation.KR);
	}
	
	/**
	 * remove() Test
	 * 타인의 게시물을 삭제하려 시도할 때, EntityOwnerMismatchException 예외가 발생한다.
	 */
	@Test
	public void 게시물_삭제_예외() {
		//given
		Member member1 = createMember("email1@email", "password");
		Member member2 = createMember("email2@email", "password");

		TempArticle article1 = createArticle(member1, Category.ACCOMODATION, "title1", "content1", Nation.JP);
		
		//when & then
		assertThrows(EntityOwnerMismatchException.class, ()->tempArticleService.remove(member2.getId(), article1.getId()));
	}
	
	/**
	 * update() Test
	 * * 게시물 수정이 정상적으로 동작하는지 테스트한다.
	 * title, content, nation, category, postFiles이 전부 수정(삭제)되는지 확인해야 한다.
	 */
	@Test
	public void 게시물_수정() {
		//given
		Member member = createMember("email@email.com", "password");
		TempArticle article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createPostFile(article, 1.2, 2.1);
		createPostFile(article, 1.3, 2.2);
		ArticleForm newArticle = createArticleForm(article.getId(), Category.RESTAURANT, "newTitle", "newContent", null);
		
		//when
		tempArticleService.update(member.getId(), newArticle);
		em.flush();
		em.clear();

		//then
		TempArticle findArticle = tempArticleRepository.findDetailById(article.getId()).orElseThrow(()->new NoExistException());
		
		assertThat(findArticle.getCategory()).isEqualTo(Category.RESTAURANT);
		assertThat(findArticle.getTitle()).isEqualTo("newTitle");
		assertThat(findArticle.getContent()).isEqualTo("newContent");
		assertThat(findArticle.getNation()).isEqualTo(Nation.KR);
		assertThat(findArticle.getUploadFiles().size()).isEqualTo(0);
	}
	
}
