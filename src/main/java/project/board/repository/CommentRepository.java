package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import project.board.domain.Comment;

@Mapper
public interface CommentRepository {

	List<Comment> findByMemberId(Long memberId, int offset, int numOfRecords);
	Integer getCommentCntByMemberId(Long memberId);
	
	int insertComment(@Param("articleId") Long articleId, @Param("memberId") Long memberId, @Param("parentCommentId") Long parentCommentId, @Param("content") String content);
	Comment selectCommentById(@Param("id") Long id);
	List<Comment> selectCommentByArticleId(@Param("articleId") Long articleId);
}
