package project.board.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.entity.TempArticle;
import project.board.entity.dto.ArticleForm;
import project.board.service.ArticleServiceJpa;
import project.board.service.TempArticleServiceJpa;
import project.board.util.MySessionUtils;

@Controller
@RequiredArgsConstructor
public class TempArticleControllerJpa {
	private final TempArticleServiceJpa tempArticleService;
	private final ArticleServiceJpa articleService;
	private final MySessionUtils sessionUtils;

	/**
	 * 비동기로 임시저장글 요청이 들어왔을 때, 임시저장글 작성이 가능한지 파악한다.
	 * 임시저장글 작성이 가능할 경우 임시저장글을 저장한다.
	 * @param articleForm
	 * @param bindingResult
	 * @param session
	 * @return 실패 시에는 상태코드 400을 전달하며, 성공 시에는 상태코드 200과 게시물 id를 전달한다.
	 */
	@PostMapping("/ajax/v1/temp-article")
	@AjaxLoginAuth
	public ResponseEntity<?> processTempArticleWirte(
			@ModelAttribute("articleForm") @Valid ArticleForm articleForm,
			BindingResult result,
			HttpSession session)
	{
		if(result.hasErrors()) return ResponseEntity.badRequest().build();
		
		//이미지 복사
		articleService.uploadToPost(articleForm.getImages());
		
		//게시물 저장
		Long id = tempArticleService.save(sessionUtils.getMemberId(session), articleForm);
		
		return ResponseEntity.ok().body(id);
	}
	
	/**
	 * 회원의 임시저장글을 전부 조회한다.
	 * @param session
	 * @return 회원의 임시저장 글 목록을 Result 객체에 담아서 전달한다.
	 */
	@GetMapping("/ajax/v1/temp-article")
	@AjaxLoginAuth
	public Result<List<TempArticleDto>> getTempArticles(HttpSession session){
		List<TempArticle> articles = tempArticleService.getMyArticles(sessionUtils.getMemberId(session));
		List<TempArticleDto> dtos = articles.stream().map(TempArticleDto::new).collect(Collectors.toList());
		
		return new Result<>(dtos, dtos.size());
	}
	
	/**
	 * 회원의 임시저장글을 삭제한다.
	 * @param articleId
	 * @param session
	 * @return 상태코드 200
	 */
	@DeleteMapping("/ajax/v1/temp-article/{articleId}")
	@AjaxLoginAuth
	public ResponseEntity<?> deleteTempArticle(
			@PathVariable(value = "articleId") Long articleId,
			HttpSession session
			)
	{
		tempArticleService.remove(sessionUtils.getMemberId(session), articleId);
		return ResponseEntity.ok().build();
	}
	
	/**
	 * 회원의 임시저장글을 수정한다.
	 * @param articleId
	 * @param articleForm
	 * @param bindingResult
	 * @param session
	 * @return 상태코드 200과 articleId를 전달한다.
	 */
	@PutMapping("/ajax/v1/temp-article/{articleId}")
	@AjaxLoginAuth
	public ResponseEntity<?> processTempArticleUpdate(
			@PathVariable("articleId") Long articleId,
			@ModelAttribute("articleForm") @Valid ArticleForm articleForm,
			BindingResult result,
			HttpSession session)
	{
		if(result.hasErrors()) return ResponseEntity.badRequest().build();
		
		Long id = tempArticleService.update(sessionUtils.getMemberId(session), articleForm);
		
		return ResponseEntity.ok(id);
	}
	
	
	
	@AllArgsConstructor
	@Data
	static class Result<T>{
		private T data;
		private int count;
	}
	
	@AllArgsConstructor
	@Data
	static class TempArticleDto{
		private String title;
		private LocalDateTime createdDate;
		private LocalDateTime lastModifiedDate;
		
		public TempArticleDto(TempArticle article) {
			this.title = article.getTitle();
			this.createdDate = article.getCreatedDate();
			this.lastModifiedDate = article.getLastModifiedDate();
		}
	}
}


