package project.board.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import project.board.entity.dto.ArticleDto2;

@Mapper
public interface ArticleRepositoryCustom {
	int count(String category, String nation, String search);
	List<ArticleDto2> findAll(String category, String nation, String search, String sort, int offset, int numOfRecords);

}
