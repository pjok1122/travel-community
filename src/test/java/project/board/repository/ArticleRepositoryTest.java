package project.board.repository;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import project.board.domain.dto.GpsDecimal;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.entity.PostFile;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.exception.NoExistException;

@SpringBootTest
@Transactional
public class ArticleRepositoryTest {
	@Autowired private EntityManager em;
	
	@Autowired private MemberRepositoryJpa memberRepository;
	@Autowired private ArticleRepositoryJpa articleRepository;
	
	
	/**
	 * count() Test
	 * 게시물 개수가 올바르게 나오는지 테스트한다.
	 */
	@Test
	public void 게시물_개수_조회() {
		//given
		Member member = memberRepository.save(Member.createMember("email@email.com", "password"));
		List<Article> articles = new ArrayList<>();
		articles.add(new Article(member, Category.ACCOMODATION, "title", "content", Nation.KR));
		articles.add(new Article(member, Category.ACCOMODATION, "title", "content", Nation.CN));
		articles.add(new Article(member, Category.ACCOMODATION, "title", "content", Nation.KR));
		articles.add(new Article(member, Category.ATTRACTIONS, "title", "content", Nation.KR));
		articleRepository.saveAll(articles);
		
		em.flush();
		em.clear();
		
		//when
		int count1 = articleRepository.count(Category.ACCOMODATION.toString(), Nation.KR.toString(),null);
		int count2 = articleRepository.count(Category.ALL.toString(), Nation.KR.toString(), null);

		//then
		assertThat(count1).isEqualTo(2);
		assertThat(count2).isEqualTo(3);
	}
	
	/**
	 * findDetailById() Test
	 * Article을 조회할 때, PostFile을 페치조인해서 가져온다.
	 */
	@Test
	public void 상세조회_페치조인(){
		//given
		Member member = memberRepository.save(Member.createMember("email@email.com", "password"));
		Article article = articleRepository.save(new Article(member, Category.ACCOMODATION, "title", "content", Nation.KR));
		
		PostFile postFile = new PostFile();
		postFile.setGps(new GpsDecimal(1.2, 2.1));
		PostFile postFile2 = new PostFile();
		postFile2.setGps(new GpsDecimal(1.3, 2.2));
		
		article.addPostFile(postFile);
		article.addPostFile(postFile2);
		
		//when
		em.flush();
		em.clear();
		Article findArticle = articleRepository.findDetailById(article.getId()).orElseThrow(()->new NoExistException());
		
		//then
		assertThat(findArticle.getCategory()).isEqualTo(article.getCategory());
		assertThat(findArticle.getTitle()).isEqualTo(article.getTitle());
		assertThat(findArticle.getContent()).isEqualTo(article.getContent());
		assertThat(findArticle.getPostFiles().size()).isEqualTo(2);
		assertThat(findArticle.getPostFiles().get(0).getLatitude()).isEqualTo(1.2);
		assertThat(findArticle.getPostFiles().get(0).getLongitude()).isEqualTo(2.1);
		assertThat(findArticle.getPostFiles().get(1).getLatitude()).isEqualTo(1.3);
		assertThat(findArticle.getPostFiles().get(1).getLongitude()).isEqualTo(2.2);
		assertThat(findArticle.getPostFiles().get(0).getLatitude()).isEqualTo(1.2);
	}
}
