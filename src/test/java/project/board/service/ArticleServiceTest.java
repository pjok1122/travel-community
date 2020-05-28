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
import project.board.domain.dto.Page;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.entity.dto.ArticleDetail;
import project.board.entity.dto.ArticleDto2;
import project.board.entity.dto.ArticleForm;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.exception.EntityOwnerMismatchException;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.PostFileRepositoryJpa;

@SpringBootTest
@Transactional
public class ArticleServiceTest {
	
	@Autowired private EntityManager em;
	@Autowired private ArticleServiceJpa articleService;
	@Autowired private ArticleRepositoryJpa articleRepository;
	@Autowired private PostFileRepositoryJpa postFileRepository;
	@Autowired private MemberRepositoryJpa memberRepository;
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private Article createArticle(Member member, Category category, String title, String content, Nation nation) {
		return articleRepository.save(new Article(member, category, title, content, nation));
	}
	
	private PostFile createPostFile(Article article, Double latitude, Double longitude) {
		PostFile postFile = new PostFile();
		postFile.setGps(new GpsDecimal(latitude, longitude));
		article.addPostFile(postFile);
		
		return postFile;
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
		Long id = articleService.save(member.getId(), articleForm);				
		Article article = articleRepository.findById(id).orElseThrow(()->new NoExistException());
		
		//then
		assertThat(article.getTitle()).isEqualTo("title");
		assertThat(article.getContent()).isEqualTo("content");
		assertThat(article.getCategory()).isEqualTo(Category.ACCOMODATION);
		assertThat(article.getNation()).isEqualTo(Nation.KR);
		
		
	}
	
	/**
	 * find() Test
	 * Category, Nation에 맞는 게시물 목록이 정상 조회되는지 테스트한다.
	 */
	@Test
	public void 게시물_목록조회() {
		//given
		Member member = createMember("email@email", "password");
		createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createArticle(member, Category.ACCOMODATION, "title", "content", Nation.CN);
		createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createArticle(member, Category.ATTRACTIONS, "title", "content", Nation.KR);

		em.flush();
		em.clear();
		
		//when
		Page<ArticleDto2> page = articleService.find(Category.ACCOMODATION, Nation.KR, new PageAndSort(), null);
		
		//then
		assertThat(page.getNumberOfRecords()).isEqualTo(2);
		assertThat(page.getList().size()).isEqualTo(2);
		assertThat(page.getCurrentPageNo()).isEqualTo(1);
		page.getList().forEach(o->assertThat(o.getCategory()).isEqualTo(Category.ACCOMODATION));
	}
	
	/**
	 * find() Search Test
	 * 검색 조건에 맞는 게시물을 정상적으로 가져오는지 테스트한다.
	 */
	@Test
	public void 게시물_검색_조회() {
		//given
		Member member = createMember("email@email", "password");
		createArticle(member, Category.ACCOMODATION, "A타이거", "content", Nation.KR);
		createArticle(member, Category.ACCOMODATION, "B타블로", "content", Nation.CN);
		createArticle(member, Category.ACCOMODATION, "C타이슨", "content", Nation.KR);
		createArticle(member, Category.ATTRACTIONS, "D타이완", "content", Nation.KR);

		em.flush();
		em.clear();
	
		//when
		Page<ArticleDto2> page = articleService.find(Category.ALL, Nation.ALL, new PageAndSort(), "타이");
		
		//then
		assertThat(page.getNumberOfRecords()).isEqualTo(3);
		assertThat(page.getList().size()).isEqualTo(3);
		page.getList().forEach(o->assertThat(o.getTitle()).contains("타이"));
	}
	
	/**
	 * getMyArticle() Test
	 * 업데이트와 관련된 서비스 기능 테스트
	 * 다른 사람의 게시물을 수정하려고 하면, 예외 발생.
	 * 본인 게시물 조회
	 */
	@Test
	public void 타인과_본인_게시물_수정폼_불러오기() {
		//given
		Member member = createMember("email@email.com", "password");
		Member member2 = createMember("another@email.com", "password");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		
		//when
		Article updateForm = articleService.getMyArticle(member.getId(), article.getId());

		//then
		assertThat(updateForm.getCategory()).isEqualTo(article.getCategory());
		assertThat(updateForm.getContent()).isEqualTo(article.getContent());
		assertThat(updateForm.getTitle()).isEqualTo(article.getTitle());
		assertThat(updateForm.getNation()).isEqualTo(article.getNation());
		
		//예외 발생
		assertThrows(EntityOwnerMismatchException.class, ()->articleService.getMyArticle(member2.getId(), article.getId()));
	}
	
	/**
	 * getDetail() Test
	 * 게시물 상세조회를 테스트한다.
	 */
	@Test
	public void 게시물_상세조회() {
		//given
		Member member = createMember("email@email.com", "password");
		Member loginMember = createMember("pjok1122@email.com", "password");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createPostFile(article, 1.2, 2.1);
		createPostFile(article, 1.3, 2.2);
		
		em.flush();
		em.clear();

		//when
		ArticleDetail detail = articleService.getDetail(loginMember.getId(), article.getId());
		
		//then
		assertThat(detail.getArticleId()).isEqualTo(article.getId());
		assertThat(detail.getCreatedDate()).isEqualTo(article.getCreatedDate());
		assertThat(detail.getGood()).isEqualTo(0);
		assertThat(detail.getGpsInfo().size()).isEqualTo(article.getPostFiles().size());
		assertThat(detail.isLiked()).isEqualTo(false);
		assertThat(detail.isBookmarked()).isEqualTo(false);
		assertThat(detail.getUpdateDate()).isEqualTo(article.getLastModifiedDate());
		assertThat(detail.getHit()).isEqualTo(1);
	}
	/**
	 * update() Test
	 * 게시물 수정이 정상적으로 동작하는지 테스트한다.
	 * title, content, nation, category, postFiles이 전부 수정(삭제)되는지 확인해야 한다.
	 */
	@Test
	public void 게시물_수정() {
		//given
		Member member = createMember("email@email.com", "password");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createPostFile(article, 1.2, 2.1);
		createPostFile(article, 1.3, 2.2);
		ArticleForm newArticle = createArticleForm(article.getId(), Category.RESTAURANT, "newTitle", "newContent", null);
		
		//when
		articleService.update(member.getId(), newArticle);
		em.flush();
		em.clear();
		Article findArticle = articleRepository.findDetailById(article.getId()).orElseThrow(()->new NoExistException());

		//then
		assertThat(findArticle.getCategory()).isEqualTo(Category.RESTAURANT);
		assertThat(findArticle.getTitle()).isEqualTo("newTitle");
		assertThat(findArticle.getContent()).isEqualTo("newContent");
		assertThat(findArticle.getNation()).isEqualTo(Nation.KR);
		assertThat(findArticle.getPostFiles().size()).isEqualTo(0);
	}

	/**
	 * remove() Test
	 * 게시물 삭제를 테스트한다.
	 * 게시물과 PostFile이 함께 삭제되는지 확인한다.
	 */
	@Test
	public void 게시물_삭제() {
		//given
		Member member = createMember("email@email.com", "password");
		Article article = createArticle(member, Category.ACCOMODATION, "title", "content", Nation.KR);
		createPostFile(article, 1.2, 2.1);
		createPostFile(article, 1.3, 2.2);
		
		//when
		articleService.remove(member.getId(), article.getId());
		List<PostFile> postFiles = postFileRepository.findByArticle(article);
		
		//then
		assertThat(postFiles.size()).isEqualTo(0);
	}
	


}
