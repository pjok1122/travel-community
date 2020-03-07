package project.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.board.domain.Article;
import project.board.domain.Bookmark;
import project.board.domain.dto.ArticleDto;
import project.board.domain.dto.Page;
import project.board.repository.BookmarkRepository;

@Service
public class BookmarkService {

	@Autowired
	BookmarkRepository bookmarkRepository;
	
	public List<ArticleDto> getArticleByMemberId(Long memberId, Page page) {
		return bookmarkRepository.selectArticleByMemberId(memberId, page.getOffset(), page.getRecordsPerPage());
	}

	public int getArticleCntByMemberId(Long memberId) {
		Integer totalCnt = bookmarkRepository.getArticleCntByMemberId(memberId);
		if(totalCnt==null) totalCnt = 0;
		return totalCnt;
	}
	
	public Boolean checkBookmarkStatus(Long memberId, Long articleId) {
		Bookmark bookmark = bookmarkRepository.selectBookmarkByMemberIdAndArticleId(memberId, articleId);
		if(bookmark == null) return false;
		else return true;
	}

	public int modifyBookmarkStatus(Long memberId, Long articleId) {
		Boolean bookmarkStatus = checkBookmarkStatus(memberId, articleId);
		if(bookmarkStatus) {
			bookmarkRepository.deleteBookmark(memberId, articleId);
			return 0;
		}
		else {
			bookmarkRepository.insertBookmark(memberId, articleId);
			return 1;
		}
	}
}
