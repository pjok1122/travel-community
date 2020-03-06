package project.board.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.board.domain.Article;
import project.board.domain.Bookmark;
import project.board.domain.CommonDomain;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.enums.Category;
import project.board.enums.Nation;
import project.board.enums.Sort;
import project.board.repository.ArticleLikeRepository;
import project.board.repository.ArticleRepository;
import project.board.repository.BookmarkRepository;
import project.board.repository.CategoryRepository;
import project.board.util.ScriptEscapeUtils;

@Service
public class ArticleService {

	@Autowired
	ArticleRepository articleRepository;
	
	@Autowired
	ArticleLikeRepository articleLikeRepository;
	
	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	BookmarkRepository bookmarkRepository;

	@Autowired
	ScriptEscapeUtils scriptEscaper;
	
	private static final int TEMP_ARTICLE_MAX_SIZE = 10;
	private static final int MAIN_ARTICLE_NUM = 5;
	private static final String NEWEST = "NEWEST";
	private static final String POPULAR = "POPULAR";
	
	public List<ArticleDto> getArticleByMemberId(Long id, Page paging) {
		return articleRepository.selectArticleListByMemberId(id, paging.getOffset(), paging.getRecordsPerPage(), "PERMANENT");
	}

	public List<ArticleDto> getTempArticleByMemberId(Long id, Page paging) {
		return articleRepository.selectArticleListByMemberId(id, paging.getOffset(), paging.getRecordsPerPage(), "TEMP");
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
	
	public Map<String, Object> getArticleList(Category category, Nation nation, int page, Sort sort, String search) {
		Map<String, Object> map = new HashMap<String,Object>();
		Page paging = new Page(page);
		paging.setNumberOfRecordsAndMakePageInfo(articleRepository.getArticleCnt(category.getKrValue(), nation.getKrValue(), search));
		paging.setList(articleRepository.selectArticleList(category.getKrValue(), nation.getKrValue(), search, sort.toString(), paging.getOffset(), paging.getRecordsPerPage()));
		map.put("page", paging);
		return map;
	}

	public Long createArticle(Article article, Long memberId) {
		article.setMemberId(memberId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		
		if(article.getId()!=null) {
			articleRepository.updateTempToPermanent(article);
		}
		else{
			articleRepository.insertArticle(article);
		}
		return article.getId();
	}
	
	public Boolean checkTempArticleWritable(Long memberId) {
		if(getTempArticleCntByMemberId(memberId) < TEMP_ARTICLE_MAX_SIZE) {
			return true;
		} else {
			return false;
		}
	}
	
	public Long createTempArticle(Article article,Long memberId) {		
		article.setMemberId(memberId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		articleRepository.insertTempArticle(article);
		return article.getId();
	}

	public ArticleDto getArticleById(Long articleId) {
		return articleRepository.selectArticleById(articleId);
	}

	public void modifyArticle(Long articleId, @Valid Article article) {
		article.setId(articleId);
		article.setTitle((scriptEscaper.scriptEscape(article.getTitle())));
		articleRepository.updateArticle(article);
	}

	public void removeArticleById(Long articleId) {
		articleRepository.deleteArticleById(articleId);
	}

	public void increaseHitById(Long articleId) {
		articleRepository.updateHitById(articleId);
	}

	public int checkLikeStatus(Long memberId, Long articleId) {
		Long id = articleLikeRepository.selectByMemberIdAndArticleId(memberId, articleId);
		if(id == null) return 0;
		else return 1;
	}

	@Transactional
	public int modifyLikeStatus(Long memberId, Long articleId) {
		Long id = articleLikeRepository.selectByMemberIdAndArticleId(memberId, articleId);
		
		if(id == null) {
			articleLikeRepository.insertByMemberIdAndArticleId(memberId, articleId);
			articleRepository.updateGoodUp(articleId); 
			return 1;
		} else {
			articleLikeRepository.deleteByMemberIdAndArticleId(memberId, articleId);
			articleRepository.updateGoodDown(articleId); 
			return 0;
		}
	}

	public int getLikeCount(Long articleId) {
		return articleRepository.selectArticleById(articleId).getGood();
	}
	public int checkBookmarkStatus(Long memberId, Long articleId) {
		Bookmark bookmark = bookmarkRepository.selectBookmarkByMemberIdAndArticleId(memberId, articleId);
		if(bookmark == null) return 0;
		else return 1;
	}

	public Map<String, Object> checkArticleSatus(Long memberId, Long articleId) {
		Map<String, Object> map = new HashMap<String,Object>();
		int liked = checkLikeStatus(memberId, articleId);
		int bookmark = checkBookmarkStatus(memberId, articleId);
		map.put("liked", liked);
		map.put("bookmark", bookmark);
		return map;
	}

	public boolean checkArticleOwner(Long memberId, ArticleDto article) {
		if(article.getMemberId().equals(memberId)) {
			return true;
		}
		return false;
	}

	public boolean checkStatusTemp(ArticleDto article) {
		if(article.getStatus().equals("TEMP")) {
			return true;
		}
		return false;
	}

	public Map<String,Object> getMainArticle() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ArticleDto> newArticles = articleRepository.selectArticleList(null, null, null, NEWEST, 0, MAIN_ARTICLE_NUM);
		List<ArticleDto> popularArticles = articleRepository.selectArticleList(null, null, null, POPULAR, 0, MAIN_ARTICLE_NUM);
		map.put("newest", newArticles);
		map.put("popular", popularArticles);
		
		return map;
		
	}



}
