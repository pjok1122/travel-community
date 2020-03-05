package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.AjaxLoginAuth;
import project.board.domain.Comment;
import project.board.domain.dto.CommentDto;
import project.board.service.CommentService;
import project.board.util.CommonUtils;

@Controller
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CommonUtils utils;
	
	@PostMapping("")
	public ResponseEntity<?> create
	(
		@RequestParam Long articleId,
		@RequestParam String content, 
		@RequestParam Long parentCommentId, 
		HttpSession session
	)
	{
		commentService.create(articleId, utils.memberIdConvert(session), parentCommentId, content);
		
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("")
	public ResponseEntity<?> getCommentByArticleId
	(
		@RequestParam Long articleId, 
		HttpSession session
	)
	{
		List<CommentDto> comments = commentService.getByArticleId(utils.memberIdConvert(session), articleId);
		
		return ResponseEntity.ok().body(comments);
	}
	
	@PostMapping("/delete/{commentId}")
	public ResponseEntity<?> delete
	(
		@PathVariable Long commentId, 
		HttpSession session
	)
	{
		commentService.delete(commentId, utils.memberIdConvert(session));
		
		return ResponseEntity.ok().build(); 
	}
	
	@PostMapping("/like")
	//@AjaxLoginAuth
	public ResponseEntity<?> processLikeArticle
	(
		@RequestParam("commentId") Long commentId,
		HttpSession session
	)
	{
		Boolean like = commentService.like(utils.memberIdConvert(session), commentId);
		return ResponseEntity.ok().body(like);
	}
}
