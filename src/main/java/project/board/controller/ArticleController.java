package project.board.controller;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;
import project.board.annotation.LoginAuth;
import project.board.annotation.isArticleOwner;
import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.PageAndSort;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.service.ArticleService;
import project.board.util.SessionManager;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final SessionManager sessionManager;

    private static final String WRITE_AND_UPDATE_FORM = "article/write_and_update";

    @GetMapping("/{category}/{nation}")
    public String articles(@PathVariable("category") Category category,
                           @PathVariable("nation") Nation nation,
                           @ModelAttribute("pageAndSort") PageAndSort pageAndSort,
                           @ModelAttribute("search") String search, Model model) {
        if (!StringUtils.isEmptyOrWhitespace(search)) {
            search = search.trim();
        }

        model.addAllAttributes(articleService.getArticleList(category, nation, pageAndSort, search));
        return "article/list";
    }

    @GetMapping("/article/write")
    @LoginAuth
    public String creationForm(@RequestParam(value = "id", required = false) Long articleId,
                               HttpSession session, Model model) {
        Article article = articleService.loadTempArticleById(sessionManager.getMemberId(session), articleId);
        model.addAttribute("article", article);
        return WRITE_AND_UPDATE_FORM;
    }

    @PostMapping("/article/write")
    @LoginAuth
    public String create(@RequestParam(value = "images", required = false) String images,
                         @ModelAttribute @Valid Article article,
                         BindingResult result,
                         HttpSession session,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("article", article);
            return WRITE_AND_UPDATE_FORM;
        }
        Long articleId = articleService.createArticle(article, images, sessionManager.getMemberId(session));
        return "redirect:/article/" + articleId;
    }

    @GetMapping("/article/{articleId}")
    public String detail(@PathVariable Long articleId, HttpSession session, Model model) {
        Map<String, Object> articleMap = articleService.getArticleDetailById(
                sessionManager.getMemberId(session), articleId);
        if (articleMap == null) {
            return "redirect:/";
        }
        model.addAllAttributes(articleMap);
        session.setAttribute("prevPage", "/article/" + articleId);
        return "article/detail";
    }

    @GetMapping("/article/update/{articleId}")
    @LoginAuth
    public String updateForm(@PathVariable Long articleId, HttpSession session, Model model) {
        ArticleDto article = articleService.getUpdateForm(sessionManager.getMemberId(session), articleId);
        if (article == null) { return "redirect:/"; }
        model.addAttribute("article", article);
        return WRITE_AND_UPDATE_FORM;
    }

    @PostMapping("/article/update/{articleId}")
    @LoginAuth
    @isArticleOwner
    public String update(@PathVariable Long articleId,
                         @RequestParam(value = "images", required = false) String images,
                         @ModelAttribute @Valid Article article,
                         HttpSession session,     // do not remove.
                         BindingResult result) {
        if (result.hasErrors()) {
            return WRITE_AND_UPDATE_FORM;
        }
        articleService.modifyArticle(articleId, article, images);
        return "redirect:/article/" + articleId;
    }

    @RequestMapping("/article/delete/{articleId}")
    @LoginAuth
    @isArticleOwner
    public String delete(@PathVariable("articleId") Long articleId, HttpSession session) {
        articleService.removeArticleById(articleId);
        return "redirect:/";
    }

}
