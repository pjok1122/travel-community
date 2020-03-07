package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Article;
import project.board.domain.Bookmark;
import project.board.domain.dto.ArticleDto;

@Mapper
public interface BookmarkRepository {

	List<ArticleDto> selectArticleByMemberId(Long memberId, int offset, int numOfRecords);

	Integer getArticleCntByMemberId(Long memberId);

	Bookmark selectBookmarkByMemberIdAndArticleId(Long memberId, Long articleId);

	void deleteBookmark(Long memberId, Long articleId);

	void insertBookmark(Long memberId, Long articleId);

}
