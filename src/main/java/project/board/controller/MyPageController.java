package project.board.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import project.board.annotation.LoginAuth;
import project.board.domain.dto.CustomPage;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.Member;
import project.board.jpa.MemberRepositoryJpa;
import project.board.service.ArticleServiceV2;
import project.board.service.BookmarkServiceV2;
import project.board.service.CommentServiceV2;
import project.board.service.MemberServiceV2;
import project.board.util.SessionManager;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
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
    private final SessionManager sessionManager;

    @GetMapping("/member")
    @LoginAuth
    public String myInfo(HttpSession session, Model model) {
        Long memberId = sessionManager.getMemberId(session);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());
        Long goodCount = memberRepository.countGoodById(memberId);

        model.addAttribute("member", member);
        model.addAttribute("like", goodCount);

        return MY_PAGE_MEMBER;
    }

    @GetMapping("/article")
    @LoginAuth
    public String myArticle(@RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "size", defaultValue = "10") int size,
                            HttpSession session, Model model) {
        Long memberId = sessionManager.getMemberId(session);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles = articleService.getArticles(member, "PERMANENT", page - 1, size)
                                                             .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());
        model.addAttribute("page", result);
        return MY_PAGE_ARTICLE;
    }

    @GetMapping("/temp-article")
    @LoginAuth
    public String myTempArticle(@RequestParam(value = "page", defaultValue = "1") int page,
                                @RequestParam(value = "size", defaultValue = "10") int size,
                                HttpSession session, Model model) {

        Long memberId = sessionManager.getMemberId(session);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles = articleService.getArticles(member, "TEMP", page - 1, size)
                                                             .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());
        model.addAttribute("page", result);
        return MY_PAGE_TEMP_ARTICLE;
    }

    @GetMapping("/bookmark")
    @LoginAuth
    public String myBookmark(@RequestParam(value = "page", defaultValue = "1") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             HttpSession session, Model model) {
        Long memberId = sessionManager.getMemberId(session);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyArticleListResponse> articles = bookmarkService.getArticleByMember(member, page - 1, size)
                                                              .map(MyArticleListResponse::of);

        CustomPage<MyArticleListResponse> result = new CustomPage<>(page, (int) articles.getTotalElements(),
                                                                    articles.getContent());

        model.addAttribute("page", result);
        return MY_PAGE_BOOKMARK;
    }

    @GetMapping("/comment")
    @LoginAuth
    public String comment(@RequestParam(value = "page", defaultValue = "1") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size,
                          HttpSession session, Model model) {
        Long memberId = sessionManager.getMemberId(session);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException());

        Page<MyCommentListResponse> comments = commentService.getCommentsByMember(member, page - 1, size)
                                                             .map(MyCommentListResponse::of);

        CustomPage<MyCommentListResponse> result = new CustomPage<>(page, (int) comments.getTotalElements(),
                                                                    comments.getContent());

        model.addAttribute("page", result);
        return MY_PAGE_COMMENT;
    }

    @GetMapping("/auth")
    @LoginAuth
    public String authForm(HttpSession session, Model model) {
        model.addAttribute("identity", new IdentityVerificationRequest(sessionManager.getMemberEmail(session)));
        return IDENTITY_AUTHORIZATION;
    }

    @PostMapping("/auth")
    @LoginAuth
    public String verifyIdentity(@ModelAttribute("identity") @Valid IdentityVerificationRequest request,
                                 BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return IDENTITY_AUTHORIZATION;
        }

        Member member = memberRepository.findById(sessionManager.getMemberId(session))
                                        .orElseThrow(() -> new IllegalArgumentException());
        if (!memberService.passwordCompare(request.getPassword(), member)) {
            result.rejectValue("rePassword", "dismatch your info", "비밀번호가 일치하지 않습니다.");
            return IDENTITY_AUTHORIZATION;
        }

        sessionManager.setVerified(session, true);
        String prevPage = sessionManager.popPreviousPage(session);
        return "redirect:" + prevPage;
    }

    @GetMapping("/update")
    @LoginAuth
    public String update(HttpServletRequest request, HttpSession session, Model model) {
        sessionManager.setPreviousPage(session, request.getRequestURI());

        MyInfoUpdateRequest updateRequest = new MyInfoUpdateRequest();
        updateRequest.setEmail(sessionManager.getMemberEmail(session));
        model.addAttribute("updateForm", updateRequest);

        if (!sessionManager.isVerified(session)) {
            return "redirect:/mypage/auth";
        }

        return MY_PAGE_UPDATE_INFO;
    }

    @PostMapping("/update")
    @LoginAuth
    public String update(@ModelAttribute("updateForm") @Valid MyInfoUpdateRequest request,
                         BindingResult errors, HttpSession session) {
        if (!sessionManager.isVerified(session)) {
            return IDENTITY_AUTHORIZATION;
        }

        request.verifyPassword(errors);
        if (errors.hasErrors()) {
            return MY_PAGE_UPDATE_INFO;
        }

        memberService.update(request.getEmail(), request.getPassword());
        sessionManager.setVerified(session, false);
        return "redirect:/mypage/member";
    }

    @RequestMapping("/delete")
    @LoginAuth
    public String delete(HttpServletRequest request, HttpSession session, Model model) {
        if (!sessionManager.isVerified(session)) {
            sessionManager.setPreviousPage(session, request.getRequestURI());
            model.addAttribute("identity",
                               new IdentityVerificationRequest(sessionManager.getMemberEmail(session)));
            return IDENTITY_AUTHORIZATION;
        }

        memberService.delete(sessionManager.getMemberId(session));
        session.invalidate();
        return "redirect:/";
    }

    @Data
    static class MyArticleListResponse {
        private Long id;
        private String title;
        private String nation;
        private long hit = 0;
        private long commentCnt = 0;
        private LocalDateTime registerDate;

        static MyArticleListResponse of(Article article) {
            MyArticleListResponse response = new MyArticleListResponse();
            response.id = article.getId();
            response.title = article.getTitle();
            response.hit = article.getHit();
            response.nation = article.getNation();  //TODO : change enum
            response.commentCnt = 0;        //TODO : article에 commentCount 필드 추가
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
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{12,20}",
                message = "비밀번호는 영문 대,소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 12자 ~ 20자의 비밀번호여야 합니다.")
        private String password;
        private String rePassword;

        public void verifyPassword(Errors errors) {
            if (!StringUtils.equals(password, rePassword)) {
                errors.rejectValue("rePassword", "dismatch your info", "비밀번호가 일치하지 않습니다.");
            }
        }
    }
}
