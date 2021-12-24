package project.board.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.board.entity.Comment;
import project.board.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"article"})
    Page<Comment> findByMember(Member member, Pageable pageable);

    Optional<Comment> findByMemberAndId(Member member, Long id);

    List<Comment> findByParentCommentId(Long parentCommentIds);

}
