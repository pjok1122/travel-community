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
import project.board.util.ScriptEscapeUtils;

@Controller
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private ScriptEscapeUtils scriptUtils;
	
	@Autowired
	private CommonUtils utils;
	
	@PostMapping("")
	@AjaxLoginAuth
	public ResponseEntity<?> processCommentCreate
	(
		@RequestParam Long articleId,
		@RequestParam String content, 
		@RequestParam Long parentCommentId, 
		HttpSession session
	) throws Exception
	{
		
		commentService.create(articleId, utils.memberIdConvertThrow(session), parentCommentId, scriptUtils.scriptEscape(content));
		
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
	@AjaxLoginAuth
	public ResponseEntity<?> deleteComment
	(
		@PathVariable Long commentId, 
		HttpSession session
	) 
	{
		commentService.delete(commentId, utils.memberIdConvert(session));
		
		return ResponseEntity.ok().build(); 
	}
	
	@PostMapping("/like")
	@AjaxLoginAuth
	public ResponseEntity<?> processLikeComment
	(
		@RequestParam("commentId") Long commentId,
		HttpSession session
	)
	{
		int likeCount = commentService.like(utils.memberIdConvert(session), commentId);
		return ResponseEntity.ok().body(likeCount);
	}
}
