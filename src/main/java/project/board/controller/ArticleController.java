package project.board.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import project.board.annotation.AjaxLoginAuth;
import project.board.annotation.LoginAuth;
import project.board.annotation.isArticleOwner;
import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.domain.dto.PageAndSort;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.service.ArticleService;
import project.board.service.MemberService;
import project.board.util.MySessionUtils;

@Controller
public class ArticleController {
	
	@Autowired
	ArticleService articleService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MySessionUtils sessionUtils;
	
	
	private static final String WRITE_AND_UPDATE_FORM = "article/write_and_update";
	
	@GetMapping("/{category}/{nation}")
	public String getBoardByCategoryAndNation(
			@PathVariable("category") Category category,
			@PathVariable("nation") Nation nation,
			@ModelAttribute("pageAndSort") PageAndSort pageAndSort, 
			@ModelAttribute("search") String search,
			Model model)
	{
		if(search!=null) {
			search = search.trim();
		}
		
		model.addAllAttributes(articleService.getArticleList(category, nation, pageAndSort, search));
		return "article/list";
	}
	
	@GetMapping("/article/write")
	@LoginAuth
	public String getCreationForm(
			HttpSession session,
			@RequestParam(value = "id", required = false) Long articleId,
			Model model)
	{
		model.addAttribute("article", articleService.loadTempArticleById(sessionUtils.getMemberId(session), articleId));
		return WRITE_AND_UPDATE_FORM;
	}
	
	@PostMapping("/article/write")
	@LoginAuth
	public String processCreationForm(
			@RequestParam(value = "images", required = false) String images,
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session,
			Model model)
	{
		if(result.hasErrors()) {
			model.addAttribute("article", article);
			return WRITE_AND_UPDATE_FORM;
		}
		Long articleId = articleService.createArticle(article, images, sessionUtils.getMemberId(session));
		return "redirect:/article/" + articleId;
	}
		
	@GetMapping("/article/{articleId}")
	public String showArticle(
			@PathVariable Long articleId,
			HttpSession session,
			Model model)
	{
		Map<String, Object> articleMap = articleService.getArticleDetailById(sessionUtils.getMemberId(session), articleId);
		if(articleMap == null) return "redirect:/";
		model.addAllAttributes(articleMap);
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
		ArticleDto article = articleService.getUpdateForm(sessionUtils.getMemberId(session), articleId);
		if(article==null) return "redirect:/";
		model.addAttribute("article", article);
		return WRITE_AND_UPDATE_FORM;
	}
	
	@PostMapping("/article/update/{articleId}")
	@LoginAuth
	@isArticleOwner
	public String processUpdateArticleForm(
			@PathVariable Long articleId,
			@RequestParam(value = "images", required = false) String images,
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session)
	{
		if(result.hasErrors()) {
			return WRITE_AND_UPDATE_FORM;
		}
		articleService.modifyArticle(articleId, article, images);
		return "redirect:/article/" + articleId;
	}
	
	@RequestMapping("/article/delete/{articleId}")
	@LoginAuth
	@isArticleOwner
	public String deleteArticle(
			@PathVariable("articleId") Long articleId,
			HttpSession session)
	{	
		articleService.removeArticleById(articleId);
		return "redirect:/";
	}
	
	@PostMapping("/article/like")
	@AjaxLoginAuth
	public ResponseEntity<?> processLikeArticle(
			@RequestParam("articleId") Long articleId,
			HttpSession session,
			HttpServletRequest request)
	{
		int likeStatus = articleService.modifyLikeStatus(sessionUtils.getMemberId(session), articleId);
		return ResponseEntity.ok().body(likeStatus);
	}
	
	@GetMapping("/article/like")
	@ResponseBody
	public Integer getLikeCount(@RequestParam("articleId") Long articleId) {
		return articleService.getLikeCount(articleId);
	}
	
	@PostMapping("/ajax/temp/write")
	@AjaxLoginAuth
	public ResponseEntity<?> processTempArticleWirte(
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session)
	{
		Long memberId = sessionUtils.getMemberId(session);

		if(result.hasErrors() || !articleService.checkTempArticleWritable(memberId)) {
			return ResponseEntity.badRequest().build();
		}
		
		Long id = articleService.createTempArticle(article, memberId);
		return ResponseEntity.ok().body(id);
	}
	
	@PostMapping("/ajax/temp/update")
	@AjaxLoginAuth
	public ResponseEntity<?> processTempArticleUpdate(
			@RequestParam("articleId") Long articleId,
			@RequestParam(value = "images", required = false) String images,
			@ModelAttribute @Valid Article article,
			BindingResult result,
			HttpSession session)
	{
		if(result.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}
		
		ArticleDto oldArticle = articleService.getUpdateForm(sessionUtils.getMemberId(session), articleId);
		
		//글의 주인이 아니거나 TEMP 글이 아니라면, 잘못된 요청.
		if(oldArticle == null || !oldArticle.getStatus().equals("TEMP")) {
			return ResponseEntity.badRequest().build();
		}
		
		articleService.modifyArticle(articleId, article, images);
		return ResponseEntity.ok().body(articleId);
	}
	
	@GetMapping("/ajax/temp")
	@AjaxLoginAuth
	public ResponseEntity<?> getTempArticleList(HttpSession session){
		Long memberId = sessionUtils.getMemberId(session);
		
		List<ArticleDto> tempArticles = articleService.getTempArticleByMemberId(memberId, new Page(1));
		return ResponseEntity.ok().body(tempArticles);
	}
	
	@PostMapping("/ajax/temp/delete")
	@AjaxLoginAuth
	public ResponseEntity<?> deleteTempArticle(
			HttpSession session,
			@RequestParam("articleId") Long articleId
			)
	{
		ArticleDto article = articleService.getUpdateForm(sessionUtils.getMemberId(session), articleId);
		
		if(article == null || !articleService.checkStatusTemp(article)) {
			return ResponseEntity.badRequest().build();
		}
		
		articleService.removeArticleById(articleId);
		return ResponseEntity.ok().build();
	}
}
