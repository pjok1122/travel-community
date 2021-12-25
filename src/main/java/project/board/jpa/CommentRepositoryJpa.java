package project.board.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryJpa extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"article"})
    Page<Comment> findByMember(Member member, Pageable pageable);

    /**
     * 내가 남긴 코멘트인지 확인
     */
    @EntityGraph(attributePaths = {"children"})
    Optional<Comment> findByMemberAndId(Member member, Long id);

    @EntityGraph(attributePaths = {"member", "children"})
    List<Comment> findByArticleAndParentIsNull(Article article);
}
