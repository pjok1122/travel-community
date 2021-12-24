package project.board.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import project.board.entity.Comment;
import project.board.entity.CommentLike;
import project.board.entity.Member;

import java.util.Optional;

public interface CommentLikeRepositoryJpa extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);

}
