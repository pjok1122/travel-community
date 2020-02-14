package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Article;
import project.board.repository.ArticleRepository;

@Service
public class ArticleService {

	@Autowired
	ArticleRepository articleRepository;
	
	public List<Article> getArticleByMemberId(Long id) {
		return articleRepository.findByMemberId(id, "PERMANENT");
	}

	public List<Article> getTempArticleByMemberId(Long id) {
		return articleRepository.findByMemberId(id, "TEMP");
	}

	public Long getArticleCntByMemberId(Long memberId) {
		Long totalCnt = articleRepository.getArticleCntByMemberId(memberId);
		if(totalCnt==null)	totalCnt = 0L;
		return totalCnt;
	}
	
	public Long getTempArticleCntByMemberId(Long memberId) {
		Long totalCnt = articleRepository.getTempArticleCntByMemberId(memberId);
		if(totalCnt==null)	totalCnt = 0L;
		return totalCnt;
	}
	
}
