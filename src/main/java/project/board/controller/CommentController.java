package project.board.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.board.annotation.LoginAuth;
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
	
	
}
