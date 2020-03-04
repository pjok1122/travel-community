package project.board.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentLikeRepository {
	void insertCommentLikeByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);
	void deleteCommentLikeByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);
	Boolean selectCommentLikeByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId); 
}
