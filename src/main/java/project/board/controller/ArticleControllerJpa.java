package project.board.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.annotation.LoginAuth;
import project.board.domain.dto.Page;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Article;
import project.board.entity.dto.ArticleDetail;
import project.board.entity.dto.ArticleDto2;
import project.board.entity.dto.ArticleForm;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.service.ArticleServiceJpa;
import project.board.util.MySessionUtils;

@Controller
@RequiredArgsConstructor
public class ArticleControllerJpa {
	private final ArticleServiceJpa articleService;
	private final MySessionUtils sessionUtils;

	private static final String WRITE_AND_UPDATE_FORM = "article/write_and_update";
	private static final String ARTICLE_LIST = "article/list";
	private static final String ARTICLE_DETAIL = "article/detail";
	
	@GetMapping("/{category}/{nation}")
	public String getBoard(
			@PathVariable("category") Category category,
			@PathVariable("nation") Nation nation,
			@ModelAttribute("pageAndSort") PageAndSort pageAndSort,
			@ModelAttribute("search") String search,
			Model model)
	{
		if(search!=null) search = search.trim();
		Page<ArticleDto2> page = articleService.find(category, nation, pageAndSort, search);
		model.addAttribute("page", page);
		
		return ARTICLE_LIST;
	}
	
	@GetMapping("/article/write")
	@LoginAuth
	public String getWriteForm(
			HttpSession session,
			@ModelAttribute("article") ArticleForm articleForm,
			Model model)
	{
		return WRITE_AND_UPDATE_FORM;
	}
	
	@GetMapping("/article/{articleId}")
	public String showArticle(
			@PathVariable Long articleId,
			HttpSession session,
			Model model)
	{
		ArticleDetail detail = articleService.getDetail(sessionUtils.getMemberId(session), articleId);
		model.addAttribute("article", detail);
		return ARTICLE_DETAIL;
	}
	
	@GetMapping("/article/update/{articleId}")
	@LoginAuth
	public String getUpdateForm(
			@PathVariable Long articleId,
			HttpSession session,
			Model model)
	{
		Article article = articleService.getMyArticle(sessionUtils.getMemberId(session), articleId);
		model.addAttribute("article", new ArticleForm(article));
		return WRITE_AND_UPDATE_FORM;
	}
	
	@PostMapping("/article/write")
	@LoginAuth
	public String processWriteForm(
			@ModelAttribute("article") @Valid ArticleForm article,
			BindingResult result,
			HttpSession session,
			Model model)
	{
		if(result.hasErrors()) return WRITE_AND_UPDATE_FORM;
		Long articleId = null;
		//게시물 저장
		if(article.getArticleId() != null) {
			articleId = articleService.tempToPost(sessionUtils.getMemberId(session), article);
		}
		else {
			articleId = articleService.save(sessionUtils.getMemberId(session), article);
		}

		//이미지 디스크 복사
		articleService.uploadToPost(article.getImages());
		
		return "redirect:/article/" + articleId;
	}
	
	@PostMapping("/article/update/{articleId}")
	@LoginAuth
	public String processUpdateForm(
			@PathVariable Long articleId,
			@ModelAttribute("article") @Valid ArticleForm article,
			BindingResult result,
			HttpSession session,
			Model model)
	{
		if(result.hasErrors()) return WRITE_AND_UPDATE_FORM;
		
		//이미지 저장
		articleService.uploadToPost(article.getImages());
		
		//게시물 수정
		articleService.update(sessionUtils.getMemberId(session), article);
		
		return "redirect:/article/" + articleId;
	}
	
	@RequestMapping("/article/delete/{articleId}")
	@LoginAuth
	public String deleteArticle(
			@PathVariable("articleId") Long articleId,
			HttpSession session)
	{
		articleService.remove(sessionUtils.getMemberId(session), articleId);
		return "redirect:/";
	}
	

}
