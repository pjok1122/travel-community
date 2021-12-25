package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.board.domain.Comment;
import project.board.domain.dto.CommentDto;
import project.board.domain.dto.Page;
import project.board.repository.CommentLikeRepository;
import project.board.repository.CommentRepository;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private CommentLikeRepository commentLikeRepository;

	@Deprecated
	public List<Comment> getCommentByMemberId(Long memberId, Page paging) {
		return commentRepository.selectByMemberId(memberId, paging.getOffset(), paging.getRecordsPerPage());
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
	
	public List<CommentDto> getByArticleId(Long memberId, Long articleId)
	{
		List<CommentDto> list = commentRepository.selectCommentByArticleId(memberId, articleId);
		return list;
	}

	@Deprecated
	public void create(Long articleId, Long memberId, Long parentCommentId, String content)
	{
		if(content.length() > 300)
		{
			content = content.substring(0, 300);
		}
		
		commentRepository.insertComment(articleId, memberId, parentCommentId, content);
	}
	
	@Transactional
	@Deprecated
	public void delete(Long commentId, Long memberId)
	{
		CommentDto comment = getById(commentId, memberId);
		
		// 자식 삭제
		if(comment.isChild())
		{
			deleteCommentById(commentId);
			
			//부모가 삭제된 상태면 부모도 삭제
			Comment parent = getById(comment.getParentCommentId());
			if(parent.parentAlreadyDeleted())
			{
				deleteCommentById(parent.getId());
			}
		}
		// 부모 삭제 
		else if(comment.isParent())
		{
			if(comment.parentHaveNoChildren())
			{
				deleteCommentById(commentId);
			}
			else // 자식 노드가 존재하면 "삭제된 댓글입니다"로 남길거임
			{
				updateCommentContentById(commentId);
			}
		}
	}

	@Transactional
	@Deprecated
	public int like(Long memberId, Long commentId)
	{
		Boolean isAlreayLike = selectCommentLikeByMemberIdAndCommentId(memberId, commentId);
		if(isAlreayLike)
		{
			deleteCommentLikeByMemberIdAndCommentId(memberId, commentId);
			GoodDown(commentId);
			
		}
		else
		{
			insertCommentLikeByMemberIdAndCommentId(memberId, commentId);
			GoodUp(commentId);
		}
		
		return selectCommentGoodById(commentId);
	}

	@Deprecated
	public void deleteCommentById(Long id)
	{
		commentRepository.deleteCommentById(id);
	}

	@Deprecated
	public void updateCommentContentById(Long id)
	{
		commentRepository.updateCommentContentById(id);
	}

	@Deprecated
	public boolean selectCommentLikeByMemberIdAndCommentId(Long memberId, Long commentId)
	{
		return commentLikeRepository.selectCommentLikeByMemberIdAndCommentId(memberId, commentId);
	}

	@Deprecated
	public void deleteCommentLikeByMemberIdAndCommentId(Long memberId, Long commentId)
	{
		commentLikeRepository.deleteCommentLikeByMemberIdAndCommentId(memberId, commentId);
	}

	@Deprecated
	public void insertCommentLikeByMemberIdAndCommentId(Long memberId, Long commentId)
	{
		commentLikeRepository.insertCommentLikeByMemberIdAndCommentId(memberId, commentId);
	}

	@Deprecated
	public void GoodDown(Long commentId) {
		commentRepository.updateGoodDown(commentId);
	}

	@Deprecated
	public void GoodUp(Long commentId) {
		commentRepository.updateGoodUp(commentId);
	}

	@Deprecated
	public int selectCommentGoodById(Long commentId) {
		return commentRepository.selectCommentById(commentId).getGood();
	}
	

}
