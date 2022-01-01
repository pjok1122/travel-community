package project.board.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import project.board.annotation.Authorization;
import project.board.common.SessionContext;
import project.board.domain.dto.CustomPage;
import project.board.domain.dto.PageAndSort;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.enums.ArticleStatus;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.jpa.MemberRepositoryJpa;
import project.board.service.ArticleServiceV2;
import project.board.service.dto.ArticleRegisterParam;

@Controller
@RequiredArgsConstructor
public class ArticleControllerV2 {
    private final ArticleServiceV2 articleService;
    private final MemberRepositoryJpa memberRepository;

    private static final String WRITE_AND_UPDATE_FORM = "article/write_and_update";

    @GetMapping("/{category}/{nation}")
    public String articles(@PathVariable("category") Category category,
                           @PathVariable("nation") Nation nation,
                           @ModelAttribute("pageAndSort") PageAndSort pageAndSort,
                           @ModelAttribute("search") String search, Model model) {
        if (!StringUtils.isEmptyOrWhitespace(search)) {
            search = search.trim();
        }

        Page<ArticleListResponse> articles = articleService.searchArticles(category, nation, pageAndSort, search)
                                                           .map(ArticleListResponse::of);

        CustomPage<ArticleListResponse> customPage = new CustomPage<>(pageAndSort.getPage(),
                                                                      (int) articles.getTotalElements(),
                                                                      articles.getContent());
        model.addAttribute("page", customPage);
        return "article/list";
    }

    @GetMapping("/article/write")
    @Authorization
    public String creationForm(@RequestParam(value = "id", required = false) Long articleId,
                               @RequestAttribute SessionContext session,
                               Model model) {
        Member member = memberRepository.findById(session.getId()).orElseThrow(() -> new IllegalArgumentException());

        if (articleId != null) {
            Article tempArticle = articleService.findMyTempArticle(member, articleId);
            model.addAttribute("article", TempArticleResponse.of(tempArticle));
        } else {
            model.addAttribute("article", new TempArticleResponse());
        }
        return WRITE_AND_UPDATE_FORM;
    }

    @PostMapping("/article/write")
    @Authorization
    public String create(@ModelAttribute @Valid ArticleCreationRequest article,
                         BindingResult errors,
                         @RequestAttribute SessionContext session, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("article", article);
            return WRITE_AND_UPDATE_FORM;
        }

        Long articleId;
        ArticleRegisterParam registerParam = article.convert(session.getId());
        if (article.getId() == null) {
            articleId = articleService.save(registerParam);
        } else {
            articleId = articleService.saveFromTemp(registerParam);
        }

        articleService.diskCopyFromTempStorage(article.getImages());
        return "redirect:/article/" + articleId;
    }


    @Data
    static class ArticleListResponse {
        private Long id;
        private String title;
        private Category category;
        private Nation nation;
        private long hit = 0;
        private long commentCnt = 0;
        private LocalDateTime registerDate;

        static ArticleListResponse of(Article article) {
            ArticleListResponse response = new ArticleListResponse();
            response.id = article.getId();
            response.title = article.getTitle();
            response.hit = article.getHit();
            response.nation = article.getNation();
            response.category = article.getCategory();
            response.commentCnt = article.getCommentCount();
            response.registerDate = article.getRegisterDate();

            return response;
        }
    }

    @Data
    static class TempArticleResponse {
        private Long id;
        private String title;
        private String content;
        private Nation nation;
        private Category category;
        private ArticleStatus status = ArticleStatus.TEMP;

        static TempArticleResponse of(Article article) {
            TempArticleResponse tempArticle = new TempArticleResponse();
            tempArticle.id = article.getId();
            tempArticle.title = article.getTitle();
            tempArticle.content = article.getContent();
            tempArticle.nation = article.getNation();
            tempArticle.category = article.getCategory();
            tempArticle.status = ArticleStatus.TEMP;
            return tempArticle;
        }
    }
    @Data
    static class ArticleCreationRequest {
        private Long id;

        @NotNull
        private Category category;

        @NotBlank
        @Length(max = 50)
        private String title;

        @NotBlank
        @Length(max = 1_000_000)
        private String content;

        @NotNull
        private Nation nation;

        @NotNull
        private ArticleStatus status = ArticleStatus.PERMANENT;

        private int good;
        private int hit;

        private List<String> images;

        public ArticleRegisterParam convert(Long memberId) {
            return ArticleRegisterParam.builder()
                                       .id(id)
                                       .category(category)
                                       .title(title)
                                       .content(content)
                                       .nation(nation)
                                       .status(status)
                                       .images(images.stream()
                                                     .filter(image -> !StringUtils.isEmptyOrWhitespace(image))
                                                     .collect(Collectors.toList()))
                                       .memberId(memberId)
                                       .build();


        }
    }
}
