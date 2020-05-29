package project.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Member;
import project.board.entity.TempArticle;

public interface TempArticleRepositoryJpa extends JpaRepository<TempArticle, Long>{

	@EntityGraph(attributePaths = {"member", "uploadFiles"})
	Optional<TempArticle> findDetailById(Long articleId);

	int countByMember(Member member);

	List<TempArticle> findByMember(Member member);

}
