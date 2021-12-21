package project.board.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.annotation.AjaxLoginAuth;
import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.service.ArticleService;
import project.board.util.SessionManager;

@RestController
@RequiredArgsConstructor
public class ArticleRestController {

    private final ArticleService articleService;
    private final SessionManager sessionManager;

    @PostMapping("/article/like")
    @AjaxLoginAuth
    public ResponseEntity<?> like(@RequestParam("articleId") Long articleId, HttpSession session,
                                  HttpServletRequest request) {
        int likeStatus = articleService.modifyLikeStatus(sessionManager.getMemberId(session), articleId);
        return ResponseEntity.ok().body(likeStatus);
    }

    @GetMapping("/article/like")
    public Integer likeCount(@RequestParam("articleId") Long articleId) {
        return articleService.getLikeCount(articleId);
    }

    @PostMapping("/ajax/temp/write")
    @AjaxLoginAuth
    public ResponseEntity<?> writeTempArticle(@ModelAttribute @Valid Article article,
                                              BindingResult result,
                                              HttpSession session) {
        Long memberId = sessionManager.getMemberId(session);

        if (result.hasErrors() || !articleService.checkTempArticleWritable(memberId)) {
            return ResponseEntity.badRequest().build();
        }

        Long id = articleService.createTempArticle(article, memberId);
        return ResponseEntity.ok().body(id);
    }

    @PostMapping("/ajax/temp/update")
    @AjaxLoginAuth
    public ResponseEntity<?> updateTempArticle(@RequestParam("articleId") Long articleId,
                                               @RequestParam(value = "images", required = false) String images,
                                               @ModelAttribute @Valid Article article,
                                               BindingResult result,
                                               HttpSession session) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        ArticleDto oldArticle = articleService.getUpdateForm(sessionManager.getMemberId(session), articleId);

        //글의 주인이 아니거나 TEMP 글이 아니라면, 잘못된 요청.
        if (oldArticle == null || !StringUtils.equals(oldArticle.getStatus(), "TEMP")) {
            return ResponseEntity.badRequest().build();
        }

        articleService.modifyArticle(articleId, article, images);
        return ResponseEntity.ok().body(articleId);
    }

    @GetMapping("/ajax/temp")
    @AjaxLoginAuth
    public ResponseEntity<?> getTempArticleList(HttpSession session) {
        Long memberId = sessionManager.getMemberId(session);

        List<ArticleDto> tempArticles = articleService.getTempArticleByMemberId(memberId, new Page(1));
        return ResponseEntity.ok().body(tempArticles);
    }

    @PostMapping("/ajax/temp/delete")
    @AjaxLoginAuth
    public ResponseEntity<?> deleteTempArticle(HttpSession session, @RequestParam("articleId") Long articleId) {
        ArticleDto article = articleService.getUpdateForm(sessionManager.getMemberId(session), articleId);

        if (article == null || !articleService.checkStatusTemp(article)) {
            return ResponseEntity.badRequest().build();
        }

        articleService.removeArticleById(articleId);
        return ResponseEntity.ok().build();
    }
}
