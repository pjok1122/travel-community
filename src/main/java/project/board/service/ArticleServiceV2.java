package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.board.entity.Article;
import project.board.entity.AuditEntity;
import project.board.entity.Member;
import project.board.jpa.ArticleRepositoryJpa;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleServiceV2 {

    private final ArticleRepositoryJpa articleRepository;

    public Page<Article> getArticles(Member member, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "registerDate"));
        return articleRepository.findByMemberAndStatus(member, status, pageable);
    }
}
