package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import project.board.domain.Comment;
import project.board.domain.dto.CommentDto;

@Mapper
public interface CommentRepository {

	List<Comment> findByMemberId(Long memberId, int offset, int numOfRecords);
	Integer getCommentCntByMemberId(Long memberId);
	
	void insertComment(@Param("articleId") Long articleId, @Param("memberId") Long memberId, @Param("parentCommentId") Long parentCommentId, @Param("content") String content);
	Comment selectCommentById(@Param("id") Long id);
	CommentDto selectCommentByIdAndMemberId(@Param("id") Long id, @Param("memberId") Long memberId);
	List<CommentDto> selectCommentByArticleId(@Param("memberId") Long memberId, @Param("articleId") Long articleId);
	void deleteCommentById(@Param("id") Long id);
	void updateCommentContentById(@Param("id") Long id);
}
