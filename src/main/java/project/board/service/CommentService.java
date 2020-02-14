package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Comment;
import project.board.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	CommentRepository commentRepository;
	
	public List<Comment> getCommentByMemberId(Long memberId) {
		return commentRepository.findByMemberId(memberId);
	}
	
	public Long getTotalCntByMemberId(Long memberId) {
		Long cnt = commentRepository.getTotalCntByMemberId(memberId);
		if (cnt==null) cnt = 0L;
		return cnt;
	}

}
