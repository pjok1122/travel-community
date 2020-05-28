package project.board.repository;

import java.util.List;


import lombok.RequiredArgsConstructor;
import project.board.entity.dto.ArticleDto2;

@RequiredArgsConstructor
public class ArticleRepositoryCustomImpl implements ArticleRepositoryCustom {
	private final ArticleRepositoryCustom articleRepositoryCustom;
	
	@Override
	public int count(String category, String nation, String search) {
		return articleRepositoryCustom.count(category, nation, search);
	}

	@Override
	public List<ArticleDto2> findAll(String category, String nation, String search, String sort, int offset,
			int numOfRecords) {
		return articleRepositoryCustom.findAll(category, nation, search, sort, offset, numOfRecords);
	}
}
