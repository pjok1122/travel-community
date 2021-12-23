package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.Member;
import project.board.jpa.BookmarkRepositoryJpa;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class BookmarkServiceV2 {

    private final BookmarkRepositoryJpa bookmarkRepository;

    public Page<Article> getArticleByMember(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookmarkRepository.findByMember(member, pageable).map(b -> b.getArticle());

    }
}
