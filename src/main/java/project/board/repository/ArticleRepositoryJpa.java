package project.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Member;

public interface ArticleRepositoryJpa extends JpaRepository<Article, Long>, ArticleRepositoryCustom{
	int countByMember(Member member);
	
	@Override
	@EntityGraph(attributePaths = {"member"})
	Optional<Article> findById(Long id);

	@EntityGraph(attributePaths = {"member", "postFiles"})
	Optional<Article> findDetailById(Long articleId);

	Page<Article> findByMember(Member member, Pageable pageable);
}
