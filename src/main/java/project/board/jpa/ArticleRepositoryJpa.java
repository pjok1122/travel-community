package project.board.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Member;
import project.board.enums.ArticleStatus;

public interface ArticleRepositoryJpa extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

    Page<Article> findByMemberAndStatus(Member member, ArticleStatus status, Pageable pageable);

    Long countAllByMemberAndStatus(Member member, String status);

    @EntityGraph(attributePaths = {"member", "postFiles"})
    Optional<Article> findDetailById(Long articleId);
}
