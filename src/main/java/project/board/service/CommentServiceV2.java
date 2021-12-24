package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import project.board.domain.dto.CommentDto;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.CommentLike;
import project.board.entity.Member;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.CommentLikeRepositoryJpa;
import project.board.jpa.CommentRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceV2 {
    private final CommentRepositoryJpa commentRepository;
    private final ArticleRepositoryJpa articleRepository;
    private final MemberRepositoryJpa memberRepository;
    private final CommentLikeRepositoryJpa commentLikeRepository;

    public Page<Comment> getCommentsByMember(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByMember(member, pageable);
    }

    @Transactional(readOnly = false)
    public void create(Long articleId, Long memberId, Long parentCommentId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NullPointerException());

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NullPointerException());

        content = cutLength(content);

        Comment comment = Comment.builder()
                .article(article)
                .member(member)
                .parentCommentId(parentCommentId)
                .content(content)
                .build();

        commentRepository.save(comment);
    }

    @Transactional(readOnly = false)
    public int like(Long memberId, Long commentId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NullPointerException());

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NullPointerException());

        Optional<CommentLike> commentLike = commentLikeRepository.findByMemberAndComment(member, comment);
        likeToggle(commentLike, member, comment);

        return comment.getGood();
    }

    @Transactional(readOnly = false)
    public void delete(Long commentId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NullPointerException());

        Comment comment = commentRepository.findByMemberAndId(member, commentId)
                .orElseThrow(() -> new NullPointerException());

        if(comment.isChild()) {
            commentRepository.delete(comment);
            ifCanParentAlsoDelete(comment);
        } else {
            List<Comment> children = commentRepository.findByParentCommentId(comment.getId());
            if(children.isEmpty()) {
                commentRepository.delete(comment);
            } else {
                comment.setUpdateDate(LocalDateTime.now());
            }
        }
    }

    private void ifCanParentAlsoDelete(Comment comment) {
        Optional<Comment> parent = commentRepository.findById(comment.getParentCommentId());
        if (parent.isPresent() && parent.get().parentAlreadyDeleted()) {
            commentRepository.delete(parent.get());
        }
    }

    private void likeToggle(Optional<CommentLike> commentLike, Member member, Comment comment) {
        if (commentLike.isPresent()) {
            commentLikeRepository.delete(commentLike.get());
            comment.goodMinusOne();
        } else {
            commentLikeRepository.save(CommentLike.builder()
                    .comment(comment)
                    .member(member)
                    .build());
            comment.goodPlusOne();
        }
    }

    private String cutLength(String content) {
        if (StringUtils.hasText(content) && content.length() > 300) {
            return content.substring(0, 300);
        }
        return content;
    }
}
