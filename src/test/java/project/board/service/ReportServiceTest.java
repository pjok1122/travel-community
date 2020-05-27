package project.board.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import project.board.entity.Article;
import project.board.entity.Member;
import project.board.entity.Report;
import project.board.entity.dto.ReportRequest;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.enums.ReportTarget;
import project.board.exception.NoExistException;
import project.board.repository.ArticleRepositoryJpa;
import project.board.repository.MemberRepositoryJpa;
import project.board.repository.ReportRepositoryJpa;

@SpringBootTest
@Transactional
public class ReportServiceTest {

	@Autowired private ArticleRepositoryJpa articleRepository;
	@Autowired private MemberRepositoryJpa memberRepository;
	@Autowired private ReportServiceJpa reportService;
	@Autowired private ReportRepositoryJpa reportRepository;
	
	private Member createMember(String email, String password) {
		return memberRepository.save(Member.createMember(email, password));
	}
	
	private Article createArticle(Member member, Category category, String title, String content, Nation nation) {
		return articleRepository.save(new Article(member, category, title, content, nation));
	}
	
	/**
	 * save() Test
	 * 게시물에 대한 신고 처리를 테스트한다.
	 */
	@Test
	public void 게시물_신고() {
		//given
		Member member1 = createMember("email1@naver.com", "123456");
		Member member2 = createMember("email2@naver.com", "123456");
		Article article = createArticle(member1, Category.ACCOMODATION, "title1", "content", Nation.KR);

		ReportRequest request = new ReportRequest(ReportTarget.ARTICLE, article.getId(), 4L, "신고 테스트입니다.");
		
		//when
		boolean status = reportService.save(member2.getId(), request, article.getId());
		
		//then
		Report report = reportRepository.findByTargetIdAndTargetAndMember(article.getId(), request.getTarget(), member2)
						.orElseThrow(()->new NoExistException());
		
		assertThat(report.getTargetId()).isEqualTo(article.getId());
		assertThat(report.getTarget()).isEqualTo(request.getTarget());
		assertThat(report.getMember()).isEqualTo(member2);
		assertThat(report.getContent()).isEqualTo(request.getContent());
		assertThat(status).isEqualTo(true);
		
	}
}
