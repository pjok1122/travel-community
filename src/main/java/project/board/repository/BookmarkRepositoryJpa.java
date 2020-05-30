package project.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;

public interface BookmarkRepositoryJpa extends JpaRepository<Bookmark, Long>{

	boolean existsByMemberAndArticle(Member member, Article article);

	Optional<Bookmark> findByMemberAndArticle(Member member, Article article);

	@EntityGraph(attributePaths = {"article"})
	Page<Bookmark> findByMember(Member member, Pageable pageable);

}