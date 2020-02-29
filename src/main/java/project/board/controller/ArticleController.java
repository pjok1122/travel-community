package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.validator.constraints.ISBN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import project.board.annotation.LoginAuth;
import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.enums.Sort;
import project.board.repository.ArticleRepository;
import project.board.service.ArticleService;

@Controller
public class ArticleController {
	
	@Autowired
	ArticleService articleService;
	
	@GetMapping("/{category}/{nation}")
	public String getBoardByCategoryAndNation(
			@PathVariable("category") Category category,
			@PathVariable("nation") Nation nation,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "sort", defaultValue = "NEWEST") Sort sort, 
			@RequestParam(required = false, value = "search") String search,
			Model model)
	{
		model.addAllAttributes(articleService.getArticleList(category, nation, page, sort));
		model.addAttribute("category", category.toString().toLowerCase());
		return "article/list";
	}
	
	@GetMapping("/{category}/write")
	@LoginAuth
	public String getCreationForm(
			@PathVariable(value = "category") Category category,
			HttpSession session,
			Model model)
	{
		return "article/write";
	}
	
	@PostMapping("/{category}/write")
	@LoginAuth
	public String processCreationForm(
			@PathVariable Category category,
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session,
			Model model)
	{
		if(result.hasErrors()) {
			model.addAttribute("error", "NOT EMPTY");
			return "article/write";
		}
		
		Long articleId = articleService.createArticle(article, category, (Long) session.getAttribute("memberId"));
		return "redirect:/article/" + articleId;
	}
		
	@GetMapping("/article/{articleId}")
	public String showArticle(
			@PathVariable Long articleId,
			HttpSession session,
			Model model)
	{
		ArticleDto article = articleService.getArticleById(articleId);
		if(article.getStatus().equals("TEMP")) {
			return "redirect:/";
		}
		articleService.increaseHitById(articleId);
		model.addAttribute("article", article);
		model.addAllAttributes(articleService.checkArticleSatus((Long)session.getAttribute("memberId"), articleId));
		session.setAttribute("prevPage", "/article/" + articleId);
		return "article/detail";
	}
	
	@GetMapping("/article/update/{articleId}")
	@LoginAuth
	public String getUpdateArticleForm(
			@PathVariable Long articleId,
			HttpSession session,
			Model model)
	{
		ArticleDto article = articleService.getArticleById(articleId);
		if(!article.getMemberId().equals(session.getAttribute("memberId"))) {
			return "redirect:/";
		}
		
		model.addAttribute("article", article);
		return "article/update";
	}
	
	@PostMapping("/article/update/{articleId}")
	@LoginAuth
	public String processUpdateArticleForm(
			@PathVariable Long articleId,
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session)
	{
		if(result.hasErrors()) {
			return "article/update";
		}
		ArticleDto oldArticle = articleService.getArticleById(articleId);
		if(!oldArticle.getMemberId().equals(session.getAttribute("memberId"))) {
			return "redirect:/";
		}
		
		articleService.modifyArticle(articleId, article);
		return "redirect:/article/" + articleId;
	}
	
	@RequestMapping("/article/delete/{articleId}")
	@LoginAuth
	public String deleteArticle(
			@PathVariable("articleId") Long articleId,
			HttpSession session)
	{
		ArticleDto article = articleService.getArticleById(articleId);
		if(!article.getMemberId().equals(session.getAttribute("memberId"))) {
			return "redirect:/";
		}
		
		articleService.removeArticleById(articleId);
		return "redirect:/";
	}
	
	@PostMapping("/article/like")
	public ResponseEntity<?> processLikeArticle(
			@RequestParam("articleId") Long articleId,
			HttpSession session,
			HttpServletRequest request){
		
		if(session.getAttribute("memberId") ==null) {
			return ResponseEntity.status(302).body("/login");
		}
		
		int likeStatus = articleService.modifyLikeStatus((Long) session.getAttribute("memberId"), articleId);
		return ResponseEntity.ok().body(likeStatus);
	}
	
	@GetMapping("/article/like")
	@ResponseBody
	public Integer getLikeCount(@RequestParam("articleId") Long articleId) {
		return articleService.getLikeCount(articleId);
	}
	
	@PostMapping("/ajax/temp/write")
	public ResponseEntity<?> processTempArticleWirte(){
		return null;
	}
}
