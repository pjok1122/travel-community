package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.entity.dto.ArticleDto2;

@Mapper
public interface ArticleRepositoryCustom {
	int count(String category, String nation, String search);
	List<ArticleDto2> findPopularAll(String category, String nation, String search, int offset, int numOfRecords);
	List<ArticleDto2> findNewAll(String category, String nation, String search, int offset, int numOfRecords);
	
}
