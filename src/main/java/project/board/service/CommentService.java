package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Comment;
import project.board.domain.dto.CommentDto;
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
	
	public CommentDto getById(Long id)
	{
		return commentRepository.selectCommentById(id);
	}
	
	public List<CommentDto> getByArticleId(Long articleId)
	{
		return commentRepository.selectCommentByArticleId(articleId);
	}
	
	public void create(Long articleId, Long memberId, Long parentCommentId, String content)
	{
		if(content.length() > 300)
		{
			content = content.substring(0, 300);
		}
		
		if(parentCommentId == null)
		{
			
		}
		
		commentRepository.insertComment(articleId, memberId, parentCommentId, content);
//		return getById(Long.valueOf(id));
	}
}
