package project.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.board.entity.Comment;
import project.board.entity.Member;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

	int countByMember(Member member);

}
