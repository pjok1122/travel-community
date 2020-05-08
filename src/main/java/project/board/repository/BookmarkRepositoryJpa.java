package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Article;
import project.board.entity.Bookmark;
import project.board.entity.Member;

public interface BookmarkRepositoryJpa extends JpaRepository<Bookmark, Long>{

	int countByMemberAndArticle(Member member, Article article);

}
