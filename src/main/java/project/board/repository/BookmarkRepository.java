package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Article;

@Mapper
public interface BookmarkRepository {

	List<Article> findArticleByMemberId(Long memberId);

	Long getTotalCntByMemberId(Long memberId);

}
