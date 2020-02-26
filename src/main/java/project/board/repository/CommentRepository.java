package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Comment;

@Mapper
public interface CommentRepository {

	List<Comment> findByMemberId(Long memberId, int offset, int numOfRecords);
	Integer getCommentCntByMemberId(Long memberId);
}
