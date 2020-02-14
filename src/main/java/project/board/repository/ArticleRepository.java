package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Article;

@Mapper
public interface ArticleRepository {

	List<Article> findByMemberId(Long id, String status);

	Long getArticleCntByMemberId(Long memberId);

	Long getTempArticleCntByMemberId(Long memberId);
	
	
}
