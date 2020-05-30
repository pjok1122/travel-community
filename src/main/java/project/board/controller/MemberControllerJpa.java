package project.board.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import project.board.domain.dto.MyPage;
import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.TempArticle;
import project.board.entity.dto.ArticleDto2;
import project.board.entity.dto.TempArticleDto;
import project.board.repository.MemberRepositoryJpa;
import project.board.service.ArticleServiceJpa;
import project.board.service.BookmarkServiceJpa;
import project.board.service.MemberServiceJpa;
import project.board.service.TempArticleServiceJpa;
import project.board.util.MySessionUtils;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MemberControllerJpa {
	
	private static final String BASE_VIEW_NAME = "member/mypage/";
	
	private final MemberServiceJpa memberService;
	private final ArticleServiceJpa articleService;
	private final TempArticleServiceJpa tempArticleService;
	private final BookmarkServiceJpa bookmarkService;
	private final MySessionUtils sessionUtils;
	private final MemberRepositoryJpa memberRepository;
	
	/**
	 * 회원 정보 페이지를 보여준다.
	 * "MemberDto myinfo" 라는 객체에 회원정보를 저장하여 화면을 출력한다.
	 * @param session
	 * @param model
	 * @return 
	 */
	@GetMapping("/member")
	public String getMyPage(HttpSession session, Model model) {
		Long memberId = sessionUtils.getMemberId(session);
		model.addAttribute("myinfo", memberService.getMyInfo(memberId));
		
		return BASE_VIEW_NAME + "member";
	}
	/**
	 * 회원이 작성한 게시물을 보여준다.
	 * @param session
	 * @param model
	 * @return 
	 */
	@GetMapping("/article")
	public String getMyArticle(
			@RequestParam(value = "page", defaultValue = "1") int pageNo, 
			HttpSession session,
			Model model) {
		Long memberId = sessionUtils.getMemberId(session);
		
		//Entity -> DTO
		Page<Article> page = articleService.getMyArticles(memberId, pageNo-1);
		Page<ArticleDto2> pageDto = page.map(o-> new ArticleDto2(o));
		
		//Page -> MyPage
		MyPage<ArticleDto2> myPage = new MyPage<ArticleDto2>(page.getNumber()+1, (int) page.getTotalElements());
		myPage.setList(pageDto.getContent());
		
		model.addAttribute("page", myPage);
		return BASE_VIEW_NAME + "article";
	}
	
	/**
	 * 회원이 작성한 임시저장 글 목록을 보여준다.
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping("/temp-article")
	public String getMyTempArticle(
			HttpSession session,
			Model model)
	{
		Long memberId = sessionUtils.getMemberId(session);
		
		//Entity -> DTO
		List<TempArticle> myArticles = tempArticleService.getMyArticles(memberId);
		List<TempArticleDto> dtos = myArticles.stream().map(TempArticleDto::new).collect(Collectors.toList());
		
		//Page -> MyPage
		MyPage<TempArticleDto> myPage = new MyPage<>(1, dtos.size());
		myPage.setList(dtos);
		
		model.addAttribute("page", myPage);
		return BASE_VIEW_NAME + "temp_article";
	}
	
	@GetMapping("bookmark")
	public String getMyBookmark(
			@RequestParam(value = "page", defaultValue = "1") int pageNo,
			HttpSession session,
			Model model)
	{
		Long memberId = sessionUtils.getMemberId(session);
		
		//Entity -> DTO
		Page<Bookmark> page = bookmarkService.getMyBookmarks(memberId, pageNo-1);
		Page<ArticleDto2> pageDto = page.map(o-> new ArticleDto2(o.getArticle()));
		
		//Page -> MyPage
		MyPage<ArticleDto2> myPage = new MyPage<>(page.getNumber()+1, (int) page.getTotalElements());
		myPage.setList(pageDto.getContent());
		
		model.addAttribute("page", myPage);
		return BASE_VIEW_NAME + "bookmark";
	}
}
