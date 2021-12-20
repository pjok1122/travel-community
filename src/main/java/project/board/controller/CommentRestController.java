package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.domain.dto.CommentDto;
import project.board.service.CommentService;
import project.board.util.CommonUtils;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final CommonUtils commonUtils; //fixme: change SessionManager with error handling.

    @PostMapping
    @AjaxLoginAuth
    public ResponseEntity<?> create(@RequestParam Long articleId,
                                    @RequestParam String content,
                                    @RequestParam Long parentCommentId,
                                    HttpSession session) throws Exception {
        commentService.create(articleId, commonUtils.memberIdConvertThrow(session), parentCommentId,
                              StringUtils.escapeJavaScript(content));

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> comments(@RequestParam Long articleId, HttpSession session) {
        List<CommentDto> comments = commentService.getByArticleId(commonUtils.memberIdConvert(session),
                                                                  articleId);
        return ResponseEntity.ok().body(comments);
    }

    @PostMapping("/delete/{commentId}")
    @AjaxLoginAuth
    public ResponseEntity<?> delete(@PathVariable Long commentId, HttpSession session) {
        commentService.delete(commentId, commonUtils.memberIdConvert(session));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/like")
    @AjaxLoginAuth
    public ResponseEntity<?> like(@RequestParam("commentId") Long commentId, HttpSession session) {
        int likeCount = commentService.like(commonUtils.memberIdConvert(session), commentId);
        return ResponseEntity.ok().body(likeCount);
    }
}
