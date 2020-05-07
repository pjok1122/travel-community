package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Member;

public interface ArticleRepositoryJpa extends JpaRepository<Article, Long>{

	int countByMember(Member member);

}
