package project.board.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.board.entity.Comment;
import project.board.entity.Member;
import project.board.jpa.CommentRepositoryJpa;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {
    private final CommentRepositoryJpa commentRepository;

    public Page<Comment> getCommentsByMember(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentRepository.findByMember(member, pageable);
    }
}
