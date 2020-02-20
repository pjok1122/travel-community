package project.board.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Article;
import project.board.domain.CommonDomain;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.enums.Sort;
import project.board.repository.ArticleRepository;
import project.board.repository.CategoryRepository;

@Service
public class ArticleService {

	@Autowired
	ArticleRepository articleRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	public List<ArticleDto> getArticleByMemberId(Long id, Page paging) {
		return articleRepository.findByMemberId(id, paging.getOffset(), paging.getRecordsPerPage(), "PERMANENT");
	}

	public List<ArticleDto> getTempArticleByMemberId(Long id, Page paging) {
		return articleRepository.findByMemberId(id, paging.getOffset(), paging.getRecordsPerPage(), "TEMP");
	}

	public int getArticleCntByMemberId(Long memberId) {
		Integer totalCnt = articleRepository.getArticleCntByMemberId(memberId);
		if(totalCnt==null)	totalCnt = 0;
		return totalCnt;
	}
	
	public int getTempArticleCntByMemberId(Long memberId) {
		Integer totalCnt = articleRepository.getTempArticleCntByMemberId(memberId);
		if(totalCnt==null)	totalCnt = 0;
		return totalCnt;
	}

	public int getArticleCnt(Category category, Nation nation) {
		return articleRepository.getArticleCnt(category.getKrValue(), nation.getKrValue());
	}
	
	public Map<String, Object> getArticle(Category category, Nation nation, int page, Sort sort) {
		Map<String, Object> map = new HashMap<String,Object>();
		Page paging = new Page(page);
		paging.setNumberOfRecordsAndMakePageInfo(articleRepository.getArticleCnt(category.getKrValue(), nation.getKrValue()));
		paging.setList(articleRepository.findAll(category.getKrValue(), nation.getKrValue(), paging.getOffset(), paging.getRecordsPerPage()));
		
		map.put("page", paging);
		return map;
	}

	public Long createArticle(Article article, Category category, Long memberId) {
		Long categoryId = categoryRepository.selectIdByTitle(category.getKrValue());
		article.setMemberId(memberId);
		article.setCategoryId(categoryId);
		articleRepository.insertArticle(article);
		return article.getId();
	}

	public ArticleDto getArticleById(Long articleId) {
		return articleRepository.selectArticleById(articleId);
	}

	public void modifyArticle(Long articleId, @Valid Article article) {
		article.setId(articleId);
		articleRepository.updateArticle(article);
	}

	public void removeArticleById(Long articleId) {
		articleRepository.deleteArticleById(articleId);
	}
	
	
}
