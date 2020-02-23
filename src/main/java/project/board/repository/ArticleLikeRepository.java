package project.board.repository;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleLikeRepository {

	Long selectByMemberIdAndArticleId(Long memberId, Long articleId);

	void deleteByMemberIdAndArticleId(Long memberId, Long articleId);

	void insertByMemberIdAndArticleId(Long memberId, Long articleId);

}
