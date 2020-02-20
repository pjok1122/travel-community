package project.board.repository;

import java.util.List;

import javax.validation.Valid;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;

@Mapper
public interface ArticleRepository {

	List<ArticleDto> findByMemberId(Long id, int offset, int numOfRecords, String status);

	Integer getArticleCntByMemberId(Long memberId);

	Integer getTempArticleCntByMemberId(Long memberId);

	int getArticleCnt();

	List<ArticleDto> findAll(String category, String nation, int offset, int numOfRecords);
	
	int getArticleCnt(String category, String nation);

	void insertArticle(@Valid Article article);

	ArticleDto selectArticleById(Long id);

	void updateArticle(Article article);

	void deleteArticleById(Long id);

	
	
}
