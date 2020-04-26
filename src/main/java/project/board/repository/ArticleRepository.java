package project.board.repository;

import java.util.List;

import javax.validation.Valid;

import org.apache.ibatis.annotations.Mapper;

import project.board.domain.Article;
import project.board.domain.dto.ArticleDto;
import project.board.enums.Sort;

@Mapper
public interface ArticleRepository {

	List<ArticleDto> selectArticleListByMemberId(Long id, int offset, int numOfRecords, String status);

	Integer getArticleCntByMemberId(Long memberId);

	Integer getTempArticleCntByMemberId(Long memberId);

	int getArticleCnt();

	List<ArticleDto> selectArticleList(String category, String nation, String search, String sort, int offset, int numOfRecords);
	
	int getArticleCnt(String category, String nation, String search);

	void insertArticle(@Valid Article article);

	void insertTempArticle(@Valid Article article);

	ArticleDto selectArticleById(Long id);

	void updateArticle(Article article);

	void deleteArticleById(Long id);

	void updateHitById(Long id);

	void updateGoodDown(Long id);

	void updateGoodUp(Long id);

	void updateTempToPermanent(Article article);



	
	
}
