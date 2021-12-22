package project.board.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Member;

public interface ArticleRepositoryJpa extends JpaRepository<Article, Long> {

    Page<Article> findByMemberAndStatus(Member member, String status, Pageable pageable);

    Long countAllByMemberAndStatus(Member member, String status);
}
