package project.board.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.ArticleLike;
import project.board.entity.Member;

public interface ArticleLikeRepositoryJpa extends JpaRepository<ArticleLike, Long> {

    boolean existsByMemberAndArticle(Member member, Article article);
}
