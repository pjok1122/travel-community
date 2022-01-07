package project.board.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;

import java.util.Optional;

public interface BookmarkRepositoryJpa extends JpaRepository<Bookmark, Long> {

    @EntityGraph(attributePaths = {"article"})
    Page<Bookmark> findByMember(Member member, Pageable pageable);

    Optional<Bookmark> findByMemberAndArticle(Member member, Article article);

    boolean existsByMemberAndArticle(Member member, Article article);
}
