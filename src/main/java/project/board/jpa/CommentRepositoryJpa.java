package project.board.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Comment;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    Long countByArticle(Article article);
}
