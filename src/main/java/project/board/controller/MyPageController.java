package project.board.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
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

    private final ArticleServiceV2 articleService;
    private final BookmarkServiceV2 bookmarkService;
    private final CommentServiceV2 commentService;
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


    @Data
    public static class MyArticleListResponse {
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
    public static class MyCommentListResponse {
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
}
