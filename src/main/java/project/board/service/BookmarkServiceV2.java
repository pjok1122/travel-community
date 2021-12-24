package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.BookmarkRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;
import project.board.repository.MemberRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookmarkServiceV2 {

    private final BookmarkRepositoryJpa bookmarkRepository;
    private final MemberRepositoryJpa memberRepository;
    private final ArticleRepositoryJpa articleRepository;

    public Page<Article> getArticleByMember(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookmarkRepository.findByMember(member, pageable).map(b -> b.getArticle());
    }

    @Transactional(readOnly = false)
    public int toggle(Long memberId, Long articleId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NullPointerException());

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NullPointerException());

        Optional<Bookmark> optionalBookmark = bookmarkRepository.findByMemberAndArticle(member, article);

        if(optionalBookmark.isPresent()) {
            bookmarkRepository.delete(optionalBookmark.get());
            return 0;
        } else {
            bookmarkRepository.save(Bookmark.builder()
                    .article(article)
                    .member(member)
                    .build());
            return 1;
        }
    }
}
