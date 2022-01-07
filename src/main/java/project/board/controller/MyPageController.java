package project.board.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import project.board.annotation.Authorization;
import project.board.annotation.validation.PasswordPattern;
import project.board.common.SessionContext;
import project.board.domain.dto.CustomPage;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.Member;
import project.board.enums.ArticleStatus;
import project.board.enums.Nation;
import project.board.jpa.MemberRepositoryJpa;
import project.board.service.ArticleServiceV2;
import project.board.service.BookmarkServiceV2;
import project.board.service.CommentServiceV2;
import project.board.service.MemberServiceV2;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
@Authorization
public class MyPageController {

    private static final String MY_PAGE_MEMBER = "member/mypage/member";
    private static final String MY_PAGE_ARTICLE = "member/mypage/article";
    private static final String MY_PAGE_TEMP_ARTICLE = "member/mypage/temp_article";
    private static final String MY_PAGE_BOOKMARK = "member/mypage/bookmark";
    private static final String MY_PAGE_COMMENT = "member/mypage/comment";
    private static final String IDENTITY_AUTHORIZATION = "member/mypage/auth";
    private static final String MY_PAGE_UPDATE_INFO = "member/mypage/update";

    private final ArticleServiceV2 articleService;
    private final BookmarkServiceV2 bookmarkService;
    private final CommentServiceV2 commentService;
    private final MemberServiceV2 memberService;
    private final MemberRepositoryJpa memberRepository;

    @GetMapping("/member")
    public String myInfo(@RequestAttribute SessionContext session, Model model) {
        Long memberId = session.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());
        Long goodCount = memberRepository.countGoodById(memberId);

        model.addAttribute("member", member);
        model.addAttribute("like", goodCount != null ? goodCount : 0L);

        return MY_PAGE_MEMBER;
    }

    @GetMapping("/article")
    public String myArticle(@RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            @RequestAttribute SessionContext session, Model model) {
        Long memberId = session.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles =
                articleService.getArticlesByMember(member, ArticleStatus.PERMANENT, page - 1, size)
                              .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());
        model.addAttribute("page", result);
        return MY_PAGE_ARTICLE;
    }

    @GetMapping("/temp-article")
    public String myTempArticle(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                @RequestAttribute SessionContext session,
                                Model model) {

        Long memberId = session.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles =
                articleService.getArticlesByMember(member, ArticleStatus.TEMP, page - 1, size)
                              .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());
        model.addAttribute("page", result);
        return MY_PAGE_TEMP_ARTICLE;
    }

    @GetMapping("/bookmark")
    public String myBookmark(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             @RequestAttribute SessionContext session, Model model) {
        Long memberId = session.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles = bookmarkService.getArticleByMember(member, page - 1, size)
                                                              .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());

        model.addAttribute("page", result);
        return MY_PAGE_BOOKMARK;
    }

    @GetMapping("/comment")
    public String comment(@RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size,
                          @RequestAttribute SessionContext session, Model model) {
        Long memberId = session.getId();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyCommentListResponse> comments = commentService.getCommentsByMember(member, page - 1, size)
                                                             .map(MyCommentListResponse::of);

        CustomPage<MyCommentListResponse> result = new CustomPage<>(page, (int) comments.getTotalElements(),
                                                                    comments.getContent());

        model.addAttribute("page", result);
        return MY_PAGE_COMMENT;
    }

    @GetMapping("/auth")
    public String authForm(@RequestAttribute SessionContext session, Model model) {
        model.addAttribute("identity", new IdentityVerificationRequest(session.getEmail()));
        return IDENTITY_AUTHORIZATION;
    }

    @PostMapping("/auth")
    public String verifyIdentity(@ModelAttribute("identity") @Valid IdentityVerificationRequest request,
                                 BindingResult result, @RequestAttribute SessionContext session) {
        if (result.hasErrors()) {
            return IDENTITY_AUTHORIZATION;
        }

        Member member = memberRepository.findById(session.getId())
                                        .orElseThrow(() -> new IllegalArgumentException());
        if (!member.verifyPassword(request.getPassword())) {
            result.rejectValue("rePassword", "mismatch your info", "비밀번호가 일치하지 않습니다.");
            return IDENTITY_AUTHORIZATION;
        }

        session.setVerified(true);
        String prevPage = session.popPreviousPage();
        return "redirect:" + prevPage;
    }

    @GetMapping("/update")
    public String update(HttpServletRequest request,
                         @RequestAttribute SessionContext session, Model model) {
        session.setPreviousPage(request.getRequestURI());

        MyInfoUpdateRequest updateRequest = new MyInfoUpdateRequest();
        updateRequest.setEmail(session.getEmail());
        model.addAttribute("updateForm", updateRequest);

        if (session.isVerified()) {
            return "redirect:/mypage/auth";
        }

        return MY_PAGE_UPDATE_INFO;
    }

    @PostMapping("/update")
    public String update(@RequestAttribute SessionContext session,
                         @ModelAttribute("updateForm") @Valid MyInfoUpdateRequest request,
                         BindingResult errors) {
        if (session.isVerified()) {
            return IDENTITY_AUTHORIZATION;
        }

        request.verifyPassword(errors);
        if (errors.hasErrors()) {
            return MY_PAGE_UPDATE_INFO;
        }

        memberService.update(request.getEmail(), request.getPassword());
        session.setVerified(false);
        return "redirect:/mypage/member";
    }

    @RequestMapping("/delete")
    public String delete(HttpServletRequest request, @RequestAttribute SessionContext session, Model model) {
        if (!session.isVerified()) {
            session.setPreviousPage(request.getRequestURI());
            model.addAttribute("identity", new IdentityVerificationRequest(session.getEmail()));
            return IDENTITY_AUTHORIZATION;
        }

        memberService.delete(session.getId());
        session.invalidate();
        return "redirect:/";
    }

    @Data
    static class MyArticleListResponse {
        private Long id;
        private String title;
        private Nation nation;
        private long hit = 0;
        private long commentCnt = 0;
        private LocalDateTime registerDate;

        static MyArticleListResponse of(Article article) {
            MyArticleListResponse response = new MyArticleListResponse();
            response.id = article.getId();
            response.title = article.getTitle();
            response.hit = article.getHit();
            response.nation = article.getNation();
            response.commentCnt = article.getCommentCount();
            response.registerDate = article.getRegisterDate();

            return response;
        }
    }

    @Data
    static class MyCommentListResponse {
        private Long id;
        private String content;
        private int good;
        private LocalDateTime registerDate;
        private Long articleId;

        static MyCommentListResponse of(Comment comment) {
            MyCommentListResponse response = new MyCommentListResponse();
            response.id = comment.getId();
            response.content = comment.getContent();
            response.good = comment.getGood();
            response.registerDate = comment.getRegisterDate();
            response.articleId = comment.getArticle().getId();
            return response;
        }
    }

    @Data
    @NoArgsConstructor
    static class IdentityVerificationRequest {
        @Email
        private String email;
        private String password;

        IdentityVerificationRequest(String email) {
            this.email = email;
        }
    }

    @Data
    static class MyInfoUpdateRequest {
        @Email
        private String email;
        @NotBlank
        @PasswordPattern
        private String password;
        private String rePassword;

        public void verifyPassword(Errors errors) {
            if (!StringUtils.equals(password, rePassword)) {
                errors.rejectValue("rePassword", "mismatch your info", "비밀번호가 일치하지 않습니다.");
            }
        }
    }
}
