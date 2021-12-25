package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import project.board.entity.Article;
import project.board.entity.Comment;
import project.board.entity.CommentLike;
import project.board.entity.Member;
import project.board.jpa.ArticleRepositoryJpa;
import project.board.jpa.CommentLikeRepositoryJpa;
import project.board.jpa.CommentRepositoryJpa;
import project.board.jpa.MemberRepositoryJpa;
import project.board.jpa.dto.CommentResponse;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                .parent(parentCommentId == null ? null : commentRepository.findById(parentCommentId)
                        .orElseThrow(() -> new NullPointerException()))
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

        if (comment.isChild()) {
            //fixme: comment 지울 때, 부모에서도 자식 삭제 필수
            comment.getParent().getChildren().remove(comment);
            commentRepository.delete(comment);

            ifCanParentAlsoDelete(comment);
        } else {
            List<Comment> children = comment.getChildren();
            if (children.isEmpty()) {
                commentRepository.delete(comment);
            } else {
                comment.parentDelete();
            }
        }
    }

    @Transactional
    public List<CommentResponse> getByArticleId(Long memberId, Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (!optionalArticle.isPresent()) {
            return Collections.emptyList();
        }

        List<Comment> comments = commentRepository.findByArticleAndParentIsNull(optionalArticle.get());

        Map<Long, Boolean> likeYnByCommentId = likeYnByCommentId(memberId, comments);

        List<CommentResponse> response = new ArrayList<>();
        for (Comment parent : comments) {
            Boolean parentLikeYn = likeYnByCommentId.getOrDefault(parent.getId(), false);
            CommentResponse res = new CommentResponse(parent, parentLikeYn);

            for (Comment child : parent.getChildren()) {
                Boolean childLikeYn = likeYnByCommentId.getOrDefault(child.getId(), false);
                CommentResponse reply = new CommentResponse(child, childLikeYn);
                res.getReplies().add(reply);
            }
            response.add(res);
        }
        return response;
    }

    private Map<Long, Boolean> likeYnByCommentId(Long memberId, List<Comment> comments) {
        if (memberId == null) {
            return new HashMap<>();
        }

        List<Comment> flatComment = Stream.concat(comments.stream(),
                comments.stream().flatMap(comment -> comment.getChildren().stream())).collect(Collectors.toList());

        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Map<Long, Boolean> likeYnByCommentId = new HashMap<>();
        if (optionalMember.isPresent()) {
            likeYnByCommentId = commentLikeRepository.findByMemberAndCommentIn(optionalMember.get(), flatComment)
                    .stream()
                    .collect(Collectors.toMap(
                            i1 -> i1.getComment().getId(),
                            i2 -> true
                    ));
        }

        return likeYnByCommentId;
    }

    private void ifCanParentAlsoDelete(Comment comment) {
        commentRepository.findById(comment.getParent().getId()).ifPresent(parent -> {

            if (parent.parentAlreadyDeleted() && CollectionUtils.isEmpty(parent.getChildren())) {
                commentRepository.delete(parent);
            }
        });
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
