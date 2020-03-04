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
	
	private Comment getById(Long id)
	{
		return commentRepository.selectCommentById(id);
	}
	
	public CommentDto getById(Long id, Long memberId)
	{
		return commentRepository.selectCommentByIdAndMemberId(id, memberId);
	}
	
	public List<CommentDto> getByArticleId(Long articleId)
	{
		List<CommentDto> list = commentRepository.selectCommentByArticleId(articleId);
		System.out.println();
		return list;
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
	}
	
	public void delete(Long commentId, Long memberId)
	{
		CommentDto comment = getById(commentId, memberId);
		
		if(comment.replyEmpty())
		{
			deleteCommentById(commentId);
		}
		else
		{
			updateCommentContentById(commentId, "삭제된 댓글입니다.");
		}
	}
	
	public void deleteCommentById(Long id)
	{
		commentRepository.deleteCommentById(id);
	}
	
	public void updateCommentContentById(Long id, String content)
	{
		commentRepository.updateCommentContentById(id, content);
	}
}
