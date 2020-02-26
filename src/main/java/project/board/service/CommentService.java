package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Comment;
import project.board.domain.CommonDomain;
import project.board.domain.dto.Page;
import project.board.repository.CommentRepository;

@Service
public class CommentService {

	@Autowired
	CommentRepository commentRepository;
	
	public List<Comment> getCommentByMemberId(Long memberId, Page paging) {
		return commentRepository.findByMemberId(memberId, paging.getOffset(), paging.getRecordsPerPage());
	}
	
	public int getCommentCntByMemberId(Long memberId) {
		Integer cnt = commentRepository.getCommentCntByMemberId(memberId);
		if (cnt==null) cnt = 0;
		return cnt;
	}

}
