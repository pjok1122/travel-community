package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Article;
import project.board.repository.BookmarkRepository;

@Service
public class BookmarkService {

	@Autowired
	BookmarkRepository bookmarkRepository;
	
	public List<Article> getArticleByMemberId(Long memberId) {
		return bookmarkRepository.findArticleByMemberId(memberId);
	}

	public Long getTotalCntByMemberId(Long memberId) {
		Long totalCnt = bookmarkRepository.getTotalCntByMemberId(memberId);
		if(totalCnt==null) totalCnt = 0L;
		return totalCnt;
	}
	
	
}
