package project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import project.board.entity.Article;
import project.board.entity.PostFile;

public interface PostFileRepositoryJpa extends JpaRepository<PostFile, Long>, PostFileRepositoryCustom{

	List<PostFile> findByArticle(Article article);
}
