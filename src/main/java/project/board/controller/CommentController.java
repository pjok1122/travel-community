package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.domain.Comment;
import project.board.domain.dto.CommentDto;
import project.board.service.CommentService;
import project.board.util.CommonUtils;

@Controller
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CommonUtils utils;
	
	@PostMapping("/comment")
//	@LoginAuth
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
	
	@GetMapping("/comment")
//	@LoginAuth
	public ResponseEntity<?> getCommentByArticleId
	(
		@RequestParam Long articleId, 
		HttpSession session
	)
	{
		List<CommentDto> comments = commentService.getByArticleId(articleId);
		return ResponseEntity.ok().body(comments);
	}
}
